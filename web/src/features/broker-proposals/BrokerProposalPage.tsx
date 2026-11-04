import { ProposalScreen } from '../proposal-wizard/ProposalScreen';

/** `/mediador/propostas/:id` (`?passo=…`): os passos em nome do cliente ou o estado. */
export function BrokerProposalPage() {
  return <ProposalScreen mode="broker" />;
}
