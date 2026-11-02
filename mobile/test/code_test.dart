import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/auth/token_storage.dart';

import 'support/mock_api.dart';

const _otpRequest = '/api/v1/auth/otp/request';
const _otpVerify = '/api/v1/auth/otp/verify';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi()
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on('POST', _otpRequest, 202, Fixtures.otpRequest());
  });

  Finder resendButton() => find.byKey(const Key('resend-code'));

  bool enabled(WidgetTester tester, Finder finder) =>
      tester.widget<ButtonStyleButton>(finder).onPressed != null;

  Future<void> openCodeStep(
    WidgetTester tester, {
    TokenStorage? storage,
  }) async {
    await pumpMoSeguros(tester, api, storage: storage);
    await tester.enterText(find.byKey(const Key('msisdn-field')), '841234567');
    await tester.tap(find.byKey(const Key('request-code')));
    await tester.pumpAndSettle();
    expect(find.text('Escreva o código que recebeu.'), findsOneWidget);
  }

  Future<void> typeCode(WidgetTester tester, String code) async {
    await tester.enterText(find.byKey(const Key('otp-code-input')), code);
    await tester.pumpAndSettle();
  }

  testWidgets('mostra o destino mascarado e o campo com codeLength dígitos', (
    tester,
  ) async {
    await openCodeStep(tester);

    expect(
      find.text('Enviámos um código por SMS para +258*****4567.'),
      findsOneWidget,
    );
    final field = tester.widget<TextField>(
      find.byKey(const Key('otp-code-input')),
    );
    expect(field.autofillHints, [AutofillHints.oneTimeCode]);
    expect(field.keyboardType, TextInputType.number);

    // Só dígitos e no máximo 6: um código colado com espaços fica limpo.
    await tester.enterText(find.byKey(const Key('otp-code-input')), '12 34');
    await tester.pump();
    expect(api.calls('POST', _otpVerify), isEmpty);
  });

  testWidgets('código errado mostra as tentativas restantes', (tester) async {
    api.on(
      'POST',
      _otpVerify,
      400,
      MockApi.problem(400, 'OTP_INVALID', extra: {'attemptsRemaining': 2}),
    );
    await openCodeStep(tester);

    await typeCode(tester, '123456');
    expect(api.calls('POST', _otpVerify).single.data, {
      'challengeId': 'c1',
      'code': '123456',
    });
    expect(find.text('Código errado. Restam 2 tentativas.'), findsOneWidget);

    api.on(
      'POST',
      _otpVerify,
      400,
      MockApi.problem(400, 'OTP_INVALID', extra: {'attemptsRemaining': 1}),
    );
    await typeCode(tester, '654321');
    expect(find.text('Código errado. Resta 1 tentativa.'), findsOneWidget);
  });

  testWidgets('código expirado activa logo o reenvio', (tester) async {
    api.on('POST', _otpVerify, 400, MockApi.problem(400, 'OTP_EXPIRED'));
    await openCodeStep(tester);
    expect(enabled(tester, resendButton()), isFalse);

    await typeCode(tester, '123456');
    expect(find.text('O código expirou. Peça um novo código.'), findsOneWidget);
    expect(enabled(tester, resendButton()), isTrue);
  });

  testWidgets('o reenvio activa no fim da contagem e pede um código novo', (
    tester,
  ) async {
    await openCodeStep(tester);

    expect(find.text('Reenviar código (30 s)'), findsOneWidget);
    expect(enabled(tester, resendButton()), isFalse);

    await tester.pump(const Duration(seconds: 29));
    expect(find.text('Reenviar código (1 s)'), findsOneWidget);
    expect(enabled(tester, resendButton()), isFalse);

    await tester.pump(const Duration(seconds: 1));
    expect(find.text('Reenviar código'), findsOneWidget);
    expect(enabled(tester, resendButton()), isTrue);

    api.on('POST', _otpRequest, 202, Fixtures.otpRequest(challengeId: 'c2'));
    await tester.tap(resendButton());
    await tester.pumpAndSettle();

    expect(api.calls('POST', _otpRequest), hasLength(2));
    expect(find.text('Enviámos um código novo.'), findsOneWidget);
    expect(find.text('Reenviar código (30 s)'), findsOneWidget);
  });

  testWidgets('fallbackUsed na resposta mostra o aviso de alternativa', (
    tester,
  ) async {
    api.on(
      'POST',
      _otpRequest,
      202,
      Fixtures.otpRequest(channel: 'WHATSAPP', fallbackUsed: true),
    );
    await openCodeStep(tester);

    expect(
      find.text(
        'Não conseguimos enviar por SMS. Enviámos o código por WhatsApp.',
      ),
      findsOneWidget,
    );
    expect(
      find.text('Enviámos um código por WhatsApp para +258*****4567.'),
      findsOneWidget,
    );
  });

  testWidgets('mudança de canal vista em getOtpChallenge mostra o aviso', (
    tester,
  ) async {
    api.on('GET', '/api/v1/auth/otp/challenges/c1', 200, Fixtures.challenge());
    await openCodeStep(tester);
    expect(find.byKey(const Key('code-fallback')), findsNothing);

    await tester.pump(const Duration(seconds: 5));
    await tester.pumpAndSettle();
    expect(api.calls('GET', '/api/v1/auth/otp/challenges/c1'), hasLength(1));
    expect(find.byKey(const Key('code-fallback')), findsNothing);

    api.on(
      'GET',
      '/api/v1/auth/otp/challenges/c1',
      200,
      Fixtures.challenge(channel: 'WHATSAPP', fallbackUsed: true),
    );
    await tester.pump(const Duration(seconds: 5));
    await tester.pumpAndSettle();
    expect(
      find.text(
        'Não conseguimos enviar por SMS. Enviámos o código por WhatsApp.',
      ),
      findsOneWidget,
    );
    expect(
      find.text('Enviámos um código por WhatsApp para +258*****4567.'),
      findsOneWidget,
    );

    // Pára ao fim de 30 s.
    await tester.pump(const Duration(seconds: 60));
    await tester.pumpAndSettle();
    expect(api.calls('GET', '/api/v1/auth/otp/challenges/c1'), hasLength(6));
  });

  testWidgets('"Alterar" volta ao primeiro passo com o número', (tester) async {
    await openCodeStep(tester);
    await tester.tap(find.text('Alterar'));
    await tester.pumpAndSettle();

    expect(find.byKey(const Key('msisdn-field')), findsOneWidget);
    expect(find.text('841234567'), findsOneWidget);
  });

  testWidgets('conta nova entra e vai completar o perfil', (tester) async {
    final storage = MemoryTokenStorage();
    api
      ..on('POST', _otpVerify, 200, Fixtures.tokens(isNewUser: true))
      ..on(
        'GET',
        '/api/v1/me',
        200,
        Fixtures.me(displayName: null, profileComplete: false),
      )
      ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile())
      ..on('GET', '/api/v1/me/contacts', 200, Fixtures.contacts())
      ..on('GET', '/api/v1/meta/version', 200, {'version': '1.0.0'});
    await openCodeStep(tester, storage: storage);

    await typeCode(tester, '123456');

    expect(storage.refreshToken, 'refresh-1');
    expect(
      find.text('Precisamos destes dados para emitir as suas apólices.'),
      findsOneWidget,
    );
    expect(
      api.calls('GET', '/api/v1/me').single.headers['Authorization'],
      'Bearer access-1',
    );
  });

  testWidgets('conta com perfil completo entra e vai para o início', (
    tester,
  ) async {
    api
      ..on('POST', _otpVerify, 200, Fixtures.tokens(isNewUser: false))
      ..on('GET', '/api/v1/me', 200, Fixtures.me());
    await openCodeStep(tester);

    await typeCode(tester, '123456');

    expect(find.text('Olá, Ana'), findsOneWidget);
    expect(find.text('Simular seguro'), findsOneWidget);
  });
}
