import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { getListMyQuoteRequestsQueryKey, listMyQuoteRequests } from '../../api/generated';
import { quotePaths, RECENT_QUOTES_SIZE } from './config';
import { QuoteSummaryCard } from './QuoteSummaryCard';

const params = { size: RECENT_QUOTES_SIZE, sort: 'createdAt,desc' };

/**
 * As 3 simulações mais recentes do cliente e "Ver todas". Não aparece sem simulações (nem com
 * erro). `card`: dentro de um cartão (início do cliente).
 */
export function RecentQuotes({ card = false }: { card?: boolean }) {
  const { t } = useTranslation();
  const query = useQuery({
    queryKey: getListMyQuoteRequestsQueryKey(params),
    queryFn: ({ signal }) => listMyQuoteRequests(params, undefined, signal),
  });
  const items = query.data?.content ?? [];
  if (items.length === 0) return null;

  const list = (
    <Stack spacing={1.5} data-testid="recent-quotes">
      <Typography variant={card ? 'h6' : 'subtitle1'} component="h2">
        {t('myQuotes.recentTitle')}
      </Typography>
      {items.map((summary) => (
        <QuoteSummaryCard
          key={summary.id}
          summary={summary}
          to={`${quotePaths.customer.base}/${summary.id}`}
        />
      ))}
      <Button
        variant="outlined"
        component={RouterLink}
        to={quotePaths.customer.list}
        sx={{ alignSelf: 'flex-start' }}
      >
        {t('myQuotes.seeAll')}
      </Button>
    </Stack>
  );
  return card ? (
    <Card>
      <CardContent>{list}</CardContent>
    </Card>
  ) : (
    list
  );
}
