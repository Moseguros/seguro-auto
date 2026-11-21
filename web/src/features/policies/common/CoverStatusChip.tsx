import Chip from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';
import type { CoverStatus } from '../../../api/generated';

export function coverStatusColor(status: CoverStatus) {
  if (status === 'COVERED') return 'success';
  if (status === 'NOT_STARTED') return 'info';
  if (status === 'SUSPENDED') return 'warning';
  if (status === 'CANCELLED' || status === 'LAPSED') return 'error';
  return 'default';
}

/** Estado da cobertura (`coverStatus.<STATUS>`): se o veículo está coberto hoje. */
export function CoverStatusChip({ status }: { status: CoverStatus }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={coverStatusColor(status)}
      label={t(`coverStatus.${status}`)}
      data-testid="cover-status"
      data-status={status}
    />
  );
}
