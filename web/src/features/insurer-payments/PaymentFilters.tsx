import FilterList from '@mui/icons-material/FilterList';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Drawer from '@mui/material/Drawer';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useEffect, useRef, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { PaymentStatus, type ListInsurerPaymentsParams } from '../../api/generated';
import { todayInMaputo } from '../../format/dates';
import { daysAgoInMaputo } from './format';

const statuses = Object.values(PaymentStatus);
const methods = ['MPESA', 'BANK_REFERENCE'];
export const paymentsDefaultSort = 'createdAt,desc';
const sortableFields = ['createdAt', 'amount'];

/** Filtros da lista dos pagamentos, lidos da query string (S5-12 §3). */
export interface PaymentListFilters {
  status: string[];
  method: string;
  from: string;
  to: string;
  q: string;
  page: number;
  size: number;
  sort: string;
}

function number(params: URLSearchParams, key: string, fallback: number): number {
  const raw = params.get(key);
  const value = Number(raw);
  return raw !== null && Number.isInteger(value) && value >= 0 ? value : fallback;
}

/** Sem `from`/`to` na URL: os últimos 30 dias; `from=` vazio = sem limite. */
export function readPaymentFilters(
  params: URLSearchParams,
  now: Date = new Date(),
): PaymentListFilters {
  const sort = params.get('sort') ?? paymentsDefaultSort;
  const size = number(params, 'size', 20);
  return {
    status: params.getAll('status').filter((s) => statuses.includes(s as PaymentStatus)),
    method: methods.includes(params.get('method') ?? '') ? (params.get('method') as string) : '',
    from: params.has('from') ? (params.get('from') ?? '') : daysAgoInMaputo(30, now),
    to: params.has('to') ? (params.get('to') ?? '') : todayInMaputo(now),
    q: params.get('q') ?? '',
    page: number(params, 'page', 0),
    size: [10, 20, 50].includes(size) ? size : 20,
    sort: sortableFields.includes(sort.split(',')[0] ?? '') ? sort : paymentsDefaultSort,
  };
}

export function writePaymentFilters(filters: PaymentListFilters): URLSearchParams {
  const params = new URLSearchParams();
  filters.status.forEach((s) => params.append('status', s));
  if (filters.method) params.set('method', filters.method);
  params.set('from', filters.from);
  params.set('to', filters.to);
  if (filters.q) params.set('q', filters.q);
  if (filters.page) params.set('page', String(filters.page));
  if (filters.size !== 20) params.set('size', String(filters.size));
  if (filters.sort !== paymentsDefaultSort) params.set('sort', filters.sort);
  return params;
}

/** Parâmetros do pedido (sem a página, para a exportação). */
export function paymentQueryParams(filters: PaymentListFilters): ListInsurerPaymentsParams {
  return {
    status: filters.status.length ? (filters.status as PaymentStatus[]) : undefined,
    method: (filters.method || undefined) as ListInsurerPaymentsParams['method'],
    from: filters.from || undefined,
    to: filters.to || undefined,
    q: filters.q || undefined,
  };
}

/** Os filtros em linha (≥ 600 px) ou num painel lateral com o botão "Filtros". */
export function FiltersBar({ narrow, children }: { narrow: boolean; children: ReactNode }) {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const fields = (
    <Stack
      direction={narrow ? 'column' : 'row'}
      spacing={2}
      useFlexGap
      flexWrap="wrap"
      sx={{ minWidth: 0 }}
    >
      {children}
    </Stack>
  );
  if (!narrow) return fields;
  return (
    <>
      <div>
        <Button variant="outlined" startIcon={<FilterList />} onClick={() => setOpen(true)}>
          {t('insurerPayments.filters.open')}
        </Button>
      </div>
      <Drawer
        anchor="right"
        open={open}
        onClose={() => setOpen(false)}
        slotProps={{ paper: { sx: { width: 'min(320px, 90vw)', p: 2 } } }}
      >
        <Stack spacing={2} role="region" aria-label={t('insurerPayments.filters.open')}>
          <Typography variant="h6" component="h2">
            {t('insurerPayments.filters.open')}
          </Typography>
          {fields}
          <Button variant="outlined" onClick={() => setOpen(false)}>
            {t('common.close')}
          </Button>
        </Stack>
      </Drawer>
    </>
  );
}

