import Button from '@mui/material/Button';
import { useTranslation } from 'react-i18next';
import { Link as RouterLink } from 'react-router-dom';
import { ProposalScreen } from '../proposal-wizard/ProposalScreen';

/** "Adquirir apólice" de uma proposta aceite (S5-11 §2): o único botão laranja do ecrã. */
function BuyPolicyButton({ proposalId }: { proposalId: string }) {
  const { t } = useTranslation();
  return (
    <Button
      variant="cta"
      size="large"
      component={RouterLink}
      to={`/cliente/propostas/${proposalId}/pagar`}
      sx={{ alignSelf: { xs: 'stretch', sm: 'flex-start' } }}
    >
      {t('payments.pay.submit')}
    </Button>
  );
}

/** `/cliente/propostas/:id` (`?passo=…`): os passos ou o estado da proposta. */
export function CustomerProposalPage() {
  return (
    <ProposalScreen
      mode="customer"
      acceptedAction={(proposal) => <BuyPolicyButton proposalId={proposal.id} />}
    />
  );
}
