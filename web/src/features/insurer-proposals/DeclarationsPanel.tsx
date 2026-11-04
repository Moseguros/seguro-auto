import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { alpha } from '@mui/material/styles';
import { useTranslation } from 'react-i18next';
import type { InsurerProposalResponse } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';

/** Declarações de risco: cada pergunta com a resposta; os "sim" destacados com os seguimentos. */
export function DeclarationsPanel({ proposal }: { proposal: InsurerProposalResponse }) {
  const { t, i18n } = useTranslation();
  const declarations = proposal.declarations;
  if (!declarations) {
    return (
      <Typography color="text.secondary">{t('insurerProposals.declarations.none')}</Typography>
    );
  }
  const usage = t(`vehicleUsage.${proposal.vehicle.usage}`);

  return (
    <Stack spacing={1.5}>
      <Stack component="ol" spacing={1.5} sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {declarations.answers.map((answer) => (
          <Box
            component="li"
            key={answer.questionCode}
            data-answer={answer.answer ? 'yes' : 'no'}
            sx={{
              p: 1.5,
              borderRadius: 1,
              border: 1,
              borderColor: answer.answer ? 'warning.main' : 'divider',
              bgcolor: (theme) =>
                answer.answer ? alpha(theme.palette.warning.main, 0.12) : 'transparent',
            }}
          >
            <Stack spacing={0.5}>
              <Typography variant="body2">
                {t(`declaration.${answer.questionCode}.question`, { usage })}
              </Typography>
              <div>
                <Chip
                  size="small"
                  label={answer.answer ? t('common.yes') : t('common.no')}
                  color={answer.answer ? 'warning' : 'default'}
                  variant={answer.answer ? 'filled' : 'outlined'}
                  aria-label={`${t('insurerProposals.declarations.answer')}: ${answer.answer ? t('common.yes') : t('common.no')}`}
                />
              </div>
              {answer.answer && answer.claimsCount != null && (
                <Typography variant="body2">
                  <strong>{t('declaration.CLAIMS_LAST_3_YEARS.claimsCount')}</strong>{' '}
                  {answer.claimsCount}
                </Typography>
              )}
              {answer.answer && answer.details && (
                <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
                  <strong>{t(`declaration.${answer.questionCode}.details`)}</strong>{' '}
                  {answer.details}
                </Typography>
              )}
            </Stack>
          </Box>
        ))}
      </Stack>
      <Typography variant="caption" color="text.secondary">
        {t('insurerProposals.declarations.confirmedAt', {
          date: formatDateTime(declarations.confirmedAt, i18n.language),
        })}
      </Typography>
    </Stack>
  );
}
