import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/policies/policy_detail_screen.dart';
import 'package:moseguros/features/policies/policy_labels.dart';
import 'package:moseguros/features/policies/show_to_police_screen.dart';

import '../../support/mock_api.dart';
import '../../support/policies.dart';
import '../../support/proposals.dart';

/// "Agora" dos testes: 10/01/2027 09:00 UTC (`proposalToday`).
final _yesterday = proposalToday.subtract(const Duration(hours: 2));
final _twoDaysAgo = proposalToday.subtract(const Duration(days: 2));

AppDatabase _database() {
  final db = AppDatabase.memory();
  addTearDown(db.close);
  return db;
}

void main() {
  group('lista', () {
    testWidgets('cartões e separadores "Em vigor" / "Todas"', (tester) async {
      final api = MockApi()
        ..on(
          'GET',
          myPoliciesPath,
          200,
          PolicyFixtures.page([
            PolicyFixtures.summary(),
            PolicyFixtures.summary(
              id: policyId2,
              plate: 'BBB-200-MC',
              status: 'EXPIRED',
              coverStatus: 'ENDED',
              number: 'AP-2026-000001',
              startAt: '2026-01-15T00:00:00Z',
              endAt: '2027-01-14T21:59:59Z',
            ),
          ]),
        )
        ..on('GET', policyPath, 200, PolicyFixtures.detail());
      await openAt(tester, api, '/policies');

      expect(find.text('As minhas apólices'), findsOneWidget);
      expect(find.text('AAA-100-MC'), findsOneWidget);
      expect(find.text('Toyota Corolla'), findsOneWidget);
      expect(find.text('Seguradora Demo · RC Auto'), findsOneWidget);
      expect(find.text('Em vigor'), findsWidgets);
      expect(find.text('Válida até 14/01/2028 23:59'), findsOneWidget);
      expect(find.text('Apólice n.º $policyNumber'), findsOneWidget);
      expect(find.text('BBB-200-MC'), findsNothing);

      await tester.tap(find.byKey(const Key('policies-tab-all')));
      await settle(tester);
      expect(find.text('BBB-200-MC'), findsOneWidget);
      expect(find.text('Terminou'), findsOneWidget);

      await tester.tap(find.text('AAA-100-MC'));
      await settle(tester);
      expect(find.byType(PolicyDetailScreen), findsOneWidget);
    });

    testWidgets('vazio: "Simular seguro" é o único botão laranja', (
      tester,
    ) async {
      final api = MockApi()
        ..on('GET', myPoliciesPath, 200, PolicyFixtures.page([]));
      await openAt(tester, api, '/policies');

      expect(find.text('Ainda não tem apólices.'), findsOneWidget);
      expect(find.byType(CtaButton), findsOneWidget);
      expect(
        find.descendant(
          of: find.byType(CtaButton),
          matching: find.text('Simular seguro'),
        ),
        findsOneWidget,
      );
    });

    testWidgets('sem rede: dados da cache com a faixa "Sem ligação"', (
      tester,
    ) async {
      final db = _database();
      await tester.runAsync(
        () => seedPolicy(
          db,
          syncedAt: DateTime.utc(2027, 1, 9, 8),
          summary: PolicyFixtures.summary(),
          detail: PolicyFixtures.detail(),
        ),
      );
      final api = MockApi();
      await openAt(
        tester,
        api,
        '/policies',
        database: db,
        connectivity: FakeConnectivity(online: false),
      );

      expect(find.text('AAA-100-MC'), findsOneWidget);
      expect(
        find.text('Sem ligação. Dados de 09/01/2027 10:00.'),
        findsOneWidget,
      );
      expect(api.calls('GET', myPoliciesPath), isEmpty);
    });

    testWidgets('erro sem cache: mensagem e "Tentar de novo"', (tester) async {
      final api = MockApi();
      await openAt(
        tester,
        api,
        '/policies',
        connectivity: FakeConnectivity(online: false),
      );
      expect(
        find.text(
          'Sem ligação ao servidor. Verifique a sua ligação à Internet.',
        ),
        findsOneWidget,
      );
      expect(find.byKey(const Key('policies-retry')), findsOneWidget);
    });
  });

  group('detalhe', () {
    testWidgets('QR com o verificationUrl e "Mostrar à polícia"', (
      tester,
    ) async {
      final api = MockApi()
        ..on('GET', policyPath, 200, PolicyFixtures.detail())
        ..on(
          'GET',
          myPoliciesPath,
          200,
          PolicyFixtures.page([PolicyFixtures.summary()]),
        );
      await openAt(tester, api, '/policies/$policyId');

      final qr = tester.widget<CertificateQr>(
        find.byKey(const Key('certificate-qr')),
      );
      expect(qr.data, verificationUrl);
      expect(qr.size, 160);
      expect(find.text('Certificado n.º $certificateNumber'), findsOneWidget);
      expect(
        find.text('Válido de 15/01/2027 02:00 a 14/01/2028 23:59'),
        findsOneWidget,
      );
      expect(find.byKey(const Key('view-certificate')), findsOneWidget);
      // Nenhum botão laranja no detalhe.
      expect(find.byType(CtaButton), findsNothing);

      await tapInList(tester, find.byKey(const Key('show-to-police')));
      await settle(tester);
      expect(find.byType(ShowToPoliceScreen), findsOneWidget);
    });

    testWidgets('CERTIFICATE PENDING → "A preparar o certificado…"', (
      tester,
    ) async {
      final api = MockApi()
        ..on(
          'GET',
          policyPath,
          200,
          PolicyFixtures.detail(certificateStatus: 'PENDING'),
        )
        ..on(
          'GET',
          myPoliciesPath,
          200,
          PolicyFixtures.page([PolicyFixtures.summary()]),
        );
      final (_, router) = await openAt(tester, api, '/policies/$policyId');

      expect(find.text('A preparar o certificado…'), findsOneWidget);
      expect(find.byKey(const Key('view-certificate')), findsNothing);
      final reads = api.calls('GET', policyPath).length;

      // Pede o detalhe de novo a cada 5 s.
      await tester.pump(const Duration(seconds: 5));
      await settle(tester);
      expect(api.calls('GET', policyPath).length, greaterThan(reads));

      // Ao sair do ecrã a espera pára.
      router.go('/');
      await settle(tester);
    });

    testWidgets('POLICY_NOT_FOUND: mensagem e sai da cache', (tester) async {
      final api = MockApi()
        ..on('GET', policyPath, 404, MockApi.problem(404, 'POLICY_NOT_FOUND'))
        ..on('GET', myPoliciesPath, 200, PolicyFixtures.page([]));
      await openAt(tester, api, '/policies/$policyId');
      expect(find.text('Apólice não encontrada.'), findsOneWidget);
    });
  });

  group('Mostrar à polícia', () {
    testWidgets('sem rede, a partir da cache; brilho máximo e repõe ao sair', (
      tester,
    ) async {
      final db = _database();
      await tester.runAsync(
        () => seedPolicy(
          db,
          syncedAt: _yesterday,
          summary: PolicyFixtures.summary(),
          detail: PolicyFixtures.detail(),
        ),
      );
      final keeper = RecordingScreenKeeper();
      final api = MockApi();
      final (_, router) = await openAt(
        tester,
        api,
        '/policies/$policyId',
        database: db,
        connectivity: FakeConnectivity(online: false),
        screenKeeper: keeper,
      );
      expect(find.byType(PolicyDetailScreen), findsOneWidget);
      expect(keeper.calls, isEmpty);

      await tapInList(tester, find.byKey(const Key('show-to-police')));
      await settle(tester);

      expect(find.byType(ShowToPoliceScreen), findsOneWidget);
      expect(keeper.calls, ['keepBright']);
      final qr = tester.widget<CertificateQr>(
        find.byKey(const Key('police-qr')),
      );
      expect(qr.data, verificationUrl);
      expect(find.byKey(const Key('police-plate')), findsOneWidget);
      expect(find.text('Seguradora Demo'), findsOneWidget);
      expect(find.text('Estado em 10/01/2027 09:00: Em vigor'), findsOneWidget);
      expect(find.byKey(const Key('police-stale')), findsNothing);
      // Nenhum pedido chegou ao servidor: tudo veio da cache.
      expect(api.calls('GET', policyPath), isEmpty);

      await tester.tap(find.byKey(const Key('police-close')));
      await settle(tester);
      expect(find.byType(ShowToPoliceScreen), findsNothing);
      expect(find.byType(PolicyDetailScreen), findsOneWidget);
      expect(keeper.calls, ['keepBright', 'restore']);
      expect(
        router.routerDelegate.currentConfiguration.uri.path,
        '/policies/$policyId',
      );
    });

    testWidgets('dados com mais de 24 h mostram o aviso', (tester) async {
      final db = _database();
      await tester.runAsync(
        () => seedPolicy(
          db,
          syncedAt: _twoDaysAgo,
          detail: PolicyFixtures.detail(),
        ),
      );
      await openAt(
        tester,
        MockApi(),
        '/policies/$policyId/police',
        database: db,
        connectivity: FakeConnectivity(online: false),
      );
      expect(find.byType(ShowToPoliceScreen), findsOneWidget);
      expect(find.byKey(const Key('police-stale')), findsOneWidget);
      expect(
        find.text(
          'Dados com mais de um dia. O agente confirma o estado actual ao ler '
          'o código.',
        ),
        findsOneWidget,
      );
    });
  });
}
