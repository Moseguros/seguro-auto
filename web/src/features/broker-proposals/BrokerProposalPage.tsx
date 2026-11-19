import { ProposalScreen } from '../proposal-wizard/ProposalScreen';
import { SendPaymentLinkButton } from './SendPaymentLinkButton';

/**
 * `/mediador/propostas/:id` (`?passo=…`): os passos em nome do cliente ou o estado; numa proposta
 * aceite, o envio do link de pagamento ao cliente (S5-11 §2).
 */
export function BrokerProposalPage() {
  return (
    <ProposalScreen
      mode="broker"
      acceptedAction={(proposal) => <SendPaymentLinkButton proposalId={proposal.id} />}
    />
  );
}
