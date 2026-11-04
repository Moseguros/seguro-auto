import Add from '@mui/icons-material/Add';
import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import Delete from '@mui/icons-material/Delete';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import InputAdornment from '@mui/material/InputAdornment';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import {
  getInsurerRatingSettings,
  updateInsurerRatingSettings,
  type Charge,
  type InsurerRatingSettingsResponse,
  type Money,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { MoneyField } from '../../components/MoneyField';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { mzn } from '../../format/money';
import { decimalPlaces, formatDecimal, normalizeDecimal, toDecimal } from './decimal';

export const ratingSettingsQueryKey = ['insurerRatingSettings'] as const;

export function useRatingSettings() {
  return useQuery({
    queryKey: ratingSettingsQueryKey,
    queryFn: ({ signal }) => getInsurerRatingSettings(undefined, signal),
    refetchOnWindowFocus: false,
  });
}

export interface ChargeDraft {
  code: string;
  labelPt: string;
  labelEn: string;
  type: 'PERCENT' | 'FIXED';
  percent: string;
  fixed: Money | null;
}

export const maxCharges = 10;
const maxFixed = 100_000_000;

export function chargeDraftFrom(charge: Charge, language: string): ChargeDraft {
  return {
    code: charge.code,
    labelPt: charge.labelPt,
    labelEn: charge.labelEn,
    type: charge.type,
    percent: charge.type === 'PERCENT' ? formatDecimal(String(charge.value), language) : '',
    fixed: charge.type === 'FIXED' && typeof charge.value === 'number' ? mzn(charge.value) : null,
  };
}

const chargeSchema = z
  .object({
    code: z.string().regex(/^[A-Z][A-Z0-9_]{1,29}$/, 'INVALID_CODE'),
    labelPt: z.string().trim().min(1, 'REQUIRED').max(80, 'TOO_LONG'),
    labelEn: z.string().trim().min(1, 'REQUIRED').max(80, 'TOO_LONG'),
    type: z.enum(['PERCENT', 'FIXED']),
    percent: z.string(),
    fixed: z.object({ amount: z.number(), currency: z.string() }).nullable(),
  })
  .superRefine((charge, ctx) => {
    const issue = (message: string) =>
      ctx.addIssue({ code: z.ZodIssueCode.custom, path: ['value'], message });
    if (charge.type === 'PERCENT') {
      const value = normalizeDecimal(charge.percent);
      if (charge.percent.trim() === '') issue('REQUIRED');
      else if (value === null || decimalPlaces(charge.percent) > 4) issue('INVALID_VALUE');
      else if (toDecimal(value)!.greaterThan(100)) issue('VALUE_OUT_OF_RANGE');
    } else if (!charge.fixed) {
      issue('REQUIRED');
    } else if (charge.fixed.amount > maxFixed) {
      issue('VALUE_OUT_OF_RANGE');
    }
  });

/** Validação dos encargos (igual ao `charges-v1.json` e ao S3-01 §4). */
export const chargesSchema = z
  .array(chargeSchema)
  .max(maxCharges, 'VALUE_OUT_OF_RANGE')
  .superRefine((charges, ctx) => {
    charges.forEach((charge, i) => {
      if (charges.findIndex((c) => c.code === charge.code) !== i) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          path: [i, 'code'],
          message: 'DUPLICATE_CHARGE',
        });
      }
    });
  });

/** Erros por campo (`<i>.<campo>`) ou `{}` se os encargos servem. */
export function validateCharges(charges: ChargeDraft[]): Record<string, string> {
  const result = chargesSchema.safeParse(charges);
  if (result.success) return {};
  const errors: Record<string, string> = {};
  for (const issue of result.error.issues) {
    const key = issue.path.join('.');
    if (!errors[key]) errors[key] = issue.message;
  }
  return errors;
}

/** Encargo para o servidor: percentagem em texto com ponto, valor fixo em centavos. */
export function chargeRequest(charge: ChargeDraft): Charge {
  return {
    code: charge.code,
    labelPt: charge.labelPt.trim(),
    labelEn: charge.labelEn.trim(),
    type: charge.type,
    value: charge.type === 'PERCENT' ? normalizeDecimal(charge.percent)! : charge.fixed!.amount,
  };
}

