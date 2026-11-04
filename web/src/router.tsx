import type { ComponentType, ReactElement } from 'react';
import { createBrowserRouter, type RouteObject } from 'react-router-dom';
import { AuthRoot } from './auth/AuthProvider';
import { Permissions, RequirePermission, type Permission } from './auth/permissions';
import { RequireAuth } from './auth/RequireAuth';
import { OtpSettingsPage } from './features/admin-auth/OtpSettingsPage';
import {
  InspectionQueuePage,
  KycQueuePage,
  KycReviewPage,
  ProposalDetailRoute,
  ProposalQueueRoute,
} from './features/insurer-proposals/lazyPages';
import { CustomerLoginPage } from './features/customer-auth/CustomerLoginPage';
import { CustomerHomePage } from './features/customer-profile/CustomerHomePage';
import { DriverFormPage } from './features/drivers/DriverFormPage';
import { DriverListPage } from './features/drivers/DriverListPage';
import { ProfilePage } from './features/customer-profile/ProfilePage';
import { OrganizationCreatePage } from './features/organizations/OrganizationCreatePage';
import { OrganizationDetailPage } from './features/organizations/OrganizationDetailPage';
import { OrganizationListPage } from './features/organizations/OrganizationListPage';
import { ProductDetailPage } from './features/products/ProductDetailPage';
import {
  BrokerNewProposalPage,
  BrokerProposalPage,
  BrokerProposalsPage,
  CustomerProposalPage,
  IdentityPage,
  MyProposalsPage,
  NewProposalPage,
} from './features/proposal-wizard/lazyPages';
import { ProductListPage } from './features/products/ProductListPage';
import { ProductVersionEditorPage } from './features/products/ProductVersionEditorPage';
import { BrokerQuotesPage } from './features/quotes/BrokerQuotesPage';
import { MyQuotesPage } from './features/quotes/MyQuotesPage';
import { QuoteComparePage } from './features/quotes/QuoteComparePage';
import { QuoteDetailPage } from './features/quotes/QuoteDetailPage';
import { QuoteResultsPage } from './features/quotes/QuoteResultsPage';
import { QuoteWizard } from './features/quotes/QuoteWizard';
import { ReferenceTablesPage } from './features/references/ReferenceTablesPage';
import { TariffEditorPage } from './features/tariffs/TariffEditorPage';
import { TariffsPage } from './features/tariffs/TariffsPage';
import { ForgotPasswordPage } from './features/staff-auth/ForgotPasswordPage';
import { InvitationPage } from './features/staff-auth/InvitationPage';
import { ResetPasswordPage } from './features/staff-auth/ResetPasswordPage';
import { StaffLoginPage } from './features/staff-auth/StaffLoginPage';
import { UsersPage } from './features/users/UsersPage';
import { VehicleDetailPage } from './features/vehicles/VehicleDetailPage';
import { VehicleFormPage } from './features/vehicles/VehicleFormPage';
import { VehicleListPage } from './features/vehicles/VehicleListPage';
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

type StaffArea = Exclude<Area, 'cliente'>;

