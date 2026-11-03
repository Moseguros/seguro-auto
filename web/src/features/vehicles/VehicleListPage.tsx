import Add from '@mui/icons-material/Add';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Skeleton from '@mui/material/Skeleton';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import { getListMyVehiclesQueryKey, listMyVehicles } from '../../api/generated';
import { LoadError } from './LoadError';
import { VehicleCard } from './VehicleCard';
import { VehiclesTabs } from './VehiclesTabs';

export const listPageSize = 50;

/** Estado de navegação para a lista (aviso depois de retirar). */
export interface ListState {
  notice?: 'removed';
}

/** Grelha dos cartões: 1 coluna abaixo de 600 px, 2 até 1200 e 3 acima. */
export function CardGrid({ children }: { children: ReactNode }) {
  return (
    <Box
      sx={{
        display: 'grid',
        gridTemplateColumns: {
          xs: '1fr',
          sm: 'repeat(2, minmax(0, 1fr))',
          lg: 'repeat(3, minmax(0, 1fr))',
        },
        gap: 2,
      }}
    >
      {children}
    </Box>
  );
}

/** Três esqueletos de cartão enquanto a lista carrega. */
export function CardSkeletons() {
  const { t } = useTranslation();
  return (
    <CardGrid>
      {[0, 1, 2].map((index) => (
        <Card key={index} data-testid="card-skeleton" aria-label={t('auth.loading')}>
          <CardContent>
            <Skeleton variant="text" width="50%" height={36} />
            <Skeleton variant="text" width="80%" />
            <Skeleton variant="text" width="60%" />
            <Skeleton variant="text" width="70%" />
          </CardContent>
        </Card>
      ))}
    </CardGrid>
  );
}

/** Página de uma lista com os separadores, o título e o botão de acrescentar. */
export function ListHeader({ title, action, to }: { title: string; action: string; to: string }) {
  return (
    <Stack spacing={2}>
      <VehiclesTabs />
      <Stack
        direction={{ xs: 'column', sm: 'row' }}
        spacing={2}
        justifyContent="space-between"
        alignItems={{ xs: 'stretch', sm: 'center' }}
      >
        <Typography variant="h4" component="h1">
          {title}
        </Typography>
        <Button variant="contained" startIcon={<Add />} component={RouterLink} to={to}>
          {action}
        </Button>
      </Stack>
    </Stack>
  );
}

/** `/cliente/veiculos`: os veículos do cliente em cartões. */
export function VehicleListPage() {
  const { t } = useTranslation();
  const location = useLocation();
  const [removed, setRemoved] = useState(
    () => (location.state as ListState | null)?.notice === 'removed',
  );
  const params = { size: listPageSize, sort: 'createdAt,desc' };
  const query = useInfiniteQuery({
    queryKey: [...getListMyVehiclesQueryKey(params), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyVehicles({ ...params, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });

  let content;
  if (query.isPending) content = <CardSkeletons />;
  else if (query.isError) {
    content = <LoadError error={query.error} kind="vehicle" onRetry={() => void query.refetch()} />;
  } else {
    const vehicles = query.data.pages.flatMap((page) => page.content);
    content =
      vehicles.length === 0 ? (
        <Card>
          <CardContent>
            <Stack spacing={2} alignItems="flex-start">
              <Typography>{t('vehicles.empty')}</Typography>
              <Button
                variant="contained"
                startIcon={<Add />}
                component={RouterLink}
                to="/cliente/veiculos/novo"
              >
                {t('vehicles.add')}
              </Button>
            </Stack>
          </CardContent>
        </Card>
      ) : (
        <Stack spacing={2}>
          <CardGrid>
            {vehicles.map((vehicle) => (
              <VehicleCard key={vehicle.id} vehicle={vehicle} />
            ))}
          </CardGrid>
          {query.hasNextPage && (
            <Box>
              <Button
                variant="outlined"
                disabled={query.isFetchingNextPage}
                onClick={() => void query.fetchNextPage()}
              >
                {t('vehicles.loadMore')}
              </Button>
            </Box>
          )}
        </Stack>
      );
  }

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <ListHeader
        title={t('vehicles.title')}
        action={t('vehicles.add')}
        to="/cliente/veiculos/novo"
      />
      {content}
      <Snackbar
        open={removed}
        autoHideDuration={4000}
        onClose={() => setRemoved(false)}
        message={t('vehicles.removed')}
      />
    </Stack>
  );
}
