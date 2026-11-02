import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { resetPassword } from '../../api/generated';
import { AuthCard } from './AuthCard';
import { NewPasswordForm } from './NewPasswordForm';

type State = 'form' | 'done' | 'invalid';

/** `/recuperar/:token`: define a nova palavra-passe pelo link do e-mail. */
export function ResetPasswordPage() {
  const { t } = useTranslation();
  const { token = '' } = useParams();
  const [state, setState] = useState<State>('form');

  let content;
  if (state === 'done') {
    content = (
      <Stack spacing={2}>
        <Alert severity="success" role="status">
          <strong>{t('staffAuth.reset.done')}</strong> {t('staffAuth.reset.doneText')}
        </Alert>
        <Button variant="contained" component={RouterLink} to="/entrar">
          {t('staffAuth.reset.goToLogin')}
        </Button>
      </Stack>
    );
  } else if (state === 'invalid') {
    content = (
      <Stack spacing={2}>
        <Alert severity="error" role="alert">
          {t('staffAuth.reset.invalid')}
        </Alert>
        <Button variant="outlined" component={RouterLink} to="/recuperar">
          {t('staffAuth.reset.requestNew')}
        </Button>
      </Stack>
    );
  } else {
    content = (
      <NewPasswordForm
        submitLabel={t('staffAuth.reset.submit')}
        onSubmit={async (newPassword) => {
          await resetPassword({ token, newPassword });
          setState('done');
        }}
        onError={(problem) => {
          if (problem.code !== 'RESET_TOKEN_INVALID') return false;
          setState('invalid');
          return true;
        }}
      />
    );
  }

  return <AuthCard title={t('staffAuth.reset.title')}>{content}</AuthCard>;
}
