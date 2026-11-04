import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { ProposalStatusView } from './ProposalStatusView';
import { ProposalWizard } from './ProposalWizard';
import { useProposal } from './useProposalMutations';
import { isEditable, proposalPaths, type ProposalMode } from './wizardSteps';

/** `<lista>/:id`: os passos (proposta editável) ou o estado. */
export function ProposalScreen({ mode }: { mode: ProposalMode }) {
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
    content = <ProposalStatusView proposal={query.data} mode={mode} />;
  }

  return (
    <Container maxWidth="md" disableGutters>
      {content}
    </Container>
  );
}
