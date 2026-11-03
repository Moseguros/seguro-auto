import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import type { TFunction } from 'i18next';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate, useSearchParams } from 'react-router-dom';
import { loginStaff } from '../../api/generated';
import { areaFor } from '../../auth/areas';
import { useAuth } from '../../auth/useAuth';
import { fieldMessage, getProblem, problemMessage, type Problem } from '../../errors/problem';
import { safeReturnPath } from '../customer-auth/CustomerLoginPage';
import { AuthCard } from './AuthCard';
import { PasswordField } from './PasswordField';

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const totpPattern = /^\d{6}$/;

/** Mensagem de um erro do login de staff. */
export function staffLoginMessage(t: TFunction, problem: Problem): string {
  if (problem.code === 'INVALID_CREDENTIALS') return t('staffAuth.login.invalidCredentials');
  if (problem.code === 'ACCOUNT_LOCKED' && problem.retryAfterSeconds !== undefined) {
    const count = Math.max(1, Math.ceil(problem.retryAfterSeconds / 60));
    return t('staffAuth.login.locked', { count });
  }
  return problemMessage(t, problem);
}

type Field = 'email' | 'password' | 'totpCode';

/** `/entrar`: login de staff com e-mail, palavra-passe e código TOTP. */
export function StaffLoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const { login } = useAuth();
  const [values, setValues] = useState<Record<Field, string>>({
    email: '',
    password: '',
    totpCode: '',
  });
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>({});
  const [message, setMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const set = (field: Field) => (event: { target: { value: string } }) =>
    setValues((v) => ({ ...v, [field]: event.target.value }));

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const email = values.email.trim();
    const totpCode = values.totpCode.replace(/\s+/g, '');
    const found: Partial<Record<Field, string>> = {};
    if (!email) found.email = 'REQUIRED';
    else if (!emailPattern.test(email)) found.email = 'INVALID_EMAIL';
    if (!values.password) found.password = 'REQUIRED';
    if (!totpCode) found.totpCode = 'REQUIRED';
    else if (!totpPattern.test(totpCode)) found.totpCode = 'PATTERN';
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setSubmitting(true);
    try {
      const tokens = await loginStaff({ email, password: values.password, totpCode });
      const me = await login(tokens, 'STAFF');
      navigate(safeReturnPath(params.get('voltar')) ?? areaFor(me).path, { replace: true });
    } catch (error) {
      setMessage(staffLoginMessage(t, getProblem(error)));
      setValues((v) => ({ ...v, totpCode: '' }));
      setSubmitting(false);
    }
  };

  const text = (field: Field) => (errors[field] ? fieldMessage(t, errors[field]) : undefined);

  return (
    <AuthCard title={t('staffAuth.login.title')} subtitle={t('staffAuth.login.subtitle')}>
      <Stack component="form" spacing={2} onSubmit={submit} noValidate>
        <TextField
          label={t('staffAuth.login.email')}
          name="email"
          type="email"
          autoComplete="username"
          value={values.email}
          onChange={set('email')}
          error={!!errors.email}
          helperText={text('email')}
          fullWidth
        />
        <PasswordField
          label={t('staffAuth.login.password')}
          name="password"
          autoComplete="current-password"
          value={values.password}
          onChange={set('password')}
          errorCodes={errors.password ? [errors.password] : []}
        />
        <TextField
          label={t('staffAuth.login.totp')}
          name="totpCode"
          autoComplete="one-time-code"
          value={values.totpCode}
          onChange={set('totpCode')}
          error={!!errors.totpCode}
          helperText={text('totpCode') ?? t('staffAuth.login.totpHelper')}
          fullWidth
          slotProps={{ htmlInput: { inputMode: 'numeric', maxLength: 6, pattern: '[0-9]*' } }}
        />
        {message && (
          <Alert severity="error" role="alert">
            {message}
          </Alert>
        )}
        <Button type="submit" variant="contained" size="large" disabled={submitting}>
          {t('staffAuth.login.submit')}
        </Button>
        <Stack spacing={1} alignItems="center">
          <Link component={RouterLink} to="/recuperar">
            {t('staffAuth.login.forgot')}
          </Link>
          <Link component={RouterLink} to="/cliente/entrar" variant="body2" color="text.secondary">
            {t('staffAuth.login.customer')}
          </Link>
        </Stack>
      </Stack>
    </AuthCard>
  );
}
