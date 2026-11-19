import Download from '@mui/icons-material/Download';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridSortModel } from '@mui/x-data-grid';
import { keepPreviousData, useInfiniteQuery, useMutation, useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  exportInsurerPayments,
  listInsurerPayments,
  type InsurerPaymentSummary,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { saveBlob } from '../../files/saveBlob';
import { formatMoney } from '../../format/money';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { readBlobProblem } from './blobProblem';
import { insurerPaymentsKey, paymentsPath, PaymentStatusChip, useCodeText } from './common';
import { formatStamp } from './format';
import {
  PaymentFilters,
  paymentQueryParams,
  paymentsDefaultSort,
  readPaymentFilters,
  writePaymentFilters,
  type PaymentListFilters,
} from './PaymentFilters';

const arrays = { paramsSerializer: { indexes: null } };

/** Nome do ficheiro exportado: `pagamentos-{de}-{até}.csv` (como o do backend). */
export function exportFileName(filters: Pick<PaymentListFilters, 'from' | 'to'>): string {
  if (!filters.from && !filters.to) return 'pagamentos.csv';
  return `pagamentos-${filters.from || 'inicio'}-${filters.to || 'hoje'}.csv`;
}

/** Separador "Pagamentos" (S5-12 §3): lista com filtros na query string e exportação CSV. */
export function PaymentsListPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const codeText = useCodeText();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => readPaymentFilters(searchParams), [searchParams]);
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const wide = useMediaQuery('(min-width:1200px)', { noSsr: true });
  const localeText = useGridLocaleText();

  const update = (changes: Partial<PaymentListFilters>, resetPage = true) => {
    const next = { ...filters, ...changes, ...(resetPage ? { page: 0 } : {}) };
    setSearchParams(writePaymentFilters(next), { replace: true });
  };
  const base = paymentQueryParams(filters);
  const pageParams = { ...base, page: filters.page, size: filters.size, sort: filters.sort };

  const query = useQuery({
    queryKey: [...insurerPaymentsKey, 'page', pageParams],
    queryFn: ({ signal }) => listInsurerPayments(pageParams, arrays, signal),
    placeholderData: keepPreviousData,
    enabled: !narrow,
  });
  const cards = useInfiniteQuery({
    queryKey: [...insurerPaymentsKey, 'cards', base, filters.sort],
    queryFn: ({ pageParam, signal }) =>
      listInsurerPayments(
        { ...base, page: pageParam, size: 20, sort: filters.sort },
        arrays,
        signal,
      ),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
    enabled: narrow,
  });

  const exportCsv = useMutation({
    mutationFn: async () => {
      try {
        return await exportInsurerPayments(base, arrays);
      } catch (error) {
        throw await readBlobProblem(error);
      }
    },
    onSuccess: (blob) => saveBlob(blob, exportFileName(filters)),
  });
  const exportProblem = exportCsv.error ? getProblem(exportCsv.error) : undefined;

  const open = (row: InsurerPaymentSummary) => navigate(`${paymentsPath}/${row.id}`);

  const columns = useMemo<GridColDef<InsurerPaymentSummary>[]>(
    () => [
      {
        field: 'createdAt',
        headerName: t('insurerPayments.columns.date'),
        minWidth: 150,
        sortable: true,
        valueFormatter: (value: string) => formatStamp(value),
      },
      {
        field: 'reference',
        headerName: t('insurerPayments.columns.reference'),
        minWidth: 160,
        sortable: false,
      },
      {
        field: 'targetLabel',
        headerName: t('insurerPayments.columns.proposal'),
        minWidth: 150,
        flex: 1,
        sortable: false,
      },
      {
        field: 'method',
        headerName: t('insurerPayments.columns.method'),
        minWidth: 150,
        sortable: false,
        valueGetter: (value: string) => t(`paymentMethod.${value}`),
      },
      {
        field: 'amount',
        headerName: t('insurerPayments.columns.amount'),
        minWidth: 140,
        sortable: true,
        align: 'right',
        headerAlign: 'right',
        renderCell: ({ row }) => (
          <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
            {formatMoney(row.amount, i18n.language)}
          </Box>
        ),
      },
      {
        field: 'status',
        headerName: t('insurerPayments.columns.status'),
        minWidth: 130,
        sortable: false,
        renderCell: ({ row }) => <PaymentStatusChip status={row.status} />,
      },
      {
        field: 'providerTxId',
        headerName: t('insurerPayments.columns.providerTxId'),
        minWidth: 160,
        sortable: false,
      },
      {
        field: 'failureCode',
        headerName: t('insurerPayments.columns.failure'),
        minWidth: 180,
        flex: 1,
        sortable: false,
        valueGetter: (value: string | undefined) => codeText('paymentFailure', value),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, i18n.language],
  );

  const [sortField, sortDirection] = filters.sort.split(',') as [string, 'asc' | 'desc'];
  const sortModel: GridSortModel = [{ field: sortField, sort: sortDirection }];

  let body;
  if (narrow) {
    const rows = cards.data?.pages.flatMap((p) => p.content) ?? [];
    if (cards.isLoading) body = <QueueSkeleton />;
    else if (cards.isError && !cards.data) {
      body = <QueueError error={cards.error} onRetry={() => void cards.refetch()} />;
    } else if (rows.length === 0) body = <QueueEmpty>{t('insurerPayments.empty')}</QueueEmpty>;
    else {
      body = (
        <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
          {rows.map((row) => (
            <li key={row.id}>
              <Card variant="outlined">
                <CardActionArea onClick={() => open(row)} data-testid="payment-card">
                  <CardContent>
                    <Stack
                      direction="row"
                      justifyContent="space-between"
                      alignItems="center"
                      spacing={1}
                    >
                      <Typography fontWeight={600} sx={{ wordBreak: 'break-all' }}>
                        {row.reference}
                      </Typography>
                      <PaymentStatusChip status={row.status} />
                    </Stack>
                    <Typography variant="body2">{row.targetLabel}</Typography>
                    <Typography variant="body2" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                      {formatMoney(row.amount, i18n.language)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {formatStamp(row.createdAt)}
                    </Typography>
                  </CardContent>
                </CardActionArea>
              </Card>
            </li>
          ))}
          {cards.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void cards.fetchNextPage()}
              disabled={cards.isFetchingNextPage}
              sx={{ alignSelf: 'center' }}
            >
              {t('insurerPayments.loadMore')}
            </Button>
          )}
        </Stack>
      );
    }
  } else if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if ((query.data?.totalElements ?? 0) === 0) {
    body = <QueueEmpty>{t('insurerPayments.empty')}</QueueEmpty>;
  } else {
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('insurerPayments.tabs.payments')}
          rows={query.data?.content ?? []}
          rowCount={query.data?.totalElements ?? 0}
          columns={columns}
          columnVisibilityModel={{ providerTxId: wide }}
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
            update({
              sort: first?.sort ? `${first.field},${first.sort}` : paymentsDefaultSort,
            });
          }}
          sortingOrder={['desc', 'asc']}
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => open(row)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    );
  }

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <PaymentFilters filters={filters} narrow={narrow} onChange={(changes) => update(changes)} />
      <div>
        <Button
          variant="outlined"
          startIcon={<Download />}
          onClick={() => exportCsv.mutate()}
          disabled={exportCsv.isPending}
        >
          {t('insurerPayments.export')}
        </Button>
      </div>
      {exportProblem && (
        <Alert severity="error" role="alert" onClose={() => exportCsv.reset()}>
          {exportProblem.code === 'EXPORT_TOO_LARGE'
            ? t('insurerPayments.exportTooLarge')
            : problemMessage(t, exportProblem)}
        </Alert>
      )}
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
    </Stack>
  );
}
