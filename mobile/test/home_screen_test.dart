import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/home/home_screen.dart';

import 'support/mock_api.dart';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi()
      ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
      ..on('GET', '/api/v1/me/profile', 200, Fixtures.profile())
      ..on('GET', '/api/v1/me/contacts', 200, Fixtures.contacts())
      ..on('GET', '/api/v1/meta/version', 200, {
        'name': 'moseguros-backend',
        'version': '1.2.3',
        'commit': 'abc1234',
      });
  });

  test('firstName', () {
    expect(firstName('Ana Maria Machava'), 'Ana');
    expect(firstName('  Zélia  '), 'Zélia');
    expect(firstName(''), isNull);
    expect(firstName(null), isNull);
  });

  testWidgets('reabrir a app com sessão vai directo ao início', (tester) async {
    api.on('GET', '/api/v1/me', 200, Fixtures.me());
    await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');

    expect(api.calls('POST', '/api/v1/auth/refresh').single.data, {
      'refreshToken': 'refresh-0',
    });
    expect(find.text('Olá, Ana'), findsOneWidget);
    expect(find.byKey(const Key('incomplete-profile')), findsNothing);
    // Um só botão laranja: "Simular seguro".
    expect(find.byType(CtaButton), findsOneWidget);
    expect(find.text('Simular seguro'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('"Simular seguro" mostra "Disponível em breve"', (tester) async {
    api.on('GET', '/api/v1/me', 200, Fixtures.me());
    await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');

    await tester.tap(find.text('Simular seguro'));
    await tester.pump();
    expect(find.text('Disponível em breve'), findsOneWidget);
  });

  testWidgets('perfil incompleto: cartão "Completar perfil" (em inglês)', (
    tester,
  ) async {
    api.on(
      'GET',
      '/api/v1/me',
      200,
      Fixtures.me(displayName: null, profileComplete: false),
    );
    final container = await pumpMoSeguros(
      tester,
      api,
      refreshToken: 'refresh-0',
      locale: const Locale('en'),
    );
    // À entrada vai completar o perfil; volta ao início pela navegação.
    expect(
      find.text('We need these details to issue your policies.'),
      findsOneWidget,
    );
    container.read(routerProvider).go('/');
    await tester.pumpAndSettle();

    expect(find.text('Hello'), findsOneWidget);
    expect(
      find.text('Complete your profile to be able to buy insurance.'),
      findsOneWidget,
    );
    await tester.tap(find.text('Complete profile'));
    await tester.pumpAndSettle();
    expect(
      find.text('We need these details to issue your policies.'),
      findsOneWidget,
    );
  });

  testWidgets('o destino "Perfil" da barra inferior abre o perfil, com "Sobre" no fundo', (
    tester,
  ) async {
    api.on('GET', '/api/v1/me', 200, Fixtures.me());
    await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');

    await tester.tap(find.byKey(const Key('nav-profile')));
    await tester.pumpAndSettle();

    expect(find.text('Perfil'), findsWidgets);
    expect(find.text('Dados pessoais'), findsOneWidget);
    await tester.scrollUntilVisible(
      find.byKey(const Key('api-commit')),
      300,
      scrollable: find.byType(Scrollable).first,
    );
    expect(find.text('Sobre'), findsOneWidget);
    expect(
      tester.widget<Text>(find.byKey(const Key('api-version'))).data,
      '1.2.3',
    );
    expect(
      tester.widget<Text>(find.byKey(const Key('api-commit'))).data,
      'abc1234',
    );
  });

  testWidgets('"Sobre": erro com "Tentar de novo"', (tester) async {
    api
      ..on('GET', '/api/v1/me', 200, Fixtures.me())
      ..on(
        'GET',
        '/api/v1/meta/version',
        500,
        MockApi.problem(500, 'INTERNAL_ERROR'),
      );
    await pumpMoSeguros(tester, api, refreshToken: 'refresh-0');
    await tester.tap(find.byKey(const Key('nav-profile')));
    await tester.pumpAndSettle();
    await tester.scrollUntilVisible(
      find.text('Tentar de novo'),
      300,
      scrollable: find.byType(Scrollable).first,
    );

    expect(
      find.textContaining('Não foi possível obter o estado da API.'),
      findsOneWidget,
    );
    expect(
      find.textContaining('Erro interno do servidor. Tente mais tarde.'),
      findsOneWidget,
    );

    api.on('GET', '/api/v1/meta/version', 200, {'version': '1.2.3'});
    await tester.ensureVisible(find.text('Tentar de novo'));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Tentar de novo'));
    await tester.pumpAndSettle();

    expect(find.byKey(const Key('system-status-error')), findsNothing);
    expect(
      tester.widget<Text>(find.byKey(const Key('api-commit'))).data,
      'desconhecido',
    );
  });
}
