import { zodResolver } from '@hookform/resolvers/zod';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useEffect, useRef, useState } from 'react';
import { useForm, type FieldError as FormFieldError } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import type {
  BrokerPolicyholderRequest,
  FieldError,
  ProposalPolicyholder,
} from '../../api/generated';
import { fieldMessage } from '../../errors/problem';
import {
  idDocTypes,
  normalizeDocumentNumber,
  normalizeName,
  normalizeNuit,
  profileFields,
  profileSchema,
  provinces,
  type ProfileFormValues,
} from '../customer-profile/profileSchema';

export interface BrokerPolicyholderFormProps {
  /** `id` do `<form>`: o botão que o submete fica na barra do passo (`form={formId}`). */
  formId: string;
  /** Tomador actual (mascarado) quando se altera; sem ele, todos os campos são obrigatórios. */
  current?: ProposalPolicyholder;
  /** Nome e data de nascimento sugeridos ("O condutor é o tomador"). */
  suggested?: { fullName?: string; birthDate?: string };
  /** Erros de campo do servidor (`policyholder.<campo>` ou `<campo>`). */
  serverErrors?: FieldError[];
  /** `dirty`: algum campo mudou desde o início. */
  onSubmit(policyholder: BrokerPolicyholderRequest, dirty: boolean): void | Promise<void>;
}

function defaultsFrom(current?: ProposalPolicyholder): ProfileFormValues {
  return {
    fullName: current?.fullName ?? '',
    birthDate: current?.birthDate ?? '',
    idDocType: current?.idDocType ?? '',
    idDocNumber: '',
    nuit: '',
    address: current?.address ?? '',
    province: current?.province ?? '',
  };
}

/**
 * Dados do tomador preenchidos pelo mediador quando o perfil do cliente está incompleto (S4-06),
 * com os campos e as regras do perfil do cliente (S1-06 / S1-07 §4).
 */
export function BrokerPolicyholderForm({
  formId,
  current,
  suggested,
  serverErrors,
  onSubmit,
}: BrokerPolicyholderFormProps) {
  const { t } = useTranslation();
  const [editingDocument, setEditingDocument] = useState(!current);
  const [editingNuit, setEditingNuit] = useState(!current);
  const editingRef = useRef({ document: editingDocument, nuit: editingNuit });
  editingRef.current = { document: editingDocument, nuit: editingNuit };

  const {
    register,
    handleSubmit,
    setError,
    setValue,
    getValues,
    watch,
    formState: { errors, isDirty },
  } = useForm<ProfileFormValues>({
    resolver: (values, context, options) => {
      const typeChanged = !!current && values.idDocType !== current.idDocType;
      return zodResolver(
        profileSchema({
          documentRequired: editingRef.current.document || typeChanged,
          nuitRequired: editingRef.current.nuit,
        }),
      )(values, context, options);
    },
    defaultValues: defaultsFrom(current),
  });

  const typeChanged = !!current && watch('idDocType') !== current.idDocType;
  const showDocument = editingDocument || typeChanged;

  useEffect(() => {
    if (!suggested) return;
    if (suggested.fullName && !getValues('fullName')) {
      setValue('fullName', suggested.fullName, { shouldDirty: true });
    }
    if (suggested.birthDate && !getValues('birthDate')) {
      setValue('birthDate', suggested.birthDate, { shouldDirty: true });
    }
  }, [suggested?.fullName, suggested?.birthDate, suggested, getValues, setValue]);

  useEffect(() => {
    for (const error of serverErrors ?? []) {
      const field = error.field.replace(/^policyholder\./, '');
      if ((profileFields as readonly string[]).includes(field)) {
        setError(field as keyof ProfileFormValues, { type: 'server', message: error.code });
      }
    }
  }, [serverErrors, setError]);

  const errorText = (error: FormFieldError | undefined) =>
    error?.message ? fieldMessage(t, error.message) : undefined;

  const submit = handleSubmit(async (values) => {
    const document = normalizeDocumentNumber(values.idDocNumber);
    const nuit = normalizeNuit(values.nuit);
    const address = values.address.trim();
    await onSubmit(
      {
        fullName: normalizeName(values.fullName),
        birthDate: values.birthDate,
        idDocType: values.idDocType as BrokerPolicyholderRequest['idDocType'],
        ...(showDocument && document ? { idDocNumber: document } : {}),
        ...(editingNuit && nuit ? { nuit } : {}),
        ...(address ? { address } : {}),
        province: values.province as BrokerPolicyholderRequest['province'],
      },
      isDirty || (!!current && (editingDocument || editingNuit)),
    );
  });

  const masked = (label: string, value: string, changeLabel: string, onChange: () => void) => (
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

  return (
    <Stack component="form" id={formId} spacing={2.5} onSubmit={submit} noValidate>
      <TextField
        label={t('profile.fields.fullName')}
        autoComplete="off"
        error={!!errors.fullName}
        helperText={errorText(errors.fullName)}
        fullWidth
        slotProps={{ inputLabel: { shrink: true } }}
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
      {showDocument ? (
        <TextField
          label={t('profile.fields.idDocNumber')}
          autoComplete="off"
          error={!!errors.idDocNumber}
          helperText={errorText(errors.idDocNumber)}
          fullWidth
          {...register('idDocNumber')}
        />
      ) : (
        masked(
          t('profile.fields.idDocNumber'),
          current?.idDocNumberMasked ?? '',
          t('profile.changeDocument'),
          () => setEditingDocument(true),
        )
      )}
      {editingNuit ? (
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
        masked(t('profile.fields.nuit'), current?.nuitMasked ?? '', t('profile.changeNuit'), () =>
          setEditingNuit(true),
        )
      )}
      <TextField
        label={t('profile.fields.address')}
        autoComplete="off"
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
    </Stack>
  );
}
