import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/core/auth/token_storage.dart';

import '../test/support/mock_api.dart';

/// Fluxo principal da app com a API simulada (`http_mock_adapter`).
///
/// Num emulador Android:
///   flutter test integration_test/login_flow_test.dart --flavor dev
/// Num dispositivo o tempo é real: espera até [finder] aparecer (no máximo 10 s).
Future<void> pumpUntilFound(WidgetTester tester, Finder finder) async {
  for (var i = 0; i < 100 && finder.evaluate().isEmpty; i++) {
    await tester.pump(const Duration(milliseconds: 100));
  }
  await tester.pumpAndSettle();
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  // Sobrevive entre os dois testes, como o armazenamento seguro do telemóvel.
  final storage = MemoryTokenStorage();

  testWidgets(
    'primeira abertura → apresentação → login → código → completar perfil → início',
    (tester) async {
      final api = MockApi()
        ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
        ..on('POST', '/api/v1/auth/otp/request', 202, Fixtures.otpRequest())
        ..on('GET', '/api/v1/auth/otp/challenges/c1', 200, Fixtures.challenge())
        ..on(
          'POST',
          '/api/v1/auth/otp/verify',
          200,
          Fixtures.tokens(isNewUser: true),
        )
        ..on(
          'GET',
          '/api/v1/me',
          200,
          Fixtures.me(displayName: null, profileComplete: false),
        )
        ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile())
        ..on('GET', '/api/v1/me/contacts', 200, Fixtures.contacts())
        ..on('GET', '/api/v1/meta/version', 200, {'version': '1.0.0'});

      await pumpMoSeguros(tester, api, onboardingDone: false, storage: storage);

      // Apresentação.
      expect(find.text('Compare seguradoras'), findsOneWidget);
      await tester.tap(find.text('Seguinte'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Seguinte'));
      await tester.pumpAndSettle();
      await tester.tap(find.text('Começar'));
      await tester.pumpAndSettle();

      // Login.
      await pumpUntilFound(tester, find.byKey(const Key('msisdn-field')));
      expect(find.text('Entrar na área do cliente'), findsOneWidget);
      await tester.enterText(
        find.byKey(const Key('msisdn-field')),
        '841234567',
      );
      await tester.tap(find.byKey(const Key('request-code')));
      await tester.pumpAndSettle();

      // Código.
      await pumpUntilFound(tester, find.byKey(const Key('otp-code-input')));
      expect(
        find.text('Enviámos um código por SMS para +258*****4567.'),
        findsOneWidget,
      );
      await tester.enterText(find.byKey(const Key('otp-code-input')), '123456');
      await tester.pumpAndSettle();

      // Completar o perfil.
      await pumpUntilFound(tester, find.byKey(const Key('profile-fullName')));
      expect(storage.refreshToken, 'refresh-1');
      expect(
        find.text('Precisamos destes dados para emitir as suas apólices.'),
        findsOneWidget,
      );
      await tester.enterText(
        find.byKey(const Key('profile-fullName')),
        'Ana Machava',
      );
      await tester.tap(find.byKey(const Key('profile-birthDate')));
      await tester.pumpAndSettle();
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      for (final (key, option) in [
        ('profile-idDocType', 'Bilhete de Identidade'),
        ('profile-province', 'Maputo Cidade'),
      ]) {
        await tester.ensureVisible(find.byKey(Key(key)));
        await tester.tap(find.byKey(Key(key)));
        await tester.pumpAndSettle();
        await tester.tap(find.text(option).last);
        await tester.pumpAndSettle();
      }
      await tester.enterText(
        find.byKey(const Key('profile-idDocNumber')),
        '110100123456A',
      );
      await tester.enterText(
        find.byKey(const Key('profile-nuit')),
        '123456789',
      );

      api
        ..on(
          'PUT',
          '/api/v1/me/profile',
          200,
          Fixtures.profile(
            complete: true,
            version: 1,
            fullName: 'Ana Machava',
            birthDate: '1990-05-10',
            idDocType: 'BI',
            idDocNumberMasked: '*********456A',
            nuitMasked: '******789',
            province: 'MAPUTO_CITY',
          ),
        )
        ..on('GET', '/api/v1/me', 200, Fixtures.me(displayName: 'Ana Machava'));
      await tester.ensureVisible(find.byKey(const Key('profile-save')));
      await tester.pumpAndSettle();
      await tester.tap(find.byKey(const Key('profile-save')));
      await tester.pumpAndSettle();

      // Início.
      await pumpUntilFound(tester, find.text('Olá, Ana'));
      expect(find.text('Olá, Ana'), findsOneWidget);
      expect(find.text('Simular seguro'), findsOneWidget);
    },
  );

  testWidgets('reabrir a app com sessão → início directo', (tester) async {
    expect(storage.refreshToken, 'refresh-1');
    final api = MockApi()
      ..on(
        'POST',
        '/api/v1/auth/refresh',
        200,
        Fixtures.tokens(access: 'access-2', refresh: 'refresh-2'),
      )
      ..on('GET', '/api/v1/me', 200, Fixtures.me(displayName: 'Ana Machava'));

    await pumpMoSeguros(tester, api, storage: storage);

    await pumpUntilFound(tester, find.text('Olá, Ana'));
    expect(find.text('Olá, Ana'), findsOneWidget);
    expect(find.text('Simular seguro'), findsOneWidget);
    expect(find.text('Entrar na área do cliente'), findsNothing);
    expect(api.calls('POST', '/api/v1/auth/otp/request'), isEmpty);
    expect(storage.refreshToken, 'refresh-2');
  });
}
