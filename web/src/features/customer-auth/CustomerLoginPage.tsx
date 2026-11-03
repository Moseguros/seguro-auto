import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { getOtpOptions, requestOtp, verifyOtp } from '../../api/generated';
import { useAuth } from '../../auth/useAuth';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { CodeStep } from './CodeStep';
import { IdentifierStep } from './IdentifierStep';
import type { OtpRequest, RequestCode, SentCode, VerifyCode } from './otp';

export const otpOptionsQueryKey = ['otpOptions'] as const;

/** Só caminhos internos (`/…`, nunca `//…`): o `voltar` não pode levar para outro site. */
export function safeReturnPath(value: string | null): string | null {
  if (!value || !value.startsWith('/') || value.startsWith('//') || value.startsWith('/\\')) {
    return null;
  }
  return value;
}

/** Destino depois de entrar: completar o perfil, `voltar` ou o início do cliente. */
export function afterLoginPath(
  isNewUser: boolean,
  profileComplete: boolean | undefined,
  back: string | null,
): string {
  if (isNewUser || profileComplete === false) return '/cliente/perfil?completar=1';
  return safeReturnPath(back) ?? '/cliente';
}

/** `/cliente/entrar`: login do cliente por código (telemóvel ou e-mail). */
export function CustomerLoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const { login } = useAuth();
  const [sent, setSent] = useState<SentCode | null>(null);
  const [lastRequest, setLastRequest] = useState<OtpRequest | undefined>();
  const options = useQuery({
    queryKey: otpOptionsQueryKey,
    queryFn: ({ signal }) => getOtpOptions(undefined, signal),
  });

  const request: RequestCode = (otpRequest) => requestOtp(otpRequest);
  const verify: VerifyCode = async (challengeId, code) => {
    const tokens = await verifyOtp({ challengeId, code });
    const me = await login(tokens, 'CUSTOMER');
    navigate(afterLoginPath(tokens.isNewUser, me.profileComplete, params.get('voltar')), {
      replace: true,
    });
  };

  let content;
  if (options.isPending) {
    content = <LoadingScreen />;
  } else if (options.isError) {
    content = (
      <Stack spacing={2}>
        <Alert severity="error">{problemMessage(t, getProblem(options.error))}</Alert>
        <Button variant="outlined" onClick={() => void options.refetch()}>
          {t('auth.retry')}
        </Button>
      </Stack>
    );
  } else if (sent) {
    content = (
      <CodeStep
        sent={sent}
        codeLength={options.data.codeLength}
        onRequest={request}
        onVerify={verify}
        onChangeIdentifier={() => setSent(null)}
      />
    );
  } else {
    content = (
      <IdentifierStep
        options={options.data}
        initial={lastRequest}
        onRequest={request}
        onSent={(value) => {
          setLastRequest(value.request);
          setSent(value);
        }}
      />
    );
  }

  return (
    <Container maxWidth="xs" disableGutters>
      <Card>
        <CardContent sx={{ p: { xs: 2, sm: 3 } }}>
          <Stack spacing={1} sx={{ mb: 3 }}>
            <Typography variant="h5" component="h1">
              {t('auth.login.title')}
            </Typography>
            <Typography color="text.secondary">
              {sent ? t('auth.code.subtitle') : t('auth.login.subtitle')}
            </Typography>
          </Stack>
          {content}
        </CardContent>
      </Card>
    </Container>
  );
}
