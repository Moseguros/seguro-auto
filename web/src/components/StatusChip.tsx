import Chip, { type ChipProps } from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';

const colors: Record<string, ChipProps['color']> = {
  ACTIVE: 'success',
  ACCEPTED: 'success',
  PENDING_ACTIVATION: 'info',
  PENDING: 'info',
  LOCKED: 'warning',
  EXPIRED: 'warning',
  DISABLED: 'default',
  REVOKED: 'default',
  SUSPENDED: 'error',
};

/** Etiqueta de estado (`status.<STATUS>`). */
export function StatusChip({ status }: { status: string }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={colors[status] ?? 'default'}
      label={t(`status.${status}`)}
      data-status={status}
    />
  );
}
