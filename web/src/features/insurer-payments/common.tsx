import Chip, { type ChipProps } from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';

/** Chaves das consultas dos pagamentos da seguradora (invalidadas depois de cada resolução). */
export const insurerPaymentsKey = ['insurer-payments'] as const;
export const insurerPaymentKey = (id: string) => ['insurer-payment', id] as const;
export const paymentExceptionsKey = ['payment-exceptions'] as const;
export const openExceptionsCountKey = ['payment-exceptions', 'open-count'] as const;
export const paymentExceptionKey = (id: string) => ['payment-exception', id] as const;
export const paymentExceptionCandidatesKey = (id: string) =>
  ['payment-exception-candidates', id] as const;
export const paymentFilesKey = ['payment-files'] as const;
export const paymentFileSourceKey = ['payment-file-source'] as const;

export const paymentsPath = '/seguradora/pagamentos';
export const exceptionsPath = '/seguradora/pagamentos/excepcoes';
export const filesPath = '/seguradora/pagamentos/ficheiros';
export const paymentSettingsPath = '/seguradora/configuracao/pagamentos';

/** Ligação para o separador "Excepções" com a excepção aberta. */
export function exceptionLink(id: string): string {
  return `${exceptionsPath}?status=&excepcao=${encodeURIComponent(id)}`;
}

const paymentStatusColors: Record<string, ChipProps['color']> = {
  SUCCEEDED: 'success',
  PENDING: 'info',
  CREATED: 'info',
  UNKNOWN: 'warning',
  FAILED: 'error',
  EXPIRED: 'default',
  CANCELLED: 'default',
  REFUNDED: 'default',
};

/** Etiqueta do estado de um pagamento (`paymentStatus.<STATUS>`). */
export function PaymentStatusChip({ status }: { status: string }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={paymentStatusColors[status] ?? 'default'}
      label={t(`paymentStatus.${status}`)}
      data-status={status}
    />
  );
}

/** Texto opcional: a tradução se existir, senão o próprio código. */
export function useCodeText() {
  const { t, i18n } = useTranslation();
  return (prefix: string, code: string | undefined | null): string => {
    if (!code) return '';
    const key = `${prefix}.${code}`;
    return i18n.exists(key) ? t(key) : code;
  };
}
