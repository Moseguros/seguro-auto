import { screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { afterEach, describe, expect, it } from 'vitest';
import { mockApi } from '../../../test/mockApi';
import { renderWithProviders } from '../../../test/render';
import { draftVersion } from '../testData';
import { DocumentsStep } from './DocumentsStep';

function file(name: string, type: string, size: number) {
  const result = new File(['x'], name, { type });
  Object.defineProperty(result, 'size', { value: size });
  return result;
}

describe('DocumentsStep', () => {
  let restore: () => void = () => {};
  afterEach(() => restore());

  it('recusa um JPEG e um PDF de 11 MB sem chamar a API', async () => {
    const calls: string[] = [];
    restore = mockApi((config) => {
      calls.push(`${config.method?.toUpperCase()} ${config.url}`);
      return { status: 201, data: {} };
    });
    const user = userEvent.setup({ applyAccept: false });
    renderWithProviders(
      <DocumentsStep
        version={draftVersion}
        readOnly={false}
        errors={{}}
        onChanged={async () => {}}
      />,
    );
    const section = screen.getByRole('region', { name: 'Condições gerais' });
    expect(within(section).getByText('Em falta')).toBeInTheDocument();
    const input = within(section).getByLabelText('Enviar PDF');

    await user.upload(input, file('foto.jpg', 'image/jpeg', 2000));
    expect(within(section).getByRole('alert')).toHaveTextContent('Tipo de ficheiro não aceite.');

    await user.upload(input, file('grande.pdf', 'application/pdf', 11 * 1024 * 1024));
    expect(within(section).getByRole('alert')).toHaveTextContent('O ficheiro tem mais de 10 MB.');
    expect(calls).toEqual([]);
  });

  it('envia um PDF com o tipo e a língua do espaço', async () => {
    const bodies: FormData[] = [];
    restore = mockApi((config) => {
      bodies.push(config.data as FormData);
      return {
        status: 201,
        data: { id: 'd1', docType: 'GENERAL_CONDITIONS', language: 'pt', fileId: 'f1' },
      };
    });
    let changed = 0;
    renderWithProviders(
      <DocumentsStep
        version={draftVersion}
        readOnly={false}
        errors={{}}
        onChanged={async () => {
          changed += 1;
        }}
      />,
    );
    const section = screen.getByRole('region', { name: 'Informação pré-contratual (inglês)' });
    await userEvent.upload(
      within(section).getByLabelText('Enviar PDF'),
      file('ipc.pdf', 'application/pdf', 1000),
    );
    await waitFor(() => expect(changed).toBe(1));
    expect(bodies[0]?.get('docType')).toBe('PRE_CONTRACT_INFO');
    expect(bodies[0]?.get('language')).toBe('en');
  });
});
