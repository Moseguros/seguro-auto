import Box from '@mui/material/Box';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridPaginationModel } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  KycCheckStatus,
  listInsurerKycChecks,
  type KycCheckSummaryResponse,
  type ListInsurerKycChecksParams,
} from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import {
  insurerKycKey,
  NavigationNotice,
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { ProposalsTabs } from '../insurer-proposals/ProposalsTabs';

const statuses = Object.values(KycCheckStatus);

/** Fila das verificações de identidade enviadas à seguradora (a mais antiga primeiro). */
export function KycQueuePage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const localeText = useGridLocaleText();
  const [status, setStatus] = useState<string>('PENDING');
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });

  useEffect(() => {
    const timer = setTimeout(() => {
      setQ(search.trim());
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const params: ListInsurerKycChecksParams = {
    status: (status || undefined) as ListInsurerKycChecksParams['status'],
    q: q || undefined,
    page: pagination.page,
    size: pagination.pageSize,
    sort: 'submittedAt,asc',
  };
  const query = useQuery({
    queryKey: [...insurerKycKey, params],
    queryFn: ({ signal }) => listInsurerKycChecks(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const columns = useMemo<GridColDef<KycCheckSummaryResponse>[]>(
    () => [
      {
        field: 'fullName',
        headerName: t('insurerKyc.columns.fullName'),
        flex: 1,
        minWidth: 160,
        sortable: false,
      },
      {
        field: 'idDocType',
        headerName: t('insurerKyc.columns.idDocType'),
        minWidth: 170,
        sortable: false,
        valueGetter: (value: string) => t(`idDocType.${value}`),
      },
      {
        field: 'submittedAt',
        headerName: t('insurerKyc.columns.submittedAt'),
        minWidth: 150,
        sortable: false,
        valueFormatter: (value: string) => formatDateTime(value, i18n.language),
      },
      {
        field: 'status',
        headerName: t('insurerKyc.columns.status'),
        minWidth: 120,
        sortable: false,
        valueGetter: (value: string) => t(`kyc.checkStatus.${value}`),
      },
    ],
    [t, i18n.language],
  );

  const total = query.data?.totalElements ?? 0;
  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data)
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  else if (total === 0) body = <QueueEmpty>{t('insurerKyc.empty')}</QueueEmpty>;
  else
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('insurerKyc.title')}
          rows={query.data?.content ?? []}
          rowCount={total}
          columns={columns}
          columnVisibilityModel={narrow ? { idDocType: false, status: false } : {}}
          loading={query.isFetching}
          paginationMode="server"
          paginationModel={pagination}
          onPaginationModelChange={setPagination}
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => navigate(`/seguradora/propostas/kyc/${row.id}`)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    );

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Typography variant="h4" component="h1">
        {t('insurerProposals.title')}
      </Typography>
      <ProposalsTabs />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} useFlexGap flexWrap="wrap">
        <TextField
          select
          size="small"
          label={t('insurerKyc.filters.status')}
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            setPagination((p) => ({ ...p, page: 0 }));
          }}
          sx={{ minWidth: 180 }}
          slotProps={{ inputLabel: { shrink: true }, select: { displayEmpty: true } }}
        >
          <MenuItem value="">{t('insurerKyc.filters.all')}</MenuItem>
          {statuses.map((value) => (
            <MenuItem key={value} value={value}>
              {t(`kyc.checkStatus.${value}`)}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          size="small"
          type="search"
          label={t('insurerKyc.filters.search')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          sx={{ minWidth: 220, flexGrow: 1 }}
        />
      </Stack>
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
      <NavigationNotice />
    </Stack>
  );
}
