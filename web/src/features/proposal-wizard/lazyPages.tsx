import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Páginas das propostas do cliente e do mediador e da identidade (S4-08), carregadas só quando
 * se abrem (como as da seguradora no S4-07): ficam fora do pacote inicial.
 */
function page(component: LazyExoticComponent<ComponentType>) {
  const Component = component;
  return function LazyPage() {
    return (
      <Suspense fallback={<LoadingScreen />}>
        <Component />
      </Suspense>
    );
  };
}

export const MyProposalsPage = page(
  lazy(() =>
    import('../customer-proposals/MyProposalsPage').then((m) => ({ default: m.MyProposalsPage })),
  ),
);
export const NewProposalPage = page(
  lazy(() =>
    import('../customer-proposals/NewProposalPage').then((m) => ({ default: m.NewProposalPage })),
  ),
);
export const CustomerProposalPage = page(
  lazy(() =>
    import('../customer-proposals/CustomerProposalPage').then((m) => ({
      default: m.CustomerProposalPage,
    })),
  ),
);
export const IdentityPage = page(
  lazy(() => import('../customer-kyc/IdentityPage').then((m) => ({ default: m.IdentityPage }))),
);
export const BrokerProposalsPage = page(
  lazy(() =>
    import('../broker-proposals/BrokerProposalsPage').then((m) => ({
      default: m.BrokerProposalsPage,
    })),
  ),
);
export const BrokerNewProposalPage = page(
  lazy(() =>
    import('../broker-proposals/BrokerNewProposalPage').then((m) => ({
      default: m.BrokerNewProposalPage,
    })),
  ),
);
export const BrokerProposalPage = page(
  lazy(() =>
    import('../broker-proposals/BrokerProposalPage').then((m) => ({
      default: m.BrokerProposalPage,
    })),
  ),
);
