import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../router.dart';
import 'deep_link_mapper.dart';

/// Abre uma ligação `moseguros://` vinda de um push ou do centro de
/// notificações (S6-07 §6).
///
/// Navega com `router.go`; o `appRedirect` faz o resto: sem sessão guarda o
/// destino no `PendingDestination`, vai para o login e volta a ele depois.
class DeepLinkHandler {
  DeepLinkHandler(this._router);

  final GoRouter Function() _router;

  /// `null` ou vazio: nada. Uma ligação sem o esquema `moseguros` vai para `/`.
  void open(String? link) {
    if (link == null || link.trim().isEmpty) return;
    _router().go(DeepLinkMapper.map(link) ?? '/');
  }
}

final deepLinkHandlerProvider = Provider<DeepLinkHandler>(
  (ref) => DeepLinkHandler(() => ref.read(routerProvider)),
);
