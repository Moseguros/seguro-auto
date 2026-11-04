import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Páginas das propostas, do KYC e das vistorias da seguradora (S4-07), carregadas só quando se
 * abrem: ficam fora do pacote inicial (que passaria dos 500 kB).
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

export const ProposalQueueRoute = page(
  lazy(() => import('./ProposalQueuePage').then((m) => ({ default: m.ProposalQueueRoute }))),
);
export const ProposalDetailRoute = page(
  lazy(() => import('./ProposalDetailPage').then((m) => ({ default: m.ProposalDetailRoute }))),
);
export const KycQueuePage = page(
  lazy(() => import('../insurer-kyc/KycQueuePage').then((m) => ({ default: m.KycQueuePage }))),
);
export const KycReviewPage = page(
  lazy(() => import('../insurer-kyc/KycReviewPage').then((m) => ({ default: m.KycReviewPage }))),
);
export const InspectionQueuePage = page(
  lazy(() =>
    import('../insurer-inspections/InspectionQueuePage').then((m) => ({
      default: m.InspectionQueuePage,
    })),
  ),
);
