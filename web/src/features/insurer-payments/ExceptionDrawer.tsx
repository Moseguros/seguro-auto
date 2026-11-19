import Close from '@mui/icons-material/Close';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Dialog from '@mui/material/Dialog';
import Divider from '@mui/material/Divider';
import Drawer from '@mui/material/Drawer';
import IconButton from '@mui/material/IconButton';
import Link from '@mui/material/Link';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery } from '@tanstack/react-query';
import { useId } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  getPaymentException,
  type PaymentExceptionDetail,
  type PaymentExceptionIntent,
} from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { getProblem } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { Field, QueueError, QueueSkeleton } from '../insurer-proposals/common';
import { formatReference } from '../payments/formatReference';
import { paymentExceptionKey, paymentsPath, useCodeText } from './common';
import { formatStamp } from './format';
import { ResolveExceptionForm } from './ResolveExceptionForm';

function IntentLink({ intent }: { intent: PaymentExceptionIntent | undefined }) {
  const { i18n } = useTranslation();
  if (!intent) return null;
  return (
    <span>
      <Link component={RouterLink} to={`${paymentsPath}/${intent.id}`}>
        {intent.reference}
      </Link>
      {` · ${intent.targetLabel} · ${formatMoney(intent.amount, i18n.language)}`}
    </span>
  );
}

/**
 * Detalhe de uma excepção (S5-12 §4): `Drawer` à direita com 480 px, ou `Dialog` em ecrã inteiro
 * abaixo de 600 px. Com a excepção por resolver e `payment:resolve-exception` mostra o formulário
 * da resolução; sem a permissão fica só em leitura.
 */
export function ExceptionDrawer({
  exceptionId,
  onClose,
  onResolved,
}: {
  exceptionId: string | null;
  onClose(): void;
  onResolved(updated: PaymentExceptionDetail): void;
}) {
  const { t, i18n } = useTranslation();
  const codeText = useCodeText();
  const titleId = useId();
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const canResolve = usePermission(Permissions.paymentResolveException);
  const open = !!exceptionId;
  const query = useQuery({
    queryKey: paymentExceptionKey(exceptionId ?? ''),
    queryFn: ({ signal }) => getPaymentException(exceptionId ?? '', undefined, signal),
    enabled: open,
  });

  const e = query.data;
  let content;
  if (query.isLoading) content = <QueueSkeleton />;
  else if (query.isError) {
    content =
      getProblem(query.error).status === 404 ? (
        <Alert severity="warning">{t('paymentExceptions.drawer.notFound')}</Alert>
      ) : (
        <QueueError error={query.error} onRetry={() => void query.refetch()} />
      );
  } else if (e) {
    content = (
      <Stack spacing={2.5}>
        <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
          <Chip size="small" variant="outlined" label={codeText('paymentExceptionType', e.type)} />
          <Chip
            size="small"
            color={e.status === 'OPEN' ? 'warning' : 'success'}
            variant="outlined"
            label={t(`paymentExceptions.status.${e.status}`)}
            data-testid="exception-status"
          />
        </Stack>
        <Box
          component="dl"
          sx={{
            display: 'grid',
            gap: 1.5,
            gridTemplateColumns: 'repeat(2, minmax(0, 1fr))',
            m: 0,
          }}
        >
          <Field label={t('paymentExceptions.drawer.createdAt')}>{formatStamp(e.createdAt)}</Field>
          <Field label={t('paymentExceptions.drawer.entity')}>{e.entity}</Field>
          <Field label={t('paymentExceptions.drawer.reference')}>
            {e.reference ? formatReference(e.reference) : ''}
          </Field>
          <Field label={t('paymentExceptions.drawer.amount')}>
            {e.amount ? formatMoney(e.amount, i18n.language) : ''}
          </Field>
          <Field label={t('paymentExceptions.drawer.paidAt')}>{formatStamp(e.paidAt)}</Field>
          <Field label={t('paymentExceptions.drawer.bankTransactionId')}>
            {e.bankTransactionId}
          </Field>
          <Field label={t('paymentExceptions.drawer.file')}>{e.fileName}</Field>
          <Field label={t('paymentExceptions.drawer.line')}>{e.lineNo}</Field>
        </Box>
        {e.detail && (
          <Field label={t('paymentExceptions.drawer.detail')}>
            {codeText('paymentExceptionDetail', e.detail)}
          </Field>
        )}
        {e.intent && (
          <Field label={t('paymentExceptions.drawer.intent')}>
            <IntentLink intent={e.intent} />
          </Field>
        )}
        {e.status === 'RESOLVED' && (
          <>
            <Divider />
            <Box
              component="dl"
              sx={{
                display: 'grid',
                gap: 1.5,
                gridTemplateColumns: 'repeat(2, minmax(0, 1fr))',
                m: 0,
              }}
            >
              <Field label={t('paymentExceptions.drawer.resolution')}>
                {codeText('paymentResolutionDone', e.resolution)}
              </Field>
              <Field label={t('paymentExceptions.drawer.resolvedAt')}>
                {formatStamp(e.resolvedAt)}
              </Field>
              <Field label={t('paymentExceptions.drawer.resolvedBy')}>{e.resolvedBy}</Field>
              {e.linkedIntent && (
                <Field label={t('paymentExceptions.drawer.linkedIntent')}>
                  <IntentLink intent={e.linkedIntent} />
                </Field>
              )}
            </Box>
            <Field label={t('paymentExceptions.drawer.note')}>{e.note}</Field>
          </>
        )}
        {e.status === 'OPEN' && (
          <>
            <Divider />
            {canResolve ? (
              <ResolveExceptionForm
                key={`${e.id}-${e.version}`}
                exception={e}
                onResolved={onResolved}
                onStale={() => void query.refetch()}
              />
            ) : (
              <Alert severity="info">{t('paymentExceptions.drawer.readOnly')}</Alert>
            )}
          </>
        )}
      </Stack>
    );
  }

  const inner = (
    <Stack spacing={2} sx={{ p: 2, minWidth: 0 }}>
      <Stack direction="row" alignItems="center" justifyContent="space-between" spacing={1}>
        <Typography id={titleId} variant="h6" component="h2">
          {t('paymentExceptions.drawer.title')}
        </Typography>
        <IconButton onClick={onClose} aria-label={t('paymentExceptions.drawer.close')}>
          <Close />
        </IconButton>
      </Stack>
      {content}
    </Stack>
  );

  if (narrow) {
    return (
      <Dialog fullScreen open={open} onClose={onClose} aria-labelledby={titleId}>
        {inner}
      </Dialog>
    );
  }
  return (
    <Drawer
      anchor="right"
      open={open}
      onClose={onClose}
      slotProps={{
        paper: {
          sx: { width: 480, maxWidth: '100vw' },
          role: 'dialog',
          'aria-labelledby': titleId,
        } as object,
      }}
    >
      {inner}
    </Drawer>
  );
}
