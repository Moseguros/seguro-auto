import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import type { VerificationResult } from '../../api/generated';
import { renderWithProviders } from '../../test/render';
import { VerificationResultCard, type VerificationView } from './VerificationResultCard';

const base: VerificationView = {
  status: 'VALID',
  checkedAt: '2027-02-10T08:31:00Z',
  plateMasked: 'ABC-***-MC',
  insurerName: 'Seguradora Demo',
  validFrom: '2027-02-10T08:31:00Z',
  validTo: '2028-02-09T21:59:00Z',
  coverageType: 'MTPL_ONLY',
};

const variants: [VerificationResult, string, string][] = [
  ['VALID', 'VerifiedUserIcon', 'Seguro válido'],
  ['NOT_YET_VALID', 'ScheduleIcon', 'O seguro ainda não começou'],
  ['EXPIRED', 'EventBusyIcon', 'Seguro expirado'],
  ['SUSPENDED', 'PauseCircleIcon', 'Seguro suspenso'],
  ['CANCELLED', 'CancelIcon', 'Seguro anulado'],
  ['NOT_FOUND', 'HelpOutlineIcon', 'Sem seguro registado na MoSeguros'],
];

describe('VerificationResultCard', () => {
  it.each(variants)('%s tem ícone e título', (status, icon, title) => {
    renderWithProviders(<VerificationResultCard result={{ ...base, status }} />);
    expect(screen.getByRole('heading', { name: title })).toBeInTheDocument();
    expect(screen.getByTestId(icon)).toBeInTheDocument();
  });

  it('as datas do resultado usam dd/MM/yyyy HH:mm em Maputo', () => {
    renderWithProviders(<VerificationResultCard result={base} />);
    expect(screen.getByText('Válido de 10/02/2027 10:31 a 09/02/2028 23:59')).toBeInTheDocument();
    expect(screen.getByText('Verificado em 10/02/2027 10:31')).toBeInTheDocument();
    expect(screen.getByText('Responsabilidade civil')).toBeInTheDocument();
    expect(screen.getByText('ABC-***-MC')).toBeInTheDocument();
  });

  it('NOT_FOUND mostra a nota', () => {
    renderWithProviders(
      <VerificationResultCard result={{ status: 'NOT_FOUND', checkedAt: base.checkedAt }} />,
    );
    expect(
      screen.getByText(/Não encontrámos nenhum seguro para esta matrícula na MoSeguros/),
    ).toBeInTheDocument();
  });

  it('certificado REVOKED mostra o aviso', () => {
    renderWithProviders(
      <VerificationResultCard result={{ ...base, certificateStatus: 'REVOKED' }} />,
    );
    expect(
      screen.getByText(
        'Este certificado foi substituído por um mais recente. O estado acima é o actual.',
      ),
    ).toBeInTheDocument();
  });

  it('vista da polícia acrescenta a apólice, o certificado e o veículo', () => {
    renderWithProviders(
      <VerificationResultCard
        police
        result={{
          ...base,
          policyNumber: 'AP-2027-900001',
          certificateNumber: 'CT-2027-000001',
          vehicleMake: 'Toyota',
          vehicleModel: 'Hilux',
        }}
      />,
    );
    expect(screen.getByText('AP-2027-900001')).toBeInTheDocument();
    expect(screen.getByText('CT-2027-000001')).toBeInTheDocument();
    expect(screen.getByText('Toyota Hilux')).toBeInTheDocument();
    expect(
      screen.getByText('Confirme que a matrícula e o veículo correspondem.'),
    ).toBeInTheDocument();
  });

  it('a versão compacta só tem ícone e título', () => {
    renderWithProviders(<VerificationResultCard compact result={base} />);
    expect(screen.getByText('Seguro válido')).toBeInTheDocument();
    expect(screen.getByTestId('VerifiedUserIcon')).toBeInTheDocument();
    expect(screen.queryByText('ABC-***-MC')).not.toBeInTheDocument();
  });
});
