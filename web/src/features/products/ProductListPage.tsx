import Add from '@mui/icons-material/Add';
import EditNote from '@mui/icons-material/EditNote';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import {
  DataGrid,
  type GridColDef,
  type GridColumnVisibilityModel,
  type GridPaginationModel,
} from '@mui/x-data-grid';
import { enUS, ptPT } from '@mui/x-data-grid/locales';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import {
  listInsurerProducts,
  type ListInsurerProductsParams,
  type ProductSummaryResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatDate } from '../../i18n/format';
import { productTypes } from './coverageRules';
import { InsurerLogoCard } from './InsurerLogoCard';
import { ProductCreateDialog } from './ProductCreateDialog';
import { ProductStatusChip } from './ProductStatusChip';
import { productsQueryKey } from './productQueries';

const statuses = ['DRAFT', 'PUBLISHED', 'RETIRED'] as const;

/** Colunas escondidas em ecrãs estreitos (só nome, estado e versão em vigor). */
const narrowHidden: GridColumnVisibilityModel = {
  code: false,
  type: false,
  hasDraft: false,
  updatedAt: false,
};

/** `/seguradora/produtos`: logótipo da seguradora e lista de produtos. */
export function ProductListPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const canManage = usePermission(Permissions.productManage);
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const [status, setStatus] = useState('');
  const [type, setType] = useState('');
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setQ(search.trim());
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const params: ListInsurerProductsParams = {
    status: (status || undefined) as ListInsurerProductsParams['status'],
    type: (type || undefined) as ListInsurerProductsParams['type'],
    q: q || undefined,
    page: pagination.page,
    size: pagination.pageSize,
    sort: 'name,asc',
  };
  const query = useQuery({
    queryKey: [...productsQueryKey, 'list', params],
    queryFn: ({ signal }) => listInsurerProducts(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const columns = useMemo<GridColDef<ProductSummaryResponse>[]>(
    () => [
      { field: 'code', headerName: t('products.columns.code'), minWidth: 130, sortable: false },
      {
        field: 'name',
        headerName: t('products.columns.name'),
        flex: 1,
        minWidth: 140,
        sortable: false,
      },
      {
        field: 'type',
        headerName: t('products.columns.type'),
        minWidth: 160,
        sortable: false,
        valueGetter: (value: string) => t(`productType.${value}`),
      },
      {
        field: 'status',
        headerName: t('products.columns.status'),
        minWidth: 120,
        sortable: false,
        renderCell: ({ value }) => <ProductStatusChip status={value as string} />,
      },
      {
        field: 'currentVersionNo',
        headerName: t('products.columns.currentVersion'),
        minWidth: 90,
        sortable: false,
        valueFormatter: (value: number | undefined) => (value == null ? '—' : String(value)),
      },
      {
        field: 'hasDraft',
        headerName: t('products.columns.hasDraft'),
        minWidth: 120,
        sortable: false,
        renderCell: ({ value }) =>
          value ? (
            <Tooltip title={t('products.columns.hasDraft')}>
              <EditNote aria-label={t('products.columns.hasDraft')} sx={{ mt: 1.5 }} />
            </Tooltip>
          ) : null,
      },
      {
        field: 'updatedAt',
        headerName: t('products.columns.updatedAt'),
        minWidth: 120,
        sortable: false,
        valueFormatter: (value: string) => formatDate(value, i18n.language),
      },
    ],
    [t, i18n.language],
  );

  const localeText = (i18n.language === 'en' ? enUS : ptPT).components.MuiDataGrid.defaultProps
    .localeText;
  const filtered = !!(status || type || q);
  const empty = query.isSuccess && query.data.totalElements === 0 && !filtered;

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
      <option value="">{t('products.filters.all')}</option>
      {options.map((option) => (
        <option key={option.value} value={option.value}>
          {option.label}
        </option>
      ))}
    </TextField>
  );

  const newButton = canManage && (
    <Button variant="contained" startIcon={<Add />} onClick={() => setCreating(true)}>
      {t('products.new')}
    </Button>
  );

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ sm: 'center' }}
      >
        <Typography variant="h4" component="h1">
          {t('products.title')}
        </Typography>
        {!empty && newButton}
      </Stack>
      <InsurerLogoCard />
      {query.isError && (
        <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>
      )}
      {empty ? (
        <Paper variant="outlined" sx={{ p: 4, textAlign: 'center' }}>
          <Stack spacing={2} alignItems="center">
            <Typography>{t('products.empty')}</Typography>
            {newButton}
          </Stack>
        </Paper>
      ) : (
        <>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} useFlexGap flexWrap="wrap">
            {select(
              t('products.filters.status'),
              status,
              setStatus,
              statuses.map((value) => ({ value, label: t(`productStatus.${value}`) })),
            )}
            {select(
              t('products.filters.type'),
              type,
              setType,
              productTypes.map((value) => ({ value, label: t(`productType.${value}`) })),
            )}
            <TextField
              size="small"
              type="search"
              label={t('products.filters.search')}
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              sx={{ minWidth: 220, flexGrow: 1 }}
            />
          </Stack>
          <Box sx={{ width: '100%', minWidth: 0 }}>
            <DataGrid
              aria-label={t('products.title')}
              rows={query.data?.content ?? []}
              rowCount={query.data?.totalElements ?? 0}
              columns={columns}
              columnVisibilityModel={narrow ? narrowHidden : {}}
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
              onRowClick={({ row }) => navigate(`/seguradora/produtos/${row.id}`)}
              sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
            />
          </Box>
        </>
      )}
      {creating && (
        <ProductCreateDialog
          onClose={() => setCreating(false)}
          onCreated={(product) => {
            setCreating(false);
            const draft = product.draftVersion ?? product.versions[0];
            navigate(
              draft
                ? `/seguradora/produtos/${product.id}/versoes/${draft.id}?passo=dados`
                : `/seguradora/produtos/${product.id}`,
            );
          }}
        />
      )}
    </Stack>
  );
}
