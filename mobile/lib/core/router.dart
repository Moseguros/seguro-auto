import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/auth/code_screen.dart';
import '../features/auth/login_screen.dart';
import '../features/drivers/driver_form_screen.dart';
import '../features/drivers/drivers_screen.dart';
import '../features/home/home_screen.dart';
import '../features/inspection/guided_camera_screen.dart';
import '../features/inspection/inspection_screen.dart';
import '../features/kyc/kyc_screen.dart';
import '../features/onboarding/onboarding_controller.dart';
import '../features/onboarding/onboarding_screen.dart';
import '../features/profile/profile_screen.dart';
import '../features/proposals/new_proposal_screen.dart';
import '../features/proposals/proposal_screen.dart';
import '../features/proposals/proposals_screen.dart';
import '../features/quote/screens/coverages_step_screen.dart';
import '../features/quote/screens/my_quotes_screen.dart';
import '../features/quote/screens/period_step_screen.dart';
import '../features/quote/screens/quote_compare_screen.dart';
import '../features/quote/screens/quote_detail_screen.dart';
import '../features/quote/screens/quote_results_screen.dart';
import '../features/quote/screens/summary_step_screen.dart';
import '../features/quote/screens/type_step_screen.dart';
import '../features/quote/screens/vehicle_step_screen.dart';
import '../features/quote/widgets/step_scaffold.dart' show QuoteSteps;
import '../features/vehicles/camera/document_camera_screen.dart';
import '../features/vehicles/vehicle_detail_screen.dart';
import '../features/vehicles/vehicle_form_screen.dart';
import '../features/vehicles/vehicles_screen.dart';
import 'auth/session_controller.dart';
import 'not_found_screen.dart';
import 'widgets/app_shell.dart';

/// Ecrã enquanto a sessão guardada é renovada ao arrancar.
const splashPath = '/splash';

/// Ecrãs de entrada: com sessão, saem para o destino pedido, o perfil (se
/// incompleto) ou o início.
bool _isEntry(String location) =>
    location == '/onboarding' ||
    location == splashPath ||
    location == '/login' ||
    location.startsWith('/login/');

/// Esquema das ligações profundas da app.
const deepLinkScheme = 'moseguros';

/// Caminho interno de uma ligação profunda (S4-09 §1, formato do S6-07):
///
/// | Ligação | Rota |
/// | --- | --- |
/// | `moseguros://proposal/{id}/inspection` | `/proposals/{id}/inspection` |
/// | `moseguros://proposal/{id}` | `/proposals/{id}` |
///
/// `null` se [uri] não for uma ligação `moseguros://`; `/` se for desconhecida.
String? deepLinkLocation(Uri uri) {
  if (uri.scheme != deepLinkScheme) return null;
  final segments = [
    if (uri.host.isNotEmpty) uri.host,
    ...uri.pathSegments.where((segment) => segment.isNotEmpty),
  ];
  if (segments.length >= 2 && segments.first == 'proposal') {
    final id = Uri.encodeComponent(segments[1]);
    if (segments.length == 2) return '/proposals/$id';
    if (segments.length == 3 && segments[2] == 'inspection') {
      return '/proposals/$id/inspection';
    }
  }
  return '/';
}

/// Um destino de regresso só vale se for um caminho interno (começa por `/`, sem
/// esquema nem anfitrião) e não for um ecrã de entrada.
String? safeReturnPath(String? value) {
  if (value == null || value.isEmpty) return null;
  if (!value.startsWith('/') || value.startsWith('//')) return null;
  final uri = Uri.tryParse(value);
  if (uri == null || uri.hasScheme || uri.hasAuthority) return null;
  if (_isEntry(uri.path)) return null;
  return value;
}

/// Destino pedido antes do login (ligação profunda ou rota protegida), guardado
/// pelo router até a sessão abrir.
class PendingDestination {
  String? value;
}

