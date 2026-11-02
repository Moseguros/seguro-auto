import type { ComponentType } from 'react';
import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { AppLayout } from './layout/AppLayout';
import { PublicLayout } from './layout/PublicLayout';
import type { Area } from './layout/menus';
import { HomePage } from './pages/HomePage';
import { NotFoundPage } from './pages/NotFoundPage';
import { VerificarPage } from './pages/VerificarPage';
import { AdminPage } from './pages/areas/AdminPage';
import { AssistenciaPage } from './pages/areas/AssistenciaPage';
import { ClientePage } from './pages/areas/ClientePage';
import { FrotaPage } from './pages/areas/FrotaPage';
import { MediadorPage } from './pages/areas/MediadorPage';
import { OficinaPage } from './pages/areas/OficinaPage';
import { PeritoPage } from './pages/areas/PeritoPage';
import { ProvedorPage } from './pages/areas/ProvedorPage';
import { SeguradoraPage } from './pages/areas/SeguradoraPage';
import { SupervisaoPage } from './pages/areas/SupervisaoPage';

const areaPages: Record<Area, ComponentType> = {
  cliente: ClientePage,
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
const areaRoutes: RouteObject[] = (Object.keys(areaPages) as Area[]).map((area) => {
  const Page = areaPages[area];
  return {
    path: `/${area}`,
    element: <AppLayout area={area} />,
    children: [
      { index: true, element: <Page /> },
      { path: '*', element: <Page /> },
    ],
  };
});

export const routes: RouteObject[] = [
  {
    element: <PublicLayout />,
    children: [
      { path: '/', element: <HomePage /> },
      { path: '/verificar', element: <VerificarPage /> },
      { path: '/v/:token', element: <VerificarPage /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
  ...areaRoutes,
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
