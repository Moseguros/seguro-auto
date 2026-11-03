import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import FormControlLabel from '@mui/material/FormControlLabel';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useId, useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  listPlatformSettings,
  updatePlatformSetting,
  type PlatformSettingResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { MoneyField } from '../../components/MoneyField';
import { getProblem, problemMessage } from '../../errors/problem';
import { mzn } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { fieldText } from '../organizations/OrganizationForm';
import { referencesQueryKey } from './referenceDomains';

const queryKey = [...referencesQueryKey, 'platformSettings'] as const;

/** Separador "Parâmetros": um cartão por parâmetro. */
export function PlatformSettingsTab() {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey,
    queryFn: ({ signal }) => listPlatformSettings(undefined, signal),
  });
  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  }
  return (
    <Stack spacing={2}>
      {query.data.settings.map((setting) => (
        <SettingCard key={`${setting.key}:${setting.version}`} setting={setting} />
      ))}
    </Stack>
  );
}

function SettingCard({ setting }: { setting: PlatformSettingResponse }) {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const titleId = useId();
  const [value, setValue] = useState(setting.value);
  const [valid, setValid] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const label = t(`platformSetting.${setting.key}`, { defaultValue: setting.key });

  const save = async () => {
    setError(null);
    setMessage(null);
    if (!valid || value.trim() === '') {
      setError(valid ? 'REQUIRED' : 'INVALID_VALUE');
      return;
    }
    setSaving(true);
    try {
      await updatePlatformSetting(setting.key, { value, version: setting.version });
      setSaved(true);
      await queryClient.invalidateQueries({
        queryKey: [...referencesQueryKey, 'platformSettings'],
      });
      void queryClient.invalidateQueries({ queryKey: ['platformSettings'] });
    } catch (e) {
      const problem = getProblem(e);
      const fieldError = problem.errors[0];
      if (fieldError) setError(fieldError.code);
      else setMessage(problemMessage(t, problem));
      if (problem.code === 'CONCURRENT_MODIFICATION') {
        void queryClient.invalidateQueries({
          queryKey: [...referencesQueryKey, 'platformSettings'],
        });
      }
    } finally {
      setSaving(false);
    }
  };

  const helper = error ? fieldText(t, error) : undefined;

  const editor = () => {
    switch (setting.valueType) {
      case 'MONEY':
        return (
          <MoneyField
            label={t('references.settings.value')}
            value={/^\d+$/.test(value) ? mzn(Number(value)) : null}
            onChange={(money) => setValue(money ? String(money.amount) : '')}
            onValidityChange={setValid}
            error={!!error}
            helperText={helper}
          />
        );
      case 'BOOLEAN':
        return (
          <FormControlLabel
            label={t('references.settings.value')}
            control={
              <Switch
                inputProps={{ role: 'switch' }}
                checked={value === 'true'}
                onChange={(event) => setValue(String(event.target.checked))}
              />
            }
          />
        );
      case 'INTEGER':
        return (
          <TextField
            label={t('references.settings.value')}
            value={value}
            onChange={(event) => setValue(event.target.value.trim())}
            error={!!error}
            helperText={helper}
            fullWidth
            slotProps={{ htmlInput: { inputMode: 'numeric' } }}
          />
        );
      default:
        return (
          <TextField
            label={t('references.settings.value')}
            value={value}
            onChange={(event) => setValue(event.target.value)}
            error={!!error}
            helperText={helper}
            fullWidth
          />
        );
    }
  };

  return (
    <Card variant="outlined" elevation={0} component="section" aria-labelledby={titleId}>
      <CardContent>
        <Stack spacing={2}>
          <Typography id={titleId} variant="h6" component="h3">
            {label}
          </Typography>
          {setting.key === 'MTPL_MIN_CAPITAL' && (
            <Typography variant="body2" color="text.secondary">
              {t('references.settings.mtplNote')}
            </Typography>
          )}
          {editor()}
          <Typography variant="body2" color="text.secondary">
            {t('references.settings.updated', {
              date: formatDateTime(setting.updatedAt, i18n.language),
              by: setting.updatedBy,
            })}
          </Typography>
          {message && (
            <Alert severity="error" role="alert">
              {message}
            </Alert>
          )}
          <div>
            <Button variant="contained" onClick={() => void save()} disabled={saving}>
              {t('common.save')}
            </Button>
          </div>
        </Stack>
      </CardContent>
      <Snackbar
        open={saved}
        autoHideDuration={3000}
        onClose={() => setSaved(false)}
        message={t('references.saved')}
      />
    </Card>
  );
}
