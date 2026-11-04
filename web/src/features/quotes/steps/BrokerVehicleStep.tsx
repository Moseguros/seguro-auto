import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import TextField, { type TextFieldProps } from '@mui/material/TextField';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { MoneyField } from '../../../components/MoneyField';
import { provinces } from '../../customer-profile/profileSchema';
import {
  codeMessage,
  isTrailer,
  requiresWeight,
  vehicleCategories,
  vehicleUsages,
} from '../../vehicles/vehicleSchema';
import { brokerVehicleSchema, fieldErrors, type BrokerVehicleValues } from '../quoteRules';
import { useStepValidity, type StepProps } from './stepTypes';

type Field = keyof BrokerVehicleValues;

/** Passo "Veículo" do mediador: só o que a tarifa usa (S3-02 §6). */
export function BrokerVehicleStep({ draft, update, onValidity, serverErrors }: StepProps) {
  const { t } = useTranslation();
  const [touched, setTouched] = useState<Set<Field>>(() => new Set());
  const [moneyInvalid, setMoneyInvalid] = useState(false);
  const values = draft.vehicle;
  const errors = useMemo(() => fieldErrors(brokerVehicleSchema().safeParse(values)), [values]);
  useStepValidity(Object.keys(errors).length === 0 && !moneyInvalid, onValidity);

  const trailer = isTrailer(values.category);
  const set = (patch: Partial<BrokerVehicleValues>) =>
    update((current) => ({ vehicle: { ...current.vehicle, ...patch } }));
  const touch = (field: Field) => setTouched((current) => new Set(current).add(field));

  const errorOf = (field: Field) => {
    const code =
      serverErrors[`vehicle.${field}`] ?? (touched.has(field) ? errors[field] : undefined);
    return code ? codeMessage(t, code) : undefined;
  };

  const text = (field: Field, label: string, extra: Partial<TextFieldProps> = {}) => {
    const message = errorOf(field);
    return (
      <TextField
        label={label}
        value={values[field] as string}
        onChange={(event) => set({ [field]: event.target.value })}
        onBlur={() => touch(field)}
        error={!!message}
        helperText={message ?? extra.helperText}
        fullWidth
        autoComplete="off"
        {...extra}
      />
    );
  };

  const select = (field: Field, label: string, options: readonly string[], prefix: string) => {
    const message = errorOf(field);
    return (
      <TextField
        select
        label={label}
        value={values[field] as string}
        onChange={(event) => {
          const value = event.target.value;
          if (field === 'category' && isTrailer(value))
            set({ category: value, seats: '0', engineCc: '' });
          else set({ [field]: value });
        }}
        onBlur={() => touch(field)}
        error={!!message}
        helperText={message}
        fullWidth
        slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
      >
        <option value="">{t('vehicles.form.choose')}</option>
        {options.map((value) => (
          <option key={value} value={value}>
            {t(`${prefix}.${value}`)}
          </option>
        ))}
      </TextField>
    );
  };

  const numeric = { htmlInput: { inputMode: 'numeric' as const } };
  const optional = t('vehicles.form.optional');

  return (
    <Box sx={{ display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 2.5 }}>
      {select('category', t('vehicles.fields.category'), vehicleCategories, 'vehicleCategory')}
      {select('usage', t('vehicles.fields.usage'), vehicleUsages, 'vehicleUsage')}
      {text('year', t('vehicles.fields.year'), { slotProps: numeric })}
      {select(
        'provinceOfCirculation',
        t('vehicles.fields.provinceOfCirculation'),
        provinces,
        'province',
      )}
      {text('seats', t('vehicles.fields.seats'), { slotProps: numeric, disabled: trailer })}
      {!trailer &&
        text('engineCc', t('vehicles.fields.engineCc'), {
          slotProps: numeric,
          helperText: optional,
        })}
      {text('grossWeightKg', t('vehicles.fields.grossWeightKg'), {
        slotProps: numeric,
        helperText: requiresWeight(values.category) ? undefined : optional,
      })}
      <MoneyField
        label={t('vehicles.fields.commercialValue')}
        value={values.commercialValue}
        onChange={(commercialValue) => set({ commercialValue })}
        onValidityChange={(valid) => setMoneyInvalid(!valid)}
        error={!!errorOf('commercialValue')}
        helperText={errorOf('commercialValue') ?? t('quoteWizard.brokerVehicle.valueHelp')}
      />
      {text('plate', t('vehicles.fields.plate'), {
        helperText: optional,
        onChange: (event) => set({ plate: event.target.value.toUpperCase() }),
      })}
      {text('make', t('vehicles.fields.make'), { helperText: optional })}
      {text('model', t('vehicles.fields.model'), { helperText: optional })}
      <FormControlLabel
        control={
          <Switch
            checked={values.hasTracker}
            onChange={(event) => set({ hasTracker: event.target.checked })}
          />
        }
        label={t('vehicles.fields.hasTracker')}
      />
    </Box>
  );
}
