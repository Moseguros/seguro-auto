import ArrowBack from '@mui/icons-material/ArrowBack';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import type { ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { getInsurerPayment } from '../../api/generated';
import { getProblem } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { Field, QueueError, QueueSkeleton } from '../insurer-proposals/common';
import { formatReference } from '../payments/formatReference';
import {
  exceptionLink,
  insurerPaymentKey,
  paymentsPath,
  PaymentStatusChip,
  useCodeText,
} from './common';
import { formatStamp } from './format';

function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <Paper variant="outlined" component="section" aria-label={title} sx={{ p: 2, minWidth: 0 }}>
      <Typography variant="h6" component="h2" sx={{ mb: 1.5 }}>
        {title}
      </Typography>
      {children}
    </Paper>
  );
}

function FieldList({ children }: { children: ReactNode }) {
  return (
    <Box
      component="dl"
      sx={{
        display: 'grid',
        gap: 1.5,
        gridTemplateColumns: { xs: 'minmax(0, 1fr)', sm: 'repeat(3, minmax(0, 1fr))' },
        m: 0,
      }}
    >
      {children}
    </Box>
  );
}

/** `/seguradora/pagamentos/:id`: resumo, referência bancária, histórico e excepções (S5-12 §3). */
export function PaymentDetailPage() {
  const { id = '' } = useParams();
  const { t, i18n } = useTranslation();
  const codeText = useCodeText();
  const query = useQuery({
    queryKey: insurerPaymentKey(id),
    queryFn: ({ signal }) => getInsurerPayment(id, undefined, signal),
  });

  const back = (
    <div>
      <Button component={RouterLink} to={paymentsPath} startIcon={<ArrowBack />} size="small">
        {t('insurerPayments.detail.back')}
      </Button>
    </div>
  );

  if (query.isLoading) {
    return (
      <Stack spacing={3}>
        {back}
        <QueueSkeleton />
      </Stack>
    );
  }
  if (query.isError) {
    const notFound = getProblem(query.error).status === 404;
    return (
      <Stack spacing={3}>
        {back}
        {notFound ? (
          <Alert severity="warning">{t('insurerPayments.detail.notFound')}</Alert>
        ) : (
          <QueueError error={query.error} onRetry={() => void query.refetch()} />
        )}
      </Stack>
    );
  }
  const p = query.data;
  if (!p) return null;

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      {back}
      <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap" useFlexGap>
        <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-all' }}>
          {p.reference}
        </Typography>
        <PaymentStatusChip status={p.status} />
      </Stack>
      <Section title={t('insurerPayments.detail.summary')}>
        <FieldList>
          <Field label={t('insurerPayments.detail.reference')}>{p.reference}</Field>
          <Field label={t('insurerPayments.detail.status')}>{t(`paymentStatus.${p.status}`)}</Field>
          <Field label={t('insurerPayments.detail.method')}>{t(`paymentMethod.${p.method}`)}</Field>
          <Field label={t('insurerPayments.detail.amount')}>
            <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
              {formatMoney(p.amount, i18n.language)}
            </Box>
          </Field>
          <Field label={t('insurerPayments.detail.proposal')}>{p.targetLabel}</Field>
          <Field label={t('insurerPayments.detail.msisdn')}>{p.payerMsisdnMasked}</Field>
          <Field label={t('insurerPayments.detail.createdAt')}>{formatStamp(p.createdAt)}</Field>
          <Field label={t('insurerPayments.detail.expiresAt')}>{formatStamp(p.expiresAt)}</Field>
          <Field label={t('insurerPayments.detail.succeededAt')}>
            {formatStamp(p.succeededAt)}
          </Field>
          <Field label={t('insurerPayments.detail.confirmedBy')}>
            {codeText('paymentConfirmedBy', p.confirmedBy)}
          </Field>
          <Field label={t('insurerPayments.detail.providerTxId')}>{p.providerTxId}</Field>
          {p.failureCode && (
            <Field label={t('insurerPayments.detail.failure')}>
              {codeText('paymentFailure', p.failureCode)}
            </Field>
          )}
        </FieldList>
      </Section>
      {p.bankReference && (
        <Section title={t('insurerPayments.detail.bankReference')}>
          <FieldList>
            <Field label={t('insurerPayments.detail.entity')}>{p.bankReference.entity}</Field>
            <Field label={t('insurerPayments.detail.bankRef')}>
              <Box component="span" sx={{ fontVariantNumeric: 'tabular-nums' }}>
                {formatReference(p.bankReference.reference)}
              </Box>
            </Field>
            <Field label={t('insurerPayments.detail.referenceStatus')}>
              {codeText('referenceStatus', p.bankReference.status)}
            </Field>
            <Field label={t('insurerPayments.detail.paidAt')}>
              {formatStamp(p.bankReference.paidAt)}
            </Field>
            <Field label={t('insurerPayments.detail.bankTransactionId')}>
              {p.bankReference.bankTransactionId}
            </Field>
          </FieldList>
        </Section>
      )}
      <Section title={t('insurerPayments.detail.history')}>
        {p.events.length === 0 ? (
          <Typography variant="body2">{t('insurerPayments.detail.noHistory')}</Typography>
        ) : (
          <Stack component="ol" spacing={1.5} sx={{ listStyle: 'none', p: 0, m: 0 }}>
            {p.events.map((event, index) => (
              <Stack
                component="li"
                key={`${event.receivedAt}-${index}`}
                spacing={0.25}
                sx={{ borderLeft: 2, borderColor: 'divider', pl: 1.5 }}
              >
                <Typography variant="body2" fontWeight={600}>
                  {codeText('paymentEventType', event.type)}
                  {(event.fromStatus || event.toStatus) &&
                    ` · ${event.fromStatus ? t(`paymentStatus.${event.fromStatus}`) : '—'} → ${
                      event.toStatus ? t(`paymentStatus.${event.toStatus}`) : '—'
                    }`}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {formatStamp(event.receivedAt)}
                </Typography>
              </Stack>
            ))}
          </Stack>
        )}
      </Section>
      {p.exceptionIds.length > 0 && (
        <Section title={t('insurerPayments.detail.exceptions')}>
          <Stack component="ul" spacing={1} sx={{ pl: 2, m: 0 }}>
            {p.exceptionIds.map((exceptionId, index) => (
              <li key={exceptionId}>
                <Link component={RouterLink} to={exceptionLink(exceptionId)}>
                  {t('insurerPayments.detail.exception', { n: index + 1 })}
                </Link>
              </li>
            ))}
          </Stack>
        </Section>
      )}
    </Stack>
  );
}
