import ArrowBack from '@mui/icons-material/ArrowBack';
import Button from '@mui/material/Button';
import Snackbar from '@mui/material/Snackbar';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import {
  listInsurerPaymentMethods,
  type PaymentMethodResponse,
  type PaymentMethodsResponse,
} from '../../api/generated';
import { QueueError, QueueSkeleton } from '../insurer-proposals/common';
import { paymentFileSourceKey } from '../insurer-payments/common';
import { PaymentMethodCard } from './PaymentMethodCard';

export const paymentMethodsKey = ['insurer-payment-methods'] as const;

/** `/seguradora/configuracao/pagamentos`: as formas de pagamento da seguradora (S5-12 §2). */
export function PaymentMethodsSettingsPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const narrow = useMediaQuery('(max-width:899.95px)', { noSsr: true });
  const [open, setOpen] = useState<string | false>(false);
  const [notice, setNotice] = useState<{ text: string; key: number } | null>(null);

  const query = useQuery({
    queryKey: paymentMethodsKey,
    queryFn: ({ signal }) => listInsurerPaymentMethods(undefined, signal),
    // Recarregar com alterações por gravar apagava-as (o cartão é recriado por versão).
    refetchOnWindowFocus: false,
  });
  const methods = query.data?.methods ?? [];

  const replace = (updated: PaymentMethodResponse) => {
    queryClient.setQueryData<PaymentMethodsResponse>(paymentMethodsKey, (current) => ({
      methods: (current?.methods ?? []).map((m) => (m.method === updated.method ? updated : m)),
    }));
    void queryClient.invalidateQueries({ queryKey: paymentFileSourceKey });
  };

  let body;
  if (query.isLoading) body = <QueueSkeleton />;
  else if (query.isError) {
    body = <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  } else {
    body = (
      <Stack spacing={narrow ? 0 : 3}>
        {methods.map((item) => (
          <PaymentMethodCard
            key={`${item.method}-${item.version ?? 'new'}`}
            item={item}
            narrow={narrow}
            expanded={open === item.method}
            onToggle={() => setOpen((current) => (current === item.method ? false : item.method))}
            onNotice={(text) => setNotice({ text, key: Date.now() })}
            onReplace={replace}
            onReload={() => void query.refetch()}
          />
        ))}
      </Stack>
    );
  }

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <div>
        <Button
          component={RouterLink}
          to="/seguradora/configuracao"
          startIcon={<ArrowBack />}
          size="small"
        >
          {t('paymentSettingsPage.back')}
        </Button>
      </div>
      <Typography variant="h4" component="h1">
        {t('paymentSettingsPage.title')}
      </Typography>
      {body}
      <Snackbar
        key={notice?.key}
        open={!!notice}
        autoHideDuration={6000}
        onClose={() => setNotice(null)}
        message={notice?.text}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      />
    </Stack>
  );
}
