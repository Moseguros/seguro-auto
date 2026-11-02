import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getOtpChallenge, type OtpChallengeResponse } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import {
  maskIdentifier,
  type OtpChannel,
  type RequestCode,
  type SentCode,
  type VerifyCode,
} from './otp';
import { OtpCodeInput } from './OtpCodeInput';
import { useResendCountdown } from './useResendCountdown';

export interface CodeStepProps {
  sent: SentCode;
  codeLength: number;
  /** Pede um código novo com o mesmo pedido (a mesma função do passo 1). */
  onRequest: RequestCode;
  onVerify: VerifyCode;
  /** "Alterar": volta ao passo 1. */
  onChangeIdentifier(): void;
  /** Estado do envio (por defeito `getOtpChallenge`). */
  getChallenge?: (challengeId: string) => Promise<OtpChallengeResponse>;
}

/** Durante 30 s depois de cada envio, consulta o canal a cada 5 s. */
export const challengePollIntervalMs = 5_000;
export const challengePollDurationMs = 30_000;

/** Códigos depois dos quais o código actual já não serve: o reenvio fica logo activo. */
const resendNowCodes = new Set(['OTP_EXPIRED', 'OTP_ATTEMPTS_EXCEEDED', 'OTP_SUPERSEDED']);

interface Fallback {
  requested: OtpChannel;
  used: OtpChannel;
}

function fallbackOf(sent: SentCode): Fallback | null {
  if (!sent.response.fallbackUsed) return null;
  return {
    requested: sent.requestedChannel ?? sent.response.channel,
    used: sent.response.channel,
  };
}

/** Passo 2: o código recebido, com reenvio e aviso de mudança de canal. */
export function CodeStep({
  sent: initialSent,
  codeLength,
  onRequest,
  onVerify,
  onChangeIdentifier,
  getChallenge = (challengeId) => getOtpChallenge(challengeId),
}: CodeStepProps) {
  const { t } = useTranslation();
  const [sent, setSent] = useState(initialSent);
  const [channel, setChannel] = useState<OtpChannel>(initialSent.response.channel);
  const [fallback, setFallback] = useState<Fallback | null>(() => fallbackOf(initialSent));
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);
  const [busy, setBusy] = useState(false);
  const countdown = useResendCountdown(initialSent.response.resendAfterSeconds);

  const channelRef = useRef(channel);
  channelRef.current = channel;
  const getChallengeRef = useRef(getChallenge);
  getChallengeRef.current = getChallenge;

  const challengeId = sent.response.challengeId;
  const requestedChannel = sent.requestedChannel ?? sent.response.channel;

  useEffect(() => {
    const started = Date.now();
    let active = true;
    const id = setInterval(() => {
      if (Date.now() - started > challengePollDurationMs) {
        clearInterval(id);
        return;
      }
      getChallengeRef
        .current(challengeId)
        .then((challenge) => {
          if (!active || challenge.channel === channelRef.current) return;
          setChannel(challenge.channel);
          setFallback({ requested: requestedChannel, used: challenge.channel });
        })
        .catch(() => undefined);
    }, challengePollIntervalMs);
    return () => {
      active = false;
      clearInterval(id);
    };
  }, [challengeId, requestedChannel]);

  async function verify(code: string) {
    if (busy) return;
    setBusy(true);
    setError(null);
    setInfo(null);
    try {
      await onVerify(challengeId, code);
    } catch (failure) {
      const problem = getProblem(failure);
      if (problem.code === 'OTP_INVALID' && problem.attemptsRemaining !== undefined) {
        setError(t('auth.code.wrongCode', { count: problem.attemptsRemaining }));
      } else {
        setError(problemMessage(t, problem));
      }
      if (resendNowCodes.has(problem.code)) countdown.finish();
      setAttempt((value) => value + 1);
    } finally {
      setBusy(false);
    }
  }

  async function resend() {
    setBusy(true);
    setError(null);
    setInfo(null);
    try {
      const response = await onRequest(sent.request);
      const next: SentCode = { ...sent, response };
      setSent(next);
      setChannel(response.channel);
      setFallback(fallbackOf(next));
      countdown.restart(response.resendAfterSeconds);
      setInfo(t('auth.code.resent'));
      setAttempt((value) => value + 1);
    } catch (failure) {
      const problem = getProblem(failure);
      setError(problemMessage(t, problem));
      if (problem.status === 429) countdown.restart(problem.retryAfterSeconds ?? 0);
    } finally {
      setBusy(false);
    }
  }

  return (
    <Stack spacing={2.5}>
      <Typography>
        {t('auth.code.sentTo', {
          channel: t(`channel.${channel}`),
          destination: maskIdentifier(sent.request.identifierType, sent.request.identifier),
        })}{' '}
        <Link
          component="button"
          type="button"
          onClick={onChangeIdentifier}
          sx={{ verticalAlign: 'baseline' }}
        >
          {t('auth.code.change')}
        </Link>
      </Typography>

      {fallback && (
        <Alert severity="info">
          {t('auth.code.fallback', {
            requested: t(`channel.${fallback.requested}`),
            used: t(`channel.${fallback.used}`),
          })}
        </Alert>
      )}

      <OtpCodeInput
        key={attempt}
        length={codeLength}
        onComplete={(code) => void verify(code)}
        disabled={busy}
        error={error !== null}
        autoFocus
      />

      {error && (
        <Alert severity="error" role="alert">
          {error}
        </Alert>
      )}
      {info && <Alert severity="success">{info}</Alert>}

      <Button variant="text" onClick={() => void resend()} disabled={busy || !countdown.done}>
        {countdown.done
          ? t('auth.code.resend')
          : t('auth.code.resendIn', { seconds: countdown.remaining })}
      </Button>
    </Stack>
  );
}
