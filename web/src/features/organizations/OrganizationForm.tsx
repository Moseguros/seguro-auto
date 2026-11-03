import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Grid from '@mui/material/Grid2';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import type { TFunction } from 'i18next';
import { useEffect, useId, useState, type ReactNode } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { listOrganizations } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { provinces } from '../customer-profile/profileSchema';
import {
  codeFields,
  emptyOrganization,
  organizationFieldOf,
  organizationSchema,
  organizationTypes,
  requiresLicense,
  type OrganizationField,
  type OrganizationFormValues,
} from './organizationSchema';

/** Mensagem de um erro de campo: `validation.<code>`, `errors.<code>` ou genérica. */
export function fieldText(t: TFunction, code: string): string {
  if (t(`validation.${code}`, { defaultValue: '' })) return t(`validation.${code}`);
  if (t(`errors.${code}`, { defaultValue: '' })) return t(`errors.${code}`);
  return t('validation.TYPE_MISMATCH');
}

export interface OrganizationFormProps {
  mode: 'create' | 'edit';
  initial?: OrganizationFormValues;
  submitLabel: string;
  /** Sem permissão para alterar: tudo só de leitura e sem botão. */
  readOnly?: boolean;
  /** Grava; um erro da API lançado aqui aparece nos campos ou por cima do botão. */
  onSubmit(values: OrganizationFormValues): Promise<void>;
}

