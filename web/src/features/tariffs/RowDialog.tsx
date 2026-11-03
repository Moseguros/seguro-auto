import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import InputAdornment from '@mui/material/InputAdornment';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { Money } from '../../api/generated';
import { MoneyField } from '../../components/MoneyField';
import { fieldMessage } from '../../errors/problem';
import { mzn } from '../../format/money';
import {
  decimalPlaces,
  formatDecimal,
  isPositiveUpTo,
  normalizeDecimal,
  percentToRate,
  rateToPercent,
  toDecimal,
} from './decimal';
import type { CodeSet, Condition, TariffRow, ValueKind, ValueRange } from './ratingTypes';
import { intLimits, maxCentavos, variables, type VariableDef } from './variables';

interface VariableState {
  use: boolean;
  set: string[];
  bool: boolean;
  gte: string;
  lt: string;
  gteMoney: Money | null;
  ltMoney: Money | null;
}

type FormState = Record<string, VariableState>;

function initialState(when: Condition, suggested?: keyof Condition): FormState {
  const state: FormState = {};
  for (const v of variables) {
    const condition = when[v.name];
    const range = (condition ?? {}) as ValueRange;
    state[v.name] = {
      use: condition !== undefined || (Object.keys(when).length === 0 && v.name === suggested),
      set: v.kind === 'set' && condition ? [...(condition as CodeSet).in] : [],
      bool: v.kind === 'bool' ? condition !== false : true,
      gte: v.kind === 'int' && range.gte !== undefined ? String(range.gte) : '',
      lt: v.kind === 'int' && range.lt !== undefined ? String(range.lt) : '',
      gteMoney: v.kind === 'money' && range.gte !== undefined ? mzn(range.gte) : null,
      ltMoney: v.kind === 'money' && range.lt !== undefined ? mzn(range.lt) : null,
    };
  }
  return state;
}

function initialValueText(row: TariffRow | null, kind: ValueKind, language: string): string {
  const value = row?.value;
  if (typeof value !== 'string') return '';
  if (kind === 'RATE') return rateToPercent(value, language) ?? value;
  return formatDecimal(value, language);
}

const digits = /^\d+$/;

/** Erros de uma variável usada, ou `null`. */
function variableError(def: VariableDef, s: VariableState, invalidMoney: boolean): string | null {
  if (def.kind === 'set') return s.set.length === 0 ? 'REQUIRED' : null;
  if (def.kind === 'bool') return null;
  if (def.kind === 'money') {
    if (invalidMoney) return 'INVALID_MONEY';
    const gte = s.gteMoney?.amount;
    const lt = s.ltMoney?.amount;
    if (gte === undefined && lt === undefined) return 'REQUIRED';
    if ((gte ?? 0) > maxCentavos || (lt ?? 0) > maxCentavos) return 'VALUE_OUT_OF_RANGE';
    if (gte !== undefined && lt !== undefined && gte >= lt) return 'INVALID_RANGE';
    return null;
  }
  const gteText = s.gte.trim();
  const ltText = s.lt.trim();
  if (gteText === '' && ltText === '') return 'REQUIRED';
  if ((gteText !== '' && !digits.test(gteText)) || (ltText !== '' && !digits.test(ltText))) {
    return 'INVALID_VALUE';
  }
  const gte = gteText === '' ? undefined : Number(gteText);
  const lt = ltText === '' ? undefined : Number(ltText);
  if (
    (gte ?? 0) > intLimits.max ||
    (lt !== undefined && (lt < intLimits.ltMin || lt > intLimits.max))
  ) {
    return 'VALUE_OUT_OF_RANGE';
  }
  if (gte !== undefined && lt !== undefined && gte >= lt) return 'INVALID_RANGE';
  return null;
}

/** Valor escrito → valor da linha, ou o código do erro. */
function parseValue(
  kind: ValueKind,
  text: string,
  money: Money | null,
  invalidMoney: boolean,
): { value: TariffRow['value'] } | { error: string } {
  if (kind === 'AMOUNT') {
    if (invalidMoney) return { error: 'INVALID_MONEY' };
    if (!money) return { error: 'REQUIRED' };
    return money.amount > maxCentavos ? { error: 'VALUE_OUT_OF_RANGE' } : { value: money.amount };
  }
  if (text.trim() === '') return { error: 'REQUIRED' };
  if (kind === 'RATE') {
    const rate = percentToRate(text);
    if (rate === null || decimalPlaces(text) > 4) return { error: 'INVALID_VALUE' };
    return isPositiveUpTo(toDecimal(rate)!, 1) ? { value: rate } : { error: 'VALUE_OUT_OF_RANGE' };
  }
  const factor = normalizeDecimal(text);
  if (factor === null || decimalPlaces(text) > 6) return { error: 'INVALID_VALUE' };
  return isPositiveUpTo(toDecimal(factor)!, 100)
    ? { value: factor }
    : { error: 'VALUE_OUT_OF_RANGE' };
}

