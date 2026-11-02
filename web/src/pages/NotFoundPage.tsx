import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';

export function NotFoundPage() {
  const { t } = useTranslation();
  return (
    <Stack spacing={2} alignItems="center" sx={{ py: 6, textAlign: 'center' }}>
      <Typography variant="h2" component="p" color="primary" aria-hidden>
        404
      </Typography>
      <Typography variant="h4" component="h1">
        {t('notFound.title')}
      </Typography>
      <Typography color="text.secondary">{t('notFound.description')}</Typography>
      <Button variant="contained" component={RouterLink} to="/">
        {t('notFound.backHome')}
      </Button>
    </Stack>
  );
}
