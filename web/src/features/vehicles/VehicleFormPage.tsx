import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import { getGetMyVehicleQueryKey, getMyVehicle, type VehicleResponse } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { LoadError } from './LoadError';
import type { VehicleDetailState } from './VehicleDetailPage';
import { VehicleForm } from './VehicleForm';
import { invalidateVehicles } from './vehicleQueries';

/** `/cliente/veiculos/novo` e `/cliente/veiculos/:id/editar`. */
export function VehicleFormPage() {
  const { t } = useTranslation();
  const { id } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const editing = Boolean(id);
  const query = useQuery({
    queryKey: getGetMyVehicleQueryKey(id ?? ''),
    queryFn: ({ signal }) => getMyVehicle(id ?? '', undefined, signal),
    enabled: editing,
  });

  const onSaved = async (vehicle: VehicleResponse) => {
    queryClient.setQueryData(getGetMyVehicleQueryKey(vehicle.id), vehicle);
    await invalidateVehicles(queryClient);
    const state: VehicleDetailState = { notice: editing ? 'saved' : 'created' };
    navigate(`/cliente/veiculos/${vehicle.id}`, { state });
  };

  let content;
  if (editing && query.isPending) content = <LoadingScreen />;
  else if (editing && query.isError) {
    content = <LoadError error={query.error} kind="vehicle" onRetry={() => void query.refetch()} />;
  } else {
    content = (
      <Card>
        <CardContent>
          <VehicleForm
            vehicle={editing ? query.data : undefined}
            onSaved={onSaved}
            onReload={() => void query.refetch()}
          />
        </CardContent>
      </Card>
    );
  }

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {editing ? t('vehicles.editTitle') : t('vehicles.newTitle')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
