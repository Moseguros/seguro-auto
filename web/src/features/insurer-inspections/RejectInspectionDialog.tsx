import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormLabel from '@mui/material/FormLabel';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { rejectInspection, type PhotoSlot } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { counterText, lengthOk } from '../insurer-proposals/common';
import type { InspectionDialogProps } from './ApproveInspectionDialog';
import { slotOrder } from './PhotoGallery';

const min = 10;
const max = 1000;

/** Recusa da vistoria: motivo (10–1000) e os ângulos a repetir. */
export function RejectInspectionDialog({
  inspection,
  onClose,
  onDone,
  onConflict,
}: InspectionDialogProps) {
  const { t } = useTranslation();
  const [reason, setReason] = useState('');
  const [slots, setSlots] = useState<PhotoSlot[]>([]);
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const reasonError = touched && !lengthOk(reason, min, max);

  const toggle = (slot: PhotoSlot, checked: boolean) =>
    setSlots((current) =>
      checked
        ? slotOrder.filter((s) => s === slot || current.includes(s))
        : current.filter((s) => s !== slot),
    );

  const submit = async () => {
    setTouched(true);
    if (!lengthOk(reason, min, max)) return;
    setSaving(true);
    setMessage(null);
    try {
      await rejectInspection(inspection.id as string, {
        reason: reason.trim(),
        retakeSlots: slots,
        version: inspection.version as number,
      });
      onDone();
    } catch (error) {
      setSaving(false);
      const problem = getProblem(error);
      if (problem.status === 409) onConflict(problemMessage(t, problem));
      else setMessage(problemMessage(t, problem));
    }
  };

  return (
    <Dialog
      open
      onClose={onClose}
      fullWidth
      maxWidth="sm"
      aria-labelledby="reject-inspection-title"
    >
      <DialogTitle id="reject-inspection-title">{t('insurerInspections.reject.title')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <TextField
            required
            label={t('insurerInspections.reject.reason')}
            value={reason}
            onChange={(event) => setReason(event.target.value)}
            multiline
            minRows={3}
            error={reasonError}
            helperText={`${reasonError ? `${t('insurerProposals.textLength', { min, max })} ` : ''}${counterText(reason, max)}`}
          />
          <FormControl component="fieldset">
            <FormLabel component="legend">{t('insurerInspections.reject.retakeSlots')}</FormLabel>
            <FormGroup row>
              {slotOrder.map((slot) => (
                <FormControlLabel
                  key={slot}
                  control={
                    <Checkbox
                      checked={slots.includes(slot)}
                      onChange={(event) => toggle(slot, event.target.checked)}
                    />
                  }
                  label={t(`inspection.slot.${slot}`)}
                />
              ))}
            </FormGroup>
          </FormControl>
          <Alert severity="info">{t('insurerInspections.reject.note')}</Alert>
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
          {t('insurerInspections.reject.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
