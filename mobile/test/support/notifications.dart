import 'dart:async';

import 'package:moseguros/core/push/push_service.dart';

/// Caminhos e respostas simuladas das notificações (S6-04/S6-07).
const myNotificationsPath = '/api/v1/me/notifications';
const unreadCountPath = '/api/v1/me/notifications/unread-count';
const readAllPath = '/api/v1/me/notifications/read-all';
const preferencesPath = '/api/v1/me/notification-preferences';
const devicesPath = '/api/v1/me/devices';
const deviceId = '01970000-0000-7000-8000-000000000a01';
const notificationId = '01970000-0000-7000-8000-000000000b01';

String readPath(String id) => '/api/v1/me/notifications/$id/read';

abstract final class NotificationFixtures {
  static Map<String, Object?> notification({
    String id = notificationId,
    String category = 'POLICY',
    String title = 'Apólice emitida',
    String body = 'A sua apólice AP-2027-000123 já está disponível.',
    String? deepLink,
    String? readAt,
    String createdAt = '2027-01-10T08:55:00Z',
  }) => {
    'id': id,
    'category': category,
    'templateCode': 'POLICY_ISSUED',
    'title': title,
    'body': body,
    'deepLink': ?deepLink,
    'readAt': ?readAt,
    'createdAt': createdAt,
  };

  static Map<String, Object?> page(
    List<Map<String, Object?>> content, {
    int? total,
  }) => {
    'content': content,
    'page': 0,
    'size': 20,
    'totalElements': total ?? content.length,
  };

  static Map<String, Object?> channel(
    String channel, {
    bool enabled = true,
    bool available = true,
  }) => {'channel': channel, 'enabled': enabled, 'available': available};

  static Map<String, Object?> preferences({
    bool pushAvailable = false,
    bool emailAvailable = true,
  }) => {
    'categories': [
      for (final (category, mandatory) in [
        ('POLICY', true),
        ('PROPOSAL', false),
      ])
        {
          'category': category,
          'mandatory': mandatory,
          'channels': [
            channel('SMS'),
            channel('WHATSAPP', enabled: false),
            channel('EMAIL', enabled: false, available: emailAvailable),
            channel('PUSH', enabled: false, available: pushAvailable),
          ],
        },
    ],
  };

  static Map<String, Object?> device() => {
    'id': deviceId,
    'platform': 'ANDROID',
    'active': true,
  };
}

/// Push falso: devolve [pushToken] e deixa emitir tokens e mensagens.
class FakePushService implements PushService {
  FakePushService({this.pushToken = 'fcm-token-1', this.granted = true});

  String? pushToken;
  bool granted;
  int permissionRequests = 0;
  PushMessage? initial;
  final tokens = StreamController<String>.broadcast();
  final opened = StreamController<PushMessage>.broadcast();
  final foreground = StreamController<PushMessage>.broadcast();

  @override
  bool get supported => true;

  @override
  Future<void> init() async {}

  @override
  Future<bool> requestPermission() async {
    permissionRequests++;
    return granted;
  }

  @override
  Future<String?> token() async => pushToken;

  @override
  Stream<String> get onTokenRefresh => tokens.stream;

  @override
  Stream<PushMessage> get onMessageOpened => opened.stream;

  @override
  Stream<PushMessage> get onForegroundMessage => foreground.stream;

  @override
  Future<PushMessage?> initialMessage() async => initial;
}
