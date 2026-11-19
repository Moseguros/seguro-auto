import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import {
  createProposalPaymentIntent,
  getGetPaymentIntentQueryKey,
  useListProposalPaymentMethods,
  type CreateProposalPaymentIntentRequest,
  type PaymentMethodOptionMethod,
  type ProposalPaymentChannel,
} from '../../api/generated';
import { useAuth } from '../../auth/useAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { PaymentMethodPicker } from './PaymentMethodPicker';
import { PaymentPageSkeleton, PaymentSummaryCard } from './PaymentSummaryCard';
import { e164, mpesaNational, nationalFromMsisdn, validateMpesaMsisdn } from './PayerMsisdnField';
import { paymentPaths, retryNetworkOnly } from './paymentPaths';
import { paymentSignature, useIdempotencyKey } from './useIdempotencyKey';
import { isMpesaInProgress } from './usePaymentIntentPolling';

type Blocked = 'notFound' | 'notPayable' | 'paid';

interface PayVariables {
  key: string;
  body: CreateProposalPaymentIntentRequest;
}

/** Proposta que não se pode pagar: a mensagem e a ligação de volta. */
function BlockedView({ kind, proposalId }: { kind: Blocked; proposalId: string }) {
  const { t } = useTranslation();
  const message = {
    notFound: t('payments.pay.notFound'),
    notPayable: t('payments.pay.notPayable'),
    paid: t('payments.pay.alreadyPaid'),
  }[kind];
  return (
    <Stack spacing={2}>
      <Alert severity={kind === 'paid' ? 'success' : 'warning'} role="alert">
        {message}
      </Alert>
      <Button
        variant="text"
        component={RouterLink}
        to={kind === 'notFound' ? paymentPaths.proposals : paymentPaths.proposal(proposalId)}
        sx={{ alignSelf: 'flex-start' }}
      >
        {kind === 'notFound' ? t('payments.pay.backToProposals') : t('payments.pay.backToProposal')}
      </Button>
    </Stack>
  );
}

/**
 * `/cliente/propostas/:id/pagar` (S5-11 §3): resumo, escolha da forma de pagamento e o botão
 * laranja "Adquirir apólice" (o único do ecrã), fixo no fundo com o total por cima.
 */
