import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getProblem, problemMessage } from '../../../errors/problem';
import { formatMoney, mzn } from '../../../format/money';
import { codeMessage } from '../../vehicles/vehicleSchema';
import {
  defaultCoverages,
  toggleCoverage,
  useCoverageOptions,
  type CoverageOption,
} from '../coverageOptions';
import { isOwnDamage } from '../quoteRules';
import { useStepValidity, type RiskVehicle, type StepProps } from './stepTypes';

export interface CoveragesStepProps extends StepProps {
  vehicle: RiskVehicle;
}

/** Passo "Coberturas": as obrigatórias de todos os produtos e as opcionais (a união). */
export function CoveragesStep({
  draft,
  update,
  onValidity,
  serverErrors,
  vehicle,
}: CoveragesStepProps) {
  const { t } = useTranslation();
  const [theftNotice, setTheftNotice] = useState(false);
  const options = useCoverageOptions(vehicle.category, vehicle.usage, draft.productType);
  const data = options.data;

  // Coberturas por defeito, uma vez por tipo escolhido.
  useEffect(() => {
    if (!data || draft.coveragesFor === draft.productType) return;
    update({
      coverages: defaultCoverages(draft.productType, data.optional),
      coveragesFor: draft.productType,
    });
  }, [data, draft.coveragesFor, draft.productType, update]);

  const ready = !!data && draft.coveragesFor === draft.productType;
  const ownDamageMissing =
    draft.productType === 'COMPREHENSIVE' && !draft.coverages.some((c) => isOwnDamage(c.code));
  useStepValidity(ready && data.total > 0 && !ownDamageMissing, onValidity);

  if (options.isError) {
    return <Alert severity="error">{problemMessage(t, getProblem(options.error))}</Alert>;
  }
  if (!data) return <CircularProgress aria-label={t('auth.loading')} />;
  if (data.total === 0) {
    return <Alert severity="info">{t('quoteWizard.coverages.noProducts')}</Alert>;
  }

  const selectedOf = (code: string) => draft.coverages.find((choice) => choice.code === code);
  const toggle = (option: CoverageOption, checked: boolean) => {
    const result = toggleCoverage(draft.coverages, option, checked, data.optional);
    setTheftNotice(result.theftNotice);
    update({ coverages: result.selected });
  };
  const setCapital = (code: string, amount: number) =>
    update({
      coverages: draft.coverages.map((choice) =>
        choice.code === code ? { ...choice, capital: mzn(amount) } : choice,
      ),
    });

  const capitalInfo = (option: CoverageOption) => {
    if (option.capitalType === 'PER_PERSON' && option.capital) {
      return t('quoteWizard.coverages.perPerson', { capital: formatMoney(option.capital) });
    }
    return null;
  };

  const coverageError =
    serverErrors.coverages ??
    Object.entries(serverErrors).find(([key]) => key.startsWith('coverages['))?.[1];

  return (
    <Stack spacing={3}>
      {coverageError && <Alert severity="error">{codeMessage(t, coverageError)}</Alert>}
      {data.mandatory.length > 0 && (
        <Stack spacing={1}>
          <Typography variant="subtitle1" component="h3">
            {t('quoteWizard.coverages.included')}
          </Typography>
          {data.mandatory.map((option) => (
            <Box key={option.code}>
              <FormControlLabel
                control={<Checkbox checked disabled />}
                label={t(`coverage.${option.code}`)}
              />
              <Typography variant="body2" color="text.secondary" sx={{ pl: 4 }}>
                {[t('quoteWizard.coverages.mandatory'), capitalInfo(option)]
                  .filter(Boolean)
                  .join(' · ')}
              </Typography>
            </Box>
          ))}
        </Stack>
      )}

      {data.optional.length > 0 && (
        <Stack spacing={1}>
          <Typography variant="subtitle1" component="h3">
            {t('quoteWizard.coverages.optional')}
          </Typography>
          {data.optional.map((option) => {
            const selected = selectedOf(option.code);
            return (
              <Box key={option.code}>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={!!selected}
                      onChange={(event) => toggle(option, event.target.checked)}
                    />
                  }
                  label={t(`coverage.${option.code}`)}
                />
                <Typography variant="body2" color="text.secondary" sx={{ pl: 4 }}>
                  {[
                    t('quoteWizard.coverages.availableIn', {
                      n: option.availableIn,
                      total: data.total,
                    }),
                    capitalInfo(option),
                  ]
                    .filter(Boolean)
                    .join(' · ')}
                </Typography>
                {selected &&
                  option.capitalType === 'OPTIONS' &&
                  option.capitalOptions.length > 0 && (
                    <Box sx={{ pl: 4, pt: 1, maxWidth: 360 }}>
                      <TextField
                        select
                        size="small"
                        label={t('quoteWizard.coverages.capital', {
                          coverage: t(`coverage.${option.code}`),
                        })}
                        value={String(selected.capital?.amount ?? option.capitalOptions[0]!.amount)}
                        onChange={(event) => setCapital(option.code, Number(event.target.value))}
                        fullWidth
                        slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
                      >
                        {option.capitalOptions.map((capital) => (
                          <option key={capital.amount} value={capital.amount}>
                            {formatMoney(capital)}
                          </option>
                        ))}
                      </TextField>
                    </Box>
                  )}
              </Box>
            );
          })}
        </Stack>
      )}

      {theftNotice && (
        <Alert severity="info" onClose={() => setTheftNotice(false)}>
          {t('quoteWizard.coverages.theftNotice')}
        </Alert>
      )}
      {ownDamageMissing && <Alert severity="warning">{t('validation.OWN_DAMAGE_REQUIRED')}</Alert>}
    </Stack>
  );
}
