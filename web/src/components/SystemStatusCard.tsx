import Alert from '@mui/material/Alert';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { errorCode } from '../api/errors';
import { useGetVersion } from '../api/generated';

/** Cartão "Estado do sistema": versão e commit da API (`GET /api/v1/meta/version`). */
export function SystemStatusCard() {
  const { t, i18n } = useTranslation();
  const { data, error, isPending, isError } = useGetVersion();

  let content;
  if (isPending) {
    content = (
      <Stack direction="row" spacing={2} alignItems="center">
        <CircularProgress size={20} aria-hidden />
        <Typography>{t('systemStatus.loading')}</Typography>
      </Stack>
    );
  } else if (isError) {
    const code = errorCode(error);
    const key = i18n.exists(`errors.${code}`) ? `errors.${code}` : 'errors.UNKNOWN';
    content = (
      <Alert severity="error">
        {t('systemStatus.error')} {t(key)}
      </Alert>
    );
  } else {
    content = (
      <Stack component="dl" spacing={1} sx={{ m: 0 }}>
        <Stack direction="row" spacing={1}>
          <Typography component="dt" color="text.secondary">
            {t('systemStatus.version')}:
          </Typography>
          <Typography component="dd" sx={{ m: 0, fontWeight: 600 }} data-testid="api-version">
            {data.version ?? t('systemStatus.unknown')}
          </Typography>
        </Stack>
        <Stack direction="row" spacing={1}>
          <Typography component="dt" color="text.secondary">
            {t('systemStatus.commit')}:
          </Typography>
          <Typography
            component="dd"
            sx={{ m: 0, fontFamily: 'monospace', wordBreak: 'break-all' }}
            data-testid="api-commit"
          >
            {data.commit ?? t('systemStatus.unknown')}
          </Typography>
        </Stack>
      </Stack>
    );
  }

  return (
    <Card component="section" aria-labelledby="system-status-title">
      <CardContent>
        <Typography id="system-status-title" variant="h6" component="h2" gutterBottom>
          {t('systemStatus.title')}
        </Typography>
        {content}
      </CardContent>
    </Card>
  );
}
