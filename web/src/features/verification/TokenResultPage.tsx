import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Link from '@mui/material/Link';
import Skeleton from '@mui/material/Skeleton';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink, useParams } from 'react-router-dom';
import { verifyCertificateToken, verifyCertificateTokenAsPolice } from '../../api/generated';
import { getProblem, problemMessage } from '../../errors/problem';
import { useResendCountdown } from '../customer-auth/useResendCountdown';
import { VerificationResultCard, type VerificationView } from './VerificationResultCard';
import { usePoliceSession, VerifyColumn } from './common';

function CardSkeleton() {
  return (
    <Card variant="outlined" elevation={0} data-testid="verification-loading">
      <CardContent>
        <Stack spacing={1.5}>
          <Skeleton variant="text" width="60%" height={32} />
          <Skeleton variant="text" width="40%" />
          <Skeleton variant="text" width="80%" />
          <Skeleton variant="text" width="50%" />
        </Stack>
      </CardContent>
    </Card>
  );
}

/** `/v/:token` (S6-05 §4): o link do QR do certificado; com sessão de polícia usa a API da polícia. */
export function TokenResultPage() {
  const { t } = useTranslation();
  const { token = '' } = useParams();
  const { loading, police } = usePoliceSession();
  const countdown = useResendCountdown(0);

  const query = useQuery({
    queryKey: ['verification-token', police ? 'police' : 'public', token],
    queryFn: ({ signal }): Promise<VerificationView> =>
      police
        ? verifyCertificateTokenAsPolice(token, undefined, signal)
        : verifyCertificateToken(token, undefined, signal),
    enabled: !loading,
    retry: false,
    staleTime: 0,
    gcTime: 0,
    refetchOnWindowFocus: false,
  });
  const problem = query.error ? getProblem(query.error) : undefined;
  const rateLimited = problem?.code === 'RATE_LIMITED';
  const { restart } = countdown;

  useEffect(() => {
    if (rateLimited && problem?.retryAfterSeconds) restart(problem.retryAfterSeconds);
  }, [rateLimited, problem?.retryAfterSeconds, query.errorUpdatedAt, restart]);

  let content;
  if (loading || query.isLoading) content = <CardSkeleton />;
  else if (query.data) content = <VerificationResultCard result={query.data} police={police} />;
  else if (problem && rateLimited) {
    content = (
      <Alert
        severity="warning"
        role="alert"
        action={
          <Button
            color="inherit"
            size="small"
            disabled={!countdown.done || query.isFetching}
            onClick={() => void query.refetch()}
          >
            {t('common.retry')}
          </Button>
        }
      >
        {countdown.done
          ? t('errors.RATE_LIMITED')
          : t('verify.rateLimitedCountdown', { seconds: countdown.remaining })}
      </Alert>
    );
  } else if (problem) {
    content = (
      <Alert severity="error" role="alert">
        {problemMessage(t, problem)}
      </Alert>
    );
  }

  return (
    <VerifyColumn>
      <Stack spacing={2}>
        <Typography variant="h5" component="h1">
          {police ? t('police.title') : t('verify.title')}
        </Typography>
        {content}
        <Link component={RouterLink} to="/verificar">
          {t('verify.otherPlate')}
        </Link>
      </Stack>
    </VerifyColumn>
  );
}
