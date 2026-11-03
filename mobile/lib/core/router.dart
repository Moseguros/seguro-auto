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

class _Splash extends StatelessWidget {
  const _Splash();

  @override
  Widget build(BuildContext context) =>
      const Scaffold(body: Center(child: CircularProgressIndicator()));
}