export function ProposalPaymentPage() {
  const { t, i18n } = useTranslation();
  const { id = '' } = useParams();
  const [searchParams] = useSearchParams();
  const channel: ProposalPaymentChannel =
    searchParams.get('origem') === 'mediador' ? 'BROKER_LINK' : 'WEB';
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { me } = useAuth();
  const query = useListProposalPaymentMethods(id, { query: { retry: retryNetworkOnly } });
  const { keyFor, reset } = useIdempotencyKey(id);

  const [method, setMethod] = useState<PaymentMethodOptionMethod | null>(null);
  const [msisdn, setMsisdn] = useState('');
  const [msisdnEdited, setMsisdnEdited] = useState(false);
  const [msisdnTouched, setMsisdnTouched] = useState(false);
  const [serverFieldError, setServerFieldError] = useState<string | null>(null);
  const [blocked, setBlocked] = useState<Blocked | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  // O telemóvel da conta, se for M-Pesa (84/85), enquanto o cliente não escrever outro.
  useEffect(() => {
    if (msisdnEdited) return;
    const national = nationalFromMsisdn(me?.msisdn);
    if (mpesaNational.test(national)) setMsisdn(national);
  }, [me?.msisdn, msisdnEdited]);

  const methods = query.data?.methods;
  useEffect(() => {
    if (!methods) return;
    if (method && !methods.some((m) => m.method === method)) setMethod(null);
    else if (!method && methods.length === 1) setMethod(methods[0]!.method);
  }, [methods, method]);

  const pay = useMutation({
    mutationFn: ({ key, body }: PayVariables) =>
      createProposalPaymentIntent(id, body, { headers: { 'Idempotency-Key': key } }),
    onSuccess: (intent) => {
      reset();
      queryClient.setQueryData(getGetPaymentIntentQueryKey(intent.id), intent);
      navigate(paymentPaths.intent(intent.id), { replace: true });
    },
    onError: (error) => {
      const problem = getProblem(error);
      switch (problem.code) {
        case 'PAYMENT_IN_PROGRESS':
          if (problem.intentId) {
            navigate(paymentPaths.intent(problem.intentId), { replace: true });
            return;
          }
          break;
        case 'PAYMENT_TARGET_ALREADY_PAID':
          setBlocked('paid');
          return;
        case 'PAYMENT_TARGET_NOT_PAYABLE':
          setBlocked('notPayable');
          return;
        case 'PROPOSAL_NOT_FOUND':
          setBlocked('notFound');
          return;
        case 'PAYMENT_METHOD_NOT_AVAILABLE':
          setNotice(t('payments.pay.methodUnavailable'));
          setMethod(null);
          void query.refetch();
          return;
        case 'VALIDATION_ERROR': {
          const field = problem.errors.find((e) => e.field === 'payerMsisdn');
          if (field) {
            setServerFieldError(field.code);
            setMsisdnTouched(true);
            return;
          }
          break;
        }
      }
      setNotice(problemMessage(t, problem));
    },
  });

  let content;
  if (blocked) {
    content = <BlockedView kind={blocked} proposalId={id} />;
  } else if (query.isPending) {
    content = <PaymentPageSkeleton />;
  } else if (query.isError) {
    const problem = getProblem(query.error);
    if (problem.code === 'PROPOSAL_NOT_FOUND' || problem.status === 404) {
      content = <BlockedView kind="notFound" proposalId={id} />;
    } else if (problem.code === 'PAYMENT_TARGET_NOT_PAYABLE') {
      content = <BlockedView kind="notPayable" proposalId={id} />;
    } else {
      content = (
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
    }
  } else if (query.data.paid) {
    content = <BlockedView kind="paid" proposalId={id} />;
  } else {
    const data = query.data;
    const active = data.activeIntent ?? undefined;
    const mpesaBusy = active ? isMpesaInProgress(active) : false;
    const msisdnError =
      method === 'MPESA' ? (serverFieldError ?? validateMpesaMsisdn(msisdn)) : null;
    const valid = method !== null && msisdnError === null;
    const insurerName = data.insurer.displayName ?? '';

    const submit = () => {
      if (!method) return;
      if (method === 'MPESA' && validateMpesaMsisdn(msisdn) !== null) {
        setMsisdnTouched(true);
        return;
      }
      setNotice(null);
      const payerMsisdn = method === 'MPESA' ? e164(msisdn) : undefined;
      pay.mutate({
        key: keyFor(paymentSignature(method, payerMsisdn)),
        body: { method, channel, ...(payerMsisdn ? { payerMsisdn } : {}) },
      });
    };

    content = (
      <Stack spacing={3}>
        {active && (
          <Alert
            severity="info"
            action={
              <Button
                variant="contained"
                size="small"
                component={RouterLink}
                to={paymentPaths.intent(active.id)}
              >
                {t('payments.pay.viewPayment')}
              </Button>
            }
          >
            {t('payments.pay.activeIntent')}
          </Alert>
        )}
        <PaymentSummaryCard
          proposalNumber={data.proposalNumber}
          insurerName={insurerName}
          amount={data.amount}
          expiresAt={data.expiresAt}
        />
        {notice && (
          <Alert severity="warning" role="alert" onClose={() => setNotice(null)}>
            {notice}
          </Alert>
        )}
        {data.methods.length === 0 ? (
          <Alert severity="info">{t('payments.pay.noMethods')}</Alert>
        ) : (
          <>
            <PaymentMethodPicker
              methods={data.methods}
              value={method}
              onChange={(m) => {
                setMethod(m);
                setNotice(null);
              }}
              msisdn={msisdn}
              onMsisdnChange={(value) => {
                setMsisdn(value);
                setMsisdnEdited(true);
                setServerFieldError(null);
              }}
              onMsisdnBlur={() => setMsisdnTouched(true)}
              msisdnError={msisdnTouched ? msisdnError : null}
              disabled={pay.isPending}
            />
            <Box
              sx={{
                position: 'sticky',
                bottom: 0,
                zIndex: 1,
                bgcolor: 'background.default',
                borderTop: 1,
                borderColor: 'divider',
                py: 1.5,
              }}
            >
              <Stack spacing={1}>
                <Stack direction="row" justifyContent="space-between" alignItems="baseline">
                  <Typography color="text.secondary">{t('payments.summary.total')}</Typography>
                  <Typography fontWeight={700} sx={{ fontVariantNumeric: 'tabular-nums' }}>
                    {formatMoney(data.amount, i18n.language)}
                  </Typography>
                </Stack>
                <Button
                  variant="cta"
                  size="large"
                  fullWidth
                  disabled={!valid || mpesaBusy || pay.isPending}
                  onClick={submit}
                  startIcon={
                    pay.isPending ? <CircularProgress size={18} color="inherit" /> : undefined
                  }
                >
                  {t('payments.pay.submit')}
                </Button>
              </Stack>
            </Box>
          </>
        )}
      </Stack>
    );
  }

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={2}>
        <Typography variant="h4" component="h1">
          {t('payments.title')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
