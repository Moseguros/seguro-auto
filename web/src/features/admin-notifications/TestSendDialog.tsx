import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import InputAdornment from '@mui/material/InputAdornment';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useMutation } from '@tanstack/react-query';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import {
  testNotificationTemplate,
  type NotificationChannel,
  type NotificationTemplateTestResult,
} from '../../api/generated';
import { applyFieldErrors, fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { emailPattern, msisdnDigitsPattern, msisdnPrefix } from '../customer-auth/otp';

/** Destino do teste já no formato da API, ou o código do erro de validação local. */
export function testDestination(
  channel: NotificationChannel,
  raw: string,
): { destination: string } | { error: string } {
  const value = raw.trim();
  if (!value) return { error: 'REQUIRED' };
  if (channel === 'SMS' || channel === 'WHATSAPP') {
    const digits = value.replace(/[\s-]/g, '');
    return msisdnDigitsPattern.test(digits)
      ? { destination: `${msisdnPrefix}${digits}` }
      : { error: 'INVALID_MSISDN' };
  }
  if (channel === 'EMAIL') {
    return emailPattern.test(value) ? { destination: value } : { error: 'INVALID_EMAIL' };
  }
  return { destination: value };
}

/** "Enviar teste" (S6-05 §7): destino conforme o canal → `testNotificationTemplate`. */
export function TestSendDialog({
  open,
  templateId,
  channel,
  onClose,
}: {
  open: boolean;
  templateId: string;
  channel: NotificationChannel;
  onClose(): void;
}) {
  const { t } = useTranslation();
  const [value, setValue] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [result, setResult] = useState<NotificationTemplateTestResult | null>(null);
  const send = useMutation({
    mutationFn: (destination: string) => testNotificationTemplate(templateId, { destination }),
    onSuccess: (data) => setResult(data),
    onError: (e) => {
      const problem = getProblem(e);
      if (
        !applyFieldErrors(problem, ['destination'] as const, (_, { message }) => setError(message))
      ) {
        setError(null);
      }
    },
  });
  const phone = channel === 'SMS' || channel === 'WHATSAPP';
  const label = phone
    ? t('notificationTemplates.testDialog.msisdn')
    : channel === 'EMAIL'
      ? t('notificationTemplates.testDialog.email')
      : t('notificationTemplates.testDialog.token');

  const close = () => {
    setValue('');
    setError(null);
    setResult(null);
    send.reset();
    onClose();
  };

  const submit = (event: FormEvent) => {
    event.preventDefault();
    setResult(null);
    const checked = testDestination(channel, value);
    if ('error' in checked) {
      setError(checked.error);
      return;
    }
    setError(null);
    send.mutate(checked.destination);
  };

  const otherProblem = send.error && !error ? problemMessage(t, getProblem(send.error)) : null;

  return (
    <Dialog open={open} onClose={close} fullWidth maxWidth="xs">
      <form onSubmit={submit} noValidate>
        <DialogTitle>{t('notificationTemplates.testDialog.title')}</DialogTitle>
        <DialogContent>
          <Stack spacing={2} sx={{ pt: 1 }}>
            <TextField
              autoFocus
              label={label}
              value={value}
              onChange={(event) => setValue(event.target.value)}
              type={channel === 'EMAIL' ? 'email' : phone ? 'tel' : 'text'}
              inputMode={phone ? 'numeric' : undefined}
              error={!!error}
              helperText={
                error
                  ? fieldMessage(t, error)
                  : channel === 'PUSH'
                    ? t('notificationTemplates.testDialog.tokenNote')
                    : undefined
              }
              fullWidth
              slotProps={{
                input: phone
                  ? {
                      startAdornment: (
                        <InputAdornment position="start">{msisdnPrefix}</InputAdornment>
                      ),
                    }
                  : undefined,
              }}
            />
            {result &&
              (result.status === 'SENT' ? (
                <Alert severity="success" role="status">
                  {t('notificationTemplates.testDialog.sent')}
                </Alert>
              ) : (
                <Alert severity="error" role="alert">
                  {t('notificationTemplates.testDialog.failed', { reason: result.reason ?? '' })}
                </Alert>
              ))}
            {otherProblem && (
              <Alert severity="error" role="alert">
                {otherProblem}
              </Alert>
            )}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={close}>{t('common.close')}</Button>
          <Button type="submit" variant="contained" disabled={send.isPending}>
            {t('notificationTemplates.testDialog.send')}
          </Button>
        </DialogActions>
      </form>
    </Dialog>
  );
}
