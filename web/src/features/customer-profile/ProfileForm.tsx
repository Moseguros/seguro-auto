import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useEffect, useState } from 'react';
import { useForm, type FieldError as FormFieldError } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import {
  updateMyProfile,
  type CustomerProfileResponse,
  type UpdateMyProfileRequest,
} from '../../api/generated';
import { applyFieldErrors, fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import {
  idDocTypes,
  normalizeDocumentNumber,
  normalizeName,
  normalizeNuit,
  profileFields,
  profileSchema,
  provinces,
  type ProfileFormValues,
} from './profileSchema';

export interface ProfileFormProps {
  profile: CustomerProfileResponse;
  onSaved(profile: CustomerProfileResponse): void | Promise<void>;
  /** 409 `CONCURRENT_MODIFICATION`: o pai recarrega o perfil. */
  onConflict(): void;
}

/** O documento e o NUIT vão `null` quando não foram alterados (o backend mantém os actuais). */
type ProfilePayload = Omit<UpdateMyProfileRequest, 'idDocNumber' | 'nuit' | 'address'> & {
  idDocNumber: string | null;
  nuit: string | null;
  address: string | null;
};

function defaultsFrom(profile: CustomerProfileResponse): ProfileFormValues {
  return {
    fullName: profile.fullName ?? '',
    birthDate: profile.birthDate ?? '',
    idDocType: profile.idDocType ?? '',
    idDocNumber: '',
    nuit: '',
    address: profile.address ?? '',
    province: profile.province ?? '',
  };
}

export function ProfileForm({ profile, onSaved, onConflict }: ProfileFormProps) {
  const { t } = useTranslation();
  const [editingDocument, setEditingDocument] = useState(false);
  const [editingNuit, setEditingNuit] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const hasDocument = Boolean(profile.idDocNumberMasked);
  const hasNuit = Boolean(profile.nuitMasked);

  const {
    register,
    handleSubmit,
    reset,
    setError,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<ProfileFormValues>({
    // O esquema depende do tipo de documento escolhido: lê-se no momento da validação.
    resolver: (values, context, options) => {
      const typeChanged =
        hasDocument && !!profile.idDocType && values.idDocType !== profile.idDocType;
      const schema = profileSchema({
        documentRequired: !hasDocument || typeChanged,
        nuitRequired: !hasNuit,
      });
      return zodResolver(schema)(values, context, options);
    },
    defaultValues: defaultsFrom(profile),
  });

  const documentTypeChanged =
    hasDocument && !!profile.idDocType && watch('idDocType') !== profile.idDocType;

  // Perfil recarregado (gravado ou alterado noutro lado): volta aos valores do servidor.
  useEffect(() => {
    reset(defaultsFrom(profile));
    setEditingDocument(false);
    setEditingNuit(false);
  }, [profile, reset]);

  const showDocumentField = !hasDocument || editingDocument || documentTypeChanged;
  const showNuitField = !hasNuit || editingNuit;

  const errorText = (error: FormFieldError | undefined) =>
    error?.message ? fieldMessage(t, error.message) : undefined;

  const submit = handleSubmit(async (values) => {
    setMessage(null);
    const document = normalizeDocumentNumber(values.idDocNumber);
    const nuit = normalizeNuit(values.nuit);
    const payload: ProfilePayload = {
      fullName: normalizeName(values.fullName),
      birthDate: values.birthDate,
      idDocType: values.idDocType as UpdateMyProfileRequest['idDocType'],
      idDocNumber: showDocumentField && document ? document : null,
      nuit: showNuitField && nuit ? nuit : null,
      address: values.address.trim() || null,
      province: values.province as UpdateMyProfileRequest['province'],
      version: profile.version,
    };
    try {
      const saved = await updateMyProfile(payload as UpdateMyProfileRequest);
      await onSaved(saved);
    } catch (error) {
      const problem = getProblem(error);
      if (
        problem.code === 'VALIDATION_ERROR' &&
        applyFieldErrors(problem, profileFields, setError)
      ) {
        return;
      }
      setMessage(problemMessage(t, problem));
      if (problem.code === 'CONCURRENT_MODIFICATION') onConflict();
    }
  });

  return (
    <Stack component="form" spacing={2.5} onSubmit={submit} noValidate>
      <TextField
        label={t('profile.fields.fullName')}
        autoComplete="name"
        error={!!errors.fullName}
        helperText={errorText(errors.fullName)}
        fullWidth
        {...register('fullName')}
      />
      <TextField
        label={t('profile.fields.birthDate')}
        type="date"
        error={!!errors.birthDate}
        helperText={errorText(errors.birthDate)}
        fullWidth
        slotProps={{ inputLabel: { shrink: true } }}
        {...register('birthDate')}
      />
      <TextField
        select
        label={t('profile.fields.idDocType')}
        error={!!errors.idDocType}
        helperText={errorText(errors.idDocType)}
        fullWidth
        slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
        {...register('idDocType')}
      >
        <option value="">{t('profile.choose')}</option>
        {idDocTypes.map((type) => (
          <option key={type} value={type}>
            {t(`idDocType.${type}`)}
          </option>
        ))}
      </TextField>

      {showDocumentField ? (
        <TextField
          label={t('profile.fields.idDocNumber')}
          autoComplete="off"
          error={!!errors.idDocNumber}
          helperText={errorText(errors.idDocNumber)}
          fullWidth
          {...register('idDocNumber')}
        />
      ) : (
        <MaskedValue
          label={t('profile.fields.idDocNumber')}
          value={profile.idDocNumberMasked ?? ''}
          changeLabel={t('profile.changeDocument')}
          onChange={() => setEditingDocument(true)}
        />
      )}

      {showNuitField ? (
        <TextField
          label={t('profile.fields.nuit')}
          autoComplete="off"
          error={!!errors.nuit}
          helperText={errorText(errors.nuit) ?? t('profile.nuitHelper')}
          fullWidth
          slotProps={{ htmlInput: { inputMode: 'numeric', maxLength: 11 } }}
          {...register('nuit')}
        />
      ) : (
        <MaskedValue
          label={t('profile.fields.nuit')}
          value={profile.nuitMasked ?? ''}
          changeLabel={t('profile.changeNuit')}
          onChange={() => setEditingNuit(true)}
        />
      )}

      <TextField
        label={t('profile.fields.address')}
        autoComplete="street-address"
        error={!!errors.address}
        helperText={errorText(errors.address) ?? t('profile.optional')}
        fullWidth
        {...register('address')}
      />
      <TextField
        select
        label={t('profile.fields.province')}
        error={!!errors.province}
        helperText={errorText(errors.province)}
        fullWidth
        slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
        {...register('province')}
      >
        <option value="">{t('profile.choose')}</option>
        {provinces.map((province) => (
          <option key={province} value={province}>
            {t(`province.${province}`)}
          </option>
        ))}
      </TextField>

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      <Button type="submit" variant="contained" size="large" disabled={isSubmitting}>
        {t('profile.save')}
      </Button>
    </Stack>
  );
}

function MaskedValue({
  label,
  value,
  changeLabel,
  onChange,
}: {
  label: string;
  value: string;
  changeLabel: string;
  onChange(): void;
}) {
  const { t } = useTranslation();
  return (
    <TextField
      label={label}
      value={value}
      fullWidth
      slotProps={{
        input: {
          readOnly: true,
          endAdornment: (
            <Button size="small" onClick={onChange} aria-label={changeLabel}>
              {t('profile.change')}
            </Button>
          ),
        },
      }}
    />
  );
}
