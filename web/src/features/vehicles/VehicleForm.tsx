import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Autocomplete from '@mui/material/Autocomplete';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import { useEffect, useMemo, useState } from 'react';
import { Controller, useForm, type FieldError as FormFieldError } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import {
  createMyVehicle,
  updateMyVehicle,
  type RegistryLookupResponse,
  type VehicleResponse,
} from '../../api/generated';
import { MoneyField } from '../../components/MoneyField';
import { getProblem, problemMessage } from '../../errors/problem';
import { RegistryLookupButton, type LookupNotice } from './RegistryLookupButton';
import { usePlateFormats, useReferenceValues } from './vehicleQueries';
import {
  codeMessage,
  emptyVehicleValues,
  isTrailer,
  provinces,
  registryValues,
  requiresWeight,
  toVehicleRequest,
  vehicleCategories,
  vehicleFieldOf,
  vehicleSchema,
  vehicleUsages,
  vehicleValuesFrom,
  type VehicleFormValues,
} from './vehicleSchema';

export interface VehicleFormProps {
  /** Veículo a alterar; sem ele, cria um novo. */
  vehicle?: VehicleResponse;
  onSaved(vehicle: VehicleResponse): void | Promise<void>;
  /** "Recarregar" depois de 409 `CONCURRENT_MODIFICATION`. */
  onReload(): void;
}

type TopMessage = { severity: 'warning' | 'error'; message: string; conflict?: boolean };

