import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import type { InternalAxiosRequestConfig } from 'axios';
import { afterEach, describe, expect, it } from 'vitest';
import { customerMe, signInCustomer } from '../../test/customer';
import { mockApi } from '../../test/mockApi';
import { notificationFixture, preferencesFixture } from '../../test/policies';
import { renderRoute, renderWithAuth } from '../../test/render';
import { is } from '../../test/staff';
import { NotificationBell } from './NotificationBell';
import { NotificationItem } from './NotificationItem';
import { customerWebPath, relativeTime } from './notificationQueries';
import { changedCategories, draftFrom, validatePreferences } from './preferencesForm';

let restore: () => void = () => {};
afterEach(() => restore());

const me = customerMe({ permissions: ['self:profile', 'self:policies'] });

describe('NotificationBell', () => {
  it('mostra a contagem e o aria-label', async () => {
    restore = mockApi((c) =>
      is(c, 'GET', '/api/v1/me/notifications/unread-count')
        ? { status: 200, data: { count: 3 } }
        : { status: 404, data: { code: 'NOT_FOUND' } },
    );
    renderWithAuth(<NotificationBell />);
    const bell = await screen.findByRole('link', { name: 'Notificações, 3 por ler' });
    expect(bell).toHaveAttribute('href', '/cliente/notificacoes');
    expect(within(bell).getByText('3')).toBeInTheDocument();
  });

  it('acima de 99 mostra 99+', async () => {
    restore = mockApi(() => ({ status: 200, data: { count: 150 } }));
    renderWithAuth(<NotificationBell />);
    const bell = await screen.findByRole('link', { name: 'Notificações, 150 por ler' });
    expect(within(bell).getByText('99+')).toBeInTheDocument();
  });

  it('aparece na barra superior da área do cliente', async () => {
    restore = signInCustomer(me, (c) =>
      is(c, 'GET', '/api/v1/me/notifications/unread-count')
        ? { status: 200, data: { count: 2 } }
        : null,
    );
    renderRoute('/cliente');
    expect(
      await screen.findByRole('link', { name: 'Notificações, 2 por ler' }, { timeout: 10_000 }),
    ).toBeVisible();
  });
});