/** Ponteiro do servidor (`/1/value`) → chave do formulário (`1.value`). */
function serverKey(field: string): string {
  return field.replace(/^\//, '').replace(/\//g, '.');
}

/** Separador "Encargos": impostos e taxas da seguradora. */
export function ChargesPanel() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.ratingManage);
  const query = useRatingSettings();
  const [loadedVersion, setLoadedVersion] = useState<number | null>(null);
  const [rows, setRows] = useState<ChargeDraft[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [invalidMoney, setInvalidMoney] = useState<Set<number>>(new Set());
  const [message, setMessage] = useState<string | null>(null);
  const [conflict, setConflict] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  const data = query.data;
  if (data && data.version !== loadedVersion) {
    setLoadedVersion(data.version);
    setRows(data.charges.map((c) => chargeDraftFrom(c, i18n.language)));
    setErrors({});
  }

  if (query.isPending) {
    return <Skeleton variant="rectangular" height={160} data-testid="charges-loading" />;
  }
  if (query.isError || !data) {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('tariffs.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(query.error))}
      </Alert>
    );
  }

  const readOnly = !canManage;
  const update = (index: number, patch: Partial<ChargeDraft>) =>
    setRows((current) => current.map((row, i) => (i === index ? { ...row, ...patch } : row)));
  const move = (from: number, to: number) =>
    setRows((current) => {
      if (to < 0 || to >= current.length) return current;
      const next = [...current];
      const [item] = next.splice(from, 1);
      next.splice(to, 0, item!);
      return next;
    });

  const save = async () => {
    setMessage(null);
    const found = validateCharges(rows);
    invalidMoney.forEach((i) => {
      found[`${i}.value`] = 'INVALID_MONEY';
    });
    setErrors(found);
    if (Object.keys(found).length > 0) {
      setMessage(t('charges.fixFirst'));
      return;
    }
    setSaving(true);
    try {
      const next: InsurerRatingSettingsResponse = await updateInsurerRatingSettings({
        charges: rows.map(chargeRequest),
        version: data.version,
      });
      queryClient.setQueryData(ratingSettingsQueryKey, next);
      setSaved(true);
    } catch (error) {
      const problem = getProblem(error);
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        setConflict(true);
      } else {
        if (problem.errors.length > 0) {
          setErrors(Object.fromEntries(problem.errors.map((e) => [serverKey(e.field), e.code])));
        }
        setMessage(problemMessage(t, problem));
      }
    } finally {
      setSaving(false);
    }
  };

  const reload = async () => {
    setConflict(false);
    setMessage(null);
    setLoadedVersion(null);
    await query.refetch();
  };

  const err = (index: number, field: string) => errors[`${index}.${field}`];
  const help = (index: number, field: string) => {
    const code = err(index, field);
    return code ? fieldMessage(t, code) : undefined;
  };

  return (
    <Stack spacing={2}>
      {conflict && (
        <Alert
          severity="warning"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={() => void reload()}>
              {t('charges.reload')}
            </Button>
          }
        >
          {t('charges.conflict')}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      {rows.length === 0 && <Typography color="text.secondary">{t('charges.empty')}</Typography>}
      {rows.map((row, index) => (
        <Paper
          key={index}
          variant="outlined"
          sx={{ p: 2 }}
          component="section"
          aria-label={t('charges.charge', { n: index + 1 })}
          data-testid={`charge-${index}`}
        >
          <Stack spacing={2}>
            <Stack direction="row" justifyContent="space-between" alignItems="center">
              <Typography variant="subtitle2">{t('charges.charge', { n: index + 1 })}</Typography>
              {!readOnly && (
                <Stack direction="row">
                  <IconButton
                    size="small"
                    aria-label={t('charges.moveUp')}
                    disabled={index === 0}
                    onClick={() => move(index, index - 1)}
                  >
                    <ArrowUpward fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    aria-label={t('charges.moveDown')}
                    disabled={index === rows.length - 1}
                    onClick={() => move(index, index + 1)}
                  >
                    <ArrowDownward fontSize="small" />
                  </IconButton>
                  <IconButton
                    size="small"
                    aria-label={t('charges.delete')}
                    onClick={() => setRows((current) => current.filter((_, i) => i !== index))}
                  >
                    <Delete fontSize="small" />
                  </IconButton>
                </Stack>
              )}
            </Stack>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField
                size="small"
                label={t('charges.columns.code')}
                value={row.code}
                disabled={readOnly}
                onChange={(event) => update(index, { code: event.target.value.toUpperCase() })}
                error={!!err(index, 'code')}
                helperText={help(index, 'code')}
                slotProps={{ htmlInput: { maxLength: 30 } }}
                fullWidth
              />
              <TextField
                size="small"
                label={t('charges.columns.labelPt')}
                value={row.labelPt}
                disabled={readOnly}
                onChange={(event) => update(index, { labelPt: event.target.value })}
                error={!!err(index, 'labelPt')}
                helperText={help(index, 'labelPt')}
                fullWidth
              />
              <TextField
                size="small"
                label={t('charges.columns.labelEn')}
                value={row.labelEn}
                disabled={readOnly}
                onChange={(event) => update(index, { labelEn: event.target.value })}
                error={!!err(index, 'labelEn')}
                helperText={help(index, 'labelEn')}
                fullWidth
              />
            </Stack>
            <Stack direction={{ xs: 'column', md: 'row' }} spacing={2}>
              <TextField
                select
                size="small"
                label={t('charges.columns.type')}
                value={row.type}
                disabled={readOnly}
                onChange={(event) =>
                  update(index, { type: event.target.value as ChargeDraft['type'] })
                }
                sx={{ minWidth: { md: 200 } }}
                fullWidth
              >
                <MenuItem value="PERCENT">{t('charges.type.PERCENT')}</MenuItem>
                <MenuItem value="FIXED">{t('charges.type.FIXED')}</MenuItem>
              </TextField>
              {row.type === 'PERCENT' ? (
                <TextField
                  size="small"
                  label={t('charges.columns.value')}
                  value={row.percent}
                  disabled={readOnly}
                  onChange={(event) => update(index, { percent: event.target.value })}
                  error={!!err(index, 'value')}
                  helperText={help(index, 'value')}
                  slotProps={{
                    htmlInput: { inputMode: 'decimal' },
                    input: { endAdornment: <InputAdornment position="end">%</InputAdornment> },
                  }}
                  fullWidth
                />
              ) : (
                <MoneyField
                  size="small"
                  label={t('charges.columns.value')}
                  value={row.fixed}
                  disabled={readOnly}
                  onChange={(value) => update(index, { fixed: value })}
                  onValidityChange={(valid) =>
                    setInvalidMoney((current) => {
                      const next = new Set(current);
                      if (valid) next.delete(index);
                      else next.add(index);
                      return next;
                    })
                  }
                  error={!!err(index, 'value')}
                  helperText={help(index, 'value')}
                />
              )}
            </Stack>
          </Stack>
        </Paper>
      ))}
      <Typography variant="body2" color="text.secondary">
        {t('charges.note')}
      </Typography>
      {!readOnly && (
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Button
            variant="outlined"
            startIcon={<Add />}
            disabled={rows.length >= maxCharges}
            onClick={() =>
              setRows((current) => [
                ...current,
                { code: '', labelPt: '', labelEn: '', type: 'PERCENT', percent: '', fixed: null },
              ])
            }
          >
            {t('charges.add')}
          </Button>
          <Button variant="contained" onClick={() => void save()} disabled={saving}>
            {t('charges.save')}
          </Button>
        </Stack>
      )}
      <Snackbar
        open={saved}
        autoHideDuration={3000}
        onClose={() => setSaved(false)}
        message={t('charges.saved')}
      />
    </Stack>
  );
}
