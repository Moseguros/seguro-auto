import ExpandMore from '@mui/icons-material/ExpandMore';
import Accordion from '@mui/material/Accordion';
import AccordionDetails from '@mui/material/AccordionDetails';
import AccordionSummary from '@mui/material/AccordionSummary';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { QuoteResponse } from '../../api/generated';
import { InsurerAvatar } from './QuoteCard';

/** Texto do motivo de uma cotação sem proposta. */
export function useUnavailableReason() {
  const { t } = useTranslation();
  return (quote: QuoteResponse) =>
    quote.unavailableReason
      ? t(`quoteUnavailable.${quote.unavailableReason}`)
      : t('quoteUnavailable.EVALUATION_ERROR');
}

/** Secção recolhida "Sem proposta ({n})": seguradora, produto e motivo. */
export function UnavailableList({ quotes }: { quotes: QuoteResponse[] }) {
  const { t } = useTranslation();
  const reason = useUnavailableReason();
  if (quotes.length === 0) return null;
  return (
    <Accordion disableGutters variant="outlined" elevation={0}>
      <AccordionSummary expandIcon={<ExpandMore />}>
        <Typography>{t('quoteResults.unavailable', { count: quotes.length })}</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Stack spacing={2}>
          {quotes.map((quote) => (
            <Stack
              key={quote.id}
              direction="row"
              spacing={1.5}
              alignItems="flex-start"
              data-testid="unavailable-quote"
            >
              <InsurerAvatar insurer={quote.insurer} size={32} />
              <Stack sx={{ minWidth: 0 }}>
                <Typography variant="subtitle2">
                  {quote.insurer.displayName} · {quote.product.name}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {reason(quote)}
                </Typography>
              </Stack>
            </Stack>
          ))}
        </Stack>
      </AccordionDetails>
    </Accordion>
  );
}
