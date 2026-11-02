import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'support/mock_api.dart';

const _otpRequest = '/api/v1/auth/otp/request';

void main() {
  late MockApi api;

  setUp(() => api = MockApi());

  Finder requestButton() => find.byKey(const Key('request-code'));

  bool enabled(WidgetTester tester, Finder finder) =>
      tester.widget<ButtonStyleButton>(finder).onPressed != null;

  Future<void> requestCode(WidgetTester tester, String digits) async {
    await tester.enterText(find.byKey(const Key('msisdn-field')), digits);
    await tester.tap(requestButton());
    await tester.pumpAndSettle();
  }

  group('escolha de canal', () {
    testWidgets(
      'aparece só quando o cliente pode escolher e há mais de um canal',
      (tester) async {
        api
          ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
          ..on(
            'POST',
            _otpRequest,
            202,
            Fixtures.otpRequest(channel: 'WHATSAPP'),
          );
        await pumpMoSeguros(tester, api);

        expect(find.byKey(const Key('identifier-type')), findsOneWidget);
        expect(find.byKey(const Key('otp-channel')), findsOneWidget);
        expect(find.text('Receber o código por'), findsOneWidget);
        expect(find.text('WhatsApp'), findsOneWidget);

        // E-mail: um só canal, sem escolha.
        await tester.tap(find.text('E-mail'));
        await tester.pumpAndSettle();
        expect(find.byKey(const Key('email-field')), findsOneWidget);
        expect(find.byKey(const Key('otp-channel')), findsNothing);

        await tester.tap(find.text('Telemóvel'));
        await tester.pumpAndSettle();
        await tester.tap(find.text('WhatsApp'));
        await tester.pumpAndSettle();
        await requestCode(tester, '841234567');

        expect(api.calls('POST', _otpRequest).single.data, {
          'identifierType': 'MSISDN',
          'identifier': '+258841234567',
          'channel': 'WHATSAPP',
        });
        expect(
          find.text('Enviámos um código por WhatsApp para +258*****4567.'),
          findsOneWidget,
        );
      },
    );

    testWidgets('sem escolha permitida não mostra canais nem envia channel', (
      tester,
    ) async {
      api
        ..on(
          'GET',
          '/api/v1/auth/otp/options',
          200,
          Fixtures.otpOptions(
            customerCanChoose: false,
            identifierTypes: ['MSISDN'],
          ),
        )
        ..on('POST', _otpRequest, 202, Fixtures.otpRequest());
      await pumpMoSeguros(tester, api);

      expect(find.byKey(const Key('otp-channel')), findsNothing);
      // Um só tipo de identificador: sem selector.
      expect(find.byKey(const Key('identifier-type')), findsNothing);

      await requestCode(tester, '841234567');
      expect(api.calls('POST', _otpRequest).single.data, {
        'identifierType': 'MSISDN',
        'identifier': '+258841234567',
      });
    });
  });

  testWidgets('valida o telemóvel antes de pedir o código', (tester) async {
    api
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on('POST', _otpRequest, 202, Fixtures.otpRequest());
    await pumpMoSeguros(tester, api);

    expect(find.text('+258 '), findsOneWidget);
    final field = tester.widget<TextField>(
      find.byKey(const Key('msisdn-field')),
    );
    expect(field.keyboardType, TextInputType.phone);

    for (final invalid in ['812345678', '88123456', '8412345']) {
      await requestCode(tester, invalid);
      expect(
        find.text('Número de telemóvel inválido (ex.: +258 84 123 4567).'),
        findsOneWidget,
        reason: invalid,
      );
    }
    // Só aceita dígitos, no máximo 9.
    await tester.enterText(
      find.byKey(const Key('msisdn-field')),
      '84a12345678',
    );
    expect(
      tester
          .widget<TextField>(find.byKey(const Key('msisdn-field')))
          .controller!
          .text,
      '841234567',
    );
    expect(api.calls('POST', _otpRequest), isEmpty);

    await requestCode(tester, '871234567');
    expect(api.calls('POST', _otpRequest), hasLength(1));
  });

  testWidgets('429 mostra a mensagem com contagem e bloqueia o botão', (
    tester,
  ) async {
    api
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on(
        'POST',
        _otpRequest,
        429,
        MockApi.problem(
          429,
          'OTP_RATE_LIMITED',
          extra: {'retryAfterSeconds': 30},
        ),
      );
    await pumpMoSeguros(tester, api);
    await requestCode(tester, '841234567');

    expect(
      find.text(
        'Pediu demasiados códigos. Tente mais tarde. Tente de novo dentro de 30 s.',
      ),
      findsOneWidget,
    );
    expect(enabled(tester, requestButton()), isFalse);

    await tester.pump(const Duration(seconds: 10));
    expect(
      find.text(
        'Pediu demasiados códigos. Tente mais tarde. Tente de novo dentro de 20 s.',
      ),
      findsOneWidget,
    );

    await tester.pump(const Duration(seconds: 20));
    expect(
      find.text('Pediu demasiados códigos. Tente mais tarde.'),
      findsOneWidget,
    );
    expect(enabled(tester, requestButton()), isTrue);
  });

  testWidgets('OTP_CHANNEL_NOT_AVAILABLE muda para o e-mail', (tester) async {
    api
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on(
        'POST',
        _otpRequest,
        422,
        MockApi.problem(422, 'OTP_CHANNEL_NOT_AVAILABLE'),
      );
    await pumpMoSeguros(tester, api);
    await requestCode(tester, '841234567');

    expect(
      find.text(
        'Este canal não está disponível. Use outro canal ou entre com o outro identificador.',
      ),
      findsOneWidget,
    );
    expect(find.byKey(const Key('email-field')), findsOneWidget);
  });

  testWidgets('erro de campo da API aparece no campo', (tester) async {
    api
      ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
      ..on(
        'POST',
        _otpRequest,
        400,
        MockApi.problem(
          400,
          'VALIDATION_ERROR',
          errors: [
            {'field': 'identifier', 'code': 'INVALID_MSISDN'},
          ],
        ),
      );
    await pumpMoSeguros(tester, api);
    await requestCode(tester, '841234567');

    expect(
      find.text('Número de telemóvel inválido (ex.: +258 84 123 4567).'),
      findsOneWidget,
    );
  });
}
