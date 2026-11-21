import Chip from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';
import type { PolicyStatus } from '../../../api/generated';

export function policyStatusColor(status: PolicyStatus) {
  if (status === 'ACTIVE') return 'success';
  if (status === 'SUSPENDED') return 'warning';
  if (status === 'CANCELLED' || status === 'LAPSED') return 'error';
  return 'default';
}

/** Estado da apólice (`policyStatus.<STATUS>`), partilhado pelo S6-05 e pelo S6-06. */
export function PolicyStatusChip({ status }: { status: PolicyStatus }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      color={policyStatusColor(status)}
      label={t(`policyStatus.${status}`)}
      data-testid="policy-status"
      data-status={status}
    />
  );
}
