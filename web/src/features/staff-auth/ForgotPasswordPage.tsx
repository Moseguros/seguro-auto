import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { forgotPassword } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { AuthCard } from './AuthCard';

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

/**
 * `/recuperar`: pede o link de recuperação. Depois de enviar mostra sempre a mesma mensagem
 * (não se revela se o e-mail existe); só a falta de ligação ao servidor aparece como erro.
 */
export function ForgotPasswordPage() {
  const { t } = useTranslation();
  const [email, setEmail] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [sent, setSent] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const value = email.trim();
    const code = !value ? 'REQUIRED' : emailPattern.test(value) ? null : 'INVALID_EMAIL';
    setError(code);
    if (code) return;
    setSubmitting(true);
    try {
      await forgotPassword({ email: value });
      setSent(true);
    } catch (e) {
      const problem = getProblem(e);
      if (problem.status === 0 || problem.status >= 500) setMessage(problemMessage(t, problem));
      else setSent(true);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <AuthCard
      title={t('staffAuth.forgot.title')}
      subtitle={sent ? undefined : t('staffAuth.forgot.subtitle')}
    >
      {sent ? (
        <Stack spacing={2}>
          <Alert severity="success" role="status">
            {t('staffAuth.forgot.sent')}
          </Alert>
          <Link component={RouterLink} to="/entrar">
            {t('staffAuth.forgot.backToLogin')}
          </Link>
        </Stack>
      ) : (
        <Stack component="form" spacing={2} onSubmit={submit} noValidate>
          <TextField
            label={t('staffAuth.login.email')}
            name="email"
            type="email"
            autoComplete="username"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            error={!!error}
            helperText={error ? fieldMessage(t, error) : undefined}
            fullWidth
          />
          {message && (
            <Alert severity="error" role="alert">
              {message}
            </Alert>
          )}
          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            {t('staffAuth.forgot.submit')}
          </Button>
          <Link component={RouterLink} to="/entrar" sx={{ alignSelf: 'center' }}>
            {t('staffAuth.forgot.backToLogin')}
          </Link>
        </Stack>
      )}
    </AuthCard>
  );
}
