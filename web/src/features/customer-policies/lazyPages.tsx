import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/** Páginas das apólices do cliente (S6-06), carregadas só quando se abrem. */
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

export const MyPolicyListPage = page(
  lazy(() => import('./MyPolicyListPage').then((m) => ({ default: m.MyPolicyListPage }))),
);
export const MyPolicyDetailPage = page(
  lazy(() => import('./MyPolicyDetailPage').then((m) => ({ default: m.MyPolicyDetailPage }))),
);
