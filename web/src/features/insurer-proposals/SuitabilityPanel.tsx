import Alert from '@mui/material/Alert';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { InsurerProposalResponse } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';

/** Questionário de adequação: respostas, resultado e avisos confirmados pelo cliente. */
export function SuitabilityPanel({ proposal }: { proposal: InsurerProposalResponse }) {
  const { t, i18n } = useTranslation();
  const suitability = proposal.suitability;
  if (!suitability) {
    return <Typography color="text.secondary">{t('insurerProposals.suitability.none')}</Typography>;
  }
  const warned = suitability.result === 'WARNED';

  return (
    <Stack spacing={1.5}>
      <Stack component="dl" spacing={1.25} sx={{ m: 0 }}>
        {suitability.answers.map((answer) => (
          <Stack key={answer.questionCode} spacing={0.25}>
            <Typography component="dt" variant="body2" color="text.secondary">
              {t(`suitability.${answer.questionCode}.question`)}
            </Typography>
            <Typography component="dd" variant="body2" fontWeight={600} sx={{ m: 0 }}>
              {t(`suitability.${answer.questionCode}.option.${answer.answer}`, {
                defaultValue: answer.answer,
              })}
            </Typography>
          </Stack>
        ))}
      </Stack>
      <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
        <Typography variant="body2">{t('insurerProposals.suitability.result')}</Typography>
        <Chip
          size="small"
          color={warned ? 'warning' : 'success'}
          label={t(`suitability.result.${suitability.result}`)}
        />
        <Typography variant="caption" color="text.secondary">
          {t('insurerProposals.suitability.answeredAt', {
            date: formatDateTime(suitability.answeredAt, i18n.language),
          })}
        </Typography>
      </Stack>
      {suitability.reasons.map((reason) => (
        <Alert key={reason} severity="warning">
          {t(`suitability.reason.${reason}`)}
        </Alert>
      ))}
      {warned && (
        <Typography variant="body2">
          {suitability.acknowledgedAt
            ? t('insurerProposals.suitability.acknowledgedAt', {
                date: formatDateTime(suitability.acknowledgedAt, i18n.language),
              })
            : t('insurerProposals.suitability.notAcknowledged')}
        </Typography>
      )}
    </Stack>
  );
}
