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
import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import type { TFunction } from 'i18next';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, Navigate, useParams, useSearchParams } from 'react-router-dom';
import type { QuoteResponse } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { FileOpenButton } from '../../files/FileOpenButton';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { QUOTE_MAX_COMPARE, quotePaths, type QuoteMode } from './config';
import { coverageCodes } from './quoteRules';
import { InsurerAvatar } from './QuoteCard';
import { chargeLabel, deductibleValue } from './quoteFormat';
import { QuoteLoadError, useQuoteRequest } from './quoteQueries';
import { RequestHeading } from './QuoteResultsPage';

export interface CompareRow {
  key: string;
  label: string;
  /** Valor de cada coluna, para ver se as colunas diferem. */
  raw: string[];
  cells: ReactNode[];
  strong?: boolean;
  /** Linha que não entra nas "principais diferenças" (documentos). */
  noDiff?: boolean;
}

/** A linha tem valores diferentes entre as colunas. */
export function isDifferent(row: CompareRow): boolean {
  return !row.noDiff && new Set(row.raw).size > 1;
}

/** Linhas da comparação, pela ordem do S3-04 §4. */
export function compareRows(t: TFunction, quotes: QuoteResponse[], language: string): CompareRow[] {
  const money = (value: { amount: number } | undefined) => (value ? formatMoney(value) : '—');
  const rows: CompareRow[] = [
    {
      key: 'insurer',
      label: t('compare.insurer'),
      raw: quotes.map((q) => q.insurer.id),
      cells: quotes.map((q) => (
        <Stack direction="row" spacing={1} alignItems="center" key={q.id}>
          <InsurerAvatar insurer={q.insurer} size={28} />
          <span>{q.insurer.displayName}</span>
        </Stack>
      )),
    },
    {
      key: 'product',
      label: t('compare.product'),
      raw: quotes.map((q) => q.product.id),
      cells: quotes.map((q) => q.product.name),
    },
    {
      key: 'total',
      label: t('quote.totalPremium'),
      raw: quotes.map((q) => String(q.totalAmount?.amount)),
      cells: quotes.map((q) => money(q.totalAmount)),
      strong: true,
    },
    {
      key: 'commercial',
      label: t('quote.commercialPremium'),
      raw: quotes.map((q) => String(q.commercialPremium?.amount)),
      cells: quotes.map((q) => money(q.commercialPremium)),
    },
  ];

  const chargeCodes: { code: string; label: string }[] = [];
  for (const quote of quotes) {
    for (const charge of quote.charges ?? []) {
      if (!chargeCodes.some((c) => c.code === charge.code)) {
        chargeCodes.push({ code: charge.code, label: chargeLabel(charge) });
      }
    }
  }
  for (const { code, label } of chargeCodes) {
    const values = quotes.map((q) => q.charges?.find((c) => c.code === code)?.amount);
    rows.push({
      key: `charge-${code}`,
      label,
      raw: values.map((v) => String(v?.amount ?? '—')),
      cells: values.map((v) => money(v)),
    });
  }

  const codes = coverageCodes.filter((code) =>
    quotes.some((q) => q.coverages?.some((c) => c.code === code)),
  );
  for (const code of codes) {
    const texts = quotes.map((q) => {
      const coverage = q.coverages?.find((c) => c.code === code);
      if (!coverage) return t('compare.notIncluded');
      return [
        coverage.capital ? formatMoney(coverage.capital) : null,
        t('compare.deductible', {
          value: deductibleValue(t, coverage.deductible) ?? t('compare.noDeductible'),
        }),
        formatMoney(coverage.premium),
      ]
        .filter(Boolean)
        .join(' · ');
    });
    rows.push({ key: `coverage-${code}`, label: t(`coverage.${code}`), raw: texts, cells: texts });
  }

  const valid = quotes.map((q) => formatDate(q.validUntil, language));
  rows.push({ key: 'validUntil', label: t('compare.validUntil'), raw: valid, cells: valid });
  return rows;
}

