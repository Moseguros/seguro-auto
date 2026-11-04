import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getGetQuoteRequestQueryKey, getQuoteRequest } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import type { QuotePaths } from './config';

/** Pedido de cotação com as cotações (`getQuoteRequest`). */
export function useQuoteRequest(requestId: string | undefined) {
  return useQuery({
    queryKey: getGetQuoteRequestQueryKey(requestId ?? ''),
    queryFn: ({ signal }) => getQuoteRequest(requestId ?? '', undefined, signal),
    enabled: !!requestId,
  });
}

/** Erro ao ler o pedido: 404 → "Cotação não encontrada" e a ligação para a lista. */
export function QuoteLoadError({
  error,
  paths,
  onRetry,
}: {
  error: unknown;
  paths: QuotePaths;
  onRetry(): void;
}) {
  const { t } = useTranslation();
  const problem = getProblem(error);
  const notFound = problem.status === 404;
  return (
    <Stack spacing={2} alignItems="flex-start">
      <Alert severity={notFound ? 'warning' : 'error'} role="alert" sx={{ alignSelf: 'stretch' }}>
        {notFound ? t('quote.notFound') : problemMessage(t, problem)}
      </Alert>
      {notFound ? (
        <Button variant="outlined" component={RouterLink} to={paths.list}>
          {t('quote.backToList')}
        </Button>
      ) : (
        <Button variant="outlined" onClick={onRetry}>
          {t('vehicles.retry')}
        </Button>
      )}
    </Stack>
  );
}
