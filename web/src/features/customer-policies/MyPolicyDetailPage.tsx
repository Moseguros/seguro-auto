import Alert from '@mui/material/Alert';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import useMediaQuery from '@mui/material/useMediaQuery';
import { useState, type ReactNode } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import type { PolicyDetailResponse } from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { formatMoney } from '../../format/money';
import { formatDateTime } from '../../i18n/format';
import { CoveragesTable } from '../policies/common/CoveragesTable';
import { CoverStatusChip } from '../policies/common/CoverStatusChip';
import { DocumentsList } from '../policies/common/DocumentsList';
import { InstalmentsTable } from '../policies/common/InstalmentsTable';
import { PolicyStatusChip } from '../policies/common/PolicyStatusChip';
import { CertificateCard } from './CertificateCard';
import { CertificateFullscreen } from './CertificateFullscreen';
import { makeModel } from './MyPolicyCard';
import { customerPolicyPaths } from './paths';
import { usePolicyDocumentsPolling } from './usePolicyDocumentsPolling';

function Section({ title, children }: { title: string; children: ReactNode }) {
  return (
    <Card variant="outlined" elevation={0}>
      <CardContent>
        <Typography variant="h6" component="h2" sx={{ mb: 1.5 }}>
          {title}
        </Typography>
        {children}
      </CardContent>
    </Card>
  );
}

function Field({ label, value }: { label: string; value: ReactNode }) {
  return (
    <Stack
      direction="row"
      justifyContent="space-between"
      gap={2}
      sx={{ py: 0.5, flexWrap: 'wrap' }}
    >
      <Typography color="text.secondary">{label}</Typography>
      <Typography sx={{ textAlign: 'right', fontVariantNumeric: 'tabular-nums' }}>
        {value}
      </Typography>
    </Stack>
  );
}

function PolicyHeader({ policy }: { policy: PolicyDetailResponse }) {
  const { t } = useTranslation();
  const vehicle = makeModel(policy.vehicle);
  return (
    <Stack spacing={1}>
      <Typography variant="h4" component="h1" sx={{ wordBreak: 'break-word' }}>
        {policy.vehicle.plate}
      </Typography>
      <Stack direction="row" gap={1} flexWrap="wrap">
        <PolicyStatusChip status={policy.status} />
        <CoverStatusChip status={policy.coverStatus} />
      </Stack>
      {vehicle && <Typography color="text.secondary">{vehicle}</Typography>}
      <Typography color="text.secondary">
        {t('customerPolicies.number', { number: policy.number })}
      </Typography>
      <Typography sx={{ wordBreak: 'break-word' }}>
        {[policy.insurer.name, policy.product.name].filter(Boolean).join(' · ')}
      </Typography>
    </Stack>
  );
}

function PolicyNotFound() {
  const { t } = useTranslation();
  return (
    <Stack spacing={2} alignItems="flex-start">
      <Typography variant="h4" component="h1">
        {t('customerPolicies.notFound')}
      </Typography>
      <Button component={RouterLink} to={customerPolicyPaths.list} variant="outlined">
        {t('customerPolicies.backToList')}
      </Button>
    </Stack>
  );
}

/** `/cliente/apolices/:id` (S6-06 §3): a apólice com o certificado, as coberturas e os documentos. */
export function MyPolicyDetailPage() {
  const { id = '' } = useParams();
  const { t, i18n } = useTranslation();
  const [fullscreen, setFullscreen] = useState(false);
  // Em ≥ 900 px o certificado fica numa coluna à direita; abaixo, logo depois do cabeçalho.
  const wide = useMediaQuery('(min-width:900px)');
  const { query, documentsTimedOut } = usePolicyDocumentsPolling(id);

  if (query.isPending) return <LoadingScreen />;
  if (query.isError) {
    const problem = getProblem(query.error);
    if (problem.status === 404) return <PolicyNotFound />;
    return (
      <Alert
        severity="error"
        role="alert"
        action={
          <Button color="inherit" size="small" onClick={() => void query.refetch()}>
            {t('common.retry')}
          </Button>
        }
      >
        {problemMessage(t, problem)}
      </Alert>
    );
  }

  const policy = query.data;
  const language = i18n.language;
  const certificate = (
    <CertificateCard policy={policy} onShowFullscreen={() => setFullscreen(true)} />
  );

  return (
    <Stack spacing={2}>
      <PolicyHeader policy={policy} />
      {documentsTimedOut && (
        <Alert severity="info" role="status">
          {t('customerPolicies.documentsSlow')}
        </Alert>
      )}
      <Box
        sx={{
          display: 'grid',
          gap: 2,
          gridTemplateColumns: { xs: 'minmax(0, 1fr)', md: 'minmax(0, 1fr) 320px' },
          alignItems: 'start',
        }}
      >
        {!wide && certificate}
        <Stack spacing={2} sx={{ minWidth: 0 }}>
          <Section title={t('customerPolicies.period.title')}>
            <Field
              label={t('customerPolicies.period.start')}
              value={formatDateTime(policy.startAt, language)}
            />
            <Field
              label={t('customerPolicies.period.end')}
              value={formatDateTime(policy.endAt, language)}
            />
            <Field
              label={t('customerPolicies.period.renewalType')}
              value={t(`renewalType.${policy.renewalType}`)}
            />
            <Field
              label={t('customerPolicies.period.paymentPlan')}
              value={t(`paymentPlan.${policy.paymentPlan}`)}
            />
            <Field
              label={t('customerPolicies.period.totalPremium')}
              value={<strong>{formatMoney(policy.totalPremium, language)}</strong>}
            />
          </Section>
          <Section title={t('customerPolicies.coverages')}>
            <CoveragesTable coverages={policy.coverages} />
          </Section>
          <Section title={t('customerPolicies.instalments')}>
            <InstalmentsTable instalments={policy.instalments} />
          </Section>
          <Section title={t('customerPolicies.documents')}>
            <DocumentsList documents={policy.documents} />
          </Section>
        </Stack>
        {wide && <Box sx={{ position: 'sticky', top: 80 }}>{certificate}</Box>}
      </Box>
      <CertificateFullscreen
        policy={policy}
        open={fullscreen}
        onClose={() => setFullscreen(false)}
      />
    </Stack>
  );
}
