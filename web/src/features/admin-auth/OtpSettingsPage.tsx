import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import FormControl from '@mui/material/FormControl';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import FormHelperText from '@mui/material/FormHelperText';
import FormLabel from '@mui/material/FormLabel';
import Grid from '@mui/material/Grid2';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { getOtpSettings, updateOtpSettings, type OtpSettingsResponse } from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { LoadingScreen } from '../../auth/RequireAuth';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { FallbackOrderList } from './FallbackOrderList';
import {
  formFieldOf,
  formFrom,
  numberFields,
  otpChannels,
  ranges,
  toggleChannel,
  toRequest,
  validate,
  type FormErrors,
  type NumberField,
  type OtpChannel,
  type OtpSettingsForm,
} from './otpSettingsForm';
import { TestDeliveryDialog } from './TestDeliveryDialog';

export const otpSettingsQueryKey = ['otpSettings'] as const;

/** `/admin/autenticacao`: canais e parâmetros do código de entrada dos clientes. */
export function OtpSettingsPage() {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: otpSettingsQueryKey,
    queryFn: ({ signal }) => getOtpSettings(undefined, signal),
  });

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  } else {
    content = <OtpSettingsEditor settings={query.data} onReload={() => void query.refetch()} />;
  }

  return (
    <Stack spacing={3} sx={{ maxWidth: 720 }}>
      <div>
        <Typography variant="h4" component="h1">
          {t('otpSettings.title')}
        </Typography>
        <Typography color="text.secondary">{t('otpSettings.subtitle')}</Typography>
      </div>
      {content}
    </Stack>
  );
}

