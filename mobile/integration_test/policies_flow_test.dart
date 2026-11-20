import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:http_mock_adapter/http_mock_adapter.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/core/clock.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/features/home/home_screen.dart';
import 'package:moseguros/features/policies/policies_controller.dart';
import 'package:moseguros/features/policies/policy_detail_screen.dart';
import 'package:moseguros/features/policies/policy_labels.dart';
import 'package:moseguros/features/policies/show_to_police_screen.dart';

import '../test/support/mock_api.dart';
import '../test/support/policies.dart';
import '../test/support/proposals.dart';
import '../test/support/vehicles.dart';

/// Espera até [finder] aparecer (no máximo 10 s num dispositivo).
Future<void> pumpUntilFound(WidgetTester tester, Finder finder) async {
  for (var i = 0; i < 100 && finder.evaluate().isEmpty; i++) {
    await tester.pump(const Duration(milliseconds: 100));
  }
  await settle(tester);
}

/// A API do login por OTP, da sessão e das apólices.
MockApi _api() {
  final api = MockApi()
    ..on('GET', '/api/v1/auth/otp/options', 200, Fixtures.otpOptions())
    ..on('POST', '/api/v1/auth/otp/request', 202, Fixtures.otpRequest())
    ..on('GET', '/api/v1/auth/otp/challenges/c1', 200, Fixtures.challenge())
    ..on(
      'POST',
      '/api/v1/auth/otp/verify',
      200,
      Fixtures.tokens(isNewUser: false),
    )
    ..on('GET', myProposalsPath, 200, ProposalFixtures.page([]))
    ..on(
      'GET',
      myPoliciesPath,
      200,
      PolicyFixtures.page([PolicyFixtures.summary()]),
    )
    ..on('GET', policyPath, 200, PolicyFixtures.detail());
  stubCustomerSession(api);
  return api;
}

/// Login no ecrã: telemóvel, "Receber código" e o código.
Future<void> _login(WidgetTester tester) async {
  await pumpUntilFound(tester, find.byKey(const Key('msisdn-field')));
  await tester.enterText(find.byKey(const Key('msisdn-field')), '841234567');
  await tester.tap(find.byKey(const Key('request-code')));
  await settle(tester);
  await pumpUntilFound(tester, find.byKey(const Key('otp-code-input')));
  await tester.enterText(find.byKey(const Key('otp-code-input')), '123456');
  await settle(tester);
}

/// Fluxo principal do S6-07 com a API simulada: login → início com o atalho →
/// apólice → "Mostrar à polícia" → modo avião → o QR continua; e a ligação
/// `moseguros://policy/{id}` sem sessão → login → detalhe.
///
/// Sem emulador: `flutter test integration_test/policies_flow_test.dart -d flutter-tester`.
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'login → atalho → apólice → "Mostrar à polícia" → modo avião: o QR fica',
    (tester) async {
      final api = _api();
      final network = FakeConnectivity();
      final db = AppDatabase.memory();
      addTearDown(db.close);
      final container = await pumpMoSeguros(
        tester,
        api,
        database: db,
        connectivity: network,
        overrides: [clockProvider.overrideWithValue(() => proposalToday)],
      );

      await _login(tester);

      // Início: a sincronização do login guardou a apólice; o atalho aparece.
      await pumpUntilFound(tester, find.byKey(const Key('home-police')));
      expect(find.byType(HomeScreen), findsOneWidget);
      expect(find.byKey(const Key('home-police')), findsOneWidget);

      // A apólice.
      await tapInList(tester, find.byKey(const Key('home-policies')));
      await settle(tester);
      await tester.tap(find.text('AAA-100-MC'));
      await settle(tester);
      expect(find.byType(PolicyDetailScreen), findsOneWidget);

      // "Mostrar à polícia".
      await tapInList(tester, find.byKey(const Key('show-to-police')));
      await settle(tester);
      expect(find.byType(ShowToPoliceScreen), findsOneWidget);

      // Modo avião: sem rede e o Dio a falhar.
      network.online = false;
      api.adapter.onRoute(
        policyPath,
        (server) => server.throws(
          0,
          DioException.connectionError(
            requestOptions: RequestOptions(path: policyPath),
            reason: 'airplane mode',
          ),
        ),
        request: const Request(method: RequestMethods.get),
      );
      await container.read(policyDetailProvider(policyId).notifier).refresh();
      await settle(tester);

      expect(find.byType(ShowToPoliceScreen), findsOneWidget);
      expect(
        tester.widget<CertificateQr>(find.byKey(const Key('police-qr'))).data,
        verificationUrl,
      );

      // Fechar e voltar a abrir, ainda sem rede.
      await tester.tap(find.byKey(const Key('police-close')));
      await settle(tester);
      container.read(routerProvider).go('/');
      await settle(tester);
      await tester.tap(find.byKey(const Key('home-police')));
      await settle(tester);
      expect(
        tester.widget<CertificateQr>(find.byKey(const Key('police-qr'))).data,
        verificationUrl,
      );
    },
  );

  testWidgets('moseguros://policy/{id} sem sessão → login → detalhe', (
    tester,
  ) async {
    final api = _api();
    final container = await pumpMoSeguros(
      tester,
      api,
      overrides: [clockProvider.overrideWithValue(() => proposalToday)],
    );
    container.read(routerProvider).go('moseguros://policy/$policyId');
    await settle(tester);

    expect(
      container
          .read(routerProvider)
          .routerDelegate
          .currentConfiguration
          .uri
          .toString(),
      '/login?from=%2Fpolicies%2F$policyId',
    );
    await _login(tester);

    await pumpUntilFound(tester, find.byType(PolicyDetailScreen));
    expect(find.byType(PolicyDetailScreen), findsOneWidget);
    expect(
      container
          .read(routerProvider)
          .routerDelegate
          .currentConfiguration
          .uri
          .path,
      '/policies/$policyId',
    );
  });
}
