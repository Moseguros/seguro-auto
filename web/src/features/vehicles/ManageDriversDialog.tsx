import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CircularProgress from '@mui/material/CircularProgress';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormHelperText from '@mui/material/FormHelperText';
import Radio from '@mui/material/Radio';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  getListMyDriversQueryKey,
  listMyDrivers,
  updateMyVehicleDrivers,
  type VehicleResponse,
} from '../../api/generated';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';

/** Máximo de condutores por veículo (S2-03 §3.5). */
export const maxVehicleDrivers = 5;

export interface ManageDriversDialogProps {
  vehicle: VehicleResponse;
  onClose(): void;
  onSaved(vehicle: VehicleResponse): void;
}

/** Escolhe os condutores do veículo e o habitual (só um). Montado só quando aberto. */
export function ManageDriversDialog({ vehicle, onClose, onSaved }: ManageDriversDialogProps) {
  const { t } = useTranslation();
  const params = { size: 50 };
  const drivers = useQuery({
    queryKey: getListMyDriversQueryKey(params),
    queryFn: ({ signal }) => listMyDrivers(params, undefined, signal),
  });
  const [selected, setSelected] = useState<string[]>(() => vehicle.drivers.map((d) => d.driverId));
  const [habitual, setHabitual] = useState<string | null>(
    () => vehicle.drivers.find((d) => d.habitual)?.driverId ?? null,
  );
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const toggle = (driverId: string, checked: boolean) => {
    if (checked) setSelected((current) => [...current, driverId]);
    else {
      setSelected((current) => current.filter((id) => id !== driverId));
      if (habitual === driverId) setHabitual(null);
    }
  };

  const missingHabitual = selected.length > 0 && (!habitual || !selected.includes(habitual));
  const full = selected.length >= maxVehicleDrivers;

  const save = async () => {
    setError(null);
    setSaving(true);
    const order = drivers.data?.content.map((d) => d.id) ?? [];
    const ids = [...selected].sort((a, b) => order.indexOf(a) - order.indexOf(b));
    try {
      const next = await updateMyVehicleDrivers(vehicle.id, {
        drivers: ids.map((driverId) => ({ driverId, habitual: driverId === habitual })),
        version: vehicle.version,
      });
      onSaved(next);
    } catch (e) {
      const problem = getProblem(e);
      const fieldError = problem.code === 'VALIDATION_ERROR' ? problem.errors[0] : undefined;
      setError(
        fieldError
          ? fieldMessage(t, fieldError.code)
          : problem.code === 'CONCURRENT_MODIFICATION'
            ? t('vehicles.form.conflict')
            : problemMessage(t, problem),
      );
    } finally {
      setSaving(false);
    }
  };

  let content;
  if (drivers.isPending) content = <CircularProgress aria-label={t('auth.loading')} />;
  else if (drivers.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(drivers.error))}</Alert>;
  } else if (drivers.data.content.length === 0) {
    content = (
      <Stack spacing={2} alignItems="flex-start">
        <Typography>{t('vehicles.manage.noDrivers')}</Typography>
        <Button
          variant="contained"
          component={RouterLink}
          to={`/cliente/condutores/novo?veiculo=${vehicle.id}`}
        >
          {t('vehicles.manage.register')}
        </Button>
      </Stack>
    );
  } else {
    content = (
      <Stack spacing={1}>
        <Typography variant="body2" color="text.secondary">
          {t('vehicles.manage.help', { max: maxVehicleDrivers })}
        </Typography>
        {drivers.data.content.map((driver) => {
          const checked = selected.includes(driver.id);
          return (
            <Stack
              key={driver.id}
              direction="row"
              alignItems="center"
              justifyContent="space-between"
              useFlexGap
              flexWrap="wrap"
              data-testid="manage-driver"
            >
              <FormControlLabel
                sx={{ minWidth: 0, mr: 1 }}
                control={
                  <Checkbox
                    checked={checked}
                    disabled={!checked && full}
                    onChange={(event) => toggle(driver.id, event.target.checked)}
                  />
                }
                label={driver.fullName}
              />
              {checked && (
                <FormControlLabel
                  control={
                    <Radio
                      checked={habitual === driver.id}
                      onChange={() => setHabitual(driver.id)}
                      name="habitual"
                      value={driver.id}
                      inputProps={{
                        'aria-label': t('vehicles.manage.habitualOf', { name: driver.fullName }),
                      }}
                    />
                  }
                  label={t('vehicles.manage.habitual')}
                />
              )}
            </Stack>
          );
        })}
        {missingHabitual && (
          <FormHelperText error>{fieldMessage(t, 'HABITUAL_DRIVER_REQUIRED')}</FormHelperText>
        )}
      </Stack>
    );
  }

  const hasDrivers = (drivers.data?.content.length ?? 0) > 0;

  return (
    <Dialog open onClose={onClose} fullWidth maxWidth="sm" aria-labelledby="manage-drivers-title">
      <DialogTitle id="manage-drivers-title">{t('vehicles.manage.title')}</DialogTitle>
      <DialogContent>
        <Stack spacing={2}>
          {content}
          {error && (
            <Alert severity="error" role="alert">
              {error}
            </Alert>
          )}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>{t('vehicles.manage.cancel')}</Button>
        {hasDrivers && (
          <Button
            variant="contained"
            disabled={saving || missingHabitual}
            onClick={() => void save()}
          >
            {t('vehicles.manage.save')}
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
}
