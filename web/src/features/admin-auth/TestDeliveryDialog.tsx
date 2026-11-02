import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import {
  testOtpDelivery,
  type OtpTestRequestIdentifierType,
  type OtpTestResponse,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import type { OtpChannel } from './otpSettingsForm';

/** Canais compatíveis com cada tipo de identificador (activos ou não). */
export const compatibleChannels: Record<OtpTestRequestIdentifierType, OtpChannel[]> = {
  MSISDN: ['SMS', 'WHATSAPP'],
  EMAIL: ['EMAIL'],
};

export interface TestDeliveryDialogProps {
  enabledChannels: OtpChannel[];
  onClose(): void;
}

/** Envia um código de teste por um canal e mostra o resultado do envio. */
export function TestDeliveryDialog({ enabledChannels, onClose }: TestDeliveryDialogProps) {
  const { t } = useTranslation();
  const [identifierType, setIdentifierType] = useState<OtpTestRequestIdentifierType>('MSISDN');
  const [identifier, setIdentifier] = useState('');
  const [channel, setChannel] = useState<OtpChannel>('SMS');
  const [fieldError, setFieldError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [result, setResult] = useState<OtpTestResponse | null>(null);
  const [sending, setSending] = useState(false);

  const changeType = (type: OtpTestRequestIdentifierType) => {
    setIdentifierType(type);
    setChannel(compatibleChannels[type][0] ?? 'SMS');
    setResult(null);
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    setResult(null);
    const value = identifier.trim();
    setFieldError(value ? null : 'REQUIRED');
    if (!value) return;
    setSending(true);
    try {
      setResult(await testOtpDelivery({ identifierType, identifier: value, channel }));
    } catch (error) {
      const problem = getProblem(error);
      const own = problem.errors.find((e) => e.field === 'identifier');
      if (own) setFieldError(own.code);
      else if (['INVALID_MSISDN', 'INVALID_EMAIL'].includes(problem.code)) {
        setFieldError(problem.code);
      } else setMessage(problemMessage(t, problem));
    } finally {
      setSending(false);
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="xs" aria-labelledby="test-delivery-title">
      <DialogTitle id="test-delivery-title">{t('otpSettings.test.title')}</DialogTitle>
      <DialogContent sx={{ pt: '8px !important' }}>
        <Stack component="form" id="test-delivery-form" spacing={2} onSubmit={submit} noValidate>
          <TextField
            select
            label={t('otpSettings.test.identifierType')}
            value={identifierType}
            onChange={(event) => changeType(event.target.value as OtpTestRequestIdentifierType)}
            fullWidth
            slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          >
            {(['MSISDN', 'EMAIL'] as const).map((type) => (
              <option key={type} value={type}>
                {t(`auth.identifier.type.${type}`)}
              </option>
            ))}
          </TextField>
          <TextField
            label={t('otpSettings.test.identifier')}
            value={identifier}
            onChange={(event) => setIdentifier(event.target.value)}
            type={identifierType === 'EMAIL' ? 'email' : 'tel'}
            error={!!fieldError}
            helperText={fieldError ? fieldMessage(t, fieldError) : undefined}
            fullWidth
          />
          <TextField
            select
            label={t('otpSettings.test.channel')}
            value={channel}
            onChange={(event) => setChannel(event.target.value as OtpChannel)}
            fullWidth
            slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
          >
            {compatibleChannels[identifierType].map((c) => (
              <option key={c} value={c}>
                {enabledChannels.includes(c)
                  ? t(`channel.${c}`)
                  : t('otpSettings.test.inactive', { channel: t(`channel.${c}`) })}
              </option>
            ))}
          </TextField>
          {message && (
            <Alert severity="error" role="alert">
              {message}
            </Alert>
          )}
          {result && (
            <Alert
              severity={result.deliveryStatus === 'FAILED' ? 'error' : 'success'}
              role="status"
              data-testid="test-delivery-result"
            >
              {t('otpSettings.test.result', {
                status: t(`deliveryStatus.${result.deliveryStatus}`),
                channel: t(`channel.${result.channel}`),
              })}
            </Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('otpSettings.test.close')}</Button>
        <Button type="submit" form="test-delivery-form" variant="contained" disabled={sending}>
          {t('otpSettings.test.send')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
