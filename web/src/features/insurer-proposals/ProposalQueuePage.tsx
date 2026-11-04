import FilterList from '@mui/icons-material/FilterList';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Checkbox from '@mui/material/Checkbox';
import Drawer from '@mui/material/Drawer';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import ListItemText from '@mui/material/ListItemText';
import MenuItem from '@mui/material/MenuItem';
import Pagination from '@mui/material/Pagination';
import Select from '@mui/material/Select';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import {
  DataGrid,
  type GridColDef,
  type GridPaginationModel,
  type GridSortModel,
} from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Navigate, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import {
  listInsurerProposals,
  ProposalChannel,
  ProposalStatus,
  SlaStatus,
  type InsurerProposalSummaryResponse,
  type ListInsurerProposalsParams,
} from '../../api/generated';
import { noAccessPath } from '../../auth/RequireAuth';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { productTypes } from '../products/coverageRules';
import {
  insurerProposalsKey,
  NavigationNotice,
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
  type QueueNavigationState,
} from './common';
import { ProposalsTabs, proposalsPath, useAllowedProposalTabs } from './ProposalsTabs';
import { SlaChip } from './SlaChip';

const statuses = Object.values(ProposalStatus);
const slaStatuses = Object.values(SlaStatus);
const channels = Object.values(ProposalChannel);
const defaultStatus = ['UNDER_REVIEW'];
const defaultSort = 'submittedAt,asc';
const sortableFields = ['submittedAt', 'slaDueAt'];

/** Filtros da fila, lidos dos parâmetros da URL (para se partilhar a ligação). */
export interface ProposalFilters {
  status: string[];
  sla: string;
  productType: string;
  channel: string;
  q: string;
  from: string;
  to: string;
  page: number;
  size: number;
  sort: string;
}

export function readFilters(params: URLSearchParams): ProposalFilters {
  // `status` ausente → "Em análise"; `status=` (vazio) → todos os estados.
  const status = params.has('status')
    ? params.getAll('status').filter((value) => statuses.includes(value as ProposalStatus))
    : defaultStatus;
  const number = (key: string, fallback: number) => {
    const value = Number(params.get(key));
    return Number.isInteger(value) && value >= 0 && params.get(key) !== null ? value : fallback;
  };
  const sort = params.get('sort') ?? defaultSort;
  return {
    status,
    sla: params.get('sla') ?? '',
    productType: params.get('productType') ?? '',
    channel: params.get('channel') ?? '',
    q: params.get('q') ?? '',
    from: params.get('from') ?? '',
    to: params.get('to') ?? '',
    page: number('page', 0),
    size: [10, 20, 50].includes(number('size', 20)) ? number('size', 20) : 20,
    sort: sortableFields.includes(sort.split(',')[0] ?? '') ? sort : defaultSort,
  };
}

function writeFilters(filters: ProposalFilters): URLSearchParams {
  const params = new URLSearchParams();
  if (filters.status.length === 0) params.set('status', '');
  else if (filters.status.join() !== defaultStatus.join()) {
    filters.status.forEach((value) => params.append('status', value));
  }
  if (filters.sla) params.set('sla', filters.sla);
  if (filters.productType) params.set('productType', filters.productType);
  if (filters.channel) params.set('channel', filters.channel);
  if (filters.q) params.set('q', filters.q);
  if (filters.from) params.set('from', filters.from);
  if (filters.to) params.set('to', filters.to);
  if (filters.page) params.set('page', String(filters.page));
  if (filters.size !== 20) params.set('size', String(filters.size));
  if (filters.sort !== defaultSort) params.set('sort', filters.sort);
  return params;
}

function toParams(filters: ProposalFilters): ListInsurerProposalsParams {
  return {
    status: filters.status.length ? (filters.status as ProposalStatus[]) : undefined,
    slaStatus: (filters.sla || undefined) as ListInsurerProposalsParams['slaStatus'],
    productType: (filters.productType || undefined) as ListInsurerProposalsParams['productType'],
    channel: (filters.channel || undefined) as ListInsurerProposalsParams['channel'],
    q: filters.q || undefined,
    submittedFrom: filters.from || undefined,
    submittedTo: filters.to || undefined,
    page: filters.page,
    size: filters.size,
    sort: filters.sort,
  };
}

/** `/seguradora/propostas`: sem `proposal:read` abre o primeiro separador permitido. */
export function ProposalQueueRoute() {
  const tabs = useAllowedProposalTabs();
  if (tabs.some((tab) => tab.path === proposalsPath)) return <ProposalQueuePage />;
  if (tabs[0]) return <Navigate to={tabs[0].path} replace />;
  return <Navigate to={noAccessPath} replace />;
}

