import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { todayInMaputo } from '../../../format/dates';
import { codeMessage } from '../../vehicles/vehicleSchema';
import { useReferenceLabel, useReferenceValues } from '../../vehicles/vehicleQueries';
import { brokerDriverSchema, fieldErrors, type BrokerDriverValues } from '../quoteRules';
import { useStepValidity, type StepProps } from './stepTypes';

type Field = keyof BrokerDriverValues;

/** Passo "Condutor" do mediador: data de nascimento e, opcionalmente, a carta. */
export function BrokerDriverStep({ draft, update, onValidity, serverErrors }: StepProps) {
  const { t } = useTranslation();
  const [touched, setTouched] = useState<Set<Field>>(() => new Set());
  const categories = useReferenceValues('LICENCE_CATEGORY');
  const label = useReferenceLabel();
  const values = draft.driver;
  const errors = useMemo(() => fieldErrors(brokerDriverSchema().safeParse(values)), [values]);
  useStepValidity(Object.keys(errors).length === 0, onValidity);

  const set = (patch: Partial<BrokerDriverValues>) =>
    update((current) => ({ driver: { ...current.driver, ...patch } }));
  const touch = (field: Field) => setTouched((current) => new Set(current).add(field));
  const errorOf = (field: Field) => {
    const server = Object.entries(serverErrors).find(
      ([key]) => key === `driver.${field}` || key.startsWith(`driver.${field}[`),
    )?.[1];
    const code =
      server ?? (touched.has(field) || values[field].length > 0 ? errors[field] : undefined);
    return code ? codeMessage(t, code) : undefined;
  };
  const today = todayInMaputo();

  const birthError = errorOf('birthDate');
  const issueError = errorOf('licenceIssueDate');
  const categoriesError = errorOf('licenceCategories');

  return (
    <Stack spacing={2.5}>
      <TextField
        type="date"
        label={t('drivers.fields.birthDate')}
        value={values.birthDate}
        onChange={(event) => set({ birthDate: event.target.value })}
        onBlur={() => touch('birthDate')}
        error={!!birthError}
        helperText={birthError}
        fullWidth
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: today } }}
      />
      <TextField
        type="date"
        label={t('quoteWizard.brokerDriver.licenceIssueDate')}
        value={values.licenceIssueDate}
        onChange={(event) => set({ licenceIssueDate: event.target.value })}
        onBlur={() => touch('licenceIssueDate')}
        error={!!issueError}
        helperText={issueError ?? t('vehicles.form.optional')}
        fullWidth
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: today } }}
      />
      <FormControl component="fieldset" error={!!categoriesError}>
        <FormLabel component="legend">{t('quoteWizard.brokerDriver.licenceCategories')}</FormLabel>
        {categories.isPending ? (
          <CircularProgress size={24} aria-label={t('auth.loading')} />
        ) : (
          <FormGroup row>
            {(categories.data ?? []).map((category) => (
              <FormControlLabel
                key={category.code}
                label={label(category)}
                control={
                  <Checkbox
                    checked={values.licenceCategories.includes(category.code)}
                    onChange={(event) =>
                      set({
                        licenceCategories: event.target.checked
                          ? [...values.licenceCategories, category.code]
                          : values.licenceCategories.filter((code) => code !== category.code),
                      })
                    }
                  />
                }
              />
            ))}
          </FormGroup>
        )}
        {categoriesError && <FormHelperText>{categoriesError}</FormHelperText>}
      </FormControl>
    </Stack>
  );
}