export interface RowDialogProps {
  /** Linha a editar; `null` para uma linha nova. */
  row: TariffRow | null;
  /** Número da linha (1, 2, …) ao editar. */
  rowNumber?: number;
  kind: ValueKind;
  /** Variável que começa ligada numa linha nova (factores pré-definidos). */
  suggested?: keyof Condition;
  onClose(): void;
  onApply(row: TariffRow): void;
}

/** Diálogo para criar ou editar uma linha de uma tabela (base ou factor). */
export function RowDialog({ row, rowNumber, kind, suggested, onClose, onApply }: RowDialogProps) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [label, setLabel] = useState(row?.label ?? '');
  const [state, setState] = useState<FormState>(() =>
    initialState(row?.when ?? {}, row ? undefined : suggested),
  );
  const [valueText, setValueText] = useState(() => initialValueText(row, kind, i18n.language));
  const [valueMoney, setValueMoney] = useState<Money | null>(
    kind === 'AMOUNT' && typeof row?.value === 'number' ? mzn(row.value) : null,
  );
  const [invalidMoney, setInvalidMoney] = useState<Set<string>>(new Set());
  const [errors, setErrors] = useState<Record<string, string>>({});

  const update = (name: string, patch: Partial<VariableState>) =>
    setState((s) => ({ ...s, [name]: { ...s[name]!, ...patch } }));
  const moneyValidity = (field: string) => (valid: boolean) =>
    setInvalidMoney((current) => {
      const next = new Set(current);
      if (valid) next.delete(field);
      else next.add(field);
      return next;
    });

  const apply = () => {
    const found: Record<string, string> = {};
    const trimmed = label.trim();
    if (trimmed.length > 80) found.label = 'TOO_LONG';
    const when: Condition = {};
    for (const def of variables) {
      const s = state[def.name]!;
      if (!s.use) continue;
      const error = variableError(
        def,
        s,
        invalidMoney.has(`${def.name}.gte`) || invalidMoney.has(`${def.name}.lt`),
      );
      if (error) {
        found[def.name] = error;
        continue;
      }
      const target = when as Record<string, unknown>;
      if (def.kind === 'set') target[def.name] = { in: s.set };
      else if (def.kind === 'bool') target[def.name] = s.bool;
      else {
        const range: ValueRange = {};
        if (def.kind === 'int') {
          if (s.gte.trim() !== '') range.gte = Number(s.gte.trim());
          if (s.lt.trim() !== '') range.lt = Number(s.lt.trim());
        } else {
          if (s.gteMoney) range.gte = s.gteMoney.amount;
          if (s.ltMoney) range.lt = s.ltMoney.amount;
        }
        target[def.name] = range;
      }
    }
    const parsed = parseValue(kind, valueText, valueMoney, invalidMoney.has('value'));
    if ('error' in parsed) found.value = parsed.error;
    setErrors(found);
    if (Object.keys(found).length > 0 || 'error' in parsed) return;
    onApply(
      trimmed ? { label: trimmed, when, value: parsed.value } : { when, value: parsed.value },
    );
  };

  const errorText = (field: string) => (errors[field] ? fieldMessage(t, errors[field]) : undefined);

  const control = (def: VariableDef) => {
    const s = state[def.name]!;
    const name = t(`variables.${def.name}`);
    switch (def.kind) {
      case 'set':
        return (
          <TextField
            select
            size="small"
            label={name}
            value={s.set}
            error={!!errors[def.name]}
            helperText={errorText(def.name)}
            onChange={(event) => {
              const value = event.target.value as unknown as string[] | string;
              update(def.name, { set: typeof value === 'string' ? value.split(',') : value });
            }}
            slotProps={{
              select: {
                multiple: true,
                renderValue: (selected) =>
                  (selected as string[]).map((c) => t(`${def.optionKey}.${c}`)).join(', '),
              },
            }}
            fullWidth
          >
            {def.options!.map((option) => (
              <MenuItem key={option} value={option}>
                {t(`${def.optionKey}.${option}`)}
              </MenuItem>
            ))}
          </TextField>
        );
      case 'bool':
        return (
          <ToggleButtonGroup
            exclusive
            size="small"
            value={s.bool ? 'yes' : 'no'}
            onChange={(_, value: string | null) => {
              if (value) update(def.name, { bool: value === 'yes' });
            }}
            aria-label={name}
          >
            <ToggleButton value="yes">{t('tariffs.row.yes')}</ToggleButton>
            <ToggleButton value="no">{t('tariffs.row.no')}</ToggleButton>
          </ToggleButtonGroup>
        );
      case 'int':
        return (
          <Stack spacing={0.5}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
              <TextField
                size="small"
                label={t('tariffs.row.gte')}
                value={s.gte}
                error={!!errors[def.name]}
                onChange={(event) => update(def.name, { gte: event.target.value })}
                slotProps={{ htmlInput: { inputMode: 'numeric' } }}
                fullWidth
              />
              <TextField
                size="small"
                label={t('tariffs.row.lt')}
                value={s.lt}
                error={!!errors[def.name]}
                onChange={(event) => update(def.name, { lt: event.target.value })}
                slotProps={{ htmlInput: { inputMode: 'numeric' } }}
                fullWidth
              />
            </Stack>
            {errors[def.name] && <FormHelperText error>{errorText(def.name)}</FormHelperText>}
          </Stack>
        );
      default:
        return (
          <Stack spacing={0.5}>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
              <MoneyField
                size="small"
                label={t('tariffs.row.gte')}
                value={s.gteMoney}
                onChange={(value) => update(def.name, { gteMoney: value })}
                onValidityChange={moneyValidity(`${def.name}.gte`)}
                error={!!errors[def.name]}
              />
              <MoneyField
                size="small"
                label={t('tariffs.row.lt')}
                value={s.ltMoney}
                onChange={(value) => update(def.name, { ltMoney: value })}
                onValidityChange={moneyValidity(`${def.name}.lt`)}
                error={!!errors[def.name]}
              />
            </Stack>
            {errors[def.name] && <FormHelperText error>{errorText(def.name)}</FormHelperText>}
          </Stack>
        );
    }
  };

  const valueLabel =
    kind === 'AMOUNT'
      ? t('tariffs.row.valueAmount')
      : kind === 'RATE'
        ? t('tariffs.row.valuePercent')
        : t('tariffs.row.valueFactor');

  return (
    <Dialog
      open
      onClose={onClose}
      fullScreen={fullScreen}
      fullWidth
      maxWidth="sm"
      aria-labelledby="row-dialog-title"
    >
      <DialogTitle id="row-dialog-title">
        {row ? t('tariffs.row.titleEdit', { n: rowNumber }) : t('tariffs.row.titleNew')}
      </DialogTitle>
      <DialogContent dividers>
        <Stack spacing={2}>
          <TextField
            size="small"
            label={t('tariffs.row.label')}
            value={label}
            onChange={(event) => setLabel(event.target.value)}
            error={!!errors.label}
            helperText={errorText('label')}
            slotProps={{ htmlInput: { maxLength: 120 } }}
            fullWidth
          />
          <Typography variant="subtitle2" component="h3">
            {t('tariffs.row.conditions')}
          </Typography>
          {variables.map((def) => {
            const s = state[def.name]!;
            const name = t(`variables.${def.name}`);
            return (
              <Box key={def.name} role="group" aria-label={name} data-variable={def.name}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={s.use}
                      onChange={(event) => update(def.name, { use: event.target.checked })}
                    />
                  }
                  label={name}
                />
                {s.use && <Box sx={{ pl: { xs: 0, sm: 6 }, pt: 1 }}>{control(def)}</Box>}
              </Box>
            );
          })}
          {kind === 'AMOUNT' ? (
            <MoneyField
              label={valueLabel}
              value={valueMoney}
              onChange={setValueMoney}
              onValidityChange={moneyValidity('value')}
              error={!!errors.value}
              helperText={errorText('value')}
            />
          ) : (
            <TextField
              label={valueLabel}
              value={valueText}
              onChange={(event) => setValueText(event.target.value)}
              error={!!errors.value}
              helperText={errorText('value')}
              slotProps={{
                htmlInput: { inputMode: 'decimal' },
                input:
                  kind === 'RATE'
                    ? { endAdornment: <InputAdornment position="end">%</InputAdornment> }
                    : { startAdornment: <InputAdornment position="start">×</InputAdornment> },
              }}
              fullWidth
            />
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('tariffs.row.cancel')}</Button>
        <Button variant="contained" onClick={apply}>
          {t('tariffs.row.apply')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
