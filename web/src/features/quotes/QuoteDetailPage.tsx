import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Container from '@mui/material/Container';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Typography from '@mui/material/Typography';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { LoadingScreen } from '../../auth/RequireAuth';
import { FileOpenButton } from '../../files/FileOpenButton';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { quotePaths, type QuoteMode } from './config';
import { InsurerAvatar } from './QuoteCard';
import { chargeLabel, deductibleValue, durationText } from './quoteFormat';
import { QuoteLoadError, useQuoteRequest } from './quoteQueries';
import { useUnavailableReason } from './UnavailableList';

const numberCell = { fontVariantNumeric: 'tabular-nums', whiteSpace: 'nowrap' } as const;

/** `<base>/:requestId/:quoteId`: coberturas, encargos, total, período e documentos. */
export function QuoteDetailPage({ mode }: { mode: QuoteMode }) {
  const { t, i18n } = useTranslation();
  const { requestId, quoteId } = useParams();
  const paths = quotePaths[mode];
  const query = useQuoteRequest(requestId);
  const reason = useUnavailableReason();
  const [fileError, setFileError] = useState<string | null>(null);
  const language = i18n.language;

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return (
      <QuoteLoadError error={query.error} paths={paths} onRetry={() => void query.refetch()} />
    );
  }
  const request = query.data;
  const resultsPath = `${paths.base}/${request.id}`;
  const quote = request.quotes.find((q) => q.id === quoteId);
  const back = (
    <Button variant="text" component={RouterLink} to={resultsPath} sx={{ alignSelf: 'flex-start' }}>
      {t('compare.back')}
    </Button>
  );
  if (!quote) {
    return (
      <Stack spacing={2}>
        <Alert severity="warning">{t('quote.notFound')}</Alert>
        {back}
      </Stack>
    );
  }

  const header = (
    <Stack direction="row" spacing={1.5} alignItems="center">
      <InsurerAvatar insurer={quote.insurer} size={48} />
      <Stack sx={{ minWidth: 0 }}>
        <Typography variant="h5" component="h1" sx={{ wordBreak: 'break-word' }}>
          {quote.insurer.displayName}
        </Typography>
        <Typography color="text.secondary">
          {quote.product.name} · {t('quote.detail.version', { n: quote.product.versionNo })}
        </Typography>
      </Stack>
    </Stack>
  );
  const statusChip =
    quote.status === 'OFFERED' ? null : (
      <Chip
        size="small"
        color={quote.status === 'USED' ? 'default' : 'warning'}
        label={t(`quote.status.${quote.status}`)}
        sx={{ alignSelf: 'flex-start' }}
      />
    );

  if (quote.status === 'NOT_AVAILABLE') {
    return (
      <Container maxWidth="md" disableGutters>
        <Stack spacing={3}>
          {header}
          {statusChip}
          <Alert severity="info">{reason(quote)}</Alert>
          {back}
        </Stack>
      </Container>
    );
  }

  const totals: { key: string; label: string; value?: { amount: number }; strong?: boolean }[] = [
    { key: 'commercial', label: t('quote.commercialPremium'), value: quote.commercialPremium },
    ...(quote.charges ?? []).map((charge) => ({
      key: `charge-${charge.code}`,
      label: chargeLabel(charge),
      value: charge.amount,
    })),
    {
      key: 'rounding',
      label: t('quote.roundingAdjustment'),
      value: quote.roundingAdjustment ?? { amount: 0 },
    },
    { key: 'total', label: t('quote.totalPremium'), value: quote.totalAmount, strong: true },
  ];

  return (
    <Container maxWidth="md" disableGutters>
      <Stack spacing={3}>
        {header}
        {statusChip}
        <Card>
          <TableContainer sx={{ overflowX: 'auto' }}>
            <Table size="small" aria-label={t('quote.detail.coverages')}>
              <TableHead>
                <TableRow>
                  <TableCell>{t('quote.detail.coverage')}</TableCell>
                  <TableCell>{t('quote.detail.capital')}</TableCell>
                  <TableCell>{t('quote.detail.deductible')}</TableCell>
                  <TableCell align="right">{t('quote.detail.premium')}</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {(quote.coverages ?? []).map((coverage) => (
                  <TableRow key={coverage.code}>
                    <TableCell>{t(`coverage.${coverage.code}`)}</TableCell>
                    <TableCell sx={numberCell}>
                      {coverage.capital ? formatMoney(coverage.capital) : '—'}
                    </TableCell>
                    <TableCell>{deductibleValue(t, coverage.deductible) ?? '—'}</TableCell>
                    <TableCell align="right" sx={numberCell}>
                      {formatMoney(coverage.premium)}
                    </TableCell>
                  </TableRow>
                ))}
                {totals.map((row) => (
                  <TableRow key={row.key}>
                    <TableCell
                      colSpan={3}
                      component="th"
                      scope="row"
                      sx={{ fontWeight: row.strong ? 700 : 400 }}
                    >
                      {row.label}
                    </TableCell>
                    <TableCell
                      align="right"
                      sx={{ ...numberCell, fontWeight: row.strong ? 700 : 400 }}
                    >
                      {row.value ? formatMoney(row.value) : '—'}
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Card>

        <Card>
          <CardContent>
            <Stack spacing={1}>
              <Typography>
                <strong>{t('quote.detail.period')}:</strong>{' '}
                {t('quote.periodRange', {
                  start: formatDate(request.startDate, language),
                  end: formatDate(request.endDate, language),
                })}{' '}
                ({durationText(t, request.durationDays)})
              </Typography>
              {quote.validUntil && (
                <Typography>
                  {t('quote.validUntil', { date: formatDate(quote.validUntil, language) })}
                </Typography>
              )}
              <Typography variant="subtitle2" sx={{ pt: 1 }}>
                {t('compare.documents')}
              </Typography>
              <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                <FileOpenButton
                  variant="outlined"
                  fileId={quote.product.generalConditionsFileId ?? undefined}
                  label={t('quote.generalConditions')}
                  onError={setFileError}
                />
                <FileOpenButton
                  variant="outlined"
                  fileId={quote.product.preContractInfoFileId ?? undefined}
                  label={t('quote.preContractInfo')}
                  onError={setFileError}
                />
              </Stack>
            </Stack>
          </CardContent>
        </Card>

        <Typography color="text.secondary">{t('quote.detail.comingSoon')}</Typography>
        {back}
      </Stack>
      <Snackbar
        open={!!fileError}
        autoHideDuration={4000}
        onClose={() => setFileError(null)}
        message={fileError}
      />
    </Container>
  );
}
