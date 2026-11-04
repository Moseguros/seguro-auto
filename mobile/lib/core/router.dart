import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/auth/code_screen.dart';
import '../features/auth/login_screen.dart';
import '../features/drivers/driver_form_screen.dart';
import '../features/drivers/drivers_screen.dart';
import '../features/home/home_screen.dart';
import '../features/onboarding/onboarding_controller.dart';
import '../features/onboarding/onboarding_screen.dart';
import '../features/profile/profile_screen.dart';
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

/// Ecrãs de entrada: com sessão, saem para o perfil (se incompleto) ou o início.
bool _isEntry(String location) =>
    location == '/onboarding' ||
    location == splashPath ||
    location == '/login' ||
    location.startsWith('/login/');

/// Redirecção global:
///
/// | Estado | Destino |
/// | --- | --- |
/// | primeira abertura (`onboarding_done` ausente) | `/onboarding` |
/// | sessão a carregar | `/splash` |
/// | sem sessão | `/login` |
/// | com sessão e perfil incompleto, à entrada | `/profile?complete=1` |
/// | com sessão, à entrada | `/` |
String? appRedirect({
  required bool onboardingDone,
  required AsyncValue<Session?> session,
  required String location,
}) {
  if (!onboardingDone) return location == '/onboarding' ? null : '/onboarding';
  if (!session.hasValue) return location == splashPath ? null : splashPath;
  final current = session.value;
  if (current == null) {
    return location == '/login' || location.startsWith('/login/')
        ? null
        : '/login';
  }
  if (_isEntry(location)) {
    return current.needsProfile ? '/profile?complete=1' : '/';
  }
  return null;
}

/// Navegador da raiz: ecrãs de entrada e ecrãs inteiros sem a barra inferior
/// (câmara do livrete).
final _rootNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'root');

/// Rotas da app. Deep links `moseguros://` chegam em sprints seguintes.
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
/// | `/profile` (`?complete=1`) | perfil (barra: Perfil) |
final routerProvider = Provider<GoRouter>((ref) {
  // Volta a avaliar a redirecção quando a sessão ou a apresentação mudam.
  final refresh = ValueNotifier<int>(0);
  ref.listen(sessionControllerProvider, (_, _) => refresh.value++);
  ref.listen(onboardingControllerProvider, (_, _) => refresh.value++);

  final router = GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: '/',
    refreshListenable: refresh,
    redirect: (context, state) => appRedirect(
      onboardingDone: ref.read(onboardingControllerProvider),
      session: ref.read(sessionControllerProvider),
      location: state.matchedLocation,
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
