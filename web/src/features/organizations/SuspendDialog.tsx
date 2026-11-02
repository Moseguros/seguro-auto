import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { suspendOrganization, type OrganizationResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';

const maxReason = 500;

/** Suspender a organização, com o motivo obrigatório. */
export function SuspendDialog({
  organization,
  onClose,
  onSuspended,
}: {
  organization: OrganizationResponse;
  onClose(): void;
  onSuspended(organization: OrganizationResponse): void;
}) {
  const { t } = useTranslation();
  const [reason, setReason] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const value = reason.trim();
    const code = !value ? 'REQUIRED' : value.length > maxReason ? 'SIZE' : null;
    setError(code);
    if (code) return;
    setSaving(true);
    try {
      onSuspended(await suspendOrganization(organization.id, { reason: value }));
    } catch (e) {
      setMessage(problemMessage(t, getProblem(e)));
      setSaving(false);
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="suspend-title">
      <DialogTitle id="suspend-title">{t('organizations.suspendDialog.title')}</DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ mb: 2 }}>
          {t('organizations.suspendDialog.text')}
        </DialogContentText>
        <form id="suspend-form" onSubmit={submit} noValidate>
          <TextField
            label={t('organizations.suspendDialog.reason')}
            name="reason"
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            error={!!error}
            helperText={error ? fieldMessage(t, error) : undefined}
            required
            multiline
            minRows={3}
            fullWidth
            slotProps={{ htmlInput: { maxLength: maxReason } }}
          />
        </form>
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('organizations.suspendDialog.cancel')}</Button>
        <Button
          type="submit"
          form="suspend-form"
          color="error"
          variant="contained"
          disabled={saving}
        >
          {t('organizations.suspendDialog.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
