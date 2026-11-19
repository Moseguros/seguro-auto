import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Pagination from '@mui/material/Pagination';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridSortModel } from '@mui/x-data-grid';
import { keepPreviousData, useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import {
  listPaymentExceptions,
  PaymentExceptionType,
  type ListPaymentExceptionsParams,
  type PaymentExceptionSummary,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { formatReference } from '../payments/formatReference';
import {
  insurerPaymentsKey,
  openExceptionsCountKey,
  paymentExceptionsKey,
  useCodeText,
} from './common';
import { ExceptionDrawer } from './ExceptionDrawer';
import { formatStamp } from './format';
import {
  DateFilter,
  FiltersBar,
  MultiSelectFilter,
  SearchFilter,
  SingleSelectFilter,
} from './PaymentFilters';

const types = Object.values(PaymentExceptionType);
const defaultSort = 'createdAt,desc';

interface ExceptionFilters {
  status: string;
  type: string[];
  from: string;
  to: string;
  q: string;
  page: number;
  size: number;
  sort: string;
}

/** `status` ausente → "Por resolver" (`OPEN`); `status=` vazio → todas. */
export function readExceptionFilters(params: URLSearchParams): ExceptionFilters {
  const status = params.has('status') ? (params.get('status') ?? '') : 'OPEN';
  const size = Number(params.get('size'));
  const page = Number(params.get('page'));
  const sort = params.get('sort') ?? defaultSort;
  return {
    status: ['OPEN', 'RESOLVED', ''].includes(status) ? status : 'OPEN',
    type: params.getAll('type').filter((v) => types.includes(v as PaymentExceptionType)),
    from: params.get('from') ?? '',
    to: params.get('to') ?? '',
    q: params.get('q') ?? '',
    page: Number.isInteger(page) && page > 0 ? page : 0,
    size: [10, 20, 50].includes(size) ? size : 20,
    sort: sort.startsWith('createdAt,') ? sort : defaultSort,
  };
}

function writeExceptionFilters(f: ExceptionFilters, exceptionId: string | null): URLSearchParams {
  const params = new URLSearchParams();
  if (f.status !== 'OPEN') params.set('status', f.status);
  f.type.forEach((v) => params.append('type', v));
  if (f.from) params.set('from', f.from);
  if (f.to) params.set('to', f.to);
  if (f.q) params.set('q', f.q);
  if (f.page) params.set('page', String(f.page));
  if (f.size !== 20) params.set('size', String(f.size));
  if (f.sort !== defaultSort) params.set('sort', f.sort);
  if (exceptionId) params.set('excepcao', exceptionId);
  return params;
}

/**
 * Separador "Excepções" (S5-12 §4): filtros na query string, lista e o `ExceptionDrawer` da
 * excepção escolhida (`?excepcao=<id>`, também usado pelas ligações do detalhe do pagamento).
 */
export function ExceptionsPage() {
  const { t, i18n } = useTranslation();
  const codeText = useCodeText();
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => readExceptionFilters(searchParams), [searchParams]);
  const exceptionId = searchParams.get('excepcao');
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const localeText = useGridLocaleText();
  const [notice, setNotice] = useState<string | null>(null);

  const update = (changes: Partial<ExceptionFilters>, resetPage = true) => {
    const next = { ...filters, ...changes, ...(resetPage ? { page: 0 } : {}) };
    setSearchParams(writeExceptionFilters(next, exceptionId), { replace: true });
  };
  const openException = (id: string | null) =>
    setSearchParams(writeExceptionFilters(filters, id), { replace: !id });

  const params: ListPaymentExceptionsParams = {
    status: filters.status,
    type: filters.type.length ? (filters.type as PaymentExceptionType[]) : undefined,
    from: filters.from || undefined,
    to: filters.to || undefined,
    q: filters.q || undefined,
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  };
  const query = useQuery({
    queryKey: [...paymentExceptionsKey, 'list', params],
    queryFn: ({ signal }) =>
      listPaymentExceptions(params, { paramsSerializer: { indexes: null } }, signal),
    placeholderData: keepPreviousData,
  });

  const statusText = (row: PaymentExceptionSummary) =>
    row.status === 'RESOLVED' && row.resolution
      ? `${t('paymentExceptions.status.RESOLVED')} · ${codeText('paymentResolutionDone', row.resolution)}`
      : t(`paymentExceptions.status.${row.status}`);
  const fileText = (row: PaymentExceptionSummary) =>
    row.fileName
      ? row.lineNo !== undefined
        ? t('paymentExceptions.fileLine', { file: row.fileName, line: row.lineNo })
        : row.fileName
      : '';
  const intentText = (row: PaymentExceptionSummary) =>
    row.intent ? `${row.intent.reference} · ${row.intent.targetLabel}` : '';

  const columns = useMemo<GridColDef<PaymentExceptionSummary>[]>(
    () => [
      {
        field: 'createdAt',
        headerName: t('paymentExceptions.columns.date'),
        minWidth: 150,
        sortable: true,
        valueFormatter: (value: string) => formatStamp(value),
      },
      {
        field: 'type',
        headerName: t('paymentExceptions.columns.type'),
        minWidth: 190,
        flex: 1,
        sortable: false,
        valueGetter: (value: string) => codeText('paymentExceptionType', value),
      },
      {
        field: 'reference',
        headerName: t('paymentExceptions.columns.reference'),
        minWidth: 150,
        sortable: false,
        valueGetter: (value: string | undefined) => (value ? formatReference(value) : ''),
      },
      {
        field: 'amount',
        headerName: t('paymentExceptions.columns.amount'),
        minWidth: 140,
        align: 'right',
        headerAlign: 'right',
        sortable: false,
        renderCell: ({ row }) => (
          <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
            {row.amount ? formatMoney(row.amount, i18n.language) : ''}
          </Box>
        ),
      },
      {
        field: 'paidAt',
        headerName: t('paymentExceptions.columns.paidAt'),
        minWidth: 150,
        sortable: false,
        valueFormatter: (value: string | undefined) => formatStamp(value),
      },
      {
        field: 'intent',
        headerName: t('paymentExceptions.columns.intent'),
        minWidth: 200,
        flex: 1,
        sortable: false,
        valueGetter: (_value, row) => intentText(row),
      },
      {
        field: 'fileName',
        headerName: t('paymentExceptions.columns.file'),
        minWidth: 200,
        flex: 1,
        sortable: false,
        valueGetter: (_value, row) => fileText(row),
      },
      {
        field: 'status',
        headerName: t('paymentExceptions.columns.status'),
        minWidth: 200,
        sortable: false,
        renderCell: ({ row }) => (
          <Chip
            size="small"
            variant="outlined"
            color={row.status === 'OPEN' ? 'warning' : 'success'}
            label={statusText(row)}
          />
        ),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, i18n.language],
  );

  const rows = query.data?.content ?? [];
  const total = query.data?.totalElements ?? 0;
  const [sortField, sortDirection] = filters.sort.split(',') as [string, 'asc' | 'desc'];
  const sortModel: GridSortModel = [{ field: sortField, sort: sortDirection }];

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if (total === 0) body = <QueueEmpty>{t('paymentExceptions.empty')}</QueueEmpty>;
  else if (narrow) {
    body = (
      <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {rows.map((row) => (
          <li key={row.id}>
            <Card variant="outlined">
              <CardActionArea onClick={() => openException(row.id)} data-testid="exception-card">
                <CardContent>
                  <Typography fontWeight={600}>
                    {codeText('paymentExceptionType', row.type)}
                  </Typography>
                  <Typography variant="body2">
                    {row.reference ? formatReference(row.reference) : ''}
                    {row.amount && ` · ${formatMoney(row.amount, i18n.language)}`}
                  </Typography>
                  {row.intent && <Typography variant="body2">{intentText(row)}</Typography>}
                  <Typography variant="caption" color="text.secondary" component="p">
                    {formatStamp(row.createdAt)} · {statusText(row)}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </li>
        ))}
        {total > filters.size && (
          <Pagination
            count={Math.ceil(total / filters.size)}
            page={filters.page + 1}
            onChange={(_event, page) => update({ page: page - 1 }, false)}
            size="small"
            sx={{ alignSelf: 'center' }}
          />
        )}
      </Stack>
    );
  } else {
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('insurerPayments.tabs.exceptions')}
          rows={rows}
          rowCount={total}
          columns={columns}
          loading={query.isFetching}
          paginationMode="server"
          sortingMode="server"
          paginationModel={{ page: filters.page, pageSize: filters.size }}
          onPaginationModelChange={(model) =>
            update({ page: model.page, size: model.pageSize }, model.pageSize !== filters.size)
          }
          sortModel={sortModel}
          onSortModelChange={(model) => {
            const first = model[0];
            update({ sort: first?.sort ? `${first.field},${first.sort}` : defaultSort });
          }}
          sortingOrder={['desc', 'asc']}
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => openException(row.id)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    );
  }

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <FiltersBar narrow={narrow}>
        <SingleSelectFilter
          id="exception-filter-status"
          label={t('insurerPayments.filters.status')}
          value={filters.status}
          options={[
            { value: 'OPEN', label: t('paymentExceptions.statusFilter.OPEN') },
            { value: 'RESOLVED', label: t('paymentExceptions.statusFilter.RESOLVED') },
          ]}
          allLabel={t('paymentExceptions.statusFilter.ALL')}
          onChange={(status) => update({ status })}
          fullWidth={narrow}
        />
        <MultiSelectFilter
          id="exception-filter-type"
          label={t('insurerPayments.filters.type')}
          value={filters.type}
          options={types.map((v) => ({ value: v, label: codeText('paymentExceptionType', v) }))}
          onChange={(type) => update({ type })}
          fullWidth={narrow}
        />
        <DateFilter
          label={t('insurerPayments.filters.from')}
          value={filters.from}
          onChange={(from) => update({ from })}
          fullWidth={narrow}
        />
        <DateFilter
          label={t('insurerPayments.filters.to')}
          value={filters.to}
          onChange={(to) => update({ to })}
          fullWidth={narrow}
        />
        <SearchFilter
          label={t('insurerPayments.filters.exceptionSearch')}
          value={filters.q}
          onChange={(q) => update({ q })}
          fullWidth={narrow}
        />
      </FiltersBar>
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
      <ExceptionDrawer
        exceptionId={exceptionId}
        onClose={() => openException(null)}
        onResolved={() => {
          openException(null);
          setNotice(t('paymentExceptions.resolve.resolved'));
          void queryClient.invalidateQueries({ queryKey: paymentExceptionsKey });
          void queryClient.invalidateQueries({ queryKey: openExceptionsCountKey });
          void queryClient.invalidateQueries({ queryKey: insurerPaymentsKey });
          void queryClient.invalidateQueries({ queryKey: ['insurer-payment'] });
          if (exceptionId) {
            void queryClient.invalidateQueries({ queryKey: ['payment-exception', exceptionId] });
          }
        }}
      />
      <Snackbar
        open={!!notice}
        autoHideDuration={6000}
        onClose={() => setNotice(null)}
        message={notice}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </Stack>
  );
}
