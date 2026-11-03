import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useLocation } from 'react-router-dom';
import {
  deleteMyDriver,
  getListMyDriversQueryKey,
  listMyDrivers,
  type DriverResponse,
} from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { LoadError } from '../vehicles/LoadError';
import { CardGrid, CardSkeletons, ListHeader, listPageSize } from '../vehicles/VehicleListPage';
import {
  invalidateDrivers,
  invalidateVehicles,
  useReferenceValues,
} from '../vehicles/vehicleQueries';
import { DriverCard } from './DriverCard';

/** Estado de navegação para a lista de condutores. */
export interface DriverListState {
  notice?: 'saved';
}

/** `/cliente/condutores`: os condutores do cliente em cartões. */
export function DriverListPage() {
  const { t } = useTranslation();
  const location = useLocation();
  const queryClient = useQueryClient();
  const [notice, setNotice] = useState<string | null>(() =>
    (location.state as DriverListState | null)?.notice === 'saved' ? t('drivers.saved') : null,
  );
  const [removing, setRemoving] = useState<DriverResponse | null>(null);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const categories = useReferenceValues('LICENCE_CATEGORY');
  const params = { size: listPageSize, sort: 'createdAt,desc' };
  const query = useInfiniteQuery({
    queryKey: [...getListMyDriversQueryKey(params), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyDrivers({ ...params, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });

  const remove = async () => {
    if (!removing) return;
    setError(null);
    setBusy(true);
    try {
      await deleteMyDriver(removing.id);
      setRemoving(null);
      setNotice(t('drivers.removed'));
      await Promise.all([invalidateDrivers(queryClient), invalidateVehicles(queryClient)]);
    } catch (e) {
      setError(problemMessage(t, getProblem(e)));
    } finally {
      setBusy(false);
    }
  };

  let content;
  if (query.isPending) content = <CardSkeletons />;
  else if (query.isError) {
    content = <LoadError error={query.error} kind="driver" onRetry={() => void query.refetch()} />;
  } else {
    const drivers = query.data.pages.flatMap((page) => page.content);
    content =
      drivers.length === 0 ? (
        <Card>
          <CardContent>
            <Stack spacing={2} alignItems="flex-start">
              <Typography>{t('drivers.empty')}</Typography>
              <Button
                variant="contained"
                startIcon={<Add />}
                component={RouterLink}
                to="/cliente/condutores/novo"
              >
                {t('drivers.add')}
              </Button>
            </Stack>
          </CardContent>
        </Card>
      ) : (
        <Stack spacing={2}>
          <CardGrid>
            {drivers.map((driver) => (
              <DriverCard
                key={driver.id}
                driver={driver}
                categories={categories.data ?? []}
                onRemove={(value) => {
                  setError(null);
                  setRemoving(value);
                }}
              />
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
        title={t('drivers.title')}
        action={t('drivers.add')}
        to="/cliente/condutores/novo"
      />
      {content}
      <Dialog
        open={removing !== null}
        onClose={() => setRemoving(null)}
        aria-labelledby="remove-driver-title"
      >
        <DialogTitle id="remove-driver-title">{t('drivers.removeDialog.title')}</DialogTitle>
        <DialogContent>
          <Stack spacing={2}>
            <DialogContentText>
              {t('drivers.removeDialog.text', { name: removing?.fullName ?? '' })}
            </DialogContentText>
            {error && (
              <Alert severity="error" role="alert">
                {error}
              </Alert>
            )}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setRemoving(null)}>{t('drivers.removeDialog.cancel')}</Button>
          <Button color="error" variant="contained" disabled={busy} onClick={() => void remove()}>
            {t('drivers.removeDialog.confirm')}
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar
        open={notice !== null}
        autoHideDuration={4000}
        onClose={() => setNotice(null)}
        message={notice}
      />
    </Stack>
  );
}
