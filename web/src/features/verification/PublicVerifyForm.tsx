import { zodResolver } from '@hookform/resolvers/zod';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useMutation } from '@tanstack/react-query';
import { useRef, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';
import {
  verifyPlate,
  verifyPlateAsPolice,
  type VerificationConfigResponse,
} from '../../api/generated';
import { applyFieldErrors, fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { useResendCountdown } from '../customer-auth/useResendCountdown';
import { TurnstileWidget, type TurnstileHandle } from './TurnstileWidget';
import { VerificationResultCard, type VerificationView } from './VerificationResultCard';

/** Validação local da matrícula (a real é a do backend). */
export const platePattern = /^[A-Za-z0-9 -]{4,12}$/;

const schema = z.object({
  plate: z.string().trim().min(1, 'REQUIRED').regex(platePattern, 'INVALID_PLATE'),
});
type Values = z.infer<typeof schema>;

/**
 * Formulário da matrícula (S6-05 §2). `public`: com o CAPTCHA da configuração, `verifyPlate` com
 * `X-Captcha-Token`; `police`: sem CAPTCHA, `verifyPlateAsPolice`. Mostra o resultado no lugar do
 * formulário.
 */
export function PublicVerifyForm({
  mode = 'public',
  config,
}: {
  mode?: 'public' | 'police';
  config?: VerificationConfigResponse;
}) {
  const { t } = useTranslation();
  const police = mode === 'police';
  const captcha = !police && config?.captchaProvider === 'TURNSTILE' && !!config.captchaSiteKey;
  const turnstile = useRef<TurnstileHandle>(null);
  const [token, setToken] = useState<string | null>(null);
  const [result, setResult] = useState<VerificationView | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const countdown = useResendCountdown(0);

  const form = useForm<Values>({
    resolver: zodResolver(schema),
    defaultValues: { plate: '' },
  });

  const check = useMutation({
    mutationFn: async (plate: string): Promise<VerificationView> => {
      try {
        return police
          ? await verifyPlateAsPolice({ plate })
          : await verifyPlate(
              { plate },
              token ? { headers: { 'X-Captcha-Token': token } } : undefined,
            );
      } finally {
        // O token do CAPTCHA só serve uma vez: depois de cada resposta pede-se outro.
        if (captcha) turnstile.current?.reset();
      }
    },
    onSuccess: (data) => setResult(data),
    onError: (error) => {
      const problem = getProblem(error);
      if (problem.code === 'RATE_LIMITED' && problem.retryAfterSeconds) {
        countdown.restart(problem.retryAfterSeconds);
        return;
      }
      if (applyFieldErrors(problem, ['plate'] as const, form.setError)) return;
      setMessage(problemMessage(t, problem));
    },
  });

  const submit = form.handleSubmit((values) => {
    setMessage(null);
    check.mutate(values.plate.trim());
  });

  if (result) {
    return (
      <VerificationResultCard
        result={result}
        police={police}
        onNew={() => {
          setResult(null);
          form.reset({ plate: '' });
        }}
      />
    );
  }

  const waiting = !countdown.done;
  const disabled = check.isPending || waiting || (captcha && !token);

  return (
    <Stack component="form" spacing={2} onSubmit={submit} noValidate>
      <Controller
        control={form.control}
        name="plate"
        render={({ field, fieldState }) => (
          <TextField
            {...field}
            onChange={(event) => field.onChange(event.target.value.toUpperCase())}
            label={t('verify.plate')}
            placeholder="ABC-123-MC"
            autoComplete="off"
            autoCapitalize="characters"
            fullWidth
            error={!!fieldState.error}
            helperText={fieldState.error?.message ? fieldMessage(t, fieldState.error.message) : ' '}
            slotProps={{ htmlInput: { maxLength: 12, spellCheck: false } }}
          />
        )}
      />
      {captcha && config?.captchaSiteKey && (
        <TurnstileWidget ref={turnstile} siteKey={config.captchaSiteKey} onToken={setToken} />
      )}
      {waiting && (
        <Alert severity="warning" role="alert">
          {t('verify.rateLimitedCountdown', { seconds: countdown.remaining })}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}
      <Button type="submit" variant="contained" size="large" disabled={disabled}>
        {check.isPending ? t('verify.loading') : t('verify.submit')}
      </Button>
    </Stack>
  );
}
