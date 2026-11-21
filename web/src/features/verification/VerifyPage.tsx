import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { getGetVerificationConfigQueryKey, getVerificationConfig } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { usePoliceSession, VerifyColumn } from './common';
import { PoliceVerifyPanel } from './PoliceVerifyPanel';
import { PublicVerifyForm } from './PublicVerifyForm';

function PublicVerify() {
  const { t } = useTranslation();
  const config = useQuery({
    queryKey: getGetVerificationConfigQueryKey(),
    queryFn: ({ signal }) => getVerificationConfig(undefined, signal),
    staleTime: 10 * 60 * 1000,
  });
  if (config.isLoading) return <LoadingScreen />;
  if (config.isError || !config.data) {
    return (
      <Alert
        severity="error"
        role="alert"
        action={
          <Button color="inherit" size="small" onClick={() => void config.refetch()}>
            {t('common.retry')}
          </Button>
        }
      >
        {problemMessage(t, getProblem(config.error))}
      </Alert>
    );
  }
  return (
    <Stack spacing={2}>
      <PublicVerifyForm config={config.data} />
      <Typography variant="body2" color="text.secondary">
        {t('verify.qrHint')}
      </Typography>
    </Stack>
  );
}

/**
 * `/verificar` (S6-05 §2 e §5): verificação pública por matrícula com CAPTCHA ou, com sessão de
 * staff com `verification:police`, o painel da polícia.
 */
export function VerifyPage() {
  const { t } = useTranslation();
  const { loading, police } = usePoliceSession();
  return (
    <VerifyColumn>
      <Stack spacing={2}>
        <Typography variant="h5" component="h1">
          {police ? t('police.title') : t('verify.title')}
        </Typography>
        {!police && <Typography>{t('verify.intro')}</Typography>}
        {loading ? <LoadingScreen /> : police ? <PoliceVerifyPanel /> : <PublicVerify />}
      </Stack>
    </VerifyColumn>
  );
}
