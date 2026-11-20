import { fireEvent, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import type { NotificationTemplateDetail } from '../../api/generated';
import { renderRoute } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { testDestination } from './TestSendDialog';
import { insertAt } from './VariableChips';

const admin = staffMe({ permissions: ['notification-template:manage'] });
const slow = { timeout: 10_000 };

const template: NotificationTemplateDetail = {
  id: 't1',
  code: 'POLICY_ISSUED',
  channel: 'SMS',
  locale: 'pt',
  subject: undefined,
  body: 'Apólice {{policyNumber}} emitida.',
  active: true,
  customized: true,
  defaultBody: 'Apólice {{policyNumber}} emitida.',
  variables: ['policyNumber', 'plate', 'link'],
  sampleParams: { policyNumber: 'AP-2027-000001', plate: 'ABC-123-MC', link: 'https://x' },
  updatedAt: '2027-02-10T08:31:00Z',
  updatedBy: 'rita@moseguros.co.mz',
  version: 3,
};

describe('TemplateEditorPage', { timeout: 30_000 }, () => {
  let restore: () => void = () => {};
  let puts: unknown[] = [];
  let putResponses: { status: number; data: unknown }[] = [];
  let previews: unknown[] = [];
  let resets = 0;
  beforeEach(() => {
    puts = [];
    previews = [];
    putResponses = [];
    resets = 0;
  });
  afterEach(() => restore());

  const api = (c: InternalAxiosRequestConfig) => {
    if (is(c, 'GET', '/api/v1/admin/notification-templates/t1')) {
      return { status: 200, data: template };
    }
    if (is(c, 'GET', '/api/v1/admin/notification-templates')) {
      return {
        status: 200,
        data: {
          content: [
            { ...template, id: 't1' },
            { ...template, id: 't2', locale: 'en' },
            { ...template, id: 't3', code: 'POLICY_ISSUED_X' },
          ],
          page: 0,
          size: 50,
          totalElements: 3,
        },
      };
    }
    if (is(c, 'POST', '/api/v1/admin/notification-templates/t1/preview')) {
      const request = JSON.parse(c.data as string) as { body: string };
      previews.push(request);
      const suffix = request.body.endsWith(' xx') ? ' xx' : '';
      return {
        status: 200,
        data: {
          body: `Apólice AP-2027-000001 emitida.${suffix}`,
          smsText: `Apolice AP-2027-000001 emitida.${suffix}`,
          length: 170,
          limit: 160,
          fits: false,
        },
      };
    }
    if (is(c, 'PUT', '/api/v1/admin/notification-templates/t1')) {
      puts.push(JSON.parse(c.data as string));
      return putResponses.shift() ?? { status: 200, data: { ...template, version: 4 } };
    }
    if (is(c, 'POST', '/api/v1/admin/notification-templates/t1/reset')) {
      resets += 1;
      return { status: 200, data: { ...template, customized: false, version: 4 } };
    }
    return null;
  };

  it('insertAt insere a variável no cursor ou na selecção', () => {
    expect(insertAt('Olá mundo', 'name', 4, 4)).toEqual({ text: 'Olá {{name}}mundo', caret: 12 });
    expect(insertAt('Olá mundo', 'name', 4, 9)).toEqual({ text: 'Olá {{name}}', caret: 12 });
    expect(insertAt('abc', 'x', undefined, undefined)).toEqual({ text: 'abc{{x}}', caret: 8 });
  });

  it('testDestination valida o destino por canal', () => {
    expect(testDestination('SMS', '84 123 4567')).toEqual({ destination: '+258841234567' });
    expect(testDestination('WHATSAPP', '123')).toEqual({ error: 'INVALID_MSISDN' });
    expect(testDestination('EMAIL', 'nao-e-email')).toEqual({ error: 'INVALID_EMAIL' });
    expect(testDestination('PUSH', '')).toEqual({ error: 'REQUIRED' });
    expect(testDestination('PUSH', 'tok')).toEqual({ destination: 'tok' });
  });

  it('clicar numa variável insere-a no cursor; o contador SMS fica vermelho', async () => {
    restore = signInStaff(admin, api);
    renderRoute('/admin/notificacoes/t1');
    const body = (await screen.findByLabelText(/Texto/, undefined, slow)) as HTMLTextAreaElement;
    expect(screen.getByRole('button', { name: 'Guardar' })).toBeDisabled();
    // Só as variantes do mesmo código (o filtro é por prefixo).
    const variants = await screen.findByTestId('template-variants');
    expect(within(variants).getByText('SMS · Português')).toBeInTheDocument();
    expect(within(variants).getByRole('button', { name: 'SMS · Inglês' })).toBeInTheDocument();
    expect(within(variants).getAllByText(/^SMS · /)).toHaveLength(2);

    body.focus();
    body.setSelectionRange(8, 8);
    fireEvent.select(body);
    await userEvent.click(screen.getByRole('button', { name: 'Inserir plate' }));
    expect(body.value).toBe('Apólice {{plate}}{{policyNumber}} emitida.');
    expect(screen.getByRole('button', { name: 'Guardar' })).toBeEnabled();

    const counter = await screen.findByTestId('sms-counter', undefined, slow);
    expect(counter).toHaveTextContent('170/160');
    expect(counter).toHaveAttribute('data-fits', 'false');
    expect(screen.getByTestId('preview-sms')).toHaveTextContent('Apolice AP-2027-000001 emitida.');
    await waitFor(() =>
      expect(previews).toContainEqual({ body: 'Apólice {{plate}}{{policyNumber}} emitida.' }),
    );
  });

  it('erros do servidor nos campos e 409 com "Recarregar"', async () => {
    putResponses = [
      {
        status: 400,
        data: {
          code: 'VALIDATION_ERROR',
          errors: [{ field: 'body', code: 'TEMPLATE_UNKNOWN_VARIABLE' }],
        },
      },
      { status: 409, data: { code: 'CONCURRENT_MODIFICATION' } },
    ];
    restore = signInStaff(admin, api);
    renderRoute('/admin/notificacoes/t1');
    const body = await screen.findByLabelText(/Texto/, undefined, slow);
    await userEvent.type(body, ' xx');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(
      await screen.findByText('Variável desconhecida. Use só as variáveis indicadas.'),
    ).toBeInTheDocument();
    expect(puts[0]).toMatchObject({ version: 3, active: true });
    // A pré-visualização pendente (200) chega depois do PUT e não apaga o erro do Guardar.
    await waitFor(
      () =>
        expect(screen.getByTestId('preview-sms')).toHaveTextContent(
          'Apolice AP-2027-000001 emitida. xx',
        ),
      slow,
    );
    expect(
      screen.getByText('Variável desconhecida. Use só as variáveis indicadas.'),
    ).toBeInTheDocument();

    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Outra pessoa alterou este modelo')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Recarregar' })).toBeInTheDocument();
  });

  it('título PUSH tem no máximo 65 caracteres', async () => {
    const push: NotificationTemplateDetail = {
      ...template,
      id: 'p1',
      channel: 'PUSH',
      subject: 'x'.repeat(70),
    };
    restore = signInStaff(admin, (c) => {
      if (is(c, 'GET', '/api/v1/admin/notification-templates/p1')) {
        return { status: 200, data: push };
      }
      if (is(c, 'GET', '/api/v1/admin/notification-templates')) {
        return { status: 200, data: { content: [push], page: 0, size: 50, totalElements: 1 } };
      }
      if (is(c, 'POST', '/api/v1/admin/notification-templates/p1/preview')) {
        return { status: 200, data: { subject: 'x', body: 'y' } };
      }
      if (is(c, 'PUT', '/api/v1/admin/notification-templates/p1')) {
        puts.push(JSON.parse(c.data as string));
        return { status: 200, data: { ...push, version: 4 } };
      }
      return null;
    });
    renderRoute('/admin/notificacoes/p1');
    const title = await screen.findByLabelText(/Título/, undefined, slow);
    expect(title).toHaveAttribute('maxLength', '65');
    await userEvent.type(screen.getByLabelText(/Texto/), '!');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Texto demasiado longo.')).toBeInTheDocument();
    expect(puts).toHaveLength(0);
  });

  it('guardar mostra "Modelo guardado"; repor pede confirmação', async () => {
    restore = signInStaff(admin, api);
    renderRoute('/admin/notificacoes/t1');
    const body = await screen.findByLabelText(/Texto/, undefined, slow);
    await userEvent.type(body, '!');
    await userEvent.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Modelo guardado')).toBeInTheDocument();
    expect(puts).toHaveLength(1);

    await userEvent.click(screen.getByRole('button', { name: 'Repor texto original' }));
    const dialog = await screen.findByRole('dialog');
    expect(
      within(dialog).getByText(
        'O texto volta ao original da plataforma. As suas alterações perdem-se.',
      ),
    ).toBeInTheDocument();
    await userEvent.click(within(dialog).getByRole('button', { name: 'Repor texto original' }));
    await waitFor(() => expect(resets).toBe(1));
  });

  it('modelo inexistente mostra a mensagem com ligação para a lista', async () => {
    restore = signInStaff(admin, (c) =>
      c.url === '/api/v1/admin/notification-templates/zz'
        ? { status: 404, data: { code: 'TEMPLATE_NOT_FOUND' } }
        : null,
    );
    renderRoute('/admin/notificacoes/zz');
    expect(await screen.findByText('Modelo não encontrado.', undefined, slow)).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Voltar aos modelos' })).toHaveAttribute(
      'href',
      '/admin/notificacoes',
    );
  });
});
