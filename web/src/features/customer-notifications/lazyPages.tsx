import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Páginas das notificações do cliente (S6-06), carregadas só quando se abrem. O
 * `NotificationBell` não está aqui: vive no `AppLayout`.
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

export const NotificationsPage = page(
  lazy(() => import('./NotificationsPage').then((m) => ({ default: m.NotificationsPage }))),
);
export const PreferencesPage = page(
  lazy(() => import('./PreferencesPage').then((m) => ({ default: m.PreferencesPage }))),
);
