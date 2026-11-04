import Chip, { type ChipProps } from '@mui/material/Chip';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { SlaStatus } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';

const colors: Record<NonNullable<SlaStatus>, ChipProps['color']> = {
  ON_TIME: 'success',
  ALERT: 'warning',
  OVERDUE: 'error',
};

/** Estado do prazo de decisão (verde, âmbar, vermelho), com a data limite no `title`. */
export function SlaChip({ status, dueAt }: { status?: SlaStatus; dueAt?: string | null }) {
  const { t, i18n } = useTranslation();
  if (!status) {
    return (
      <Typography component="span" variant="body2" color="text.secondary">
        —
      </Typography>
    );
  }
  const title = dueAt
    ? t('insurerProposals.slaDue', { date: formatDateTime(dueAt, i18n.language) })
    : '';
  return (
    <Chip
      size="small"
      color={colors[status]}
      label={t(`slaStatus.${status}`)}
      title={title || undefined}
      data-sla={status}
    />
  );
}
