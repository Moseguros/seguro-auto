import Construction from '@mui/icons-material/Construction';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';

/** Página provisória das rotas que ainda não têm conteúdo. */
export function UnderConstructionPage() {
  const { t } = useTranslation();
  return (
    <Stack spacing={2} alignItems="center" sx={{ py: 6, textAlign: 'center' }}>
      <Construction color="primary" sx={{ fontSize: 56 }} aria-hidden />
      <Typography variant="h4" component="h1">
        {t('underConstruction.title')}
      </Typography>
      <Typography color="text.secondary">{t('underConstruction.description')}</Typography>
    </Stack>
  );
}
