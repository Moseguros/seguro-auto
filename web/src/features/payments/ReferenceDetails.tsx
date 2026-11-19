import Download from '@mui/icons-material/Download';
import Sms from '@mui/icons-material/Sms';
import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import CircularProgress from '@mui/material/CircularProgress';
import Divider from '@mui/material/Divider';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { createReferenceDocument, type IntentView } from '../../api/generated';
import { useAuth } from '../../auth/useAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { downloadFile } from '../../files/downloadFile';
import { formatMoney } from '../../format/money';
import { formatDateTimeNumeric } from '../../i18n/format';
import { amountCopyValue, formatReference, referenceCopyValue } from './formatReference';
import { mozNational, nationalFromMsisdn } from './PayerMsisdnField';
import { SendReferenceSmsDialog } from './SendReferenceSmsDialog';

interface Row {
  key: string;
  label: string;
  display: string;
  copy: string;
  note?: string;
}

/**
 * Entidade, referência e montante para pagar na rede Ponto24 (S5-11 §4): cada um com "Copiar",
 * o prazo, como pagar, o aviso da confirmação, o PDF e o envio por SMS.
 */
export function ReferenceDetails({
  intent,
  onReferenceInactive,
}: {
  intent: IntentView;
  /** A referência deixou de estar activa: quem mostra relê o pagamento. */
  onReferenceInactive: () => void;
}) {
  const { t, i18n } = useTranslation();
  const { me } = useAuth();
  const action = intent.nextAction;
  const entity = action.entity ?? '';
  const reference = action.reference ?? '';
  const amount = action.amount ?? intent.amount;
  const [toast, setToast] = useState<string | null>(null);
  const [smsOpen, setSmsOpen] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const rows: Row[] = [
    { key: 'entity', label: t('payments.reference.entity'), display: entity, copy: entity },
    {
      key: 'reference',
      label: t('payments.reference.reference'),
      display: formatReference(reference),
      copy: referenceCopyValue(reference),
    },
    {
      key: 'amount',
      label: t('payments.reference.amount'),
      display: formatMoney(amount, i18n.language),
      copy: amountCopyValue(amount),
      note: t('payments.reference.exactAmount'),
    },
  ];

  const copy = async (value: string) => {
    try {
      await navigator.clipboard.writeText(value);
      setToast(t('payments.reference.copied'));
    } catch {
      setToast(t('payments.reference.copyFailed'));
    }
  };

  const pdf = useMutation({
    mutationFn: async () => {
      const { fileId } = await createReferenceDocument(intent.id);
      await downloadFile(fileId, `referencia-${referenceCopyValue(reference)}.pdf`);
    },
    onError: (err) => {
      const problem = getProblem(err);
      if (problem.code === 'REFERENCE_NOT_ACTIVE' || problem.code === 'NOT_A_REFERENCE_PAYMENT') {
        onReferenceInactive();
      }
      setError(problemMessage(t, problem));
    },
  });

  const accountMsisdn = nationalFromMsisdn(me?.msisdn);

  return (
    <Stack spacing={2}>
      <Card variant="outlined" elevation={0}>
        <CardContent>
          <Stack spacing={1.5} divider={<Divider flexItem />}>
            {rows.map((row) => (
              <Stack key={row.key} spacing={0.5}>
                <Stack direction="row" alignItems="center" spacing={1}>
                  <Box sx={{ flex: 1, minWidth: 0 }}>
                    <Typography variant="body2" color="text.secondary">
                      {row.label}
                    </Typography>
                    <Typography
                      data-testid={`reference-${row.key}`}
                      sx={{
                        fontSize: 24,
                        fontWeight: 700,
                        fontVariantNumeric: 'tabular-nums',
                        wordBreak: 'break-word',
                      }}
                    >
                      {row.display}
                    </Typography>
                  </Box>
                  <Button
                    variant="outlined"
                    onClick={() => void copy(row.copy)}
                    aria-label={t('payments.reference.copy', { label: row.label })}
                    sx={{ minWidth: 48, minHeight: 48, flexShrink: 0 }}
                  >
                    {t('common.copy')}
                  </Button>
                </Stack>
                {row.note && (
                  <Typography variant="body2" color="warning.dark">
                    {row.note}
                  </Typography>
                )}
              </Stack>
            ))}
          </Stack>
        </CardContent>
      </Card>
      {action.validUntil && (
        <Typography>
          {t('payments.reference.validUntil', { date: formatDateTimeNumeric(action.validUntil) })}
        </Typography>
      )}
      <Typography>{t('payments.reference.howTo')}</Typography>
      <Alert severity="info">{t('payments.reference.confirmationInfo')}</Alert>
      {error && (
        <Alert severity="error" role="alert" onClose={() => setError(null)}>
          {error}
        </Alert>
      )}
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={1}>
        <Button
          variant="outlined"
          startIcon={pdf.isPending ? <CircularProgress size={16} /> : <Download />}
          disabled={pdf.isPending}
          onClick={() => {
            setError(null);
            pdf.mutate();
          }}
        >
          {t('payments.reference.downloadPdf')}
        </Button>
        <Button
          variant="outlined"
          startIcon={<Sms />}
          onClick={() => {
            setError(null);
            setSmsOpen(true);
          }}
        >
          {t('payments.reference.sendSms')}
        </Button>
      </Stack>
      {smsOpen && (
        <SendReferenceSmsDialog
          open
          intentId={intent.id}
          defaultMsisdn={mozNational.test(accountMsisdn) ? accountMsisdn : ''}
          onClose={() => setSmsOpen(false)}
          onSent={(sentTo) => setToast(t('payments.sms.sent', { sentTo }))}
          onReferenceInactive={onReferenceInactive}
        />
      )}
      <Snackbar
        open={toast !== null}
        autoHideDuration={4000}
        onClose={() => setToast(null)}
        message={toast}
      />
    </Stack>
  );
}
