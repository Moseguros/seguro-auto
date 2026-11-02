import { expect, test, type Page, type Route } from '@playwright/test';

type Who = 'admin' | 'insurer';

interface Scenario {
  /** O convite do primeiro gestor falha com este código. */
  inviteError?: string;
}

const orgId = '0192f0c4-0000-7000-8000-0000000000a1';

const people: Record<Who, Record<string, unknown>> = {
  admin: {
    id: '0192f0c4-0000-7000-8000-000000000001',
    type: 'STAFF',
    roles: ['PLATFORM_ADMIN'],
    permissions: [
      'otp-settings:read',
      'otp-settings:manage',
      'organization:read',
      'organization:manage',
      'org-user:read',
      'org-user:manage',
    ],
    preferredLanguage: 'pt',
    emailVerified: true,
    msisdnVerified: false,
    displayName: 'Rita Admin',
    email: 'admin@moseguros.co.mz',
  },
  insurer: {
    id: '0192f0c4-0000-7000-8000-000000000002',
    type: 'STAFF',
    roles: ['INSURER_ADMIN'],
    permissions: ['org-user:read', 'org-user:manage'],
    preferredLanguage: 'pt',
    emailVerified: true,
    msisdnVerified: false,
    displayName: 'Maria Gestora',
    email: 'maria@indico.co.mz',
    organization: { id: orgId, displayName: 'Índico Seguros', type: 'INSURER' },
  },
};

const tokensFor = (who: Who) => ({
  accessToken: `access-${who}`,
  refreshToken: `refresh-${who}`,
  expiresIn: 900,
});

const page = (content: unknown[]) => ({
  content,
  page: 0,
  size: 20,
  totalElements: content.length,
});

