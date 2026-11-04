import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useNavigate, useParams } from 'react-router-dom';
import type { QuoteRequestResponse } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { formatDate } from '../../i18n/format';
import { CompareBar, toggleSelection } from './CompareBar';
import { quotePaths, type QuoteMode } from './config';
import { QuoteCard } from './QuoteCard';
import { durationText, formatMsisdn, vehicleLabel } from './quoteFormat';
import { QuoteLoadError, useQuoteRequest } from './quoteQueries';
import { UnavailableList } from './UnavailableList';

/** Data mais tardia (ISO) de uma lista, ou `undefined`. */
function latest(values: (string | undefined)[]): string | undefined {
  return values
    .filter((v): v is string => !!v)
    .sort()
    .at(-1);
}

/** Cabeçalho comum aos resultados, à comparação e ao detalhe: veículo, tipo e período. */
export function RequestHeading({ request }: { request: QuoteRequestResponse }) {
  const { t, i18n } = useTranslation();
  return (
    <Stack spacing={0.5}>
      <Typography sx={{ fontWeight: 600, wordBreak: 'break-word' }}>
        {vehicleLabel(t, request.vehicle)}
      </Typography>
      <Typography variant="body2" color="text.secondary">
        {t(`quote.types.${request.productType}.title`)} ·{' '}
        {t('quote.periodRange', {
          start: formatDate(request.startDate, i18n.language),
          end: formatDate(request.endDate, i18n.language),
        })}{' '}
        ({durationText(t, request.durationDays)})
      </Typography>
    </Stack>
  );
}

/** `<base>/:requestId`: as propostas ordenadas pela API, a comparação e as sem proposta. */
export function QuoteResultsPage({ mode }: { mode: QuoteMode }) {
  const { t, i18n } = useTranslation();
  const { requestId } = useParams();
  const navigate = useNavigate();
  const paths = quotePaths[mode];
  const query = useQuoteRequest(requestId);
  const [selected, setSelected] = useState<string[]>([]);
  const [refused, setRefused] = useState(false);

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return (
      <Container maxWidth="md" disableGutters>
        <QuoteLoadError error={query.error} paths={paths} onRetry={() => void query.refetch()} />
      </Container>
    );
  }

  const request = query.data;
  const redoTo = `${paths.wizard}?refazer=${request.id}`;
  const shown = request.quotes.filter((quote) => quote.status !== 'NOT_AVAILABLE');
  const offered = shown.filter((quote) => quote.status === 'OFFERED');
  const expired = shown.filter((quote) => quote.status === 'EXPIRED');
  const unavailable = request.quotes.filter((quote) => quote.status === 'NOT_AVAILABLE');
  const allExpired = offered.length === 0 && expired.length > 0;
  const nothing = offered.length === 0 && expired.length === 0;
  const cheapestId = offered.length >= 2 ? offered[0]?.id : undefined;
  const language = i18n.language;

  let intro: string;
  if (mode === 'broker') {
    intro = t('quoteResults.brokerText', { msisdn: formatMsisdn(request.clientMsisdn) });
  } else if (request.origin === 'BROKER') {
    intro = t('quoteResults.byBroker');
  } else {
    intro = t('quoteResults.savedText', {
      date: formatDate(latest(offered.map((q) => q.validUntil)), language),
    });
  }

  const compare = () =>
    navigate(
      `${paths.base}/${request.id}/comparar?ids=${selected.map(encodeURIComponent).join(',')}`,
    );

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3} sx={{ pb: selected.length > 0 ? 10 : 0 }}>
        <Typography variant="h4" component="h1">
          {t('quoteResults.title')}
        </Typography>
        <Stack
          direction={{ xs: 'column', sm: 'row' }}
          spacing={2}
          justifyContent="space-between"
          alignItems={{ xs: 'stretch', sm: 'flex-start' }}
        >
          <RequestHeading request={request} />
          {!nothing && (
            <Button variant="outlined" component={RouterLink} to={redoTo}>
              {t('quoteResults.change')}
            </Button>
          )}
        </Stack>
        {(!allExpired || mode === 'broker') && !nothing && (
          <Typography color="text.secondary">{intro}</Typography>
        )}

        {allExpired && (
          <Alert
            severity="warning"
            action={
              <Button variant="cta" component={RouterLink} to={redoTo}>
                {t('quote.simulate')}
              </Button>
            }
            sx={{ alignItems: 'center', flexWrap: 'wrap' }}
          >
            {t('quoteResults.expired', {
              date: formatDate(latest(expired.map((q) => q.validUntil)), language),
            })}
          </Alert>
        )}

        {nothing && (
          <Stack spacing={2} alignItems="flex-start">
            <Alert severity="info" sx={{ alignSelf: 'stretch' }}>
              {t('quoteResults.none')}
            </Alert>
            <Button variant="contained" component={RouterLink} to={redoTo}>
              {t('quoteResults.change')}
            </Button>
          </Stack>
        )}

        {shown.length > 0 && (
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: { xs: '1fr', md: 'repeat(2, minmax(0, 1fr))' },
              gap: 2,
            }}
          >
            {shown.map((quote) => (
              <QuoteCard
                key={quote.id}
                quote={quote}
                cheapest={quote.id === cheapestId}
                detailTo={`${paths.base}/${request.id}/${quote.id}`}
                compareChecked={selected.includes(quote.id)}
                onCompareChange={
                  quote.status === 'OFFERED' && !allExpired
                    ? (checked) => {
                        const result = toggleSelection(selected, quote.id, checked);
                        setSelected(result.selected);
                        setRefused(result.refused);
                      }
                    : undefined
                }
              />
            ))}
          </Box>
        )}

        <UnavailableList quotes={unavailable} />
      </Stack>
      {!allExpired && <CompareBar count={selected.length} onCompare={compare} />}
      <Snackbar
        open={refused}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        autoHideDuration={4000}
        onClose={() => setRefused(false)}
        message={t('quoteResults.maxCompare')}
      />
    </Container>
  );
}
