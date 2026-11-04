import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridPaginationModel } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  listBrokerProposals,
  ProposalStatus,
  type BrokerProposalSummaryResponse,
  type ProposalChecklist,
} from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { proposalStatusColor } from '../customer-proposals/MyProposalsPage';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { brokerProposalsKey } from '../proposal-wizard/useProposalMutations';
import { proposalPaths } from '../proposal-wizard/wizardSteps';

const statuses = Object.values(ProposalStatus);

/** Itens do `checklist` por fazer (coluna "Falta"). */
export function checklistMissing(checklist: ProposalChecklist): string[] {
  const missing: string[] = [];
  if (checklist.declarations !== 'DONE') missing.push('DECLARATIONS');
  if (checklist.suitability === 'MISSING') missing.push('SUITABILITY');
  if (checklist.suitability === 'ACK_REQUIRED') missing.push('SUITABILITY_ACKNOWLEDGEMENT');
  if (checklist.inspection === 'MISSING' || checklist.inspection === 'REJECTED') {
    missing.push('INSPECTION');
  }
  if (checklist.signature !== 'DONE') missing.push('SIGNATURE');
  return missing;
}

/** `/mediador/propostas`: as propostas da organização do mediador. */
export function BrokerProposalsPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const theme = useTheme();
  const narrow = useMediaQuery(theme.breakpoints.down('md'), { noSsr: true });
  const localeText = useGridLocaleText();
  const [status, setStatus] = useState('');
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });

  // Pesquisa só depois de uma pausa na escrita.
  useEffect(() => {
    const id = setTimeout(() => {
      setQ(search.trim());
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(id);
  }, [search]);

  const params = {
    ...(status ? { status: [status as ProposalStatus] } : {}),
    ...(q ? { q } : {}),
    page: pagination.page,
    size: pagination.pageSize,
    sort: 'createdAt,desc',
  };
  const query = useQuery({
    queryKey: [...brokerProposalsKey, params],
    queryFn: ({ signal }) =>
      listBrokerProposals(params, { paramsSerializer: { indexes: null } }, signal),
    placeholderData: keepPreviousData,
  });

  const missingText = (row: BrokerProposalSummaryResponse) =>
    checklistMissing(row.checklist)
      .map((item) => t(`proposal.missing.${item}`))
      .join(', ');

  const columns = useMemo<GridColDef<BrokerProposalSummaryResponse>[]>(
    () => [
      {
        field: 'number',
        headerName: t('brokerProposals.columns.number'),
        minWidth: 150,
        sortable: false,
      },
      {
        field: 'customerName',
        headerName: t('brokerProposals.columns.customer'),
        flex: 1,
        minWidth: 160,
        sortable: false,
      },
      {
        field: 'customerMsisdnMasked',
        headerName: t('brokerProposals.columns.msisdn'),
        minWidth: 140,
        sortable: false,
      },
      {
        field: 'insurer',
        headerName: t('brokerProposals.columns.insurer'),
        minWidth: 150,
        sortable: false,
        valueGetter: (_, row) => row.insurer?.displayName ?? '',
      },
      {
        field: 'total',
        headerName: t('brokerProposals.columns.total'),
        minWidth: 130,
        sortable: false,
        valueGetter: (_, row) => formatMoney(row.total, i18n.language),
      },
      {
        field: 'status',
        headerName: t('brokerProposals.columns.status'),
        minWidth: 150,
        sortable: false,
        renderCell: ({ row }) => (
          <Chip
            size="small"
            color={proposalStatusColor(row.status)}
            label={t(`proposalStatus.${row.status}`)}
          />
        ),
      },
      {
        field: 'expiresAt',
        headerName: t('brokerProposals.columns.expiresAt'),
        minWidth: 120,
        sortable: false,
        valueFormatter: (value: string | null | undefined) =>
          formatDate(value ?? undefined, i18n.language),
      },
      {
        field: 'missing',
        headerName: t('brokerProposals.columns.missing'),
        flex: 1,
        minWidth: 200,
        sortable: false,
        valueGetter: (_, row) => missingText(row),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, i18n.language],
  );

  const open = (row: BrokerProposalSummaryResponse) =>
    navigate(`${proposalPaths.broker.list}/${row.id}`);

  const rows = query.data?.content ?? [];
  const total = query.data?.totalElements ?? 0;
  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if (total === 0) body = <QueueEmpty>{t('brokerProposals.empty')}</QueueEmpty>;
  else if (narrow) {
    const pages = Math.ceil(total / pagination.pageSize);
    body = (
      <Stack spacing={1.5}>
        {rows.map((row) => {
          const missing = missingText(row);
          return (
            <Card key={row.id} variant="outlined" data-testid="broker-proposal-card">
              <CardActionArea onClick={() => open(row)}>
                <CardContent>
                  <Stack spacing={0.75}>
                    <Stack direction="row" justifyContent="space-between" spacing={1}>
                      <Typography fontWeight={600} sx={{ wordBreak: 'break-word' }}>
                        {row.number}
                      </Typography>
                      <Chip
                        size="small"
                        color={proposalStatusColor(row.status)}
                        label={t(`proposalStatus.${row.status}`)}
                      />
                    </Stack>
                    <Typography sx={{ wordBreak: 'break-word' }}>
                      {row.customerName}
                      {row.customerMsisdnMasked ? ` · ${row.customerMsisdnMasked}` : ''}
                    </Typography>
                    <Typography color="text.secondary">
                      {row.insurer?.displayName} · {formatMoney(row.total, i18n.language)}
                    </Typography>
                    {row.expiresAt && (
                      <Typography variant="body2" color="text.secondary">
                        {t('proposal.list.expiresAt', {
                          date: formatDate(row.expiresAt, i18n.language),
                        })}
                      </Typography>
                    )}
                    {missing && (
                      <Typography variant="body2">
                        {t('brokerProposals.missingValue', { items: missing })}
                      </Typography>
                    )}
                  </Stack>
                </CardContent>
              </CardActionArea>
            </Card>
          );
        })}
        {pages > 1 && (
          <Stack direction="row" spacing={1} justifyContent="center" alignItems="center">
            <Button
              disabled={pagination.page === 0}
              onClick={() => setPagination((p) => ({ ...p, page: p.page - 1 }))}
            >
              {t('brokerProposals.previous')}
            </Button>
            <Typography variant="body2">
              {t('brokerProposals.pageOf', { page: pagination.page + 1, pages })}
            </Typography>
            <Button
              disabled={pagination.page + 1 >= pages}
              onClick={() => setPagination((p) => ({ ...p, page: p.page + 1 }))}
            >
              {t('brokerProposals.next')}
            </Button>
          </Stack>
        )}
      </Stack>
    );
  } else {
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('brokerProposals.title')}
          rows={rows}
          rowCount={total}
          columns={columns}
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
  }

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Typography variant="h4" component="h1">
        {t('brokerProposals.title')}
      </Typography>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
        <TextField
          select
          size="small"
          label={t('brokerProposals.filters.status')}
          value={status}
          onChange={(event) => {
            setStatus(event.target.value);
            setPagination((p) => ({ ...p, page: 0 }));
          }}
          sx={{ minWidth: 200 }}
          slotProps={{ inputLabel: { shrink: true }, select: { displayEmpty: true } }}
        >
          <MenuItem value="">{t('brokerProposals.filters.all')}</MenuItem>
          {statuses.map((value) => (
            <MenuItem key={value} value={value}>
              {t(`proposalStatus.${value}`)}
            </MenuItem>
          ))}
        </TextField>
        <TextField
          size="small"
          type="search"
          label={t('brokerProposals.filters.search')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          sx={{ flex: 1, minWidth: 0 }}
        />
      </Stack>
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
    </Stack>
  );
}
