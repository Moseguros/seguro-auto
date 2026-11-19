import CheckCircle from '@mui/icons-material/CheckCircle';
import ErrorOutline from '@mui/icons-material/ErrorOutline';
import Info from '@mui/icons-material/InfoOutlined';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { IntentView } from '../../api/generated';
import { paymentPaths, proposalIdOf } from './paymentPaths';

/** Resultado de um pagamento num estado final (S5-11 §4), com o passo seguinte. */
export function PaymentOutcome({ intent }: { intent: IntentView }) {
  const { t } = useTranslation();
  const proposalId = proposalIdOf(intent);
  const viewProposal = proposalId ? paymentPaths.proposal(proposalId) : paymentPaths.list;
  const chooseAnother = proposalId ? paymentPaths.pay(proposalId) : paymentPaths.list;

  let icon = <Info color="info" sx={{ fontSize: 48 }} aria-hidden />;
  let title: string | null = null;
  let message: string;
  let retry = false;
  switch (intent.status) {
    case 'SUCCEEDED':
      icon = <CheckCircle color="success" sx={{ fontSize: 48 }} aria-hidden />;
      title = t('payments.outcome.succeeded');
      message = t('payments.outcome.succeededText');
      break;
    case 'FAILED':
      icon = <ErrorOutline color="error" sx={{ fontSize: 48 }} aria-hidden />;
      message = intent.failureCode
        ? t(`paymentFailure.${intent.failureCode}`)
        : t('paymentStatus.FAILED');
      retry = true;
      break;
    case 'EXPIRED':
      message = t('payments.outcome.expired');
      retry = true;
      break;
    case 'CANCELLED':
      message = t('payments.outcome.cancelled');
      break;
    case 'REFUNDED':
      message = t('payments.outcome.refunded');
      break;
    default:
      message = t(`paymentStatus.${intent.status}`);
  }

  return (
    <Stack spacing={2} alignItems="flex-start" role="status">
      {icon}
      {title && (
        <Typography variant="h5" component="h2">
          {title}
        </Typography>
      )}
      <Typography>{message}</Typography>
      <Button variant="contained" component={RouterLink} to={retry ? chooseAnother : viewProposal}>
        {retry ? t('payments.outcome.chooseAnother') : t('payments.outcome.viewProposal')}
      </Button>
    </Stack>
  );
}
