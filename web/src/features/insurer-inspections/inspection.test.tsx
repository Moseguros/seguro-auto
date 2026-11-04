import { cleanup, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { inspectionFixture } from '../../test/insurerProposals';
import { mockApi } from '../../test/mockApi';
import { renderWithAuth, renderWithProviders } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { ApproveInspectionDialog } from './ApproveInspectionDialog';
import { galleryTiles, PhotoGallery } from './PhotoGallery';

describe('PhotoGallery', () => {
  let restore: () => void = () => {};
  beforeEach(() => {
    URL.createObjectURL = vi.fn(() => 'blob:foto');
    URL.revokeObjectURL = vi.fn();
    restore = mockApi(() => ({ status: 200, data: new Blob(['x'], { type: 'image/jpeg' }) }));
  });
  afterEach(() => {
    cleanup();
    restore();
    Reflect.deleteProperty(URL, 'createObjectURL');
    Reflect.deleteProperty(URL, 'revokeObjectURL');
  });

  it('mostra "Sem fotografia" nos ângulos obrigatórios em falta', async () => {
    const inspection = inspectionFixture({
      photos: inspectionFixture().photos!.filter((p) => !['REAR', 'CHASSIS'].includes(p.slot)),
      missingSlots: ['REAR', 'CHASSIS'],
    });
    renderWithProviders(<PhotoGallery inspection={inspection} />);
    const missing = screen.getAllByTestId('missing-photo');
    expect(missing).toHaveLength(2);
    expect(within(missing[0]!).getByText('Sem fotografia')).toBeInTheDocument();
    expect(within(missing[0]!).getByText('Traseira')).toBeInTheDocument();
    expect(within(missing[1]!).getByText('Número do chassis')).toBeInTheDocument();
    expect(await screen.findByRole('img', { name: 'Frente' })).toHaveAttribute('src', 'blob:foto');
    expect(screen.getByRole('link', { name: 'Ver a localização no mapa' })).toHaveAttribute(
      'href',
      'https://www.openstreetmap.org/?mlat=-25.9655&mlon=32.5832',
    );
  });

  it('pela ordem dos ângulos; presencial sem quadrados vazios', () => {
    const order = galleryTiles(inspectionFixture()).map((tile) =>
      tile.kind === 'photo' ? tile.photo.slot : `-${tile.slot}`,
    );
    expect(order).toEqual(['FRONT', 'REAR', 'LEFT', 'RIGHT', 'ODOMETER', 'CHASSIS']);
    expect(galleryTiles(inspectionFixture({ mode: 'ON_SITE', photos: [] }))).toEqual([]);
  });

  it('abre o lightbox e passa à seguinte com a seta do teclado', async () => {
    renderWithProviders(<PhotoGallery inspection={inspectionFixture()} />);
    await userEvent.click(screen.getByRole('button', { name: 'Ampliar Frente' }));
    const dialog = await screen.findByRole('dialog', { name: 'Frente' });
    expect(within(dialog).getByText('1 de 6')).toBeInTheDocument();
    await userEvent.keyboard('{ArrowRight}');
    expect(await screen.findByRole('dialog', { name: 'Traseira' })).toBeInTheDocument();
    await userEvent.keyboard('{Escape}');
    await waitFor(() => expect(screen.queryByRole('dialog')).not.toBeInTheDocument());
  });
});

describe('ApproveInspectionDialog', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('exige notas com "presencial" e envia mode = ON_SITE', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(
      staffMe({ permissions: ['inspection:review'], orgType: 'INSURER' }),
      (c) => {
        if (is(c, 'POST', '/api/v1/insurer/inspections/insp1/approve')) {
          bodies.push(JSON.parse(c.data as string));
          return { status: 200, data: inspectionFixture({ status: 'APPROVED' }) };
        }
        return null;
      },
    );
    const onDone = vi.fn();
    renderWithAuth(
      <ApproveInspectionDialog
        inspection={inspectionFixture()}
        onClose={() => {}}
        onDone={onDone}
        onConflict={() => {}}
      />,
    );
    await userEvent.click(
      await screen.findByRole('checkbox', { name: 'Vistoria feita presencialmente' }),
    );
    await userEvent.click(screen.getByRole('button', { name: 'Aprovar' }));
    expect(bodies).toHaveLength(0);
    expect(screen.getByText('Descreva a vistoria presencial (obrigatório).')).toBeInTheDocument();

    await userEvent.type(screen.getByRole('textbox', { name: /Notas/ }), 'Visto na agência.');
    await userEvent.click(screen.getByRole('button', { name: 'Aprovar' }));
    await waitFor(() => expect(onDone).toHaveBeenCalled());
    expect(bodies).toEqual([{ mode: 'ON_SITE', notes: 'Visto na agência.', version: 3 }]);
  });

  it('sem "presencial" aprova sem notas; liga sozinho numa vistoria presencial', async () => {
    const bodies: unknown[] = [];
    restore = signInStaff(
      staffMe({ permissions: ['inspection:review'], orgType: 'INSURER' }),
      (c) => {
        if (is(c, 'POST', '/api/v1/insurer/inspections/insp1/approve')) {
          bodies.push(JSON.parse(c.data as string));
          return { status: 200, data: inspectionFixture({ status: 'APPROVED' }) };
        }
        return null;
      },
    );
    const onDone = vi.fn();
    const { unmount } = renderWithAuth(
      <ApproveInspectionDialog
        inspection={inspectionFixture()}
        onClose={() => {}}
        onDone={onDone}
        onConflict={() => {}}
      />,
    );
    await userEvent.click(await screen.findByRole('button', { name: 'Aprovar' }));
    await waitFor(() => expect(onDone).toHaveBeenCalled());
    expect(bodies).toEqual([{ version: 3 }]);
    unmount();

    renderWithAuth(
      <ApproveInspectionDialog
        inspection={inspectionFixture({ mode: 'ON_SITE' })}
        onClose={() => {}}
        onDone={() => {}}
        onConflict={() => {}}
      />,
    );
    expect(
      await screen.findByRole('checkbox', { name: 'Vistoria feita presencialmente' }),
    ).toBeChecked();
  });
});
