import { QueryClient } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import type { ReactElement } from 'react';
import { createMemoryRouter, RouterProvider, type RouteObject } from 'react-router-dom';
import { AppProviders } from '../App';
import { AuthRoot } from '../auth/AuthProvider';
import { routerFutureFlags, routes } from '../router';

export function testQueryClient(): QueryClient {
  return new QueryClient({ defaultOptions: { queries: { retry: false } } });
}

export function renderWithProviders(ui: ReactElement) {
  return render(<AppProviders queryClient={testQueryClient()}>{ui}</AppProviders>);
}

/** Abre o caminho com as rotas da aplicação; devolve também o router (para ler a localização). */
export function renderRoute(path: string, queryClient: QueryClient = testQueryClient()) {
  const router = createMemoryRouter(routes, {
    initialEntries: [path],
    future: routerFutureFlags,
  });
  const result = render(
    <AppProviders queryClient={queryClient}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AppProviders>,
  );
  return { ...result, router, queryClient };
}

/**
 * Mostra um componente dentro do `AuthProvider` e de um router de memória (para componentes que
 * usam a sessão ou navegam). As rotas extra servem de destino às navegações.
 */
export function renderWithAuth(
  ui: ReactElement,
  { path = '/', extraRoutes = [] }: { path?: string; extraRoutes?: RouteObject[] } = {},
) {
  const queryClient = testQueryClient();
  const router = createMemoryRouter(
    [
      {
        element: <AuthRoot />,
        children: [{ path, element: ui }, ...extraRoutes, { path: '*', element: null }],
      },
    ],
    { initialEntries: [path], future: routerFutureFlags },
  );
  const result = render(
    <AppProviders queryClient={queryClient}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AppProviders>,
  );
  return { ...result, router, queryClient };
}
