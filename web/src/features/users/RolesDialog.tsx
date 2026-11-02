import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { updateUserRoles, type StaffUserResponse } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { RoleCheckboxes } from './RoleCheckboxes';

export interface RolesDialogProps {
  user: StaffUserResponse;
  /** Perfis atribuíveis (`listAssignableRoles`). */
  roles: string[];
  onClose(): void;
  onSaved(user: StaffUserResponse): void;
}

/** Alterar perfis: caixas com os perfis atribuíveis; envia a `version` lida. */
export function RolesDialog({ user, roles, onClose, onSaved }: RolesDialogProps) {
  const { t } = useTranslation();
  const [value, setValue] = useState<string[]>(user.roles.filter((r) => roles.includes(r)));
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const name = user.displayName || user.email;

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    setError(value.length === 0 ? 'REQUIRED' : null);
    if (value.length === 0) return;
    setSaving(true);
    try {
      onSaved(await updateUserRoles(user.id, { roleCodes: value, version: user.version }));
    } catch (e) {
      setMessage(problemMessage(t, getProblem(e)));
      setSaving(false);
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="roles-dialog-title">
      <DialogTitle id="roles-dialog-title">{t('users.rolesDialog.title', { name })}</DialogTitle>
      <DialogContent>
        <form id="roles-form" onSubmit={submit} noValidate>
          <RoleCheckboxes roles={roles} value={value} onChange={setValue} errorCode={error} />
        </form>
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('users.rolesDialog.cancel')}</Button>
        <Button type="submit" form="roles-form" variant="contained" disabled={saving}>
          {t('users.rolesDialog.save')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
