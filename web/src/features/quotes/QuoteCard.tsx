import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Checkbox from '@mui/material/Checkbox';
import Chip from '@mui/material/Chip';
import FormControlLabel from '@mui/material/FormControlLabel';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { QuoteInsurer, QuoteResponse } from '../../api/generated';
import { useFileBlob } from '../../files/useFileBlob';
import { formatMoney } from '../../format/money';
import { formatDate } from '../../i18n/format';
import { chargesTotal, deductibleText, initials } from './quoteFormat';

/** Logótipo da seguradora (descarga autenticada) ou as iniciais. */
export function InsurerAvatar({ insurer, size = 40 }: { insurer: QuoteInsurer; size?: number }) {
  const logo = useFileBlob(insurer.logoFileId);
  return (
    <Avatar
      src={logo.url}
      alt={insurer.displayName}
      variant="rounded"
      sx={{ width: size, height: size, bgcolor: 'primary.main', fontSize: size * 0.4 }}
      slotProps={{ img: { sx: { objectFit: 'contain', bgcolor: 'background.paper' } } }}
    >
      {initials(insurer.displayName)}
    </Avatar>
  );
}

export interface QuoteCardProps {
  quote: QuoteResponse;
  detailTo: string;
  cheapest?: boolean;
  /** Sem `onCompareChange` a caixa "Comparar" não aparece. */
  compareChecked?: boolean;
  onCompareChange?(checked: boolean): void;
}

/** Cartão de uma proposta nos resultados. */
export function QuoteCard({
  quote,
  detailTo,
  cheapest = false,
  compareChecked = false,
  onCompareChange,
}: QuoteCardProps) {
  const { t, i18n } = useTranslation();
  const deductibles = [
    ...new Set(
      (quote.coverages ?? [])
        .map((coverage) => deductibleText(t, coverage.deductible))
        .filter((text): text is string => !!text),
    ),
  ];

  return (
    <Card data-testid="quote-card">
      <CardContent>
        <Stack spacing={1.5}>
          <Stack direction="row" spacing={1.5} alignItems="center">
            <InsurerAvatar insurer={quote.insurer} />
            <Stack sx={{ minWidth: 0, flexGrow: 1 }}>
              <Typography variant="subtitle1" component="h3" sx={{ fontWeight: 600 }}>
                {quote.insurer.displayName}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ wordBreak: 'break-word' }}>
                {quote.product.name}
              </Typography>
            </Stack>
          </Stack>

          <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
            {cheapest && (
              <Chip size="small" color="primary" variant="outlined" label={t('quote.cheapest')} />
            )}
            {quote.status === 'USED' && <Chip size="small" label={t('quote.status.USED')} />}
            {quote.status === 'EXPIRED' && (
              <Chip size="small" color="warning" label={t('quote.status.EXPIRED')} />
            )}
          </Stack>

          {quote.totalAmount && (
            <Stack spacing={0.25}>
              <Typography
                variant="h5"
                component="p"
                data-testid="quote-total"
                sx={{ fontWeight: 700, fontVariantNumeric: 'tabular-nums' }}
              >
                {formatMoney(quote.totalAmount)}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                {t('quote.chargesIncluded', { amount: formatMoney(chargesTotal(quote)) })}
              </Typography>
            </Stack>
          )}

          <Stack direction="row" spacing={0.75} useFlexGap flexWrap="wrap">
            {(quote.coverages ?? []).map((coverage) => (
              <Chip
                key={coverage.code}
                size="small"
                variant="outlined"
                label={t(`coverage.${coverage.code}`)}
              />
            ))}
          </Stack>
          {deductibles.map((text) => (
            <Typography key={text} variant="body2">
              {text}
            </Typography>
          ))}
          {quote.validUntil && (
            <Typography variant="body2" color="text.secondary">
              {t('quote.validUntil', { date: formatDate(quote.validUntil, i18n.language) })}
            </Typography>
          )}

          <Stack
            direction="row"
            spacing={1}
            justifyContent="space-between"
            alignItems="center"
            useFlexGap
            flexWrap="wrap"
          >
            <Button variant="outlined" component={RouterLink} to={detailTo}>
              {t('quote.viewDetail')}
            </Button>
            {onCompareChange && (
              <FormControlLabel
                control={
                  <Checkbox
                    checked={compareChecked}
                    onChange={(event) => onCompareChange(event.target.checked)}
                    inputProps={{
                      'aria-label': t('quote.compareWith', {
                        insurer: quote.insurer.displayName,
                        product: quote.product.name,
                      }),
                    }}
                  />
                }
                label={t('quote.compare')}
              />
            )}
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
