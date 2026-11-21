import CheckCircle from '@mui/icons-material/CheckCircle';
import RemoveCircleOutline from '@mui/icons-material/RemoveCircleOutline';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  listNotificationTemplates,
  NotificationChannel,
  type ListNotificationTemplatesParams,
  type NotificationTemplateSummary,
} from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import { FiltersBar, SearchFilter, SingleSelectFilter } from '../insurer-payments/PaymentFilters';
import {
  QueueEmpty,
  QueueError,
  QueueSkeleton,
  useGridLocaleText,
} from '../insurer-proposals/common';
import { templatesKey, templatesPath } from './TemplateEditorPage';

const channels = Object.values(NotificationChannel);
const locales = ['pt', 'en'];
const pageSizes = [20, 50, 100];

/** Filtros da lista dos modelos, lidos da query string. */
export interface TemplateListFilters {
  code: string;
  channel: string;
  locale: string;
  customized: boolean;
  page: number;
  size: number;
}

function number(params: URLSearchParams, key: string, fallback: number): number {
  const raw = params.get(key);
  const value = Number(raw);
  return raw !== null && Number.isInteger(value) && value >= 0 ? value : fallback;
}

export function readTemplateFilters(params: URLSearchParams): TemplateListFilters {
  const channel = params.get('channel') ?? '';
  const locale = params.get('locale') ?? '';
  const size = number(params, 'size', 50);
  return {
    code: params.get('code') ?? '',
    channel: channels.includes(channel as NotificationChannel) ? channel : '',
    locale: locales.includes(locale) ? locale : '',
    customized: params.get('customized') === 'true',
    page: number(params, 'page', 0),
    size: pageSizes.includes(size) ? size : 50,
  };
}

function writeTemplateFilters(filters: TemplateListFilters): URLSearchParams {
  const params = new URLSearchParams();
  if (filters.code) params.set('code', filters.code);
  if (filters.channel) params.set('channel', filters.channel);
  if (filters.locale) params.set('locale', filters.locale);
  if (filters.customized) params.set('customized', 'true');
  if (filters.page) params.set('page', String(filters.page));
  if (filters.size !== 50) params.set('size', String(filters.size));
  return params;
}