/** Página inicial de cada área de staff (por agora vazias). */
const areaPages: Record<StaffArea, ComponentType> = {
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

const withPermission = (permission: Permission, element: ReactElement) => (
  <RequirePermission permission={permission}>{element}</RequirePermission>
);

/** Páginas da administração da plataforma. */
const adminRoutes: RouteObject[] = [
  {
    path: 'autenticacao',
    element: withPermission(Permissions.otpSettingsRead, <OtpSettingsPage />),
  },
  {
    path: 'organizacoes',
    element: withPermission(Permissions.organizationRead, <OrganizationListPage />),
  },
  {
    path: 'organizacoes/nova',
    element: withPermission(Permissions.organizationManage, <OrganizationCreatePage />),
  },
  {
    path: 'organizacoes/:id',
    element: withPermission(Permissions.organizationRead, <OrganizationDetailPage />),
  },
  {
    path: 'utilizadores',
    element: withPermission(Permissions.orgUserRead, <UsersPage platform />),
  },
  {
    path: 'referencias',
    element: withPermission(Permissions.referenceManage, <ReferenceTablesPage />),
  },
];

/** Páginas comuns às áreas das organizações. */
const organizationRoutes: RouteObject[] = [
  { path: 'utilizadores', element: withPermission(Permissions.orgUserRead, <UsersPage />) },
];

/** Páginas próprias da área das seguradoras. */
const insurerRoutes: RouteObject[] = [
  {
    path: 'produtos',
    element: withPermission(Permissions.productRead, <ProductListPage />),
  },
  {
    path: 'produtos/:productId',
    element: withPermission(Permissions.productRead, <ProductDetailPage />),
  },
  {
    path: 'produtos/:productId/versoes/:versionId',
    element: withPermission(Permissions.productRead, <ProductVersionEditorPage />),
  },
  {
    path: 'tarifas',
    element: withPermission(Permissions.productRead, <TariffsPage />),
  },
  {
    path: 'tarifas/:versionId',
    element: withPermission(Permissions.productRead, <TariffEditorPage />),
  },
  // Propostas, KYC e vistorias (S4-07): cada fila com a sua permissão; o detalhe com
  // `proposal:read` ou, só a vistoria, com `inspection:review`.
  { path: 'propostas', element: <ProposalQueueRoute /> },
  { path: 'propostas/kyc', element: withPermission(Permissions.kycReview, <KycQueuePage />) },
  {
    path: 'propostas/kyc/:id',
    element: withPermission(Permissions.kycReview, <KycReviewPage />),
  },
  {
    path: 'propostas/vistorias',
    element: withPermission(Permissions.inspectionReview, <InspectionQueuePage />),
  },
  { path: 'propostas/:id', element: <ProposalDetailRoute /> },
];

/** Cotações do mediador (S3-04): só com `quote:create-for-client`. */
const brokerRoutes: RouteObject[] = [
  {
    path: 'cotacoes',
    element: withPermission(Permissions.quoteCreateForClient, <BrokerQuotesPage />),
  },
  {
    path: 'cotacoes/nova',
    element: withPermission(Permissions.quoteCreateForClient, <QuoteWizard mode="broker" />),
  },
  {
    path: 'cotacoes/:requestId',
    element: withPermission(Permissions.quoteCreateForClient, <QuoteResultsPage mode="broker" />),
  },
  {
    path: 'cotacoes/:requestId/comparar',
    element: withPermission(Permissions.quoteCreateForClient, <QuoteComparePage mode="broker" />),
  },
  {
    path: 'cotacoes/:requestId/:quoteId',
    element: withPermission(Permissions.quoteCreateForClient, <QuoteDetailPage mode="broker" />),
  },
  // Propostas em nome do cliente (S4-08): só com `proposal:create-for-client`.
  {
    path: 'propostas',
    element: withPermission(Permissions.proposalCreateForClient, <BrokerProposalsPage />),
  },
  {
    path: 'propostas/nova',
    element: withPermission(Permissions.proposalCreateForClient, <BrokerNewProposalPage />),
  },
  {
    path: 'propostas/:id',
    element: withPermission(Permissions.proposalCreateForClient, <BrokerProposalPage />),
  },
];

/** Páginas próprias de cada área de staff, além das comuns. */
const areaExtraRoutes: Record<StaffArea, RouteObject[]> = {
  admin: [],
  seguradora: insurerRoutes,
  mediador: brokerRoutes,
  perito: [],
  oficina: [],
  assistencia: [],
  provedor: [],
  supervisao: [],
  frota: [],
};

/**
 * Cada área de staff: só com sessão de staff da própria área. `/<área>` mostra a página da área
 * e os caminhos ainda sem página mostram a página da área (por agora "Em construção").
 */
const areaRoutes: RouteObject[] = (Object.keys(areaPages) as StaffArea[]).map((area) => {
  const Page = areaPages[area];
  return {
    path: `/${area}`,
    element: (
      <RequireAuth type="STAFF" area={area}>
        <AppLayout area={area} />
      </RequireAuth>
    ),
    children: [
      { index: true, element: <Page /> },
      ...(area === 'admin' ? adminRoutes : organizationRoutes),
      ...areaExtraRoutes[area],
      { path: '*', element: <Page /> },
    ],
  };
});

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
    { path: 'veiculos', element: <VehicleListPage /> },
    { path: 'veiculos/novo', element: <VehicleFormPage /> },
    { path: 'veiculos/:id', element: <VehicleDetailPage /> },
    { path: 'veiculos/:id/editar', element: <VehicleFormPage /> },
    { path: 'condutores', element: <DriverListPage /> },
    { path: 'condutores/novo', element: <DriverFormPage /> },
    { path: 'condutores/:id/editar', element: <DriverFormPage /> },
    // Decisão D1: só `/cliente/simular` (sem `/cliente/cotar` nem redireccionamento).
    { path: 'simular', element: <QuoteWizard mode="customer" /> },
    { path: 'simular/:requestId', element: <QuoteResultsPage mode="customer" /> },
    { path: 'simular/:requestId/comparar', element: <QuoteComparePage mode="customer" /> },
    { path: 'simular/:requestId/:quoteId', element: <QuoteDetailPage mode="customer" /> },
    { path: 'cotacoes', element: <MyQuotesPage /> },
    // Propostas e verificação de identidade (S4-08).
    { path: 'propostas', element: <MyProposalsPage /> },
    { path: 'propostas/nova', element: <NewProposalPage /> },
    { path: 'propostas/:id', element: <CustomerProposalPage /> },
    { path: 'identidade', element: <IdentityPage /> },
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
          { path: '/entrar', element: <StaffLoginPage /> },
          { path: '/recuperar', element: <ForgotPasswordPage /> },
          { path: '/recuperar/:token', element: <ResetPasswordPage /> },
          { path: '/convite/:token', element: <InvitationPage /> },
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
