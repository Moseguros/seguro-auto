import Alert from '@mui/material/Alert';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../auth/useAuth';
import { UsersPanel } from './UsersPanel';

/**
 * `/admin/utilizadores` (staff da plataforma) e `/<área>/utilizadores` (utilizadores da
 * organização de quem tem a sessão).
 */
export function UsersPage({ platform = false }: { platform?: boolean }) {
  const { t } = useTranslation();
  const { me } = useAuth();
  const organizationId = me?.organization?.id;

  return (
    <Stack spacing={3}>
      <Typography variant="h4" component="h1">
        {t('users.title')}
      </Typography>
      {platform ? (
        <UsersPanel scope={{ kind: 'platform' }} />
      ) : organizationId ? (
        <UsersPanel scope={{ kind: 'organization', organizationId }} />
      ) : (
        <Alert severity="error">{t('errors.FORBIDDEN')}</Alert>
      )}
    </Stack>
  );
}
