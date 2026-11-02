import type { ComponentType } from 'react';
import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { AuthRoot } from './auth/AuthProvider';
import { RequireAuth } from './auth/RequireAuth';
import { CustomerLoginPage } from './features/customer-auth/CustomerLoginPage';
import { CustomerHomePage } from './features/customer-profile/CustomerHomePage';
import { ProfilePage } from './features/customer-profile/ProfilePage';
import { AppLayout } from './layout/AppLayout';
import { PublicLayout } from './layout/PublicLayout';
import type { Area } from './layout/menus';
import { HomePage } from './pages/HomePage';
import { NoAccessPage } from './pages/NoAccessPage';
import { NotFoundPage } from './pages/NotFoundPage';
import { UnderConstructionPage } from './pages/UnderConstructionPage';
import { VerificarPage } from './pages/VerificarPage';
import { AdminPage } from './pages/areas/AdminPage';
import { AssistenciaPage } from './pages/areas/AssistenciaPage';
import { FrotaPage } from './pages/areas/FrotaPage';
import { MediadorPage } from './pages/areas/MediadorPage';
import { OficinaPage } from './pages/areas/OficinaPage';
import { PeritoPage } from './pages/areas/PeritoPage';
import { ProvedorPage } from './pages/areas/ProvedorPage';
import { SeguradoraPage } from './pages/areas/SeguradoraPage';
import { SupervisaoPage } from './pages/areas/SupervisaoPage';

/** Áreas de staff, ainda sem conteúdo (a guarda de sessão do staff chega no S1-08). */
const areaPages: Record<Exclude<Area, 'cliente'>, ComponentType> = {
  admin: AdminPage,
  seguradora: SeguradoraPage,
  mediador: MediadorPage,
  perito: PeritoPage,
  oficina: OficinaPage,
  assistencia: AssistenciaPage,
  provedor: ProvedorPage,
  supervisao: SupervisaoPage,
  frota: FrotaPage,
};

/** Cada área: `/<área>` e qualquer caminho abaixo dela mostram a página da área (por agora vazia). */
const areaRoutes: RouteObject[] = (Object.keys(areaPages) as (keyof typeof areaPages)[]).map(
  (area) => {
    const Page = areaPages[area];
    return {
      path: `/${area}`,
      element: <AppLayout area={area} />,
      children: [
        { index: true, element: <Page /> },
        { path: '*', element: <Page /> },
      ],
    };
  },
);

/** Área do cliente: só com sessão de cliente. */
const customerRoutes: RouteObject = {
  path: '/cliente',
  element: (
    <RequireAuth type="CUSTOMER" area="cliente">
      <AppLayout area="cliente" />
    </RequireAuth>
  ),
  children: [
    { index: true, element: <CustomerHomePage /> },
    { path: 'perfil', element: <ProfilePage /> },
    { path: '*', element: <UnderConstructionPage /> },
  ],
};

export const routes: RouteObject[] = [
  {
    element: <AuthRoot />,
    children: [
      {
        element: <PublicLayout />,
        children: [
          { path: '/', element: <HomePage /> },
          { path: '/cliente/entrar', element: <CustomerLoginPage /> },
          { path: '/sem-acesso', element: <NoAccessPage /> },
          { path: '/verificar', element: <VerificarPage /> },
          { path: '/v/:token', element: <VerificarPage /> },
          { path: '*', element: <NotFoundPage /> },
        ],
      },
      customerRoutes,
      ...areaRoutes,
    ],
  },
];

export const routerFutureFlags = {
  v7_relativeSplatPath: true,
  v7_fetcherPersist: true,
  v7_normalizeFormMethod: true,
  v7_partialHydration: true,
  v7_skipActionErrorRevalidation: true,
} as const;

export function createAppRouter() {
  return createBrowserRouter(routes, { future: routerFutureFlags });
}
