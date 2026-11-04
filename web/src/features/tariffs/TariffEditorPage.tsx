import ArrowBack from '@mui/icons-material/ArrowBack';
import ErrorOutline from '@mui/icons-material/ErrorOutline';
import MoreVert from '@mui/icons-material/MoreVert';
import Redo from '@mui/icons-material/Redo';
import Undo from '@mui/icons-material/Undo';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import List from '@mui/material/List';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useBlocker, useParams, useSearchParams } from 'react-router-dom';
import {
  getProductVersion,
  getRatingRules,
  updateRatingRules,
  validateRatingRules,
  type RatingRules,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { productVersionQueryKey } from '../products/productQueries';
import { CoverageTariffEditor } from './CoverageTariffEditor';
import { downloadText, readFileText } from './download';
import { DurationFactorsEditor } from './DurationFactorsEditor';
import { draftFrom } from './initialDraft';
import { coverageOf, locate, type TariffLocation } from './pointer';
import type { Rating, TariffError } from './ratingTypes';
import { SimulatorPanel } from './SimulatorPanel';
import { ratingRulesQueryKey } from './TariffsPage';
import { useTariffDraft } from './useTariffDraft';
import { ratingForServer, validateRating } from './validation';
import { ValidationErrorsPanel } from './ValidationErrorsPanel';

type EditorTab = 'tarifa' | 'simulador';

function isRating(value: unknown): value is Rating {
  return (
    !!value &&
    typeof value === 'object' &&
    Array.isArray((value as Rating).coverages) &&
    (value as Rating).coverages.every(
      (c) => !!c && typeof c === 'object' && !!c.base && Array.isArray(c.base.rows),
    )
  );
}

/** `/seguradora/tarifas/:versionId`: editor da tarifa de uma versão e simulador. */
export function TariffEditorPage() {
  const { t } = useTranslation();
  const { versionId = '' } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.ratingManage);
  const wide = useMediaQuery('(min-width:900px)', { noSsr: true });

  const versionQuery = useQuery({
    queryKey: productVersionQueryKey(versionId),
    queryFn: ({ signal }) => getProductVersion(versionId, undefined, signal),
    refetchOnWindowFocus: false,
  });
  const rulesQuery = useQuery({
    queryKey: ratingRulesQueryKey(versionId),
    queryFn: ({ signal }) => getRatingRules(versionId, undefined, signal),
    refetchOnWindowFocus: false,
  });

  const { draft, change, reset, undo, redo, markSaved, canUndo, canRedo, dirty } = useTariffDraft();
  const [loadedKey, setLoadedKey] = useState('');
  const [errors, setErrors] = useState<TariffError[]>([]);
  const [highlight, setHighlight] = useState<TariffLocation | null>(null);
  const [selected, setSelected] = useState(0);
  const [message, setMessage] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [conflict, setConflict] = useState(false);
  const [lockedReadOnly, setLockedReadOnly] = useState(false);
  const [busy, setBusy] = useState(false);
  const [moreAnchor, setMoreAnchor] = useState<HTMLElement | null>(null);
  const [pendingJson, setPendingJson] = useState<Rating | null>(null);
  const [validateNext, setValidateNext] = useState(false);
  const jsonInput = useRef<HTMLInputElement>(null);

  const rules = rulesQuery.data;
  const version = versionQuery.data;

  const key = rules ? `${rules.productVersionId}:${rules.version}` : '';
  if (rules && key !== loadedKey) {
    setLoadedKey(key);
    reset(draftFrom(rules));
    setErrors([]);
    setHighlight(null);
  }

  const readOnly = !rules?.editable || !canManage || lockedReadOnly;
  const tab: EditorTab =
    searchParams.get('separador') === 'simulador' && canManage ? 'simulador' : 'tarifa';

  // Depois de importar um CSV, a validação do cliente corre sobre o rascunho novo.
  useEffect(() => {
    if (!validateNext || !draft || !rules) return;
    setValidateNext(false);
    setErrors(validateRating(draft, rules));
  }, [validateNext, draft, rules]);

  const blocker = useBlocker(
    ({ currentLocation, nextLocation }) =>
      dirty && !readOnly && currentLocation.pathname !== nextLocation.pathname,
  );
  useEffect(() => {
    if (!dirty || readOnly) return;
    const handler = (event: BeforeUnloadEvent) => {
      event.preventDefault();
      event.returnValue = '';
    };
    window.addEventListener('beforeunload', handler);
    return () => window.removeEventListener('beforeunload', handler);
  }, [dirty, readOnly]);

  if (rulesQuery.isPending) return <LoadingScreen />;
  if (rulesQuery.isError || !rules || !draft) {
    return (
      <Alert
        severity="error"
        action={
          <Button color="inherit" size="small" onClick={() => void rulesQuery.refetch()}>
            {t('tariffs.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(rulesQuery.error))}
      </Alert>
    );
  }

  const productCode = version?.productCode ?? 'tarifa';
  const versionNo = version?.versionNo ?? 0;
  const coverageIndex = Math.min(selected, Math.max(0, draft.coverages.length - 1));
  const current = draft.coverages[coverageIndex];
  const coverageErrors = new Set(
    errors.map((e) => coverageOf(locate(e.field))).filter((c): c is number => c !== undefined),
  );

  const setTab = (next: EditorTab) =>
    setSearchParams(next === 'simulador' ? { separador: 'simulador' } : {}, { replace: true });

  const goTo = (location: TariffLocation) => {
    setTab('tarifa');
    const coverage = coverageOf(location);
    if (coverage !== undefined) setSelected(coverage);
    setHighlight(location);
  };

  const showErrors = (list: TariffError[]) => {
    setErrors(list);
    const first = list.map((e) => locate(e.field)).find((l) => l && l.kind !== 'top');
    if (first) goTo(first);
  };

  const validate = async () => {
    setMessage(null);
    setBusy(true);
    try {
      const result = await validateRatingRules(versionId, {
        rating: ratingForServer(draft) as unknown as RatingRules,
      });
      setErrors(result.errors.map(({ field, code }) => ({ field, code })));
      if (result.valid) setNotice(t('tariffs.editor.valid'));
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  const save = async () => {
    setMessage(null);
    const local = validateRating(draft, rules);
    if (local.length > 0) {
      showErrors(local);
      setMessage(t('tariffs.editor.fixFirst'));
      return;
    }
    setBusy(true);
    try {
      const next = await updateRatingRules(versionId, {
        rating: ratingForServer(draft) as unknown as RatingRules,
        version: rules.version,
      });
      setLoadedKey(`${next.productVersionId}:${next.version}`);
      queryClient.setQueryData(ratingRulesQueryKey(versionId), next);
      markSaved(draft);
      setErrors([]);
      setHighlight(null);
      setNotice(t('tariffs.editor.saved'));
    } catch (error) {
      const problem = getProblem(error);
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        setConflict(true);
      } else if (problem.code === 'PRODUCT_VERSION_NOT_EDITABLE') {
        setLockedReadOnly(true);
        setMessage(problemMessage(t, problem));
      } else if (problem.code === 'VALIDATION_ERROR' && problem.errors.length > 0) {
        showErrors(problem.errors.map(({ field, code }) => ({ field, code })));
        setMessage(problemMessage(t, problem));
      } else {
        setMessage(problemMessage(t, problem));
      }
    } finally {
      setBusy(false);
    }
  };

  const reload = async () => {
    setConflict(false);
    setMessage(null);
    setLoadedKey('');
    await rulesQuery.refetch();
  };

  const exportJson = () => {
    setMoreAnchor(null);
    downloadText(
      `tarifa-${productCode}-v${versionNo}.json`,
      `${JSON.stringify(ratingForServer(draft), null, 2)}\n`,
      'application/json',
    );
  };

  const importJson = async (file: File) => {
    try {
      const parsed: unknown = JSON.parse(await readFileText(file));
      if (!isRating(parsed)) throw new Error('not a tariff');
      setPendingJson(parsed);
    } catch {
      setMessage(t('tariffs.editor.invalidJson'));
    }
  };

  const confirmJson = async () => {
    const next = pendingJson;
    setPendingJson(null);
    if (!next) return;
    change(next);
    setSelected(0);
    setHighlight(null);
    setBusy(true);
    try {
      const result = await validateRatingRules(versionId, {
        rating: ratingForServer(next) as unknown as RatingRules,
      });
      setErrors(result.errors.map(({ field, code }) => ({ field, code })));
      if (result.valid) setNotice(t('tariffs.editor.valid'));
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  const changeCoverage = (index: number, tariff: Rating['coverages'][number]) =>
    change((d) => ({ ...d, coverages: d.coverages.map((c, i) => (i === index ? tariff : c)) }));

  const durationErrors = new Map<number, string>();
  errors.forEach((e) => {
    const location = locate(e.field);
    if (location?.kind === 'duration') {
      const days = draft.durationFactors?.[location.index]?.days;
      if (days !== undefined) durationErrors.set(days, e.code);
    }
  });
  const highlightDays =
    highlight?.kind === 'duration' ? draft.durationFactors?.[highlight.index]?.days : undefined;

  const coverageLabel = (code: string) => t(`coverage.${code}`, { defaultValue: code });

  return (
    <Stack spacing={3} sx={{ minWidth: 0, maxWidth: 1200 }}>
      <Stack spacing={1}>
        <div>
          <Link
            component={RouterLink}
            to="/seguradora/tarifas"
            sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
          >
            <ArrowBack fontSize="small" />
            {t('tariffs.editor.back')}
          </Link>
        </div>
        <Stack
          direction={{ xs: 'column', md: 'row' }}
          spacing={2}
          justifyContent="space-between"
          alignItems={{ md: 'center' }}
        >
          <Stack spacing={0.5} sx={{ minWidth: 0 }}>
            <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
              {t('tariffs.editor.title', { name: version?.productName ?? productCode })}
            </Typography>
            <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
              {version && (
                <Typography variant="body2">
                  {t('tariffs.versionNo', { n: version.versionNo })}
                </Typography>
              )}
              {version && (
                <Chip
                  size="small"
                  variant="outlined"
                  label={t(`productVersionStatus.${version.status}`)}
                />
              )}
              <Chip
                size="small"
                variant="outlined"
                color={rules.configured ? 'success' : 'warning'}
                label={rules.configured ? t('tariffs.configured') : t('tariffs.notConfigured')}
              />
            </Stack>
          </Stack>
          <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
            {!readOnly && (
              <>
                <Tooltip title={t('tariffs.editor.undo')}>
                  <span>
                    <IconButton
                      aria-label={t('tariffs.editor.undo')}
                      disabled={!canUndo}
                      onClick={undo}
                    >
                      <Undo />
                    </IconButton>
                  </span>
                </Tooltip>
                <Tooltip title={t('tariffs.editor.redo')}>
                  <span>
                    <IconButton
                      aria-label={t('tariffs.editor.redo')}
                      disabled={!canRedo}
                      onClick={redo}
                    >
                      <Redo />
                    </IconButton>
                  </span>
                </Tooltip>
              </>
            )}
            {canManage && (
              <Button variant="outlined" onClick={() => void validate()} disabled={busy}>
                {t('tariffs.editor.validate')}
              </Button>
            )}
            {!readOnly && (
              <Button variant="contained" onClick={() => void save()} disabled={busy}>
                {t('tariffs.editor.save')}
              </Button>
            )}
            <Button
              variant="text"
              endIcon={<MoreVert />}
              aria-haspopup="menu"
              onClick={(event) => setMoreAnchor(event.currentTarget)}
            >
              {t('tariffs.editor.more')}
            </Button>
            <Menu open={!!moreAnchor} anchorEl={moreAnchor} onClose={() => setMoreAnchor(null)}>
              <MenuItem onClick={exportJson}>{t('tariffs.editor.exportJson')}</MenuItem>
              {!readOnly && (
                <MenuItem
                  onClick={() => {
                    setMoreAnchor(null);
                    jsonInput.current?.click();
                  }}
                >
                  {t('tariffs.editor.importJson')}
                </MenuItem>
              )}
            </Menu>
            <input
              ref={jsonInput}
              type="file"
              accept=".json,application/json"
              hidden
              data-testid="json-input"
              onChange={(event) => {
                const file = event.target.files?.[0];
                event.target.value = '';
                if (file) void importJson(file);
              }}
            />
          </Stack>
        </Stack>
      </Stack>

      {(!rules.editable || lockedReadOnly) && (
        <Alert severity="info" data-testid="read-only-notice">
          {t('tariffs.editor.readOnlyStatus')}
        </Alert>
      )}
      {rules.editable && !lockedReadOnly && !canManage && (
        <Alert severity="info" data-testid="read-only-notice">
          {t('tariffs.editor.readOnlyPermission')}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}
      <ValidationErrorsPanel errors={errors} rating={draft} onSelect={goTo} />

      {canManage && (
        <Tabs value={tab} onChange={(_, value: EditorTab) => setTab(value)}>
          <Tab value="tarifa" label={t('tariffs.tabs.tariff')} />
          <Tab value="simulador" label={t('tariffs.tabs.simulator')} />
        </Tabs>
      )}

      {tab === 'simulador' ? (
        <SimulatorPanel
          versionId={versionId}
          rating={draft}
          coverages={rules.coverages}
          productType={rules.productType}
          onTariffErrors={showErrors}
          onGoToTable={goTo}
        />
      ) : (
        <Stack spacing={3}>
          {rules.productType === 'TEMPORARY' && (
            <DurationFactorsEditor
              key={loadedKey}
              value={draft.durationFactors ?? []}
              readOnly={readOnly}
              onChange={(durationFactors) => change((d) => ({ ...d, durationFactors }))}
              errors={durationErrors}
              highlightDays={highlightDays}
            />
          )}
          <Stack direction={wide ? 'row' : 'column'} spacing={3} alignItems="flex-start">
            {wide ? (
              <Paper
                variant="outlined"
                sx={{ width: 280, flexShrink: 0 }}
                component="nav"
                aria-label={t('tariffs.editor.coverages')}
              >
                <List dense>
                  {draft.coverages.map((c, i) => (
                    <ListItemButton
                      key={`${c.coverageCode}-${i}`}
                      selected={i === coverageIndex}
                      onClick={() => setSelected(i)}
                    >
                      <ListItemText
                        primary={coverageLabel(c.coverageCode)}
                        secondary={c.coverageCode}
                      />
                      {coverageErrors.has(i) && (
                        <ListItemIcon sx={{ minWidth: 0 }}>
                          <ErrorOutline color="error" aria-label={t('tariffs.editor.hasErrors')} />
                        </ListItemIcon>
                      )}
                    </ListItemButton>
                  ))}
                </List>
              </Paper>
            ) : (
              <TextField
                select
                size="small"
                label={t('tariffs.editor.coverage')}
                value={String(coverageIndex)}
                onChange={(event) => setSelected(Number(event.target.value))}
                fullWidth
              >
                {draft.coverages.map((c, i) => (
                  <MenuItem key={`${c.coverageCode}-${i}`} value={String(i)}>
                    {coverageLabel(c.coverageCode)}
                    {coverageErrors.has(i) ? ` · ${t('tariffs.editor.hasErrors')}` : ''}
                  </MenuItem>
                ))}
              </TextField>
            )}
            <Box sx={{ flex: 1, minWidth: 0, width: '100%' }}>
              {current && (
                <Paper sx={{ p: { xs: 1.5, sm: 3 } }}>
                  <CoverageTariffEditor
                    key={`${coverageIndex}-${current.coverageCode}`}
                    index={coverageIndex}
                    tariff={current}
                    coverage={rules.coverages.find((c) => c.code === current.coverageCode)}
                    readOnly={readOnly}
                    onChange={(tariff) => changeCoverage(coverageIndex, tariff)}
                    errors={errors}
                    highlight={highlight}
                    csvPrefix={`${productCode}-v${versionNo}-${current.coverageCode}`}
                    onImported={() => setValidateNext(true)}
                  />
                </Paper>
              )}
            </Box>
          </Stack>
        </Stack>
      )}

      <Dialog open={conflict} onClose={() => setConflict(false)} aria-labelledby="conflict-title">
        <DialogTitle id="conflict-title">{t('tariffs.editor.conflict.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('tariffs.editor.conflict.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConflict(false)}>{t('tariffs.editor.conflict.cancel')}</Button>
          <Button variant="contained" onClick={() => void reload()}>
            {t('tariffs.editor.conflict.reload')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={!!pendingJson}
        onClose={() => setPendingJson(null)}
        aria-labelledby="json-title"
      >
        <DialogTitle id="json-title">{t('tariffs.editor.importConfirm.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('tariffs.editor.importConfirm.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPendingJson(null)}>
            {t('tariffs.editor.importConfirm.cancel')}
          </Button>
          <Button variant="contained" onClick={() => void confirmJson()}>
            {t('tariffs.editor.importConfirm.confirm')}
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={blocker.state === 'blocked'}
        onClose={() => blocker.reset?.()}
        aria-labelledby="leave-title"
      >
        <DialogTitle id="leave-title">{t('tariffs.editor.unsaved.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('tariffs.editor.unsaved.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => blocker.reset?.()}>{t('tariffs.editor.unsaved.stay')}</Button>
          <Button color="error" onClick={() => blocker.proceed?.()}>
            {t('tariffs.editor.unsaved.leave')}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={!!notice}
        autoHideDuration={3000}
        onClose={() => setNotice(null)}
        message={notice}
      />
    </Stack>
  );
}
