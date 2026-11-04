import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { approveInspection, type InspectionResponse } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { counterText } from '../insurer-proposals/common';

const notesMax = 1000;

export interface InspectionDialogProps {
  inspection: InspectionResponse;
  onClose(): void;
  /** Gravado (recarregar o detalhe). */
  onDone(): void;
  /** 409: a vistoria ou a proposta mudou entretanto (recarregar e mostrar a mensagem). */
  onConflict(message: string): void;
}

/** Aprovação: "feita presencialmente" (`mode = ON_SITE`) exige notas. */
export function ApproveInspectionDialog({
  inspection,
  onClose,
  onDone,
  onConflict,
}: InspectionDialogProps) {
  const { t } = useTranslation();
  const [onSite, setOnSite] = useState(inspection.mode === 'ON_SITE');
  const [notes, setNotes] = useState('');
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const trimmed = notes.trim();
  const notesMissing = onSite && trimmed.length === 0;
  const tooLong = trimmed.length > notesMax;
  const notesError = (touched && notesMissing) || tooLong;

  const submit = async () => {
    setTouched(true);
    if (notesMissing || tooLong) return;
    setSaving(true);
    setMessage(null);
    try {
      await approveInspection(inspection.id as string, {
        mode: onSite ? 'ON_SITE' : undefined,
        notes: trimmed || undefined,
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
      aria-labelledby="approve-inspection-title"
    >
      <DialogTitle id="approve-inspection-title">
        {t('insurerInspections.approve.title')}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <FormControlLabel
            control={
              <Switch checked={onSite} onChange={(event) => setOnSite(event.target.checked)} />
            }
            label={t('insurerInspections.approve.onSite')}
          />
          <TextField
            label={t('insurerInspections.approve.notes')}
            required={onSite}
            value={notes}
            onChange={(event) => setNotes(event.target.value)}
            multiline
            minRows={3}
            error={notesError}
            helperText={
              notesError && notesMissing
                ? t('insurerInspections.approve.notesRequired')
                : counterText(notes, notesMax)
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
        <Button variant="contained" onClick={() => void submit()} disabled={saving}>
          {t('insurerInspections.approve.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
