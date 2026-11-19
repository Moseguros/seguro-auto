import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { formatTime } from '../../i18n/format';

/** Estado `UNKNOWN` (S5-11 §4): estamos a confirmar; sem botões de pagamento. */
export function CheckingStatus({ nextCheckAt }: { nextCheckAt?: string }) {
  const { t } = useTranslation();
  return (
    <Stack spacing={2}>
      <Alert severity="warning" role="status">
        {t('payments.checking.lead')} <strong>{t('payments.checking.dontPay')}</strong>
        {t('payments.checking.rest')}
      </Alert>
      {nextCheckAt && (
        <Typography color="text.secondary">
          {t('payments.checking.nextCheck', { time: formatTime(nextCheckAt) })}
        </Typography>
      )}
    </Stack>
  );
}
