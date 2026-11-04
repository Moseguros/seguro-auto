import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getListMyQuoteRequestsQueryKey, listMyQuoteRequests } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { QUOTE_LIST_PAGE_SIZE, quotePaths } from './config';
import { QuoteSummaryCard } from './QuoteSummaryCard';

/** `/cliente/cotacoes`: as simulações do cliente (incluindo as do mediador). */
export function MyQuotesPage() {
  const { t } = useTranslation();
  const base = { size: QUOTE_LIST_PAGE_SIZE, sort: 'createdAt,desc' };
  const query = useInfiniteQuery({
    queryKey: [...getListMyQuoteRequestsQueryKey(base), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listMyQuoteRequests({ ...base, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  } else {
    const items = query.data.pages.flatMap((page) => page.content);
    content =
      items.length === 0 ? (
        <Stack spacing={2} alignItems="flex-start">
          <Typography>{t('myQuotes.empty')}</Typography>
          <Button variant="cta" size="large" component={RouterLink} to={quotePaths.customer.wizard}>
            {t('quote.simulate')}
          </Button>
        </Stack>
      ) : (
        <Stack spacing={1.5}>
          {items.map((summary) => (
            <QuoteSummaryCard
              key={summary.id}
              summary={summary}
              to={`${quotePaths.customer.base}/${summary.id}`}
            />
          ))}
          {query.hasNextPage && (
            <Button
              variant="outlined"
              onClick={() => void query.fetchNextPage()}
              disabled={query.isFetchingNextPage}
              sx={{ alignSelf: 'center' }}
            >
              {t('myQuotes.loadMore')}
            </Button>
          )}
        </Stack>
      );
  }

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('myQuotes.title')}
        </Typography>
        {content}
      </Stack>
    </Container>
  );
}
