import Chip, { type ChipProps } from '@mui/material/Chip';
import { useTranslation } from 'react-i18next';
import type { CoverStatus } from '../../../api/generated';

const colors: Record<CoverStatus, ChipProps['color']> = {
  COVERED: 'success',
  NOT_STARTED: 'info',
  ENDED: 'default',
  SUSPENDED: 'warning',
  CANCELLED: 'error',
  LAPSED: 'error',
};

/** Estado da cobertura no momento (`coverStatus.<STATUS>`), partilhado com o S6-06. */
export function CoverStatusChip({ status }: { status: CoverStatus }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      color={colors[status] ?? 'default'}
      label={t(`coverStatus.${status}`)}
      data-status={status}
    />
  );
}
