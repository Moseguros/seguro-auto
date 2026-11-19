import Alert from '@mui/material/Alert';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { LastTestResponse } from '../../api/generated';
import { formatStamp } from '../insurer-payments/format';

/** Resultado do último "Testar ligação" (S5-12 §2). */
export function TestResultAlert({ test }: { test: LastTestResponse | undefined }) {
  const { t, i18n } = useTranslation();
  if (!test) return null;
  const messageKey = test.message ? `paymentTest.${test.message}` : undefined;
  const message = messageKey && i18n.exists(messageKey) ? t(messageKey) : undefined;
  const last = (
    <Typography variant="caption" component="p" sx={{ mt: 0.5 }}>
      {t('paymentSettingsPage.test.last', { date: formatStamp(test.at) })}
    </Typography>
  );
  if (test.result === 'OK') {
    return (
      <Alert severity="success" data-testid="test-result">
        {t('paymentSettingsPage.test.OK')}
        {message && ` — ${message}`}
        {last}
      </Alert>
    );
  }
  if (test.result === 'NOT_SUPPORTED') {
    return (
      <Alert severity="info" data-testid="test-result">
        {t('paymentSettingsPage.test.NOT_SUPPORTED')}
        {last}
      </Alert>
    );
  }
  return (
    <Alert severity="error" data-testid="test-result">
      {message ?? t('paymentSettingsPage.test.failedUnknown')}
      {last}
    </Alert>
  );
}