/** API simulada com estado: organizações e convites criados durante o teste. */
async function mockApi(p: Page, scenario: Scenario = {}) {
  const state = {
    organizations: [
      {
        id: '0192f0c4-0000-7000-8000-0000000000b1',
        type: 'OMBUDSMAN',
        legalName: 'Provedor Nacional do Cliente',
        displayName: 'Provedor Nacional',
        nuit: '400000001',
        status: 'ACTIVE',
        contactEmail: 'provedor@exemplo.co.mz',
        createdAt: '2026-10-01T08:00:00Z',
        updatedAt: '2026-10-01T08:00:00Z',
        version: 0,
      },
    ] as Record<string, unknown>[],
    invitations: [] as Record<string, unknown>[],
    created: [] as unknown[],
    invites: [] as unknown[],
    logins: [] as unknown[],
    activated: false,
  };
  const json = (route: Route, status: number, body?: unknown) =>
    route.fulfill({
      status,
      contentType: status >= 400 ? 'application/problem+json' : 'application/json',
      body: body === undefined ? '' : JSON.stringify(body),
    });
  const problem = (route: Route, status: number, code: string, extra = {}) =>
    json(route, status, { type: 'about:blank', title: code, status, code, ...extra });
  const who = (authorization: string | undefined): Who | null => {
    if (authorization === 'Bearer access-admin') return 'admin';
    if (authorization === 'Bearer access-insurer') return 'insurer';
    return null;
  };

  await p.route('**/api/v1/**', async (route) => {
    const request = route.request();
    const { pathname } = new URL(request.url());
    const method = request.method();
    const key = `${method} ${pathname}`;

    if (key === 'POST /api/v1/auth/login') {
      const body = request.postDataJSON() as { email: string; totpCode: string };
      state.logins.push(body);
      if (body.totpCode !== '123456') return problem(route, 401, 'INVALID_CREDENTIALS');
      return json(route, 200, tokensFor(body.email.startsWith('admin') ? 'admin' : 'insurer'));
    }
    if (key === 'POST /api/v1/auth/refresh') {
      const body = request.postDataJSON() as { refreshToken: string };
      const owner = body.refreshToken.replace('refresh-', '') as Who;
      return owner in people
        ? json(route, 200, tokensFor(owner))
        : problem(route, 401, 'REFRESH_TOKEN_INVALID');
    }
    if (key === 'POST /api/v1/auth/logout') return json(route, 204);
    if (key === 'GET /api/v1/auth/invitations/convite-1') {
      return json(route, 200, {
        fullName: 'Maria Gestora',
        email: 'maria@indico.co.mz',
        expiresAt: '2026-11-09T08:00:00Z',
        organization: { displayName: 'Índico Seguros', type: 'INSURER' },
      });
    }
    if (pathname.startsWith('/api/v1/auth/invitations/') && method === 'GET') {
      return problem(route, 404, 'INVITATION_INVALID');
    }
    if (key === 'POST /api/v1/auth/invitations/convite-1/accept') {
      return json(route, 200, {
        otpauthUri:
          'otpauth://totp/MoSeguros:maria%40indico.co.mz?secret=JBSWY3DPEHPK3PXPJBSWY3DP&issuer=MoSeguros',
        totpSecret: 'JBSWY3DPEHPK3PXPJBSWY3DP',
      });
    }
    if (key === 'POST /api/v1/auth/invitations/convite-1/activate') {
      const body = request.postDataJSON() as { totpCode: string };
      if (body.totpCode !== '654321') return problem(route, 400, 'TOTP_INVALID');
      state.activated = true;
      return json(route, 200, tokensFor('insurer'));
    }

    // Daqui para baixo, só com sessão.
    const user = who(request.headers()['authorization']);
    if (!user) return problem(route, 401, 'UNAUTHORIZED');
    if (key === 'GET /api/v1/me') return json(route, 200, people[user]);

    if (key === 'GET /api/v1/organizations') {
      const type = new URL(request.url()).searchParams.get('type');
      const rows = state.organizations.filter((o) => !type || o.type === type);
      return json(route, 200, page(rows));
    }
    if (key === 'POST /api/v1/organizations') {
      const body = request.postDataJSON() as Record<string, unknown>;
      state.created.push(body);
      const organization = {
        ...body,
        id: orgId,
        displayName: (body.tradeName as string) ?? body.legalName,
        status: 'ACTIVE',
        createdAt: '2026-11-02T08:00:00Z',
        updatedAt: '2026-11-02T08:00:00Z',
        version: 0,
      };
      state.organizations.push(organization);
      return json(route, 201, organization);
    }
    if (key === `GET /api/v1/organizations/${orgId}`) {
      const organization = state.organizations.find((o) => o.id === orgId);
      return organization
        ? json(route, 200, organization)
        : problem(route, 404, 'ORGANIZATION_NOT_FOUND');
    }
    if (key === 'GET /api/v1/admin/otp-settings') {
      return json(route, 200, {
        enabledChannels: ['SMS', 'WHATSAPP', 'EMAIL'],
        defaultChannel: 'WHATSAPP',
        customerCanChoose: true,
        fallbackOrder: ['WHATSAPP', 'SMS', 'EMAIL'],
        codeLength: 6,
        validitySeconds: 300,
        maxAttempts: 5,
        resendAfterSeconds: 60,
        maxRequestsPerHour: 5,
        updatedAt: '2026-11-01T08:00:00Z',
        updatedBy: 'system',
        version: 1,
      });
    }
    if (key === 'GET /api/v1/admin/users' || key === 'GET /api/v1/admin/invitations') {
      const admin = {
        id: people.admin.id,
        email: people.admin.email,
        displayName: people.admin.displayName,
        roles: people.admin.roles,
        status: 'ACTIVE',
        lastLoginAt: '2026-11-02T07:00:00Z',
        createdAt: '2026-10-01T08:00:00Z',
        version: 0,
      };
      return json(route, 200, page(key.endsWith('users') ? [admin] : []));
    }
    if (key === 'GET /api/v1/roles') {
      return json(route, 200, {
        roles: ['INSURER_ADMIN', 'PRODUCT_MANAGER', 'UNDERWRITER', 'FINANCE'],
        managerRoles: ['INSURER_ADMIN'],
        managerInviteRoles: ['INSURER_ADMIN'],
      });
    }
    if (key === `GET /api/v1/organizations/${orgId}/users`) return json(route, 200, page([]));
    if (key === `GET /api/v1/organizations/${orgId}/invitations`) {
      return json(route, 200, page(state.invitations));
    }
    if (key === `POST /api/v1/organizations/${orgId}/users`) {
      const body = request.postDataJSON() as Record<string, unknown>;
      state.invites.push(body);
      if (scenario.inviteError) return problem(route, 409, scenario.inviteError);
      const invitation = {
        ...body,
        id: `0192f0c4-0000-7000-8000-00000000010${state.invitations.length}`,
        organizationId: orgId,
        status: 'PENDING',
        createdAt: '2026-11-02T08:00:00Z',
        expiresAt: '2026-11-09T08:00:00Z',
      };
      state.invitations.push(invitation);
      return json(route, 201, invitation);
    }
    return problem(route, 404, 'NOT_FOUND');
  });
  return state;
}

async function expectNoHorizontalScroll(p: Page) {
  const overflow = await p.evaluate(
    () => document.documentElement.scrollWidth - document.documentElement.clientWidth,
  );
  expect(overflow).toBeLessThanOrEqual(0);
}

