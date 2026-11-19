import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import type { ProposalResponse } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { ProposalStatusView } from './ProposalStatusView';
import { ProposalWizard } from './ProposalWizard';
import { useProposal } from './useProposalMutations';
import { isEditable, proposalPaths, type ProposalMode } from './wizardSteps';

/**
 * `<lista>/:id`: os passos (proposta editável) ou o estado. `acceptedAction` é a acção de uma
 * proposta aceite (S5-11: "Adquirir apólice" do cliente, link de pagamento do mediador).
 */
export function ProposalScreen({
  mode,
  acceptedAction,
}: {
  mode: ProposalMode;
  acceptedAction?: (proposal: ProposalResponse) => ReactNode;
}) {
  const { t } = useTranslation();
  const { id } = useParams();
  const query = useProposal(id);

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = (
      <Stack spacing={2}>
        <Alert
          severity="error"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={() => void query.refetch()}>
              {t('common.retry')}
            </Button>
          }
        >
          {problemMessage(t, getProblem(query.error))}
        </Alert>
        <Button
          variant="text"
          component={RouterLink}
          to={proposalPaths[mode].list}
          sx={{ alignSelf: 'flex-start' }}
        >
          {t('proposal.status.backToList')}
        </Button>
      </Stack>
    );
  } else if (isEditable(query.data.status)) {
    content = <ProposalWizard proposal={query.data} mode={mode} />;
  } else {
    content = (
      <ProposalStatusView
        proposal={query.data}
        mode={mode}
        acceptedAction={acceptedAction?.(query.data)}
      />
    );
  }

  return (
    <Container maxWidth="md" disableGutters>
      {content}
    </Container>
  );
}
