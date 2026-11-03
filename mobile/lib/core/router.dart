import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/auth/code_screen.dart';
import '../features/auth/login_screen.dart';
import '../features/home/home_screen.dart';
import '../features/onboarding/onboarding_controller.dart';
import '../features/onboarding/onboarding_screen.dart';
import '../features/profile/profile_screen.dart';
import 'auth/session_controller.dart';
import 'not_found_screen.dart';

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

/// Rotas da app. Deep links `moseguros://` chegam em sprints seguintes.
final routerProvider = Provider<GoRouter>((ref) {
  // Volta a avaliar a redirecção quando a sessão ou a apresentação mudam.
  final refresh = ValueNotifier<int>(0);
  ref.listen(sessionControllerProvider, (_, _) => refresh.value++);
  ref.listen(onboardingControllerProvider, (_, _) => refresh.value++);

  final router = GoRouter(
    initialLocation: '/',
    refreshListenable: refresh,
    redirect: (context, state) => appRedirect(
      onboardingDone: ref.read(onboardingControllerProvider),
      session: ref.read(sessionControllerProvider),
      location: state.matchedLocation,
    ),
    routes: [
      GoRoute(path: '/', builder: (context, state) => const HomeScreen()),
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
      GoRoute(
        path: '/profile',
        builder: (context, state) => ProfileScreen(
          completing: state.uri.queryParameters['complete'] == '1',
        ),
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
