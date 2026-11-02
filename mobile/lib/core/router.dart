import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/home/home_screen.dart';
import 'not_found_screen.dart';

/// Rotas da app. Deep links `moseguros://` chegam em sprints seguintes.
final routerProvider = Provider<GoRouter>((ref) {
  final router = GoRouter(
    initialLocation: '/',
    routes: [
      GoRoute(path: '/', builder: (context, state) => const HomeScreen()),
    ],
    errorBuilder: (context, state) => const NotFoundScreen(),
  );
  ref.onDispose(router.dispose);
  return router;
});
