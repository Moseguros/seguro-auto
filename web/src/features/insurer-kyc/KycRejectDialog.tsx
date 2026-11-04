import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import MenuItem from '@mui/material/MenuItem';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  KycRejectionReason,
  rejectKycCheck,
  type InsurerKycCheckResponse,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { counterText, lengthOk } from '../insurer-proposals/common';

const reasonCodes = Object.values(KycRejectionReason);
const min = 10;
const max = 500;

export interface KycRejectDialogProps {
  check: InsurerKycCheckResponse;
  onClose(): void;
  onDone(): void;
  onConflict(message: string): void;
}

/** Recusa do KYC: motivo e texto (obrigatório com `OTHER`; 10–500). */
export function KycRejectDialog({ check, onClose, onDone, onConflict }: KycRejectDialogProps) {
  const { t } = useTranslation();
  const [reasonCode, setReasonCode] = useState('');
  const [reason, setReason] = useState('');
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});

  const textRequired = reasonCode === 'OTHER';
  const textValid = reason.trim() === '' ? !textRequired : lengthOk(reason, min, max);
  const codeError = touched && !reasonCode;
  const reasonError = touched && !textValid;

  const submit = async () => {
    setTouched(true);
    if (!reasonCode || !textValid) return;
    setSaving(true);
    setMessage(null);
    setServerErrors({});
    try {
      await rejectKycCheck(check.id, {
        reasonCode: reasonCode as KycRejectionReason,
        reason: reason.trim() || undefined,
        version: check.version,
      });
      onDone();
    } catch (error) {
      setSaving(false);
      const problem = getProblem(error);
      if (problem.status === 409) {
        onConflict(problemMessage(t, problem));
        return;
      }
      const fields = Object.fromEntries(
        problem.errors.map((e) => [e.field, fieldMessage(t, e.code)]),
      );
      setServerErrors(fields);
      if (Object.keys(fields).length === 0) setMessage(problemMessage(t, problem));
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="kyc-reject-title">
      <DialogTitle id="kyc-reject-title">{t('insurerKyc.rejectDialog.title')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <TextField
            select
            required
            id="kyc-reject-reason-code"
            label={t('insurerKyc.rejectDialog.reasonCode')}
            value={reasonCode}
            onChange={(event) => setReasonCode(event.target.value)}
            error={codeError || !!serverErrors.reasonCode}
            helperText={
              serverErrors.reasonCode ?? (codeError ? t('insurerProposals.required') : ' ')
            }
          >
            {reasonCodes.map((code) => (
              <MenuItem key={code} value={code}>
                {t(`kyc.rejectionReason.${code}`)}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            required={textRequired}
            label={
              textRequired
                ? t('insurerKyc.rejectDialog.reason')
                : t('insurerKyc.rejectDialog.reasonOptional')
            }
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            multiline
            minRows={3}
            error={reasonError || !!serverErrors.reason}
            helperText={
              serverErrors.reason ??
              `${reasonError ? `${t('insurerProposals.textLength', { min, max })} ` : ''}${counterText(reason, max)}`
            }
          />
          {message && (
            <Alert severity="error" role="alert">
              {message}
            </Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common.cancel')}</Button>
        <Button variant="contained" color="error" onClick={() => void submit()} disabled={saving}>
          {t('insurerKyc.rejectDialog.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
