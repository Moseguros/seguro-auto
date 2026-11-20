import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../features/notifications/notifications_controller.dart';
import '../../features/policies/policies_controller.dart';
import '../auth/session_controller.dart';
import '../deeplinks/deep_link_handler.dart';
import '../deeplinks/deep_link_mapper.dart';
import 'device_registration.dart';
import 'push_service.dart';

/// Id da apólice de uma ligação `moseguros://policy/{id}`, se for uma.
String? policyIdOfLink(String? link) {
  final location = DeepLinkMapper.map(link);
  if (location == null || !location.startsWith('/policies/')) return null;
  return location.substring('/policies/'.length);
}

/// Liga o push à app (S6-07 §4); vive enquanto a app vive (`MoSegurosApp`):
///
/// - registo do dispositivo em cada arranque com sessão, a cada login e quando
///   o token muda;
/// - mensagem em primeiro plano (já mostrada pelo serviço): actualiza a
///   contagem de não lidas e a apólice da ligação;
/// - toque numa notificação: marca-a como lida (sem esperar) e abre a ligação
///   pelo [DeepLinkHandler].
final pushStartupProvider = Provider<void>((ref) {
  final push = ref.watch(pushServiceProvider);
  final registration = ref.read(deviceRegistrationProvider);
  final subscriptions = <StreamSubscription<Object?>>[];
  ref.onDispose(() {
    for (final subscription in subscriptions) {
      unawaited(subscription.cancel());
    }
  });

  void refreshFor(PushMessage message) {
    ref.invalidate(unreadCountProvider);
    final policyId = policyIdOfLink(message.deepLink);
    if (policyId != null) {
      unawaited(ref.read(policySyncProvider.notifier).syncPolicy(policyId));
    }
  }

  void opened(PushMessage message) {
    final id = message.notificationId;
    if (id != null && ref.read(sessionControllerProvider).value != null) {
      ref.read(notificationActionsProvider).markRead(id);
    }
    refreshFor(message);
    ref.read(deepLinkHandlerProvider).open(message.deepLink);
  }

  ref.listen(sessionControllerProvider, (previous, next) {
    if (next.value != null && previous?.value == null) {
      unawaited(registration.register());
    }
  }, fireImmediately: true);

  if (!push.supported) return;
  unawaited(() async {
    try {
      await push.init();
    } on Object {
      return;
    }
    subscriptions
      ..add(
        push.onTokenRefresh.listen(
          (token) => unawaited(registration.register(token: token)),
        ),
      )
      ..add(push.onForegroundMessage.listen(refreshFor))
      ..add(push.onMessageOpened.listen(opened));
    final initial = await push.initialMessage();
    if (initial != null) opened(initial);
    // O registo do arranque pode ter corrido antes do `init`.
    unawaited(registration.register());
  }());
});
