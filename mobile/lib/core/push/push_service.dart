import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'noop_push_service.dart';

/// Uma notificação push recebida. Os dados do backend (S6-04) são
/// `{ notificationId, deepLink }`; `deepLink` falta quando não há ecrã a abrir.
/// Não trazem a categoria.
class PushMessage {
  const PushMessage({
    this.notificationId,
    this.deepLink,
    this.title,
    this.body,
  });

  /// Lê os dados (`data`) de uma mensagem.
  factory PushMessage.fromData(
    Map<String, Object?> data, {
    String? title,
    String? body,
  }) {
    String? text(Object? value) =>
        value is String && value.trim().isNotEmpty ? value.trim() : null;
    return PushMessage(
      notificationId: text(data['notificationId']),
      deepLink: text(data['deepLink']),
      title: title,
      body: body,
    );
  }

  final String? notificationId;
  final String? deepLink;
  final String? title;
  final String? body;
}

/// Notificações push do telemóvel (S6-07 §4).
///
/// [FirebasePushService] com `--dart-define=PUSH_ENABLED=true` e o Firebase
/// configurado; senão [NoopPushService] (a app corre sem projecto Firebase).
abstract interface class PushService {
  /// `false` no [NoopPushService]: não há push, nem pedido de permissão, nem
  /// botão "Activar" nas preferências.
  bool get supported;

  /// Prepara o serviço (canais, mensagens). Chamado uma vez no arranque.
  Future<void> init();

  /// Pede autorização para mostrar avisos (Android 13+ `POST_NOTIFICATIONS`,
  /// iOS). `true` se ficou autorizado.
  Future<bool> requestPermission();

  /// Token do dispositivo, ou `null` sem push ou sem autorização.
  Future<String?> token();

  /// Token novo (o anterior deixou de valer).
  Stream<String> get onTokenRefresh;

  /// Toque numa notificação, com a app em segundo plano ou fechada.
  Stream<PushMessage> get onMessageOpened;

  /// Mensagem recebida com a app em primeiro plano (já mostrada no telemóvel).
  Stream<PushMessage> get onForegroundMessage;

  /// Notificação que abriu a app quando estava fechada, se houver (uma vez).
  Future<PushMessage?> initialMessage();
}

/// O serviço de push da app; `runMoSeguros` substitui-o quando o Firebase
/// arranca (`createPushService`).
final pushServiceProvider = Provider<PushService>(
  (ref) => const NoopPushService(),
);
