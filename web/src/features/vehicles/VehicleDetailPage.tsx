import Edit from '@mui/icons-material/Edit';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Fragment, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation, useNavigate, useParams } from 'react-router-dom';
import {
  deleteMyVehicle,
  getGetMyVehicleQueryKey,
  getMyVehicle,
  type VehicleResponse,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { invalidateDrivers, invalidateVehicles } from './vehicleQueries';
import { LoadError } from './LoadError';
import { ManageDriversDialog } from './ManageDriversDialog';
import { RegistrationDocSection } from './RegistrationDocSection';
import type { ListState } from './VehicleListPage';

/** Estado de navegação para o detalhe (avisos e abrir os condutores). */
export interface VehicleDetailState {
  notice?: 'created' | 'saved';
  /** Volta do registo de um condutor: abre "Gerir condutores". */
  openDrivers?: boolean;
}

/** `/cliente/veiculos/:id`: dados, livrete e condutores do veículo. */
export function VehicleDetailPage() {
  const { t } = useTranslation();
  const { id = '' } = useParams();
  const location = useLocation();
  const queryClient = useQueryClient();
  const state = (location.state ?? {}) as VehicleDetailState;
  const [created, setCreated] = useState(state.notice === 'created');
  const [saved, setSaved] = useState(state.notice === 'saved');
  const [managing, setManaging] = useState(state.openDrivers === true);
  const [driversSaved, setDriversSaved] = useState(false);
  const queryKey = getGetMyVehicleQueryKey(id);
  const query = useQuery({
    queryKey,
    queryFn: ({ signal }) => getMyVehicle(id, undefined, signal),
  });

  const updated = (vehicle: VehicleResponse) => {
    queryClient.setQueryData(queryKey, vehicle);
    void invalidateVehicles(queryClient);
  };

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return <LoadError error={query.error} kind="vehicle" onRetry={() => void query.refetch()} />;
  }
  const vehicle = query.data;

  return (
    <Stack spacing={3} sx={{ maxWidth: 900, minWidth: 0 }}>
      <Header vehicle={vehicle} />
      {created && (
        <Alert severity="success" onClose={() => setCreated(false)}>
          {t('vehicles.created')}
        </Alert>
      )}
      <DataSection vehicle={vehicle} />
      <RegistrationDocSection
        vehicle={vehicle}
        onUpdated={(next) => {
          setCreated(false);
          updated(next);
        }}
      />
      <Card component="section" aria-labelledby="vehicle-drivers-title">
        <CardContent>
          <Stack spacing={2}>
            <Typography variant="h6" component="h2" id="vehicle-drivers-title">
              {t('vehicles.drivers.title')}
            </Typography>
            {vehicle.drivers.length === 0 ? (
              <Typography color="text.secondary">{t('vehicles.drivers.none')}</Typography>
            ) : (
              <Stack component="ul" spacing={1} sx={{ m: 0, p: 0, listStyle: 'none' }}>
                {vehicle.drivers.map((driver) => (
                  <Stack
                    component="li"
                    key={driver.driverId}
                    direction="row"
                    spacing={1}
                    alignItems="center"
                    data-testid="vehicle-driver"
                  >
                    <Typography sx={{ wordBreak: 'break-word' }}>{driver.fullName}</Typography>
                    {driver.habitual && (
                      <Chip size="small" color="primary" label={t('vehicles.drivers.habitual')} />
                    )}
                  </Stack>
                ))}
              </Stack>
            )}
            <Box>
              <Button variant="outlined" onClick={() => setManaging(true)}>
                {t('vehicles.drivers.manage')}
              </Button>
            </Box>
          </Stack>
        </CardContent>
      </Card>
      {managing && (
        <ManageDriversDialog
          vehicle={vehicle}
          onClose={() => setManaging(false)}
          onSaved={(next) => {
            setManaging(false);
            updated(next);
            void invalidateDrivers(queryClient);
            setDriversSaved(true);
          }}
        />
      )}
      <Snackbar
        open={saved || driversSaved}
        autoHideDuration={4000}
        onClose={() => {
          setSaved(false);
          setDriversSaved(false);
        }}
        message={driversSaved ? t('vehicles.manage.saved') : t('vehicles.saved')}
      />
    </Stack>
  );
}

