import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormLabel from '@mui/material/FormLabel';
import InputAdornment from '@mui/material/InputAdornment';
import Radio from '@mui/material/Radio';
import RadioGroup from '@mui/material/RadioGroup';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import type { OtpOptionsResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import {
  emailPattern,
  msisdnDigitsPattern,
  msisdnPrefix,
  type IdentifierType,
  type OtpChannel,
  type OtpRequest,
  type RequestCode,
  type SentCode,
} from './otp';
import { useResendCountdown } from './useResendCountdown';

export interface IdentifierStepProps {
  options: OtpOptionsResponse;
  /** Tipos permitidos; por defeito os `identifierTypes` das opções. */
  types?: IdentifierType[];
  /** Pedido anterior (ao voltar do passo do código). */
  initial?: OtpRequest;
  /** Pede o código: `requestOtp` no login, `startContactVerification` nos contactos. */
  onRequest: RequestCode;
  onSent(sent: SentCode): void;
}

function localDigits(identifier: string): string {
  const digits = identifier.replace(/\D/g, '');
  return digits.startsWith('258') ? digits.slice(3) : digits;
}

/** Passo 1: telemóvel ou e-mail e, se permitido, o canal de envio. */
export function IdentifierStep({
  options,
  types,
  initial,
  onRequest,
  onSent,
}: IdentifierStepProps) {
  const { t } = useTranslation();
  const allowed: IdentifierType[] = types ?? options.identifierTypes;
  const [type, setType] = useState<IdentifierType>(
    initial && allowed.includes(initial.identifierType)
      ? initial.identifierType
      : (allowed[0] ?? 'MSISDN'),
  );
  const [msisdn, setMsisdn] = useState(
    initial?.identifierType === 'MSISDN' ? localDigits(initial.identifier) : '',
  );
  const [email, setEmail] = useState(initial?.identifierType === 'EMAIL' ? initial.identifier : '');
  const [chosen, setChosen] = useState<Partial<Record<IdentifierType, OtpChannel>>>(
    initial?.channel ? { [initial.identifierType]: initial.channel } : {},
  );
  const [fieldError, setFieldError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const wait = useResendCountdown(0);

  const channels = (options.channels[type] ?? []) as OtpChannel[];
  const defaultChannel = options.defaultChannels[type] as OtpChannel | undefined;
  const canChoose = options.customerCanChoose && channels.length > 1;
  const channel: OtpChannel | undefined =
    chosen[type] && channels.includes(chosen[type])
      ? chosen[type]
      : (defaultChannel ?? channels[0]);

  function changeType(next: IdentifierType) {
    setType(next);
    setFieldError(null);
  }

  async function submit(event: FormEvent) {
    event.preventDefault();
    setMessage(null);
    let identifier: string;
    if (type === 'MSISDN') {
      if (!msisdnDigitsPattern.test(msisdn)) {
        setFieldError('INVALID_MSISDN');
        return;
      }
      identifier = `${msisdnPrefix}${msisdn}`;
    } else {
      const value = email.trim();
      if (!emailPattern.test(value)) {
        setFieldError('INVALID_EMAIL');
        return;
      }
      identifier = value;
    }
    setFieldError(null);
    const request: OtpRequest = {
      identifierType: type,
      identifier,
      ...(canChoose && channel ? { channel } : {}),
    };
    setSubmitting(true);
    try {
      const response = await onRequest(request);
      onSent({ request, requestedChannel: channel, response });
    } catch (error) {
      const problem = getProblem(error);
      const onField = problem.errors.find((e) =>
        ['identifier', 'value', 'identifierType', 'type'].includes(e.field),
      );
      if (problem.status === 429) {
        wait.restart(problem.retryAfterSeconds ?? 0);
        setMessage(problemMessage(t, problem));
      } else if (onField) {
        setFieldError(onField.code);
      } else {
        setMessage(problemMessage(t, problem));
        if (problem.code === 'OTP_CHANNEL_NOT_AVAILABLE') {
          const other = allowed.find((candidate) => candidate !== type);
          if (other) changeType(other);
        }
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Stack component="form" spacing={2.5} onSubmit={submit} noValidate>
      {allowed.length > 1 && (
        <ToggleButtonGroup
          exclusive
          fullWidth
          color="primary"
          value={type}
          onChange={(_, value: IdentifierType | null) => value && changeType(value)}
          aria-label={t('auth.identifier.typeLabel')}
        >
          {allowed.map((candidate) => (
            <ToggleButton key={candidate} value={candidate}>
              {t(`auth.identifier.type.${candidate}`)}
            </ToggleButton>
          ))}
        </ToggleButtonGroup>
      )}

      {type === 'MSISDN' ? (
        <TextField
          label={t('auth.identifier.msisdnLabel')}
          value={msisdn}
          onChange={(event) => setMsisdn(event.target.value.replace(/\D/g, '').slice(0, 9))}
          error={fieldError !== null}
          helperText={fieldError ? fieldMessage(t, fieldError) : t('auth.identifier.msisdnHelper')}
          autoComplete="tel-national"
          fullWidth
          slotProps={{
            input: {
              startAdornment: <InputAdornment position="start">{msisdnPrefix}</InputAdornment>,
            },
            htmlInput: { inputMode: 'numeric', maxLength: 9 },
          }}
        />
      ) : (
        <TextField
          label={t('auth.identifier.emailLabel')}
          type="email"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          error={fieldError !== null}
          helperText={fieldError ? fieldMessage(t, fieldError) : undefined}
          autoComplete="email"
          fullWidth
        />
      )}

      {canChoose && (
        <FormControl>
          <FormLabel id="otp-channel-label">{t('auth.identifier.channelLabel')}</FormLabel>
          <RadioGroup
            row
            aria-labelledby="otp-channel-label"
            value={channel ?? ''}
            onChange={(event) =>
              setChosen((previous) => ({ ...previous, [type]: event.target.value as OtpChannel }))
            }
          >
            {channels.map((candidate) => (
              <FormControlLabel
                key={candidate}
                value={candidate}
                control={<Radio />}
                label={t(`channel.${candidate}`)}
              />
            ))}
          </RadioGroup>
        </FormControl>
      )}

      {message && (
        <Alert severity="error" role="alert">
          {message}
          {!wait.done && ` ${t('auth.retryIn', { seconds: wait.remaining })}`}
        </Alert>
      )}

      <Button
        type="submit"
        variant="contained"
        size="large"
        disabled={submitting || !wait.done}
        fullWidth
      >
        {t('auth.identifier.submit')}
      </Button>
    </Stack>
  );
}
