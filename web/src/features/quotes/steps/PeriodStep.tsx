import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { formatDate } from '../../../i18n/format';
import { codeMessage } from '../../vehicles/vehicleSchema';
import { endDateOf, startDateRange } from '../period';
import { allowedDurations, fieldErrors, quoteTermsSchema } from '../quoteRules';
import { durationText } from '../quoteFormat';
import { useStepValidity, type StepProps } from './stepTypes';

/** Passo "Início e duração". */
export function PeriodStep({ draft, update, onValidity, serverErrors }: StepProps) {
  const { t, i18n } = useTranslation();
  const { min, max } = startDateRange();
  const errors = fieldErrors(
    quoteTermsSchema().safeParse({
      productType: draft.productType,
      durationDays: draft.durationDays,
      startDate: draft.startDate,
      coverages: [],
    }),
  );
  const startError = serverErrors.startDate ?? (draft.startDate ? errors.startDate : undefined);
  const durationError = serverErrors.durationDays ?? errors.durationDays;
  useStepValidity(!errors.startDate && !errors.durationDays, onValidity);
  const end = !errors.startDate ? endDateOf(draft.startDate, draft.durationDays) : '';
  const durations = allowedDurations(draft.productType);

  return (
    <Stack spacing={3}>
      <TextField
        type="date"
        label={t('quoteWizard.period.startDate')}
        value={draft.startDate}
        onChange={(event) => update({ startDate: event.target.value })}
        error={!!startError}
        helperText={startError ? codeMessage(t, startError) : undefined}
        fullWidth
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { min, max } }}
      />
      {durations.length > 1 ? (
        <FormControl error={!!durationError}>
          <FormLabel id="quote-duration">{t('quoteWizard.period.duration')}</FormLabel>
          <RadioGroup
            aria-labelledby="quote-duration"
            value={String(draft.durationDays)}
            onChange={(event) => update({ durationDays: Number(event.target.value) })}
          >
            {durations.map((days) => (
              <FormControlLabel
                key={days}
                value={String(days)}
                control={<Radio />}
                label={durationText(t, days)}
              />
            ))}
          </RadioGroup>
        </FormControl>
      ) : (
        <Stack spacing={0.5}>
          <Typography variant="subtitle2" color="text.secondary">
            {t('quoteWizard.period.duration')}
          </Typography>
          <Typography>{durationText(t, 365)}</Typography>
        </Stack>
      )}
      {end && (
        <Typography data-testid="quote-end-date">
          {t('quoteWizard.period.endsOn', { date: formatDate(end, i18n.language) })}
        </Typography>
      )}
    </Stack>
  );
}
