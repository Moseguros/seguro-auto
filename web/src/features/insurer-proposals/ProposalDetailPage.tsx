import ArrowBack from '@mui/icons-material/ArrowBack';
import Download from '@mui/icons-material/Download';
import ExpandMore from '@mui/icons-material/ExpandMore';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import Link from '@mui/material/Link';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import {
  Link as RouterLink,
  Navigate,
  useLocation,
  useNavigate,
  useParams,
} from 'react-router-dom';
import {
  getInsurerInspection,
  getInsurerProposal,
  type InsurerProposalResponse,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen, noAccessPath } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { downloadProtectedFile } from '../../files/downloadProtectedFile';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { InspectionPanel } from '../insurer-inspections/InspectionPanel';
import {
  Field,
  insurerInspectionsKey,
  insurerKycKey,
  insurerProposalKey,
  insurerProposalsKey,
  NavigationNotice,
  QueueError,
  type QueueNavigationState,
} from './common';
import { DecisionBar } from './DecisionBar';
import { DeclarationsPanel } from './DeclarationsPanel';
import type { DecisionOutcome } from './decisionSupport';
import { ProposalSummaryCard } from './ProposalSummaryCard';
import { proposalsPath } from './ProposalsTabs';
import { SignaturePanel } from './SignaturePanel';
import { SlaChip } from './SlaChip';
import { SuitabilityPanel } from './SuitabilityPanel';
import { UnderwritingPanel } from './UnderwritingPanel';

type Notice = { severity: 'success' | 'warning' | 'error'; text: string };

const sectionIds = [
  'resumo',
  'declaracoes',
  'adequacao',
  'vistoria',
  'identidade',
  'regras',
  'assinatura',
  'decisao',
] as const;
type SectionId = (typeof sectionIds)[number];

function Section({
  id,
  title,
  expanded,
  onToggle,
  children,
}: {
  id: SectionId;
  title: string;
  expanded: boolean;
  onToggle(id: SectionId, expanded: boolean): void;
  children: ReactNode;
}) {
  return (
    <Accordion
      id={id}
      expanded={expanded}
      onChange={(_event, value) => onToggle(id, value)}
      disableGutters
      slotProps={{ transition: { unmountOnExit: true } }}
      sx={{ scrollMarginTop: 80 }}
    >
      <AccordionSummary
        expandIcon={<ExpandMore />}
        aria-controls={`${id}-content`}
        id={`${id}-header`}
      >
        <Typography variant="h6" component="h2">
          {title}
        </Typography>
      </AccordionSummary>
      <AccordionDetails id={`${id}-content`}>{children}</AccordionDetails>
    </Accordion>
  );
}

/** `/seguradora/propostas/:id`: com `proposal:read` o detalhe; só com `inspection:review` a vistoria. */
export function ProposalDetailRoute() {
  const canRead = usePermission(Permissions.proposalRead);
  const canInspect = usePermission(Permissions.inspectionReview);
  if (canRead) return <ProposalDetailPage />;
  if (canInspect) return <InspectionOnlyPage />;
  return <Navigate to={noAccessPath} replace />;
}

function useQueueBack() {
  const location = useLocation();
  const state = (location.state as QueueNavigationState | null) ?? {};
  return { state, queueSearch: state.queueSearch ?? '' };
}