async function signIn(p: Page, email: string, path = '/entrar') {
  await p.goto(path);
  await expect(p.getByRole('heading', { name: 'Entrar na MoSeguros' })).toBeVisible();
  await p.getByLabel('E-mail').fill(email);
  await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
  await p.getByLabel('Código da aplicação de autenticação').fill('123456');
  await p.getByRole('button', { name: 'Entrar' }).click();
}

async function createInsurer(p: Page) {
  await p.goto('/admin/organizacoes/nova');
  await expect(p.getByRole('heading', { name: 'Nova organização' })).toBeVisible();
  await p.getByLabel('Tipo').selectOption('INSURER');
  await p.getByLabel('NUIT').fill('400123456');
  await p.getByLabel('Nome legal').fill('Índico Companhia de Seguros, SA');
  await p.getByLabel('Nome comercial').fill('Índico Seguros');
  await p.getByLabel('N.º de autorização ASFPM').fill('ASF-2026-001');
  await p.getByLabel('E-mail de contacto').fill('geral@indico.co.mz');
  await p.getByLabel('Código curto').fill('IDC');
  await p.getByLabel('E-mail de reclamações').fill('reclamacoes@indico.co.mz');
  await p.getByLabel('Provedor do cliente').selectOption({ label: 'Provedor Nacional' });
  const manager = p.getByRole('region', { name: 'Primeiro gestor' });
  await manager.getByLabel('Nome').fill('Maria Gestora');
  await manager.getByLabel('E-mail').fill('maria@indico.co.mz');
  await p.getByRole('button', { name: 'Criar organização' }).click();
}