export function VehicleForm({ vehicle, onSaved, onReload }: VehicleFormProps) {
  const { t } = useTranslation();
  const [notice, setNotice] = useState<LookupNotice | null>(null);
  const [top, setTop] = useState<TopMessage | null>(null);
  const [moneyInvalid, setMoneyInvalid] = useState(false);
  const formats = usePlateFormats();
  const makes = useReferenceValues('VEHICLE_MAKE');

  const {
    control,
    register,
    handleSubmit,
    reset,
    setError,
    clearErrors,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<VehicleFormValues>({
    resolver: zodResolver(vehicleSchema()),
    defaultValues: vehicle ? vehicleValuesFrom(vehicle) : emptyVehicleValues,
  });

  useEffect(() => {
    if (vehicle) reset(vehicleValuesFrom(vehicle));
  }, [vehicle, reset]);

  const category = watch('category');
  const make = watch('make');
  const trailer = isTrailer(category);

  // Reboque: sem cilindrada e 0 lugares.
  useEffect(() => {
    if (trailer) {
      setValue('seats', '0');
      setValue('engineCc', '');
      clearErrors(['seats', 'engineCc']);
    }
  }, [trailer, setValue, clearErrors]);

  const makeCode = useMemo(() => {
    const typed = make.trim().toLowerCase();
    return makes.data?.find((value) => value.labelPt.toLowerCase() === typed)?.code;
  }, [make, makes.data]);
  const models = useReferenceValues('VEHICLE_MODEL', makeCode, Boolean(makeCode));

  const plateExamples = formats.data?.map((format) => format.example) ?? [];

  const errorText = (error: FormFieldError | undefined) =>
    error?.message ? codeMessage(t, error.message) : undefined;

  const fillFromRegistry = (data: RegistryLookupResponse) => {
    const values = registryValues(data);
    for (const [field, value] of Object.entries(values)) {
      setValue(field as keyof VehicleFormValues, value as string, { shouldDirty: true });
    }
    clearErrors();
  };

  const submit = handleSubmit(async (values) => {
    setTop(null);
    if (moneyInvalid) return;
    const request = toVehicleRequest(values);
    try {
      const saved = vehicle
        ? await updateMyVehicle(vehicle.id, { ...request, version: vehicle.version })
        : await createMyVehicle(request);
      await onSaved(saved);
    } catch (error) {
      const problem = getProblem(error);
      if (problem.code === 'VALIDATION_ERROR') {
        let applied = false;
        for (const fieldError of problem.errors) {
          const field = vehicleFieldOf(fieldError.field);
          if (field) {
            setError(field, { type: 'server', message: fieldError.code });
            applied = true;
          }
        }
        if (applied) return;
      }
      if (problem.code === 'VEHICLE_ALREADY_REGISTERED') {
        setError('plate', { type: 'server', message: problem.code }, { shouldFocus: true });
        return;
      }
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        setTop({ severity: 'warning', message: t('vehicles.form.conflict'), conflict: true });
        return;
      }
      setTop({
        severity: problem.code === 'VEHICLE_LIMIT_REACHED' ? 'warning' : 'error',
        message: problemMessage(t, problem),
      });
    }
  });

  const numeric = { htmlInput: { inputMode: 'numeric' as const } };

  return (
    <Stack component="form" spacing={2.5} onSubmit={submit} noValidate>
      {top && (
        <Alert
          severity={top.severity}
          role="alert"
          action={
            top.conflict ? (
              <Button color="inherit" size="small" onClick={onReload}>
                {t('vehicles.form.reload')}
              </Button>
            ) : undefined
          }
        >
          {top.message}
        </Alert>
      )}

      <Stack spacing={1}>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1.5}>
          <Controller
            control={control}
            name="plate"
            render={({ field }) => (
              <TextField
                label={t('vehicles.fields.plate')}
                autoComplete="off"
                error={!!errors.plate}
                helperText={
                  errorText(errors.plate) ??
                  (plateExamples.length > 0
                    ? t('vehicles.form.plateHelp', {
                        examples: plateExamples.join(t('vehicles.form.or')),
                      })
                    : undefined)
                }
                fullWidth
                name={field.name}
                inputRef={field.ref}
                value={field.value}
                onBlur={field.onBlur}
                onChange={(event) => field.onChange(event.target.value.toUpperCase())}
                slotProps={{ htmlInput: { maxLength: 20 } }}
              />
            )}
          />
          <RegistryLookupButton
            plate={watch('plate')}
            onFound={fillFromRegistry}
            onNotice={setNotice}
            onPlateError={(code) => setError('plate', { type: 'server', message: code })}
          />
        </Stack>
        {notice && (
          <Alert severity={notice.severity} onClose={() => setNotice(null)}>
            {notice.message}
          </Alert>
        )}
      </Stack>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' },
          gap: 2.5,
        }}
      >
        <Controller
          control={control}
          name="chassisNumber"
          render={({ field }) => (
            <TextField
              label={t('vehicles.fields.chassisNumber')}
              autoComplete="off"
              error={!!errors.chassisNumber}
              helperText={errorText(errors.chassisNumber)}
              fullWidth
              name={field.name}
              inputRef={field.ref}
              value={field.value}
              onBlur={field.onBlur}
              onChange={(event) => field.onChange(event.target.value.toUpperCase())}
            />
          )}
        />
        <Controller
          control={control}
          name="make"
          render={({ field }) => (
            <Autocomplete
              freeSolo
              options={makes.data?.map((value) => value.labelPt) ?? []}
              inputValue={field.value}
              onInputChange={(_, value) => field.onChange(value)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label={t('vehicles.fields.make')}
                  name={field.name}
                  inputRef={field.ref}
                  onBlur={field.onBlur}
                  error={!!errors.make}
                  helperText={errorText(errors.make)}
                  slotProps={{ htmlInput: { ...params.inputProps, maxLength: 60 } }}
                />
              )}
            />
          )}
        />
        <Controller
          control={control}
          name="model"
          render={({ field }) => (
            <Autocomplete
              freeSolo
              options={makeCode ? (models.data?.map((value) => value.labelPt) ?? []) : []}
              inputValue={field.value}
              onInputChange={(_, value) => field.onChange(value)}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label={t('vehicles.fields.model')}
                  name={field.name}
                  inputRef={field.ref}
                  onBlur={field.onBlur}
                  error={!!errors.model}
                  helperText={errorText(errors.model)}
                  slotProps={{ htmlInput: { ...params.inputProps, maxLength: 60 } }}
                />
              )}
            />
          )}
        />
        <TextField
          label={t('vehicles.fields.year')}
          error={!!errors.year}
          helperText={errorText(errors.year)}
          fullWidth
          slotProps={numeric}
          {...register('year')}
        />
        <TextField
          select
          label={t('vehicles.fields.category')}
          error={!!errors.category}
          helperText={errorText(errors.category)}
          fullWidth
          slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          {...register('category')}
        >
          <option value="">{t('vehicles.form.choose')}</option>
          {vehicleCategories.map((value) => (
            <option key={value} value={value}>
              {t(`vehicleCategory.${value}`)}
            </option>
          ))}
        </TextField>
        {!trailer && (
          <TextField
            label={t('vehicles.fields.engineCc')}
            error={!!errors.engineCc}
            helperText={errorText(errors.engineCc)}
            fullWidth
            slotProps={numeric}
            {...register('engineCc')}
          />
        )}
        {/* Controlado: o valor vem do formulário e não do DOM (desactivado em reboques). */}
        <Controller
          control={control}
          name="seats"
          render={({ field }) => (
            <TextField
              label={t('vehicles.fields.seats')}
              error={!!errors.seats}
              helperText={errorText(errors.seats)}
              fullWidth
              slotProps={numeric}
              disabled={trailer}
              name={field.name}
              inputRef={field.ref}
              value={field.value}
              onBlur={field.onBlur}
              onChange={field.onChange}
            />
          )}
        />
        <TextField
          label={t('vehicles.fields.grossWeightKg')}
          error={!!errors.grossWeightKg}
          helperText={
            errorText(errors.grossWeightKg) ??
            (requiresWeight(category) ? undefined : t('vehicles.form.optional'))
          }
          fullWidth
          slotProps={numeric}
          {...register('grossWeightKg')}
        />
        <TextField
          select
          label={t('vehicles.fields.usage')}
          error={!!errors.usage}
          helperText={errorText(errors.usage)}
          fullWidth
          slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          {...register('usage')}
        >
          <option value="">{t('vehicles.form.choose')}</option>
          {vehicleUsages.map((value) => (
            <option key={value} value={value}>
              {t(`vehicleUsage.${value}`)}
            </option>
          ))}
        </TextField>
        <TextField
          select
          label={t('vehicles.fields.provinceOfCirculation')}
          error={!!errors.provinceOfCirculation}
          helperText={errorText(errors.provinceOfCirculation)}
          fullWidth
          slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          {...register('provinceOfCirculation')}
        >
          <option value="">{t('vehicles.form.choose')}</option>
          {provinces.map((value) => (
            <option key={value} value={value}>
              {t(`province.${value}`)}
            </option>
          ))}
        </TextField>
        <Controller
          control={control}
          name="commercialValue"
          render={({ field }) => (
            <MoneyField
              label={t('vehicles.fields.commercialValue')}
              name={field.name}
              value={field.value}
              onChange={field.onChange}
              onValidityChange={(valid) => setMoneyInvalid(!valid)}
              error={!!errors.commercialValue}
              helperText={
                errorText(errors.commercialValue as FormFieldError | undefined) ??
                t('vehicles.form.commercialValueHelp')
              }
              fullWidth
            />
          )}
        />
      </Box>

      <Controller
        control={control}
        name="hasTracker"
        render={({ field }) => (
          <FormControlLabel
            control={
              <Switch
                checked={field.value}
                onChange={(event) => field.onChange(event.target.checked)}
                name={field.name}
              />
            }
            label={t('vehicles.fields.hasTracker')}
          />
        )}
      />

      <Box>
        <Button type="submit" variant="contained" size="large" disabled={isSubmitting}>
          {t('vehicles.form.save')}
        </Button>
      </Box>
    </Stack>
  );
}
