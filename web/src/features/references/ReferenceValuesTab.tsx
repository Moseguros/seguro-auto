import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import useMediaQuery from '@mui/material/useMediaQuery';
import {
  DataGrid,
  type GridColDef,
  type GridColumnVisibilityModel,
  type GridPaginationModel,
} from '@mui/x-data-grid';
import { enUS, ptPT } from '@mui/x-data-grid/locales';
import { keepPreviousData, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  activateReferenceValue,
  deactivateReferenceValue,
  listAdminReferenceValues,
  type ListAdminReferenceValuesParams,
  type ReferenceValueResponse,
} from '../../api/generated';
import { StatusChip } from '../../components/StatusChip';
import { getProblem, problemMessage } from '../../errors/problem';
import { referencesQueryKey, type ReferenceDomain } from './referenceDomains';
import { ReferenceValueDialog } from './ReferenceValueDialog';
import { RowActions } from './RowActions';

const narrowHidden: GridColumnVisibilityModel = { labelEn: false, sortOrder: false };

/** Separador de uma tabela de referência (Marcas, Modelos ou Categorias de carta). */
export function ReferenceValuesTab({ domain }: { domain: ReferenceDomain }) {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const isModel = domain === 'VEHICLE_MODEL';
  const [active, setActive] = useState('');
  const [make, setMake] = useState('');
  const [search, setSearch] = useState('');
  const [q, setQ] = useState('');
  const [pagination, setPagination] = useState<GridPaginationModel>({ page: 0, pageSize: 20 });
  const [editing, setEditing] = useState<ReferenceValueResponse | 'new' | null>(null);
  const [deactivating, setDeactivating] = useState<ReferenceValueResponse | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [done, setDone] = useState<string | null>(null);

  useEffect(() => {
    const timer = setTimeout(() => {
      setQ(search.trim());
      setPagination((p) => ({ ...p, page: 0 }));
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  const params: ListAdminReferenceValuesParams = {
    active: active === '' ? undefined : active === 'true',
    parentCode: isModel && make ? make : undefined,
    q: q || undefined,
    page: pagination.page,
    size: pagination.pageSize,
    sort: 'sortOrder,asc',
  };
  const query = useQuery({
    queryKey: [...referencesQueryKey, domain, params],
    queryFn: ({ signal }) => listAdminReferenceValues(domain, params, undefined, signal),
    placeholderData: keepPreviousData,
  });
  const makesQuery = useQuery({
    queryKey: [...referencesQueryKey, 'VEHICLE_MAKE', 'all'],
    queryFn: ({ signal }) =>
      listAdminReferenceValues(
        'VEHICLE_MAKE',
        { size: 200, sort: 'labelPt,asc' },
        undefined,
        signal,
      ),
    enabled: isModel,
  });
  const makes = (makesQuery.data?.content ?? []).map((m) => ({ code: m.code, label: m.labelPt }));

  const refresh = () => queryClient.invalidateQueries({ queryKey: referencesQueryKey });

  const toggle = async (value: ReferenceValueResponse) => {
    setDeactivating(null);
    setMessage(null);
    try {
      if (value.active) await deactivateReferenceValue(domain, value.id);
      else await activateReferenceValue(domain, value.id);
      setDone(value.active ? t('references.deactivated') : t('references.activated'));
      await refresh();
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    }
  };

  const columns = useMemo<GridColDef<ReferenceValueResponse>[]>(
    () => [
      { field: 'code', headerName: t('references.columns.code'), minWidth: 130, sortable: false },
      {
        field: 'labelPt',
        headerName: t('references.columns.labelPt'),
        flex: 1,
        minWidth: 120,
        sortable: false,
      },
      {
        field: 'labelEn',
        headerName: t('references.columns.labelEn'),
        flex: 1,
        minWidth: 120,
        sortable: false,
      },
      ...(isModel
        ? [
            {
              field: 'parentCode',
              headerName: t('references.columns.make'),
              minWidth: 110,
              sortable: false,
            } satisfies GridColDef<ReferenceValueResponse>,
          ]
        : []),
      {
        field: 'sortOrder',
        headerName: t('references.columns.sortOrder'),
        minWidth: 80,
        sortable: false,
      },
      {
        field: 'active',
        headerName: t('references.columns.status'),
        minWidth: 120,
        sortable: false,
        renderCell: ({ value }) => <StatusChip status={value ? 'ACTIVE' : 'DISABLED'} />,
      },
      {
        field: 'actions',
        headerName: '',
        width: 56,
        sortable: false,
        renderCell: ({ row }) => (
          <RowActions
            name={row.code}
            active={row.active}
            onEdit={() => setEditing(row)}
            onToggle={() => (row.active ? setDeactivating(row) : void toggle(row))}
          />
        ),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, isModel, domain],
  );

  const localeText = (i18n.language === 'en' ? enUS : ptPT).components.MuiDataGrid.defaultProps
    .localeText;

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        useFlexGap
        flexWrap="wrap"
        alignItems={{ sm: 'center' }}
      >
        <TextField
          select
          size="small"
          label={t('references.filters.status')}
          value={active}
          onChange={(event) => {
            setActive(event.target.value);
            setPagination((p) => ({ ...p, page: 0 }));
          }}
          sx={{ minWidth: 160 }}
          slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
        >
          <option value="">{t('references.filters.all')}</option>
          <option value="true">{t('status.ACTIVE')}</option>
          <option value="false">{t('status.DISABLED')}</option>
        </TextField>
        {isModel && (
          <TextField
            select
            size="small"
            label={t('references.filters.make')}
            value={make}
            onChange={(event) => {
              setMake(event.target.value);
              setPagination((p) => ({ ...p, page: 0 }));
            }}
            sx={{ minWidth: 160 }}
            slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          >
            <option value="">{t('references.filters.all')}</option>
            {makes.map((m) => (
              <option key={m.code} value={m.code}>
                {m.label}
              </option>
            ))}
          </TextField>
        )}
        <TextField
          size="small"
          type="search"
          label={t('references.filters.search')}
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          sx={{ minWidth: 200, flexGrow: 1 }}
        />
        <Button variant="contained" startIcon={<Add />} onClick={() => setEditing('new')}>
          {t('references.values.new')}
        </Button>
      </Stack>
      {(query.isError || message) && (
        <Alert severity="error" role="alert" onClose={message ? () => setMessage(null) : undefined}>
          {message ?? problemMessage(t, getProblem(query.error))}
        </Alert>
      )}
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t(`referenceDomain.${domain}`)}
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
        />
      </Box>
      {editing && (
        <ReferenceValueDialog
          domain={domain}
          value={editing === 'new' ? undefined : editing}
          makes={makes}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null);
            setDone(t('references.saved'));
            void refresh();
          }}
          onConflict={() => void refresh()}
        />
      )}
      <Dialog
        open={!!deactivating}
        onClose={() => setDeactivating(null)}
        aria-labelledby="deactivate-title"
      >
        <DialogTitle id="deactivate-title">
          {t('references.deactivateDialog.title', { name: deactivating?.labelPt ?? '' })}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>{t('references.deactivateDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeactivating(null)}>{t('common.cancel')}</Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => deactivating && void toggle(deactivating)}
          >
            {t('references.deactivate')}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={done !== null}
        autoHideDuration={3000}
        onClose={() => setDone(null)}
        message={done}
      />
    </Stack>
  );
}
