import Sms from '@mui/icons-material/Sms';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { sendProposalPaymentLink } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';

/**
 * Mediador, proposta aceite (S5-11 §2): confirma e envia ao cliente, por SMS, o link para pagar
 * (`sendProposalPaymentLink`). O cliente paga directamente à seguradora.
 */
export function SendPaymentLinkButton({ proposalId }: { proposalId: string }) {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [result, setResult] = useState<{ severity: 'success' | 'error'; text: string } | null>(
    null,
  );

  const send = useMutation({
    mutationFn: () => sendProposalPaymentLink(proposalId),
    onSuccess: (response) => {
      setOpen(false);
      setResult({
        severity: 'success',
        text: t('payments.link.sent', { sentTo: response.sentTo }),
      });
    },
    onError: (error) => {
      setOpen(false);
      const problem = getProblem(error);
      let text: string;
      if (problem.code === 'PAYMENT_LINK_RATE_LIMITED') {
        const minutes = Math.max(1, Math.ceil((problem.retryAfterSeconds ?? 60) / 60));
        text = t('payments.link.rateLimited', { minutes });
      } else if (problem.code === 'CUSTOMER_HAS_NO_MSISDN') {
        text = t('payments.link.noMsisdn');
      } else {
        text = problemMessage(t, problem);
      }
      setResult({ severity: 'error', text });
    },
  });

  return (
    <Stack spacing={1} alignItems="flex-start">
      <Button
        variant="outlined"
        startIcon={<Sms />}
        onClick={() => {
          setResult(null);
          setOpen(true);
        }}
      >
        {t('payments.link.button')}
      </Button>
      {result && (
        <Alert
          severity={result.severity}
          role={result.severity === 'error' ? 'alert' : 'status'}
          onClose={() => setResult(null)}
        >
          {result.text}
        </Alert>
      )}
      <Dialog
        open={open}
        onClose={send.isPending ? undefined : () => setOpen(false)}
        aria-labelledby="payment-link-title"
        fullWidth
        maxWidth="xs"
      >
        <DialogTitle id="payment-link-title">{t('payments.link.confirmTitle')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('payments.link.confirmText')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)} disabled={send.isPending}>
            {t('common.cancel')}
          </Button>
          <Button
            variant="contained"
            onClick={() => send.mutate()}
            disabled={send.isPending}
            startIcon={send.isPending ? <CircularProgress size={16} color="inherit" /> : undefined}
          >
            {t('payments.link.send')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
