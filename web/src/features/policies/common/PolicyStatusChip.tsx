import Chip, { type ChipProps } from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';
import type { PolicyStatus } from '../../../api/generated';

const colors: Record<PolicyStatus, ChipProps['color']> = {
  ACTIVE: 'success',
  SUSPENDED: 'warning',
  CANCELLED: 'error',
  EXPIRED: 'default',
  LAPSED: 'error',
};

/** Estado da apólice (`policyStatus.<STATUS>`), partilhado com o S6-06. */
export function PolicyStatusChip({ status }: { status: PolicyStatus }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={colors[status] ?? 'default'}
      label={t(`policyStatus.${status}`)}
      data-status={status}
    />
  );
}
