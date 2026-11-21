import Box from '@mui/material/Box';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridSortModel } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { listInsurerPolicies, type InsurerPolicySummaryResponse } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDate, formatDateTime } from '../../i18n/format';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { PolicyStatusChip } from '../policies/common/PolicyStatusChip';
import {
  policiesDefaultSort,
  PolicyFilters,
  policyQueryParams,
  readPolicyFilters,
  writePolicyFilters,
  type PolicyListFilters,
} from './PolicyFilters';

/** Chave das consultas das apólices da seguradora. */
export const insurerPoliciesKey = ['insurer-policies'] as const;
export const insurerPoliciesPath = '/seguradora/apolices';

const arrays = { paramsSerializer: { indexes: null } };

/** Colunas visíveis abaixo de 900 px: só n.º, matrícula e estado. */
const narrowColumns = ['number', 'plate', 'status'];

/** `/seguradora/apolices` (S6-05 §6): lista com filtros na URL, paginação e ordenação no servidor. */
export function InsurerPolicyListPage() {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => readPolicyFilters(searchParams), [searchParams]);
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const compact = useMediaQuery('(max-width:899.95px)', { noSsr: true });
  const localeText = useGridLocaleText();

  const update = (changes: Partial<PolicyListFilters>, resetPage = true) => {
    const next = { ...filters, ...changes, ...(resetPage ? { page: 0 } : {}) };
    setSearchParams(writePolicyFilters(next), { replace: true });
  };
  const params = policyQueryParams(filters);

  const query = useQuery({
    queryKey: [...insurerPoliciesKey, params],
    queryFn: ({ signal }) => listInsurerPolicies(params, arrays, signal),
    placeholderData: keepPreviousData,
  });

  const columns = useMemo<GridColDef<InsurerPolicySummaryResponse>[]>(
    () => [
      {
        field: 'number',
        headerName: t('insurerPolicies.columns.number'),
        minWidth: 150,
        sortable: false,
      },
      {
        field: 'policyholderName',
        headerName: t('insurerPolicies.columns.policyholder'),
        minWidth: 180,
        flex: 1,
        sortable: false,
      },
      {
        field: 'plate',
        headerName: t('insurerPolicies.columns.plate'),
        minWidth: 110,
        sortable: false,
      },
      {
        field: 'productName',
        headerName: t('insurerPolicies.columns.product'),
        minWidth: 150,
        flex: 1,
        sortable: false,
      },
      {
        field: 'coverageType',
        headerName: t('insurerPolicies.columns.coverageType'),
        minWidth: 170,
        sortable: false,
        valueGetter: (value: string) => t(`coverageType.${value}`),
      },
      {
        field: 'status',
        headerName: t('insurerPolicies.columns.status'),
        minWidth: 120,
        sortable: false,
        renderCell: ({ row }) => <PolicyStatusChip status={row.status} />,
      },
      {
        field: 'startAt',
        headerName: t('insurerPolicies.columns.startAt'),
        minWidth: 100,
        sortable: true,
        valueFormatter: (value: string) => formatDate(value, language),
      },
      {
        field: 'endAt',
        headerName: t('insurerPolicies.columns.endAt'),
        minWidth: 100,
        sortable: true,
        valueFormatter: (value: string) => formatDate(value, language),
      },
      {
        field: 'totalPremium',
        headerName: t('insurerPolicies.columns.premium'),
        minWidth: 130,
        sortable: false,
        align: 'right',
        headerAlign: 'right',
        renderCell: ({ row }) => (
          <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
            {formatMoney(row.totalPremium, language)}
          </Box>
        ),
      },
      {
        field: 'issuedAt',
        headerName: t('insurerPolicies.columns.issuedAt'),
        minWidth: 140,
        sortable: true,
        valueFormatter: (value: string) => formatDateTime(value, language),
      },
    ],
    [t, language],
  );

  const visibility = useMemo(
    () =>
      compact
        ? Object.fromEntries(columns.map((c) => [c.field, narrowColumns.includes(c.field)]))
        : {},
    [compact, columns],
  );

  const [sortField, sortDirection] = filters.sort.split(',') as [string, 'asc' | 'desc'];
  const sortModel: GridSortModel = [{ field: sortField, sort: sortDirection }];

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if ((query.data?.totalElements ?? 0) === 0) {
    body = <QueueEmpty>{t('insurerPolicies.empty')}</QueueEmpty>;
  } else {
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('insurerPolicies.title')}
          rows={query.data?.content ?? []}
          rowCount={query.data?.totalElements ?? 0}
          columns={columns}
          columnVisibilityModel={visibility}
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
              sort: first?.sort ? `${first.field},${first.sort}` : policiesDefaultSort,
            });
          }}
          sortingOrder={['desc', 'asc']}
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => navigate(`${insurerPoliciesPath}/${row.id}`)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    );
  }

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <Typography variant="h5" component="h1">
        {t('insurerPolicies.title')}
      </Typography>
      <PolicyFilters filters={filters} narrow={narrow} onChange={(changes) => update(changes)} />
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
    </Stack>
  );
}
