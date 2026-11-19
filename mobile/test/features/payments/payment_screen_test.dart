import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_serializers.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:moseguros/features/payments/payment_controller.dart';
import 'package:moseguros/features/payments/payment_intent_screen.dart';
import 'package:moseguros/features/payments/payment_screen.dart';

import '../../support/mock_api.dart';
import '../../support/payments.dart';
import '../../support/proposals.dart';

CtaButton _cta(WidgetTester tester) =>
    tester.widget<CtaButton>(find.byType(CtaButton));

void main() {
  testWidgets(
    'resumo, métodos pela ordem recebida, campo certo por método e um só '
    'botão laranja',
    (tester) async {
      final api = MockApi()
        ..on(
          'GET',
          paymentMethodsPath,
          200,
          PaymentFixtures.methods(
            methods: [
              {'method': 'BANK_REFERENCE', 'sortOrder': 1},
              {'method': 'MPESA', 'sortOrder': 2, 'displayName': 'Vodacom'},
            ],
          ),
        );
      await openAt(tester, api, '/proposals/$proposalId/pay');

      expect(find.byType(PaymentScreen), findsOneWidget);
      expect(
        find.text('Proposta $proposalNumber · Seguradora Demo'),
        findsOneWidget,
      );
      expect(find.text('Pagar até 20/01/2027 12:00'), findsOneWidget);
      expect(
        find.text(
          'O pagamento é feito directamente à Seguradora Demo. '
          'A MoSeguros não recebe dinheiro.',
        ),
        findsOneWidget,
      );
      final reference = tester.getTopLeft(
        find.byKey(const Key('method-BANK_REFERENCE')),
      );
      final mpesa = tester.getTopLeft(find.byKey(const Key('method-MPESA')));
      expect(reference.dy, lessThan(mpesa.dy));
      expect(find.text('Vodacom'), findsOneWidget);

      // Nada escolhido ao abrir: o botão laranja está desactivado.
      expect(find.byType(CtaButton), findsOneWidget);
      expect(_cta(tester).label, 'Adquirir apólice');
      expect(_cta(tester).onPressed, isNull);

      await chooseMethod(tester, 'BANK_REFERENCE');
      await tester.pumpAndSettle();
      expect(
        find.textContaining('Recebe uma entidade e uma referência'),
        findsOneWidget,
      );
      expect(find.byKey(const Key('payer-msisdn')), findsNothing);
      expect(_cta(tester).onPressed, isNotNull);

      await chooseMethod(tester, 'MPESA');
      await tester.pumpAndSettle();
      final field = find.descendant(
        of: find.byKey(const Key('payer-msisdn')),
        matching: find.byType(EditableText),
      );
      // Preenchido com o telemóvel da conta (84…).
      expect(tester.widget<EditableText>(field).controller.text, '841234567');

      // 82 não é M-Pesa.
      await tester.enterText(field, '821234567');
      await tester.pumpAndSettle();
      expect(
        find.text('Use um número Vodacom (começado por 84 ou 85).'),
        findsOneWidget,
      );
      expect(_cta(tester).onPressed, isNull);
      await tester.enterText(field, '851234567');
      await tester.pumpAndSettle();
      expect(_cta(tester).onPressed, isNotNull);
    },
  );

  testWidgets('paga com M-Pesa e abre o acompanhamento', (tester) async {
    final api = MockApi()
      ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods())
      ..on('POST', proposalPayPath, 202, PaymentFixtures.intent())
      ..on('GET', paymentIntentPath, 200, PaymentFixtures.intent());
    final (_, router) = await openAt(tester, api, '/proposals/$proposalId/pay');

    await chooseMethod(tester, 'MPESA');
    await tester.pumpAndSettle();
    await tester.tap(find.byType(CtaButton));
    await settle(tester);

    final request = api.calls('POST', proposalPayPath).single;
    expect(request.data, {
      'channel': 'APP',
      'method': 'MPESA',
      'payerMsisdn': '+258841234567',
    });
    expect(request.headers['Idempotency-Key'], isA<String>());
    expect(find.byType(PaymentIntentScreen), findsOneWidget);
    expect(
      router.routerDelegate.currentConfiguration.last.matchedLocation,
      '/payments/$paymentIntentId',
    );
    expect(find.text('Confirme o pagamento no seu telemóvel'), findsOneWidget);
    expect(find.byType(CtaButton), findsNothing);
    // Sai do acompanhamento (pára a leitura periódica).
    router.go('/');
    await settle(tester);
  });

  testWidgets('PAYMENT_IN_PROGRESS vai para o pagamento intentId', (
    tester,
  ) async {
    final api = MockApi()
      ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods())
      ..on(
        'POST',
        proposalPayPath,
        409,
        MockApi.problem(
          409,
          'PAYMENT_IN_PROGRESS',
          extra: {'intentId': paymentIntentId},
        ),
      )
      ..on('GET', paymentIntentPath, 200, PaymentFixtures.intent());
    final (_, router) = await openAt(tester, api, '/proposals/$proposalId/pay');
    await chooseMethod(tester, 'BANK_REFERENCE');
    await tester.pumpAndSettle();
    await tester.tap(find.byType(CtaButton));
    await settle(tester);
    expect(
      router.routerDelegate.currentConfiguration.last.matchedLocation,
      '/payments/$paymentIntentId',
    );
    router.go('/');
    await settle(tester);
  });

  testWidgets('proposta paga: "já está paga", sem botão laranja', (
    tester,
  ) async {
    final api = MockApi()
      ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods(paid: true));
    await openAt(tester, api, '/proposals/$proposalId/pay');
    expect(find.text('Esta proposta já está paga.'), findsOneWidget);
    expect(find.byType(CtaButton), findsNothing);
  });

  testWidgets('409 PAYMENT_TARGET_NOT_PAYABLE: "já não pode ser paga"', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        paymentMethodsPath,
        409,
        MockApi.problem(409, 'PAYMENT_TARGET_NOT_PAYABLE'),
      );
    await openAt(tester, api, '/proposals/$proposalId/pay');
    expect(find.text('Esta proposta já não pode ser paga.'), findsOneWidget);
  });

  testWidgets('sem métodos: contactar a seguradora', (tester) async {
    final api = MockApi()
      ..on(
        'GET',
        paymentMethodsPath,
        200,
        PaymentFixtures.methods(methods: const []),
      );
    await openAt(tester, api, '/proposals/$proposalId/pay');
    expect(
      find.text(
        'A seguradora ainda não tem formas de pagamento disponíveis. '
        'Contacte a seguradora.',
      ),
      findsOneWidget,
    );
  });

  testWidgets('M-Pesa em curso: "Ver pagamento" e botão laranja desactivado', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        paymentMethodsPath,
        200,
        PaymentFixtures.methods(activeIntent: PaymentFixtures.intent()),
      );
    await openAt(tester, api, '/proposals/$proposalId/pay');
    expect(find.text('Tem um pagamento em curso'), findsOneWidget);
    expect(find.text('Ver pagamento'), findsOneWidget);
    await chooseMethod(tester, 'BANK_REFERENCE');
    await tester.pumpAndSettle();
    expect(_cta(tester).onPressed, isNull);
  });

  group('PaymentController', () {
    test(
      'repete a chave depois de um erro de rede e muda-a com o método',
      () async {
        var n = 0;
        final repository = _FakeRepository();
        final controller = PaymentController(
          repository,
          proposalId,
          () => 'key-${n++}',
        );

        repository.fail = true;
        await expectLater(
          controller.pay(method: 'MPESA', payerMsisdn: '+258841234567'),
          throwsA(isA<Exception>()),
        );
        await expectLater(
          controller.pay(method: 'MPESA', payerMsisdn: '+258841234567'),
          throwsA(isA<Exception>()),
        );
        expect(repository.keys, ['key-0', 'key-0']);

        await expectLater(
          controller.pay(method: 'BANK_REFERENCE'),
          throwsA(isA<Exception>()),
        );
        expect(repository.keys.last, 'key-1');

        repository.fail = false;
        await controller.pay(method: 'BANK_REFERENCE');
        expect(repository.keys.last, 'key-1');
        // Depois de uma resposta 2xx a chave é nova.
        await controller.pay(method: 'BANK_REFERENCE');
        expect(repository.keys.last, 'key-2');
      },
    );
  });
}

class _FakeRepository implements PaymentRepository {
  bool fail = false;
  final keys = <String>[];

  @override
  Future<IntentView> payProposal(
    String proposalId, {
    required String idempotencyKey,
    required String method,
    String? payerMsisdn,
  }) async {
    keys.add(idempotencyKey);
    if (fail) throw Exception('network');
    return apiSerializers.deserializeWith(
      IntentView.serializer,
      PaymentFixtures.intent(method: method),
    )!;
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}
