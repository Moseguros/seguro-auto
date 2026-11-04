import Box from '@mui/material/Box';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridPaginationModel } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  InspectionMode,
  InspectionStatus,
  listInsurerInspections,
  type InsurerInspectionSummaryResponse,
  type ListInsurerInspectionsParams,
} from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import {
  insurerInspectionsKey,
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
  type QueueNavigationState,
} from '../insurer-proposals/common';
import { ProposalsTabs } from '../insurer-proposals/ProposalsTabs';

const statuses = Object.values(InspectionStatus);
const modes = Object.values(InspectionMode);

/** Fila das vistorias (por defeito as enviadas), a mais antiga primeiro. */
export function InspectionQueuePage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const localeText = useGridLocaleText();
  const [status, setStatus] = useState<string>('SUBMITTED');
  const [mode, setMode] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });

  const params: ListInsurerInspectionsParams = {
    status: (status || undefined) as ListInsurerInspectionsParams['status'],
    mode: (mode || undefined) as ListInsurerInspectionsParams['mode'],
    page: pagination.page,
    size: pagination.pageSize,
    sort: 'submittedAt,asc',
  };
  const query = useQuery({
    queryKey: [...insurerInspectionsKey, params],
    queryFn: ({ signal }) => listInsurerInspections(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const columns = useMemo<GridColDef<InsurerInspectionSummaryResponse>[]>(
    () => [
      {
        field: 'proposalNumber',
        headerName: t('insurerInspections.columns.proposal'),
        minWidth: 150,
        sortable: false,
      },
      {
        field: 'customerName',
        headerName: t('insurerInspections.columns.customer'),
        flex: 1,
        minWidth: 160,
        sortable: false,
      },
      {
        field: 'vehiclePlate',
        headerName: t('insurerInspections.columns.plate'),
        minWidth: 110,
        sortable: false,
      },
      {
        field: 'mode',
        headerName: t('insurerInspections.columns.mode'),
        minWidth: 170,
        sortable: false,
        valueGetter: (value: string) => t(`inspection.mode.${value}`),
      },
      {
        field: 'photoCount',
        headerName: t('insurerInspections.columns.photos'),
        minWidth: 110,
        type: 'number',
        sortable: false,
      },
      {
        field: 'submittedAt',
        headerName: t('insurerInspections.columns.submittedAt'),
        minWidth: 150,
        sortable: false,
        valueFormatter: (value: string | undefined) => formatDateTime(value, i18n.language),
      },
    ],
    [t, i18n.language],
  );

  const open = (row: InsurerInspectionSummaryResponse) =>
    navigate(`/seguradora/propostas/${row.proposalId}#vistoria`, {
      state: { inspectionId: row.id } satisfies QueueNavigationState,
    });

  const select = (
    label: string,
    value: string,
    onChange: (value: string) => void,
    options: { value: string; label: string }[],
  ) => (
    <TextField
      select
      size="small"
      label={label}
      value={value}
      onChange={(event) => {
        onChange(event.target.value);
        setPagination((p) => ({ ...p, page: 0 }));
      }}
      sx={{ minWidth: 180 }}
      slotProps={{ inputLabel: { shrink: true }, select: { displayEmpty: true } }}
    >
      <MenuItem value="">{t('insurerInspections.filters.all')}</MenuItem>
      {options.map((option) => (
        <MenuItem key={option.value} value={option.value}>
          {option.label}
        </MenuItem>
      ))}
    </TextField>
  );

  const total = query.data?.totalElements ?? 0;
  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data)
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  else if (total === 0) body = <QueueEmpty>{t('insurerInspections.empty')}</QueueEmpty>;
  else
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('insurerInspections.title')}
          rows={query.data?.content ?? []}
          rowCount={total}
          columns={columns}
          columnVisibilityModel={
            narrow ? { mode: false, photoCount: false, vehiclePlate: false } : {}
          }
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
          onRowClick={({ row }) => open(row)}
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
        {select(
          t('insurerInspections.filters.status'),
          status,
          setStatus,
          statuses.map((value) => ({ value, label: t(`inspection.status.${value}`) })),
        )}
        {select(
          t('insurerInspections.filters.mode'),
          mode,
          setMode,
          modes.map((value) => ({ value, label: t(`inspection.mode.${value}`) })),
        )}
      </Stack>
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
    </Stack>
  );
}
