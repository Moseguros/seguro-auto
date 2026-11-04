import Download from '@mui/icons-material/Download';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  confirmProposalSignature,
  getOtpOptions,
  requestProposalSignature,
  submitProposal,
  type ProposalResponse,
  type RequestSignatureRequestChannel,
  type SignatureRequestResponse,
} from '../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { downloadProtectedFile } from '../../files/downloadProtectedFile';
import { otpOptionsQueryKey } from '../customer-auth/CustomerLoginPage';
import { OtpCodeInput } from '../customer-auth/OtpCodeInput';
import { useResendCountdown } from '../customer-auth/useResendCountdown';
import type { ProposalMode } from './wizardSteps';

/** Códigos depois dos quais o código actual já não serve: o reenvio fica logo activo. */
const resendNowCodes = new Set([
  'OTP_EXPIRED',
  'OTP_ATTEMPTS_EXCEEDED',
  'OTP_SUPERSEDED',
  'OTP_ALREADY_USED',
  'PROPOSAL_CHANGED_SINCE_SIGN_REQUEST',
  'SIGN_REQUEST_NOT_FOUND',
]);

const DEFAULT_CODE_LENGTH = 6;

export interface SignDialogProps {
  open: boolean;
  mode: ProposalMode;
  proposal: Pick<ProposalResponse, 'id' | 'number' | 'version'>;
  /** Pedido de assinatura em curso (`requestProposalSignature`). */
  request: SignatureRequestResponse;
  onClose(): void;
  /** Assinada e submetida: a proposta já fora dos passos. */
  onSubmitted(proposal: ProposalResponse): void;
  /** Assinada, mas a submissão falhou (ex.: `INSURER_NOT_ACTIVE`). */
  onSubmitFailed(signed: ProposalResponse, problem: Problem): void;
}

/** Código de assinatura: confirmação, submissão e reenvio (S4-08 §3, `resumo`). */
export function SignDialog({
  open,
  mode,
  proposal,
  request: initialRequest,
  onClose,
  onSubmitted,
  onSubmitFailed,
}: SignDialogProps) {
  const { t } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [request, setRequest] = useState(initialRequest);
  const [error, setError] = useState<string | null>(null);
  const [info, setInfo] = useState<string | null>(null);
  const [attempt, setAttempt] = useState(0);
  const [busy, setBusy] = useState(false);
  const countdown = useResendCountdown(initialRequest.resendAfterSeconds);
  const options = useQuery({
    queryKey: otpOptionsQueryKey,
    queryFn: ({ signal }) => getOtpOptions(undefined, signal),
    enabled: open,
  });
  const codeLength = options.data?.codeLength ?? DEFAULT_CODE_LENGTH;
  const alternatives =
    mode === 'customer' && options.data?.customerCanChoose
      ? [...new Set(Object.values(options.data.channels).flat())].filter(
          (channel) => channel !== request.channel,
        )
      : [];

  async function confirm(code: string) {
    if (busy) return;
    setBusy(true);
    setError(null);
    setInfo(null);
    let signed: ProposalResponse;
    try {
      signed = await confirmProposalSignature(proposal.id, {
        challengeId: request.challengeId,
        code,
      });
    } catch (failure) {
      const problem = getProblem(failure);
      if (problem.code === 'OTP_INVALID' && problem.attemptsRemaining !== undefined) {
        setError(t('auth.code.wrongCode', { count: problem.attemptsRemaining }));
      } else {
        setError(problemMessage(t, problem));
      }
      if (resendNowCodes.has(problem.code)) countdown.finish();
      setAttempt((value) => value + 1);
      setBusy(false);
      return;
    }
    try {
      onSubmitted(await submitProposal(proposal.id, { version: signed.version }));
    } catch (failure) {
      onSubmitFailed(signed, getProblem(failure));
    } finally {
      setBusy(false);
    }
  }

  async function resend(channel?: RequestSignatureRequestChannel) {
    setBusy(true);
    setError(null);
    setInfo(null);
    try {
      const next = await requestProposalSignature(proposal.id, {
        documentsAcknowledged: true,
        version: proposal.version,
        ...(channel ? { channel } : {}),
      });
      setRequest(next);
      countdown.restart(next.resendAfterSeconds);
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

  async function download() {
    setError(null);
    try {
      await downloadProtectedFile(request.documentFileId, `${proposal.number}.pdf`);
    } catch {
      setError(t('files.loadError'));
    }
  }

  return (
    <Dialog
      open={open}
      onClose={busy ? undefined : onClose}
      fullScreen={fullScreen}
      fullWidth
      maxWidth="sm"
    >
      <DialogTitle>{t('proposal.sign.title')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2.5}>
          <Typography data-testid="sign-sent-to">
            {mode === 'broker'
              ? t('proposal.sign.sentBroker', { destination: request.destinationMasked })
              : t('proposal.sign.sentCustomer', {
                  channel: t(`channel.${request.channel}`),
                  destination: request.destinationMasked,
                })}
          </Typography>
          {request.fallbackUsed && <Alert severity="info">{t('proposal.sign.fallback')}</Alert>}
          <Button
            variant="text"
            startIcon={<Download />}
            onClick={() => void download()}
            sx={{ alignSelf: 'flex-start' }}
          >
            {t('proposal.sign.downloadPdf')}
          </Button>
          <OtpCodeInput
            key={`${attempt}-${codeLength}`}
            length={codeLength}
            onComplete={(code) => void confirm(code)}
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
          <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
            <Button variant="text" onClick={() => void resend()} disabled={busy || !countdown.done}>
              {countdown.done
                ? t('auth.code.resend')
                : t('auth.code.resendIn', { seconds: countdown.remaining })}
            </Button>
            {alternatives.map((channel) => (
              <Button
                key={channel}
                variant="text"
                disabled={busy || !countdown.done}
                onClick={() => void resend(channel)}
              >
                {t('proposal.sign.sendBy', { channel: t(`channel.${channel}`) })}
              </Button>
            ))}
          </Stack>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={busy}>
          {t('common.cancel')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
