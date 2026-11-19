import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { mockApi } from '../../test/mockApi';
import { brokerQuoteRequestFixture, proposalFixture } from '../../test/proposals';
import { renderRoute, renderWithAuth } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { BrokerVehicleDriverForm } from './BrokerVehicleDriverForm';

let restore: () => void = () => {};
afterEach(() => restore());

const broker = staffMe({
  roles: ['BROKER_AGENT'],
  permissions: ['quote:create-for-client', 'proposal:create-for-client'],
  orgType: 'BROKER',
});

describe('SendPaymentLinkButton (S5-11)', () => {
  const accepted = () =>
    proposalFixture({ status: 'ACCEPTED', channel: 'BROKER', expiresAt: '2027-01-20T10:00:00Z' });

  it('confirma, envia o link e mostra para onde foi', async () => {
    const user = userEvent.setup();
    let sent = 0;
    restore = signInStaff(
      broker,
      (c) => (is(c, 'GET', '/api/v1/proposals/p1') ? { status: 200, data: accepted() } : null),
      (c) => {
        if (!is(c, 'POST', '/api/v1/broker/proposals/p1/payment-link')) return null;
        sent++;
        return { status: 202, data: { sentTo: '+258*****4567', sentAt: '2027-01-10T09:00:00Z' } };
      },
    );
    renderRoute('/mediador/propostas/p1');
    const button = await screen.findByRole(
      'button',
      {
        name: 'Enviar link de pagamento ao cliente',
      },
      {
        timeout: 10_000,
      },
    );
    expect(button).not.toHaveClass('MuiButton-cta');
    await user.click(button);
    const dialog = await screen.findByRole('dialog');
    expect(
      within(dialog).getByText(
        'Vamos enviar um SMS ao cliente com o link para pagar. O cliente paga directamente à seguradora.',
      ),
    ).toBeInTheDocument();
    await user.click(within(dialog).getByRole('button', { name: 'Enviar' }));
    expect(await screen.findByText('Link enviado para +258*****4567')).toBeInTheDocument();
    expect(sent).toBe(1);
  });

  it('429 PAYMENT_LINK_RATE_LIMITED: mensagem com os minutos; cliente sem telemóvel', async () => {
    const user = userEvent.setup();
    let answer: { status: number; data: unknown } = {
      status: 429,
      data: { code: 'PAYMENT_LINK_RATE_LIMITED', retryAfterSeconds: 1500 },
    };
    restore = signInStaff(
      broker,
      (c) => (is(c, 'GET', '/api/v1/proposals/p1') ? { status: 200, data: accepted() } : null),
      (c) => (is(c, 'POST', '/api/v1/broker/proposals/p1/payment-link') ? answer : null),
    );
    renderRoute('/mediador/propostas/p1');
    await user.click(
      await screen.findByRole(
        'button',
        { name: 'Enviar link de pagamento ao cliente' },
        { timeout: 10_000 },
      ),
    );
    await user.click(
      within(await screen.findByRole('dialog')).getByRole('button', { name: 'Enviar' }),
    );
    expect(
      await screen.findByText(
        'Já enviou o link 3 vezes nas últimas 24 horas. Tente dentro de 25 minutos.',
      ),
    ).toBeInTheDocument();

    answer = { status: 409, data: { code: 'CUSTOMER_HAS_NO_MSISDN' } };
    await user.click(
      await screen.findByRole('button', { name: 'Enviar link de pagamento ao cliente' }),
    );
    await user.click(
      within(await screen.findByRole('dialog')).getByRole('button', { name: 'Enviar' }),
    );
    expect(await screen.findByText('O cliente não tem telemóvel registado.')).toBeInTheDocument();
  });
});

describe('BrokerVehicleDriverForm', () => {
  it('põe só de leitura os campos que a cotação tem e exige o chassis e o n.º da carta', async () => {
    const user = userEvent.setup();
    restore = mockApi(() => ({ status: 200, data: { values: [] } }));
    const onSubmit = vi.fn();
    renderWithAuth(
      <>
        <BrokerVehicleDriverForm
          formId="f"
          request={brokerQuoteRequestFixture()}
          onSubmit={onSubmit}
        />
        <button type="submit" form="f">
          enviar
        </button>
      </>,
    );
    for (const name of ['Matrícula', 'Ano', 'Categoria', 'Uso', 'Cilindrada (cm³)', 'Categorias']) {
      expect(screen.getByRole('textbox', { name })).toHaveAttribute('readonly');
    }
    expect(screen.getByRole('textbox', { name: /^Matrícula/ })).toHaveValue('AAA-100-MC');
    expect(
      screen.getByText(
        'Estes dados foram usados na simulação. Para os mudar, faça uma nova simulação.',
      ),
    ).toBeInTheDocument();
    expect(screen.getByRole('textbox', { name: /^N\.º de chassis/ })).not.toHaveAttribute(
      'readonly',
    );

    // Sem o chassis e o n.º da carta não envia.
    await user.click(screen.getByRole('button', { name: 'enviar' }));
    const chassis = screen.getByRole('textbox', { name: /^N\.º de chassis/ });
    const licence = screen.getByRole('textbox', { name: /^N\.º da carta/ });
    await waitFor(() => expect(chassis).toHaveAttribute('aria-invalid', 'true'));
    expect(licence).toHaveAttribute('aria-invalid', 'true');
    expect(onSubmit).not.toHaveBeenCalled();

    await user.type(chassis, 'JTDBR32E720012345');
    await user.type(screen.getByRole('textbox', { name: /^Marca/ }), 'Toyota');
    await user.type(screen.getByRole('textbox', { name: /^Modelo/ }), 'Corolla');
    await user.type(screen.getByRole('textbox', { name: /^Nome completo/ }), 'João Carlos Sitoe');
    await user.type(licence, 'MAP-123456');
    await user.click(screen.getByRole('button', { name: 'enviar' }));
    await waitFor(() => expect(onSubmit).toHaveBeenCalled());
    expect(onSubmit.mock.calls[0]![0]).toEqual({
      vehicle: {
        plate: 'AAA-100-MC',
        chassisNumber: 'JTDBR32E720012345',
        make: 'Toyota',
        model: 'Corolla',
        year: 2019,
        category: 'LIGHT_PASSENGER',
        engineCc: 1598,
        seats: 5,
        usage: 'PRIVATE',
        provinceOfCirculation: 'MAPUTO_CITY',
        commercialValue: { amount: 120000000, currency: 'MZN' },
        hasTracker: false,
      },
      driver: {
        fullName: 'João Carlos Sitoe',
        birthDate: '1991-09-09',
        licenceNumber: 'MAP-123456',
        licenceCategories: ['B'],
        licenceIssueDate: '2011-04-04',
        isOwner: false,
      },
    });
  });
});

