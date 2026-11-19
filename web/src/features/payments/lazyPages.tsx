import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/** Páginas dos pagamentos do cliente (S5-11), carregadas só quando se abrem. */
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

export const ProposalPaymentPage = page(
  lazy(() => import('./ProposalPaymentPage').then((m) => ({ default: m.ProposalPaymentPage }))),
);
export const PaymentIntentPage = page(
  lazy(() => import('./PaymentIntentPage').then((m) => ({ default: m.PaymentIntentPage }))),
);
export const MyPaymentsPage = page(
  lazy(() => import('./MyPaymentsPage').then((m) => ({ default: m.MyPaymentsPage }))),
);
