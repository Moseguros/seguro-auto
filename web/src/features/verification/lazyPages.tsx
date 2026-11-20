import { lazy, Suspense, type ComponentType, type LazyExoticComponent } from 'react';
import { LoadingScreen } from '../../auth/RequireAuth';

/**
 * Páginas da verificação (S6-05), carregadas só quando se abrem, num bloco que não importa a
 * grelha de dados (`mui-x`) nem o leitor de QR (`zxing`, carregado à parte pelo `QrScanner`).
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

export const VerifyPage = page(
  lazy(() => import('./VerifyPage').then((m) => ({ default: m.VerifyPage }))),
);
export const TokenResultPage = page(
  lazy(() => import('./TokenResultPage').then((m) => ({ default: m.TokenResultPage }))),
);
export const PoliceHistoryPage = page(
  lazy(() => import('./PoliceHistoryPage').then((m) => ({ default: m.PoliceHistoryPage }))),
);
