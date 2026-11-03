import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import {
  createInsurerProduct,
  type CreateProductRequestType,
  type ProductResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { fieldText } from '../organizations/OrganizationForm';
import { productTypes } from './coverageRules';

const codePattern = /^[A-Z0-9][A-Z0-9_-]{1,29}$/;
type Field = 'code' | 'name' | 'type' | 'description';
const fields: Field[] = ['code', 'name', 'type', 'description'];

/** Valida os dados de um produto; devolve os códigos de erro por campo. */
export function validateProduct(values: {
  code?: string;
  name: string;
  type: string;
  description: string;
}): Partial<Record<Field, string>> {
  const errors: Partial<Record<Field, string>> = {};
  if (values.code !== undefined) {
    if (!values.code) errors.code = 'REQUIRED';
    else if (!codePattern.test(values.code)) errors.code = 'PATTERN';
  }
  const name = values.name.trim();
  if (!name) errors.name = 'REQUIRED';
  else if (name.length < 2 || name.length > 120) errors.name = 'SIZE';
  if (!values.type) errors.type = 'REQUIRED';
  if (values.description.trim().length > 1000) errors.description = 'SIZE';
  return errors;
}

/** "Novo produto": código, nome, tipo e descrição. */
export function ProductCreateDialog({
  onClose,
  onCreated,
}: {
  onClose(): void;
  onCreated(product: ProductResponse): void;
}) {
  const { t } = useTranslation();
  const [values, setValues] = useState({ code: '', name: '', type: '', description: '' });
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>({});
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const found = validateProduct(values);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    setSaving(true);
    try {
      onCreated(
        await createInsurerProduct({
          code: values.code,
          name: values.name.trim(),
          type: values.type as CreateProductRequestType,
          description: values.description.trim() || undefined,
        }),
      );
    } catch (error) {
      const problem = getProblem(error);
      setSaving(false);
      if (problem.code === 'PRODUCT_CODE_ALREADY_EXISTS') {
        setErrors({ code: problem.code });
        return;
      }
      const server: Partial<Record<Field, string>> = {};
      for (const e of problem.errors) {
        if ((fields as string[]).includes(e.field)) server[e.field as Field] = e.code;
      }
      if (Object.keys(server).length > 0) setErrors(server);
      else setMessage(problemMessage(t, problem));
    }
  };

  const helper = (field: Field, fallback?: string) =>
    errors[field] ? fieldText(t, errors[field]!) : fallback;

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="product-create-title">
      <DialogTitle id="product-create-title">{t('products.create.title')}</DialogTitle>
      <DialogContent>
        <form id="product-create-form" onSubmit={submit} noValidate>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField
              label={t('products.fields.code')}
              value={values.code}
              onChange={(event) =>
                setValues((v) => ({ ...v, code: event.target.value.toUpperCase() }))
              }
              required
              error={!!errors.code}
              helperText={helper('code', t('products.fields.codeHelp'))}
              autoComplete="off"
              slotProps={{ htmlInput: { maxLength: 30 } }}
            />
            <TextField
              label={t('products.fields.name')}
              value={values.name}
              onChange={(event) => setValues((v) => ({ ...v, name: event.target.value }))}
              required
              error={!!errors.name}
              helperText={helper('name')}
              autoComplete="off"
              slotProps={{ htmlInput: { maxLength: 120 } }}
            />
            <TextField
              select
              label={t('products.fields.type')}
              value={values.type}
              onChange={(event) => setValues((v) => ({ ...v, type: event.target.value }))}
              required
              error={!!errors.type}
              helperText={helper('type')}
              slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
            >
              <option value="">{t('products.fields.choose')}</option>
              {productTypes.map((type) => (
                <option key={type} value={type}>
                  {t(`productType.${type}`)}
                </option>
              ))}
            </TextField>
            <TextField
              label={t('products.fields.description')}
              value={values.description}
              onChange={(event) => setValues((v) => ({ ...v, description: event.target.value }))}
              error={!!errors.description}
              helperText={helper('description', t('products.fields.optional'))}
              multiline
              minRows={3}
              slotProps={{ htmlInput: { maxLength: 1000 } }}
            />
          </Stack>
        </form>
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common.cancel')}</Button>
        <Button type="submit" form="product-create-form" variant="contained" disabled={saving}>
          {t('products.create.submit')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
