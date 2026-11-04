import CheckCircle from '@mui/icons-material/CheckCircle';
import ListIcon from '@mui/icons-material/FormatListNumbered';
import RadioButtonUnchecked from '@mui/icons-material/RadioButtonUnchecked';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Chip from '@mui/material/Chip';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import MobileStepper from '@mui/material/MobileStepper';
import Stack from '@mui/material/Stack';
import Step from '@mui/material/Step';
import StepButton from '@mui/material/StepButton';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useCallback, useMemo, useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import type { ProposalResponse } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { DeclarationsStep } from './DeclarationsStep';
import { IdentityStep } from './IdentityStep';
import { InfoRequestBanner } from './InfoRequestBanner';
import { InspectionStep } from './InspectionStep';
import { PolicyholderStep } from './PolicyholderStep';
import { ReviewStep } from './ReviewStep';
import { SuitabilityStep } from './SuitabilityStep';
import {
  useProposalMutations,
  WizardContext,
  type WizardContextValue,
} from './useProposalMutations';
import {
  firstPendingStep,
  isStepDone,
  isWizardStep,
  nextStep,
  previousStep,
  visibleSteps,
  type ProposalMode,
  type WizardStep,
} from './wizardSteps';

const kycSkipKey = (id: string) => `moseguros.proposal.kycSkipped.${id}`;

function readKycSkipped(id: string): boolean {
  try {
    return sessionStorage.getItem(kycSkipKey(id)) === '1';
  } catch {
    return false;
  }
}

/**
 * Passos de uma proposta editável (`DRAFT`/`INFO_REQUESTED`), partilhados pelo cliente e pelo
 * mediador (S4-08 §3). O passo vem de `?passo=`; sem ele abre o primeiro por concluir.
 */
export function ProposalWizard({
  proposal,
  mode,
}: {
  proposal: ProposalResponse;
  mode: ProposalMode;
}) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const wide = useMediaQuery(theme.breakpoints.up('sm'), { noSsr: true });
  const [params, setParams] = useSearchParams();
  const [kycSkipped, setKycSkipped] = useState(() => readKycSkipped(proposal.id));
  const [menuAnchor, setMenuAnchor] = useState<HTMLElement | null>(null);
  const mutations = useProposalMutations(proposal.id);

  const steps = visibleSteps(proposal);
  const requested = params.get('passo');
  const current: WizardStep =
    isWizardStep(requested) && steps.includes(requested)
      ? requested
      : firstPendingStep(proposal, kycSkipped);
  const index = steps.indexOf(current);
  const pending = firstPendingStep(proposal, kycSkipped);
  const canOpen = (step: WizardStep) =>
    step === current ||
    isStepDone(step, proposal, kycSkipped) ||
    steps.indexOf(step) <= steps.indexOf(pending);

  const goTo = useCallback(
    (step: WizardStep) => {
      setParams({ passo: step });
      window.scrollTo?.({ top: 0 });
    },
    [setParams],
  );

  const skipKyc = useCallback(() => {
    setKycSkipped(true);
    try {
      sessionStorage.setItem(kycSkipKey(proposal.id), '1');
    } catch {
      // Sem armazenamento: vale só nesta página.
    }
  }, [proposal.id]);

  const back = previousStep(current, proposal);
  const context = useMemo<WizardContextValue>(
    () => ({
      proposal,
      mode,
      current,
      update: mutations.update,
      handleCommon: mutations.handleCommon,
      reload: mutations.reload,
      goTo,
      next: () => goTo(nextStep(current, proposal)),
      back: back ? () => goTo(back) : undefined,
      kycSkipped,
      skipKyc,
    }),
    [proposal, mode, current, mutations, goTo, back, kycSkipped, skipKyc],
  );

  const label = (step: WizardStep) => t(`proposal.steps.${step}`);

  let content: ReactNode;
  switch (current) {
    case 'tomador':
      content = <PolicyholderStep />;
      break;
    case 'declaracoes':
      content = <DeclarationsStep />;
      break;
    case 'adequacao':
      content = <SuitabilityStep />;
      break;
    case 'vistoria':
      content = <InspectionStep />;
      break;
    case 'identidade':
      content = <IdentityStep />;
      break;
    case 'resumo':
      content = <ReviewStep />;
      break;
  }

  return (
    <WizardContext.Provider value={context}>
      <Stack spacing={2.5} sx={{ minWidth: 0 }}>
        <Stack spacing={0.5}>
          <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
            {t('proposal.wizard.title', { number: proposal.number })}
          </Typography>
          <Stack direction="row" spacing={1} alignItems="center" useFlexGap flexWrap="wrap">
            <Chip size="small" label={t(`proposalStatus.${proposal.status}`)} />
            <Typography color="text.secondary">
              {proposal.insurer?.displayName} · {formatMoney(proposal.premium.total, i18n.language)}
            </Typography>
          </Stack>
        </Stack>

        {mutations.notice && (
          <Alert severity="warning" role="alert" onClose={mutations.clearNotice}>
            {mutations.notice}
          </Alert>
        )}
        {proposal.status === 'INFO_REQUESTED' && proposal.decision?.infoRequest && (
          <InfoRequestBanner
            insurerName={proposal.insurer?.displayName ?? ''}
            request={proposal.decision.infoRequest}
            onOpen={goTo}
          />
        )}

        {wide ? (
          <Stepper nonLinear activeStep={index} alternativeLabel sx={{ overflowX: 'auto' }}>
            {steps.map((step) => (
              <Step
                key={step}
                completed={step !== current && isStepDone(step, proposal, kycSkipped)}
              >
                <StepButton onClick={() => goTo(step)} disabled={!canOpen(step)}>
                  {label(step)}
                </StepButton>
              </Step>
            ))}
          </Stepper>
        ) : (
          <Stack spacing={1}>
            <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={1}>
              <Typography variant="subtitle1" component="p" fontWeight={600}>
                {t('proposal.wizard.stepOf', { n: index + 1, total: steps.length })} ·{' '}
                {label(current)}
              </Typography>
              <Button
                size="small"
                startIcon={<ListIcon />}
                aria-haspopup="menu"
                onClick={(event) => setMenuAnchor(event.currentTarget)}
              >
                {t('proposal.wizard.allSteps')}
              </Button>
            </Stack>
            <MobileStepper
              variant="progress"
              steps={steps.length}
              activeStep={index}
              position="static"
              backButton={null}
              nextButton={null}
              sx={{ p: 0, bgcolor: 'transparent', '& .MuiMobileStepper-progress': { width: 1 } }}
            />
            <Menu anchorEl={menuAnchor} open={!!menuAnchor} onClose={() => setMenuAnchor(null)}>
              {steps.map((step, i) => (
                <MenuItem
                  key={step}
                  selected={step === current}
                  disabled={!canOpen(step)}
                  onClick={() => {
                    setMenuAnchor(null);
                    goTo(step);
                  }}
                >
                  <ListItemIcon>
                    {isStepDone(step, proposal, kycSkipped) ? (
                      <CheckCircle color="success" fontSize="small" />
                    ) : (
                      <RadioButtonUnchecked fontSize="small" />
                    )}
                  </ListItemIcon>
                  <ListItemText>{`${i + 1}. ${label(step)}`}</ListItemText>
                </MenuItem>
              ))}
            </Menu>
          </Stack>
        )}

        <Box key={current}>{content}</Box>
      </Stack>
    </WizardContext.Provider>
  );
}
