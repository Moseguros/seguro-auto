import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/** Páginas das apólices da seguradora (S6-05), carregadas só quando se abrem. */
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

export const InsurerPolicyListPage = page(
  lazy(() => import('./InsurerPolicyListPage').then((m) => ({ default: m.InsurerPolicyListPage }))),
);
export const InsurerPolicyDetailPage = page(
  lazy(() =>
    import('./InsurerPolicyDetailPage').then((m) => ({ default: m.InsurerPolicyDetailPage })),
  ),
);
