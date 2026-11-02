import { QueryClient } from '@tanstack/react-query';
import { render } from '@testing-library/react';
import type { ReactElement } from 'react';
import { createMemoryRouter, RouterProvider } from 'react-router-dom';
import { AppProviders } from '../App';
import { routerFutureFlags, routes } from '../router';

export function testQueryClient(): QueryClient {
  return new QueryClient({ defaultOptions: { queries: { retry: false } } });
}

export function renderWithProviders(ui: ReactElement) {
  return render(<AppProviders queryClient={testQueryClient()}>{ui}</AppProviders>);
}

export function renderRoute(path: string) {
  const router = createMemoryRouter(routes, {
    initialEntries: [path],
    future: routerFutureFlags,
  });
  return render(
    <AppProviders queryClient={testQueryClient()}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
    </AppProviders>,
  );
}
