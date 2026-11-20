import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/auth/session_controller.dart';
import 'package:moseguros/core/auth/token_storage.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/push/device_registration.dart';
import 'package:moseguros/core/push/noop_push_service.dart';
import 'package:moseguros/core/push/push_service.dart';
import 'package:moseguros/core/push/push_startup.dart';

import '../../support/mock_api.dart';
import '../../support/notifications.dart';

/// Deixa correr os pedidos simulados e os listeners.
Future<void> _flush() async {
  for (var i = 0; i < 20; i++) {
    await Future<void>.delayed(Duration.zero);
  }
}

void main() {
  late MockApi api;
  late FakePushService push;
  late MemoryDeviceIdStorage ids;

  ProviderContainer start({PushService? service, String? refreshToken}) {
    final db = AppDatabase.memory();
    addTearDown(db.close);
    final container = ProviderContainer(
      overrides: [
        apiClientProvider.overrideWithValue(api.client),
        tokenStorageProvider.overrideWithValue(
          MemoryTokenStorage(refreshToken),
        ),
        pushServiceProvider.overrideWithValue(service ?? push),
        deviceIdStorageProvider.overrideWithValue(ids),
        appVersionProvider.overrideWith((ref) async => '0.1.0+1'),
        appDatabaseProvider.overrideWithValue(db),
      ],
    );
    addTearDown(container.dispose);
    return container;
  }

  setUp(() {
    api = MockApi()
      ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
      ..on('GET', '/api/v1/me', 200, Fixtures.me())
      ..on('POST', devicesPath, 201, NotificationFixtures.device())
      ..on('DELETE', '$devicesPath/$deviceId', 204)
      ..on('POST', '/api/v1/auth/logout', 204);
    push = FakePushService();
    ids = MemoryDeviceIdStorage();
  });

  test('regista o dispositivo no arranque com sessão', () async {
    final container = start(refreshToken: 'refresh-0');
    container.read(pushStartupProvider);
    await container.read(sessionControllerProvider.future);
    await _flush();

    final calls = api.calls('POST', devicesPath);
    expect(calls, isNotEmpty);
    expect(calls.first.data, {
      'token': 'fcm-token-1',
      'platform': 'ANDROID',
      'appVersion': '0.1.0+1',
    });
    expect(calls.first.headers['Authorization'], 'Bearer access-1');
    expect(ids.id, deviceId);
  });

  test('sem sessão não regista', () async {
    final container = start();
    container.read(pushStartupProvider);
    await container.read(sessionControllerProvider.future);
    await _flush();
    expect(api.calls('POST', devicesPath), isEmpty);
  });

  test('regista de novo quando o token muda', () async {
    final container = start(refreshToken: 'refresh-0');
    container.read(pushStartupProvider);
    await container.read(sessionControllerProvider.future);
    await _flush();
    final before = api.calls('POST', devicesPath).length;

    push.tokens.add('fcm-token-2');
    await _flush();

    final calls = api.calls('POST', devicesPath);
    expect(calls.length, before + 1);
    expect((calls.last.data as Map)['token'], 'fcm-token-2');
  });

  test('NoopPushService: sem token não regista', () async {
    final container = start(
      service: const NoopPushService(),
      refreshToken: 'refresh-0',
    );
    container.read(pushStartupProvider);
    await container.read(sessionControllerProvider.future);
    await _flush();
    expect(api.calls('POST', devicesPath), isEmpty);
  });

  test('logout anula o registo antes de largar o token', () async {
    final container = start(refreshToken: 'refresh-0');
    container.read(pushStartupProvider);
    await container.read(sessionControllerProvider.future);
    await _flush();
    expect(ids.id, deviceId);

    await container.read(sessionControllerProvider.notifier).logout();

    final unregister = api.calls('DELETE', '$devicesPath/$deviceId').single;
    expect(unregister.headers['Authorization'], 'Bearer access-1');
    expect(ids.id, isNull);
    expect(container.read(sessionControllerProvider).value, isNull);
  });
}