/** Fila das propostas da seguradora, a mais antiga primeiro, com o estado do prazo. */
export function ProposalQueuePage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => readFilters(searchParams), [searchParams]);
  const narrow = useMediaQuery('(max-width:899.95px)', { noSsr: true });
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [search, setSearch] = useState(filters.q);
  const localeText = useGridLocaleText();

  const update = (changes: Partial<ProposalFilters>, resetPage = true) => {
    const next = { ...filters, ...changes, ...(resetPage ? { page: 0 } : {}) };
    setSearchParams(writeFilters(next), { replace: true });
  };

  useEffect(() => {
    const timer = setTimeout(() => {
      if (search.trim() !== filters.q) update({ q: search.trim() });
    }, 300);
    return () => clearTimeout(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search]);

  const params = toParams(filters);
  const query = useQuery({
    queryKey: [...insurerProposalsKey, params],
    queryFn: ({ signal }) =>
      listInsurerProposals(params, { paramsSerializer: { indexes: null } }, signal),
    placeholderData: keepPreviousData,
  });

  const open = (row: InsurerProposalSummaryResponse) =>
    navigate(`/seguradora/propostas/${row.id}`, {
      state: { queueSearch: location.search } satisfies QueueNavigationState,
    });

  const columns = useMemo<GridColDef<InsurerProposalSummaryResponse>[]>(
    () => [
      { field: 'number', headerName: t('insurerProposals.columns.number'), minWidth: 150 },
      {
        field: 'customerName',
        headerName: t('insurerProposals.columns.policyholder'),
        flex: 1,
        minWidth: 160,
      },
      {
        field: 'productName',
        headerName: t('insurerProposals.columns.product'),
        flex: 1,
        minWidth: 140,
      },
      { field: 'vehiclePlate', headerName: t('insurerProposals.columns.plate'), minWidth: 110 },
      {
        field: 'total',
        headerName: t('insurerProposals.columns.total'),
        minWidth: 140,
        align: 'right',
        headerAlign: 'right',
        renderCell: ({ row }) => (
          <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
            {formatMoney(row.total, i18n.language)}
          </Box>
        ),
      },
      {
        field: 'channel',
        headerName: t('insurerProposals.columns.channel'),
        minWidth: 140,
        valueGetter: (_value, row) =>
          row.brokerName
            ? `${t(`proposalChannel.${row.channel}`)} · ${row.brokerName}`
            : t(`proposalChannel.${row.channel}`),
      },
      {
        field: 'submittedAt',
        headerName: t('insurerProposals.columns.submittedAt'),
        minWidth: 150,
        sortable: true,
        valueFormatter: (value: string) => formatDateTime(value, i18n.language),
      },
      {
        field: 'slaDueAt',
        headerName: t('insurerProposals.columns.sla'),
        minWidth: 170,
        sortable: true,
        renderCell: ({ row }) => <SlaChip status={row.slaStatus} dueAt={row.slaDueAt} />,
      },
      {
        field: 'kycState',
        headerName: t('insurerProposals.columns.kyc'),
        minWidth: 130,
        valueGetter: (value: string) => t(`kyc.state.${value}`),
      },
      {
        field: 'inspectionStatus',
        headerName: t('insurerProposals.columns.inspection'),
        minWidth: 130,
        valueGetter: (value: string | undefined) =>
          value ? t(`inspection.status.${value}`) : t('insurerProposals.noInspection'),
      },
    ],
    [t, i18n.language],
  );

  const [sortField, sortDirection] = filters.sort.split(',') as [string, 'asc' | 'desc'];
  const sortModel: GridSortModel = [{ field: sortField, sort: sortDirection }];
  const pagination: GridPaginationModel = { page: filters.page, pageSize: filters.size };
  const rows = query.data?.content ?? [];
  const total = query.data?.totalElements ?? 0;

  const option = (value: string, label: string) => (
    <MenuItem key={value} value={value}>
      {label}
    </MenuItem>
  );
  const single = (
    id: string,
    label: string,
    value: string,
    onChange: (value: string) => void,
    options: { value: string; label: string }[],
  ) => (
    <TextField
      select
      size="small"
      id={id}
      label={label}
      value={value}
      onChange={(event) => onChange(event.target.value)}
      sx={{ minWidth: 170 }}
      fullWidth={narrow}
      slotProps={{ inputLabel: { shrink: true }, select: { displayEmpty: true } }}
    >
      {option('', t('insurerProposals.filters.all'))}
      {options.map((o) => option(o.value, o.label))}
    </TextField>
  );

  const filterFields = (
    <Stack
      direction={narrow ? 'column' : 'row'}
      spacing={2}
      useFlexGap
      flexWrap="wrap"
      sx={{ minWidth: 0 }}
    >
      <FormControl size="small" sx={{ minWidth: 200 }} fullWidth={narrow}>
        <InputLabel id="filter-status-label" shrink>
          {t('insurerProposals.filters.status')}
        </InputLabel>
        <Select
          labelId="filter-status-label"
          id="filter-status"
          multiple
          notched
          displayEmpty
          label={t('insurerProposals.filters.status')}
          value={filters.status}
          onChange={(event) => {
            const value = event.target.value;
            update({ status: typeof value === 'string' ? value.split(',') : value });
          }}
          renderValue={(selected) =>
            selected.length === 0
              ? t('insurerProposals.filters.all')
              : selected.map((s) => t(`proposalStatus.${s}`)).join(', ')
          }
        >
          {statuses.map((value) => (
            <MenuItem key={value} value={value}>
              <Checkbox size="small" checked={filters.status.includes(value)} />
              <ListItemText primary={t(`proposalStatus.${value}`)} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      {single(
        'filter-sla',
        t('insurerProposals.filters.sla'),
        filters.sla,
        (sla) => update({ sla }),
        slaStatuses.map((value) => ({ value, label: t(`slaStatus.${value}`) })),
      )}
      {single(
        'filter-product-type',
        t('insurerProposals.filters.productType'),
        filters.productType,
        (productType) => update({ productType }),
        productTypes.map((value) => ({ value, label: t(`productType.${value}`) })),
      )}
      {single(
        'filter-channel',
        t('insurerProposals.filters.channel'),
        filters.channel,
        (channel) => update({ channel }),
        channels.map((value) => ({ value, label: t(`proposalChannel.${value}`) })),
      )}
      <TextField
        size="small"
        type="search"
        label={t('insurerProposals.filters.search')}
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        sx={{ minWidth: 200, flexGrow: 1 }}
        fullWidth={narrow}
      />
      <TextField
        size="small"
        type="date"
        label={t('insurerProposals.filters.from')}
        value={filters.from}
        onChange={(event) => update({ from: event.target.value })}
        slotProps={{ inputLabel: { shrink: true } }}
        fullWidth={narrow}
      />
      <TextField
        size="small"
        type="date"
        label={t('insurerProposals.filters.to')}
        value={filters.to}
        onChange={(event) => update({ to: event.target.value })}
        slotProps={{ inputLabel: { shrink: true } }}
        fullWidth={narrow}
      />
    </Stack>
  );

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data)
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  else if (total === 0) body = <QueueEmpty>{t('insurerProposals.empty')}</QueueEmpty>;
  else if (narrow) {
    body = (
      <Stack spacing={1.5} component="ul" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        {rows.map((row) => (
          <li key={row.id}>
            <Card variant="outlined">
              <CardActionArea onClick={() => open(row)} data-testid="proposal-card">
                <CardContent>
                  <Stack
                    direction="row"
                    justifyContent="space-between"
                    alignItems="center"
                    spacing={1}
                  >
                    <Typography fontWeight={600}>{row.number}</Typography>
                    <SlaChip status={row.slaStatus} dueAt={row.slaDueAt} />
                  </Stack>
                  <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
                    {row.customerName}
                  </Typography>
                  <Typography variant="body2" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                    {formatMoney(row.total, i18n.language)}
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
          aria-label={t('insurerProposals.title')}
          rows={rows}
          rowCount={total}
          columns={columns.map((column) => ({
            sortable: sortableFields.includes(column.field),
            ...column,
          }))}
          loading={query.isFetching}
          paginationMode="server"
          sortingMode="server"
          paginationModel={pagination}
          onPaginationModelChange={(model) =>
            update({ page: model.page, size: model.pageSize }, model.pageSize !== filters.size)
          }
          sortModel={sortModel}
          onSortModelChange={(model) => {
            const first = model[0];
            update({ sort: first?.sort ? `${first.field},${first.sort}` : defaultSort });
          }}
          sortingOrder={['asc', 'desc']}
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
        {t('insurerProposals.title')}
      </Typography>
      <ProposalsTabs />
      {narrow ? (
        <>
          <div>
            <Button
              variant="outlined"
              startIcon={<FilterList />}
              onClick={() => setDrawerOpen(true)}
            >
              {t('insurerProposals.filters.open')}
            </Button>
          </div>
          <Drawer
            anchor="right"
            open={drawerOpen}
            onClose={() => setDrawerOpen(false)}
            slotProps={{ paper: { sx: { width: 'min(320px, 90vw)', p: 2 } } }}
          >
            <Stack spacing={2} role="region" aria-label={t('insurerProposals.filters.open')}>
              <Typography variant="h6" component="h2">
                {t('insurerProposals.filters.open')}
              </Typography>
              {filterFields}
              <Button variant="outlined" onClick={() => setDrawerOpen(false)}>
                {t('common.close')}
              </Button>
            </Stack>
          </Drawer>
        </>
      ) : (
        filterFields
      )}
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
      <NavigationNotice />
    </Stack>
  );
}