/// Redirecção global:
///
/// | Estado | Destino |
/// | --- | --- |
/// | ligação `moseguros://…` | a rota interna ([deepLinkLocation]) |
/// | primeira abertura (`onboarding_done` ausente) | `/onboarding` |
/// | sessão a carregar | `/splash` |
/// | sem sessão | `/login?from=<destino>` |
/// | com sessão e perfil incompleto, à entrada | `/profile?complete=1&from=<destino>` |
/// | com sessão, à entrada | o destino pedido, ou `/` |
///
/// O destino pedido (caminho e parâmetros) fica em [pending] enquanto não há
/// sessão; o `?from=` do login também o define.
String? appRedirect({
  required bool onboardingDone,
  required AsyncValue<Session?> session,
  required Uri uri,
  PendingDestination? pending,
}) {
  final link = deepLinkLocation(uri);
  if (link != null) return link;

  final location = uri.path;
  if (session.value == null && !_isEntry(location) && location != '/') {
    // Guarda-se até haver sessão.
    pending?.value = safeReturnPath(
      Uri(
        path: uri.path,
        query: uri.query.isEmpty ? null : uri.query,
      ).toString(),
    );
  }
  if (location == '/login') {
    final from = safeReturnPath(uri.queryParameters['from']);
    if (from != null) pending?.value = from;
  }

  if (!onboardingDone) return location == '/onboarding' ? null : '/onboarding';
  if (!session.hasValue) return location == splashPath ? null : splashPath;
  final current = session.value;
  if (current == null) {
    if (location == '/login' || location.startsWith('/login/')) return null;
    final from = pending?.value;
    return from == null
        ? '/login'
        : Uri(path: '/login', queryParameters: {'from': from}).toString();
  }
  if (_isEntry(location)) {
    final destination = pending?.value;
    pending?.value = null;
    if (current.needsProfile) {
      return Uri(
        path: '/profile',
        queryParameters: {'complete': '1', 'from': ?destination},
      ).toString();
    }
    return destination ?? '/';
  }
  return null;
}

/// Navegador da raiz: ecrãs de entrada e ecrãs inteiros sem a barra inferior
/// (câmara do livrete).
final _rootNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'root');

/// Rotas da app. Ligações `moseguros://` em [deepLinkLocation].
///
/// | Rota | Ecrã |
/// | --- | --- |
/// | `/` | início (barra inferior: Início) |
/// | `/quote` → `/quote/vehicle` (`?redo=`), `/quote/type`, `/quote/coverages`, `/quote/period`, `/quote/summary` | assistente "Simular seguro" (barra: Início) |
/// | `/quote/results/:requestId`, `…/compare?ids=`, `…/quotes/:quoteId` | resultados, comparação e detalhe (barra: Início) |
/// | `/quotes` | as minhas cotações (barra: Início) |
/// | `/vehicles`, `/vehicles/new`, `/vehicles/:id`, `/vehicles/:id/edit` | veículos (barra: Veículos) |
/// | `/vehicles/:id/registration-doc/camera` | câmara guiada, sem a barra |
/// | `/drivers`, `/drivers/new?vehicleId=`, `/drivers/:id/edit` | condutores (barra: Veículos) |
/// | `/proposals`, `/proposals/new?quoteId=`, `/proposals/:id` (`?step=`) | propostas (barra: Início) |
/// | `/proposals/:id/inspection` | vistoria (barra: Início) |
/// | `/proposals/:id/inspection/camera/:slot` (`?then=`) | câmara guiada, sem a barra |
/// | `/identity` | verificação de identidade (barra: Início) |
/// | `/profile` (`?complete=1&from=`) | perfil (barra: Perfil) |
final routerProvider = Provider<GoRouter>((ref) {
  // Volta a avaliar a redirecção quando a sessão ou a apresentação mudam.
  final refresh = ValueNotifier<int>(0);
  final pending = PendingDestination();
  ref.listen(sessionControllerProvider, (_, _) => refresh.value++);
  ref.listen(onboardingControllerProvider, (_, _) => refresh.value++);

  final router = GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: '/',
    refreshListenable: refresh,
    redirect: (context, state) => appRedirect(
      onboardingDone: ref.read(onboardingControllerProvider),
      session: ref.read(sessionControllerProvider),
      uri: state.uri,
      pending: pending,
    ),
    routes: [
      StatefulShellRoute.indexedStack(
        builder: (context, state, shell) => AppShell(navigationShell: shell),
        branches: [
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/',
                builder: (context, state) => const HomeScreen(),
                routes: _quoteRoutes,
              ),
              ..._proposalRoutes,
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/vehicles',
                builder: (context, state) => const VehiclesScreen(),
                routes: [
                  GoRoute(
                    path: 'new',
                    builder: (context, state) => const VehicleFormScreen(),
                  ),
                  GoRoute(
                    path: ':id',
                    builder: (context, state) => VehicleDetailScreen(
                      vehicleId: state.pathParameters['id']!,
                    ),
                    routes: [
                      GoRoute(
                        path: 'edit',
                        builder: (context, state) => VehicleFormScreen(
                          vehicleId: state.pathParameters['id'],
                        ),
                      ),
                      GoRoute(
                        path: 'registration-doc/camera',
                        parentNavigatorKey: _rootNavigatorKey,
                        builder: (context, state) => DocumentCameraScreen(
                          vehicleId: state.pathParameters['id']!,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              GoRoute(
                path: '/drivers',
                builder: (context, state) => const DriversScreen(),
                routes: [
                  GoRoute(
                    path: 'new',
                    builder: (context, state) => DriverFormScreen(
                      vehicleId: state.uri.queryParameters['vehicleId'],
                    ),
                  ),
                  GoRoute(
                    path: ':id/edit',
                    builder: (context, state) =>
                        DriverFormScreen(driverId: state.pathParameters['id']),
                  ),
                ],
              ),
            ],
          ),
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: '/profile',
                builder: (context, state) => ProfileScreen(
                  completing: state.uri.queryParameters['complete'] == '1',
                  returnTo: safeReturnPath(state.uri.queryParameters['from']),
                ),
              ),
            ],
          ),
        ],
      ),
      GoRoute(
        path: '/onboarding',
        builder: (context, state) => const OnboardingScreen(),
      ),
      GoRoute(path: splashPath, builder: (context, state) => const _Splash()),
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
        routes: [
          GoRoute(
            path: 'code',
            builder: (context, state) => const CodeScreen(),
          ),
        ],
      ),
    ],
    errorBuilder: (context, state) => const NotFoundScreen(),
  );
  ref.onDispose(() {
    router.dispose();
    refresh.dispose();
  });
  return router;
});

