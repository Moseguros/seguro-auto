import ContentCopy from '@mui/icons-material/ContentCopy';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Tooltip from '@mui/material/Tooltip';
import Typography from '@mui/material/Typography';
import { QRCodeSVG } from 'qrcode.react';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import type { TotpSetupResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';

/** Segredo em grupos de 4 caracteres (`JBSW Y3DP EHPK 3PXP`). */
export function groupSecret(secret: string): string {
  return (secret.replace(/\s+/g, '').match(/.{1,4}/g) ?? []).join(' ');
}

export interface TotpSetupProps {
  setup: TotpSetupResponse;
  /** Activa a conta com o código; os erros da API são mostrados aqui. */
  onActivate(totpCode: string): Promise<void>;
}

/** Passo 2 da activação: QR, segredo para entrada manual e o primeiro código. */
export function TotpSetup({ setup, onActivate }: TotpSetupProps) {
  const { t } = useTranslation();
  const [code, setCode] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const grouped = groupSecret(setup.totpSecret);

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(setup.totpSecret);
      setCopied(true);
    } catch {
      // Sem acesso à área de transferência: o segredo continua visível para copiar à mão.
    }
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const value = code.replace(/\s+/g, '');
    const found = !value ? 'REQUIRED' : /^\d{6}$/.test(value) ? null : 'PATTERN';
    setError(found);
    if (found) return;
    setSubmitting(true);
    try {
      await onActivate(value);
    } catch (e) {
      const problem = getProblem(e);
      setMessage(
        problem.code === 'TOTP_INVALID' ? t('staffAuth.totp.invalid') : problemMessage(t, problem),
      );
      setCode('');
      setSubmitting(false);
    }
  };

  return (
    <Stack component="form" spacing={2} onSubmit={submit} noValidate>
      <Typography>{t('staffAuth.totp.instructions')}</Typography>
      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <Box sx={{ p: 1.5, bgcolor: '#fff', borderRadius: 1, lineHeight: 0 }}>
          <QRCodeSVG
            value={setup.otpauthUri}
            size={192}
            title={t('staffAuth.totp.qrLabel')}
            role="img"
            aria-label={t('staffAuth.totp.qrLabel')}
            data-testid="totp-qr"
          />
        </Box>
      </Box>
      <Typography variant="body2" color="text.secondary">
        {t('staffAuth.totp.manual')}
      </Typography>
      <Stack direction="row" spacing={1} alignItems="center" sx={{ minWidth: 0 }}>
        <Typography
          data-testid="totp-secret"
          sx={{
            fontFamily: 'monospace',
            fontSize: '1.05rem',
            wordBreak: 'break-word',
            minWidth: 0,
          }}
        >
          {grouped}
        </Typography>
        <Tooltip title={copied ? t('staffAuth.totp.copied') : t('staffAuth.totp.copy')}>
          <IconButton aria-label={t('staffAuth.totp.copy')} onClick={() => void copy()}>
            <ContentCopy fontSize="small" />
          </IconButton>
        </Tooltip>
      </Stack>
      {copied && (
        <Typography variant="body2" color="success.main" role="status">
          {t('staffAuth.totp.copied')}
        </Typography>
      )}
      <TextField
        label={t('staffAuth.totp.code')}
        name="totpCode"
        autoComplete="one-time-code"
        value={code}
        onChange={(event) => setCode(event.target.value)}
        error={!!error}
        helperText={error ? fieldMessage(t, error) : undefined}
        fullWidth
        slotProps={{ htmlInput: { inputMode: 'numeric', maxLength: 6, pattern: '[0-9]*' } }}
      />
      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}
      <Button type="submit" variant="contained" size="large" disabled={submitting}>
        {t('staffAuth.totp.activate')}
      </Button>
    </Stack>
  );
}
