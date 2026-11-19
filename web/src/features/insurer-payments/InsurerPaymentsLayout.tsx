import Chip from '@mui/material/Chip';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, Outlet, useLocation } from 'react-router-dom';
import { listPaymentExceptions } from '../../api/generated';
import { exceptionsPath, filesPath, openExceptionsCountKey, paymentsPath } from './common';

const tabs = [
  { key: 'payments', path: paymentsPath },
  { key: 'exceptions', path: exceptionsPath },
  { key: 'files', path: filesPath },
] as const;

/** Número de excepções por resolver (etiqueta do separador "Excepções"). */
export function useOpenExceptionsCount(): number | undefined {
  const query = useQuery({
    queryKey: openExceptionsCountKey,
    queryFn: ({ signal }) => listPaymentExceptions({ status: 'OPEN', size: 1 }, undefined, signal),
  });
  return query.data?.totalElements;
}

/**
 * `/seguradora/pagamentos`: separadores "Pagamentos", "Excepções" (com o número de excepções por
 * resolver) e "Ficheiros". A 360 px os separadores fazem *scroll* horizontal.
 */
export function InsurerPaymentsLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const openCount = useOpenExceptionsCount();
  const current = tabs.find((tab) => tab.path === pathname)?.path ?? false;

  return (
    <Stack spacing={3} sx={{ minWidth: 0 }}>
      <Typography variant="h4" component="h1">
        {t('insurerPayments.title')}
      </Typography>
      <Tabs
        value={current}
        variant="scrollable"
        scrollButtons="auto"
        allowScrollButtonsMobile
        aria-label={t('insurerPayments.title')}
        sx={{ borderBottom: 1, borderColor: 'divider', maxWidth: '100%' }}
      >
        {tabs.map((tab) => (
          <Tab
            key={tab.key}
            value={tab.path}
            component={RouterLink}
            to={tab.path}
            label={
              tab.key === 'exceptions' && openCount ? (
                <Stack direction="row" spacing={1} alignItems="center" component="span">
                  <span>{t('insurerPayments.tabs.exceptions')}</span>
                  <Chip
                    size="small"
                    color="warning"
                    label={openCount}
                    aria-label={t('insurerPayments.openExceptions', { count: openCount })}
                    data-testid="open-exceptions-count"
                    component="span"
                  />
                </Stack>
              ) : (
                t(`insurerPayments.tabs.${tab.key}`)
              )
            }
          />
        ))}
      </Tabs>
      <Outlet />
    </Stack>
  );
}
