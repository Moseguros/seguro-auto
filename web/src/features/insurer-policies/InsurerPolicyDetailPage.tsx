import ArrowBack from '@mui/icons-material/ArrowBack';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Link from '@mui/material/Link';
import Paper from '@mui/material/Paper';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Tab from '@mui/material/Tab';
import Tabs from '@mui/material/Tabs';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useQuery } from '@tanstack/react-query';
import { useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { getInsurerPolicy, type PolicyDetailResponse } from '../../api/generated';
import { Permissions, usePermission } from '../../auth/permissions';
import { getProblem } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { Field, QueueError } from '../insurer-proposals/common';
import { CoveragesTable } from '../policies/common/CoveragesTable';
import { CoverStatusChip } from '../policies/common/CoverStatusChip';
import { DocumentsList } from '../policies/common/DocumentsList';
import { InstalmentsTable } from '../policies/common/InstalmentsTable';
import { PolicyStatusChip } from '../policies/common/PolicyStatusChip';
import { insurerPoliciesKey, insurerPoliciesPath } from './InsurerPolicyListPage';
import { PolicyTimeline } from './PolicyTimeline';

/** Enquanto houver documentos `PENDING` o detalhe é pedido de 5 em 5 s, no máximo 2 minutos. */
export const documentsPollMs = 5_000;
export const documentsPollLimitMs = 120_000;

type DetailTab = 'summary' | 'instalments' | 'documents' | 'history';

function hasPendingDocuments(policy: PolicyDetailResponse | undefined): boolean {
  return !!policy?.documents.some((d) => d.status === 'PENDING');
}

function NotFound() {
  const { t } = useTranslation();
  return (
    <Paper variant="outlined" sx={{ p: 3 }}>
      <Stack spacing={2} alignItems="flex-start">
        <Typography variant="h5" component="h1">
          {t('insurerPolicies.notFound')}
        </Typography>
        <Typography>{t('insurerPolicies.notFoundText')}</Typography>
        <Button component={RouterLink} to={insurerPoliciesPath} variant="outlined">
          {t('insurerPolicies.backToList')}
        </Button>
      </Stack>
    </Paper>
  );
}

function Summary({ policy }: { policy: PolicyDetailResponse }) {
  const { t, i18n } = useTranslation();
  const language = i18n.language;
  const vehicle = policy.vehicle;
  const makeModel = [vehicle.make, vehicle.model].filter(Boolean).join(' ');
  return (
    <Stack spacing={3}>
      <Box
        component="dl"
        sx={{
          m: 0,
          display: 'grid',
          gap: 2,
          gridTemplateColumns: {
            xs: '1fr',
            sm: 'repeat(2, minmax(0, 1fr))',
            md: 'repeat(3, minmax(0, 1fr))',
          },
        }}
      >
        <Field label={t('insurerPolicies.fields.policyholder')}>{policy.policyholderName}</Field>
        <Field label={t('insurerPolicies.fields.plate')}>{vehicle.plate}</Field>
        <Field label={t('insurerPolicies.fields.makeModel')}>{makeModel}</Field>
        <Field label={t('insurerPolicies.fields.year')}>{vehicle.year}</Field>
        <Field label={t('insurerPolicies.fields.period')}>
          {t('insurerPolicies.fields.periodValue', {
            from: formatDateTime(policy.startAt, language),
            to: formatDateTime(policy.endAt, language),
          })}
        </Field>
        <Field label={t('insurerPolicies.fields.coverageType')}>
          {t(`coverageType.${policy.coverageType}`)}
        </Field>
        <Field label={t('insurerPolicies.fields.renewalType')}>
          {t(`renewalType.${policy.renewalType}`)}
        </Field>
        <Field label={t('insurerPolicies.fields.paymentPlan')}>
          {t(`paymentPlan.${policy.paymentPlan}`)}
        </Field>
        <Field label={t('insurerPolicies.fields.totalPremium')}>
          {formatMoney(policy.totalPremium, language)}
        </Field>
      </Box>
      <Stack spacing={1}>
        <Typography variant="h6" component="h2">
          {t('insurerPolicies.fields.coverages')}
        </Typography>
        <CoveragesTable coverages={policy.coverages} />
      </Stack>
    </Stack>
  );
}

/** `/seguradora/apolices/:id` (S6-05 §6): cabeçalho e separadores do detalhe da apólice. */
export function InsurerPolicyDetailPage() {
  const { t } = useTranslation();
  const { id = '' } = useParams();
  const canReadProposal = usePermission(Permissions.proposalRead);
  const narrow = useMediaQuery('(max-width:599.95px)', { noSsr: true });
  const [tab, setTab] = useState<DetailTab>('summary');
  const openedAt = useRef(Date.now());

  const query = useQuery({
    queryKey: [...insurerPoliciesKey, 'detail', id],
    queryFn: ({ signal }) => getInsurerPolicy(id, undefined, signal),
    refetchInterval: (q) =>
      hasPendingDocuments(q.state.data) && Date.now() - openedAt.current < documentsPollLimitMs
        ? documentsPollMs
        : false,
  });

  if (query.isLoading) {
    return (
      <Stack spacing={2} data-testid="policy-loading">
        <Skeleton variant="text" width="40%" height={40} />
        <Skeleton variant="rectangular" height={200} sx={{ borderRadius: 1 }} />
      </Stack>
    );
  }
  if (query.isError && !query.data) {
    const problem = getProblem(query.error);
    if (problem.status === 404 || problem.code === 'POLICY_NOT_FOUND') return <NotFound />;
    return <QueueError error={query.error} onRetry={() => void query.refetch()} />;
  }
  const policy = query.data;
  if (!policy) return <NotFound />;
  const proposal = policy.proposal;
  const proposalNumber = proposal?.number ?? proposal?.id;

  return (
    <Stack spacing={2} sx={{ minWidth: 0 }}>
      <div>
        <Button component={RouterLink} to={insurerPoliciesPath} startIcon={<ArrowBack />}>
          {t('insurerPolicies.backToList')}
        </Button>
      </div>
      <Stack spacing={1}>
        <Stack direction="row" spacing={1} alignItems="center" flexWrap="wrap" useFlexGap>
          <Typography variant="h5" component="h1" sx={{ wordBreak: 'break-all' }}>
            {policy.number}
          </Typography>
          <PolicyStatusChip status={policy.status} />
          <CoverStatusChip status={policy.coverStatus} />
        </Stack>
        <Typography color="text.secondary">
          {[policy.insurer.name, policy.product.name].filter(Boolean).join(' · ')}
        </Typography>
        {proposal &&
          (canReadProposal ? (
            <Link
              component={RouterLink}
              to={`/seguradora/propostas/${proposal.id}`}
              data-testid="policy-proposal"
            >
              {t('insurerPolicies.viewProposal', { number: proposalNumber })}
            </Link>
          ) : (
            <Typography variant="body2" data-testid="policy-proposal">
              {t('insurerPolicies.proposal', { number: proposalNumber })}
            </Typography>
          ))}
      </Stack>
      <Tabs
        value={tab}
        onChange={(_, value: DetailTab) => setTab(value)}
        variant={narrow ? 'scrollable' : 'standard'}
        scrollButtons={narrow ? 'auto' : false}
        allowScrollButtonsMobile
      >
        <Tab value="summary" label={t('insurerPolicies.tabs.summary')} />
        <Tab value="instalments" label={t('insurerPolicies.tabs.instalments')} />
        <Tab value="documents" label={t('insurerPolicies.tabs.documents')} />
        <Tab value="history" label={t('insurerPolicies.tabs.history')} />
      </Tabs>
      <Paper variant="outlined" sx={{ p: { xs: 2, sm: 3 }, minWidth: 0 }}>
        {tab === 'summary' && <Summary policy={policy} />}
        {tab === 'instalments' && <InstalmentsTable instalments={policy.instalments} />}
        {tab === 'documents' && <DocumentsList documents={policy.documents} />}
        {tab === 'history' && <PolicyTimeline events={policy.events ?? []} />}
      </Paper>
    </Stack>
  );
}
