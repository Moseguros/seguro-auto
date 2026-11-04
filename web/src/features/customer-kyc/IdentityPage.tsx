import Alert from '@mui/material/Alert';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Container from '@mui/material/Container';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import {
  getGetMyProfileQueryKey,
  getMyKyc,
  getMyProfile,
  listMyProposals,
  type KycCheckResponse,
  type KycState,
} from '../../api/generated';
import { LoadingScreen } from '../../auth/RequireAuth';
import { getProblem, problemMessage } from '../../errors/problem';
import { myProposalsKey } from '../proposal-wizard/useProposalMutations';
import { KycForm } from './KycForm';

export const myKycKey = ['my-kyc'] as const;

/** Estados em que o cliente pode enviar (de novo) o documento e a selfie. */
export function kycFormAllowed(state: KycState): boolean {
  return state === 'NONE' || state === 'REJECTED' || state === 'OUTDATED';
}

export function useMyKyc(enabled = true) {
  return useQuery({
    queryKey: myKycKey,
    queryFn: ({ signal }) => getMyKyc(undefined, signal),
    enabled,
  });
}

export function useMyProfileQuery(enabled = true) {
  return useQuery({
    queryKey: getGetMyProfileQueryKey(),
    queryFn: ({ signal }) => getMyProfile(undefined, signal),
    enabled,
  });
}

/** Estado da verificação de identidade (S4-08 §3, `identidade`). */
export function KycStateView({ state, check }: { state: KycState; check?: KycCheckResponse }) {
  const { t } = useTranslation();
  switch (state) {
    case 'VERIFIED':
      return <Alert severity="success">{t('kyc.view.verified')}</Alert>;
    case 'PENDING':
      return <Alert severity="info">{t('kyc.view.pending')}</Alert>;
    case 'OUTDATED':
      return <Alert severity="warning">{t('kyc.view.outdated')}</Alert>;
    case 'REJECTED': {
      const reasons = [
        check?.rejectionReasonCode ? t(`kyc.rejectionReason.${check.rejectionReasonCode}`) : null,
        check?.rejectionReason ?? null,
      ].filter(Boolean);
      return (
        <Alert severity="error">
          {reasons.length > 0
            ? t('kyc.view.rejectedWith', { reason: reasons.join(' — ') })
            : t('kyc.view.rejected')}
        </Alert>
      );
    }
    default:
      return null;
  }
}

/** Estados das propostas a que o KYC pode ir (editáveis ou em análise). */
const kycProposalStatuses = ['DRAFT', 'INFO_REQUESTED', 'UNDER_REVIEW'] as const;

/** `/cliente/identidade`: estado e envio fora de uma proposta. */
export function IdentityPage() {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const kyc = useMyKyc();
  const profile = useMyProfileQuery();
  const proposals = useQuery({
    queryKey: [...myProposalsKey, 'kyc-target'],
    queryFn: ({ signal }) =>
      listMyProposals(
        { status: [...kycProposalStatuses], page: 0, size: 1, sort: 'createdAt,desc' },
        { paramsSerializer: { indexes: null } },
        signal,
      ),
  });

  let body;
  if (kyc.isPending || proposals.isPending) body = <LoadingScreen />;
  else if (kyc.isError) {
    body = <Alert severity="error">{problemMessage(t, getProblem(kyc.error))}</Alert>;
  } else {
    const insurerId = proposals.data?.content[0]?.insurer?.id;
    const state = kyc.data.state;
    body = (
      <Stack spacing={2}>
        <KycStateView state={state} check={kyc.data.check} />
        {kycFormAllowed(state) &&
          (insurerId ? (
            <KycForm
              insurerId={insurerId}
              idDocType={profile.data?.idDocType}
              onSubmitted={() => void queryClient.invalidateQueries({ queryKey: myKycKey })}
            />
          ) : (
            <Typography color="text.secondary">{t('kyc.view.noProposal')}</Typography>
          ))}
      </Stack>
    );
  }

  return (
    <Container maxWidth="sm" disableGutters>
      <Stack spacing={3}>
        <Typography variant="h4" component="h1">
          {t('kyc.view.title')}
        </Typography>
        <Card>
          <CardContent>{body}</CardContent>
        </Card>
      </Stack>
    </Container>
  );
}
