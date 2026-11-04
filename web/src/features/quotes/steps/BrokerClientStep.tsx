import InputAdornment from '@mui/material/InputAdornment';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../../errors/problem';
import { msisdnPrefix } from '../../customer-auth/otp';
import { clientSchema, fieldErrors } from '../quoteRules';
import { useStepValidity, type StepProps } from './stepTypes';

/** Passo "Cliente" do mediador: o telemóvel do cliente. */
export function BrokerClientStep({ draft, update, onValidity, serverErrors }: StepProps) {
  const { t } = useTranslation();
  const [touched, setTouched] = useState(false);
  const errors = fieldErrors(clientSchema.safeParse({ clientMsisdn: draft.clientMsisdn }));
  useStepValidity(!errors.clientMsisdn, onValidity);
  const code =
    serverErrors.clientMsisdn ??
    (touched || draft.clientMsisdn.length >= 9 ? errors.clientMsisdn : undefined);

  return (
    <Stack spacing={2}>
      <TextField
        label={t('quoteWizard.client.msisdn')}
        value={draft.clientMsisdn}
        onChange={(event) =>
          update({ clientMsisdn: event.target.value.replace(/\D/g, '').slice(0, 9) })
        }
        onBlur={() => setTouched(true)}
        error={!!code}
        helperText={code ? fieldMessage(t, code) : undefined}
        fullWidth
        autoComplete="off"
        slotProps={{
          htmlInput: { inputMode: 'numeric', maxLength: 9 },
          input: {
            startAdornment: <InputAdornment position="start">{msisdnPrefix}</InputAdornment>,
          },
        }}
      />
      <Typography color="text.secondary">{t('quoteWizard.client.help')}</Typography>
    </Stack>
  );
}
