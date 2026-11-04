import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { DriverResponse, VehicleResponse } from '../../../api/generated';
import { fieldMessage } from '../../../errors/problem';
import { backToWizard } from '../config';
import { RecentQuotes } from '../RecentQuotes';
import { useStepValidity, type StepProps } from './stepTypes';

/** Condutor por defeito: o habitual do veículo; senão o próprio cliente; senão nenhum. */
export function defaultDriverId(
  vehicle: VehicleResponse | undefined,
  drivers: readonly DriverResponse[],
): string {
  const habitual = vehicle?.drivers.find((driver) => driver.habitual)?.driverId;
  if (habitual && drivers.some((driver) => driver.id === habitual)) return habitual;
  return drivers.find((driver) => driver.isOwner)?.id ?? '';
}

export interface VehicleStepProps extends StepProps {
  vehicles: VehicleResponse[];
  drivers: DriverResponse[];
}

/** Passo "Veículo" do cliente: um dos seus veículos e o condutor. */
export function VehicleStep({
  draft,
  update,
  onValidity,
  serverErrors,
  vehicles,
  drivers,
}: VehicleStepProps) {
  const { t } = useTranslation();
  const vehicleKnown = vehicles.some((vehicle) => vehicle.id === draft.vehicleId);
  const driverKnown = drivers.some((driver) => driver.id === draft.driverId);
  useStepValidity(vehicleKnown && driverKnown, onValidity);

  const choose = (vehicle: VehicleResponse) =>
    update({ vehicleId: vehicle.id, driverId: defaultDriverId(vehicle, drivers) });

  const vehicleError = serverErrors.vehicleId;
  const driverError = serverErrors.driverId;

  return (
    <Stack spacing={3}>
      {vehicles.length === 0 ? (
        <Stack spacing={2} alignItems="flex-start">
          <Typography>{t('quoteWizard.vehicle.noVehicles')}</Typography>
          <Button
            variant="contained"
            component={RouterLink}
            to={`/cliente/veiculos/novo?voltar=${backToWizard}`}
          >
            {t('quoteWizard.vehicle.addVehicle')}
          </Button>
        </Stack>
      ) : (
        <Stack spacing={1.5} role="radiogroup" aria-label={t('quoteWizard.vehicle.choose')}>
          <Typography variant="subtitle1" component="h3">
            {t('quoteWizard.vehicle.choose')}
          </Typography>
          {vehicleError && <Alert severity="error">{fieldMessage(t, vehicleError)}</Alert>}
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: { xs: '1fr', sm: 'repeat(2, minmax(0, 1fr))' },
              gap: 1.5,
            }}
          >
            {vehicles.map((vehicle) => {
              const selected = vehicle.id === draft.vehicleId;
              return (
                <Card
                  key={vehicle.id}
                  variant="outlined"
                  elevation={0}
                  sx={{
                    borderColor: selected ? 'primary.main' : undefined,
                    borderWidth: selected ? 2 : 1,
                  }}
                >
                  <CardActionArea
                    role="radio"
                    aria-checked={selected}
                    onClick={() => choose(vehicle)}
                    data-testid="quote-vehicle"
                  >
                    <CardContent>
                      <Typography
                        variant="h6"
                        component="span"
                        sx={{ display: 'block', fontVariantNumeric: 'tabular-nums' }}
                      >
                        {vehicle.plate}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {t('vehicles.card.summary', {
                          make: vehicle.make,
                          model: vehicle.model,
                          year: vehicle.year,
                        })}
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              );
            })}
          </Box>
          <Button
            variant="text"
            component={RouterLink}
            to={`/cliente/veiculos/novo?voltar=${backToWizard}`}
            sx={{ alignSelf: 'flex-start' }}
          >
            {t('quoteWizard.vehicle.addVehicle')}
          </Button>
        </Stack>
      )}

      {drivers.length === 0 ? (
        <Stack spacing={2} alignItems="flex-start">
          <Typography>{t('quoteWizard.vehicle.noDrivers')}</Typography>
          <Button
            variant="outlined"
            component={RouterLink}
            to={`/cliente/condutores/novo?voltar=${backToWizard}`}
          >
            {t('quoteWizard.vehicle.addDriver')}
          </Button>
        </Stack>
      ) : (
        <TextField
          select
          label={t('quoteWizard.vehicle.driver')}
          value={driverKnown ? draft.driverId : ''}
          onChange={(event) => update({ driverId: event.target.value })}
          error={!!driverError}
          helperText={driverError ? fieldMessage(t, driverError) : undefined}
          fullWidth
          slotProps={{ select: { native: true }, inputLabel: { shrink: true } }}
        >
          <option value="">{t('quoteWizard.vehicle.chooseDriver')}</option>
          {drivers.map((driver) => (
            <option key={driver.id} value={driver.id}>
              {driver.fullName}
            </option>
          ))}
        </TextField>
      )}

      <RecentQuotes />
    </Stack>
  );
}
