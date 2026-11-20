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