/// Propostas, vistoria e identidade (S4-09 §1), no ramo do início. A câmara
/// guiada abre-se em ecrã inteiro, sem a barra inferior.
final _proposalRoutes = <RouteBase>[
  GoRoute(
    path: '/proposals',
    builder: (context, state) => const ProposalsScreen(),
    routes: [
      GoRoute(
        path: 'new',
        builder: (context, state) =>
            NewProposalScreen(quoteId: state.uri.queryParameters['quoteId']),
      ),
      GoRoute(
        path: ':id',
        builder: (context, state) => ProposalScreen(
          proposalId: state.pathParameters['id']!,
          step: state.uri.queryParameters['step'],
        ),
        routes: [
          GoRoute(
            path: 'inspection',
            builder: (context, state) =>
                InspectionScreen(proposalId: state.pathParameters['id']!),
            routes: [
              GoRoute(
                path: 'camera/:slot',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => GuidedCameraScreen(
                  proposalId: state.pathParameters['id']!,
                  slot: state.pathParameters['slot']!,
                  then: [
                    for (final slot
                        in (state.uri.queryParameters['then'] ?? '').split(','))
                      if (slot.isNotEmpty) slot,
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
    ],
  ),
  GoRoute(path: '/identity', builder: (context, state) => const KycScreen()),
];

/// "Fazer seguro" (S3-05 §1), no ramo do início: os passos do assistente são
/// empilhados um a um (o "voltar" do Android é o "Voltar" do passo) e cada um
/// tem como nome o seu caminho (para "Alterar" no resumo voltar a ele).
///
/// Os formulários de veículo (`/vehicles/new`, `/vehicles/:id/edit`) abrem-se com
/// `push` a partir daqui; o ramo dos veículos mostra-os e o "voltar" regressa ao
/// passo.
final _quoteRoutes = <RouteBase>[
  GoRoute(path: 'quote', redirect: (context, state) => QuoteSteps.vehicle),
  GoRoute(
    path: 'quote/vehicle',
    name: QuoteSteps.vehicle,
    builder: (context, state) =>
        VehicleStepScreen(redo: state.uri.queryParameters['redo']),
  ),
  GoRoute(
    path: 'quote/type',
    name: QuoteSteps.type,
    builder: (context, state) => const TypeStepScreen(),
  ),
  GoRoute(
    path: 'quote/coverages',
    name: QuoteSteps.coverages,
    builder: (context, state) => const CoveragesStepScreen(),
  ),
  GoRoute(
    path: 'quote/period',
    name: QuoteSteps.period,
    builder: (context, state) => const PeriodStepScreen(),
  ),
  GoRoute(
    path: 'quote/summary',
    name: QuoteSteps.summary,
    builder: (context, state) => const SummaryStepScreen(),
  ),
  GoRoute(
    path: 'quote/results/:requestId',
    builder: (context, state) =>
        QuoteResultsScreen(requestId: state.pathParameters['requestId']!),
    routes: [
      GoRoute(
        path: 'compare',
        builder: (context, state) => QuoteCompareScreen(
          requestId: state.pathParameters['requestId']!,
          ids: [
            for (final id in (state.uri.queryParameters['ids'] ?? '').split(
              ',',
            ))
              if (id.trim().isNotEmpty) id.trim(),
          ],
        ),
      ),
      GoRoute(
        path: 'quotes/:quoteId',
        builder: (context, state) => QuoteDetailScreen(
          requestId: state.pathParameters['requestId']!,
          quoteId: state.pathParameters['quoteId']!,
        ),
      ),
    ],
  ),
  GoRoute(path: 'quotes', builder: (context, state) => const MyQuotesScreen()),
];

class _Splash extends StatelessWidget {
  const _Splash();

  @override
  Widget build(BuildContext context) =>
      const Scaffold(body: Center(child: CircularProgressIndicator()));
}
