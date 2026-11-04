import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActions from '@mui/material/CardActions';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { useAuth } from '../../auth/useAuth';
import { RecentQuotes } from '../quotes/RecentQuotes';

export function firstName(displayName: string | undefined): string | undefined {
  const name = displayName?.trim();
  return name ? name.split(/\s+/)[0] : undefined;
}

/** `/cliente`: saudação, aviso de perfil incompleto, "Simular seguro" e as cotações recentes. */
export function CustomerHomePage() {
  const { t } = useTranslation();
  const { me } = useAuth();
  const location = useLocation();
  const [saved, setSaved] = useState(
    () => (location.state as { profileSaved?: boolean } | null)?.profileSaved === true,
  );
  const name = firstName(me?.displayName);

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {name ? t('customer.home.greeting', { name }) : t('customer.home.greetingNoName')}
        </Typography>

        {me?.profileComplete === false && (
          <Card>
            <CardContent>
              <Typography>{t('customer.home.incompleteProfile')}</Typography>
            </CardContent>
            <CardActions sx={{ px: 2, pb: 2 }}>
              <Button variant="contained" component={RouterLink} to="/cliente/perfil?completar=1">
                {t('customer.home.completeProfile')}
              </Button>
            </CardActions>
          </Card>
        )}

        <Card>
          <CardContent>
            <Stack spacing={2} alignItems="flex-start">
              <Typography variant="h6" component="h2">
                {t('customer.home.quoteTitle')}
              </Typography>
              <Typography color="text.secondary">{t('customer.home.quoteText')}</Typography>
              <Button variant="cta" size="large" component={RouterLink} to="/cliente/simular">
                {t('customer.home.quote')}
              </Button>
            </Stack>
          </CardContent>
        </Card>

        <RecentQuotes card />
      </Stack>
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('profile.saved')}
      />
    </Container>
  );
}
