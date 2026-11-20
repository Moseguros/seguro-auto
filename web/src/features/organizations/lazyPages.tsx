import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Lista das organizações, carregada só quando se abre: usa a grelha de dados (`mui-x`), que assim fica fora da
 * entrada e da verificação pública (S6-05 §1).
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

export const OrganizationListPage = page(
  lazy(() => import('./OrganizationListPage').then((m) => ({ default: m.OrganizationListPage }))),
);
