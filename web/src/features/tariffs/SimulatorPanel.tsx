import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import Grid from '@mui/material/Grid2';
import MenuItem from '@mui/material/MenuItem';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import {
  simulateRating,
  type Money,
  type RatingCoverage,
  type RatingRulesResponse,
  type RatingSimulationResponse,
  type RequestedCoverageRequest,
  type SimulateRatingRequest,
  type SimulationVehicleRequest,
} from '../../api/generated';
import { MoneyField } from '../../components/MoneyField';
import { getProblem, problemMessage } from '../../errors/problem';
import { currentYearInMaputo, todayInMaputo } from '../../format/dates';
import { formatMoney } from '../../format/money';
import { BreakdownTable } from './BreakdownTable';
import type { TariffLocation } from './pointer';
import type { Rating, TariffError } from './ratingTypes';
import { ratingForServer } from './validation';
import { provinces, vehicleCategories, vehicleUsages } from './variables';

/** Data `AAAA-MM-DD` com `anos` anos a menos (29/02 passa a 28/02). */
export function yearsBefore(date: string, years: number): string {
  const [y, m, d] = date.split('-');
  const year = Number(y) - years;
  const day = m === '02' && d === '29' ? '28' : d;
  return `${String(year).padStart(4, '0')}-${m}-${day}`;
}

export interface SimulatorForm {
  category: string;
  usage: string;
  province: string;
  year: string;
  engineCc: string;
  seats: string;
  grossWeightKg: string;
  commercialValue: Money | null;
  hasTracker: boolean;
  birthDate: string;
  licenceIssueDate: string;
  coverages: string[];
  capitals: Record<string, number>;
  startDate: string;
  durationDays: number;
}

/** Valores iniciais: ligeiro particular em Maputo Cidade, 3 anos; condutor de 35 com carta de 10. */
export function initialSimulatorForm(now: Date = new Date()): SimulatorForm {
  const today = todayInMaputo(now);
  return {
    category: 'LIGHT_PASSENGER',
    usage: 'PRIVATE',
    province: 'MAPUTO_CITY',
    year: String(currentYearInMaputo(now) - 3),
    engineCc: '',
    seats: '',
    grossWeightKg: '',
    commercialValue: null,
    hasTracker: false,
    birthDate: yearsBefore(today, 35),
    licenceIssueDate: yearsBefore(today, 10),
    coverages: [],
    capitals: {},
    startDate: today,
    durationDays: 365,
  };
}

const optionalInt = (text: string) => (text.trim() === '' ? undefined : Number(text.trim()));

export interface SimulatorPanelProps {
  versionId: string;
  rating: Rating;
  coverages: RatingCoverage[];
  productType: RatingRulesResponse['productType'];
  /** Erros da tarifa devolvidos pelo servidor (rascunho inválido). */
  onTariffErrors(errors: TariffError[]): void;
  onGoToTable(location: TariffLocation): void;
}