/** Dados da organização, perfil da seguradora (só Seguradora) e, na criação, o primeiro gestor. */
export function OrganizationForm({
  mode,
  initial = emptyOrganization,
  submitLabel,
  readOnly = false,
  onSubmit,
}: OrganizationFormProps) {
  const { t } = useTranslation();
  const [message, setMessage] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    reset,
    setError,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<OrganizationFormValues>({
    resolver: zodResolver(organizationSchema({ withManager: mode === 'create' })),
    defaultValues: initial,
  });

  useEffect(() => {
    reset(initial);
  }, [initial, reset]);

  const type = watch('type');
  const isInsurer = type === 'INSURER';
  const ombudsmen = useQuery({
    queryKey: ['organizations', 'ombudsmen'],
    queryFn: ({ signal }) =>
      listOrganizations({ type: 'OMBUDSMAN', status: 'ACTIVE', size: 100 }, undefined, signal),
    enabled: isInsurer && !readOnly,
    staleTime: 60_000,
  });

  const submit = handleSubmit(async (values) => {
    setMessage(null);
    try {
      await onSubmit(values);
    } catch (error) {
      const problem = getProblem(error);
      let applied = false;
      for (const fieldError of problem.errors) {
        const field = organizationFieldOf(fieldError.field);
        if (field) {
          setError(field, { type: 'server', message: fieldError.code });
          applied = true;
        }
      }
      const codeField = codeFields[problem.code];
      if (!applied && codeField) {
        setError(codeField, { type: 'server', message: problem.code });
        applied = true;
      }
      if (!applied) setMessage(problemMessage(t, problem));
    }
  });

  const field = (
    name: OrganizationField,
    options: {
      type?: string;
      autoComplete?: string;
      helper?: string;
      required?: boolean;
      disabled?: boolean;
    } = {},
  ) => {
    const code = errors[name]?.message;
    return (
      <TextField
        label={t(`organizations.fields.${fieldLabel(name)}`)}
        type={options.type}
        autoComplete={options.autoComplete ?? 'off'}
        required={options.required}
        error={!!code}
        helperText={code ? fieldText(t, code) : options.helper}
        disabled={readOnly || options.disabled}
        fullWidth
        {...register(name)}
      />
    );
  };

  const select = (
    name: OrganizationField,
    choices: { value: string; label: string }[],
    options: { required?: boolean; disabled?: boolean; empty?: string; helper?: string } = {},
  ) => {
    const code = errors[name]?.message;
    return (
      <TextField
        select
        label={t(`organizations.fields.${fieldLabel(name)}`)}
        required={options.required}
        error={!!code}
        helperText={code ? fieldText(t, code) : options.helper}
        disabled={readOnly || options.disabled}
        fullWidth
        slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
        {...register(name)}
      >
        <option value="">{options.empty ?? t('organizations.choose')}</option>
        {choices.map((choice) => (
          <option key={choice.value} value={choice.value}>
            {choice.label}
          </option>
        ))}
      </TextField>
    );
  };

  const half = { xs: 12, sm: 6 } as const;

  return (
    <Stack component="form" spacing={3} onSubmit={submit} noValidate>
      <Section title={t('organizations.sections.data')}>
        <Grid size={half}>
          {select(
            'type',
            organizationTypes.map((value) => ({ value, label: t(`orgType.${value}`) })),
            { required: true, disabled: mode === 'edit' },
          )}
        </Grid>
        <Grid size={half}>
          {field('nuit', { required: true, helper: t('profile.nuitHelper') })}
        </Grid>
        <Grid size={half}>{field('legalName', { required: true })}</Grid>
        <Grid size={half}>{field('tradeName', { helper: t('organizations.optional') })}</Grid>
        <Grid size={half}>
          {field('licenseNumber', {
            required: requiresLicense(type),
            helper: requiresLicense(type) ? undefined : t('organizations.optional'),
          })}
        </Grid>
        <Grid size={half}>
          {field('contactEmail', { type: 'email', autoComplete: 'email', required: true })}
        </Grid>
        <Grid size={half}>
          {field('contactPhone', {
            type: 'tel',
            autoComplete: 'tel',
            helper: t('organizations.optional'),
          })}
        </Grid>
        <Grid size={half}>
          {select(
            'province',
            provinces.map((value) => ({ value, label: t(`province.${value}`) })),
            { helper: t('organizations.optional') },
          )}
        </Grid>
        <Grid size={12}>
          {field('address', {
            autoComplete: 'street-address',
            helper: t('organizations.optional'),
          })}
        </Grid>
      </Section>

      {isInsurer && (
        <Section title={t('organizations.sections.insurer')} testId="insurer-profile">
          <Grid size={half}>
            {field('shortCode', { required: true, helper: t('organizations.shortCodeHelper') })}
          </Grid>
          <Grid size={half}>{field('complaintsEmail', { type: 'email', required: true })}</Grid>
          <Grid size={12}>
            {select(
              'ombudsmanOrganizationId',
              (ombudsmen.data?.content ?? []).map((o) => ({ value: o.id, label: o.displayName })),
              { empty: t('organizations.none'), helper: t('organizations.optional') },
            )}
          </Grid>
        </Section>
      )}

      {mode === 'create' && (
        <Section title={t('organizations.sections.manager')}>
          <Grid size={half}>{field('managerName', { autoComplete: 'off', required: true })}</Grid>
          <Grid size={half}>{field('managerEmail', { type: 'email', required: true })}</Grid>
        </Section>
      )}

      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}
      {!readOnly && (
        <Button
          type="submit"
          variant="contained"
          size="large"
          disabled={isSubmitting}
          sx={{ alignSelf: { sm: 'flex-start' } }}
        >
          {submitLabel}
        </Button>
      )}
    </Stack>
  );
}

function fieldLabel(name: OrganizationField): string {
  return name === 'ombudsmanOrganizationId' ? 'ombudsman' : name;
}

function Section({
  title,
  testId,
  children,
}: {
  title: string;
  testId?: string;
  children: ReactNode;
}) {
  const id = useId();
  return (
    <Card component="section" aria-labelledby={id} data-testid={testId}>
      <CardContent>
        <Typography id={id} variant="subtitle1" component="h2" sx={{ mb: 2 }}>
          {title}
        </Typography>
        <Grid container spacing={2}>
          {children}
        </Grid>
      </CardContent>
    </Card>
  );
}
