import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import type { Money } from '../../api/generated';
import { formatMoney } from '../../format/money';
import { formatDateTimeNumeric } from '../../i18n/format';

/** Resumo do que se paga (S5-11 §3): proposta, seguradora, total, prazo e a frase fixa. */
export function PaymentSummaryCard({
  proposalNumber,
  insurerName,
  amount,
  expiresAt,
}: {
  proposalNumber: string;
  insurerName: string;
  amount: Money;
  expiresAt?: string;
}) {
  const { t, i18n } = useTranslation();
  return (
    <Card variant="outlined" elevation={0}>
      <CardContent>
        <Stack spacing={1}>
          <Typography variant="subtitle2" color="text.secondary">
            {t('payments.summary.proposal', { number: proposalNumber })} · {insurerName}
          </Typography>
          <Stack spacing={0}>
            <Typography variant="body2" color="text.secondary">
              {t('payments.summary.total')}
            </Typography>
            <Typography
              variant="h4"
              component="p"
              sx={{ fontVariantNumeric: 'tabular-nums', fontWeight: 700 }}
            >
              {formatMoney(amount, i18n.language)}
            </Typography>
          </Stack>
          {expiresAt && (
            <Typography variant="body2">
              {t('payments.summary.payBy', { date: formatDateTimeNumeric(expiresAt) })}
            </Typography>
          )}
          <Typography variant="body2" color="text.secondary">
            {t('payments.summary.direct', { insurer: insurerName })}
          </Typography>
        </Stack>
      </CardContent>
    </Card>
  );
}

/** Esqueleto do cartão de resumo e de duas opções, enquanto carrega. */
export function PaymentPageSkeleton() {
  return (
    <Stack spacing={2} aria-busy="true">
      <Skeleton variant="rounded" height={160} />
      <Skeleton variant="rounded" height={64} />
      <Skeleton variant="rounded" height={64} />
    </Stack>
  );
}
