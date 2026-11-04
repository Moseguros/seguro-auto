import Add from '@mui/icons-material/Add';
import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import InputAdornment from '@mui/material/InputAdornment';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useState, type FormEvent } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import {
  getListBrokerQuoteRequestsQueryKey,
  listBrokerQuoteRequests,
  type ListBrokerQuoteRequestsParams,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { fieldMessage, getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { msisdnPrefix } from '../customer-auth/otp';
import { QUOTE_LIST_PAGE_SIZE, quotePaths } from './config';
import { clientSchema, fieldErrors } from './quoteRules';
import { formatMsisdn, offeredText, vehicleLabel } from './quoteFormat';
import { QuoteSummaryCard } from './QuoteSummaryCard';

const paths = quotePaths.broker;

/** `/mediador/cotacoes`: cotações da organização, com o filtro pelo telemóvel do cliente. */
export function BrokerQuotesPage() {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const wide = useMediaQuery(theme.breakpoints.up('sm'));
  const navigate = useNavigate();
  const [digits, setDigits] = useState('');
  const [filterError, setFilterError] = useState<string | null>(null);
  const [clientMsisdn, setClientMsisdn] = useState<string | undefined>(undefined);
  const language = i18n.language;

  const base: ListBrokerQuoteRequestsParams = {
    size: QUOTE_LIST_PAGE_SIZE,
    sort: 'createdAt,desc',
    ...(clientMsisdn ? { clientMsisdn } : {}),
  };
  const query = useInfiniteQuery({
    queryKey: [...getListBrokerQuoteRequestsQueryKey(base), 'infinite'],
    queryFn: ({ pageParam, signal }) =>
      listBrokerQuoteRequests({ ...base, page: pageParam }, undefined, signal),
    initialPageParam: 0,
    getNextPageParam: (last) =>
      (last.page + 1) * last.size < last.totalElements ? last.page + 1 : undefined,
  });

  const applyFilter = (event: FormEvent) => {
    event.preventDefault();
    if (!digits) {
      setFilterError(null);
      setClientMsisdn(undefined);
      return;
    }
    const code = fieldErrors(clientSchema.safeParse({ clientMsisdn: digits })).clientMsisdn;
    setFilterError(code ?? null);
    if (!code) setClientMsisdn(`${msisdnPrefix}${digits}`);
  };

  const newButton = (
    <Button variant="contained" startIcon={<Add />} component={RouterLink} to={paths.wizard}>
      {t('brokerQuotes.new')}
    </Button>
  );

  let content;
  if (query.isPending) content = <LoadingScreen />;
  else if (query.isError) {
    content = <Alert severity="error">{problemMessage(t, getProblem(query.error))}</Alert>;
  } else {
    const items = query.data.pages.flatMap((page) => page.content);
    if (items.length === 0) {
      content = (
        <Stack spacing={2} alignItems="flex-start">
          <Typography>{t('brokerQuotes.empty')}</Typography>
          {newButton}
        </Stack>
      );
    } else {
      const validity = (item: (typeof items)[number]) =>
        item.expired ? (
          <Chip size="small" color="warning" label={t('quote.expired')} />
        ) : item.validUntil ? (
          formatDate(item.validUntil, language)
        ) : (
          '—'
        );
      content = (
        <Stack spacing={2}>
          {wide ? (
            <TableContainer component={Card} sx={{ overflowX: 'auto' }}>
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>{t('brokerQuotes.columns.date')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.client')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.vehicle')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.type')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.offers')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.best')}</TableCell>
                    <TableCell>{t('brokerQuotes.columns.validity')}</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {items.map((item) => (
                    <TableRow
                      key={item.id}
                      hover
                      data-testid="broker-quote-row"
                      onClick={() => navigate(`${paths.base}/${item.id}`)}
                      sx={{ cursor: 'pointer' }}
                    >
                      <TableCell>{formatDate(item.createdAt, language)}</TableCell>
                      <TableCell sx={{ whiteSpace: 'nowrap' }}>
                        <RouterLink to={`${paths.base}/${item.id}`}>
                          {formatMsisdn(item.clientMsisdn)}
                        </RouterLink>
                      </TableCell>
                      <TableCell>{vehicleLabel(t, item.vehicle)}</TableCell>
                      <TableCell>{t(`quote.types.${item.productType}.title`)}</TableCell>
                      <TableCell>{offeredText(t, item.offeredCount)}</TableCell>
                      <TableCell sx={{ fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' }}>
                        {item.bestTotal
                          ? t('quote.bestFrom', { amount: formatMoney(item.bestTotal) })
                          : '—'}
                      </TableCell>
                      <TableCell>{validity(item)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          ) : (
            <Stack spacing={1.5}>
              {items.map((item) => (
                <QuoteSummaryCard
                  key={item.id}
                  summary={item}
                  to={`${paths.base}/${item.id}`}
                  showClient
                />
              ))}
            </Stack>
          )}
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
  }

  return (
    <Container maxWidth="lg" disableGutters>
      <Stack spacing={3}>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          justifyContent="space-between"
          alignItems={{ xs: 'stretch', sm: 'center' }}
        >
          <Typography variant="h4" component="h1">
            {t('brokerQuotes.title')}
          </Typography>
          {newButton}
        </Stack>
        <Stack
          component="form"
          onSubmit={applyFilter}
          direction={{ xs: 'column', sm: 'row' }}
          spacing={1.5}
          alignItems={{ xs: 'stretch', sm: 'flex-start' }}
          noValidate
        >
          <TextField
            label={t('brokerQuotes.filter')}
            value={digits}
            onChange={(event) => setDigits(event.target.value.replace(/\D/g, '').slice(0, 9))}
            error={!!filterError}
            helperText={filterError ? fieldMessage(t, filterError) : undefined}
            size="small"
            autoComplete="off"
            slotProps={{
              htmlInput: { inputMode: 'numeric', maxLength: 9 },
              input: {
                startAdornment: <InputAdornment position="start">{msisdnPrefix}</InputAdornment>,
              },
            }}
          />
          <Button type="submit" variant="outlined">
            {t('brokerQuotes.apply')}
          </Button>
        </Stack>
        {content}
      </Stack>
    </Container>
  );
}
