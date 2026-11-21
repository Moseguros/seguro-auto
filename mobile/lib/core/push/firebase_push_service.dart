import 'dart:async';
import 'dart:convert';
import 'dart:ui';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';

import '../../l10n/app_localizations.dart';
import 'noop_push_service.dart';
import 'push_service.dart';

/// `--dart-define=PUSH_ENABLED=true` liga o push (precisa do
/// `google-services.json` / `GoogleService-Info.plist` do flavor).
const pushEnabled = bool.fromEnvironment('PUSH_ENABLED');

/// [FirebasePushService] quando [pushEnabled] e o `Firebase.initializeApp()`
/// funciona; senão [NoopPushService].
Future<PushService> createPushService() async {
  if (!pushEnabled) return const NoopPushService();
  try {
    await Firebase.initializeApp();
    return FirebasePushService();
  } on Object catch (error) {
    // Sem configuração do Firebase para este flavor.
    debugPrint('Push desligado: ${error.runtimeType}');
    return const NoopPushService();
  }
}

/// Push com o Firebase Cloud Messaging. As mensagens em primeiro plano
/// mostram-se com o `flutter_local_notifications` (o FCM só as mostra com a app
/// em segundo plano). Os tokens nunca vão para os logs.
class FirebasePushService implements PushService {
  FirebasePushService({
    FirebaseMessaging? messaging,
    FlutterLocalNotificationsPlugin? local,
  }) : _messaging = messaging ?? FirebaseMessaging.instance,
       _local = local ?? FlutterLocalNotificationsPlugin();

  static const _channelId = 'moseguros_default';

  final FirebaseMessaging _messaging;
  final FlutterLocalNotificationsPlugin _local;
  final _opened = StreamController<PushMessage>.broadcast();
  final _foreground = StreamController<PushMessage>.broadcast();
  StreamSubscription<RemoteMessage>? _onMessage;
  StreamSubscription<RemoteMessage>? _onOpened;
  var _nextId = 0;

  @override
  bool get supported => true;

  static PushMessage _fromRemote(RemoteMessage message) => PushMessage.fromData(
    message.data,
    title: message.notification?.title,
    body: message.notification?.body,
  );

  @override
  Future<void> init() async {
    final l10n = lookupAppLocalizations(_deviceLocale());
    await _local.initialize(
      settings: const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(
          requestAlertPermission: false,
          requestBadgePermission: false,
          requestSoundPermission: false,
        ),
      ),
      onDidReceiveNotificationResponse: (response) {
        final payload = response.payload;
        if (payload == null || payload.isEmpty) return;
        try {
          final data = jsonDecode(payload);
          if (data is Map<String, Object?>) {
            _opened.add(PushMessage.fromData(data));
          }
        } on FormatException {
          // Notificação sem dados da app.
        }
      },
    );
    await _local
        .resolvePlatformSpecificImplementation<
          AndroidFlutterLocalNotificationsPlugin
        >()
        ?.createNotificationChannel(
          AndroidNotificationChannel(
            _channelId,
            l10n.pushChannelName,
            importance: Importance.high,
          ),
        );
    await _messaging.setForegroundNotificationPresentationOptions(
      alert: false,
      badge: true,
      sound: false,
    );
    _onMessage = FirebaseMessaging.onMessage.listen((message) {
      final push = _fromRemote(message);
      unawaited(_show(push, message.data, l10n));
      _foreground.add(push);
    });
    _onOpened = FirebaseMessaging.onMessageOpenedApp.listen(
      (message) => _opened.add(_fromRemote(message)),
    );
  }

  Future<void> _show(
    PushMessage push,
    Map<String, dynamic> data,
    AppLocalizations l10n,
  ) async {
    if (push.title == null && push.body == null) return;
    await _local.show(
      id: _nextId++,
      title: push.title,
      body: push.body,
      notificationDetails: NotificationDetails(
        android: AndroidNotificationDetails(
          _channelId,
          l10n.pushChannelName,
          importance: Importance.high,
          priority: Priority.high,
        ),
        iOS: const DarwinNotificationDetails(),
      ),
      payload: jsonEncode({
        'notificationId': ?push.notificationId,
        'deepLink': ?push.deepLink,
      }),
    );
  }

  @override
  Future<bool> requestPermission() async {
    final settings = await _messaging.requestPermission();
    return settings.authorizationStatus == AuthorizationStatus.authorized ||
        settings.authorizationStatus == AuthorizationStatus.provisional;
  }

  @override
  Future<String?> token() async {
    try {
      final settings = await _messaging.getNotificationSettings();
      if (settings.authorizationStatus == AuthorizationStatus.denied) {
        return null;
      }
      // O backend (S6-04, `FcmPushSender`) envia para o token de registo do
      // FCM; o `register()`/`onRegistered` novo devolve o FID (envio directo).
      // ignore: deprecated_member_use
      return await _messaging.getToken();
    } on Object {
      return null;
    }
  }

  @override
  // ignore: deprecated_member_use
  Stream<String> get onTokenRefresh => _messaging.onTokenRefresh;

  @override
  Stream<PushMessage> get onMessageOpened => _opened.stream;

  @override
  Stream<PushMessage> get onForegroundMessage => _foreground.stream;

  @override
  Future<PushMessage?> initialMessage() async {
    final message = await _messaging.getInitialMessage();
    return message == null ? null : _fromRemote(message);
  }

  /// Liberta as subscrições (não é chamado em produção: vive com a app).
  Future<void> dispose() async {
    await _onMessage?.cancel();
    await _onOpened?.cancel();
    await _opened.close();
    await _foreground.close();
  }

  static Locale _deviceLocale() {
    final device = PlatformDispatcher.instance.locale;
    return AppLocalizations.supportedLocales.any(
          (locale) => locale.languageCode == device.languageCode,
        )
        ? Locale(device.languageCode)
        : const Locale('pt');
  }
}
