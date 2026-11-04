import { ProposalScreen } from '../proposal-wizard/ProposalScreen';

/** `/cliente/propostas/:id` (`?passo=…`): os passos ou o estado da proposta. */
export function CustomerProposalPage() {
  return <ProposalScreen mode="customer" />;
}
