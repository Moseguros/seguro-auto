import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { QuoteRequestSummaryResponse } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { formatMsisdn, offeredText, vehicleLabel } from './quoteFormat';

/** Cartão de um pedido de cotação numa lista; o toque abre os resultados. */
export function QuoteSummaryCard({
  summary,
  to,
  showClient = false,
}: {
  summary: QuoteRequestSummaryResponse;
  to: string;
  showClient?: boolean;
}) {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  return (
    <Card variant="outlined" elevation={0} data-testid="quote-summary-card">
      <CardActionArea component={RouterLink} to={to}>
        <CardContent>
          <Stack spacing={0.75}>
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap" alignItems="center">
              <Typography variant="subtitle1" component="span" sx={{ fontWeight: 600 }}>
                {vehicleLabel(t, summary.vehicle)}
              </Typography>
              {summary.origin === 'BROKER' && !showClient && (
                <Chip size="small" variant="outlined" label={t('quote.byBroker')} />
              )}
              {summary.expired && <Chip size="small" color="warning" label={t('quote.expired')} />}
            </Stack>
            {showClient && summary.clientMsisdn && (
              <Typography variant="body2">{formatMsisdn(summary.clientMsisdn)}</Typography>
            )}
            <Typography variant="body2" color="text.secondary">
              {t(`quote.types.${summary.productType}.title`)} ·{' '}
              {t('quote.startsOn', { date: formatDate(summary.startDate, language) })}
            </Typography>
            <Typography variant="body2">
              {offeredText(t, summary.offeredCount)}
              {summary.bestTotal && (
                <>
                  {' · '}
                  <span style={{ fontVariantNumeric: 'tabular-nums' }}>
                    {t('quote.bestFrom', { amount: formatMoney(summary.bestTotal) })}
                  </span>
                </>
              )}
            </Typography>
            {!summary.expired && summary.validUntil && (
              <Typography variant="body2" color="text.secondary">
                {t('quote.validUntil', { date: formatDate(summary.validUntil, language) })}
              </Typography>
            )}
          </Stack>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}