/** Detalhe da proposta com as secções, a barra de decisão e a vistoria. */
export function ProposalDetailPage() {
  const { t, i18n } = useTranslation();
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const queryClient = useQueryClient();
  const { queueSearch } = useQueueBack();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const canKyc = usePermission(Permissions.kycReview);
  const wantsInspection = location.hash === '#vistoria';
  const [expanded, setExpanded] = useState<Record<SectionId, boolean>>(
    () =>
      Object.fromEntries(
        sectionIds.map((section) => [
          section,
          !narrow || section === 'resumo' || (section === 'vistoria' && wantsInspection),
        ]),
      ) as Record<SectionId, boolean>,
  );
  const [notice, setNotice] = useState<Notice | null>(null);
  const [toast, setToast] = useState<string | null>(null);
  const [downloadError, setDownloadError] = useState<string | null>(null);

  const query = useQuery({
    queryKey: insurerProposalKey(id),
    queryFn: ({ signal }) => getInsurerProposal(id, undefined, signal),
  });
  const proposal = query.data;

  useEffect(() => {
    if (!wantsInspection || !proposal) return;
    setExpanded((current) => ({ ...current, vistoria: true }));
    const timer = setTimeout(
      () => document.getElementById('vistoria')?.scrollIntoView?.({ block: 'start' }),
      50,
    );
    return () => clearTimeout(timer);
  }, [wantsInspection, proposal]);

  const invalidateLists = () => {
    void queryClient.invalidateQueries({ queryKey: insurerProposalsKey });
    void queryClient.invalidateQueries({ queryKey: insurerInspectionsKey });
    void queryClient.invalidateQueries({ queryKey: insurerKycKey });
  };

  const onOutcome = (outcome: DecisionOutcome) => {
    if (outcome.kind === 'reload') {
      setNotice({ severity: 'warning', text: outcome.message });
      void query.refetch();
      return;
    }
    invalidateLists();
    queryClient.removeQueries({ queryKey: insurerProposalKey(id) });
    navigate(`${proposalsPath}${queueSearch}`, {
      state: { notice: outcome.notice } satisfies QueueNavigationState,
    });
  };

  const onInspectionChanged = async (kind: 'approved' | 'rejected') => {
    invalidateLists();
    const fresh = await query.refetch();
    if (kind === 'rejected') setToast(t('insurerInspections.panel.rejected'));
    else if (fresh.data?.status === 'ACCEPTED') {
      setNotice({ severity: 'success', text: t('insurerInspections.panel.approvedAndAccepted') });
    } else setToast(t('insurerInspections.panel.approved'));
  };

  const onConflict = (message: string) => {
    setNotice({ severity: 'warning', text: message });
    void query.refetch();
  };

  if (query.isLoading) return <LoadingScreen />;
  if (!proposal) {
    return (
      <Stack spacing={2}>
        <BackLink queueSearch={queueSearch} />
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      </Stack>
    );
  }

  const toggle = (section: SectionId, value: boolean) =>
    setExpanded((current) => ({ ...current, [section]: value }));
  const download = async () => {
    if (!proposal.signature) return;
    setDownloadError(null);
    try {
      await downloadProtectedFile(proposal.signature.documentFileId, `${proposal.number}.pdf`);
    } catch (error) {
      setDownloadError(problemMessage(t, getProblem(error)));
    }
  };
  const decisionBar = <DecisionBar proposal={proposal} fixed={narrow} onOutcome={onOutcome} />;
  const showBar = proposal.status === 'UNDER_REVIEW';

  return (
    <Stack spacing={2} sx={{ minWidth: 0, pb: narrow && showBar ? 16 : 0 }}>
      <BackLink queueSearch={queueSearch} />
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ sm: 'flex-start' }}
      >
        <Stack spacing={1} sx={{ minWidth: 0 }}>
          <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
            {proposal.number}
          </Typography>
          <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap" alignItems="center">
            <Chip
              size="small"
              variant="outlined"
              label={t(`proposalStatus.${proposal.status}`)}
              data-status={proposal.status}
            />
            <SlaChip status={proposal.sla.status} dueAt={proposal.sla.dueAt} />
          </Stack>
          <Typography>
            {t('insurerProposals.detail.policyholder')}:{' '}
            <strong>{proposal.policyholder.fullName}</strong>
          </Typography>
          <Typography sx={{ fontVariantNumeric: 'tabular-nums' }}>
            {t('insurerProposals.detail.total')}:{' '}
            <strong>{formatMoney(proposal.premium.total, i18n.language)}</strong>
          </Typography>
          {proposal.signature && (
            <div>
              <Button variant="outlined" startIcon={<Download />} onClick={() => void download()}>
                {t('insurerProposals.detail.downloadPdf')}
              </Button>
            </div>
          )}
          {downloadError && (
            <Alert severity="error" role="alert">
              {downloadError}
            </Alert>
          )}
        </Stack>
        {!narrow && decisionBar}
      </Stack>

      {notice && (
        <Alert severity={notice.severity} role="alert" onClose={() => setNotice(null)}>
          {notice.text}
        </Alert>
      )}

      <Box>
        <Section
          id="resumo"
          title={t('insurerProposals.sections.summary')}
          expanded={expanded.resumo}
          onToggle={toggle}
        >
          <ProposalSummaryCard proposal={proposal} />
        </Section>
        <Section
          id="declaracoes"
          title={t('insurerProposals.sections.declarations')}
          expanded={expanded.declaracoes}
          onToggle={toggle}
        >
          <DeclarationsPanel proposal={proposal} />
        </Section>
        <Section
          id="adequacao"
          title={t('insurerProposals.sections.suitability')}
          expanded={expanded.adequacao}
          onToggle={toggle}
        >
          <SuitabilityPanel proposal={proposal} />
        </Section>
        {proposal.inspection.required && (
          <Section
            id="vistoria"
            title={t('insurerProposals.sections.inspection')}
            expanded={expanded.vistoria}
            onToggle={toggle}
          >
            <InspectionPanel
              inspection={proposal.inspection}
              proposalStatus={proposal.status}
              onChanged={(kind) => void onInspectionChanged(kind)}
              onConflict={onConflict}
            />
          </Section>
        )}
        <Section
          id="identidade"
          title={t('insurerProposals.sections.identity')}
          expanded={expanded.identidade}
          onToggle={toggle}
        >
          <IdentitySection proposal={proposal} canKyc={canKyc} queueSearch={queueSearch} />
        </Section>
        <Section
          id="regras"
          title={t('insurerProposals.sections.underwriting')}
          expanded={expanded.regras}
          onToggle={toggle}
        >
          <UnderwritingPanel result={proposal.underwriting} />
        </Section>
        <Section
          id="assinatura"
          title={t('insurerProposals.sections.signature')}
          expanded={expanded.assinatura}
          onToggle={toggle}
        >
          <SignaturePanel signatures={proposal.signatures} />
        </Section>
        {!showBar && (
          <Section
            id="decisao"
            title={t('insurerProposals.sections.decision')}
            expanded={expanded.decisao}
            onToggle={toggle}
          >
            <DecisionBlock proposal={proposal} />
          </Section>
        )}
      </Box>
      {narrow && decisionBar}
      <NavigationNotice />
      <Snackbar
        open={!!toast}
        autoHideDuration={6000}
        onClose={() => setToast(null)}
        message={toast}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
      />
    </Stack>
  );
}

