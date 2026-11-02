import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'support/mock_api.dart';

const _contacts = '/api/v1/me/contacts';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi()
      ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
      ..on('GET', '/api/v1/me', 200, Fixtures.me())
      ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile(complete: true))
      ..on('GET', _contacts, 200, Fixtures.contacts())
      ..on('GET', '/api/v1/meta/version', 200, {'version': '1.0.0'})
      ..on(
        'GET',
        '/api/v1/auth/otp/options',
        200,
        Fixtures.otpOptions(customerCanChoose: false),
      );
  });

  Future<void> openContacts(WidgetTester tester) async {
    await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');
    await tester.tap(find.byKey(const Key('open-profile')));
    await tester.pumpAndSettle();
    // Até ao fim da secção (o botão de e-mail ou o e-mail confirmado).
    await tester.scrollUntilVisible(
      find.byKey(const Key('logout')),
      300,
      scrollable: find.byType(Scrollable).first,
    );
  }

  testWidgets('lista os contactos com "Verificado" e o botão do que falta', (
    tester,
  ) async {
    await openContacts(tester);

    expect(find.text('+258841234567'), findsOneWidget);
    expect(find.text('Verificado'), findsOneWidget);
    expect(find.byKey(const Key('add-contact-EMAIL')), findsOneWidget);
    expect(find.text('Acrescentar e-mail'), findsOneWidget);
    expect(find.byKey(const Key('add-contact-MSISDN')), findsNothing);
  });

  testWidgets('LAST_CONTACT mostra a mensagem', (tester) async {
    api.on(
      'DELETE',
      '$_contacts/MSISDN',
      409,
      MockApi.problem(409, 'LAST_CONTACT'),
    );
    await openContacts(tester);

    await tester.tap(find.byKey(const Key('remove-contact-MSISDN')));
    await tester.pumpAndSettle();
    expect(find.text('Remover contacto?'), findsOneWidget);
    expect(
      find.text('Deixará de poder entrar e receber avisos em +258841234567.'),
      findsOneWidget,
    );

    await tester.tap(find.byKey(const Key('confirm-remove-contact')));
    await tester.pumpAndSettle();

    expect(api.calls('DELETE', '$_contacts/MSISDN'), hasLength(1));
    expect(
      find.text('A conta tem de ficar com pelo menos um contacto confirmado.'),
      findsOneWidget,
    );
    expect(find.text('+258841234567'), findsOneWidget);
  });

  testWidgets(
    'o BottomSheet pede e reenvia com startContactVerification e confirma com verifyContact',
    (tester) async {
      api.on(
        'POST',
        _contacts,
        202,
        Fixtures.otpRequest(channel: 'EMAIL', resendAfterSeconds: 0),
      );
      await openContacts(tester);

      await tester.tap(find.byKey(const Key('add-contact-EMAIL')));
      await tester.pumpAndSettle();
      expect(find.byType(BottomSheet), findsOneWidget);
      // Só o tipo do contacto, sem selector.
      expect(find.byKey(const Key('identifier-type')), findsNothing);

      await tester.enterText(
        find.byKey(const Key('email-field')),
        'ana@exemplo.co.mz',
      );
      await tester.tap(find.byKey(const Key('request-code')));
      await tester.pumpAndSettle();

      expect(
        find.text('Enviámos um código por E-mail para a***@exemplo.co.mz.'),
        findsOneWidget,
      );

      await tester.tap(find.byKey(const Key('resend-code')));
      await tester.pumpAndSettle();

      final starts = api.calls('POST', _contacts);
      expect(starts, hasLength(2));
      for (final request in starts) {
        expect(request.data, {'type': 'EMAIL', 'value': 'ana@exemplo.co.mz'});
      }
      expect(api.calls('POST', '/api/v1/auth/otp/request'), isEmpty);

      api.on(
        'POST',
        '$_contacts/verify',
        200,
        Fixtures.contacts([
          {'type': 'MSISDN', 'value': '+258841234567', 'verified': true},
          {'type': 'EMAIL', 'value': 'ana@exemplo.co.mz', 'verified': true},
        ]),
      );
      await tester.enterText(find.byKey(const Key('otp-code-input')), '123456');
      await tester.pumpAndSettle();

      expect(api.calls('POST', '$_contacts/verify').single.data, {
        'challengeId': 'c1',
        'code': '123456',
      });
      expect(find.byType(BottomSheet), findsNothing);
      expect(find.text('ana@exemplo.co.mz'), findsOneWidget);
      expect(find.text('Contacto confirmado'), findsOneWidget);
    },
  );
}
