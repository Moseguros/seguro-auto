import { screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { renderWithAuth } from '../../test/render';
import { is, signInStaff, staffMe } from '../../test/staff';
import { InsurerLogoCard } from './InsurerLogoCard';

function file(name: string, type: string, size: number) {
  const result = new File(['x'], name, { type });
  Object.defineProperty(result, 'size', { value: size });
  return result;
}

const me = (permissions: string[]) =>
  staffMe({
    roles: ['PRODUCT_MANAGER'],
    permissions,
    orgType: 'INSURER',
    organization: { id: 'o1', displayName: 'Seguradora Demo', type: 'INSURER' },
  });

describe('InsurerLogoCard', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('recusa um PDF e um PNG de 2 MB sem chamar a API', async () => {
    const writes: string[] = [];
    restore = signInStaff(me(['product:read', 'product:manage']), (c) => {
      if (is(c, 'GET', '/api/v1/insurer/branding')) {
        return { status: 200, data: { logoFileId: null } };
      }
      writes.push(`${c.method?.toUpperCase()} ${c.url}`);
      return { status: 200, data: { logoFileId: 'f1' } };
    });
    const user = userEvent.setup({ applyAccept: false });
    renderWithAuth(<InsurerLogoCard />);
    const card = await screen.findByRole('region', { name: 'Logótipo da seguradora' });
    const upload = await within(card).findByRole('button', { name: 'Enviar logótipo' });
    expect(upload).toBeEnabled();
    expect(within(card).getByText('SD')).toBeInTheDocument();
    const input = within(card).getByLabelText('Enviar logótipo');

    await user.upload(input, file('logo.pdf', 'application/pdf', 1000));
    expect(within(card).getByRole('alert')).toHaveTextContent('Tipo de ficheiro não aceite.');

    await user.upload(input, file('logo.png', 'image/png', 2 * 1024 * 1024));
    expect(within(card).getByRole('alert')).toHaveTextContent('O logótipo tem mais de 1 MB.');
    expect(writes).toEqual([]);
  });

  it('sem product:manage não há botões', async () => {
    restore = signInStaff(me(['product:read']), (c) =>
      is(c, 'GET', '/api/v1/insurer/branding') ? { status: 200, data: { logoFileId: null } } : null,
    );
    renderWithAuth(<InsurerLogoCard />);
    const card = await screen.findByRole('region', { name: 'Logótipo da seguradora' });
    expect(
      within(card).getByText('Aparece nas cotações e nos certificados. PNG ou JPEG, até 1 MB.'),
    ).toBeInTheDocument();
    expect(within(card).queryByRole('button')).not.toBeInTheDocument();
  });
});
