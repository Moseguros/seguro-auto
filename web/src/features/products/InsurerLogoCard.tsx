import Avatar from '@mui/material/Avatar';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Alert from '@mui/material/Alert';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { getInsurerBranding, removeInsurerLogo, uploadInsurerLogo } from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { useAuth } from '../../auth/useAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { FileUploadButton } from '../../files/FileUploadButton';
import { useFileBlob } from '../../files/useFileBlob';
import { brandingQueryKey } from './productQueries';

const maxLogoBytes = 1024 * 1024;

function initials(name: string): string {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((word) => word[0]?.toUpperCase() ?? '')
    .join('');
}

/** Logótipo da seguradora (S2-04 §7.1): pré-visualização, envio e remoção. */
export function InsurerLogoCard() {
  const { t } = useTranslation();
  const { me } = useAuth();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.productManage);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [confirmRemove, setConfirmRemove] = useState(false);
  const branding = useQuery({
    queryKey: brandingQueryKey,
    queryFn: ({ signal }) => getInsurerBranding(undefined, signal),
  });
  const logoFileId = branding.data?.logoFileId ?? null;
  const logo = useFileBlob(logoFileId);
  const insurerName = me?.organization?.displayName ?? '';

  const upload = async (file: File) => {
    setBusy(true);
    setMessage(null);
    try {
      queryClient.setQueryData(brandingQueryKey, await uploadInsurerLogo({ file }));
    } catch (error) {
      const problem = getProblem(error);
      setMessage(
        problem.errors.some((e) => e.field === 'file' && e.code === 'OUT_OF_RANGE')
          ? t('products.logo.tooLarge')
          : problemMessage(t, problem),
      );
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    setConfirmRemove(false);
    setBusy(true);
    setMessage(null);
    try {
      await removeInsurerLogo();
      queryClient.setQueryData(brandingQueryKey, { logoFileId: null });
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card variant="outlined" component="section" aria-label={t('products.logo.title')}>
      <CardContent>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} alignItems={{ sm: 'center' }}>
          <Box
            sx={{
              width: 160,
              height: 60,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0,
            }}
          >
            {logo.url ? (
              <Box
                component="img"
                src={logo.url}
                alt={t('products.logo.alt', { name: insurerName })}
                sx={{ maxWidth: 160, maxHeight: 60, objectFit: 'contain' }}
              />
            ) : (
              <Avatar sx={{ bgcolor: 'primary.main', width: 56, height: 56 }}>
                {initials(insurerName)}
              </Avatar>
            )}
          </Box>
          <Stack spacing={1} sx={{ minWidth: 0, flexGrow: 1 }}>
            <Typography variant="h6" component="h2">
              {t('products.logo.title')}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('products.logo.note')}
            </Typography>
            {canManage && (
              <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                <FileUploadButton
                  label={logoFileId ? t('products.logo.replace') : t('products.logo.upload')}
                  accept="image/png,image/jpeg"
                  maxBytes={maxLogoBytes}
                  tooLargeMessage={t('products.logo.tooLarge')}
                  onFile={(file) => void upload(file)}
                  disabled={busy || branding.isPending}
                />
                {logoFileId && (
                  <Button
                    size="small"
                    color="error"
                    onClick={() => setConfirmRemove(true)}
                    disabled={busy}
                  >
                    {t('products.logo.remove')}
                  </Button>
                )}
              </Stack>
            )}
            {message && (
              <Alert severity="error" role="alert">
                {message}
              </Alert>
            )}
          </Stack>
        </Stack>
      </CardContent>
      <Dialog
        open={confirmRemove}
        onClose={() => setConfirmRemove(false)}
        aria-labelledby="logo-remove-title"
      >
        <DialogTitle id="logo-remove-title">{t('products.logo.removeDialog.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>{t('products.logo.removeDialog.text')}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirmRemove(false)}>{t('common.cancel')}</Button>
          <Button color="error" variant="contained" onClick={() => void remove()}>
            {t('products.logo.remove')}
          </Button>
        </DialogActions>
      </Dialog>
    </Card>
  );
}
