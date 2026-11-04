import Alert from '@mui/material/Alert';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import type { GetUserMedia } from '../customer-kyc/CameraCaptureDialog';
import {
  kycFormAllowed,
  KycStateView,
  myKycKey,
  useMyKyc,
  useMyProfileQuery,
} from '../customer-kyc/IdentityPage';
import { KycForm } from '../customer-kyc/KycForm';
import { StepActions } from './StepActions';
import { useWizard } from './useProposalMutations';
import { isStepDone } from './wizardSteps';

/** Passo `identidade`: estado do KYC e, para o cliente, o envio do documento e da selfie. */
export function IdentityStep({ getUserMedia }: { getUserMedia?: GetUserMedia } = {}) {
  const { t } = useTranslation();
  const wizard = useWizard();
  const queryClient = useQueryClient();
  const { proposal, mode } = wizard;
  const customer = mode === 'customer';
  const state = proposal.kyc.state;
  const kyc = useMyKyc(customer && state === 'REJECTED');
  const profile = useMyProfileQuery(customer && kycFormAllowed(state));
  const done = isStepDone('identidade', proposal, wizard.kycSkipped);

  function later() {
    wizard.skipKyc();
    wizard.next();
  }

  return (
    <Stack spacing={2.5}>
      <Typography>
        {proposal.kyc.requiredForAutomaticAcceptance
          ? t('proposal.identity.required')
          : t('proposal.identity.optional')}
      </Typography>
      <KycStateView state={state} check={kyc.data?.check} />

      {customer && kycFormAllowed(state) && proposal.insurer && (
        <KycForm
          insurerId={proposal.insurer.id}
          idDocType={profile.data?.idDocType}
          getUserMedia={getUserMedia}
          onSubmitted={() => {
            void queryClient.invalidateQueries({ queryKey: myKycKey });
            void wizard.reload().then(() => wizard.next());
          }}
        />
      )}
      {!customer && <Alert severity="info">{t('proposal.identity.broker')}</Alert>}

      <StepActions onBack={wizard.back}>
        {done ? (
          <Button variant="contained" onClick={wizard.next}>
            {t('proposal.wizard.continue')}
          </Button>
        ) : customer ? (
          <Button variant="text" onClick={later}>
            {t('proposal.identity.later')}
          </Button>
        ) : (
          <Button variant="contained" onClick={later}>
            {t('proposal.wizard.continue')}
          </Button>
        )}
      </StepActions>
    </Stack>
  );
}
