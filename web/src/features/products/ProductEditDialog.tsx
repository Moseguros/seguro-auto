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
  updateInsurerProduct,
  type ProductResponse,
  type UpdateProductRequestType,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { fieldText } from '../organizations/OrganizationForm';
import { productTypes } from './coverageRules';
import { validateProduct } from './ProductCreateDialog';

type Field = 'name' | 'type' | 'description';

/** O produto já teve uma versão publicada (o tipo deixa de poder mudar). */
export function wasPublished(product: ProductResponse): boolean {
  return product.status !== 'DRAFT' || product.versions.some((v) => !!v.publishedAt);
}

/** "Editar dados": nome, descrição e tipo do produto. */
export function ProductEditDialog({
  product,
  onClose,
  onSaved,
  onConflict,
}: {
  product: ProductResponse;
  onClose(): void;
  onSaved(product: ProductResponse): void;
  onConflict(): void;
}) {
  const { t } = useTranslation();
  const typeLocked = wasPublished(product);
  const [values, setValues] = useState({
    name: product.name,
    type: product.type as string,
    description: product.description ?? '',
  });
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
      onSaved(
        await updateInsurerProduct(product.id, {
          name: values.name.trim(),
          type: values.type as UpdateProductRequestType,
          description: values.description.trim() || undefined,
          version: product.version,
        }),
      );
    } catch (error) {
      const problem = getProblem(error);
      setSaving(false);
      if (problem.code === 'CONCURRENT_MODIFICATION') onConflict();
      if (problem.code === 'PRODUCT_TYPE_LOCKED') {
        setErrors({ type: problem.code });
        return;
      }
      const server: Partial<Record<Field, string>> = {};
      for (const e of problem.errors) {
        if (e.field === 'name' || e.field === 'type' || e.field === 'description') {
          server[e.field] = e.code;
        }
      }
      if (Object.keys(server).length > 0) setErrors(server);
      else setMessage(problemMessage(t, problem));
    }
  };

  const helper = (field: Field, fallback?: string) => {
    const code = errors[field];
    return code ? fieldText(t, code) : fallback;
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="product-edit-title">
      <DialogTitle id="product-edit-title">{t('products.edit.title')}</DialogTitle>
      <DialogContent>
        <form id="product-edit-form" onSubmit={submit} noValidate>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField label={t('products.fields.code')} value={product.code} disabled />
            <TextField
              label={t('products.fields.name')}
              value={values.name}
              onChange={(event) => setValues((v) => ({ ...v, name: event.target.value }))}
              required
              error={!!errors.name}
              helperText={helper('name')}
              slotProps={{ htmlInput: { maxLength: 120 } }}
            />
            <TextField
              select
              label={t('products.fields.type')}
              value={values.type}
              onChange={(event) => setValues((v) => ({ ...v, type: event.target.value }))}
              disabled={typeLocked}
              error={!!errors.type}
              helperText={helper('type', typeLocked ? t('products.edit.typeLocked') : undefined)}
              slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
            >
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
        <Button type="submit" form="product-edit-form" variant="contained" disabled={saving}>
          {t('common.save')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
