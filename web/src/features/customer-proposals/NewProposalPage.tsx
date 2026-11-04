import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import { useQueryClient } from '@tanstack/react-query';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation, useNavigate, useSearchParams } from 'react-router-dom';
import { createProposal } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { problemData } from '../proposal-wizard/proposalQuestions';
import { proposalKey } from '../proposal-wizard/useProposalMutations';
import { proposalPaths, type ProposalMode } from '../proposal-wizard/wizardSteps';

/** Erros da criação que pedem uma nova simulação (botão "Simular seguro"). */
export const quoteProblemCodes = new Set([
  'QUOTE_EXPIRED',
  'QUOTE_NOT_AVAILABLE',
  'QUOTE_ALREADY_USED',
  'QUOTE_DATA_CHANGED',
  'QUOTE_NOT_FOUND',
]);

/** Erro ao criar a proposta: "Simular seguro" (cotação) ou "Tentar de novo". */
export function ProposalCreateError({
  problem,
  mode,
  onRetry,
}: {
  problem: Problem;
  mode: ProposalMode;
  onRetry(): void;
}) {
  const { t } = useTranslation();
  const quoteProblem = quoteProblemCodes.has(problem.code);
  return (
    <Stack spacing={2} alignItems="flex-start">
      <Alert
        severity={quoteProblem ? 'warning' : 'error'}
        role="alert"
        sx={{ alignSelf: 'stretch' }}
      >
        {problemMessage(t, problem)}
      </Alert>
      {quoteProblem ? (
        <Button variant="cta" component={RouterLink} to={proposalPaths[mode].quotes}>
          {t('proposal.status.simulate')}
        </Button>
      ) : (
        <Button variant="outlined" onClick={onRetry}>
          {t('common.retry')}
        </Button>
      )}
    </Stack>
  );
}

/** `/cliente/propostas/nova?cotacao=:quoteId`: cria a proposta e abre-a. */
export function NewProposalPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const queryClient = useQueryClient();
  const [params] = useSearchParams();
  const quoteId = params.get('cotacao') ?? '';
  const [problem, setProblem] = useState<Problem | null>(null);
  const [attempt, setAttempt] = useState(0);
  const started = useRef<number | null>(null);

  useEffect(() => {
    // Uma só criação por tentativa (o StrictMode corre os efeitos duas vezes).
    if (started.current === attempt) return;
    started.current = attempt;
    if (!quoteId) {
      setProblem({ status: 404, code: 'QUOTE_NOT_FOUND', errors: [] });
      return;
    }
    setProblem(null);
    createProposal({ quoteId, channel: 'WEB' })
      .then((proposal) => {
        queryClient.setQueryData(proposalKey(proposal.id), proposal);
        navigate(`${proposalPaths.customer.list}/${proposal.id}`, { replace: true });
      })
      .catch((error: unknown) => {
        const found = getProblem(error);
        if (found.code === 'PROFILE_INCOMPLETE') {
          const back = encodeURIComponent(`${location.pathname}${location.search}`);
          navigate(`/cliente/perfil?completar=1&voltar=${back}`, { replace: true });
          return;
        }
        const existing = problemData(error).proposalId;
        if (found.code === 'PROPOSAL_ALREADY_EXISTS' && typeof existing === 'string') {
          navigate(`${proposalPaths.customer.list}/${existing}`, { replace: true });
          return;
        }
        setProblem(found);
      });
  }, [attempt, quoteId, navigate, location.pathname, location.search, queryClient]);

  return (
    <Container maxWidth="sm" disableGutters>
      {problem ? (
        <ProposalCreateError
          problem={problem}
          mode="customer"
          onRetry={() => setAttempt((value) => value + 1)}
        />
      ) : (
        <LoadingScreen />
      )}
    </Container>
  );
}
