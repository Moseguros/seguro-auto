import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useTranslation } from 'react-i18next';
import type { SecretStatusResponse } from '../../api/generated';
import { fieldMessage } from '../../errors/problem';
import { formatStamp } from '../insurer-payments/format';
import { schemaFieldId } from './SchemaForm';
import { isMultilineSecret } from './methodRules';

export interface SecretFieldsProps {
  method: string;
  /** Campos a mostrar, pela ordem (só os que se aplicam com os dados actuais). */
  fields: string[];
  /** Estado de cada segredo no servidor (nunca o valor). */
  status: Record<string, SecretStatusResponse | undefined>;
  /** Obrigatório para activar com os dados actuais. */
  required(field: string): boolean;
  /** O que foi escrito (vazio = mantém o segredo actual). */
  values: Record<string, string>;
  onChange(field: string, value: string): void;
  /** Código do erro por campo (sem o prefixo `secrets.`). */
  errors?: Record<string, string>;
  disabled?: boolean;
}

/** Só os segredos escritos (os vazios mantêm o valor actual); `undefined` sem nenhum. */
export function typedSecrets(
  fields: string[],
  values: Record<string, string>,
): Record<string, string> | undefined {
  const entries = fields
    .map((field) => [field, values[field] ?? ''] as const)
    .filter(([, value]) => value.trim() !== '');
  return entries.length ? Object.fromEntries(entries) : undefined;
}

/**
 * Um campo por segredo do método (S5-12 §2). O valor nunca vem do servidor nem se mostra: o campo
 * começa vazio e diz só se o segredo está configurado; o que se escreve substitui-o ao gravar.
 */
export function SecretFields({
  method,
  fields,
  status,
  required,
  values,
  onChange,
  errors = {},
  disabled,
}: SecretFieldsProps) {
  const { t } = useTranslation();
  return (
    <Stack spacing={2}>
      {fields.map((field) => {
        const state = status[field];
        const error = errors[field];
        const help = state?.configured
          ? state.updatedAt
            ? t('paymentSettingsPage.secret.configured', {
                date: formatStamp(state.updatedAt),
              })
            : t('paymentSettingsPage.secret.configuredNoDate')
          : t('paymentSettingsPage.secret.notConfigured');
        const multiline = isMultilineSecret(field);
        const label = t(`paymentSettings.${method}.${field}`);
        return (
          <Stack key={field} spacing={0.5}>
            {required(field) && (
              <Chip
                size="small"
                variant="outlined"
                label={t('paymentSettingsPage.secret.required')}
                sx={{ alignSelf: 'flex-start' }}
              />
            )}
            <TextField
              id={schemaFieldId(method, `secret.${field}`)}
              label={label}
              type={multiline ? undefined : 'password'}
              autoComplete="new-password"
              multiline={multiline}
              minRows={multiline ? 3 : undefined}
              value={values[field] ?? ''}
              onChange={(event) => onChange(field, event.target.value)}
              error={!!error}
              helperText={error ? fieldMessage(t, error) : help}
              disabled={disabled}
              fullWidth
              slotProps={{
                htmlInput: {
                  spellCheck: false,
                  'data-secret': field,
                  ...(multiline ? { style: { fontFamily: 'monospace' } } : {}),
                },
              }}
            />
          </Stack>
        );
      })}
    </Stack>
  );
}
