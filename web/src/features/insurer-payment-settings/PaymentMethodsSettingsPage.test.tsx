import { cleanup, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it } from 'vitest';
import type { PaymentMethodResponse } from '../../api/generated';
import {
  bankReferenceMethodFixture,
  financeMe,
  mpesaMethodFixture,
} from '../../test/insurerPayments';
import { renderRoute } from '../../test/render';
import { is, signInStaff, type StaffHandler } from '../../test/staff';

const slow = { timeout: 15000 };
const path = '/seguradora/configuracao/pagamentos';

function api(methods: PaymentMethodResponse[], ...handlers: StaffHandler[]): StaffHandler[] {
  return [
    ...handlers,
    (c) =>
      is(c, 'GET', '/api/v1/insurer/payment-methods')
        ? { status: 200, data: { methods } }
        : is(c, 'GET', '/api/v1/insurer/payment-file-source')
          ? { status: 200, data: { configured: false } }
          : null,
  ];
}

function card(method: string): HTMLElement {
  return screen.getByTestId(`payment-method-${method}`);
}

function field(id: string): HTMLElement {
  const element = document.getElementById(id);
  if (!element) throw new Error(`sem o campo ${id}`);
  return element;
}

/** Texto de ajuda/erro do campo (`aria-describedby`). */
function helper(id: string): string {
  const describedBy = field(id).getAttribute('aria-describedby') ?? '';
  return describedBy
    .split(' ')
    .map((d) => document.getElementById(d)?.textContent ?? '')
    .join(' ');
}

