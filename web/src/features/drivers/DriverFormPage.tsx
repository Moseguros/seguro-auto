import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import {
  getGetMyDriverQueryKey,
  getListMyDriversQueryKey,
  getMyDriver,
  listMyDrivers,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { LoadError } from '../vehicles/LoadError';
import type { VehicleDetailState } from '../vehicles/VehicleDetailPage';
import { invalidateDrivers, invalidateVehicles } from '../vehicles/vehicleQueries';
import { DriverForm } from './DriverForm';
import type { DriverListState } from './DriverListPage';

/**
 * `/cliente/condutores/novo` e `/cliente/condutores/:id/editar`. Com `?veiculo={id}`, depois de
 * gravar volta ao veículo e abre "Gerir condutores".
 */
export function DriverFormPage() {
  const { t } = useTranslation();
  const { id } = useParams();
  const [params] = useSearchParams();
  const vehicleId = params.get('veiculo');
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const editing = Boolean(id);
  const driver = useQuery({
    queryKey: getGetMyDriverQueryKey(id ?? ''),
    queryFn: ({ signal }) => getMyDriver(id ?? '', undefined, signal),
    enabled: editing,
    // Só a máscara da carta vem na resposta; ao voltar a esta página lê-se de novo.
    staleTime: 0,
  });
  const listParams = { size: 50 };
  const drivers = useQuery({
    queryKey: getListMyDriversQueryKey(listParams),
    queryFn: ({ signal }) => listMyDrivers(listParams, undefined, signal),
  });

  const onSaved = async () => {
    await Promise.all([invalidateDrivers(queryClient), invalidateVehicles(queryClient)]);
    if (vehicleId) {
      const state: VehicleDetailState = { openDrivers: true };
      navigate(`/cliente/veiculos/${vehicleId}`, { state });
    } else {
      const state: DriverListState = { notice: 'saved' };
      navigate('/cliente/condutores', { state });
    }
  };

  let content;
  if ((editing && driver.isPending) || drivers.isPending) content = <LoadingScreen />;
  else if (editing && driver.isError) {
    content = (
      <LoadError error={driver.error} kind="driver" onRetry={() => void driver.refetch()} />
    );
  } else if (drivers.isError) {
    content = (
      <LoadError error={drivers.error} kind="driver" onRetry={() => void drivers.refetch()} />
    );
  } else {
    const current = editing ? driver.data : undefined;
    const ownerTaken = drivers.data.content.some((d) => d.isOwner && d.id !== current?.id);
    content = (
      <Card>
        <CardContent>
          <DriverForm
            key={current ? `${current.id}-${current.version}` : 'new'}
            driver={current}
            ownerTaken={ownerTaken}
            onSaved={onSaved}
            onReload={() => void driver.refetch()}
          />
        </CardContent>
      </Card>
    );
  }

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {editing ? t('drivers.editTitle') : t('drivers.newTitle')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
