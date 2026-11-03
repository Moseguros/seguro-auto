import Checkbox from '@mui/material/Checkbox';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { ProductVersionResponse } from '../../../api/generated';
import { fieldText } from '../../organizations/OrganizationForm';
import {
  vehicleCategories,
  vehicleUsages,
  type FieldErrors,
  type GeneralDraft,
} from '../editorModel';

export interface GeneralStepProps {
  version: ProductVersionResponse;
  draft: GeneralDraft;
  onChange(draft: GeneralDraft): void;
  readOnly: boolean;
  errors: FieldErrors;
}

function toggle<T>(list: T[], value: T, on: boolean): T[] {
  return on ? [...list.filter((item) => item !== value), value] : list.filter((v) => v !== value);
}

/** Passo 1: elegibilidade (categorias e usos) e notas da versão. */
export function GeneralStep({ version, draft, onChange, readOnly, errors }: GeneralStepProps) {
  const { t } = useTranslation();

  const group = <T extends string>(
    name: 'vehicleCategories' | 'usages',
    legend: string,
    values: T[],
    selected: T[],
    prefix: string,
    set: (next: T[]) => void,
  ) => {
    const code = errors[name];
    return (
      <FormControl component="fieldset" error={!!code} disabled={readOnly}>
        <FormLabel component="legend">{legend}</FormLabel>
        <FormGroup sx={{ display: 'grid', gridTemplateColumns: { sm: '1fr 1fr' } }}>
          {values.map((value) => (
            <FormControlLabel
              key={value}
              label={t(`${prefix}.${value}`)}
              control={
                <Checkbox
                  checked={selected.includes(value)}
                  onChange={(event) => set(toggle(selected, value, event.target.checked))}
                />
              }
            />
          ))}
        </FormGroup>
        {code && <FormHelperText>{fieldText(t, code)}</FormHelperText>}
      </FormControl>
    );
  };

  return (
    <Stack spacing={3}>
      <Stack spacing={0.5}>
        <Typography variant="body2" color="text.secondary">
          {t('products.general.product')}
        </Typography>
        <Typography>
          {version.productName} · {t(`productType.${version.productType}`)}
        </Typography>
        {!readOnly && (
          <Link component={RouterLink} to={`/seguradora/produtos/${version.productId}`}>
            {t('products.general.editProduct')}
          </Link>
        )}
      </Stack>
      {group(
        'vehicleCategories',
        t('products.general.categories'),
        vehicleCategories,
        draft.vehicleCategories,
        'vehicleCategory',
        (next) => onChange({ ...draft, vehicleCategories: next }),
      )}
      {group(
        'usages',
        t('products.general.usages'),
        vehicleUsages,
        draft.usages,
        'vehicleUsage',
        (next) => onChange({ ...draft, usages: next }),
      )}
      <TextField
        label={t('products.general.changeNotes')}
        value={draft.changeNotes}
        onChange={(event) => onChange({ ...draft, changeNotes: event.target.value })}
        disabled={readOnly}
        multiline
        minRows={3}
        fullWidth
        error={!!errors.changeNotes}
        helperText={errors.changeNotes ? fieldText(t, errors.changeNotes) : undefined}
        slotProps={{ htmlInput: { maxLength: 1000 } }}
      />
    </Stack>
  );
}
