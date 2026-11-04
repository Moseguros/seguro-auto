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
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { InfoRequestItem, requestProposalInfo } from '../../api/generated';
import { fieldMessage } from '../../errors/problem';
import type { DecisionDialogProps } from './AcceptDialog';
import { counterText, lengthOk } from './common';
import { handleDecisionError } from './decisionSupport';

const items = Object.values(InfoRequestItem);
const min = 10;
const max = 1000;

/** Pedido de informação: um ou mais itens e a mensagem (10–1000). */
export function RequestInfoDialog({ proposal, onClose, onOutcome }: DecisionDialogProps) {
  const { t } = useTranslation();
  const [selected, setSelected] = useState<string[]>([]);
  const [text, setText] = useState('');
  const [touched, setTouched] = useState(false);
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [serverErrors, setServerErrors] = useState<Record<string, string>>({});

  const itemsError = touched && selected.length === 0;
  const textError = touched && !lengthOk(text, min, max);

  const toggle = (item: string, checked: boolean) =>
    setSelected((current) =>
      checked
        ? items.filter((i) => i === item || current.includes(i))
        : current.filter((i) => i !== item),
    );

  const submit = async () => {
    setTouched(true);
    if (selected.length === 0 || !lengthOk(text, min, max)) return;
    setSaving(true);
    setMessage(null);
    setServerErrors({});
    try {
      await requestProposalInfo(proposal.id, {
        items: selected,
        message: text.trim(),
        version: proposal.version,
      });
      onOutcome({ kind: 'done', notice: t('insurerProposals.requestInfo.done') });
    } catch (error) {
      setSaving(false);
      const handled = handleDecisionError(t, error, onOutcome);
      if (!handled) return;
      const fields: Record<string, string> = {};
      for (const e of handled.problem.errors) {
        fields[e.field.startsWith('items') ? 'items' : e.field] = fieldMessage(t, e.code);
      }
      setServerErrors(fields);
      if (Object.keys(fields).length === 0) setMessage(handled.message);
    }
  };

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="request-info-title">
      <DialogTitle id="request-info-title">{t('insurerProposals.requestInfo.title')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ pt: 1 }}>
          <FormControl component="fieldset" error={itemsError || !!serverErrors.items} required>
            <FormLabel component="legend">{t('insurerProposals.requestInfo.items')}</FormLabel>
            <FormGroup>
              {items.map((item) => (
                <FormControlLabel
                  key={item}
                  control={
                    <Checkbox
                      checked={selected.includes(item)}
                      onChange={(event) => toggle(item, event.target.checked)}
                    />
                  }
                  label={t(`infoRequestItem.${item}`)}
                />
              ))}
            </FormGroup>
            {(itemsError || serverErrors.items) && (
              <FormHelperText>
                {serverErrors.items ?? t('insurerProposals.requestInfo.itemsRequired')}
              </FormHelperText>
            )}
          </FormControl>
          <TextField
            required
            label={t('insurerProposals.requestInfo.message')}
            value={text}
            onChange={(event) => setText(event.target.value)}
            multiline
            minRows={3}
            error={textError || !!serverErrors.message}
            helperText={
              serverErrors.message ??
              `${textError ? `${t('insurerProposals.textLength', { min, max })} ` : ''}${counterText(text, max)}`
            }
          />
          <Alert severity="warning">{t('insurerProposals.requestInfo.warning')}</Alert>
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
          {t('insurerProposals.requestInfo.confirm')}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
