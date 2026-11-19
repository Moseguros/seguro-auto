import Payments from '@mui/icons-material/Payments';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { paymentSettingsPath } from '../insurer-payments/common';

/**
 * `/seguradora/configuracao`: até aqui "Em construção"; o S5-12 acrescenta o cartão "Formas de
 * pagamento". As tarefas seguintes acrescentam os seus cartões.
 */
export function InsurerSettingsPage() {
  const { t } = useTranslation();
  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Typography variant="h4" component="h1">
        {t('insurerSettings.title')}
      </Typography>
      <Card variant="outlined" sx={{ maxWidth: 480 }}>
        <CardActionArea
          component={RouterLink}
          to={paymentSettingsPath}
          aria-label={t('insurerSettings.paymentMethods.open')}
        >
          <CardContent>
            <Stack direction="row" spacing={2} alignItems="flex-start">
              <Payments color="primary" aria-hidden />
              <Stack spacing={0.5}>
                <Typography variant="h6" component="h2">
                  {t('insurerSettings.paymentMethods.title')}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {t('insurerSettings.paymentMethods.description')}
                </Typography>
              </Stack>
            </Stack>
          </CardContent>
        </CardActionArea>
      </Card>
    </Stack>
  );
}
