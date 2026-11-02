import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import {
  DataGrid,
  type GridColDef,
  type GridPaginationModel,
  type GridSortModel,
} from '@mui/x-data-grid';
import { enUS, ptPT } from '@mui/x-data-grid/locales';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  listOrganizations,
  type ListOrganizationsParams,
  type OrganizationSummaryResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { StatusChip } from '../../components/StatusChip';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatDate } from '../../i18n/format';
import { organizationTypes } from './organizationSchema';

const statuses = ['ACTIVE', 'SUSPENDED'] as const;

/** `/admin/organizacoes`: lista com filtros e paginação no servidor. */
export function OrganizationListPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const canManage = usePermission(Permissions.organizationManage);
  const [type, setType] = useState('');
  const [status, setStatus] = useState('');
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });
  const [sortModel, setSortModel] = useState<GridSortModel>([{ field: 'createdAt', sort: 'desc' }]);

  // A pesquisa só parte quando se pára de escrever.
  useEffect(() => {
    const timer = setTimeout(() => {
      setQ(search.trim());
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const sort = sortModel[0]
    ? `${sortModel[0].field === 'displayName' ? 'legalName' : 'createdAt'},${sortModel[0].sort ?? 'desc'}`
    : 'createdAt,desc';
  const params: ListOrganizationsParams = {
    type: (type || undefined) as ListOrganizationsParams['type'],
    status: (status || undefined) as ListOrganizationsParams['status'],
    q: q || undefined,
    page: pagination.page,
    size: pagination.pageSize,
    sort,
  };
  const query = useQuery({
    queryKey: ['organizations', 'list', params],
    queryFn: ({ signal }) => listOrganizations(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const columns = useMemo<GridColDef<OrganizationSummaryResponse>[]>(
    () => [
      {
        field: 'displayName',
        headerName: t('organizations.columns.name'),
        flex: 1,
        minWidth: 180,
        renderCell: ({ row }) => (
          <Link component={RouterLink} to={`/admin/organizacoes/${row.id}`}>
            {row.displayName}
          </Link>
        ),
      },
      {
        field: 'type',
        headerName: t('organizations.columns.type'),
        minWidth: 150,
        sortable: false,
        valueGetter: (value: string) => t(`orgType.${value}`),
      },
      {
        field: 'nuit',
        headerName: t('organizations.columns.nuit'),
        minWidth: 110,
        sortable: false,
      },
      {
        field: 'status',
        headerName: t('organizations.columns.status'),
        minWidth: 120,
        sortable: false,
        renderCell: ({ value }) => <StatusChip status={value as string} />,
      },
      {
        field: 'createdAt',
        headerName: t('organizations.columns.createdAt'),
        minWidth: 120,
        valueFormatter: (value: string) => formatDate(value, i18n.language),
      },
    ],
    [t, i18n.language],
  );

  const localeText = (i18n.language === 'en' ? enUS : ptPT).components.MuiDataGrid.defaultProps
    .localeText;

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
      slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
    >
      <option value="">{t('organizations.filters.all')}</option>
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </TextField>
  );

  return (
    <Stack spacing={3}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ sm: 'center' }}
      >
        <Typography variant="h4" component="h1">
          {t('organizations.title')}
        </Typography>
        {canManage && (
          <Button
            variant="contained"
            startIcon={<Add />}
            component={RouterLink}
            to="/admin/organizacoes/nova"
          >
            {t('organizations.new')}
          </Button>
        )}
      </Stack>
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} useFlexGap flexWrap="wrap">
        {select(
          t('organizations.filters.type'),
          type,
          setType,
          organizationTypes.map((value) => ({ value, label: t(`orgType.${value}`) })),
        )}
        {select(
          t('organizations.filters.status'),
          status,
          setStatus,
          statuses.map((value) => ({ value, label: t(`status.${value}`) })),
        )}
        <TextField
          size="small"
          type="search"
          label={t('organizations.filters.search')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          sx={{ minWidth: 220, flexGrow: 1 }}
        />
      </Stack>
      {query.isError && (
        <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>
      )}
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('organizations.title')}
          rows={query.data?.content ?? []}
          rowCount={query.data?.totalElements ?? 0}
          columns={columns}
          loading={query.isFetching}
          paginationMode="server"
          sortingMode="server"
          paginationModel={pagination}
          onPaginationModelChange={setPagination}
          sortModel={sortModel}
          onSortModelChange={setSortModel}
          pageSizeOptions={[10, 20, 50]}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => navigate(`/admin/organizacoes/${row.id}`)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    </Stack>
  );
}
