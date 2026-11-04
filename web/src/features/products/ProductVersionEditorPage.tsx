import ArrowBack from '@mui/icons-material/ArrowBack';
import Gavel from '@mui/icons-material/Gavel';
import KeyboardArrowLeft from '@mui/icons-material/KeyboardArrowLeft';
import KeyboardArrowRight from '@mui/icons-material/KeyboardArrowRight';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Link from '@mui/material/Link';
import MobileStepper from '@mui/material/MobileStepper';
import Paper from '@mui/material/Paper';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import {
  createProductVersion,
  getInsurerProduct,
  getProductVersion,
  listPublicPlatformSettings,
  replaceProductVersionCoverages,
  updateProductVersion,
  type Money,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { useAuth } from '../../auth/useAuth';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { mzn } from '../../format/money';
import { UnderwritingRulesDialog } from './UnderwritingRulesDialog';
import { formatDate } from '../../i18n/format';
import {
  coverageFrom,
  coverageRequest,
  generalFrom,
  parsePercent,
  type CoverageDraft,
  type FieldErrors,
  type GeneralDraft,
} from './editorModel';
import { editorSteps, isEditorStep, type EditorStep } from './publicationErrors';
import { productQueryKey, productVersionQueryKey } from './productQueries';
import { CoveragesStep } from './steps/CoveragesStep';
import { DeductiblesStep } from './steps/DeductiblesStep';
import { DocumentsStep } from './steps/DocumentsStep';
import { GeneralStep } from './steps/GeneralStep';
import { PreviewStep } from './steps/PreviewStep';

/** Grupo de dados que cada passo grava (os passos 2 e 3 gravam a mesma lista). */
type DraftGroup = 'general' | 'coverages' | null;

function groupOf(step: EditorStep): DraftGroup {
  if (step === 'dados') return 'general';
  if (step === 'coberturas' || step === 'franquias') return 'coverages';
  return null;
}

/** Erros locais que impedem gravar (percentagens mal escritas). */
function localErrors(coverages: CoverageDraft[]): FieldErrors {
  const errors: FieldErrors = {};
  coverages.forEach((coverage, index) => {
    if (
      coverage.deductibleType === 'PERCENT' &&
      parsePercent(coverage.deductiblePercent) === 'invalid'
    ) {
      errors[`coverages[${index}].deductiblePercent`] = 'OUT_OF_RANGE';
    }
  });
  return errors;
}

/** `/seguradora/produtos/:productId/versoes/:versionId`: editor em passos de uma versão. */
export function ProductVersionEditorPage() {
  const { t, i18n } = useTranslation();
  const { productId = '', versionId = '' } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const theme = useTheme();
  const wide = useMediaQuery(theme.breakpoints.up('md'), { noSsr: true });
  const canManage = usePermission(Permissions.productManage);
  const { me } = useAuth();
  const [rulesOpen, setRulesOpen] = useState(false);

  const passo = searchParams.get('passo');
  const step: EditorStep = isEditorStep(passo) ? passo : 'dados';
  const stepIndex = editorSteps.indexOf(step);

  const versionQuery = useQuery({
    queryKey: productVersionQueryKey(versionId),
    queryFn: ({ signal }) => getProductVersion(versionId, undefined, signal),
    refetchOnWindowFocus: false,
  });
  const productQuery = useQuery({
    queryKey: productQueryKey(productId),
    queryFn: ({ signal }) => getInsurerProduct(productId, undefined, signal),
    refetchOnWindowFocus: false,
  });
  const settingsQuery = useQuery({
    queryKey: ['platformSettings', 'public'],
    queryFn: ({ signal }) => listPublicPlatformSettings(undefined, signal),
    staleTime: 5 * 60_000,
  });
  const minSetting = settingsQuery.data?.settings.find((s) => s.key === 'MTPL_MIN_CAPITAL');
  const minCapital: Money | null =
    minSetting && /^\d+$/.test(minSetting.value) ? mzn(Number(minSetting.value)) : null;

  const version = versionQuery.data;
  const [loadedKey, setLoadedKey] = useState('');
  const [general, setGeneral] = useState<GeneralDraft | null>(null);
  const [coverages, setCoverages] = useState<CoverageDraft[]>([]);
  const [dirty, setDirty] = useState<{ general: boolean; coverages: boolean }>({
    general: false,
    coverages: false,
  });
  const [invalid, setInvalid] = useState<Set<string>>(new Set());
  const [serverErrors, setServerErrors] = useState<FieldErrors>({});
  const [message, setMessage] = useState<string | null>(null);
  const [conflict, setConflict] = useState(false);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [leaving, setLeaving] = useState<EditorStep | null>(null);
  const [creating, setCreating] = useState(false);

  // Cada versão lida do servidor substitui os rascunhos (depois de gravar, recarregar, etc.).
  const key = version ? `${version.id}:${version.version}` : '';
  if (version && key !== loadedKey) {
    setLoadedKey(key);
    setGeneral(generalFrom(version));
    setCoverages(version.coverages.map(coverageFrom));
    setDirty({ general: false, coverages: false });
    setInvalid(new Set());
  }

  if (versionQuery.isPending) return <LoadingScreen />;
  if (versionQuery.isError || !version || !general) {
    return <Alert severity="error">{problemMessage(t, getProblem(versionQuery.error))}</Alert>;
  }

  const product = productQuery.data;
  const readOnly = !version.editable || !canManage;
  const errors: FieldErrors = { ...serverErrors, ...localErrors(coverages) };

  const setStep = (next: EditorStep) => {
    setSearchParams({ passo: next }, { replace: false });
  };

  const goTo = (next: EditorStep) => {
    if (next === step) return;
    const group = groupOf(step);
    if (!readOnly && group && dirty[group] && groupOf(next) !== group) {
      setLeaving(next);
      return;
    }
    setStep(next);
  };

  const discard = () => {
    setLoadedKey('');
    setServerErrors({});
    const next = leaving;
    setLeaving(null);
    if (next) setStep(next);
  };

  const reload = async () => {
    setConflict(false);
    setMessage(null);
    setServerErrors({});
    await Promise.all([versionQuery.refetch(), productQuery.refetch()]);
    setLoadedKey('');
  };

  const handleError = async (problem: Problem) => {
    if (problem.code === 'CONCURRENT_MODIFICATION') {
      setConflict(true);
      return;
    }
    if (problem.code === 'PRODUCT_VERSION_NOT_EDITABLE' || problem.code === 'PRODUCT_RETIRED') {
      await Promise.all([versionQuery.refetch(), productQuery.refetch()]);
      setMessage(problemMessage(t, problem));
      return;
    }
    if (problem.errors.length > 0) {
      setServerErrors(Object.fromEntries(problem.errors.map((e) => [e.field, e.code])));
    }
    setMessage(problemMessage(t, problem));
  };

  const save = async (andContinue: boolean) => {
    setMessage(null);
    if (invalid.size > 0 || Object.keys(localErrors(coverages)).length > 0) {
      setMessage(t('products.editor.fixFields'));
      return;
    }
    setSaving(true);
    try {
      const next =
        step === 'dados'
          ? await updateProductVersion(version.id, {
              vehicleCategories: general.vehicleCategories,
              usages: general.usages,
              changeNotes: general.changeNotes.trim() || undefined,
              version: version.version,
            })
          : await replaceProductVersionCoverages(version.id, {
              coverages: coverages.map(coverageRequest),
              version: version.version,
            });
      queryClient.setQueryData(productVersionQueryKey(version.id), next);
      setServerErrors({});
      setSaved(true);
      const following = editorSteps[stepIndex + 1];
      if (andContinue && following) setStep(following);
    } catch (error) {
      await handleError(getProblem(error));
    } finally {
      setSaving(false);
    }
  };

  const createVersion = async () => {
    setCreating(true);
    setMessage(null);
    try {
      const next = await createProductVersion(productId);
      void queryClient.invalidateQueries({ queryKey: productQueryKey(productId) });
      void queryClient.invalidateQueries({ queryKey: ['products'] });
      navigate(`/seguradora/produtos/${productId}/versoes/${next.id}?passo=dados`);
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setCreating(false);
    }
  };

  const onValidityChange = (field: string, valid: boolean) =>
    setInvalid((current) => {
      const next = new Set(current);
      if (valid) next.delete(field);
      else next.add(field);
      return next;
    });

  const changeGeneral = (next: GeneralDraft) => {
    setGeneral(next);
    setDirty((d) => ({ ...d, general: true }));
  };
  const changeCoverages = (next: CoverageDraft[]) => {
    setCoverages(next);
    setDirty((d) => ({ ...d, coverages: true }));
  };

  const canCreateVersion =
    canManage && !!product && product.status !== 'RETIRED' && !product.draftVersion;
  const group = groupOf(step);

  const stepContent = () => {
    switch (step) {
      case 'dados':
        return (
          <GeneralStep
            version={version}
            draft={general}
            onChange={changeGeneral}
            readOnly={readOnly}
            errors={errors}
          />
        );
      case 'coberturas':
        return (
          <CoveragesStep
            productType={version.productType}
            coverages={coverages}
            onChange={changeCoverages}
            readOnly={readOnly}
            errors={errors}
            minCapital={minCapital}
            onValidityChange={onValidityChange}
          />
        );
      case 'franquias':
        return (
          <DeductiblesStep
            coverages={coverages}
            onChange={changeCoverages}
            readOnly={readOnly}
            errors={errors}
            onValidityChange={onValidityChange}
          />
        );
      case 'documentos':
        return (
          <DocumentsStep
            version={version}
            readOnly={readOnly}
            errors={errors}
            onChanged={() => versionQuery.refetch()}
          />
        );
      default:
        return (
          <PreviewStep
            version={version}
            readOnly={readOnly}
            insurerName={me?.organization?.displayName ?? ''}
            productDescription={product?.description}
            onFix={goTo}
            onPublished={(next) => {
              queryClient.setQueryData(productVersionQueryKey(next.id), next);
              void queryClient.invalidateQueries({ queryKey: productQueryKey(productId) });
              void queryClient.invalidateQueries({ queryKey: ['products'] });
              navigate(`/seguradora/produtos/${productId}`, {
                state: { published: next.versionNo },
              });
            }}
          />
        );
    }
  };

  return (
    <Stack spacing={3} sx={{ maxWidth: 1100, minWidth: 0 }}>
      <Stack spacing={1}>
        <div>
          <Link
            component={RouterLink}
            to={`/seguradora/produtos/${productId}`}
            sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
          >
            <ArrowBack fontSize="small" />
            {t('products.editor.back')}
          </Link>
        </div>
        <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
          {t('products.editor.title', { name: version.productName, n: version.versionNo })}
        </Typography>
        <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
          <Chip
            size="small"
            variant="outlined"
            label={t(`productVersionStatus.${version.status}`)}
          />
          <Button
            variant="outlined"
            size="small"
            startIcon={<Gavel />}
            onClick={() => setRulesOpen(true)}
          >
            {t('underwritingRules.open')}
          </Button>
        </Stack>
      </Stack>
      {rulesOpen && (
        <UnderwritingRulesDialog
          versionId={version.id}
          editable={version.editable}
          onClose={() => setRulesOpen(false)}
        />
      )}

      {!version.editable && version.publishedAt && (
        <Alert
          severity="info"
          data-testid="read-only-notice"
          action={
            canCreateVersion ? (
              <Button
                color="inherit"
                size="small"
                onClick={() => void createVersion()}
                disabled={creating}
              >
                {t('products.detail.newVersion')}
              </Button>
            ) : undefined
          }
        >
          {t('products.editor.publishedNotice', {
            n: version.versionNo,
            date: formatDate(version.publishedAt, i18n.language),
          })}
        </Alert>
      )}
      {!version.editable && !version.publishedAt && (
        <Alert severity="info" data-testid="read-only-notice">
          {t('errors.PRODUCT_RETIRED')}
        </Alert>
      )}
      {version.editable && !canManage && (
        <Alert severity="info" data-testid="read-only-notice">
          {t('products.editor.noPermission')}
        </Alert>
      )}
      {conflict && (
        <Alert
          severity="warning"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={() => void reload()}>
              {t('products.editor.reload')}
            </Button>
          }
        >
          {t('products.editor.conflict')}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert" onClose={() => setMessage(null)}>
          {message}
        </Alert>
      )}

      {wide ? (
        <Stepper nonLinear activeStep={stepIndex} alternativeLabel>
          {editorSteps.map((s) => (
            <Step key={s}>
              <StepButton onClick={() => goTo(s)}>{t(`products.steps.${s}`)}</StepButton>
            </Step>
          ))}
        </Stepper>
      ) : (
        <MobileStepper
          variant="text"
          steps={editorSteps.length}
          position="static"
          activeStep={stepIndex}
          sx={{ bgcolor: 'transparent', px: 0 }}
          backButton={
            <Button
              size="small"
              onClick={() => {
                const previous = editorSteps[stepIndex - 1];
                if (previous) goTo(previous);
              }}
              disabled={stepIndex === 0}
              startIcon={<KeyboardArrowLeft />}
            >
              {t('products.editor.previous')}
            </Button>
          }
          nextButton={
            <Button
              size="small"
              onClick={() => {
                const following = editorSteps[stepIndex + 1];
                if (following) goTo(following);
              }}
              disabled={stepIndex === editorSteps.length - 1}
              endIcon={<KeyboardArrowRight />}
            >
              {t('products.editor.next')}
            </Button>
          }
        />
      )}

      <Paper sx={{ p: { xs: 2, sm: 3 } }}>
        <Stack spacing={3}>
          <Typography variant="h5" component="h2">
            {t(`products.steps.${step}`)}
          </Typography>
          {stepContent()}
          {!readOnly && group && (
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <Button variant="contained" onClick={() => void save(false)} disabled={saving}>
                {t('products.editor.save')}
              </Button>
              <Button variant="outlined" onClick={() => void save(true)} disabled={saving}>
                {t('products.editor.saveAndContinue')}
              </Button>
            </Stack>
          )}
          {step === 'documentos' && (
            <div>
              <Button variant="outlined" onClick={() => setStep('revisao')}>
                {t('products.editor.continue')}
              </Button>
            </div>
          )}
        </Stack>
      </Paper>

      <Dialog open={!!leaving} onClose={() => setLeaving(null)} aria-labelledby="leave-title">
        <DialogTitle id="leave-title">{t('products.editor.unsaved.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('products.editor.unsaved.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setLeaving(null)}>{t('products.editor.unsaved.stay')}</Button>
          <Button color="error" onClick={discard}>
            {t('products.editor.unsaved.leave')}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={saved}
        autoHideDuration={3000}
        onClose={() => setSaved(false)}
        message={t('products.editor.saved')}
      />
    </Stack>
  );
}