function Header({ vehicle }: { vehicle: VehicleResponse }) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [confirming, setConfirming] = useState(false);
  const [removing, setRemoving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const remove = async () => {
    setError(null);
    setRemoving(true);
    try {
      await deleteMyVehicle(vehicle.id);
      queryClient.removeQueries({ queryKey: getGetMyVehicleQueryKey(vehicle.id) });
      await Promise.all([invalidateVehicles(queryClient), invalidateDrivers(queryClient)]);
      const state: ListState = { notice: 'removed' };
      navigate('/cliente/veiculos', { state });
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setRemoving(false);
    }
  };

  return (
    <Stack
      direction={{ xs: 'column', sm: 'row' }}
      spacing={2}
      justifyContent="space-between"
      alignItems={{ xs: 'stretch', sm: 'flex-start' }}
    >
      <Stack spacing={1} sx={{ minWidth: 0 }}>
        <Typography
          variant="h4"
          component="h1"
          sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}
        >
          {vehicle.plate}
        </Typography>
        <Typography sx={{ wordBreak: 'break-word' }}>
          {vehicle.make} {vehicle.model}
        </Typography>
        <Box>
          {vehicle.inatroVerified ? (
            <Chip size="small" color="success" variant="outlined" label={t('vehicles.verified')} />
          ) : (
            <Chip size="small" variant="outlined" label={t('vehicles.notVerified')} />
          )}
        </Box>
      </Stack>
      <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
        <Button
          variant="outlined"
          startIcon={<Edit />}
          component={RouterLink}
          to={`/cliente/veiculos/${vehicle.id}/editar`}
        >
          {t('vehicles.detail.edit')}
        </Button>
        <Button
          color="error"
          onClick={() => {
            setError(null);
            setConfirming(true);
          }}
        >
          {t('vehicles.detail.remove')}
        </Button>
      </Stack>
      <Dialog
        open={confirming}
        onClose={() => setConfirming(false)}
        aria-labelledby="remove-vehicle-title"
      >
        <DialogTitle id="remove-vehicle-title">
          {t('vehicles.detail.removeDialog.title')}
        </DialogTitle>
        <DialogContent>
          <Stack spacing={2}>
            <DialogContentText>{t('vehicles.detail.removeDialog.text')}</DialogContentText>
            {error && (
              <Alert severity="error" role="alert">
                {error}
              </Alert>
            )}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setConfirming(false)}>
            {t('vehicles.detail.removeDialog.cancel')}
          </Button>
          <Button
            color="error"
            variant="contained"
            disabled={removing}
            onClick={() => void remove()}
          >
            {t('vehicles.detail.removeDialog.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}

function DataSection({ vehicle }: { vehicle: VehicleResponse }) {
  const { t } = useTranslation();
  const none = t('vehicles.detail.none');
  const number = (value: number | undefined) => (value === undefined ? none : String(value));
  const rows: [string, string][] = [
    [t('vehicles.fields.plate'), vehicle.plate],
    [t('vehicles.fields.chassisNumber'), vehicle.chassisNumber],
    [t('vehicles.fields.make'), vehicle.make],
    [t('vehicles.fields.model'), vehicle.model],
    [t('vehicles.fields.year'), String(vehicle.year)],
    [t('vehicles.fields.category'), t(`vehicleCategory.${vehicle.category}`)],
    [t('vehicles.fields.engineCc'), number(vehicle.engineCc)],
    [t('vehicles.fields.seats'), String(vehicle.seats)],
    [t('vehicles.fields.grossWeightKg'), number(vehicle.grossWeightKg)],
    [t('vehicles.fields.usage'), t(`vehicleUsage.${vehicle.usage}`)],
    [t('vehicles.fields.provinceOfCirculation'), t(`province.${vehicle.provinceOfCirculation}`)],
    [
      t('vehicles.fields.commercialValue'),
      vehicle.commercialValue ? formatMoney(vehicle.commercialValue) : none,
    ],
    [
      t('vehicles.fields.hasTracker'),
      vehicle.hasTracker ? t('vehicles.detail.yes') : t('vehicles.detail.no'),
    ],
  ];
  return (
    <Card component="section" aria-labelledby="vehicle-data-title">
      <CardContent>
        <Typography variant="h6" component="h2" id="vehicle-data-title" sx={{ mb: 2 }}>
          {t('vehicles.detail.data')}
        </Typography>
        <Box
          component="dl"
          sx={{
            display: 'grid',
            gridTemplateColumns: { xs: '1fr', sm: 'minmax(0, 14rem) minmax(0, 1fr)' },
            columnGap: 2,
            rowGap: { xs: 0.25, sm: 1 },
            m: 0,
          }}
        >
          {rows.map(([label, value]) => (
            <Fragment key={label}>
              <Typography
                component="dt"
                variant="body2"
                color="text.secondary"
                sx={{ mt: { xs: 1, sm: 0 } }}
              >
                {label}
              </Typography>
              <Typography component="dd" sx={{ m: 0, wordBreak: 'break-word' }}>
                {value}
              </Typography>
            </Fragment>
          ))}
        </Box>
      </CardContent>
    </Card>
  );
}
