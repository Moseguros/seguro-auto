import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Stepper from '@mui/material/Stepper';
import Typography from '@mui/material/Typography';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate, useParams } from 'react-router-dom';
import {
  acceptInvitation,
  activateInvitation,
  getInvitationByToken,
  type InvitationDetailsResponse,
  type TotpSetupResponse,
} from '../../api/generated';
import { areaFor } from '../../auth/areas';
import { LoadingScreen } from '../../auth/RequireAuth';
import { useAuth } from '../../auth/useAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { AuthCard } from './AuthCard';
import { NewPasswordForm } from './NewPasswordForm';
import { TotpSetup } from './TotpSetup';

/** `/convite/:token`: activação da conta de staff (palavra-passe e depois TOTP). */
export function InvitationPage() {
  const { t } = useTranslation();
  const { token = '' } = useParams();
  const navigate = useNavigate();
  const { login } = useAuth();
  const [setup, setSetup] = useState<TotpSetupResponse | null>(null);
  const [invalid, setInvalid] = useState(false);
  const invitation = useQuery({
    queryKey: ['invitation', token],
    queryFn: ({ signal }) => getInvitationByToken(token, undefined, signal),
    retry: false,
    staleTime: Infinity,
  });

  let content;
  const problem = invitation.isError ? getProblem(invitation.error) : null;
  if (invitation.isPending) {
    content = <LoadingScreen />;
  } else if (invalid || problem?.code === 'INVITATION_INVALID' || problem?.status === 404) {
    content = (
      <Alert severity="error" role="alert">
        {t('staffAuth.invitation.invalid')}
      </Alert>
    );
  } else if (problem) {
    content = (
      <Stack spacing={2}>
        <Alert severity="error">{problemMessage(t, problem)}</Alert>
        <Button variant="outlined" onClick={() => void invitation.refetch()}>
          {t('auth.retry')}
        </Button>
      </Stack>
    );
  } else if (invitation.data) {
    content = (
      <Stack spacing={3}>
        <Stepper activeStep={setup ? 1 : 0} alternativeLabel>
          <Step>
            <StepLabel>{t('staffAuth.invitation.step1')}</StepLabel>
          </Step>
          <Step>
            <StepLabel>{t('staffAuth.invitation.step2')}</StepLabel>
          </Step>
        </Stepper>
        {setup ? (
          <TotpSetup
            setup={setup}
            onActivate={async (totpCode) => {
              const tokens = await activateInvitation(token, { totpCode });
              const me = await login(tokens, 'STAFF');
              navigate(areaFor(me).path, { replace: true });
            }}
          />
        ) : (
          <Stack spacing={2}>
            <InvitationSummary invitation={invitation.data} />
            <NewPasswordForm
              email={invitation.data.email}
              submitLabel={t('staffAuth.invitation.continue')}
              onSubmit={async (password) => setSetup(await acceptInvitation(token, { password }))}
              onError={(p) => {
                if (p.code !== 'INVITATION_INVALID') return false;
                setInvalid(true);
                return true;
              }}
            />
          </Stack>
        )}
      </Stack>
    );
  }

  return <AuthCard title={t('staffAuth.invitation.title')}>{content}</AuthCard>;
}

function InvitationSummary({ invitation }: { invitation: InvitationDetailsResponse }) {
  const { t } = useTranslation();
  const rows: [string, string][] = [
    [t('staffAuth.invitation.name'), invitation.fullName],
    [t('staffAuth.invitation.email'), invitation.email],
    [
      t('staffAuth.invitation.organization'),
      invitation.organization
        ? `${invitation.organization.displayName} (${t(`orgType.${invitation.organization.type}`)})`
        : t('staffAuth.invitation.platform'),
    ],
  ];
  return (
    <Stack component="dl" spacing={1} sx={{ m: 0 }} data-testid="invitation-summary">
      {rows.map(([label, value]) => (
        <div key={label}>
          <Typography component="dt" variant="body2" color="text.secondary">
            {label}
          </Typography>
          <Typography component="dd" sx={{ m: 0, wordBreak: 'break-word' }}>
            {value}
          </Typography>
        </div>
      ))}
    </Stack>
  );
}
