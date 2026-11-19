import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useMutation } from '@tanstack/react-query';
import { useId, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  resolvePaymentException,
  type PaymentExceptionCandidate,
  type PaymentExceptionDetail,
  type PaymentResolution,
  type ResolvePaymentExceptionRequest,
} from '../../api/generated';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { counterText, lengthOk } from '../insurer-proposals/common';
import { ConfirmDialog } from '../insurer-payment-settings/ConfirmDialog';
import { CandidatesTable } from './CandidatesTable';

const noteMin = 5;
const noteMax = 500;

/** Resoluções que confirmam um pagamento (pedem confirmação). */
const confirming = new Set<string>(['LINKED', 'MARK_SUCCEEDED']);

/**
 * Resolução de uma excepção por resolver (S5-12 §4): só as `allowedResolutions`; `LINKED` exige um
 * pagamento escolhido; nota obrigatória (5–500). `REFUND` só regista a decisão: a devolução é feita
 * pela seguradora fora da plataforma.
 */
export function ResolveExceptionForm({
  exception,
  onResolved,
  onStale,
}: {
  exception: PaymentExceptionDetail;
  onResolved(updated: PaymentExceptionDetail): void;
  /** A excepção mudou no servidor (já resolvida ou outra versão): voltar a lê-la. */
  onStale(): void;
}) {
  const { t } = useTranslation();
  const groupLabelId = useId();
  const [resolution, setResolution] = useState<PaymentResolution | ''>('');
  const [candidate, setCandidate] = useState<PaymentExceptionCandidate | undefined>();
  const [providerTxId, setProviderTxId] = useState('');
  const [note, setNote] = useState('');
  const [submitted, setSubmitted] = useState(false);
  const [confirmOpen, setConfirmOpen] = useState(false);

  const resolve = useMutation({
    mutationFn: () => {
      const body: ResolvePaymentExceptionRequest = {
        resolution: resolution as ResolvePaymentExceptionRequest['resolution'],
        note: note.trim(),
        version: exception.version,
        ...(resolution === 'LINKED' && candidate ? { intentId: candidate.id } : {}),
        ...(resolution === 'MARK_SUCCEEDED' && providerTxId.trim()
          ? { providerTxId: providerTxId.trim() }
          : {}),
      };
      return resolvePaymentException(exception.id, body);
    },
    onSuccess: (updated) => onResolved(updated),
    onError: (error) => {
      const problem = getProblem(error);
      if (
        problem.code === 'INVALID_STATE_TRANSITION' ||
        problem.code === 'CONCURRENT_MODIFICATION'
      ) {
        onStale();
      }
    },
  });

  const errorText = (problem: Problem) => {
    const key = `paymentExceptions.resolve.errors.${problem.code}`;
    const specific = t(key, { defaultValue: '' });
    return specific || problemMessage(t, problem);
  };

  const noteValid = lengthOk(note, noteMin, noteMax);
  const missingCandidate = resolution === 'LINKED' && !candidate;

  const submit = () => {
    setSubmitted(true);
    resolve.reset();
    if (!resolution || !noteValid || missingCandidate) return;
    if (confirming.has(resolution)) {
      setConfirmOpen(true);
      return;
    }
    resolve.mutate();
  };

  const confirmPayment =
    resolution === 'LINKED'
      ? candidate
      : resolution === 'MARK_SUCCEEDED'
        ? exception.intent
        : undefined;

  return (
    <Stack
      spacing={2}
      component="form"
      noValidate
      onSubmit={(e) => {
        e.preventDefault();
        submit();
      }}
    >
      <Typography variant="h6" component="h3">
        {t('paymentExceptions.resolve.title')}
      </Typography>
      <FormControl error={submitted && !resolution}>
        <FormLabel id={groupLabelId}>{t('paymentExceptions.resolve.resolution')}</FormLabel>
        <RadioGroup
          aria-labelledby={groupLabelId}
          value={resolution}
          onChange={(event) => {
            setResolution(event.target.value as PaymentResolution);
            resolve.reset();
          }}
        >
          {exception.allowedResolutions.map((r) => (
            <FormControlLabel
              key={r}
              value={r}
              control={<Radio />}
              sx={{ alignItems: 'flex-start', my: 0.5 }}
              label={
                <Stack spacing={0.25} sx={{ pt: 1 }}>
                  <span>{t(`paymentResolution.${r}`)}</span>
                  <Typography variant="caption" color="text.secondary">
                    {t(`paymentResolutionHelp.${r}`)}
                  </Typography>
                </Stack>
              }
            />
          ))}
        </RadioGroup>
        {submitted && !resolution && (
          <FormHelperText>{t('paymentExceptions.resolve.chooseResolution')}</FormHelperText>
        )}
      </FormControl>
      {resolution === 'LINKED' && (
        <CandidatesTable
          exceptionId={exception.id}
          selected={candidate?.id}
          onSelect={setCandidate}
          error={
            submitted && missingCandidate
              ? t('paymentExceptions.resolve.chooseCandidate')
              : undefined
          }
        />
      )}
      {resolution === 'MARK_SUCCEEDED' && (
        <TextField
          label={t('paymentExceptions.resolve.providerTxId')}
          value={providerTxId}
          onChange={(event) => setProviderTxId(event.target.value)}
          slotProps={{ htmlInput: { maxLength: 100 } }}
          fullWidth
        />
      )}
      <TextField
        label={t('paymentExceptions.resolve.note')}
        value={note}
        onChange={(event) => setNote(event.target.value)}
        required
        multiline
        minRows={3}
        error={submitted && !noteValid}
        helperText={`${t('paymentExceptions.resolve.noteHelp')} ${counterText(note, noteMax)}`}
        slotProps={{ htmlInput: { maxLength: noteMax } }}
        fullWidth
      />
      {resolve.isError && (
        <Alert severity="error" role="alert">
          {errorText(getProblem(resolve.error))}
        </Alert>
      )}
      <div>
        <Button type="submit" variant="contained" disabled={resolve.isPending}>
          {t('paymentExceptions.resolve.submit')}
        </Button>
      </div>
      <ConfirmDialog
        open={confirmOpen}
        title={t('paymentExceptions.resolve.confirmTitle')}
        text={t('paymentExceptions.resolve.confirm', {
          payment: confirmPayment?.reference ?? '—',
          proposal: confirmPayment?.targetLabel ?? '—',
        })}
        confirmLabel={t('paymentExceptions.resolve.continue')}
        busy={resolve.isPending}
        onConfirm={() => {
          setConfirmOpen(false);
          resolve.mutate();
        }}
        onClose={() => setConfirmOpen(false)}
      />
    </Stack>
  );
}