/** Separador "Simulador": um risco, o rascunho actual da tarifa e a decomposição do prémio. */
export function SimulatorPanel({
  versionId,
  rating,
  coverages,
  productType,
  onTariffErrors,
  onGoToTable,
}: SimulatorPanelProps) {
  const { t, i18n } = useTranslation();
  const [form, setForm] = useState<SimulatorForm>(() => initialSimulatorForm());
  const [invalid, setInvalid] = useState<Record<string, boolean>>({});
  const [result, setResult] = useState<RatingSimulationResponse | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [running, setRunning] = useState(false);

  const temporary = productType === 'TEMPORARY';
  const durations = temporary
    ? (rating.durationFactors ?? []).map((d) => d.days).sort((a, b) => a - b)
    : [365];
  const durationDays = durations.includes(form.durationDays)
    ? form.durationDays
    : (durations[durations.length - 1] ?? 365);

  const set = <K extends keyof SimulatorForm>(key: K, value: SimulatorForm[K]) =>
    setForm((f) => ({ ...f, [key]: value }));

  const yearValid = /^\d{4}$/.test(form.year.trim());
  const intValid = (text: string) => text.trim() === '' || /^\d+$/.test(text.trim());
  const canRun =
    yearValid &&
    form.birthDate !== '' &&
    form.startDate !== '' &&
    intValid(form.engineCc) &&
    intValid(form.seats) &&
    intValid(form.grossWeightKg) &&
    !Object.values(invalid).some(Boolean);

  const run = async () => {
    setMessage(null);
    setRunning(true);
    try {
      const vehicle: SimulationVehicleRequest = {
        category: form.category as SimulationVehicleRequest['category'],
        usage: form.usage as SimulationVehicleRequest['usage'],
        provinceOfCirculation: form.province as SimulationVehicleRequest['provinceOfCirculation'],
        year: Number(form.year.trim()),
        engineCc: optionalInt(form.engineCc),
        seats: optionalInt(form.seats),
        grossWeightKg: optionalInt(form.grossWeightKg),
        commercialValue: form.commercialValue ?? undefined,
        hasTracker: form.hasTracker,
      };
      const requested: RequestedCoverageRequest[] = coverages
        .filter((c) => !c.mandatory && form.coverages.includes(c.code))
        .map((c) => {
          const capital = form.capitals[c.code];
          return capital !== undefined && c.capitalType === 'OPTIONS'
            ? { code: c.code, capital: { amount: capital, currency: 'MZN' } }
            : { code: c.code };
        });
      const request: SimulateRatingRequest = {
        vehicle,
        driver: {
          birthDate: form.birthDate,
          licenceIssueDate: form.licenceIssueDate || undefined,
        },
        coverages: requested,
        startDate: form.startDate,
        durationDays,
        rating: ratingForServer(rating) as unknown as SimulateRatingRequest['rating'],
        charges: null,
      };
      setResult(await simulateRating(versionId, request));
    } catch (error) {
      const problem = getProblem(error);
      setResult(null);
      const tariffErrors = problem.errors.filter((e) => e.field.startsWith('/'));
      if (problem.status === 400 && tariffErrors.length > 0) {
        onTariffErrors(tariffErrors.map(({ field, code }) => ({ field, code })));
        setMessage(t('simulator.fixTariff'));
      } else {
        setMessage(problemMessage(t, problem));
      }
    } finally {
      setRunning(false);
    }
  };

  const select = (
    key: 'category' | 'usage' | 'province',
    label: string,
    options: readonly string[],
    prefix: string,
  ) => (
    <TextField
      select
      size="small"
      label={label}
      value={form[key]}
      onChange={(event) => set(key, event.target.value)}
      fullWidth
    >
      {options.map((option) => (
        <MenuItem key={option} value={option}>
          {t(`${prefix}.${option}`)}
        </MenuItem>
      ))}
    </TextField>
  );

  const intField = (key: 'year' | 'engineCc' | 'seats' | 'grossWeightKg', label: string) => {
    const valid = key === 'year' ? yearValid : intValid(form[key]);
    return (
      <TextField
        size="small"
        label={label}
        value={form[key]}
        required={key === 'year'}
        onChange={(event) => set(key, event.target.value)}
        error={!valid}
        helperText={valid ? undefined : t('simulator.invalidField')}
        slotProps={{ htmlInput: { inputMode: 'numeric' } }}
        fullWidth
      />
    );
  };

  const unavailable = () => {
    if (!result || result.status !== 'NOT_AVAILABLE') return null;
    const detail = (result.unavailableDetail ?? {}) as Record<string, unknown>;
    const coverageCode = typeof detail.coverageCode === 'string' ? detail.coverageCode : undefined;
    const table = typeof detail.table === 'string' ? detail.table : undefined;
    const c = coverageCode
      ? rating.coverages.findIndex((x) => x.coverageCode === coverageCode)
      : -1;
    const factorIndex =
      c >= 0 && table && table !== 'BASE'
        ? (rating.coverages[c]!.factors ?? []).findIndex((f) => f.code === table)
        : -1;
    const factor = factorIndex >= 0 ? rating.coverages[c]!.factors![factorIndex] : undefined;
    const parts: string[] = [];
    if (coverageCode)
      parts.push(t('simulator.detail.coverage', { name: t(`coverage.${coverageCode}`) }));
    if (table) {
      parts.push(
        t('simulator.detail.table', {
          name: table === 'BASE' ? t('simulator.detail.base') : factor?.label || table,
        }),
      );
    }
    const location: TariffLocation | null =
      c < 0
        ? null
        : table === 'BASE'
          ? { kind: 'base', coverage: c }
          : factorIndex >= 0
            ? { kind: 'factor', coverage: c, factor: factorIndex }
            : { kind: 'coverage', coverage: c };
    return (
      <Alert
        severity="warning"
        data-testid="simulation-unavailable"
        action={
          location ? (
            <Button color="inherit" size="small" onClick={() => onGoToTable(location)}>
              {t('simulator.goToTable')}
            </Button>
          ) : undefined
        }
      >
        <Typography variant="body2">
          {t(`quoteUnavailable.${result.unavailableReason ?? 'EVALUATION_ERROR'}`)}
        </Typography>
        {parts.length > 0 && <Typography variant="body2">{parts.join(' · ')}</Typography>}
      </Alert>
    );
  };

  return (
    <Grid container spacing={3}>
      <Grid size={{ xs: 12, md: 6 }}>
        <Paper
          sx={{ p: { xs: 2, sm: 3 } }}
          component="form"
          aria-label={t('tariffs.tabs.simulator')}
          onSubmit={(e) => {
            e.preventDefault();
            if (canRun) void run();
          }}
        >
          <Stack spacing={2.5}>
            <Typography variant="h6" component="h2">
              {t('simulator.vehicle')}
            </Typography>
            {select('category', t('simulator.category'), vehicleCategories, 'vehicleCategory')}
            {select('usage', t('simulator.usage'), vehicleUsages, 'vehicleUsage')}
            {select('province', t('simulator.province'), provinces, 'province')}
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              {intField('year', t('simulator.year'))}
              {intField('engineCc', t('simulator.engineCc'))}
            </Stack>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              {intField('seats', t('simulator.seats'))}
              {intField('grossWeightKg', t('simulator.grossWeightKg'))}
            </Stack>
            <MoneyField
              size="small"
              label={t('simulator.commercialValue')}
              value={form.commercialValue}
              onChange={(value) => set('commercialValue', value)}
              onValidityChange={(valid) => setInvalid((s) => ({ ...s, commercialValue: !valid }))}
            />
            <FormControlLabel
              control={
                <Switch
                  checked={form.hasTracker}
                  onChange={(event) => set('hasTracker', event.target.checked)}
                />
              }
              label={t('simulator.hasTracker')}
            />

            <Typography variant="h6" component="h2">
              {t('simulator.driver')}
            </Typography>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                size="small"
                type="date"
                label={t('simulator.birthDate')}
                value={form.birthDate}
                required
                onChange={(event) => set('birthDate', event.target.value)}
                slotProps={{ inputLabel: { shrink: true } }}
                fullWidth
              />
              <TextField
                size="small"
                type="date"
                label={t('simulator.licenceIssueDate')}
                value={form.licenceIssueDate}
                onChange={(event) => set('licenceIssueDate', event.target.value)}
                slotProps={{ inputLabel: { shrink: true } }}
                fullWidth
              />
            </Stack>

            <Typography variant="h6" component="h2">
              {t('simulator.coverages')}
            </Typography>
            <FormGroup>
              {coverages.map((c) => {
                const name = t(`coverage.${c.code}`);
                const checked = c.mandatory || form.coverages.includes(c.code);
                return (
                  <Stack key={c.code} spacing={1} sx={{ mb: 1 }}>
                    <FormControlLabel
                      control={
                        <Checkbox
                          checked={checked}
                          disabled={c.mandatory}
                          onChange={(event) =>
                            set(
                              'coverages',
                              event.target.checked
                                ? [...form.coverages, c.code]
                                : form.coverages.filter((x) => x !== c.code),
                            )
                          }
                        />
                      }
                      label={c.mandatory ? t('simulator.mandatory', { name }) : name}
                    />
                    {c.capitalType === 'OPTIONS' && checked && c.capitalOptions?.length ? (
                      <TextField
                        select
                        size="small"
                        label={t('simulator.capital', { name })}
                        value={String(form.capitals[c.code] ?? c.capitalOptions[0]!.amount)}
                        onChange={(event) =>
                          set('capitals', {
                            ...form.capitals,
                            [c.code]: Number(event.target.value),
                          })
                        }
                        sx={{ ml: { sm: 4 } }}
                      >
                        {c.capitalOptions.map((option) => (
                          <MenuItem key={option.amount} value={String(option.amount)}>
                            {formatMoney(option, i18n.language)}
                          </MenuItem>
                        ))}
                      </TextField>
                    ) : null}
                  </Stack>
                );
              })}
            </FormGroup>

            <Typography variant="h6" component="h2">
              {t('simulator.period')}
            </Typography>
            <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
              <TextField
                size="small"
                type="date"
                label={t('simulator.startDate')}
                value={form.startDate}
                required
                onChange={(event) => set('startDate', event.target.value)}
                slotProps={{ inputLabel: { shrink: true } }}
                fullWidth
              />
              {temporary ? (
                <TextField
                  select
                  size="small"
                  label={t('simulator.duration')}
                  value={String(durationDays)}
                  onChange={(event) => set('durationDays', Number(event.target.value))}
                  fullWidth
                >
                  {durations.map((days) => (
                    <MenuItem key={days} value={String(days)}>
                      {t('simulator.days', { days })}
                    </MenuItem>
                  ))}
                </TextField>
              ) : (
                <TextField
                  size="small"
                  label={t('simulator.duration')}
                  value={t('simulator.oneYear')}
                  disabled
                  fullWidth
                />
              )}
            </Stack>
            <div>
              <Button type="submit" variant="contained" disabled={!canRun || running}>
                {t('simulator.calculate')}
              </Button>
            </div>
          </Stack>
        </Paper>
      </Grid>
      <Grid size={{ xs: 12, md: 6 }}>
        <Paper sx={{ p: { xs: 2, sm: 3 } }} component="section" aria-label={t('simulator.result')}>
          <Stack spacing={2}>
            <Typography variant="h6" component="h2">
              {t('simulator.result')}
            </Typography>
            {running && <CircularProgress size={24} />}
            {message && (
              <Alert severity="error" role="alert">
                {message}
              </Alert>
            )}
            {result?.status === 'OFFERED' && result.breakdown && (
              <BreakdownTable breakdown={result.breakdown} />
            )}
            {unavailable()}
          </Stack>
        </Paper>
      </Grid>
    </Grid>
  );
}
