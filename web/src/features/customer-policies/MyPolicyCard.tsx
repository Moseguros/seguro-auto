import Card from '@mui/material/Card';
import CardActionArea from '@mui/material/CardActionArea';
import CardContent from '@mui/material/CardContent';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import type { MyPolicySummaryResponse, PolicyVehicleRef } from '../../api/generated';
import { formatDateTime } from '../../i18n/format';
import { CoverStatusChip } from '../policies/common/CoverStatusChip';
import { customerPolicyPaths } from './paths';

/** Marca e modelo do veículo (vazio se nenhum for conhecido). */
export function makeModel(vehicle: PolicyVehicleRef): string {
  return [vehicle.make, vehicle.model].filter(Boolean).join(' ');
}

/** Uma apólice na lista do cliente; o cartão inteiro abre o detalhe. */
export function MyPolicyCard({ policy }: { policy: MyPolicySummaryResponse }) {
  const { t, i18n } = useTranslation();
  const vehicle = makeModel(policy.vehicle);
  const insurer = [policy.insurer.name, policy.productName].filter(Boolean).join(' · ');
  return (
    <Card variant="outlined" elevation={0} data-testid="policy-card">
      <CardActionArea
        component={RouterLink}
        to={customerPolicyPaths.detail(policy.id)}
        sx={{ height: '100%' }}
      >
        <CardContent>
          <Stack spacing={0.75}>
            <Stack direction="row" justifyContent="space-between" alignItems="flex-start" gap={1}>
              <Typography
                variant="h6"
                component="h2"
                fontWeight={700}
                sx={{ letterSpacing: 0.5, wordBreak: 'break-word' }}
              >
                {policy.vehicle.plate}
              </Typography>
              <CoverStatusChip status={policy.coverStatus} />
            </Stack>
            {vehicle && <Typography color="text.secondary">{vehicle}</Typography>}
            <Typography sx={{ wordBreak: 'break-word' }}>{insurer}</Typography>
            <Typography variant="body2">
              {t('customerPolicies.validUntil', {
                date: formatDateTime(policy.endAt, i18n.language),
              })}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {t('customerPolicies.number', { number: policy.number })}
            </Typography>
          </Stack>
        </CardContent>
      </CardActionArea>
    </Card>
  );
}

/** Três cartões esqueleto enquanto a lista carrega. */
export function MyPolicyCardSkeletons() {
  const { t } = useTranslation();
  return (
    <>
      {[0, 1, 2].map((index) => (
        <Card
          key={index}
          variant="outlined"
          elevation={0}
          data-testid="policy-card-skeleton"
          aria-label={t('auth.loading')}
        >
          <CardContent>
            <Skeleton variant="text" width="40%" height={36} />
            <Skeleton variant="text" width="60%" />
            <Skeleton variant="text" width="80%" />
            <Skeleton variant="text" width="50%" />
          </CardContent>
        </Card>
      ))}
    </>
  );
}