describe('BrokerNewProposalPage', () => {
  it('mostra o tomador com REQUIRED em policyholder e envia vehicle, driver e policyholder', async () => {
    const user = userEvent.setup();
    const bodies: Record<string, unknown>[] = [];
    const created = proposalFixture({ id: 'p5', channel: 'BROKER', policyholderSource: 'BROKER' });
    restore = signInStaff(
      broker,
      (c) =>
        is(c, 'GET', '/api/v1/quotes/req1')
          ? { status: 200, data: brokerQuoteRequestFixture() }
          : null,
      (c) => {
        if (!is(c, 'POST', '/api/v1/broker/proposals')) return null;
        const body = JSON.parse(c.data as string) as Record<string, unknown>;
        bodies.push(body);
        if (!body.policyholder) {
          return {
            status: 400,
            data: {
              code: 'VALIDATION_ERROR',
              errors: [{ field: 'policyholder', code: 'REQUIRED' }],
            },
          };
        }
        return { status: 201, data: created };
      },
      (c) => (is(c, 'GET', '/api/v1/proposals/p5') ? { status: 200, data: created } : null),
      (c) =>
        c.url?.startsWith('/api/v1/reference') ? { status: 200, data: { values: [] } } : null,
    );
    const { router } = renderRoute('/mediador/propostas/nova?pedido=req1&cotacao=q1');

    const chassis = await screen.findByRole('textbox', { name: /^N\.º de chassis/ });
    await user.type(chassis, 'JTDBR32E720012345');
    await user.type(screen.getByRole('textbox', { name: /^Marca/ }), 'Toyota');
    await user.type(screen.getByRole('textbox', { name: /^Modelo/ }), 'Corolla');
    await user.click(screen.getByRole('checkbox', { name: 'O condutor é o tomador' }));
    await user.type(screen.getByRole('textbox', { name: /^Nome completo/ }), 'João Carlos Sitoe');
    await user.type(screen.getByRole('textbox', { name: /^N\.º da carta/ }), 'MAP-123456');
    await user.click(screen.getByRole('button', { name: 'Continuar' }));

    expect(
      await screen.findByText(
        'O perfil deste cliente está incompleto. Preencha os dados do tomador.',
      ),
    ).toBeInTheDocument();
    expect(bodies).toHaveLength(1);
    expect(bodies[0]).not.toHaveProperty('policyholder');
    const holder = screen
      .getByText('Tomador do seguro')
      .closest('.MuiCardContent-root') as HTMLElement;
    // O nome e a data de nascimento do condutor vêm sugeridos.
    expect(within(holder).getByRole('textbox', { name: /^Nome completo/ })).toHaveValue(
      'João Carlos Sitoe',
    );
    expect(within(holder).getByLabelText(/^Data de nascimento/)).toHaveValue('1991-09-09');
    await user.selectOptions(
      within(holder).getByRole('combobox', { name: /Tipo de documento/ }),
      'BI',
    );
    await user.type(
      within(holder).getByRole('textbox', { name: /Número do documento/ }),
      '110100765432B',
    );
    await user.type(within(holder).getByRole('textbox', { name: /^NUIT/ }), '123456789');
    await user.selectOptions(within(holder).getByRole('combobox', { name: /Província/ }), 'MAPUTO');
    await user.click(screen.getByRole('button', { name: 'Continuar' }));

    await waitFor(() => expect(router.state.location.pathname).toBe('/mediador/propostas/p5'));
    expect(bodies).toHaveLength(2);
    expect(bodies[1]).toMatchObject({
      quoteId: 'q1',
      vehicle: { chassisNumber: 'JTDBR32E720012345', plate: 'AAA-100-MC' },
      driver: { fullName: 'João Carlos Sitoe', licenceNumber: 'MAP-123456', isOwner: true },
      policyholder: {
        fullName: 'João Carlos Sitoe',
        birthDate: '1991-09-09',
        idDocType: 'BI',
        idDocNumber: '110100765432B',
        nuit: '123456789',
        province: 'MAPUTO',
      },
    });
  });
});
