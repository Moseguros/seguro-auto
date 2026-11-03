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
  createReferenceValue,
  updateReferenceValue,
  type ReferenceValueResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { fieldText } from '../organizations/OrganizationForm';
import type { ReferenceDomain } from './referenceDomains';

type Field = 'code' | 'labelPt' | 'labelEn' | 'parentCode' | 'sortOrder';
const fields: Field[] = ['code', 'labelPt', 'labelEn', 'parentCode', 'sortOrder'];
const codePattern = /^[A-Z0-9_]{1,40}$/;

export interface ReferenceValueDialogProps {
  domain: ReferenceDomain;
  /** Valor a editar; sem ele, cria um novo. */
  value?: ReferenceValueResponse;
  /** Marcas para escolher (só Modelos). */
  makes?: { code: string; label: string }[];
  onClose(): void;
  onSaved(value: ReferenceValueResponse): void;
  onConflict?(): void;
}

/** Criar ou editar um valor de uma tabela de referência. */
export function ReferenceValueDialog({
  domain,
  value,
  makes = [],
  onClose,
  onSaved,
  onConflict,
}: ReferenceValueDialogProps) {
  const { t } = useTranslation();
  const creating = !value;
  const withParent = domain === 'VEHICLE_MODEL';
  const [values, setValues] = useState({
    code: value?.code ?? '',
    labelPt: value?.labelPt ?? '',
    labelEn: value?.labelEn ?? '',
    parentCode: value?.parentCode ?? '',
    sortOrder: String(value?.sortOrder ?? 100),
  });
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>({});
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const validate = () => {
    const found: Partial<Record<Field, string>> = {};
    if (creating) {
      if (!values.code) found.code = 'REQUIRED';
      else if (!codePattern.test(values.code)) found.code = 'INVALID_CODE';
      if (withParent && !values.parentCode) found.parentCode = 'REQUIRED';
    }
    const labelPt = values.labelPt.trim();
    if (!labelPt) found.labelPt = 'REQUIRED';
    else if (labelPt.length > 120) found.labelPt = 'SIZE';
    if (values.labelEn.trim().length > 120) found.labelEn = 'SIZE';
    const order = Number(values.sortOrder);
    if (!/^\d{1,4}$/.test(values.sortOrder) || order > 9999) found.sortOrder = 'OUT_OF_RANGE';
    return found;
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const found = validate();
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    setSaving(true);
    const common = {
      labelPt: values.labelPt.trim(),
      labelEn: values.labelEn.trim() || undefined,
      sortOrder: Number(values.sortOrder),
    };
    try {
      onSaved(
        value
          ? await updateReferenceValue(domain, value.id, { ...common, version: value.version })
          : await createReferenceValue(domain, {
              ...common,
              code: values.code,
              parentCode: withParent ? values.parentCode : undefined,
            }),
      );
    } catch (error) {
      const problem = getProblem(error);
      setSaving(false);
      if (problem.code === 'REFERENCE_CODE_ALREADY_EXISTS') {
        setErrors({ code: problem.code });
        return;
      }
      if (problem.code === 'CONCURRENT_MODIFICATION') onConflict?.();
      const server: Partial<Record<Field, string>> = {};
      for (const e of problem.errors) {
        if ((fields as string[]).includes(e.field)) server[e.field as Field] = e.code;
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
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="reference-title">
      <DialogTitle id="reference-title">
        {creating ? t('references.values.newTitle') : t('references.values.editTitle')}
      </DialogTitle>
      <DialogContent>
        <form id="reference-form" onSubmit={submit} noValidate>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField
              label={t('references.fields.code')}
              value={values.code}
              onChange={(event) =>
                setValues((v) => ({ ...v, code: event.target.value.toUpperCase() }))
              }
              disabled={!creating}
              required={creating}
              error={!!errors.code}
              helperText={helper('code', creating ? t('references.fields.codeHelp') : undefined)}
              autoComplete="off"
              slotProps={{ htmlInput: { maxLength: 40 } }}
            />
            {withParent && (
              <TextField
                select
                label={t('references.fields.make')}
                value={values.parentCode}
                onChange={(event) => setValues((v) => ({ ...v, parentCode: event.target.value }))}
                disabled={!creating}
                required={creating}
                error={!!errors.parentCode}
                helperText={helper('parentCode')}
                slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
              >
                <option value="">{t('references.fields.choose')}</option>
                {value?.parentCode && !makes.some((m) => m.code === value.parentCode) && (
                  <option value={value.parentCode}>{value.parentCode}</option>
                )}
                {makes.map((make) => (
                  <option key={make.code} value={make.code}>
                    {make.label}
                  </option>
                ))}
              </TextField>
            )}
            <TextField
              label={t('references.fields.labelPt')}
              value={values.labelPt}
              onChange={(event) => setValues((v) => ({ ...v, labelPt: event.target.value }))}
              required
              error={!!errors.labelPt}
              helperText={helper('labelPt')}
              autoComplete="off"
              slotProps={{ htmlInput: { maxLength: 120 } }}
            />
            <TextField
              label={t('references.fields.labelEn')}
              value={values.labelEn}
              onChange={(event) => setValues((v) => ({ ...v, labelEn: event.target.value }))}
              error={!!errors.labelEn}
              helperText={helper('labelEn', t('references.fields.optional'))}
              autoComplete="off"
              slotProps={{ htmlInput: { maxLength: 120 } }}
            />
            <TextField
              label={t('references.fields.sortOrder')}
              value={values.sortOrder}
              onChange={(event) => setValues((v) => ({ ...v, sortOrder: event.target.value }))}
              error={!!errors.sortOrder}
              helperText={helper('sortOrder', t('references.fields.sortOrderHelp'))}
              slotProps={{ htmlInput: { inputMode: 'numeric', maxLength: 4 } }}
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
        <Button type="submit" form="reference-form" variant="contained" disabled={saving}>
          {t('common.save')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
