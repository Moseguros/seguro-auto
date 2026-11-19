import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { sendReferenceSms } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { e164, PayerMsisdnField, validateMozMsisdn } from './PayerMsisdnField';

/** Envia a entidade e a referência por SMS para o número escolhido (S5-11 §4). */
export function SendReferenceSmsDialog({
  open,
  intentId,
  defaultMsisdn,
  onClose,
  onSent,
  onReferenceInactive,
}: {
  open: boolean;
  intentId: string;
  /** 9 dígitos nacionais (o telemóvel da conta), ou `''`. */
  defaultMsisdn: string;
  onClose: () => void;
  onSent: (sentTo: string) => void;
  onReferenceInactive: () => void;
}) {
  const { t } = useTranslation();
  const [msisdn, setMsisdn] = useState(defaultMsisdn);
  const [touched, setTouched] = useState(false);
  const [serverFieldError, setServerFieldError] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const send = useMutation({
    mutationFn: () => sendReferenceSms(intentId, { msisdn: e164(msisdn) }),
    onSuccess: (result) => {
      onSent(result.sentTo);
      onClose();
    },
    onError: (err) => {
      const problem = getProblem(err);
      if (problem.code === 'REFERENCE_SMS_RATE_LIMITED') {
        setError(t('payments.sms.rateLimited'));
      } else if (problem.code === 'REFERENCE_NOT_ACTIVE') {
        onClose();
        onReferenceInactive();
      } else if (
        problem.code === 'VALIDATION_ERROR' &&
        problem.errors.some((e) => e.field === 'msisdn')
      ) {
        setServerFieldError(problem.errors.find((e) => e.field === 'msisdn')!.code);
        setTouched(true);
      } else {
        setError(problemMessage(t, problem));
      }
    },
  });

  const fieldError = serverFieldError ?? validateMozMsisdn(msisdn);

  return (
    <Dialog
      open={open}
      onClose={send.isPending ? undefined : onClose}
      fullWidth
      maxWidth="xs"
      aria-labelledby="reference-sms-title"
    >
      <DialogTitle id="reference-sms-title">{t('payments.sms.title')}</DialogTitle>
      <DialogContent>
        {error && (
          <Alert severity="error" role="alert" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}
        <PayerMsisdnField
          label={t('payments.sms.msisdn')}
          value={msisdn}
          autoFocus
          onChange={(value) => {
            setMsisdn(value);
            setServerFieldError(null);
            setError(null);
          }}
          onBlur={() => setTouched(true)}
          errorCode={touched ? fieldError : null}
        />
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={send.isPending}>
          {t('common.cancel')}
        </Button>
        <Button
          variant="contained"
          disabled={fieldError !== null || send.isPending}
          onClick={() => send.mutate()}
          startIcon={send.isPending ? <CircularProgress size={16} color="inherit" /> : undefined}
        >
          {t('payments.sms.send')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
