import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getProblem, problemMessage } from '../../errors/problem';

const lists = {
  vehicle: { path: '/cliente/veiculos', label: 'vehicles.backToList' },
  driver: { path: '/cliente/condutores', label: 'drivers.backToList' },
} as const;

/**
 * Erro ao carregar um veículo, um condutor ou uma lista: 404 (`VEHICLE_NOT_FOUND`,
 * `DRIVER_NOT_FOUND`) mostra a mensagem e a ligação para a lista; os outros erros, a mensagem
 * traduzida e "Tentar de novo".
 */
export function LoadError({
  error,
  kind,
  onRetry,
}: {
  error: unknown;
  kind: keyof typeof lists;
  onRetry(): void;
}) {
  const { t } = useTranslation();
  const problem = getProblem(error);
  const list = lists[kind];
  const notFound = problem.status === 404;
  return (
    <Stack spacing={2} alignItems="flex-start">
      <Alert severity={notFound ? 'warning' : 'error'} role="alert" sx={{ alignSelf: 'stretch' }}>
        {problemMessage(t, problem)}
      </Alert>
      {notFound ? (
        <Button variant="outlined" component={RouterLink} to={list.path}>
          {t(list.label)}
        </Button>
      ) : (
        <Button variant="outlined" onClick={onRetry}>
          {t('vehicles.retry')}
        </Button>
      )}
    </Stack>
  );
}
