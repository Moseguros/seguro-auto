import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/** Páginas dos modelos de notificação (S6-05), carregadas só quando se abrem. */
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

export const TemplateListPage = page(
  lazy(() => import('./TemplateListPage').then((m) => ({ default: m.TemplateListPage }))),
);
export const TemplateEditorPage = page(
  lazy(() => import('./TemplateEditorPage').then((m) => ({ default: m.TemplateEditorPage }))),
);
