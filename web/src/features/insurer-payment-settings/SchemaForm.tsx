import Box from '@mui/material/Box';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';
import {
  getAt,
  schemaFields,
  setAt,
  type JsonSchema,
  type SchemaField,
  type Settings,
} from './schemaPaths';

export interface SchemaFormProps {
  method: string;
  schema: JsonSchema;
  value: Settings;
  onChange(next: Settings): void;
  /** Código do erro por caminho (sem o prefixo `settings.`). */
  errors?: Record<string, string>;
  /** Campos a esconder com os dados actuais (ex.: as pastas sem `moveAfterProcessing`). */
  isHidden?(path: string): boolean;
  disabled?: boolean;
}

/** `id` do campo (para os rótulos e os testes). */
export function schemaFieldId(method: string, path: string): string {
  return `${method}-${path.replace(/\./g, '-')}`;
}

/** Texto escrito num campo inteiro: número se for um inteiro, senão o texto como está. */
function integerValue(text: string): unknown {
  const trimmed = text.trim();
  if (trimmed === '') return '';
  return /^-?\d+$/.test(trimmed) ? Number(trimmed) : text;
}

/**
 * Formulário gerado a partir do JSON Schema do método (S5-12 §2), sem biblioteca externa:
 * `string` → texto (com `pattern` e `maxLength`), `enum` → lista, `const` → só leitura,
 * `integer` → número com `min`/`max`, `boolean` → interruptor, `object` → `fieldset` (um nível).
 * A obrigatoriedade só é verificada pelo backend ao activar.
 */
export function SchemaForm({
  method,
  schema,
  value,
  onChange,
  errors = {},
  isHidden = () => false,
  disabled,
}: SchemaFormProps) {
  const { t, i18n } = useTranslation();
  const optional = (key: string) => (i18n.exists(key) ? t(key) : undefined);
  const label = (path: string) => t(`paymentSettings.${method}.${path}`);
  const help = (path: string) => optional(`paymentSettingsHelp.${method}.${path}`);

  const renderField = (field: SchemaField) => {
    if (isHidden(field.path)) return null;
    const id = schemaFieldId(method, field.path);
    const error = errors[field.path];
    const helperText = error ? fieldMessage(t, error) : help(field.path);
    const current = getAt(value, field.path);
    const set = (next: unknown) => onChange(setAt(value, field.path, next));

    if (field.children) {
      const groupHelp = help(`${field.path}.group`);
      return (
        <Box
          key={field.path}
          component="fieldset"
          sx={{
            gridColumn: '1 / -1',
            border: 1,
            borderColor: 'divider',
            borderRadius: 1,
            px: 2,
            pb: 2,
            pt: 1,
            m: 0,
            minWidth: 0,
          }}
        >
          <Typography component="legend" variant="subtitle2" sx={{ px: 0.5 }}>
            {t(`paymentSettingsGroup.${method}.${field.path}`)}
          </Typography>
          {groupHelp && (
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
              {groupHelp}
            </Typography>
          )}
          <FieldGrid>{field.children.map(renderField)}</FieldGrid>
        </Box>
      );
    }

    if (field.kind === 'boolean') {
      return (
        <Stack key={field.path} sx={{ gridColumn: '1 / -1' }}>
          <FormControlLabel
            control={
              <Switch
                id={id}
                checked={current === true}
                onChange={(event) => set(event.target.checked)}
                disabled={disabled}
              />
            }
            label={field.required ? `${label(field.path)} *` : label(field.path)}
          />
          {helperText && <FormHelperText error={!!error}>{helperText}</FormHelperText>}
        </Stack>
      );
    }

    if (field.kind === 'const') {
      return (
        <TextField
          key={field.path}
          id={id}
          label={label(field.path)}
          value={String(field.schema.const)}
          required={field.required}
          helperText={helperText}
          error={!!error}
          fullWidth
          slotProps={{ input: { readOnly: true } }}
        />
      );
    }

    if (field.kind === 'enum') {
      return (
        <TextField
          key={field.path}
          id={id}
          select
          label={label(field.path)}
          value={typeof current === 'string' ? current : ''}
          onChange={(event) => set(event.target.value)}
          required={field.required}
          helperText={helperText}
          error={!!error}
          disabled={disabled}
          fullWidth
        >
          {(field.schema.enum ?? []).map((option) => (
            <MenuItem key={option} value={option}>
              {t(`paymentSettingsOption.${method}.${field.path}.${option}`, {
                defaultValue: option,
              })}
            </MenuItem>
          ))}
        </TextField>
      );
    }

    if (field.kind === 'integer') {
      return (
        <TextField
          key={field.path}
          id={id}
          type="number"
          label={label(field.path)}
          value={current === undefined || current === null ? '' : String(current)}
          onChange={(event) => set(integerValue(event.target.value))}
          required={field.required}
          helperText={helperText}
          error={!!error}
          disabled={disabled}
          fullWidth
          slotProps={{
            htmlInput: { min: field.schema.minimum, max: field.schema.maximum, step: 1 },
          }}
        />
      );
    }

    return (
      <TextField
        key={field.path}
        id={id}
        label={label(field.path)}
        value={typeof current === 'string' ? current : ''}
        onChange={(event) => set(event.target.value)}
        required={field.required}
        helperText={helperText}
        error={!!error}
        disabled={disabled}
        fullWidth
        autoComplete="off"
        slotProps={{
          htmlInput: { pattern: field.schema.pattern, maxLength: field.schema.maxLength },
        }}
      />
    );
  };

  return <FieldGrid>{schemaFields(schema).map(renderField)}</FieldGrid>;
}

/** Uma coluna a 360 px, duas a partir de 600 px. */
export function FieldGrid({ children }: { children: ReactNode }) {
  return (
    <Box
      sx={{
        display: 'grid',
        gap: 2,
        gridTemplateColumns: { xs: 'minmax(0, 1fr)', sm: 'repeat(2, minmax(0, 1fr))' },
        minWidth: 0,
      }}
    >
      {children}
    </Box>
  );
}
