import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { acceptProposal, type InsurerProposalResponse } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { counterText, Field } from './common';
import { handleDecisionError, type DecisionOutcome } from './decisionSupport';

const noteMax = 1000;

export interface DecisionDialogProps {
  proposal: InsurerProposalResponse;
  onClose(): void;
  onOutcome(outcome: DecisionOutcome): void;
}

/** Aceitação manual: resumo (total, KYC, vistoria) e nota opcional. */
export function AcceptDialog({ proposal, onClose, onOutcome }: DecisionDialogProps) {
  const { t, i18n } = useTranslation();
  const [note, setNote] = useState('');
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const tooLong = note.trim().length > noteMax;

  const submit = async () => {
    if (tooLong) return;
    setSaving(true);
    setMessage(null);
    try {
      await acceptProposal(proposal.id, {
        note: note.trim() || undefined,
        version: proposal.version,
      });
      onOutcome({ kind: 'done', notice: t('insurerProposals.accept.done') });
    } catch (error) {
      setSaving(false);
      const handled = handleDecisionError(t, error, onOutcome);
      if (handled) setMessage(handled.message);
    }
  };

  const inspection = proposal.inspection;
  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="accept-title">
      <DialogTitle id="accept-title">
        {t('insurerProposals.accept.title', { number: proposal.number })}
      </DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <Box
            component="dl"
            sx={{
              m: 0,
              display: 'grid',
              gap: 1.5,
              gridTemplateColumns: 'repeat(3, minmax(0, 1fr))',
            }}
          >
            <Field label={t('insurerProposals.accept.total')}>
              {formatMoney(proposal.premium.total, i18n.language)}
            </Field>
            <Field label={t('insurerProposals.accept.kyc')}>
              {t(`kyc.state.${proposal.kyc.state}`)}
            </Field>
            <Field label={t('insurerProposals.accept.inspection')}>
              {inspection.required && inspection.status
                ? t(`inspection.status.${inspection.status}`)
                : t('insurerProposals.noInspection')}
            </Field>
          </Box>
          <TextField
            label={t('insurerProposals.accept.note')}
            value={note}
            onChange={(event) => setNote(event.target.value)}
            multiline
            minRows={3}
            error={tooLong}
            helperText={counterText(note, noteMax)}
            slotProps={{ htmlInput: { maxLength: noteMax + 50 } }}
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
        <Button variant="contained" onClick={() => void submit()} disabled={saving || tooLong}>
          {t('insurerProposals.accept.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
