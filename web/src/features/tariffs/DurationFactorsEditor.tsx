import Alert from '@mui/material/Alert';
import FormControlLabel from '@mui/material/FormControlLabel';
import InputAdornment from '@mui/material/InputAdornment';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { alpha, useTheme } from '@mui/material/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { fieldMessage } from '../../errors/problem';
import { formatDecimal, normalizeDecimal } from './decimal';
import { allowedDurations, type DurationFactor } from './ratingTypes';

export interface DurationFactorsEditorProps {
  value: DurationFactor[];
  readOnly: boolean;
  onChange(value: DurationFactor[]): void;
  /** Erros por número de dias (código `validation.*`). */
  errors?: ReadonlyMap<number, string>;
  /** Número de dias da linha a destacar. */
  highlightDays?: number;
}

/** Secção "Duração" dos produtos temporários: factor de cada duração disponível. */
export function DurationFactorsEditor({
  value,
  readOnly,
  onChange,
  errors,
  highlightDays,
}: DurationFactorsEditorProps) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const [texts, setTexts] = useState<Record<number, string>>(() =>
    Object.fromEntries(value.map((d) => [d.days, formatDecimal(d.factor, i18n.language)])),
  );

  const entry = (days: number) => value.find((d) => d.days === days);
  const sorted = (list: DurationFactor[]) => [...list].sort((a, b) => a.days - b.days);

  const toggle = (days: number, on: boolean) => {
    if (!on) {
      onChange(value.filter((d) => d.days !== days));
      return;
    }
    const text = days === 365 ? '1' : (texts[days] ?? '');
    onChange(sorted([...value, { days, factor: normalizeDecimal(text) ?? text }]));
  };

  const write = (days: number, text: string) => {
    setTexts((current) => ({ ...current, [days]: text }));
    onChange(
      value.map((d) => (d.days === days ? { days, factor: normalizeDecimal(text) ?? text } : d)),
    );
  };

  return (
    <Paper
      variant="outlined"
      sx={{ p: 2 }}
      component="section"
      aria-label={t('tariffs.duration.title')}
    >
      <Stack spacing={1.5}>
        <Typography variant="h6" component="h2">
          {t('tariffs.duration.title')}
        </Typography>
        <Typography variant="body2" color="text.secondary">
          {t('tariffs.duration.help')}
        </Typography>
        {value.length === 0 && (
          <Alert severity="error">{fieldMessage(t, 'DURATION_FACTORS_REQUIRED')}</Alert>
        )}
        {allowedDurations.map((days) => {
          const current = entry(days);
          const error = errors?.get(days);
          const fixed = days === 365;
          const text = fixed
            ? '1'
            : (texts[days] ?? (current ? formatDecimal(current.factor, i18n.language) : ''));
          return (
            <Stack
              key={days}
              direction={{ xs: 'column', sm: 'row' }}
              spacing={1}
              alignItems={{ sm: 'center' }}
              data-testid={`duration-${days}`}
              data-highlighted={highlightDays === days ? 'true' : undefined}
              sx={{
                p: 0.5,
                borderRadius: 1,
                ...(highlightDays === days && { bgcolor: alpha(theme.palette.warning.main, 0.18) }),
              }}
            >
              <FormControlLabel
                sx={{ minWidth: 200 }}
                control={
                  <Switch
                    checked={!!current}
                    disabled={readOnly}
                    onChange={(event) => toggle(days, event.target.checked)}
                  />
                }
                label={t('tariffs.duration.available', { days })}
              />
              <TextField
                size="small"
                label={t('tariffs.duration.factor', { days })}
                value={text}
                disabled={readOnly || fixed || !current}
                onChange={(event) => write(days, event.target.value)}
                error={!!error}
                helperText={error ? fieldMessage(t, error) : undefined}
                slotProps={{
                  htmlInput: { inputMode: 'decimal' },
                  input: { startAdornment: <InputAdornment position="start">×</InputAdornment> },
                }}
                sx={{ maxWidth: { sm: 220 } }}
              />
            </Stack>
          );
        })}
      </Stack>
    </Paper>
  );
}
