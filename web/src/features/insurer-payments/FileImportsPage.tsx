import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Link from '@mui/material/Link';
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useSearchParams } from 'react-router-dom';
import {
  listPaymentFileImports,
  type ListPaymentFileImportsParams,
  type PaymentFileImport,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import { FileSourceStatusPanel } from '../insurer-payment-settings/FileSourceStatusPanel';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { exceptionsPath, paymentFilesKey, useCodeText } from './common';
import { formatStamp } from './format';
import { DateFilter, FiltersBar, SingleSelectFilter } from './PaymentFilters';

/** Ligação para o separador "Excepções" filtrado pelo ficheiro (todas, resolvidas ou não). */
export function fileExceptionsLink(fileName: string): string {
  return `${exceptionsPath}?status=&q=${encodeURIComponent(fileName)}`;
}

function FileStatusChip({ status }: { status: string }) {
  const { t } = useTranslation();
  return (
    <Chip
      size="small"
      variant="outlined"
      color={status === 'REJECTED' ? 'error' : 'success'}
      label={t(`fileImportStatus.${status}`)}
      data-status={status}
    />
  );
}

/** Separador "Ficheiros" (S5-12 §5): estado da leitura e os ficheiros do banco recebidos. */
export function FileImportsPage() {
  const { t, i18n } = useTranslation();
  const codeText = useCodeText();
  const [searchParams, setSearchParams] = useSearchParams();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const localeText = useGridLocaleText();
  const status = ['PROCESSED', 'REJECTED'].includes(searchParams.get('status') ?? '')
    ? (searchParams.get('status') as string)
    : '';
  const from = searchParams.get('from') ?? '';
  const to = searchParams.get('to') ?? '';
  const page = Math.max(0, Number(searchParams.get('page')) || 0);
  const size = [10, 20, 50].includes(Number(searchParams.get('size')))
    ? Number(searchParams.get('size'))
    : 20;

  const update = (changes: Record<string, string | number>, resetPage = true) => {
    const next = { status, from, to, page, size, ...changes, ...(resetPage ? { page: 0 } : {}) };
    const params = new URLSearchParams();
    if (next.status) params.set('status', String(next.status));
    if (next.from) params.set('from', String(next.from));
    if (next.to) params.set('to', String(next.to));
    if (next.page) params.set('page', String(next.page));
    if (next.size !== 20) params.set('size', String(next.size));
    setSearchParams(params, { replace: true });
  };

  const params: ListPaymentFileImportsParams = {
    status: status || undefined,
    from: from || undefined,
    to: to || undefined,
    page,
    size,
    sort: 'occurredAt,desc',
  };
  const query = useQuery({
    queryKey: [...paymentFilesKey, params],
    queryFn: ({ signal }) => listPaymentFileImports(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const money = (value: PaymentFileImport['totalAmount']) =>
    value ? formatMoney(value, i18n.language) : '';
  const reasonText = (row: PaymentFileImport) => {
    if (!row.reason) return '';
    const reason = codeText('fileRejectionReason', row.reason);
    return row.lineNo !== undefined
      ? `${reason} (${t('paymentFiles.line', { line: row.lineNo })})`
      : reason;
  };
  const exceptionsCell = (row: PaymentFileImport) =>
    row.exceptionCount > 0 ? (
      <Link
        component={RouterLink}
        to={fileExceptionsLink(row.fileName)}
        aria-label={t('paymentFiles.openExceptions', { file: row.fileName })}
        onClick={(event) => event.stopPropagation()}
      >
        {row.exceptionCount}
      </Link>
    ) : (
      String(row.exceptionCount)
    );

  const numeric = { align: 'right', headerAlign: 'right', sortable: false } as const;
  const columns = useMemo<GridColDef<PaymentFileImport>[]>(
    () => [
      {
        field: 'occurredAt',
        headerName: t('paymentFiles.columns.date'),
        minWidth: 150,
        sortable: false,
        valueFormatter: (value: string) => formatStamp(value),
      },
      {
        field: 'fileName',
        headerName: t('paymentFiles.columns.file'),
        minWidth: 200,
        flex: 1,
        sortable: false,
      },
      {
        field: 'fileFormat',
        headerName: t('paymentFiles.columns.format'),
        minWidth: 90,
        sortable: false,
      },
      {
        field: 'formatFileId',
        headerName: t('paymentFiles.columns.fileId'),
        minWidth: 150,
        sortable: false,
      },
      {
        field: 'status',
        headerName: t('paymentFiles.columns.status'),
        minWidth: 130,
        sortable: false,
        renderCell: ({ row }) => <FileStatusChip status={row.status} />,
      },
      {
        field: 'recordCount',
        headerName: t('paymentFiles.columns.records'),
        minWidth: 100,
        ...numeric,
      },
      {
        field: 'totalAmount',
        headerName: t('paymentFiles.columns.total'),
        minWidth: 140,
        ...numeric,
        valueGetter: (_value, row) => money(row.totalAmount),
      },
      {
        field: 'totalFees',
        headerName: t('paymentFiles.columns.fees'),
        minWidth: 150,
        ...numeric,
        valueGetter: (_value, row) => money(row.totalFees),
      },
      {
        field: 'confirmedCount',
        headerName: t('paymentFiles.columns.confirmed'),
        minWidth: 110,
        ...numeric,
      },
      {
        field: 'exceptionCount',
        headerName: t('paymentFiles.columns.exceptions'),
        minWidth: 110,
        ...numeric,
        renderCell: ({ row }) => exceptionsCell(row),
      },
      {
        field: 'reason',
        headerName: t('paymentFiles.columns.reason'),
        minWidth: 220,
        flex: 1,
        sortable: false,
        valueGetter: (_value, row) => reasonText(row),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, i18n.language],
  );

  const rows = query.data?.content ?? [];
  const total = query.data?.totalElements ?? 0;

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if (total === 0) body = <QueueEmpty>{t('paymentFiles.empty')}</QueueEmpty>;
  else if (narrow) {
    body = (
      <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {rows.map((row) => (
          <li key={row.id}>
            <Card variant="outlined" data-testid="file-card">
              <CardContent>
                <Stack direction="row" justifyContent="space-between" spacing={1}>
                  <Typography fontWeight={600} sx={{ wordBreak: 'break-all' }}>
                    {row.fileName}
                  </Typography>
                  <FileStatusChip status={row.status} />
                </Stack>
                <Typography variant="body2">
                  {row.fileFormat}
                  {row.formatFileId && ` · ${row.formatFileId}`}
                </Typography>
                {row.status === 'PROCESSED' && (
                  <Typography variant="body2" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                    {row.recordCount} · {money(row.totalAmount)} · {row.confirmedCount} /{' '}
                    {exceptionsCell(row)}
                  </Typography>
                )}
                {row.reason && (
                  <Typography variant="body2" color="error">
                    {reasonText(row)}
                  </Typography>
                )}
                <Typography variant="caption" color="text.secondary">
                  {formatStamp(row.occurredAt)}
                </Typography>
              </CardContent>
            </Card>
          </li>
        ))}
        {total > size && (
          <Pagination
            count={Math.ceil(total / size)}
            page={page + 1}
            onChange={(_event, next) => update({ page: next - 1 }, false)}
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
          aria-label={t('insurerPayments.tabs.files')}
          // Poucas linhas e muitas colunas: todas desenhadas (a ligação das excepções fica à mão).
          disableVirtualization
          rows={rows}
          rowCount={total}
          columns={columns}
          loading={query.isFetching}
          paginationMode="server"
          paginationModel={{ page, pageSize: size }}
          onPaginationModelChange={(model) =>
            update({ page: model.page, size: model.pageSize }, model.pageSize !== size)
          }
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          getRowClassName={({ row }) => (row.status === 'REJECTED' ? 'file-rejected' : '')}
          sx={{ '& .file-rejected': { color: 'error.main' } }}
        />
      </Box>
    );
  }

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <FileSourceStatusPanel placement="files" />
      <FiltersBar narrow={narrow}>
        <SingleSelectFilter
          id="file-filter-status"
          label={t('paymentFiles.statusFilter')}
          value={status}
          options={['PROCESSED', 'REJECTED'].map((v) => ({
            value: v,
            label: t(`fileImportStatus.${v}`),
          }))}
          allLabel={t('insurerPayments.filters.all')}
          onChange={(value) => update({ status: value })}
          fullWidth={narrow}
        />
        <DateFilter
          label={t('insurerPayments.filters.from')}
          value={from}
          onChange={(value) => update({ from: value })}
          fullWidth={narrow}
        />
        <DateFilter
          label={t('insurerPayments.filters.to')}
          value={to}
          onChange={(value) => update({ to: value })}
          fullWidth={narrow}
        />
      </FiltersBar>
      {body}
    </Stack>
  );
}