describe('formas de pagamento da seguradora', { timeout: 45_000 }, () => {
  let restore: () => void = () => {};
  afterEach(() => {
    cleanup();
    restore();
  });

  it('só envia os segredos escritos, limpa-os depois de gravar e nunca mostra valores', async () => {
    const puts: unknown[] = [];
    let current = mpesaMethodFixture();
    restore = signInStaff(
      financeMe,
      ...api([current, bankReferenceMethodFixture()], (c: InternalAxiosRequestConfig) => {
        if (is(c, 'PUT', '/api/v1/insurer/payment-methods/MPESA')) {
          puts.push(JSON.parse(c.data as string));
          current = mpesaMethodFixture({ version: 4 });
          return { status: 200, data: current };
        }
        return null;
      }),
    );
    renderRoute(path);
    await screen.findByTestId('payment-method-MPESA', undefined, slow);
    const apiKey = field('MPESA-secret-apiKey') as HTMLInputElement;
    expect(apiKey).toHaveAttribute('type', 'password');
    expect(apiKey).toHaveAttribute('autocomplete', 'new-password');
    expect(apiKey.value).toBe('');
    expect(helper('MPESA-secret-apiKey')).toContain('Configurado em');
    expect(helper('MPESA-secret-securityCredential')).toContain('Não configurado');

    await userEvent.type(apiKey, 'nova-chave');
    await userEvent.click(within(card('MPESA')).getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(puts).toHaveLength(1));
    const body = puts[0] as Record<string, unknown>;
    expect(body.secrets).toEqual({ apiKey: 'nova-chave' });
    expect(body.version).toBe(3);
    expect(await screen.findByText('Guardado')).toBeInTheDocument();
    await waitFor(() => expect((field('MPESA-secret-apiKey') as HTMLInputElement).value).toBe(''));
    expect(document.body.textContent).not.toContain('nova-chave');
  });

  it('"Testar ligação" fica desactivado com alterações por gravar', async () => {
    restore = signInStaff(financeMe, ...api([mpesaMethodFixture()]));
    renderRoute(path);
    await screen.findByTestId('payment-method-MPESA', undefined, slow);
    const test = within(card('MPESA')).getByRole('button', { name: 'Testar ligação' });
    expect(test).toBeEnabled();
    await userEvent.type(field('MPESA-origin'), 'x');
    expect(test).toBeDisabled();
    expect(within(card('MPESA')).getByText('Guarde antes de testar')).toBeInTheDocument();
  });

  it('mostra os erros settings.sftp.port e secrets.apiKey nos campos certos', async () => {
    restore = signInStaff(
      financeMe,
      ...api([mpesaMethodFixture(), bankReferenceMethodFixture()], (c) => {
        if (is(c, 'PUT', '/api/v1/insurer/payment-methods/MPESA')) {
          return {
            status: 400,
            data: {
              code: 'VALIDATION_ERROR',
              errors: [{ field: 'secrets.apiKey', code: 'REQUIRED' }],
            },
          };
        }
        if (is(c, 'PUT', '/api/v1/insurer/payment-methods/BANK_REFERENCE')) {
          return {
            status: 400,
            data: {
              code: 'VALIDATION_ERROR',
              errors: [{ field: 'settings.sftp.port', code: 'OUT_OF_RANGE' }],
            },
          };
        }
        return null;
      }),
    );
    renderRoute(path);
    await screen.findByTestId('payment-method-MPESA', undefined, slow);
    await userEvent.click(within(card('MPESA')).getByRole('button', { name: 'Guardar' }));
    await waitFor(() =>
      expect(field('MPESA-secret-apiKey')).toHaveAttribute('aria-invalid', 'true'),
    );
    expect(helper('MPESA-secret-apiKey')).toContain('Campo obrigatório.');
    expect(field('MPESA-origin')).toHaveAttribute('aria-invalid', 'false');

    await userEvent.click(within(card('BANK_REFERENCE')).getByRole('button', { name: 'Guardar' }));
    // Método activo sem mudar a entidade nem o formato: sem confirmação.
    await waitFor(() =>
      expect(field('BANK_REFERENCE-sftp-port')).toHaveAttribute('aria-invalid', 'true'),
    );
    expect(field('BANK_REFERENCE-sftp-host')).toHaveAttribute('aria-invalid', 'false');
    expect(within(card('BANK_REFERENCE')).getByText('Há campos por corrigir.')).toBeInTheDocument();
  });

  it('PAYMENT_METHOD_INCOMPLETE ao activar mostra os erros nos campos', async () => {
    restore = signInStaff(
      financeMe,
      ...api(
        [mpesaMethodFixture({ secrets: { apiKey: { configured: false, required: true } } })],
        (c) =>
          is(c, 'POST', '/api/v1/insurer/payment-methods/MPESA/enable')
            ? {
                status: 409,
                data: {
                  code: 'PAYMENT_METHOD_INCOMPLETE',
                  errors: [
                    { field: 'secrets.apiKey', code: 'REQUIRED' },
                    { field: 'settings.serviceProviderCode', code: 'REQUIRED' },
                  ],
                },
              }
            : null,
      ),
    );
    renderRoute(path);
    await screen.findByTestId('payment-method-MPESA', undefined, slow);
    await userEvent.click(within(card('MPESA')).getByRole('button', { name: 'Activar' }));
    const dialog = await screen.findByRole('dialog');
    expect(dialog).toHaveTextContent('Os clientes passam a ver esta forma de pagamento.');
    await userEvent.click(within(dialog).getByRole('button', { name: 'Activar' }));
    expect(await screen.findByText('Faltam dados para activar.')).toBeInTheDocument();
    expect(field('MPESA-secret-apiKey')).toHaveAttribute('aria-invalid', 'true');
    expect(field('MPESA-serviceProviderCode')).toHaveAttribute('aria-invalid', 'true');
  });

  it('BANK_REFERENCE: authType troca os segredos, BMEPS mostra a empresa e o perfil, as pastas somem', async () => {
    restore = signInStaff(financeMe, ...api([bankReferenceMethodFixture()]));
    renderRoute(path);
    await screen.findByTestId('payment-method-BANK_REFERENCE', undefined, slow);
    const bank = card('BANK_REFERENCE');

    // Palavra-passe: só o segredo da palavra-passe.
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-password')).not.toBeNull();
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-privateKey')).toBeNull();
    await userEvent.click(within(bank).getByRole('combobox', { name: /Autenticação/ }));
    await userEvent.click(await screen.findByRole('option', { name: 'Chave privada' }));
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-password')).toBeNull();
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-privateKey')).not.toBeNull();
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-passphrase')).not.toBeNull();
    expect(document.getElementById('BANK_REFERENCE-secret-sftp-privateKey')?.tagName).toBe(
      'TEXTAREA',
    );

    // MEPS esconde a empresa e o perfil; BMEPS mostra-os.
    expect(document.getElementById('BANK_REFERENCE-bmepsCompanyId')).toBeNull();
    await userEvent.click(within(bank).getByRole('combobox', { name: /Formato do ficheiro/ }));
    await userEvent.click(await screen.findByRole('option', { name: 'BMEPS' }));
    expect(document.getElementById('BANK_REFERENCE-bmepsCompanyId')).not.toBeNull();
    expect(document.getElementById('BANK_REFERENCE-bmepsProfileId')).not.toBeNull();
    await userEvent.click(within(bank).getByRole('combobox', { name: /Formato do ficheiro/ }));
    await userEvent.click(await screen.findByRole('option', { name: 'MEPS' }));
    expect(document.getElementById('BANK_REFERENCE-bmepsCompanyId')).toBeNull();

    // Desligar "mudar de pasta" esconde as pastas.
    expect(document.getElementById('BANK_REFERENCE-sftp-processedDir')).not.toBeNull();
    await userEvent.click(field('BANK_REFERENCE-sftp-moveAfterProcessing'));
    expect(document.getElementById('BANK_REFERENCE-sftp-processedDir')).toBeNull();
    expect(document.getElementById('BANK_REFERENCE-sftp-errorDir')).toBeNull();
  });

  it('mudar a entidade de um método activo pede confirmação e não envia as pastas escondidas', async () => {
    const puts: Record<string, unknown>[] = [];
    restore = signInStaff(
      financeMe,
      ...api([bankReferenceMethodFixture()], (c) => {
        if (is(c, 'PUT', '/api/v1/insurer/payment-methods/BANK_REFERENCE')) {
          puts.push(JSON.parse(c.data as string) as Record<string, unknown>);
          return { status: 200, data: bankReferenceMethodFixture({ version: 6 }) };
        }
        return null;
      }),
    );
    renderRoute(path);
    await screen.findByTestId('payment-method-BANK_REFERENCE', undefined, slow);
    const entity = field('BANK_REFERENCE-entity');
    await userEvent.clear(entity);
    await userEvent.type(entity, '99887');
    await userEvent.click(field('BANK_REFERENCE-sftp-moveAfterProcessing'));
    await userEvent.click(within(card('BANK_REFERENCE')).getByRole('button', { name: 'Guardar' }));
    const dialog = await screen.findByRole('dialog');
    expect(dialog).toHaveTextContent('As referências já emitidas continuam válidas');
    expect(puts).toHaveLength(0);
    await userEvent.click(within(dialog).getByRole('button', { name: 'Guardar' }));
    await waitFor(() => expect(puts).toHaveLength(1));
    const settings = puts[0]?.settings as Record<string, Record<string, unknown>>;
    expect(settings.entity).toBe('99887');
    expect(settings.sftp?.moveAfterProcessing).toBe(false);
    expect(settings.sftp).not.toHaveProperty('processedDir');
    expect(settings).not.toHaveProperty('bmepsCompanyId');
    expect(puts[0]).not.toHaveProperty('secrets');
  });

  it('testa a ligação e mostra o resultado', async () => {
    let tested = false;
    restore = signInStaff(financeMe, (c) => {
      if (is(c, 'POST', '/api/v1/insurer/payment-methods/MPESA/test')) {
        tested = true;
        return {
          status: 200,
          data: { result: 'FAILED', message: 'API_KEY_REJECTED', testedAt: '2027-01-10T09:00:00Z' },
        };
      }
      if (is(c, 'GET', '/api/v1/insurer/payment-methods')) {
        return {
          status: 200,
          data: {
            methods: [
              mpesaMethodFixture(
                tested
                  ? {
                      version: 4,
                      lastTest: {
                        at: '2027-01-10T09:00:00Z',
                        result: 'FAILED',
                        message: 'API_KEY_REJECTED',
                      },
                    }
                  : {},
              ),
            ],
          },
        };
      }
      return null;
    });
    renderRoute(path);
    await screen.findByTestId('payment-method-MPESA', undefined, slow);
    await userEvent.click(within(card('MPESA')).getByRole('button', { name: 'Testar ligação' }));
    expect(await screen.findByTestId('test-result')).toHaveTextContent(
      'A chave da API foi recusada',
    );
    expect(screen.getByTestId('test-result')).toHaveTextContent('Último teste: 10/01/2027 11:00');
  });
});
