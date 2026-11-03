import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
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
import { useQueryClient } from '@tanstack/react-query';
import { useRef, useState } from 'react';
import { Controller, useForm, type FieldError as FormFieldError } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import {
  createMyDriver,
  getGetMyProfileQueryKey,
  getMyProfile,
  updateMyDriver,
  type DriverResponse,
  type UpdateDriverRequest,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { todayInMaputo } from '../../format/dates';
import { normalizeName } from '../customer-profile/profileSchema';
import { useReferenceLabel, useReferenceValues } from '../vehicles/vehicleQueries';
import { codeMessage } from '../vehicles/vehicleSchema';
import {
  driverFieldOf,
  driverSchema,
  normalizeLicenceNumber,
  type DriverFormValues,
} from './driverSchema';

export interface DriverFormProps {
  /** Condutor a alterar; sem ele, regista um novo. */
  driver?: DriverResponse;
  /** Já existe outro condutor "Eu": o interruptor "Sou eu" fica escondido. */
  ownerTaken: boolean;
  onSaved(driver: DriverResponse): void | Promise<void>;
  /** "Recarregar" depois de 409 `CONCURRENT_MODIFICATION`. */
  onReload(): void;
}

/** No `PUT`, `licenceNumber` vai `null` quando a carta não foi alterada (o backend mantém-na). */
type UpdatePayload = Omit<UpdateDriverRequest, 'licenceNumber'> & { licenceNumber: string | null };

type TopMessage = { severity: 'warning' | 'error'; message: string; conflict?: boolean };

function defaultsFrom(driver: DriverResponse | undefined): DriverFormValues {
  return {
    isOwner: driver?.isOwner ?? false,
    fullName: driver?.fullName ?? '',
    birthDate: driver?.birthDate ?? '',
    licenceNumber: '',
    licenceCategories: driver?.licenceCategories ?? [],
    licenceIssueDate: driver?.licenceIssueDate ?? '',
  };
}

export function DriverForm({ driver, ownerTaken, onSaved, onReload }: DriverFormProps) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const categories = useReferenceValues('LICENCE_CATEGORY');
  const label = useReferenceLabel();
  const [editingLicence, setEditingLicenceState] = useState(!driver);
  // O esquema lê-o no momento da validação.
  const editingLicenceRef = useRef(editingLicence);
  const setEditingLicence = (value: boolean) => {
    editingLicenceRef.current = value;
    setEditingLicenceState(value);
  };
  const [top, setTop] = useState<TopMessage | null>(null);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const today = todayInMaputo();

  const {
    control,
    register,
    handleSubmit,
    setError,
    setValue,
    formState: { errors, isSubmitting },
  } = useForm<DriverFormValues>({
    resolver: (values, context, options) =>
      zodResolver(driverSchema({ licenceRequired: editingLicenceRef.current }))(
        values,
        context,
        options,
      ),
    defaultValues: defaultsFrom(driver),
  });

  const errorText = (error: FormFieldError | undefined) =>
    error?.message ? codeMessage(t, error.message) : undefined;

  /** "Sou eu": preenche o nome e a data de nascimento com o perfil (continuam editáveis). */
  const fillFromProfile = async () => {
    setLoadingProfile(true);
    try {
      const profile = await queryClient.fetchQuery({
        queryKey: getGetMyProfileQueryKey(),
        queryFn: ({ signal }) => getMyProfile(undefined, signal),
      });
      if (profile.fullName) setValue('fullName', profile.fullName, { shouldDirty: true });
      if (profile.birthDate) setValue('birthDate', profile.birthDate, { shouldDirty: true });
    } catch {
      // Sem perfil o cliente preenche à mão.
    } finally {
      setLoadingProfile(false);
    }
  };

  const submit = handleSubmit(async (values) => {
    setTop(null);
    const licence = normalizeLicenceNumber(values.licenceNumber);
    const base = {
      fullName: normalizeName(values.fullName),
      birthDate: values.birthDate,
      licenceCategories: values.licenceCategories,
      licenceIssueDate: values.licenceIssueDate,
      isOwner: values.isOwner,
    };
    try {
      let saved: DriverResponse;
      if (driver) {
        const payload: UpdatePayload = {
          ...base,
          licenceNumber: editingLicence && licence ? licence : null,
          version: driver.version,
        };
        saved = await updateMyDriver(driver.id, payload as UpdateDriverRequest);
      } else {
        saved = await createMyDriver({ ...base, licenceNumber: licence });
      }
      await onSaved(saved);
    } catch (error) {
      const problem = getProblem(error);
      if (problem.code === 'VALIDATION_ERROR') {
        let applied = false;
        for (const fieldError of problem.errors) {
          const field = driverFieldOf(fieldError.field);
          if (field && (field !== 'licenceNumber' || editingLicence)) {
            setError(field, { type: 'server', message: fieldError.code });
            applied = true;
          }
        }
        if (applied) return;
      }
      if (problem.code === 'DRIVER_ALREADY_REGISTERED' && editingLicence) {
        setError('licenceNumber', { type: 'server', message: problem.code }, { shouldFocus: true });
        return;
      }
      if (problem.code === 'OWNER_DRIVER_ALREADY_EXISTS') {
        setError('isOwner', { type: 'server', message: problem.code });
        return;
      }
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        setTop({ severity: 'warning', message: t('drivers.form.conflict'), conflict: true });
        return;
      }
      setTop({
        severity: problem.code === 'DRIVER_LIMIT_REACHED' ? 'warning' : 'error',
        message: problemMessage(t, problem),
      });
    }
  });

  const showOwnerSwitch = !ownerTaken;

  return (
    <Stack component="form" spacing={2.5} onSubmit={submit} noValidate>
      {top && (
        <Alert
          severity={top.severity}
          role="alert"
          action={
            top.conflict ? (
              <Button color="inherit" size="small" onClick={onReload}>
                {t('drivers.form.reload')}
              </Button>
            ) : undefined
          }
        >
          {top.message}
        </Alert>
      )}

      {showOwnerSwitch && (
        <Controller
          control={control}
          name="isOwner"
          render={({ field }) => (
            <FormControl error={!!errors.isOwner}>
              <Stack direction="row" alignItems="center" spacing={1}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={field.value}
                      name={field.name}
                      onChange={(event) => {
                        field.onChange(event.target.checked);
                        if (event.target.checked) void fillFromProfile();
                      }}
                    />
                  }
                  label={t('drivers.fields.isOwner')}
                />
                {loadingProfile && <CircularProgress size={20} aria-label={t('auth.loading')} />}
              </Stack>
              <FormHelperText>
                {errorText(errors.isOwner) ?? t('drivers.form.isOwnerHelp')}
              </FormHelperText>
            </FormControl>
          )}
        />
      )}

      <TextField
        label={t('drivers.fields.fullName')}
        autoComplete="off"
        error={!!errors.fullName}
        helperText={errorText(errors.fullName)}
        fullWidth
        slotProps={{ inputLabel: { shrink: true } }}
        {...register('fullName')}
      />
      <TextField
        label={t('drivers.fields.birthDate')}
        type="date"
        error={!!errors.birthDate}
        helperText={errorText(errors.birthDate)}
        fullWidth
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: today } }}
        {...register('birthDate')}
      />

      {editingLicence ? (
        <Controller
          control={control}
          name="licenceNumber"
          render={({ field }) => (
            <TextField
              label={t('drivers.fields.licenceNumber')}
              autoComplete="off"
              error={!!errors.licenceNumber}
              helperText={errorText(errors.licenceNumber)}
              fullWidth
              name={field.name}
              inputRef={field.ref}
              value={field.value}
              onBlur={field.onBlur}
              onChange={(event) => field.onChange(event.target.value.toUpperCase())}
              slotProps={{ htmlInput: { maxLength: 30 } }}
            />
          )}
        />
      ) : (
        <TextField
          label={t('drivers.fields.licenceNumber')}
          value={driver?.licenceNumberMasked ?? ''}
          fullWidth
          slotProps={{
            input: {
              readOnly: true,
              endAdornment: (
                <Button
                  size="small"
                  aria-label={t('drivers.form.changeLicence')}
                  onClick={() => {
                    setValue('licenceNumber', '');
                    setEditingLicence(true);
                  }}
                >
                  {t('drivers.form.change')}
                </Button>
              ),
            },
          }}
        />
      )}

      <Controller
        control={control}
        name="licenceCategories"
        render={({ field }) => (
          <FormControl component="fieldset" error={!!errors.licenceCategories}>
            <FormLabel component="legend">{t('drivers.fields.licenceCategories')}</FormLabel>
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
                        name={field.name}
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
            {errors.licenceCategories && (
              <FormHelperText>
                {errorText(errors.licenceCategories as FormFieldError)}
              </FormHelperText>
            )}
          </FormControl>
        )}
      />

      <TextField
        label={t('drivers.fields.licenceIssueDate')}
        type="date"
        error={!!errors.licenceIssueDate}
        helperText={errorText(errors.licenceIssueDate)}
        fullWidth
        slotProps={{ inputLabel: { shrink: true }, htmlInput: { max: today } }}
        {...register('licenceIssueDate')}
      />

      <Box>
        <Button type="submit" variant="contained" size="large" disabled={isSubmitting}>
          {t('drivers.form.save')}
        </Button>
      </Box>
    </Stack>
  );
}
