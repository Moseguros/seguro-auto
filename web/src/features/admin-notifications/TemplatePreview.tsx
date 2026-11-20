import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { NotificationChannel, NotificationTemplatePreview } from '../../api/generated';
import { SmsCounter } from './SmsCounter';

/**
 * Pré-visualização do modelo com os valores de exemplo (S6-05 §7). Em SMS mostra também o texto
 * normalizado (sem acentos) e o contador.
 */
export function TemplatePreview({
  channel,
  preview,
  loading,
}: {
  channel: NotificationChannel;
  preview: NotificationTemplatePreview | null;
  loading: boolean;
}) {
  const { t } = useTranslation();
  return (
    <Paper variant="outlined" sx={{ p: 2, minWidth: 0 }} data-testid="template-preview">
      <Stack spacing={1.5}>
        <Stack direction="row" justifyContent="space-between" alignItems="center" spacing={1}>
          <Typography variant="subtitle2" component="h2">
            {t('notificationTemplates.preview.title')}
          </Typography>
          {loading && (
            <Typography variant="caption" color="text.secondary">
              {t('notificationTemplates.preview.loading')}
            </Typography>
          )}
        </Stack>
        {preview?.subject && (
          <Stack spacing={0.25}>
            <Typography variant="caption" color="text.secondary">
              {channel === 'PUSH'
                ? t('notificationTemplates.editor.pushTitle')
                : t('notificationTemplates.preview.subject')}
            </Typography>
            <Typography fontWeight={600} sx={{ wordBreak: 'break-word' }}>
              {preview.subject}
            </Typography>
          </Stack>
        )}
        {preview?.body !== undefined && (
          <Stack spacing={0.25}>
            <Typography variant="caption" color="text.secondary">
              {t('notificationTemplates.preview.body')}
            </Typography>
            <Box
              data-testid="preview-body"
              sx={{ whiteSpace: 'pre-wrap', wordBreak: 'break-word', fontSize: '0.875rem' }}
            >
              {preview.body}
            </Box>
          </Stack>
        )}
        {channel === 'SMS' && preview?.smsText !== undefined && (
          <Stack spacing={0.25}>
            <Typography variant="caption" color="text.secondary">
              {t('notificationTemplates.preview.smsText')}
            </Typography>
            <Box
              data-testid="preview-sms"
              sx={{
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
                fontFamily: 'monospace',
                fontSize: '0.875rem',
              }}
            >
              {preview.smsText}
            </Box>
            {preview.length !== undefined && preview.limit !== undefined && (
              <SmsCounter
                length={preview.length}
                limit={preview.limit}
                fits={preview.fits !== false}
              />
            )}
          </Stack>
        )}
      </Stack>
    </Paper>
  );
}
