import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef, type GridColumnVisibilityModel } from '@mui/x-data-grid';
import { enUS, ptPT } from '@mui/x-data-grid/locales';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  activatePlateFormat,
  deactivatePlateFormat,
  listPlateFormats,
  type PlateFormatResponse,
} from '../../api/generated';
import { StatusChip } from '../../components/StatusChip';
import { getProblem, problemMessage } from '../../errors/problem';
import { PlateFormatDialog } from './PlateFormatDialog';
import { referencesQueryKey } from './referenceDomains';
import { RowActions } from './RowActions';

const queryKey = [...referencesQueryKey, 'plateFormats'] as const;
const narrowHidden: GridColumnVisibilityModel = { code: false, pattern: false, sortOrder: false };

/** Separador "Formatos de matrícula". */
export function PlateFormatsTab() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const [editing, setEditing] = useState<PlateFormatResponse | 'new' | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [done, setDone] = useState<string | null>(null);
  const query = useQuery({
    queryKey,
    queryFn: ({ signal }) => listPlateFormats(undefined, signal),
  });

  const refresh = () => queryClient.invalidateQueries({ queryKey });

  const toggle = async (format: PlateFormatResponse) => {
    setMessage(null);
    try {
      if (format.active) await deactivatePlateFormat(format.id);
      else await activatePlateFormat(format.id);
      setDone(format.active ? t('references.deactivated') : t('references.activated'));
      await refresh();
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    }
  };

  const columns = useMemo<GridColDef<PlateFormatResponse>[]>(
    () => [
      {
        field: 'code',
        headerName: t('references.plateColumns.code'),
        minWidth: 120,
        sortable: false,
      },
      {
        field: 'label',
        headerName: t('references.plateColumns.label'),
        flex: 1,
        minWidth: 120,
        sortable: false,
      },
      {
        field: 'example',
        headerName: t('references.plateColumns.example'),
        minWidth: 100,
        sortable: false,
      },
      {
        field: 'pattern',
        headerName: t('references.plateColumns.pattern'),
        flex: 1,
        minWidth: 160,
        sortable: false,
        renderCell: ({ value }) => (
          <Box
            component="span"
            title={value as string}
            sx={{
              fontFamily: 'monospace',
              overflow: 'hidden',
              textOverflow: 'ellipsis',
              whiteSpace: 'nowrap',
              display: 'block',
            }}
          >
            {value as string}
          </Box>
        ),
      },
      {
        field: 'sortOrder',
        headerName: t('references.plateColumns.sortOrder'),
        minWidth: 80,
        sortable: false,
      },
      {
        field: 'active',
        headerName: t('references.plateColumns.status'),
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
            onToggle={() => void toggle(row)}
          />
        ),
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t],
  );

  const localeText = (i18n.language === 'en' ? enUS : ptPT).components.MuiDataGrid.defaultProps
    .localeText;

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <div>
        <Button variant="contained" startIcon={<Add />} onClick={() => setEditing('new')}>
          {t('references.plates.new')}
        </Button>
      </div>
      {(query.isError || message) && (
        <Alert severity="error" role="alert" onClose={message ? () => setMessage(null) : undefined}>
          {message ?? problemMessage(t, getProblem(query.error))}
        </Alert>
      )}
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('references.tabs.matriculas')}
          rows={query.data?.formats ?? []}
          columns={columns}
          columnVisibilityModel={narrow ? narrowHidden : {}}
          loading={query.isFetching}
          disableColumnMenu
          disableColumnFilter
          disableRowSelectionOnClick
          hideFooter
          autoHeight
          localeText={localeText}
        />
      </Box>
      {editing && (
        <PlateFormatDialog
          format={editing === 'new' ? undefined : editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null);
            setDone(t('references.saved'));
            void refresh();
          }}
          onConflict={() => void refresh()}
        />
      )}
      <Snackbar
        open={done !== null}
        autoHideDuration={3000}
        onClose={() => setDone(null)}
        message={done}
      />
    </Stack>
  );
}