function BackLink({ queueSearch }: { queueSearch: string }) {
  const { t } = useTranslation();
  return (
    <div>
      <Link
        component={RouterLink}
        to={`${proposalsPath}${queueSearch}`}
        sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
      >
        <ArrowBack fontSize="small" />
        {t('insurerProposals.detail.back')}
      </Link>
    </div>
  );
}

function IdentitySection({
  proposal,
  canKyc,
  queueSearch,
}: {
  proposal: InsurerProposalResponse;
  canKyc: boolean;
  queueSearch: string;
}) {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const kyc = proposal.kyc;
  const otherInsurer = kyc.state === 'VERIFIED' && !kyc.checkId && !kyc.verifiedByThisInsurer;

  return (
    <Stack spacing={1.5}>
      <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
        <Typography variant="body2">{t('insurerProposals.identity.state')}</Typography>
        <Chip
          size="small"
          color={
            kyc.state === 'VERIFIED' ? 'success' : kyc.state === 'REJECTED' ? 'error' : 'default'
          }
          label={t(`kyc.state.${kyc.state}`)}
        />
      </Stack>
      {otherInsurer && (
        <Typography variant="body2">
          {kyc.verifiedAt
            ? t('insurerProposals.identity.otherInsurer', {
                date: formatDateTime(kyc.verifiedAt, i18n.language),
              })
            : t('insurerProposals.identity.otherInsurerNoDate')}
        </Typography>
      )}
      {!otherInsurer && kyc.verifiedAt && (
        <Typography variant="body2">
          {t('insurerProposals.identity.verifiedAt', {
            date: formatDateTime(kyc.verifiedAt, i18n.language),
          })}
        </Typography>
      )}
      {kyc.requiredForAutomaticAcceptance && kyc.state !== 'VERIFIED' && (
        <Alert severity="info">{t('insurerProposals.identity.required')}</Alert>
      )}
      {kyc.checkId && canKyc && (
        <div>
          <Button
            variant="outlined"
            onClick={() =>
              navigate(`/seguradora/propostas/kyc/${kyc.checkId}`, {
                state: { returnTo: location.pathname, queueSearch } satisfies QueueNavigationState,
              })
            }
          >
            {t('insurerProposals.identity.review')}
          </Button>
        </div>
      )}
      {!kyc.checkId && canKyc && kyc.state === 'PENDING' && (
        <Typography variant="body2" color="text.secondary">
          {t('insurerProposals.identity.pendingQueue')}
        </Typography>
      )}
    </Stack>
  );
}

