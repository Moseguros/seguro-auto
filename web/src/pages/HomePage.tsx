import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { SystemStatusCard } from '../components/SystemStatusCard';

export function HomePage() {
  const { t } = useTranslation();
  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3} sx={{ py: { xs: 2, sm: 6 } }}>
        <Typography variant="h3" component="h1" color="primary">
          {t('app.name')}
        </Typography>
        <Typography variant="h6" component="p" color="text.secondary">
          {t('home.tagline')}
        </Typography>
        <SystemStatusCard />
      </Stack>
    </Container>
  );
}
