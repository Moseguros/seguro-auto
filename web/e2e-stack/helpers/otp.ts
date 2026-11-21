import { expect, type Page, type Response } from '@playwright/test';

/**
 * Código OTP do perfil `local`: o backend devolve-o no cabeçalho `X-Debug-Otp` (S1-03) dos pedidos de
 * código do login (`/auth/otp/request`), dos contactos (`/me/contacts/...`) e da assinatura das propostas
 * (`/proposals/{id}/sign/request`). Começar a esperar antes do clique que faz o pedido.
 */
export function waitForDebugOtp(page: Page, urlPart: string | RegExp): Promise<string> {
  return page
    .waitForResponse(
      (response: Response) =>
        response.request().method() === 'POST' &&
        (typeof urlPart === 'string'
          ? response.url().includes(urlPart)
          : urlPart.test(response.url())) &&
        response.headers()['x-debug-otp'] !== undefined,
      { timeout: 30_000 },
    )
    .then((response) => {
      const code = response.headers()['x-debug-otp'];
      expect(code, `X-Debug-Otp em ${response.url()}`).toMatch(/^\d{6}$/);
      return code!;
    });
}

/** Escreve um código de 6 dígitos no campo segmentado (`Dígito 1 de 6`). */
export async function typeSegmentedCode(page: Page, code: string) {
  await page.getByLabel('Dígito 1 de 6').click();
  await page.keyboard.type(code);
}

/**
 * Faz o pedido de código com `trigger` e devolve o `X-Debug-Otp`. Os códigos do mesmo telemóvel têm um
 * intervalo mínimo entre pedidos (429 `OTP_RESEND_TOO_SOON` com `Retry-After`, S1-03): o login e a
 * confirmação do contacto acabaram de pedir um, por isso espera-se o tempo indicado e repete-se.
 */
export async function requestDebugOtp(
  page: Page,
  urlPart: RegExp,
  trigger: () => Promise<void>,
): Promise<string> {
  for (let attempt = 0; attempt < 3; attempt++) {
    const pending = page.waitForResponse(
      (response: Response) =>
        response.request().method() === 'POST' && urlPart.test(response.url()),
      { timeout: 30_000 },
    );
    await trigger();
    const response = await pending;
    if (response.status() !== 429) {
      const code = response.headers()['x-debug-otp'];
      expect(code, `X-Debug-Otp em ${response.url()}`).toMatch(/^\d{6}$/);
      return code!;
    }
    const retryAfter = Number(response.headers()['retry-after'] ?? '60');
    await page.waitForTimeout((retryAfter + 1) * 1000);
  }
  throw new Error(`Sem código depois de três pedidos a ${urlPart}`);
}
