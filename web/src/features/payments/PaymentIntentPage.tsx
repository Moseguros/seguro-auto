import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import CircularProgress from '@mui/material/CircularProgress';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQueryClient } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import {
  getGetProposalQueryKey,
  getListProposalPaymentMethodsQueryKey,
  type IntentView,
  type PaymentStatus,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { AwaitConfirmation } from './AwaitConfirmation';
import { CheckingStatus } from './CheckingStatus';
import { PaymentOutcome } from './PaymentOutcome';
import { paymentPaths, proposalIdOf } from './paymentPaths';
import { ReferenceDetails } from './ReferenceDetails';
import { isFinalStatus, usePaymentIntentPolling } from './usePaymentIntentPolling';

/** Cor da etiqueta de estado de um pagamento. */
export function paymentStatusColor(
  status: PaymentStatus,
): 'success' | 'error' | 'warning' | 'default' {
  switch (status) {
    case 'SUCCEEDED':
      return 'success';
    case 'FAILED':
      return 'error';
    case 'UNKNOWN':
      return 'warning';
    default:
      return 'default';
  }
}

/** O conteúdo do pagamento por estado e `nextAction.type` (S5-11 §4). */
function IntentBody({ intent, reload }: { intent: IntentView; reload: () => void }) {
  const { t } = useTranslation();
  if (isFinalStatus(intent.status)) return <PaymentOutcome intent={intent} />;
  switch (intent.nextAction.type) {
    case 'AWAIT_CUSTOMER_CONFIRMATION':
      return <AwaitConfirmation intent={intent} />;
    case 'CHECKING_STATUS':
      return <CheckingStatus nextCheckAt={intent.nextAction.nextCheckAt} />;
    case 'PAY_BY_REFERENCE':
      return <ReferenceDetails intent={intent} onReferenceInactive={reload} />;
    default:
      return (
        <Stack direction="row" spacing={1} alignItems="center" role="status">
          <CircularProgress size={20} />
          <Typography>{t('payments.intent.preparing')}</Typography>
        </Stack>
      );
  }
}

/**
 * `/cliente/pagamentos/:intentId` (S5-11 §4): o pagamento, lido de novo a cada 3 s (M-Pesa) ou
 * 30 s (entidade e referência) até a um estado final. Sem botões laranja.
 */
export function PaymentIntentPage() {
  const { t, i18n } = useTranslation();
  const { intentId = '' } = useParams();
  const queryClient = useQueryClient();
  const query = usePaymentIntentPolling(intentId);
  const intent = query.data;
  const finalStatus = intent && isFinalStatus(intent.status) ? intent.status : undefined;
  const proposalId = intent ? proposalIdOf(intent) : undefined;

  // Num estado final a proposta mudou (paga ou de novo por pagar): relê-se quando se abrir.
  useEffect(() => {
    if (!finalStatus || !proposalId) return;
    void queryClient.invalidateQueries({ queryKey: getGetProposalQueryKey(proposalId) });
    void queryClient.invalidateQueries({
      queryKey: getListProposalPaymentMethodsQueryKey(proposalId),
    });
  }, [finalStatus, proposalId, queryClient]);

  let content;
  if (query.isPending) {
    content = <LoadingScreen />;
  } else if (query.isError) {
    const problem = getProblem(query.error);
    content =
      problem.code === 'PAYMENT_INTENT_NOT_FOUND' || problem.status === 404 ? (
        <Stack spacing={2}>
          <Alert severity="warning" role="alert">
            {t('payments.intent.notFound')}
          </Alert>
          <Button
            variant="text"
            component={RouterLink}
            to={paymentPaths.list}
            sx={{ alignSelf: 'flex-start' }}
          >
            {t('payments.intent.backToPayments')}
          </Button>
        </Stack>
      ) : (
        <Alert
          severity="error"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={() => void query.refetch()}>
              {t('common.retry')}
            </Button>
          }
        >
          {problemMessage(t, problem)}
        </Alert>
      );
  } else {
    const data = query.data;
    content = (
      <Stack spacing={3}>
        <Card variant="outlined" elevation={0}>
          <CardContent>
            <Stack spacing={1}>
              <Stack
                direction="row"
                spacing={1}
                alignItems="center"
                justifyContent="space-between"
                useFlexGap
                flexWrap="wrap"
              >
                <Typography variant="h6" component="h1">
                  {t('payments.intent.reference', { reference: data.reference })}
                </Typography>
                <Chip
                  size="small"
                  color={paymentStatusColor(data.status)}
                  label={t(`paymentStatus.${data.status}`)}
                />
              </Stack>
              <Typography
                variant="h4"
                component="p"
                sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}
              >
                {formatMoney(data.amount, i18n.language)}
              </Typography>
              <Typography color="text.secondary">
                {data.insurer.displayName ?? ''} · {data.target.label}
              </Typography>
            </Stack>
          </CardContent>
        </Card>
        <IntentBody intent={data} reload={() => void query.refetch()} />
      </Stack>
    );
  }

  return (
    <Container maxWidth="sm" disableGutters>
      {content}
    </Container>
  );
}
