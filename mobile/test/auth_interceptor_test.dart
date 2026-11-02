import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/auth/session_controller.dart';
import 'package:moseguros/core/auth/token_storage.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros/core/errors/problem.dart';

import 'support/mock_api.dart';

void main() {
  late MockApi api;
  late MemoryTokenStorage storage;
  late ProviderContainer container;

  setUp(() {
    api = MockApi();
    storage = MemoryTokenStorage('refresh-0');
    container = ProviderContainer(
      overrides: [
        envProvider.overrideWithValue(testEnv),
        apiClientProvider.overrideWithValue(api.client),
        tokenStorageProvider.overrideWithValue(storage),
      ],
    );
    addTearDown(container.dispose);
  });

  /// Arranque com refresh token guardado: `refreshToken` dá `access-1`.
  Future<SessionController> startSession() async {
    api
      ..on(
        'POST',
        '/api/v1/auth/refresh',
        200,
        Fixtures.tokens(access: 'access-1', refresh: 'refresh-1'),
      )
      ..on('GET', '/api/v1/me', 200, Fixtures.me());
    final session = await container.read(sessionControllerProvider.future);
    expect(session?.accessToken, 'access-1');
    expect(storage.refreshToken, 'refresh-1');
    api.requests.clear();
    return container.read(sessionControllerProvider.notifier);
  }

  test('três 401 em simultâneo levam a um só refreshToken', () async {
    await startSession();
    api
      ..on(
        'POST',
        '/api/v1/auth/refresh',
        200,
        Fixtures.tokens(access: 'access-2', refresh: 'refresh-2'),
      )
      // Token antigo → 401; token novo → 200.
      ..on(
        'GET',
        '/api/v1/me',
        401,
        MockApi.problem(401, 'UNAUTHORIZED'),
        const {},
        {'Authorization': 'Bearer access-1'},
      )
      ..on('GET', '/api/v1/me', 200, Fixtures.me(), const {}, {
        'Authorization': 'Bearer access-2',
      });

    final identity = api.client.identity;
    final responses = await Future.wait([
      identity.getMe(),
      identity.getMe(),
      identity.getMe(),
    ]);

    expect(responses.map((r) => r.statusCode), [200, 200, 200]);
    expect(api.calls('POST', '/api/v1/auth/refresh'), hasLength(1));
    expect(
      api
          .calls('GET', '/api/v1/me')
          .where((r) => r.headers['Authorization'] == 'Bearer access-2'),
      hasLength(3),
    );
    expect(storage.refreshToken, 'refresh-2');
    expect(
      container.read(sessionControllerProvider).value?.accessToken,
      'access-2',
    );
  });

  test('pedidos a /api/v1/auth/** não levam Authorization', () async {
    await startSession();
    api
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile());

    await api.client.identity.getOtpOptions();
    await api.client.identity.getMyProfile();

    final options = api.calls('GET', '/api/v1/auth/otp/options').single;
    expect(options.headers.containsKey('Authorization'), isFalse);
    expect(options.headers['Accept-Language'], 'pt-MZ');
    final profile = api.calls('GET', '/api/v1/me/profile').single;
    expect(profile.headers['Authorization'], 'Bearer access-1');
    expect(profile.headers['Accept-Language'], 'pt-MZ');
  });

  test('renovação recusada termina a sessão e apaga o refresh token', () async {
    await startSession();
    api
      ..on(
        'POST',
        '/api/v1/auth/refresh',
        401,
        MockApi.problem(401, 'REFRESH_TOKEN_INVALID'),
      )
      ..on('GET', '/api/v1/me', 401, MockApi.problem(401, 'UNAUTHORIZED'));

    await expectLater(
      api.client.identity.getMe(),
      throwsA(predicate((Object e) => Problem.from(e).code == 'UNAUTHORIZED')),
    );
    expect(container.read(sessionControllerProvider).value, isNull);
    expect(storage.refreshToken, isNull);
  });

  test(
    'ao arrangar, refresh token recusado fica anónimo e é apagado',
    () async {
      api.on(
        'POST',
        '/api/v1/auth/refresh',
        401,
        MockApi.problem(401, 'REFRESH_TOKEN_INVALID'),
      );
      expect(await container.read(sessionControllerProvider.future), isNull);
      expect(storage.refreshToken, isNull);
    },
  );

  test('logout chama logoutSession e apaga o refresh token', () async {
    final controller = await startSession();
    api.on('POST', '/api/v1/auth/logout', 204);
    await controller.logout();
    await pumpEventQueue();

    expect(container.read(sessionControllerProvider).value, isNull);
    expect(storage.refreshToken, isNull);
    final logout = api.calls('POST', '/api/v1/auth/logout').single;
    expect(logout.data, {'refreshToken': 'refresh-1'});
    expect(logout.headers.containsKey('Authorization'), isFalse);
  });
}
