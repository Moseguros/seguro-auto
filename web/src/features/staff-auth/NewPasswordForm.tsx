import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { getProblem, problemMessage, type Problem } from '../../errors/problem';
import { PasswordField, passwordMinLength } from './PasswordField';

export interface NewPasswordFormProps {
  /** E-mail da conta, se conhecido (regra "não conter o e-mail"). */
  email?: string;
  submitLabel: string;
  /** Grava a palavra-passe; os erros da API são tratados aqui, salvo os que `onError` tratar. */
  onSubmit(password: string): Promise<void>;
  /** Devolve `true` se tratou o erro (ex.: token inválido). */
  onError?(problem: Problem): boolean;
}

/** Nova palavra-passe e confirmação, com as regras e os erros `PASSWORD_TOO_WEAK` por campo. */
export function NewPasswordForm({ email, submitLabel, onSubmit, onError }: NewPasswordFormProps) {
  const { t } = useTranslation();
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [codes, setCodes] = useState<string[]>([]);
  const [mismatch, setMismatch] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const local: string[] = [];
    if (!password) local.push('REQUIRED');
    else if (password.length < passwordMinLength) local.push('TOO_SHORT');
    setCodes(local);
    const different = password !== confirm;
    setMismatch(different);
    if (local.length > 0 || different) return;
    setSubmitting(true);
    try {
      await onSubmit(password);
    } catch (error) {
      const problem = getProblem(error);
      if (onError?.(problem)) return;
      if (problem.code === 'PASSWORD_TOO_WEAK' || problem.code === 'VALIDATION_ERROR') {
        const fieldCodes = problem.errors
          .filter((e) => e.field === 'password' || e.field === 'newPassword')
          .map((e) => e.code);
        if (fieldCodes.length > 0) {
          setCodes(fieldCodes);
          return;
        }
      }
      setMessage(problemMessage(t, problem));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Stack component="form" spacing={2} onSubmit={submit} noValidate>
      <PasswordField
        label={t('staffAuth.password.new')}
        name="password"
        value={password}
        onChange={(event) => setPassword(event.target.value)}
        showRules
        email={email}
        errorCodes={codes}
      />
      <PasswordField
        label={t('staffAuth.password.confirm')}
        name="confirm"
        value={confirm}
        onChange={(event) => setConfirm(event.target.value)}
        errorText={mismatch ? t('staffAuth.password.mismatch') : undefined}
      />
      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}
      <Button type="submit" variant="contained" size="large" disabled={submitting}>
        {submitLabel}
      </Button>
    </Stack>
  );
}