function OtpSettingsEditor({
  settings,
  onReload,
}: {
  settings: OtpSettingsResponse;
  onReload(): void;
}) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const canManage = usePermission(Permissions.otpSettingsManage);
  const readOnly = !canManage;
  const [form, setForm] = useState<OtpSettingsForm>(() => formFrom(settings));
  const [errors, setErrors] = useState<FormErrors>({});
  const [message, setMessage] = useState<string | null>(null);
  const [conflict, setConflict] = useState(false);
  const [saved, setSaved] = useState(false);
  const [saving, setSaving] = useState(false);
  const [testing, setTesting] = useState(false);

  // Configuração recarregada: volta aos valores do servidor.
  useEffect(() => {
    setForm(formFrom(settings));
    setErrors({});
    setConflict(false);
    setMessage(null);
  }, [settings]);

  const update = (next: OtpSettingsForm) => {
    setForm(next);
    setSaved(false);
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    if (readOnly) return;
    setMessage(null);
    setConflict(false);
    const found = validate(form);
    setErrors(found);
    if (Object.keys(found).length > 0) return;
    setSaving(true);
    try {
      const result = await updateOtpSettings(toRequest(form, settings.version));
      queryClient.setQueryData(otpSettingsQueryKey, result);
      setSaved(true);
    } catch (error) {
      const problem = getProblem(error);
      if (problem.code === 'CONCURRENT_MODIFICATION' || problem.status === 409) {
        setConflict(true);
        return;
      }
      const serverErrors: FormErrors = {};
      for (const fieldError of problem.errors) {
        const field = formFieldOf(fieldError.field);
        if (field && !serverErrors[field]) serverErrors[field] = fieldError.code;
      }
      if (Object.keys(serverErrors).length > 0) setErrors(serverErrors);
      else setMessage(problemMessage(t, problem));
    } finally {
      setSaving(false);
    }
  };

  const numberField = (field: NumberField) => {
    const [min, max] = ranges[field];
    const error = errors[field];
    return (
      <Grid key={field} size={{ xs: 12, sm: 6 }}>
        <TextField
          label={t(`otpSettings.${field}`)}
          name={field}
          type="number"
          value={form[field]}
          onChange={(event) => update({ ...form, [field]: event.target.value })}
          error={!!error}
          helperText={
            error
              ? `${fieldMessage(t, error)} ${t('otpSettings.range', { min, max })}`
              : t('otpSettings.range', { min, max })
          }
          disabled={readOnly}
          fullWidth
          slotProps={{ htmlInput: { min, max, step: 1, inputMode: 'numeric' } }}
        />
      </Grid>
    );
  };

  const channelError = errors.enabledChannels;
  const orderedEnabled = form.fallbackOrder.filter((c) => form.enabledChannels.includes(c));

  return (
    <Stack component="form" spacing={3} onSubmit={submit} noValidate>
      {readOnly && <Alert severity="info">{t('otpSettings.readOnly')}</Alert>}
      <Card variant="outlined">
        <CardContent>
          <Stack spacing={3}>
            <FormControl component="fieldset" error={!!channelError} disabled={readOnly}>
              <FormLabel component="legend">{t('otpSettings.channels')}</FormLabel>
              <FormGroup row>
                {otpChannels.map((channel) => (
                  <FormControlLabel
                    key={channel}
                    label={t(`channel.${channel}`)}
                    control={
                      <Switch
                        name={`channel-${channel}`}
                        checked={form.enabledChannels.includes(channel)}
                        onChange={(event) =>
                          update(toggleChannel(form, channel, event.target.checked))
                        }
                      />
                    }
                  />
                ))}
              </FormGroup>
              {channelError && (
                <FormHelperText role="alert">{fieldMessage(t, channelError)}</FormHelperText>
              )}
            </FormControl>

            <TextField
              select
              label={t('otpSettings.defaultChannel')}
              name="defaultChannel"
              value={form.defaultChannel}
              onChange={(event) =>
                update({ ...form, defaultChannel: event.target.value as OtpChannel | '' })
              }
              error={!!errors.defaultChannel}
              helperText={
                errors.defaultChannel ? fieldMessage(t, errors.defaultChannel) : undefined
              }
              disabled={readOnly}
              fullWidth
              slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
            >
              {form.defaultChannel === '' && <option value="" />}
              {orderedEnabled.map((channel) => (
                <option key={channel} value={channel}>
                  {t(`channel.${channel}`)}
                </option>
              ))}
            </TextField>

            <FormControlLabel
              disabled={readOnly}
              label={t('otpSettings.customerCanChoose')}
              control={
                <Switch
                  name="customerCanChoose"
                  checked={form.customerCanChoose}
                  onChange={(event) => update({ ...form, customerCanChoose: event.target.checked })}
                />
              }
            />

            <Stack spacing={1}>
              <Typography id="fallback-order-label" variant="subtitle1" component="h2">
                {t('otpSettings.fallback')}
              </Typography>
              {!readOnly && (
                <Typography variant="body2" color="text.secondary">
                  {t('otpSettings.fallbackHelp')}
                </Typography>
              )}
              <FallbackOrderList
                order={form.fallbackOrder}
                readOnly={readOnly}
                labelledBy="fallback-order-label"
                onChange={(fallbackOrder) => update({ ...form, fallbackOrder })}
              />
              {errors.fallbackOrder && (
                <Typography variant="body2" color="error" role="alert">
                  {fieldMessage(t, errors.fallbackOrder)}
                </Typography>
              )}
              <Alert severity="info" variant="outlined">
                {t('otpSettings.fallbackNote')}
              </Alert>
            </Stack>
          </Stack>
        </CardContent>
      </Card>

      <Card variant="outlined">
        <CardContent>
          <Typography variant="subtitle1" component="h2" sx={{ mb: 2 }}>
            {t('otpSettings.parameters')}
          </Typography>
          <Grid container spacing={2}>
            {numberFields.map(numberField)}
          </Grid>
        </CardContent>
      </Card>

      {conflict && (
        <Alert
          severity="warning"
          role="alert"
          action={
            <Button color="inherit" size="small" onClick={onReload}>
              {t('otpSettings.reload')}
            </Button>
          }
        >
          {t('otpSettings.conflict')}
        </Alert>
      )}
      {message && (
        <Alert severity="error" role="alert">
          {message}
        </Alert>
      )}

      {canManage && (
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
          <Button type="submit" variant="contained" size="large" disabled={saving}>
            {t('otpSettings.save')}
          </Button>
          <Button variant="outlined" size="large" onClick={() => setTesting(true)}>
            {t('otpSettings.test.open')}
          </Button>
        </Stack>
      )}

      {testing && (
        <TestDeliveryDialog
          enabledChannels={settings.enabledChannels}
          onClose={() => setTesting(false)}
        />
      )}
      <Snackbar
        open={saved}
        autoHideDuration={4000}
        onClose={() => setSaved(false)}
        message={t('otpSettings.saved')}
      />
    </Stack>
  );
}
