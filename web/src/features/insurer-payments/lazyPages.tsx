import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Páginas de staff dos pagamentos (S5-12), carregadas só quando se abrem: ficam fora do pacote
 * inicial (que tem de ficar abaixo dos 500 kB).
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

export const InsurerSettingsPage = page(
  lazy(() =>
    import('../insurer-payment-settings/InsurerSettingsPage').then((m) => ({
      default: m.InsurerSettingsPage,
    })),
  ),
);
export const PaymentMethodsSettingsPage = page(
  lazy(() =>
    import('../insurer-payment-settings/PaymentMethodsSettingsPage').then((m) => ({
      default: m.PaymentMethodsSettingsPage,
    })),
  ),
);
export const InsurerPaymentsLayout = page(
  lazy(() => import('./InsurerPaymentsLayout').then((m) => ({ default: m.InsurerPaymentsLayout }))),
);
export const PaymentsListPage = page(
  lazy(() => import('./PaymentsListPage').then((m) => ({ default: m.PaymentsListPage }))),
);
export const PaymentDetailPage = page(
  lazy(() => import('./PaymentDetailPage').then((m) => ({ default: m.PaymentDetailPage }))),
);
export const ExceptionsPage = page(
  lazy(() => import('./ExceptionsPage').then((m) => ({ default: m.ExceptionsPage }))),
);
export const FileImportsPage = page(
  lazy(() => import('./FileImportsPage').then((m) => ({ default: m.FileImportsPage }))),
);
export const PaymentOperationsPage = page(
  lazy(() =>
    import('../admin-payment-ops/PaymentOperationsPage').then((m) => ({
      default: m.PaymentOperationsPage,
    })),
  ),
);
