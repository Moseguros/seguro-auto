import Block from '@mui/icons-material/Block';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { areaFor } from '../auth/areas';
import { useAuth } from '../auth/useAuth';

/** `/sem-acesso`: a sessão não é do tipo ou da área pedida. */
export function NoAccessPage() {
  const { t } = useTranslation();
  const { me } = useAuth();
  const target = me ? areaFor(me).path : '/';
  return (
    <Stack spacing={2} alignItems="center" sx={{ py: 6, textAlign: 'center' }}>
      <Block color="primary" sx={{ fontSize: 56 }} aria-hidden />
      <Typography variant="h4" component="h1">
        {t('auth.noAccess.title')}
      </Typography>
      <Typography color="text.secondary">{t('auth.noAccess.description')}</Typography>
      <Button variant="contained" component={RouterLink} to={target}>
        {me ? t('auth.noAccess.goToArea') : t('auth.noAccess.goHome')}
      </Button>
    </Stack>
  );
}
