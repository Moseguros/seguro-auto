import Chip from '@mui/material/Chip';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import type { TFunction } from 'i18next';
import { useTranslation } from 'react-i18next';
import type {
  UnderwritingFailure,
  UnderwritingInputs,
  UnderwritingResult,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';

/** Valores de `inputs` que explicam uma falha (ex.: "Prémio 12 000,00 MT ≥ limite 10 000,00 MT"). */
export function failureDetail(
  t: TFunction,
  failure: UnderwritingFailure,
  inputs: UnderwritingInputs,
  language: string,
): string | null {
  const money = (amount: number) => formatMoney({ amount }, language);
  switch (failure) {
    case 'KYC_REQUIRED':
      return t('underwriting.input.KYC_REQUIRED', {
        total: money(inputs.totalAmount),
        limit: money(inputs.simplifiedKycMaxPremium),
      });
    case 'DECLARATION_POSITIVE':
      return inputs.positiveDeclarations.length
        ? t('underwriting.input.DECLARATION_POSITIVE', {
            questions: inputs.positiveDeclarations
              .map((code) => t(`declaration.${code}.short`))
              .join('; '),
          })
        : null;
    case 'INSPECTION_NOT_APPROVED':
      return inputs.inspectionStatus
        ? t('underwriting.input.INSPECTION_NOT_APPROVED', {
            status: t(`inspection.status.${inputs.inspectionStatus}`),
          })
        : null;
    case 'VEHICLE_VALUE_ABOVE_LIMIT':
      return inputs.vehicleValue != null
        ? t('underwriting.input.VEHICLE_VALUE_ABOVE_LIMIT', {
            value: money(inputs.vehicleValue),
            limit: money(inputs.maxVehicleValue),
          })
        : null;
    default:
      return null;
  }
}

/** Resultado da última avaliação das regras automáticas e as falhas, com os valores. */
export function UnderwritingPanel({ result }: { result?: UnderwritingResult }) {
  const { t, i18n } = useTranslation();
  if (!result) {
    return (
      <Typography color="text.secondary">{t('insurerProposals.underwriting.none')}</Typography>
    );
  }
  return (
    <Stack spacing={1}>
      <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
        <Chip
          size="small"
          color={result.outcome === 'AUTOMATIC' ? 'success' : 'default'}
          label={t(`underwriting.outcome.${result.outcome}`)}
        />
        <Typography variant="caption" color="text.secondary">
          {t('insurerProposals.underwriting.evaluatedAt', {
            date: formatDateTime(result.evaluatedAt, i18n.language),
          })}
          {' · '}
          {t(`underwriting.rulesSource.${result.rulesSource}`)}
        </Typography>
      </Stack>
      {result.failures.length === 0 ? (
        <Typography variant="body2">{t('insurerProposals.underwriting.noFailures')}</Typography>
      ) : (
        <List dense disablePadding aria-label={t('insurerProposals.underwriting.failures')}>
          {result.failures.map((failure) => (
            <ListItem key={failure} disableGutters>
              <ListItemText
                primary={t(`underwriting.failure.${failure}`)}
                secondary={failureDetail(t, failure, result.inputs, i18n.language)}
              />
            </ListItem>
          ))}
        </List>
      )}
    </Stack>
  );
}
