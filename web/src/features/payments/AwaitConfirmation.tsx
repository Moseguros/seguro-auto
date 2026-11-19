import PhoneAndroid from '@mui/icons-material/PhoneAndroid';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { IntentView } from '../../api/generated';
import { paymentPaths, proposalIdOf } from './paymentPaths';

/** O instante actual, actualizado a cada segundo. */
export function useNow(intervalMs = 1000): number {
  const [now, setNow] = useState(() => Date.now());
  useEffect(() => {
    const timer = setInterval(() => setNow(Date.now()), intervalMs);
    return () => clearInterval(timer);
  }, [intervalMs]);
  return now;
}

/** Milissegundos → `mm:ss` (nunca negativo). */
export function formatCountdown(ms: number): string {
  const total = Math.max(0, Math.ceil(ms / 1000));
  const minutes = Math.floor(total / 60);
  const seconds = total % 60;
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
}

/** M-Pesa à espera da confirmação no telemóvel (S5-11 §4), com a contagem até `timeoutAt`. */
export function AwaitConfirmation({ intent }: { intent: IntentView }) {
  const { t } = useTranslation();
  const now = useNow();
  const timeoutAt = intent.nextAction.timeoutAt;
  const remaining = timeoutAt ? Date.parse(timeoutAt) - now : null;
  const timedOut = remaining !== null && remaining <= 0;
  const proposalId = proposalIdOf(intent);

  return (
    <Stack spacing={2}>
      <Stack direction="row" spacing={1} alignItems="center">
        <PhoneAndroid color="primary" aria-hidden />
        <Typography variant="h6" component="h2">
          {t('payments.await.title')}
        </Typography>
      </Stack>
      <Stack spacing={0.5} component="ol" sx={{ listStyle: 'none', p: 0, m: 0 }}>
        <Typography component="li">
          {t('payments.await.step1', { msisdn: intent.payerMsisdnMasked ?? '' })}
        </Typography>
        <Typography component="li">{t('payments.await.step2')}</Typography>
        <Typography component="li">{t('payments.await.step3')}</Typography>
      </Stack>
      {remaining !== null && (
        <Stack spacing={0} alignItems="center">
          <Typography variant="body2" color="text.secondary">
            {t('payments.await.remaining')}
          </Typography>
          <Typography
            role="timer"
            aria-live="off"
            variant="h3"
            component="p"
            sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}
          >
            {formatCountdown(remaining)}
          </Typography>
        </Stack>
      )}
      {timedOut && intent.status === 'PENDING' && (
        <Alert severity="info" role="status">
          {t('payments.await.stillWaiting')}
        </Alert>
      )}
      {proposalId && (
        <Button
          variant="outlined"
          component={RouterLink}
          to={paymentPaths.proposal(proposalId)}
          sx={{ alignSelf: 'flex-start' }}
        >
          {t('payments.await.backToProposal')}
        </Button>
      )}
    </Stack>
  );
}
