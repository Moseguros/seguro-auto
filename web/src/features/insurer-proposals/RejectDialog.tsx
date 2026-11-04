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
import { rejectProposal, RejectProposalRequestReasonCode } from '../../api/generated';
import { fieldMessage } from '../../errors/problem';
import type { DecisionDialogProps } from './AcceptDialog';
import { counterText, lengthOk } from './common';
import { handleDecisionError } from './decisionSupport';

const reasonCodes = Object.values(RejectProposalRequestReasonCode);
const min = 10;
const max = 1000;

/** Recusa: motivo e texto obrigatórios (10–1000), com o aviso de que não se pode desfazer. */
export function RejectDialog({ proposal, onClose, onOutcome }: DecisionDialogProps) {
  const { t } = useTranslation();
  const [reasonCode, setReasonCode] = useState('');
  const [reason, setReason] = useState('');
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});

  const codeError = touched && !reasonCode;
  const reasonError = touched && !lengthOk(reason, min, max);

  const submit = async () => {
    setTouched(true);
    if (!reasonCode || !lengthOk(reason, min, max)) return;
    setSaving(true);
    setMessage(null);
    setServerErrors({});
    try {
      await rejectProposal(proposal.id, {
        reasonCode: reasonCode as RejectProposalRequestReasonCode,
        reason: reason.trim(),
        version: proposal.version,
      });
      onOutcome({ kind: 'done', notice: t('insurerProposals.reject.done') });
    } catch (error) {
      setSaving(false);
      const handled = handleDecisionError(t, error, onOutcome);
      if (!handled) return;
      const fields = Object.fromEntries(
        handled.problem.errors.map((e) => [e.field, fieldMessage(t, e.code)]),
      );
      setServerErrors(fields);
      if (Object.keys(fields).length === 0) setMessage(handled.message);
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="reject-title">
      <DialogTitle id="reject-title">
        {t('insurerProposals.reject.title', { number: proposal.number })}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <TextField
            select
            required
            id="reject-reason-code"
            label={t('insurerProposals.reject.reasonCode')}
            value={reasonCode}
            onChange={(event) => setReasonCode(event.target.value)}
            error={codeError || !!serverErrors.reasonCode}
            helperText={
              serverErrors.reasonCode ?? (codeError ? t('insurerProposals.required') : ' ')
            }
          >
            {reasonCodes.map((code) => (
              <MenuItem key={code} value={code}>
                {t(`rejectionReason.${code}`)}
              </MenuItem>
            ))}
          </TextField>
          <TextField
            required
            label={t('insurerProposals.reject.reason')}
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
          <Alert severity="warning">{t('insurerProposals.reject.warning')}</Alert>
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
          {t('insurerProposals.reject.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
