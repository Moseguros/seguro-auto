import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from '@mui/material/TextField';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { requestOnSiteInspection, type ProposalResponse } from '../../api/generated';
import { fieldMessage, getProblem, problemMessage, type Problem } from '../../errors/problem';
import { counterText, lengthOk } from '../insurer-proposals/common';

const NOTES_MIN = 10;
const NOTES_MAX = 500;

/** "Prefiro vistoria presencial": local e horas preferidas (10–500). */
export function OnSiteDialog({
  open,
  proposal,
  onClose,
  onSaved,
  onError,
}: {
  open: boolean;
  proposal: ProposalResponse;
  onClose(): void;
  onSaved(proposal: ProposalResponse): void;
  onError(problem: Problem): boolean;
}) {
  const { t } = useTranslation();
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'), { noSsr: true });
  const [notes, setNotes] = useState('');
  const [fieldError, setFieldError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function submit() {
    setMessage(null);
    if (!lengthOk(notes, NOTES_MIN, NOTES_MAX)) {
      setFieldError(
        notes.trim() ? (notes.trim().length < NOTES_MIN ? 'TOO_SHORT' : 'TOO_LONG') : 'REQUIRED',
      );
      return;
    }
    setBusy(true);
    try {
      const saved = await requestOnSiteInspection(proposal.id, {
        notes: notes.trim(),
        version: proposal.version,
      });
      onSaved(saved);
    } catch (error) {
      const problem = getProblem(error);
      if (onError(problem)) {
        onClose();
        return;
      }
      const notesError = problem.errors.find((e) => e.field === 'notes');
      if (notesError) setFieldError(notesError.code);
      else setMessage(problemMessage(t, problem));
    } finally {
      setBusy(false);
    }
  }

  return (
    <Dialog open={open} onClose={onClose} fullScreen={fullScreen} fullWidth maxWidth="sm">
      <DialogTitle>{t('proposal.inspection.onSiteTitle')}</DialogTitle>
      <DialogContent>
        <DialogContentText sx={{ mb: 2 }}>{t('proposal.inspection.onSiteText')}</DialogContentText>
        <TextField
          label={t('proposal.inspection.onSiteNotes')}
          value={notes}
          onChange={(event) => {
            setNotes(event.target.value);
            setFieldError(null);
          }}
          error={!!fieldError}
          helperText={
            fieldError
              ? `${fieldMessage(t, fieldError)} · ${counterText(notes, NOTES_MAX)}`
              : counterText(notes, NOTES_MAX)
          }
          multiline
          minRows={3}
          fullWidth
          autoFocus
        />
        {message && (
          <Alert severity="error" role="alert" sx={{ mt: 2 }}>
            {message}
          </Alert>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('common.cancel')}</Button>
        <Button variant="contained" disabled={busy} onClick={() => void submit()}>
          {t('proposal.inspection.onSiteSubmit')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
