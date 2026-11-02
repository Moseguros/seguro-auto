import Email from '@mui/icons-material/Email';
import Smartphone from '@mui/icons-material/Smartphone';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  getListMyContactsQueryKey,
  listMyContacts,
  removeMyContact,
  type ContactResponse,
  type ContactsResponse,
} from '../../api/generated';
import { meQueryKey } from '../../auth/AuthProvider';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import type { IdentifierType } from '../customer-auth/otp';
import { ContactDialog } from './ContactDialog';

const contactTypes: IdentifierType[] = ['MSISDN', 'EMAIL'];

/** Secção "Contactos" do perfil: telemóvel e e-mail verificados. */
export function ContactsSection() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const queryKey = getListMyContactsQueryKey();
  const contacts = useQuery({
    queryKey,
    queryFn: ({ signal }) => listMyContacts(undefined, signal),
  });
  const [dialog, setDialog] = useState<IdentifierType | null>(null);
  const [removing, setRemoving] = useState<IdentifierType | null>(null);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [notice, setNotice] = useState<string | null>(null);

  const byType = (type: IdentifierType): ContactResponse | undefined =>
    contacts.data?.contacts.find((contact) => contact.type === type);

  function verified(data: ContactsResponse) {
    queryClient.setQueryData(queryKey, data);
    void queryClient.invalidateQueries({ queryKey: meQueryKey });
    setDialog(null);
    setMessage(null);
    setNotice(t('contacts.saved'));
  }

  async function confirmRemove() {
    if (!removing) return;
    setBusy(true);
    setMessage(null);
    try {
      await removeMyContact(removing);
      await queryClient.invalidateQueries({ queryKey });
      void queryClient.invalidateQueries({ queryKey: meQueryKey });
      setNotice(t('contacts.removed'));
    } catch (error) {
      setMessage(problemMessage(t, getProblem(error)));
    } finally {
      setBusy(false);
      setRemoving(null);
    }
  }

  let body;
  if (contacts.isPending) body = <LoadingScreen />;
  else if (contacts.isError) {
    body = <Alert severity="error">{problemMessage(t, getProblem(contacts.error))}</Alert>;
  } else {
    body = (
      <Stack spacing={2}>
        {contactTypes.map((type) => {
          const contact = byType(type);
          const Icon = type === 'MSISDN' ? Smartphone : Email;
          if (!contact) {
            return (
              <Button
                key={type}
                variant="outlined"
                startIcon={<Icon />}
                onClick={() => setDialog(type)}
                sx={{ alignSelf: 'flex-start' }}
              >
                {t(`contacts.add.${type}`)}
              </Button>
            );
          }
          return (
            <Stack
              key={type}
              direction="row"
              spacing={1.5}
              alignItems="center"
              flexWrap="wrap"
              useFlexGap
              data-testid={`contact-${type}`}
            >
              <Icon color="primary" aria-hidden />
              <Stack sx={{ minWidth: 0, flex: '1 1 160px' }}>
                <Typography variant="body2" color="text.secondary">
                  {t(`contacts.type.${type}`)}
                </Typography>
                <Typography sx={{ overflowWrap: 'anywhere' }}>{contact.value}</Typography>
              </Stack>
              {contact.verified && (
                <Chip
                  size="small"
                  color="success"
                  variant="outlined"
                  label={t('contacts.verified')}
                />
              )}
              <Stack direction="row" spacing={1}>
                <Button
                  size="small"
                  onClick={() => setDialog(type)}
                  aria-label={t(`contacts.change.${type}`)}
                >
                  {t('contacts.changeShort')}
                </Button>
                <Button
                  size="small"
                  color="error"
                  onClick={() => setRemoving(type)}
                  aria-label={t(`contacts.remove.${type}`)}
                >
                  {t('contacts.removeShort')}
                </Button>
              </Stack>
            </Stack>
          );
        })}
        {message && (
          <Alert severity="error" role="alert">
            {message}
          </Alert>
        )}
      </Stack>
    );
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h6" component="h2" sx={{ mb: 2 }}>
          {t('contacts.title')}
        </Typography>
        {body}
      </CardContent>

      {dialog && (
        <ContactDialog
          type={dialog}
          replacing={byType(dialog) !== undefined}
          onClose={() => setDialog(null)}
          onVerified={verified}
        />
      )}

      <Dialog open={removing !== null} onClose={() => setRemoving(null)}>
        <DialogTitle>{t('contacts.confirmRemove.title')}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {removing && t('contacts.confirmRemove.text', { value: byType(removing)?.value ?? '' })}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRemoving(null)}>{t('contacts.cancel')}</Button>
          <Button color="error" onClick={() => void confirmRemove()} disabled={busy}>
            {t('contacts.confirmRemove.confirm')}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={notice !== null}
        autoHideDuration={4000}
        onClose={() => setNotice(null)}
        message={notice}
      />
    </Card>
  );
}
