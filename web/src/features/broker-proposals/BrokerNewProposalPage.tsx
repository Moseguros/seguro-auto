import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQueryClient } from '@tanstack/react-query';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import {
  createProposalForClient,
  type BrokerPolicyholderRequest,
  type FieldError,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { ProposalCreateError, quoteProblemCodes } from '../customer-proposals/NewProposalPage';
import { BrokerPolicyholderForm } from '../proposal-wizard/BrokerPolicyholderForm';
import { problemData } from '../proposal-wizard/proposalQuestions';
import { StepActions } from '../proposal-wizard/StepActions';
import { brokerProposalsKey, proposalKey } from '../proposal-wizard/useProposalMutations';
import { proposalPaths } from '../proposal-wizard/wizardSteps';
import { quotePaths } from '../quotes/config';
import { QuoteLoadError, useQuoteRequest } from '../quotes/quoteQueries';
import { BrokerVehicleDriverForm, type VehicleDriverPayload } from './BrokerVehicleDriverForm';

const vehicleFormId = 'broker-vehicle-driver';
const holderFormId = 'broker-policyholder';

/**
 * `/mediador/propostas/nova?pedido=:quoteRequestId&cotacao=:quoteId`: dados do veículo, do
 * condutor e (se o perfil do cliente estiver incompleto) do tomador; cria a proposta e abre-a.
 */
export function BrokerNewProposalPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [params] = useSearchParams();
  const requestId = params.get('pedido') ?? undefined;
  const quoteId = params.get('cotacao') ?? '';
  const request = useQuoteRequest(requestId);
  const payload = useRef<VehicleDriverPayload | null>(null);
  const [needsHolder, setNeedsHolder] = useState(false);
  const [suggested, setSuggested] = useState<{ fullName?: string; birthDate?: string }>();
  const [formErrors, setFormErrors] = useState<FieldError[]>([]);
  const [holderErrors, setHolderErrors] = useState<FieldError[]>([]);
  const [message, setMessage] = useState<string | null>(null);
  const [blocking, setBlocking] = useState<Problem | null>(null);
  const [busy, setBusy] = useState(false);

  async function create(data: VehicleDriverPayload, policyholder?: BrokerPolicyholderRequest) {
    setBusy(true);
    setMessage(null);
    try {
      const proposal = await createProposalForClient({
        quoteId,
        vehicle: data.vehicle,
        driver: data.driver,
        ...(policyholder ? { policyholder } : {}),
      });
      queryClient.setQueryData(proposalKey(proposal.id), proposal);
      void queryClient.invalidateQueries({ queryKey: brokerProposalsKey });
      navigate(`${proposalPaths.broker.list}/${proposal.id}`, { replace: true });
    } catch (error) {
      const problem = getProblem(error);
      const existing = problemData(error).proposalId;
      if (problem.code === 'PROPOSAL_ALREADY_EXISTS' && typeof existing === 'string') {
        navigate(`${proposalPaths.broker.list}/${existing}`, { replace: true });
        return;
      }
      const first = problem.errors[0];
      if (first?.field === 'policyholder' && first.code === 'REQUIRED') {
        setNeedsHolder(true);
        setSuggested(
          data.driver.isOwner
            ? { fullName: data.driver.fullName, birthDate: data.driver.birthDate }
            : undefined,
        );
        return;
      }
      const holder = problem.errors.filter((e) => e.field.startsWith('policyholder.'));
      const fields = problem.errors.filter(
        (e) => e.field.startsWith('vehicle.') || e.field.startsWith('driver.'),
      );
      if (holder.length > 0 || fields.length > 0) {
        setHolderErrors(holder);
        setFormErrors(fields);
        return;
      }
      if (problem.code === 'VEHICLE_ALREADY_REGISTERED') {
        setFormErrors([{ field: 'vehicle.chassisNumber', code: 'VEHICLE_ALREADY_REGISTERED' }]);
        return;
      }
      if (quoteProblemCodes.has(problem.code)) {
        setBlocking(problem);
        return;
      }
      setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  function onVehicleDriver(data: VehicleDriverPayload) {
    payload.current = data;
    if (needsHolder) {
      const form = document.getElementById(holderFormId) as HTMLFormElement | null;
      form?.requestSubmit();
      return;
    }
    return create(data);
  }

  let content;
  if (!requestId) {
    content = (
      <ProposalCreateError
        problem={{ status: 404, code: 'QUOTE_NOT_FOUND', errors: [] }}
        mode="broker"
        onRetry={() => undefined}
      />
    );
  } else if (request.isPending) content = <LoadingScreen />;
  else if (request.isError) {
    content = (
      <QuoteLoadError
        error={request.error}
        paths={quotePaths.broker}
        onRetry={() => void request.refetch()}
      />
    );
  } else if (blocking) {
    content = (
      <ProposalCreateError problem={blocking} mode="broker" onRetry={() => setBlocking(null)} />
    );
  } else {
    content = (
      <Stack spacing={3}>
        <Card>
          <CardContent>
            <BrokerVehicleDriverForm
              formId={vehicleFormId}
              request={request.data}
              serverErrors={formErrors}
              onSubmit={onVehicleDriver}
            />
          </CardContent>
        </Card>
        {needsHolder && (
          <Card>
            <CardContent>
              <Stack spacing={2}>
                <Typography variant="h6" component="h2">
                  {t('brokerProposals.form.policyholder')}
                </Typography>
                <Alert severity="warning">{t('brokerProposals.form.profileIncomplete')}</Alert>
                <BrokerPolicyholderForm
                  formId={holderFormId}
                  suggested={suggested}
                  serverErrors={holderErrors}
                  onSubmit={(policyholder) =>
                    payload.current ? create(payload.current, policyholder) : undefined
                  }
                />
              </Stack>
            </CardContent>
          </Card>
        )}
        {message && (
          <Alert severity="error" role="alert">
            {message}
          </Alert>
        )}
        <StepActions onBack={() => navigate(-1)}>
          <Button type="submit" form={vehicleFormId} variant="contained" disabled={busy}>
            {t('proposal.wizard.continue')}
          </Button>
        </StepActions>
      </Stack>
    );
  }

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('brokerProposals.new.title')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
