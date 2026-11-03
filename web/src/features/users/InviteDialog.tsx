import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useTheme } from '@mui/material/styles';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { emailPattern } from '../organizations/organizationSchema';
import { RoleCheckboxes } from './RoleCheckboxes';
import type { InviteValues } from './usersApi';

type Field = 'fullName' | 'email' | 'roleCodes';

/** Erros de topo que pertencem ao campo do e-mail. */
const emailCodes = ['EMAIL_ALREADY_IN_USE', 'INVALID_EMAIL'];

export interface InviteDialogProps {
  roles: string[];
  initial?: InviteValues;
  /** Erro de uma tentativa anterior (ex.: na criação da organização). */
  initialErrorCode?: string;
  onInvite(values: InviteValues): Promise<void>;
  onClose(): void;
}

/** Convidar utilizador: nome, e-mail e perfis. */
export function InviteDialog({
  roles,
  initial,
  initialErrorCode,
  onInvite,
  onClose,
}: InviteDialogProps) {
  const { t } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [values, setValues] = useState<InviteValues>(
    initial ?? { fullName: '', email: '', roleCodes: [] },
  );
  const [errors, setErrors] = useState<Partial<Record<Field, string>>>(
    initialErrorCode && emailCodes.includes(initialErrorCode) ? { email: initialErrorCode } : {},
  );
  const [message, setMessage] = useState<string | null>(
    initialErrorCode ? problemMessage(t, { status: 0, code: initialErrorCode, errors: [] }) : null,
  );
  const [sending, setSending] = useState(false);

  const text = (code: string | undefined) => {
    if (!code) return undefined;
    return t(`validation.${code}`, { defaultValue: '' })
      ? fieldMessage(t, code)
      : problemMessage(t, { status: 0, code, errors: [] });
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage(null);
    const fullName = values.fullName.trim().replace(/\s+/g, ' ');
    const email = values.email.trim();
    const found: Partial<Record<Field, string>> = {};
    if (!fullName) found.fullName = 'REQUIRED';
    else if (fullName.length < 2 || fullName.length > 120) found.fullName = 'SIZE';
    if (!email) found.email = 'REQUIRED';
    else if (!emailPattern.test(email)) found.email = 'INVALID_EMAIL';
    if (values.roleCodes.length === 0) found.roleCodes = 'REQUIRED';
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    setSending(true);
    try {
      await onInvite({ fullName, email, roleCodes: values.roleCodes });
    } catch (error) {
      const problem = getProblem(error);
      const serverErrors: Partial<Record<Field, string>> = {};
      for (const fieldError of problem.errors) {
        if (['fullName', 'email', 'roleCodes'].includes(fieldError.field)) {
          serverErrors[fieldError.field as Field] = fieldError.code;
        }
      }
      if (emailCodes.includes(problem.code)) serverErrors.email = problem.code;
      setErrors(serverErrors);
      if (Object.keys(serverErrors).length === 0 || problem.code !== 'VALIDATION_ERROR') {
        setMessage(problemMessage(t, problem));
      }
      setSending(false);
    }
  };

  return (
    <Dialog
      open
      onClose={onClose}
      fullScreen={fullScreen}
      fullWidth
      maxWidth="sm"
      aria-labelledby="invite-dialog-title"
    >
      <DialogTitle id="invite-dialog-title">{t('users.inviteDialog.title')}</DialogTitle>
      <DialogContent sx={{ pt: '8px !important' }}>
        <Stack component="form" id="invite-form" spacing={2} onSubmit={submit} noValidate>
          <TextField
            label={t('users.inviteDialog.fullName')}
            name="fullName"
            value={values.fullName}
            onChange={(event) => setValues({ ...values, fullName: event.target.value })}
            error={!!errors.fullName}
            helperText={text(errors.fullName)}
            autoComplete="off"
            fullWidth
          />
          <TextField
            label={t('users.inviteDialog.email')}
            name="email"
            type="email"
            value={values.email}
            onChange={(event) => setValues({ ...values, email: event.target.value })}
            error={!!errors.email}
            helperText={text(errors.email)}
            autoComplete="off"
            fullWidth
          />
          <RoleCheckboxes
            roles={roles}
            value={values.roleCodes}
            onChange={(roleCodes) => setValues({ ...values, roleCodes })}
            errorCode={errors.roleCodes}
          />
          {message && (
            <Alert severity="error" role="alert">
              {message}
            </Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('users.inviteDialog.cancel')}</Button>
        <Button type="submit" form="invite-form" variant="contained" disabled={sending}>
          {t('users.inviteDialog.send')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
