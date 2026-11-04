import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { InsurerProposalResponse } from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { AcceptDialog } from './AcceptDialog';
import { acceptBlocker, type DecisionOutcome } from './decisionSupport';
import { RejectDialog } from './RejectDialog';
import { RequestInfoDialog } from './RequestInfoDialog';

export interface DecisionBarProps {
  proposal: InsurerProposalResponse;
  /** Abaixo de 600 px a barra fica fixa no fundo do ecrã. */
  fixed?: boolean;
  onOutcome(outcome: DecisionOutcome): void;
}

type Open = 'accept' | 'reject' | 'info' | null;

/** "Aceitar", "Pedir informação" e "Recusar": só com `proposal:decide` e `UNDER_REVIEW`. */
export function DecisionBar({ proposal, fixed = false, onOutcome }: DecisionBarProps) {
  const { t } = useTranslation();
  const canDecide = usePermission(Permissions.proposalDecide);
  const [open, setOpen] = useState<Open>(null);
  if (!canDecide || proposal.status !== 'UNDER_REVIEW') return null;

  const blocker = acceptBlocker(proposal);
  const explanation = blocker ? t(`errors.${blocker}`) : '';
  const outcome = (result: DecisionOutcome) => {
    setOpen(null);
    onOutcome(result);
  };

  const buttons = (
    <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap" justifyContent="flex-end">
      <Tooltip title={explanation}>
        {/* O `span` deixa o `Tooltip` funcionar com o botão desactivado. */}
        <span>
          <Button
            variant="contained"
            onClick={() => setOpen('accept')}
            disabled={!!blocker}
            aria-describedby={blocker ? 'accept-blocker' : undefined}
          >
            {t('insurerProposals.actions.accept')}
          </Button>
        </span>
      </Tooltip>
      <Button variant="outlined" onClick={() => setOpen('info')}>
        {t('insurerProposals.actions.requestInfo')}
      </Button>
      <Button variant="outlined" color="error" onClick={() => setOpen('reject')}>
        {t('insurerProposals.actions.reject')}
      </Button>
    </Stack>
  );
  const note = blocker && (
    <Typography id="accept-blocker" variant="caption" color="text.secondary" component="p">
      {explanation}
    </Typography>
  );

  return (
    <>
      {fixed ? (
        <Paper
          elevation={8}
          square
          data-testid="decision-bar"
          sx={{
            position: 'fixed',
            left: 0,
            right: 0,
            bottom: 0,
            zIndex: (theme) => theme.zIndex.appBar,
            px: 2,
            py: 1,
            maxWidth: '100vw',
            boxSizing: 'border-box',
          }}
        >
          <Stack spacing={0.5}>
            {buttons}
            {note}
          </Stack>
        </Paper>
      ) : (
        <Stack spacing={0.5} alignItems="flex-end" data-testid="decision-bar">
          {buttons}
          {note}
        </Stack>
      )}
      {open === 'accept' && (
        <AcceptDialog proposal={proposal} onClose={() => setOpen(null)} onOutcome={outcome} />
      )}
      {open === 'reject' && (
        <RejectDialog proposal={proposal} onClose={() => setOpen(null)} onOutcome={outcome} />
      )}
      {open === 'info' && (
        <RequestInfoDialog proposal={proposal} onClose={() => setOpen(null)} onOutcome={outcome} />
      )}
    </>
  );
}