function DecisionBlock({ proposal }: { proposal: InsurerProposalResponse }) {
  const { t, i18n } = useTranslation();
  const decision = proposal.decision;
  if (!decision) {
    return <Typography color="text.secondary">{t('insurerProposals.decision.none')}</Typography>;
  }
  const info = decision.infoRequest;
  return (
    <Stack spacing={2}>
      <Box
        component="dl"
        sx={{
          m: 0,
          display: 'grid',
          gap: 1.5,
          gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
        }}
      >
        {decision.mode && (
          <Field label={t('insurerProposals.decision.mode')}>
            {t(`insurerProposals.decision.modeValue.${decision.mode}`)}
          </Field>
        )}
        {decision.decidedAt && (
          <Field label={t('insurerProposals.decision.decidedAt')}>
            {formatDateTime(decision.decidedAt, i18n.language)}
          </Field>
        )}
        {decision.rejectionReasonCode && (
          <Field label={t('insurerProposals.decision.reasonCode')}>
            {t(`rejectionReason.${decision.rejectionReasonCode}`)}
          </Field>
        )}
        {decision.reason && (
          <Field
            label={
              proposal.status === 'REJECTED'
                ? t('insurerProposals.decision.reason')
                : t('insurerProposals.decision.note')
            }
          >
            {decision.reason}
          </Field>
        )}
      </Box>
      {info && (
        <Stack spacing={0.5}>
          <Typography variant="subtitle2" component="h3">
            {t('insurerProposals.decision.infoRequest')}
          </Typography>
          <Typography variant="caption" color="text.secondary">
            {t('insurerProposals.decision.requestedAt', {
              date: formatDateTime(info.requestedAt, i18n.language),
            })}
          </Typography>
          <Typography variant="body2">
            {info.items.map((item) => t(`infoRequestItem.${item}`)).join(', ')}
          </Typography>
          <Typography variant="body2" sx={{ wordBreak: 'break-word' }}>
            {info.message}
          </Typography>
        </Stack>
      )}
    </Stack>
  );
}

/** Sem `proposal:read`: só a vistoria (aberta a partir da fila das vistorias). */
function InspectionOnlyPage() {
  const { t } = useTranslation();
  const { state } = useQueueBack();
  const queryClient = useQueryClient();
  const [notice, setNotice] = useState<Notice | null>(null);
  const inspectionId = state.inspectionId;
  const query = useQuery({
    queryKey: ['insurer-inspection', inspectionId],
    queryFn: ({ signal }) => getInsurerInspection(inspectionId as string, undefined, signal),
    enabled: !!inspectionId,
  });
  if (!inspectionId) return <Navigate to="/seguradora/propostas/vistorias" replace />;
  if (query.isLoading) return <LoadingScreen />;
  if (!query.data) {
    return <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  }
  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <div>
        <Link
          component={RouterLink}
          to="/seguradora/propostas/vistorias"
          sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
        >
          <ArrowBack fontSize="small" />
          {t('insurerInspections.title')}
        </Link>
      </div>
      <Typography variant="h4" component="h1">
        {t('insurerProposals.sections.inspection')}
      </Typography>
      <Alert severity="info">{t('insurerProposals.detail.inspectionOnly')}</Alert>
      {notice && (
        <Alert severity={notice.severity} role="alert" onClose={() => setNotice(null)}>
          {notice.text}
        </Alert>
      )}
      <Box id="vistoria">
        <InspectionPanel
          inspection={query.data}
          onChanged={(kind) => {
            void queryClient.invalidateQueries({ queryKey: insurerInspectionsKey });
            void query.refetch();
            setNotice({
              severity: 'success',
              text:
                kind === 'approved'
                  ? t('insurerInspections.panel.approved')
                  : t('insurerInspections.panel.rejected'),
            });
          }}
          onConflict={(message) => {
            setNotice({ severity: 'warning', text: message });
            void query.refetch();
          }}
        />
      </Box>
    </Stack>
  );
}
