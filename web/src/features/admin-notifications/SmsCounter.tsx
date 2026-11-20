import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';

/** Contador `{length}/{limit}` do SMS; a vermelho quando não cabe (`fits = false`). */
export function SmsCounter({
  length,
  limit,
  fits,
}: {
  length: number;
  limit: number;
  fits: boolean;
}) {
  const { t } = useTranslation();
  return (
    <Typography
      variant="body2"
      data-testid="sms-counter"
      data-fits={fits ? 'true' : 'false'}
      aria-label={`${t('notificationTemplates.preview.smsCounter')}: ${length}/${limit}`}
      sx={{ color: fits ? 'text.secondary' : 'error.main', fontWeight: fits ? 400 : 700 }}
    >
      {length}/{limit}
    </Typography>
  );
}