/** `/admin/notificacoes` (S6-05 §7): lista dos modelos com filtros. */
export function TemplateListPage() {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const filters = useMemo(() => readTemplateFilters(searchParams), [searchParams]);
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const compact = useMediaQuery('(max-width:899.95px)', { noSsr: true });
  const localeText = useGridLocaleText();

  const update = (changes: Partial<TemplateListFilters>, resetPage = true) => {
    const next = { ...filters, ...changes, ...(resetPage ? { page: 0 } : {}) };
    setSearchParams(writeTemplateFilters(next), { replace: true });
  };
  const params: ListNotificationTemplatesParams = {
    code: filters.code || undefined,
    channel: (filters.channel || undefined) as NotificationChannel | undefined,
    locale: filters.locale || undefined,
    customized: filters.customized || undefined,
    page: filters.page,
    size: filters.size,
  };
  const query = useQuery({
    queryKey: [...templatesKey, params],
    queryFn: ({ signal }) => listNotificationTemplates(params, undefined, signal),
    placeholderData: keepPreviousData,
  });

  const localeLabel = (value: string) =>
    t(`notificationTemplates.locale.${value}`, { defaultValue: value });

  const columns = useMemo<GridColDef<NotificationTemplateSummary>[]>(
    () => [
      {
        field: 'code',
        headerName: t('notificationTemplates.columns.code'),
        minWidth: 200,
        flex: 1,
        sortable: false,
      },
      {
        field: 'channel',
        headerName: t('notificationTemplates.columns.channel'),
        minWidth: 150,
        sortable: false,
        valueGetter: (value: string) => t(`channel.${value}`),
      },
      {
        field: 'locale',
        headerName: t('notificationTemplates.columns.locale'),
        minWidth: 110,
        sortable: false,
        valueGetter: (value: string) => localeLabel(value),
      },
      {
        field: 'subject',
        headerName: t('notificationTemplates.columns.subject'),
        minWidth: 200,
        flex: 1,
        sortable: false,
      },
      {
        field: 'active',
        headerName: t('notificationTemplates.columns.active'),
        minWidth: 80,
        sortable: false,
        renderCell: ({ row }) =>
          row.active ? (
            <CheckCircle
              color="success"
              fontSize="small"
              titleAccess={t('notificationTemplates.active')}
              sx={{ verticalAlign: 'middle' }}
            />
          ) : (
            <RemoveCircleOutline
              color="disabled"
              fontSize="small"
              titleAccess={t('notificationTemplates.inactive')}
              sx={{ verticalAlign: 'middle' }}
            />
          ),
      },
      {
        field: 'customized',
        headerName: t('notificationTemplates.columns.customized'),
        minWidth: 130,
        sortable: false,
        renderCell: ({ row }) =>
          row.customized ? (
            <Chip size="small" color="info" label={t('notificationTemplates.customized')} />
          ) : null,
      },
      {
        field: 'updatedAt',
        headerName: t('notificationTemplates.columns.updatedAt'),
        minWidth: 200,
        sortable: false,
        valueGetter: (_: unknown, row: NotificationTemplateSummary) =>
          `${formatDateTime(row.updatedAt, language)} · ${row.updatedBy}`,
      },
    ],
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [t, language],
  );

  const visibility: Record<string, boolean> = compact
    ? { subject: false, active: false, updatedAt: false, customized: false }
    : {};

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError && !query.data) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else if ((query.data?.totalElements ?? 0) === 0) {
    body = <QueueEmpty>{t('notificationTemplates.empty')}</QueueEmpty>;
  } else {
    body = (
      <Box sx={{ width: '100%', minWidth: 0 }}>
        <DataGrid
          aria-label={t('notificationTemplates.title')}
          rows={query.data?.content ?? []}
          rowCount={query.data?.totalElements ?? 0}
          columns={columns}
          columnVisibilityModel={visibility}
          loading={query.isFetching}
          paginationMode="server"
          paginationModel={{ page: filters.page, pageSize: filters.size }}
          onPaginationModelChange={(model) =>
            update({ page: model.page, size: model.pageSize }, model.pageSize !== filters.size)
          }
          pageSizeOptions={pageSizes}
          disableColumnMenu
          disableColumnFilter
          disableColumnSorting
          disableRowSelectionOnClick
          autoHeight
          localeText={localeText}
          onRowClick={({ row }) => navigate(`${templatesPath}/${row.id}`)}
          sx={{ '& .MuiDataGrid-row': { cursor: 'pointer' } }}
        />
      </Box>
    );
  }

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <Typography variant="h5" component="h1">
        {t('notificationTemplates.title')}
      </Typography>
      <FiltersBar narrow={narrow}>
        <SearchFilter
          label={t('notificationTemplates.filters.code')}
          value={filters.code}
          onChange={(code) => update({ code: code.toUpperCase() })}
          fullWidth={narrow}
        />
        <SingleSelectFilter
          id="template-filter-channel"
          label={t('notificationTemplates.filters.channel')}
          value={filters.channel}
          options={channels.map((c) => ({ value: c, label: t(`channel.${c}`) }))}
          onChange={(channel) => update({ channel })}
          allLabel={t('notificationTemplates.filters.all')}
          fullWidth={narrow}
        />
        <SingleSelectFilter
          id="template-filter-locale"
          label={t('notificationTemplates.filters.locale')}
          value={filters.locale}
          options={locales.map((l) => ({ value: l, label: localeLabel(l) }))}
          onChange={(locale) => update({ locale })}
          allLabel={t('notificationTemplates.filters.all')}
          fullWidth={narrow}
        />
        <FormControlLabel
          control={
            <Switch
              checked={filters.customized}
              onChange={(event) => update({ customized: event.target.checked })}
            />
          }
          label={t('notificationTemplates.filters.customizedOnly')}
        />
      </FiltersBar>
      {query.isError && query.data && (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      )}
      {body}
    </Stack>
  );
}