test.describe('staff', () => {
  test('login de administrador vai para /admin', async ({ page: p }) => {
    const state = await mockApi(p);
    await signIn(p, 'admin@moseguros.co.mz');
    await expect(p).toHaveURL(/\/admin$/);
    expect(state.logins).toEqual([
      {
        email: 'admin@moseguros.co.mz',
        password: 'uma-palavra-passe-longa',
        totpCode: '123456',
      },
    ]);
    await expect(p.getByRole('navigation', { name: 'Administração' })).toContainText(
      'Organizações',
    );
  });

  test('criar uma seguradora com gestor abre o detalhe com o convite pendente', async ({
    page: p,
  }) => {
    const state = await mockApi(p);
    await signIn(p, 'admin@moseguros.co.mz');
    await expect(p).toHaveURL(/\/admin$/);
    await createInsurer(p);

    await expect(p).toHaveURL(new RegExp(`/admin/organizacoes/${orgId}$`));
    await expect(p.getByRole('heading', { name: 'Índico Seguros' })).toBeVisible();
    await expect(p.getByText('Esta organização ainda não tem gestor activo.')).toBeVisible();
    const invitation = p.getByTestId('invitation-row-maria@indico.co.mz');
    await expect(invitation).toContainText('Maria Gestora');
    await expect(invitation).toContainText('Gestor da seguradora');
    await expect(invitation).toContainText('Pendente');
    expect(state.created).toEqual([
      expect.objectContaining({
        type: 'INSURER',
        nuit: '400123456',
        licenseNumber: 'ASF-2026-001',
        insurerProfile: {
          shortCode: 'IDC',
          complaintsEmail: 'reclamacoes@indico.co.mz',
          ombudsmanOrganizationId: '0192f0c4-0000-7000-8000-0000000000b1',
        },
      }),
    ]);
    expect(state.invites).toEqual([
      { fullName: 'Maria Gestora', email: 'maria@indico.co.mz', roleCodes: ['INSURER_ADMIN'] },
    ]);
  });

  test('convite com o e-mail já em uso abre o detalhe com o erro e o diálogo preenchido', async ({
    page: p,
  }) => {
    await mockApi(p, { inviteError: 'EMAIL_ALREADY_IN_USE' });
    await signIn(p, 'admin@moseguros.co.mz');
    await expect(p).toHaveURL(/\/admin$/);
    await createInsurer(p);

    await expect(p).toHaveURL(new RegExp(`/admin/organizacoes/${orgId}$`));
    const dialog = p.getByRole('dialog', { name: 'Convidar utilizador' });
    await expect(dialog).toBeVisible();
    await expect(dialog.getByRole('alert')).toHaveText(
      'Este e-mail já pertence a outro utilizador.',
    );
    await expect(dialog.getByLabel('Nome')).toHaveValue('Maria Gestora');
    await expect(dialog.getByLabel('E-mail')).toHaveValue('maria@indico.co.mz');
    await expect(dialog.getByRole('checkbox', { name: 'Gestor da seguradora' })).toBeChecked();
  });

  test('activação pelo /convite/:token entra na área da seguradora', async ({ page: p }) => {
    const state = await mockApi(p);
    await p.goto('/convite/convite-1');
    await expect(p.getByRole('heading', { name: 'Activar a conta' })).toBeVisible();
    await expect(p.getByTestId('invitation-summary')).toContainText('Índico Seguros (Seguradora)');
    await p.getByLabel('Nova palavra-passe').fill('uma-palavra-passe-longa');
    await p.getByLabel('Confirmar a palavra-passe').fill('uma-palavra-passe-longa');
    await p.getByRole('button', { name: 'Continuar' }).click();

    await expect(p.getByText('Abra a aplicação de autenticação e leia o código QR.')).toBeVisible();
    await expect(
      p.getByRole('img', { name: 'Código QR para a aplicação de autenticação' }),
    ).toBeVisible();
    await expect(p.getByTestId('totp-secret')).toHaveText('JBSW Y3DP EHPK 3PXP JBSW Y3DP');
    await p.getByLabel('Código de 6 dígitos').fill('111111');
    await p.getByRole('button', { name: 'Activar a conta' }).click();
    await expect(p.getByRole('alert')).toHaveText('Código errado. Confirme a hora do telemóvel.');

    await p.getByLabel('Código de 6 dígitos').fill('654321');
    await p.getByRole('button', { name: 'Activar a conta' }).click();
    await expect(p).toHaveURL(/\/seguradora$/);
    expect(state.activated).toBe(true);
  });

  test('convite inválido mostra a mensagem', async ({ page: p }) => {
    await mockApi(p);
    await p.goto('/convite/outro');
    await expect(p.getByRole('alert')).toHaveText(
      'Este convite já não é válido. Peça um novo ao gestor da sua organização.',
    );
  });

  test('utilizador de seguradora a abrir /admin vai para /sem-acesso', async ({ page: p }) => {
    await mockApi(p);
    await signIn(p, 'maria@indico.co.mz');
    await expect(p).toHaveURL(/\/seguradora$/);
    await p.goto('/admin');
    await expect(p).toHaveURL(/\/sem-acesso$/);
    await expect(p.getByRole('heading', { name: 'Não tem acesso a esta área' })).toBeVisible();
  });

  test.describe('a 360×740', () => {
    test.use({ viewport: { width: 360, height: 740 } });

    test('o login é utilizável', async ({ page: p }) => {
      await mockApi(p);
      await p.goto('/entrar?voltar=%2Fadmin%2Fautenticacao');
      await expect(p.getByRole('heading', { name: 'Entrar na MoSeguros' })).toBeVisible();
      await expectNoHorizontalScroll(p);
      for (const label of ['E-mail', 'Código da aplicação de autenticação']) {
        await expect(p.getByLabel(label)).toBeInViewport();
      }
      await expect(p.getByRole('button', { name: 'Entrar' })).toBeInViewport();
      await p.getByLabel('E-mail').fill('admin@moseguros.co.mz');
      await p.getByLabel('Palavra-passe', { exact: true }).fill('uma-palavra-passe-longa');
      await p.getByLabel('Código da aplicação de autenticação').fill('000000');
      await p.getByRole('button', { name: 'Entrar' }).click();
      await expect(p.getByRole('alert')).toHaveText('E-mail, palavra-passe ou código errados.');
      await expectNoHorizontalScroll(p);

      await p.getByLabel('Código da aplicação de autenticação').fill('123456');
      await p.getByRole('button', { name: 'Entrar' }).click();
      await expect(p).toHaveURL(/\/admin\/autenticacao$/);
      await expect(p.getByRole('heading', { name: 'Autenticação dos clientes' })).toBeVisible();
      await expect(p.getByRole('switch', { name: 'WhatsApp' })).toBeChecked();
      await expectNoHorizontalScroll(p);
    });

    test('as páginas de administração cabem no ecrã', async ({ page: p }) => {
      await mockApi(p);
      await signIn(p, 'admin@moseguros.co.mz');
      await expect(p).toHaveURL(/\/admin$/);
      for (const [path, heading] of [
        ['/admin/organizacoes', 'Organizações'],
        ['/admin/organizacoes/nova', 'Nova organização'],
        ['/admin/utilizadores', 'Utilizadores'],
      ] as const) {
        await p.goto(path);
        await expect(p.getByRole('heading', { level: 1, name: heading })).toBeVisible();
        await expectNoHorizontalScroll(p);
      }
    });
  });
});
