import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useEffect, type ReactNode } from 'react';
import { Controller, useForm, type FieldError as FormFieldError } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import type {
  BrokerProposalDriverRequest,
  BrokerProposalVehicleRequest,
  FieldError,
  QuoteRequestResponse,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import { normalizeName } from '../customer-profile/profileSchema';
import { driverSchema, normalizeLicenceNumber } from '../drivers/driverSchema';
import { useReferenceLabel, useReferenceValues } from '../vehicles/vehicleQueries';
import {
  codeMessage,
  emptyVehicleValues,
  isTrailer,
  requiresWeight,
  toVehicleRequest,
  vehicleSchema,
  type VehicleFormValues,
} from '../vehicles/vehicleSchema';

export interface VehicleDriverPayload {
  vehicle: BrokerProposalVehicleRequest;
  driver: BrokerProposalDriverRequest;
}

export interface BrokerVehicleDriverFormProps {
  formId: string;
  /** Pedido de cotação do mediador (`getQuoteRequest`). */
  request: QuoteRequestResponse;
  /** Erros de campo do servidor (`vehicle.*` / `driver.*`). */
  serverErrors?: FieldError[];
  onSubmit(payload: VehicleDriverPayload): void | Promise<void>;
}

const formSchema = (today?: string) =>
  z.object({
    vehicle: vehicleSchema(),
    driver: driverSchema({ licenceRequired: true, today }),
  });

type FormValues = z.infer<ReturnType<typeof formSchema>>;

const text = (value: number | string | null | undefined) =>
  value === null || value === undefined ? '' : String(value);

/** Campos que a cotação já tem: vêm preenchidos e só de leitura (S4-08 §2). */
export function lockedFields(request: QuoteRequestResponse) {
  const v = request.vehicle;
  const d = request.driver;
  return {
    vehicle: {
      category: true,
      usage: true,
      year: true,
      provinceOfCirculation: true,
      hasTracker: true,
      commercialValue: true,
      plate: !!v.plate,
      engineCc: v.engineCc !== null && v.engineCc !== undefined,
      seats: v.seats !== null && v.seats !== undefined,
      grossWeightKg: v.grossWeightKg !== null && v.grossWeightKg !== undefined,
    },
    driver: {
      birthDate: !!d.birthDate,
      licenceIssueDate: !!d.licenceIssueDate,
      licenceCategories: d.licenceCategories.length > 0,
    },
  };
}

function defaultsFrom(request: QuoteRequestResponse): FormValues {
  const v = request.vehicle;
  const d = request.driver;
  const vehicle: VehicleFormValues = {
    ...emptyVehicleValues,
    plate: v.plate ?? '',
    make: v.make ?? '',
    model: v.model ?? '',
    year: String(v.year),
    category: v.category,
    engineCc: text(v.engineCc),
    seats: isTrailer(v.category) ? '0' : text(v.seats),
    grossWeightKg: text(v.grossWeightKg),
    usage: v.usage,
    provinceOfCirculation: v.provinceOfCirculation,
    commercialValue: v.commercialValue ?? null,
    hasTracker: v.hasTracker,
  };
  return {
    vehicle,
    driver: {
      isOwner: false,
      fullName: '',
      birthDate: d.birthDate ?? '',
      licenceNumber: '',
      licenceCategories: d.licenceCategories,
      licenceIssueDate: d.licenceIssueDate ?? '',
    },
  };
}

/** Dados do veículo e do condutor para a proposta do mediador (S4-06 §5). */
export function BrokerVehicleDriverForm({
  formId,
  request,
  serverErrors,
  onSubmit,
}: BrokerVehicleDriverFormProps) {
  const { t, i18n } = useTranslation();
  const locked = lockedFields(request);
  const categories = useReferenceValues(
    'LICENCE_CATEGORY',
    undefined,
    !locked.driver.licenceCategories,
  );
  const referenceLabel = useReferenceLabel();
  const {
    control,
    register,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(formSchema()),
    defaultValues: defaultsFrom(request),
  });

  useEffect(() => {
    for (const error of serverErrors ?? []) {
      const [section, field] = error.field.split(/[.[]/);
      if ((section === 'vehicle' || section === 'driver') && field) {
        setError(`${section}.${field}` as never, { type: 'server', message: error.code });
      }
    }
  }, [serverErrors, setError]);

  const errorText = (error: FormFieldError | undefined) => {
    if (!error?.message) return undefined;
    if (error.message === 'VEHICLE_ALREADY_REGISTERED') {
      return t('brokerProposals.form.vehicleAlreadyRegistered');
    }
    return codeMessage(t, error.message);
  };

  const submit = handleSubmit(async (values) => {
    const vehicle = toVehicleRequest(values.vehicle) as BrokerProposalVehicleRequest;
    await onSubmit({
      vehicle,
      driver: {
        fullName: normalizeName(values.driver.fullName),
        birthDate: values.driver.birthDate,
        licenceNumber: normalizeLicenceNumber(values.driver.licenceNumber),
        licenceCategories: values.driver.licenceCategories,
        licenceIssueDate: values.driver.licenceIssueDate,
        isOwner: values.driver.isOwner,
      },
    });
  });

  const v = request.vehicle;
  const readOnly = (label: string, value: ReactNode) => (
    <TextField
      label={label}
      value={value === '' || value === null || value === undefined ? '—' : value}
      fullWidth
      slotProps={{ input: { readOnly: true }, inputLabel: { shrink: true } }}
    />
  );
  const grid = { display: 'grid', gridTemplateColumns: { xs: '1fr', md: '1fr 1fr' }, gap: 2.5 };
  const numeric = { htmlInput: { inputMode: 'numeric' as const } };
  const ve = errors.vehicle;
  const de = errors.driver;

  return (
    <Stack component="form" id={formId} spacing={3} onSubmit={submit} noValidate>
      <Alert severity="info">{t('brokerProposals.form.lockedNote')}</Alert>

      <Stack spacing={2}>
        <Typography variant="h6" component="h2">
          {t('brokerProposals.form.vehicle')}
        </Typography>
        <Box sx={grid}>
          {locked.vehicle.plate ? (
            readOnly(t('vehicles.fields.plate'), v.plate)
          ) : (
            <TextField
              label={t('vehicles.fields.plate')}
              error={!!ve?.plate}
              helperText={errorText(ve?.plate)}
              fullWidth
              {...register('vehicle.plate')}
            />
          )}
          <TextField
            label={t('vehicles.fields.chassisNumber')}
            error={!!ve?.chassisNumber}
            helperText={errorText(ve?.chassisNumber)}
            fullWidth
            autoComplete="off"
            {...register('vehicle.chassisNumber')}
          />
          <TextField
            label={t('vehicles.fields.make')}
            error={!!ve?.make}
            helperText={errorText(ve?.make)}
            fullWidth
            slotProps={{ inputLabel: { shrink: true }, htmlInput: { maxLength: 60 } }}
            {...register('vehicle.make')}
          />
          <TextField
            label={t('vehicles.fields.model')}
            error={!!ve?.model}
            helperText={errorText(ve?.model)}
            fullWidth
            slotProps={{ inputLabel: { shrink: true }, htmlInput: { maxLength: 60 } }}
            {...register('vehicle.model')}
          />
          {readOnly(t('vehicles.fields.year'), v.year)}
          {readOnly(t('vehicles.fields.category'), t(`vehicleCategory.${v.category}`))}
          {!isTrailer(v.category) &&
            (locked.vehicle.engineCc ? (
              readOnly(t('vehicles.fields.engineCc'), v.engineCc)
            ) : (
              <TextField
                label={t('vehicles.fields.engineCc')}
                error={!!ve?.engineCc}
                helperText={errorText(ve?.engineCc)}
                fullWidth
                slotProps={numeric}
                {...register('vehicle.engineCc')}
              />
            ))}
          {locked.vehicle.seats || isTrailer(v.category) ? (
            readOnly(t('vehicles.fields.seats'), isTrailer(v.category) ? 0 : v.seats)
          ) : (
            <TextField
              label={t('vehicles.fields.seats')}
              error={!!ve?.seats}
              helperText={errorText(ve?.seats)}
              fullWidth
              slotProps={numeric}
              {...register('vehicle.seats')}
            />
          )}
          {locked.vehicle.grossWeightKg ? (
            readOnly(t('vehicles.fields.grossWeightKg'), v.grossWeightKg)
          ) : (
            <TextField
              label={t('vehicles.fields.grossWeightKg')}
              error={!!ve?.grossWeightKg}
              helperText={
                errorText(ve?.grossWeightKg) ??
                (requiresWeight(v.category) ? undefined : t('vehicles.form.optional'))
              }
              fullWidth
              slotProps={numeric}
              {...register('vehicle.grossWeightKg')}
            />
          )}
          {readOnly(t('vehicles.fields.usage'), t(`vehicleUsage.${v.usage}`))}
          {readOnly(
            t('vehicles.fields.provinceOfCirculation'),
            t(`province.${v.provinceOfCirculation}`),
          )}
          {readOnly(
            t('vehicles.fields.commercialValue'),
            v.commercialValue ? formatMoney(v.commercialValue, i18n.language) : '',
          )}
          {readOnly(
            t('vehicles.fields.hasTracker'),
            v.hasTracker ? t('common.yes') : t('common.no'),
          )}
        </Box>
      </Stack>

      <Stack spacing={2}>
        <Typography variant="h6" component="h2">
          {t('brokerProposals.form.driver')}
        </Typography>
        <Controller
          control={control}
          name="driver.isOwner"
          render={({ field }) => (
            <FormControlLabel
              control={
                <Switch
                  checked={field.value}
                  name={field.name}
                  onChange={(event) => field.onChange(event.target.checked)}
                />
              }
              label={t('brokerProposals.form.isOwner')}
            />
          )}
        />
        <Box sx={grid}>
          <TextField
            label={t('drivers.fields.fullName')}
            error={!!de?.fullName}
            helperText={errorText(de?.fullName)}
            fullWidth
            autoComplete="off"
            {...register('driver.fullName')}
          />
          {locked.driver.birthDate ? (
            readOnly(t('drivers.fields.birthDate'), request.driver.birthDate ?? '')
          ) : (
            <TextField
              label={t('drivers.fields.birthDate')}
              type="date"
              error={!!de?.birthDate}
              helperText={errorText(de?.birthDate)}
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
              {...register('driver.birthDate')}
            />
          )}
          <TextField
            label={t('drivers.fields.licenceNumber')}
            error={!!de?.licenceNumber}
            helperText={errorText(de?.licenceNumber)}
            fullWidth
            autoComplete="off"
            slotProps={{ htmlInput: { maxLength: 30 } }}
            {...register('driver.licenceNumber')}
          />
          {locked.driver.licenceIssueDate ? (
            readOnly(t('drivers.fields.licenceIssueDate'), request.driver.licenceIssueDate ?? '')
          ) : (
            <TextField
              label={t('drivers.fields.licenceIssueDate')}
              type="date"
              error={!!de?.licenceIssueDate}
              helperText={errorText(de?.licenceIssueDate)}
              fullWidth
              slotProps={{ inputLabel: { shrink: true } }}
              {...register('driver.licenceIssueDate')}
            />
          )}
        </Box>
        {locked.driver.licenceCategories ? (
          readOnly(
            t('drivers.fields.licenceCategories'),
            request.driver.licenceCategories.join(', '),
          )
        ) : (
          <Controller
            control={control}
            name="driver.licenceCategories"
            render={({ field }) => (
              <FormControl component="fieldset" error={!!de?.licenceCategories}>
                <FormLabel component="legend">{t('drivers.fields.licenceCategories')}</FormLabel>
                {categories.isPending ? (
                  <CircularProgress size={24} aria-label={t('auth.loading')} />
                ) : (
                  <FormGroup row>
                    {(categories.data ?? []).map((category) => (
                      <FormControlLabel
                        key={category.code}
                        label={referenceLabel(category)}
                        control={
                          <Checkbox
                            checked={field.value.includes(category.code)}
                            onChange={(event) =>
                              field.onChange(
                                event.target.checked
                                  ? [...field.value, category.code]
                                  : field.value.filter((code) => code !== category.code),
                              )
                            }
                          />
                        }
                      />
                    ))}
                  </FormGroup>
                )}
                {de?.licenceCategories && (
                  <FormHelperText>
                    {errorText(de.licenceCategories as FormFieldError)}
                  </FormHelperText>
                )}
              </FormControl>
            )}
          />
        )}
      </Stack>
    </Stack>
  );
}