/** `<base>/:requestId/comparar?ids=a,b,c`: 2 ou 3 propostas lado a lado. */
export function QuoteComparePage({ mode }: { mode: QuoteMode }) {
  const { t, i18n } = useTranslation();
  const theme = useTheme();
  const narrow = !useMediaQuery(theme.breakpoints.up('sm'));
  const { requestId } = useParams();
  const [params] = useSearchParams();
  const paths = quotePaths[mode];
  const query = useQuoteRequest(requestId);
  const [fileError, setFileError] = useState<string | null>(null);

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    return (
      <QuoteLoadError error={query.error} paths={paths} onRetry={() => void query.refetch()} />
    );
  }
  const request = query.data;
  const resultsPath = `${paths.base}/${request.id}`;
  const ids = [...new Set((params.get('ids') ?? '').split(',').filter(Boolean))];
  const quotes = request.quotes.filter((q) => ids.includes(q.id) && q.status === 'OFFERED');
  if (ids.length < 2 || ids.length > QUOTE_MAX_COMPARE || quotes.length !== ids.length) {
    return <Navigate to={resultsPath} replace />;
  }

  const cheapest = quotes.reduce((best, q) =>
    (q.totalAmount?.amount ?? Infinity) < (best.totalAmount?.amount ?? Infinity) ? q : best,
  );
  const rows = compareRows(t, quotes, i18n.language);
  const documents = (quote: QuoteResponse) => (
    <Stack spacing={0.5} alignItems="flex-start">
      <FileOpenButton
        fileId={quote.product.generalConditionsFileId ?? undefined}
        label={t('quote.generalConditions')}
        onError={setFileError}
      />
      <FileOpenButton
        fileId={quote.product.preContractInfoFileId ?? undefined}
        label={t('quote.preContractInfo')}
        onError={setFileError}
      />
    </Stack>
  );
  const detail = (quote: QuoteResponse) => (
    <Button variant="outlined" component={RouterLink} to={`${resultsPath}/${quote.id}`}>
      {t('quote.viewDetail')}
    </Button>
  );
  const cheapestChip = (
    <Chip size="small" color="primary" variant="outlined" label={t('quote.cheapest')} />
  );
  const diffSx = { bgcolor: 'background.default' };

  let body;
  if (narrow) {
    body = (
      <Stack spacing={2} data-testid="compare-cards">
        {quotes.map((quote, column) => (
          <Card key={quote.id} data-testid="compare-card">
            <CardContent>
              <Stack spacing={1.25}>
                {quote.id === cheapest.id && cheapestChip}
                {rows.map((row) => {
                  const different = isDifferent(row);
                  return (
                    <Stack
                      key={row.key}
                      spacing={0.25}
                      data-different={different || undefined}
                      sx={{ ...(different ? diffSx : {}), px: 1, py: 0.5, borderRadius: 1 }}
                    >
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        sx={{ fontWeight: different ? 700 : 400 }}
                      >
                        {row.label}
                      </Typography>
                      <Typography
                        component="div"
                        sx={{
                          fontWeight: row.strong ? 700 : 400,
                          fontVariantNumeric: 'tabular-nums',
                          wordBreak: 'break-word',
                        }}
                      >
                        {row.cells[column]}
                      </Typography>
                    </Stack>
                  );
                })}
                <Stack spacing={0.25} sx={{ px: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    {t('compare.documents')}
                  </Typography>
                  {documents(quote)}
                </Stack>
                {detail(quote)}
              </Stack>
            </CardContent>
          </Card>
        ))}
      </Stack>
    );
  } else {
    const sticky = {
      position: 'sticky',
      left: 0,
      zIndex: 1,
      bgcolor: 'background.paper',
      minWidth: 140,
    } as const;
    body = (
      <TableContainer
        component={Card}
        sx={{ overflowX: 'auto', maxWidth: '100%' }}
        data-testid="compare-table"
      >
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={sticky} />
              {quotes.map((quote) => (
                <TableCell key={quote.id} sx={{ minWidth: 180 }}>
                  {quote.id === cheapest.id && cheapestChip}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row) => {
              const different = isDifferent(row);
              return (
                <TableRow
                  key={row.key}
                  data-different={different || undefined}
                  sx={different ? diffSx : undefined}
                >
                  <TableCell
                    component="th"
                    scope="row"
                    sx={{
                      ...sticky,
                      ...(different ? diffSx : {}),
                      fontWeight: different ? 700 : 400,
                    }}
                  >
                    {row.label}
                  </TableCell>
                  {row.cells.map((cell, index) => (
                    <TableCell
                      key={quotes[index]?.id ?? index}
                      sx={{
                        fontWeight: row.strong ? 700 : 400,
                        fontVariantNumeric: 'tabular-nums',
                      }}
                    >
                      {cell}
                    </TableCell>
                  ))}
                </TableRow>
              );
            })}
            <TableRow>
              <TableCell component="th" scope="row" sx={sticky}>
                {t('compare.documents')}
              </TableCell>
              {quotes.map((quote) => (
                <TableCell key={quote.id}>{documents(quote)}</TableCell>
              ))}
            </TableRow>
            <TableRow>
              <TableCell sx={sticky} />
              {quotes.map((quote) => (
                <TableCell key={quote.id}>{detail(quote)}</TableCell>
              ))}
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    );
  }

  return (
    <Container maxWidth="lg" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('compare.title')}
        </Typography>
        <RequestHeading request={request} />
        <Typography variant="body2" color="text.secondary">
          {t('compare.differencesHint')}
        </Typography>
        {body}
        <Button
          variant="text"
          component={RouterLink}
          to={resultsPath}
          sx={{ alignSelf: 'flex-start' }}
        >
          {t('compare.back')}
        </Button>
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
