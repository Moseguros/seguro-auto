import ArrowDownward from '@mui/icons-material/ArrowDownward';
import ArrowUpward from '@mui/icons-material/ArrowUpward';
import Delete from '@mui/icons-material/Delete';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import IconButton from '@mui/material/IconButton';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { RatingCoverage } from '../../api/generated';
import { fieldMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { suggestedVariable } from './factorPresets';
import { FactorMenu } from './FactorMenu';
import { allowedBaseTypes } from './initialDraft';
import { locate, type TariffLocation } from './pointer';
import { RateTableEditor } from './RateTableEditor';
import {
  valueKindOf,
  type BaseType,
  type CoverageTariff,
  type FactorTable,
  type TariffError,
} from './ratingTypes';

export interface CoverageTariffEditorProps {
  index: number;
  tariff: CoverageTariff;
  coverage?: RatingCoverage;
  readOnly: boolean;
  onChange(tariff: CoverageTariff): void;
  errors: TariffError[];
  highlight: TariffLocation | null;
  /** Início do nome dos CSV: `<produto>-v<n>-<cobertura>`. */
  csvPrefix: string;
  onImported(): void;
}

/** Linhas com erros de uma tabela (base ou factor `f`) desta cobertura. */
function errorRowsOf(errors: TariffError[], coverage: number, factor?: number): Set<number> {
  const rows = new Set<number>();
  for (const error of errors) {
    const location = locate(error.field);
    if (!location || location.kind === 'top' || location.kind === 'duration') continue;
    if (location.coverage !== coverage) continue;
    if (factor === undefined && location.kind === 'base' && location.row !== undefined) {
      rows.add(location.row);
    }
    if (
      factor !== undefined &&
      location.kind === 'factor' &&
      location.factor === factor &&
      location.row !== undefined
    ) {
      rows.add(location.row);
    }
  }
  return rows;
}

function tableErrors(errors: TariffError[], prefix: string): TariffError[] {
  return errors.filter(
    (e) => e.field === prefix || (e.field.startsWith(`${prefix}/`) && !e.field.includes('/rows/')),
  );
}

/** Tarifa de uma cobertura: base e factores. */
export function CoverageTariffEditor({
  index,
  tariff,
  coverage,
  readOnly,
  onChange,
  errors,
  highlight,
  csvPrefix,
  onImported,
}: CoverageTariffEditorProps) {
  const { t, i18n } = useTranslation();
  const [baseTypeChange, setBaseTypeChange] = useState<BaseType | null>(null);
  const [deleting, setDeleting] = useState<number | null>(null);
  const factors = tariff.factors ?? [];

  const capitalText = () => {
    if (!coverage) return '';
    const type = t(`capitalType.${coverage.capitalType}`);
    if (coverage.capitalType === 'OPTIONS' && coverage.capitalOptions?.length) {
      return `${type}: ${coverage.capitalOptions.map((m) => formatMoney(m, i18n.language)).join(', ')}`;
    }
    if (coverage.capital) return `${type}: ${formatMoney(coverage.capital, i18n.language)}`;
    return type;
  };

  const setFactors = (next: FactorTable[]) =>
    onChange({ ...tariff, factors: next.length > 0 ? next : undefined });

  const moveFactor = (from: number, to: number) => {
    if (to < 0 || to >= factors.length) return;
    const next = [...factors];
    const [item] = next.splice(from, 1);
    next.splice(to, 0, item!);
    setFactors(next);
  };

  const highlightRow = (factor?: number) => {
    if (!highlight || highlight.kind === 'top' || highlight.kind === 'duration') return undefined;
    if (highlight.coverage !== index) return undefined;
    if (factor === undefined && highlight.kind === 'base') return highlight.row;
    if (factor !== undefined && highlight.kind === 'factor' && highlight.factor === factor) {
      return highlight.row;
    }
    return undefined;
  };

  const baseErrors = tableErrors(errors, `/coverages/${index}/base`);
  const allowed = coverage ? allowedBaseTypes(coverage.capitalType) : [tariff.base.type];

  return (
    <Stack spacing={3} data-testid={`coverage-${tariff.coverageCode}`}>
      <Stack spacing={0.5}>
        <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
          <Typography variant="h5" component="h2">
            {t(`coverage.${tariff.coverageCode}`, { defaultValue: tariff.coverageCode })}
          </Typography>
          {coverage?.mandatory && (
            <Chip
              size="small"
              color="primary"
              variant="outlined"
              label={t('tariffs.coverage.mandatory')}
            />
          )}
        </Stack>
        {coverage && (
          <Typography variant="body2" color="text.secondary">
            {t('tariffs.coverage.capital', { type: capitalText() })}
          </Typography>
        )}
      </Stack>

      <Paper
        variant="outlined"
        sx={{ p: { xs: 1.5, sm: 2 } }}
        component="section"
        aria-label={t('tariffs.coverage.base')}
      >
        <Stack spacing={2}>
          <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ sm: 'center' }}>
            <Typography variant="h6" component="h3">
              {t('tariffs.coverage.base')}
            </Typography>
            <TextField
              select
              size="small"
              label={t('tariffs.coverage.baseType')}
              value={tariff.base.type}
              disabled={readOnly}
              onChange={(event) => {
                const next = event.target.value as BaseType;
                if (next !== tariff.base.type) setBaseTypeChange(next);
              }}
              sx={{ minWidth: { sm: 280 } }}
            >
              {allowed.map((type) => (
                <MenuItem key={type} value={type}>
                  {t(`baseType.${type}`)}
                </MenuItem>
              ))}
            </TextField>
          </Stack>
          {baseErrors.map((e, i) => (
            <Typography key={i} color="error" variant="body2">
              {fieldMessage(t, e.code)}
            </Typography>
          ))}
          <RateTableEditor
            tableKey={`${index}-base`}
            rows={tariff.base.rows}
            kind={valueKindOf(tariff.base.type)}
            readOnly={readOnly}
            onChange={(rows) => onChange({ ...tariff, base: { ...tariff.base, rows } })}
            errorRows={errorRowsOf(errors, index)}
            highlightRow={highlightRow()}
            csvFileName={`${csvPrefix}-BASE.csv`}
            onImported={onImported}
          />
        </Stack>
      </Paper>

      <Stack spacing={2}>
        <Typography variant="h6" component="h3">
          {t('tariffs.coverage.factors')}
        </Typography>
        {factors.length === 0 && (
          <Typography variant="body2" color="text.secondary">
            {t('tariffs.coverage.noFactors')}
          </Typography>
        )}
        {factors.map((factor, f) => {
          const name = factor.label || factor.code;
          const ownErrors = tableErrors(errors, `/coverages/${index}/factors/${f}`);
          return (
            <Paper
              key={`${factor.code}-${f}`}
              variant="outlined"
              sx={{ p: { xs: 1.5, sm: 2 } }}
              component="section"
              aria-label={name}
            >
              <Stack spacing={2}>
                <Stack
                  direction="row"
                  spacing={1}
                  alignItems="center"
                  justifyContent="space-between"
                >
                  <div>
                    <Typography variant="subtitle1" component="h4" sx={{ fontWeight: 600 }}>
                      {name}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {t('tariffs.coverage.factorCode', { code: factor.code })}
                    </Typography>
                  </div>
                  {!readOnly && (
                    <Stack direction="row">
                      <IconButton
                        size="small"
                        aria-label={t('tariffs.coverage.moveUp', { name })}
                        disabled={f === 0}
                        onClick={() => moveFactor(f, f - 1)}
                      >
                        <ArrowUpward fontSize="small" />
                      </IconButton>
                      <IconButton
                        size="small"
                        aria-label={t('tariffs.coverage.moveDown', { name })}
                        disabled={f === factors.length - 1}
                        onClick={() => moveFactor(f, f + 1)}
                      >
                        <ArrowDownward fontSize="small" />
                      </IconButton>
                      <IconButton
                        size="small"
                        aria-label={t('tariffs.coverage.delete', { name })}
                        onClick={() => setDeleting(f)}
                      >
                        <Delete fontSize="small" />
                      </IconButton>
                    </Stack>
                  )}
                </Stack>
                {ownErrors.map((e, i) => (
                  <Typography key={i} color="error" variant="body2">
                    {fieldMessage(t, e.code)}
                  </Typography>
                ))}
                <RateTableEditor
                  tableKey={`${index}-factor-${f}`}
                  rows={factor.rows}
                  kind="FACTOR"
                  readOnly={readOnly}
                  onChange={(rows) =>
                    setFactors(factors.map((x, i) => (i === f ? { ...x, rows } : x)))
                  }
                  errorRows={errorRowsOf(errors, index, f)}
                  highlightRow={highlightRow(f)}
                  suggested={suggestedVariable(factor.code)}
                  csvFileName={`${csvPrefix}-${factor.code}.csv`}
                  onImported={onImported}
                />
              </Stack>
            </Paper>
          );
        })}
        {!readOnly && (
          <div>
            <FactorMenu
              existingCodes={factors.map((f) => f.code)}
              disabled={factors.length >= 12}
              onAdd={({ code, label }) =>
                setFactors([...factors, { code, label, rows: [{ when: {}, value: '1' }] }])
              }
            />
          </div>
        )}
      </Stack>

      <Dialog
        open={baseTypeChange !== null}
        onClose={() => setBaseTypeChange(null)}
        aria-labelledby="base-type-title"
      >
        <DialogTitle id="base-type-title">{t('tariffs.coverage.changeBaseType.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('tariffs.coverage.changeBaseType.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setBaseTypeChange(null)}>
            {t('tariffs.coverage.changeBaseType.cancel')}
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              if (baseTypeChange) {
                onChange({
                  ...tariff,
                  base: {
                    type: baseTypeChange,
                    rows: tariff.base.rows.map((row) => ({ ...row, value: null })),
                  },
                });
              }
              setBaseTypeChange(null);
            }}
          >
            {t('tariffs.coverage.changeBaseType.confirm')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={deleting !== null}
        onClose={() => setDeleting(null)}
        aria-labelledby="delete-factor-title"
      >
        <DialogTitle id="delete-factor-title">
          {t('tariffs.coverage.deleteFactor.title', {
            name: deleting === null ? '' : factors[deleting]?.label || factors[deleting]?.code,
          })}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>{t('tariffs.coverage.deleteFactor.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleting(null)}>
            {t('tariffs.coverage.deleteFactor.cancel')}
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => {
              if (deleting !== null) setFactors(factors.filter((_, i) => i !== deleting));
              setDeleting(null);
            }}
          >
            {t('tariffs.coverage.deleteFactor.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