/** Selecção múltipla com caixas (vazio = todos). */
export function MultiSelectFilter({
  id,
  label,
  value,
  options,
  onChange,
  fullWidth,
}: {
  id: string;
  label: string;
  value: string[];
  options: { value: string; label: string }[];
  onChange(value: string[]): void;
  fullWidth?: boolean;
}) {
  const { t } = useTranslation();
  const labelId = `${id}-label`;
  return (
    <FormControl size="small" sx={{ minWidth: 200 }} fullWidth={fullWidth}>
      <InputLabel id={labelId} shrink>
        {label}
      </InputLabel>
      <Select
        labelId={labelId}
        id={id}
        multiple
        notched
        displayEmpty
        label={label}
        value={value}
        onChange={(event) => {
          const next = event.target.value;
          onChange(typeof next === 'string' ? next.split(',') : next);
        }}
        renderValue={(selected) =>
          selected.length === 0
            ? t('insurerPayments.filters.all')
            : selected.map((s) => options.find((o) => o.value === s)?.label ?? s).join(', ')
        }
      >
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            <Checkbox size="small" checked={value.includes(option.value)} />
            <ListItemText primary={option.label} />
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
}

/** Lista de uma opção, com "Todos" (valor vazio) se `allLabel` vier. */
export function SingleSelectFilter({
  id,
  label,
  value,
  options,
  onChange,
  allLabel,
  fullWidth,
}: {
  id: string;
  label: string;
  value: string;
  options: { value: string; label: string }[];
  onChange(value: string): void;
  allLabel?: string;
  fullWidth?: boolean;
}) {
  return (
    <TextField
      select
      size="small"
      id={id}
      label={label}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      sx={{ minWidth: 170 }}
      fullWidth={fullWidth}
      slotProps={{ inputLabel: { shrink: true }, select: { displayEmpty: true } }}
    >
      {allLabel !== undefined && <MenuItem value="">{allLabel}</MenuItem>}
      {options.map((o) => (
        <MenuItem key={o.value} value={o.value}>
          {o.label}
        </MenuItem>
      ))}
    </TextField>
  );
}

/** Campo de pesquisa que só actualiza o filtro `delayMs` (300) ms depois de se parar de escrever. */
export function SearchFilter({
  label,
  value,
  onChange,
  fullWidth,
  delayMs = 300,
}: {
  label: string;
  value: string;
  onChange(value: string): void;
  fullWidth?: boolean;
  delayMs?: number;
}) {
  const [text, setText] = useState(value);
  useEffect(() => setText(value), [value]);
  // O `onChange` e o `value` mais recentes: com as transições do router, o texto pode mudar antes de
  // a página voltar a desenhar com os outros filtros (ex.: o estado acabado de escolher), e o
  // `onChange` dessa altura ainda não os tem (S6-05).
  const latest = useRef({ onChange, value });
  useEffect(() => {
    latest.current = { onChange, value };
  });
  useEffect(() => {
    const timer = setTimeout(() => {
      if (text.trim() !== latest.current.value) latest.current.onChange(text.trim());
    }, delayMs);
    return () => clearTimeout(timer);
  }, [text, delayMs]);
  return (
    <TextField
      size="small"
      type="search"
      label={label}
      value={text}
      onChange={(event) => setText(event.target.value)}
      sx={{ minWidth: 220, flexGrow: 1 }}
      fullWidth={fullWidth}
    />
  );
}

/** Data (`yyyy-MM-dd`). */
export function DateFilter({
  label,
  value,
  onChange,
  fullWidth,
}: {
  label: string;
  value: string;
  onChange(value: string): void;
  fullWidth?: boolean;
}) {
  return (
    <TextField
      size="small"
      type="date"
      label={label}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      slotProps={{ inputLabel: { shrink: true } }}
      fullWidth={fullWidth}
    />
  );
}

/** Filtros da lista dos pagamentos: estado, método, de/até e pesquisa. */
export function PaymentFilters({
  filters,
  narrow,
  onChange,
}: {
  filters: PaymentListFilters;
  narrow: boolean;
  onChange(changes: Partial<PaymentListFilters>): void;
}) {
  const { t } = useTranslation();
  return (
    <FiltersBar narrow={narrow}>
      <MultiSelectFilter
        id="payment-filter-status"
        label={t('insurerPayments.filters.status')}
        value={filters.status}
        options={statuses.map((s) => ({ value: s, label: t(`paymentStatus.${s}`) }))}
        onChange={(status) => onChange({ status })}
        fullWidth={narrow}
      />
      <SingleSelectFilter
        id="payment-filter-method"
        label={t('insurerPayments.filters.method')}
        value={filters.method}
        options={methods.map((m) => ({ value: m, label: t(`paymentMethod.${m}`) }))}
        onChange={(method) => onChange({ method })}
        allLabel={t('insurerPayments.filters.all')}
        fullWidth={narrow}
      />
      <DateFilter
        label={t('insurerPayments.filters.from')}
        value={filters.from}
        onChange={(from) => onChange({ from })}
        fullWidth={narrow}
      />
      <DateFilter
        label={t('insurerPayments.filters.to')}
        value={filters.to}
        onChange={(to) => onChange({ to })}
        fullWidth={narrow}
      />
      <SearchFilter
        label={t('insurerPayments.filters.search')}
        value={filters.q}
        onChange={(q) => onChange({ q })}
        fullWidth={narrow}
      />
    </FiltersBar>
  );
}
