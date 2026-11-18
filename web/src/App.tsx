import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useMemo, useState, type ReactNode } from 'react';
import { RouterProvider } from 'react-router-dom';
import { DemoBanner } from './components/DemoBanner';
import { createAppRouter } from './router';
import { createAppTheme } from './theme/theme';

/** Fornecedores comuns: React Query e tema MUI (claro ou escuro pela preferência do sistema). */
export function AppProviders({
  children,
  queryClient,
}: {
  children: ReactNode;
  queryClient: QueryClient;
}) {
  const prefersDark = useMediaQuery('(prefers-color-scheme: dark)', { noSsr: true });
  const theme = useMemo(() => createAppTheme(prefersDark ? 'dark' : 'light'), [prefersDark]);
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <CssBaseline enableColorScheme />
        {children}
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export function App() {
  const [queryClient] = useState(() => new QueryClient());
  const [router] = useState(createAppRouter);
  return (
    <AppProviders queryClient={queryClient}>
      <RouterProvider router={router} future={{ v7_startTransition: true }} />
      <DemoBanner />
    </AppProviders>
  );
}