describe('NotificationItem', () => {
  function track() {
    const calls: string[] = [];
    restore = mockApi((c: InternalAxiosRequestConfig) => {
      calls.push(`${c.method?.toUpperCase()} ${c.url}`);
      return { status: 204 };
    });
    return calls;
  }

  it('clicar marca como lida e navega para o webPath', async () => {
    const calls = track();
    const { router } = renderWithAuth(<NotificationItem notification={notificationFixture()} />, {
      path: '/cliente/notificacoes',
    });
    expect(screen.getByText('há 5 min')).toBeVisible();
    await userEvent.click(screen.getByText('Apólice emitida'));
    await waitFor(() => expect(router.state.location.pathname).toBe('/cliente/apolices/pol-1'));
    expect(calls).toContain('POST /api/v1/me/notifications/n-1/read');
  });

  it('um webPath fora de /cliente/ não navega', async () => {
    const calls = track();
    const { router } = renderWithAuth(
      <NotificationItem notification={notificationFixture({ webPath: '/seguradora/x' })} />,
      { path: '/cliente/notificacoes' },
    );
    await userEvent.click(screen.getByText('Apólice emitida'));
    await waitFor(() => expect(calls).toContain('POST /api/v1/me/notifications/n-1/read'));
    expect(router.state.location.pathname).toBe('/cliente/notificacoes');
  });

  it('usa o ícone da categoria e o sino para categorias desconhecidas', () => {
    restore = mockApi(() => ({ status: 204 }));
    renderWithAuth(
      <>
        <NotificationItem notification={notificationFixture({ category: 'PAYMENT' })} />
        <NotificationItem notification={notificationFixture({ id: 'n-2', category: 'CLAIM' })} />
      </>,
    );
    const items = screen.getAllByTestId('notification-item');
    expect(items[0]!.querySelector('[data-testid="PaymentsIcon"]')).not.toBeNull();
    expect(items[1]!.querySelector('[data-testid="NotificationsIcon"]')).not.toBeNull();
  });

  it('helpers: caminhos da área do cliente e data relativa', () => {
    expect(customerWebPath('/cliente/pagamentos/i-1')).toBe('/cliente/pagamentos/i-1');
    expect(customerWebPath('https://evil.example/cliente/')).toBeUndefined();
    expect(customerWebPath('/admin')).toBeUndefined();
    expect(customerWebPath(undefined)).toBeUndefined();
    const now = Date.parse('2027-02-10T10:00:00Z');
    expect(relativeTime('2027-02-10T07:00:00Z', 'pt-MZ', now)).toBe('há 3 h');
    expect(relativeTime('2027-02-01T10:00:00Z', 'pt-MZ', now)).toBe('01/02/27');
    expect(relativeTime('2027-02-10T09:58:00Z', 'en', now)).toBe('2 min ago');
  });

  it('centro: marcar todas como lidas e separador "Por ler"', async () => {
    const calls: InternalAxiosRequestConfig[] = [];
    let unread = 2;
    restore = signInCustomer(me, (c) => {
      if (c.url?.startsWith('/api/v1/me/notifications')) calls.push(c);
      if (is(c, 'GET', '/api/v1/me/notifications/unread-count')) {
        return { status: 200, data: { count: unread } };
      }
      if (is(c, 'POST', '/api/v1/me/notifications/read-all')) {
        unread = 0;
        return { status: 204 };
      }
      if (is(c, 'GET', '/api/v1/me/notifications')) {
        return {
          status: 200,
          data: { content: [notificationFixture()], page: 0, size: 20, totalElements: 1 },
        };
      }
      return null;
    });
    renderRoute('/cliente/notificacoes');
    expect(
      await screen.findByText('Apólice emitida', undefined, { timeout: 10_000 }),
    ).toBeVisible();
    expect(screen.getByRole('link', { name: 'Preferências' })).toHaveAttribute(
      'href',
      '/cliente/notificacoes/preferencias',
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Marcar todas como lidas' }));
    await waitFor(() =>
      expect(screen.queryByRole('button', { name: 'Marcar todas como lidas' })).toBeNull(),
    );
    await userEvent.click(screen.getByRole('tab', { name: 'Por ler' }));
    await waitFor(() =>
      expect(
        calls.some((c) => is(c, 'GET', '/api/v1/me/notifications') && c.params?.unreadOnly),
      ).toBe(true),
    );
  });
});

describe('PreferencesPage', () => {
  function preferencesApi(available?: Partial<Record<string, boolean>>) {
    const bodies: unknown[] = [];
    restore = signInCustomer(me, (c) => {
      if (is(c, 'GET', '/api/v1/me/notification-preferences')) {
        return { status: 200, data: preferencesFixture(available) };
      }
      if (is(c, 'PUT', '/api/v1/me/notification-preferences')) {
        bodies.push(JSON.parse(c.data as string));
        return { status: 200, data: preferencesFixture(available) };
      }
      return null;
    });
    return bodies;
  }

  const section = (category: string) => screen.getByTestId(`category-${category}`);
  const channelSwitch = (category: string, name: string) =>
    within(section(category)).getByRole('switch', { name });

  it('canal indisponível fica desactivado com a ajuda certa', async () => {
    preferencesApi({ PUSH: false, EMAIL: false, SMS: true, WHATSAPP: false });
    renderRoute('/cliente/notificacoes/preferencias');
    await screen.findByTestId('category-PAYMENT', undefined, { timeout: 10_000 });
    const payment = section('PAYMENT');
    expect(within(payment).getByText('Obrigatório')).toBeVisible();
    expect(
      within(payment).getByText(
        'Estes avisos são obrigatórios: escolha pelo menos SMS, WhatsApp ou e-mail.',
      ),
    ).toBeVisible();
    expect(within(section('PROPOSAL')).queryByText('Obrigatório')).toBeNull();
    expect(channelSwitch('PAYMENT', 'Notificação na app')).toBeDisabled();
    expect(channelSwitch('PAYMENT', 'E-mail')).toBeDisabled();
    expect(channelSwitch('PAYMENT', 'WhatsApp')).toBeDisabled();
    expect(channelSwitch('PAYMENT', 'SMS')).toBeEnabled();
    const push = within(payment).getByTestId('channel-PUSH');
    expect(
      within(push).getByText('Instale a app MoSeguros e active as notificações.'),
    ).toBeVisible();
    const email = within(payment).getByTestId('channel-EMAIL');
    expect(within(email).getByText(/Precisa de um e-mail verificado\./)).toBeVisible();
    expect(within(email).getByRole('link', { name: 'Acrescentar no perfil' })).toHaveAttribute(
      'href',
      '/cliente/perfil',
    );
    const whatsapp = within(payment).getByTestId('channel-WHATSAPP');
    expect(within(whatsapp).getByText(/Precisa de um telemóvel verificado\./)).toBeVisible();
  });

  it('desligar os três canais externos de PAYMENT bloqueia "Guardar"', async () => {
    preferencesApi({ PUSH: true, EMAIL: true, SMS: true, WHATSAPP: true });
    renderRoute('/cliente/notificacoes/preferencias');
    await screen.findByTestId('category-PAYMENT', undefined, { timeout: 10_000 });
    const save = screen.getByRole('button', { name: 'Guardar' });
    expect(save).toBeDisabled();
    const user = userEvent.setup();
    await user.click(channelSwitch('PAYMENT', 'SMS'));
    await user.click(channelSwitch('PAYMENT', 'WhatsApp'));
    expect(save).toBeEnabled();
    await user.click(channelSwitch('PAYMENT', 'E-mail'));
    expect(
      within(section('PAYMENT')).getByText(
        'Estes avisos são obrigatórios: mantenha pelo menos SMS, WhatsApp ou e-mail.',
      ),
    ).toBeVisible();
    expect(save).toBeDisabled();
    await user.click(channelSwitch('PAYMENT', 'E-mail'));
    expect(save).toBeEnabled();
  });

  it('envia só as categorias alteradas', async () => {
    const bodies = preferencesApi();
    renderRoute('/cliente/notificacoes/preferencias');
    await screen.findByTestId('category-PROPOSAL', undefined, { timeout: 10_000 });
    const user = userEvent.setup();
    await user.click(channelSwitch('PROPOSAL', 'SMS'));
    await user.click(channelSwitch('QUOTE', 'WhatsApp'));
    await user.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(await screen.findByText('Preferências guardadas')).toBeVisible();
    expect(bodies).toEqual([
      {
        categories: [
          { category: 'PROPOSAL', channels: [{ channel: 'SMS', enabled: false }] },
          { category: 'QUOTE', channels: [{ channel: 'WHATSAPP', enabled: false }] },
        ],
      },
    ]);
  });

  it('erros do servidor aparecem na secção da categoria pelo índice no pedido', async () => {
    restore = signInCustomer(me, (c) => {
      if (is(c, 'GET', '/api/v1/me/notification-preferences')) {
        return { status: 200, data: preferencesFixture() };
      }
      if (is(c, 'PUT', '/api/v1/me/notification-preferences')) {
        return {
          status: 400,
          data: {
            code: 'VALIDATION_ERROR',
            errors: [{ field: 'categories[0].channels', code: 'MANDATORY_CATEGORY_NEEDS_CHANNEL' }],
          },
        };
      }
      return null;
    });
    renderRoute('/cliente/notificacoes/preferencias');
    await screen.findByTestId('category-LEGAL', undefined, { timeout: 10_000 });
    const user = userEvent.setup();
    // Só LEGAL muda: é o índice 0 do pedido (na resposta é o 2).
    await user.click(channelSwitch('LEGAL', 'SMS'));
    await user.click(screen.getByRole('button', { name: 'Guardar' }));
    expect(
      await within(section('LEGAL')).findByText(
        'Estes avisos são obrigatórios: mantenha pelo menos SMS, WhatsApp ou e-mail.',
      ),
    ).toBeVisible();
    expect(within(section('PAYMENT')).queryByRole('alert')).toBeNull();
  });

  it('sair com alterações por gravar pede confirmação', async () => {
    preferencesApi();
    const { router } = renderRoute('/cliente/notificacoes/preferencias');
    await screen.findByTestId('category-PROPOSAL', undefined, { timeout: 10_000 });
    const user = userEvent.setup();
    await user.click(channelSwitch('PROPOSAL', 'SMS'));
    await user.click(
      within(section('PROPOSAL')).getAllByRole('link', { name: 'Acrescentar no perfil' })[0]!,
    );
    const dialog = await screen.findByRole('dialog');
    expect(within(dialog).getByText('Sair sem guardar?')).toBeVisible();
    await user.click(within(dialog).getByRole('button', { name: 'Continuar a editar' }));
    expect(router.state.location.pathname).toBe('/cliente/notificacoes/preferencias');
  });

  it('regras do formulário', () => {
    const response = preferencesFixture({});
    const draft = draftFrom(response);
    expect(changedCategories(response, draft)).toEqual([]);
    draft.POLICY = { ...draft.POLICY, SMS: false, WHATSAPP: false, EMAIL: false };
    expect(validatePreferences(response, draft)).toEqual({
      POLICY: 'MANDATORY_CATEGORY_NEEDS_CHANNEL',
    });
    draft.POLICY.PUSH = true;
    draft.QUOTE = { ...draft.QUOTE, SMS: false, WHATSAPP: false, EMAIL: false };
    expect(Object.keys(validatePreferences(response, draft))).toEqual(['POLICY']);
  });
});
