import Add from '@mui/icons-material/Add';
import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import ContentCopy from '@mui/icons-material/ContentCopy';
import Delete from '@mui/icons-material/Delete';
import Download from '@mui/icons-material/Download';
import Edit from '@mui/icons-material/Edit';
import MoreVert from '@mui/icons-material/MoreVert';
import Upload from '@mui/icons-material/Upload';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { alpha, useTheme } from '@mui/material/styles';
import { useEffect, useRef, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { ConditionSummary } from './ConditionSummary';
import { exportTableCsv, importTableCsv, maxCsvRows, type CsvError } from './csv';
import { downloadText, readFileText } from './download';
import type { Condition, TariffRow, ValueKind } from './ratingTypes';
import { RowDialog } from './RowDialog';
import { rowValueText } from './rowValue';

export interface RateTableEditorProps {
  /** Identifica a tabela nos testes e nos destaques: `base` ou `factor-<i>`. */
  tableKey: string;
  rows: TariffRow[];
  kind: ValueKind;
  readOnly: boolean;
  onChange(rows: TariffRow[]): void;
  /** Linhas com erros (índices). */
  errorRows?: ReadonlySet<number>;
  /** Linha a destacar (clicada no painel de erros). */
  highlightRow?: number;
  /** Variável sugerida numa linha nova. */
  suggested?: keyof Condition;
  /** Nome do ficheiro exportado. */
  csvFileName: string;
  /** Depois de importar um CSV (para a validação correr). */
  onImported?(): void;
}

/** Linha nova: antes de uma última linha "sempre" (que apanha tudo), senão no fim. */
export function insertRow(rows: TariffRow[], row: TariffRow): TariffRow[] {
  const last = rows[rows.length - 1];
  if (last && Object.keys(last.when).length === 0) {
    return [...rows.slice(0, -1), row, last];
  }
  return [...rows, row];
}

function move<T>(list: T[], from: number, to: number): T[] {
  if (to < 0 || to >= list.length) return list;
  const next = [...list];
  const [item] = next.splice(from, 1);
  next.splice(to, 0, item!);
  return next;
}

/** Tabela de linhas (a base ou um factor): ver, editar, ordenar, exportar e importar CSV. */
export function RateTableEditor({
  tableKey,
  rows,
  kind,
  readOnly,
  onChange,
  errorRows,
  highlightRow,
  suggested,
  csvFileName,
  onImported,
}: RateTableEditorProps) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const narrow = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [editing, setEditing] = useState<number | 'new' | null>(null);
  const [menu, setMenu] = useState<{ anchor: HTMLElement; index: number } | null>(null);
  const [csvErrors, setCsvErrors] = useState<CsvError[] | null>(null);
  const [pending, setPending] = useState<TariffRow[] | null>(null);
  const fileInput = useRef<HTMLInputElement>(null);
  const rowRefs = useRef<Map<number, HTMLElement>>(new Map());

  useEffect(() => {
    if (highlightRow === undefined) return;
    const element = rowRefs.current.get(highlightRow);
    if (element && typeof element.scrollIntoView === 'function') {
      element.scrollIntoView({ block: 'center', behavior: 'smooth' });
    }
  }, [highlightRow]);

  const actions = (index: number) => [
    {
      key: 'edit',
      icon: <Edit fontSize="small" />,
      label: t('tariffs.table.edit'),
      run: () => setEditing(index),
    },
    {
      key: 'duplicate',
      icon: <ContentCopy fontSize="small" />,
      label: t('tariffs.table.duplicate'),
      disabled: rows.length >= maxCsvRows,
      run: () => {
        const copy = structuredClone(rows[index]!);
        onChange([...rows.slice(0, index + 1), copy, ...rows.slice(index + 1)]);
      },
    },
    {
      key: 'up',
      icon: <ArrowUpward fontSize="small" />,
      label: t('tariffs.table.moveUp'),
      disabled: index === 0,
      run: () => onChange(move(rows, index, index - 1)),
    },
    {
      key: 'down',
      icon: <ArrowDownward fontSize="small" />,
      label: t('tariffs.table.moveDown'),
      disabled: index === rows.length - 1,
      run: () => onChange(move(rows, index, index + 1)),
    },
    {
      key: 'delete',
      icon: <Delete fontSize="small" />,
      label: t('tariffs.table.delete'),
      run: () => onChange(rows.filter((_, i) => i !== index)),
    },
  ];

  const valueCell = (row: TariffRow) => {
    const text = rowValueText(row.value, kind, i18n.language);
    return text === null ? (
      <Typography component="span" color="error" variant="body2">
        {t('tariffs.table.emptyValue')}
      </Typography>
    ) : (
      <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}>
        {text}
      </Box>
    );
  };

  const rowProps = (index: number) => {
    const error = errorRows?.has(index) ?? false;
    const highlighted = highlightRow === index;
    return {
      'data-testid': `row-${tableKey}-${index}`,
      'data-error': error ? 'true' : undefined,
      'data-highlighted': highlighted ? 'true' : undefined,
      ref: (element: HTMLElement | null) => {
        if (element) rowRefs.current.set(index, element);
        else rowRefs.current.delete(index);
      },
      sx: {
        ...(error && { boxShadow: `inset 4px 0 0 ${theme.palette.error.main}` }),
        ...(highlighted && { bgcolor: alpha(theme.palette.warning.main, 0.18) }),
      },
    };
  };

  const importFile = async (file: File) => {
    const text = await readFileText(file);
    const result = importTableCsv(text, kind);
    if (!result.ok) {
      setCsvErrors(result.errors);
      return;
    }
    setPending(result.rows);
  };

  let content: ReactNode;
  if (narrow) {
    content = (
      <Stack spacing={1}>
        {rows.map((row, index) => (
          <Paper key={index} variant="outlined" {...rowProps(index)}>
            <Stack spacing={1} sx={{ p: 1.5 }}>
              <Stack direction="row" justifyContent="space-between" alignItems="center">
                <Typography variant="subtitle2">
                  {t('tariffs.table.row', { n: index + 1 })}
                  {row.label ? ` · ${row.label}` : ''}
                </Typography>
                {!readOnly && (
                  <IconButton
                    size="small"
                    aria-label={t('tariffs.table.actions', { n: index + 1 })}
                    onClick={(event) => setMenu({ anchor: event.currentTarget, index })}
                  >
                    <MoreVert fontSize="small" />
                  </IconButton>
                )}
              </Stack>
              <ConditionSummary when={row.when} />
              <div>{valueCell(row)}</div>
            </Stack>
          </Paper>
        ))}
      </Stack>
    );
  } else {
    content = (
      <Box sx={{ overflowX: 'auto' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>{t('tariffs.table.columns.n')}</TableCell>
              <TableCell>{t('tariffs.table.columns.label')}</TableCell>
              <TableCell>{t('tariffs.table.columns.conditions')}</TableCell>
              <TableCell align="right">{t('tariffs.table.columns.value')}</TableCell>
              {!readOnly && (
                <TableCell align="right">{t('tariffs.table.columns.actions')}</TableCell>
              )}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row, index) => (
              <TableRow key={index} {...rowProps(index)}>
                <TableCell>{index + 1}</TableCell>
                <TableCell sx={{ wordBreak: 'break-word' }}>{row.label ?? ''}</TableCell>
                <TableCell>
                  <ConditionSummary when={row.when} />
                </TableCell>
                <TableCell align="right">{valueCell(row)}</TableCell>
                {!readOnly && (
                  <TableCell align="right" sx={{ whiteSpace: 'nowrap' }}>
                    {actions(index).map((action) => (
                      <Tooltip key={action.key} title={action.label}>
                        <span>
                          <IconButton
                            size="small"
                            aria-label={`${action.label} ${t('tariffs.table.row', { n: index + 1 })}`}
                            disabled={action.disabled}
                            onClick={action.run}
                          >
                            {action.icon}
                          </IconButton>
                        </span>
                      </Tooltip>
                    ))}
                  </TableCell>
                )}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Box>
    );
  }

  return (
    <Stack spacing={1.5} data-testid={`table-${tableKey}`}>
      <Typography variant="body2" color="text.secondary">
        {t('tariffs.table.help')}
      </Typography>
      {content}
      <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
        {!readOnly && (
          <Button
            size="small"
            variant="outlined"
            startIcon={<Add />}
            disabled={rows.length >= maxCsvRows}
            onClick={() => setEditing('new')}
          >
            {t('tariffs.table.addRow')}
          </Button>
        )}
        <Button
          size="small"
          startIcon={<Download />}
          onClick={() =>
            downloadText(csvFileName, exportTableCsv(rows, kind), 'text/csv;charset=utf-8')
          }
        >
          {t('tariffs.table.exportCsv')}
        </Button>
        {!readOnly && (
          <>
            <Button size="small" startIcon={<Upload />} onClick={() => fileInput.current?.click()}>
              {t('tariffs.table.importCsv')}
            </Button>
            <input
              ref={fileInput}
              type="file"
              accept=".csv,text/csv"
              hidden
              data-testid={`csv-input-${tableKey}`}
              onChange={(event) => {
                const file = event.target.files?.[0];
                event.target.value = '';
                if (file) void importFile(file);
              }}
            />
          </>
        )}
      </Stack>

      <Menu open={!!menu} anchorEl={menu?.anchor} onClose={() => setMenu(null)}>
        {menu &&
          actions(menu.index).map((action) => (
            <MenuItem
              key={action.key}
              disabled={action.disabled}
              onClick={() => {
                setMenu(null);
                action.run();
              }}
            >
              <ListItemIcon>{action.icon}</ListItemIcon>
              <ListItemText>{action.label}</ListItemText>
            </MenuItem>
          ))}
      </Menu>

      {editing !== null && (
        <RowDialog
          row={editing === 'new' ? null : rows[editing]!}
          rowNumber={editing === 'new' ? undefined : editing + 1}
          kind={kind}
          suggested={suggested}
          onClose={() => setEditing(null)}
          onApply={(row) => {
            if (editing === 'new') onChange(insertRow(rows, row));
            else onChange(rows.map((r, i) => (i === editing ? row : r)));
            setEditing(null);
          }}
        />
      )}

      <Dialog
        open={!!csvErrors}
        onClose={() => setCsvErrors(null)}
        aria-labelledby={`csv-errors-${tableKey}`}
      >
        <DialogTitle id={`csv-errors-${tableKey}`}>
          {t('tariffs.table.importErrors.title')}
        </DialogTitle>
        <DialogContent>
          <Alert severity="error" icon={false}>
            <List dense disablePadding data-testid="csv-errors">
              {(csvErrors ?? []).map((error, index) => (
                <ListItem key={index} disableGutters>
                  {t(`tariffCsv.${error.code}`, {
                    line: error.line,
                    column: error.column,
                    value: error.value,
                  })}
                </ListItem>
              ))}
            </List>
          </Alert>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCsvErrors(null)}>
            {t('tariffs.table.importErrors.close')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={!!pending}
        onClose={() => setPending(null)}
        aria-labelledby={`csv-confirm-${tableKey}`}
      >
        <DialogTitle id={`csv-confirm-${tableKey}`}>
          {t('tariffs.table.importConfirm.title')}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t('tariffs.table.importConfirm.text', { n: rows.length })}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPending(null)}>
            {t('tariffs.table.importConfirm.cancel')}
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              if (pending) onChange(pending);
              setPending(null);
              onImported?.();
            }}
          >
            {t('tariffs.table.importConfirm.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
