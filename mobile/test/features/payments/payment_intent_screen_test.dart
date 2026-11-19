import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/payments/await_confirmation.dart';
import 'package:moseguros/features/payments/checking_status.dart';
import 'package:moseguros/features/payments/format_reference.dart';
import 'package:moseguros/features/payments/payment_controller.dart';
import 'package:moseguros/features/payments/payment_screen.dart';
import 'package:moseguros/features/payments/reference_details.dart';

import '../../support/mock_api.dart';
import '../../support/payments.dart';
import '../../support/proposals.dart';

/// Sai do acompanhamento (pára a leitura periódica).
Future<void> _leave(WidgetTester tester, GoRouter router) async {
  router.go('/');
  await settle(tester);
}

void main() {
  test('formatReference agrupa os 11 dígitos e copia sem espaços', () {
    expect(formatReference('00548781108'), '005 487 811 08');
    expect(referenceCopyValue('005 487 811 08'), '00548781108');
    expect(amountCopyValue(299900), '2999,00');
    expect(formatCountdown(const Duration(seconds: 125)), '02:05');
    expect(formatCountdown(const Duration(seconds: -3)), '00:00');
  });

  testWidgets(
    'M-Pesa: passos e contagem; lê de 3 em 3 s até ficar confirmado',
    (tester) async {
      var reads = 0;
      final api = MockApi()
        ..on('GET', paymentIntentPath, 200, (_) {
          reads++;
          return reads < 3
              ? PaymentFixtures.intent()
              : PaymentFixtures.intent(
                  status: 'SUCCEEDED',
                  nextAction: {'type': 'NONE'},
                );
        });
      final (_, router) = await openAt(
        tester,
        api,
        '/payments/$paymentIntentId',
      );

      expect(find.byType(AwaitConfirmation), findsOneWidget);
      expect(
        find.text('1. Desbloqueie o telemóvel com o número +258*****4567.'),
        findsOneWidget,
      );
      expect(
        tester.widget<Text>(find.byKey(const Key('payment-countdown'))).data,
        '01:30',
      );
      expect(find.text('Pendente'), findsOneWidget);
      expect(find.byType(CtaButton), findsNothing);
      expect(reads, 1);

      await tester.pump(const Duration(seconds: 3));
      await settle(tester);
      expect(reads, 2);
      await tester.pump(const Duration(seconds: 3));
      await settle(tester);
      expect(reads, 3);
      expect(find.text('Pagamento confirmado'), findsOneWidget);
      expect(
        find.text('A sua apólice vai ser emitida em breve.'),
        findsOneWidget,
      );
      // Num estado final deixa de ler.
      await tester.pump(const Duration(seconds: 30));
      await settle(tester);
      expect(reads, 3);

      await tester.tap(find.byKey(const Key('payment-view-proposal')));
      await settle(tester);
      expect(
        router.routerDelegate.currentConfiguration.uri.path,
        '/proposals/$proposalId',
      );
    },
  );

  testWidgets(
    'contagem a zero com o pagamento pendente: "Estamos a confirmar"',
    (tester) async {
      final api = MockApi()
        ..on(
          'GET',
          paymentIntentPath,
          200,
          PaymentFixtures.intent(
            nextAction: {
              'type': 'AWAIT_CUSTOMER_CONFIRMATION',
              // `proposalToday` − 1 s.
              'timeoutAt': '2027-01-10T08:59:59Z',
            },
          ),
        );
      final (_, router) = await openAt(
        tester,
        api,
        '/payments/$paymentIntentId',
      );
      expect(
        tester.widget<Text>(find.byKey(const Key('payment-countdown'))).data,
        '00:00',
      );
      expect(
        find.text('Ainda não recebemos a resposta. Estamos a confirmar.'),
        findsOneWidget,
      );
      await _leave(tester, router);
    },
  );

  testWidgets('UNKNOWN: "Não volte a pagar", próxima verificação, sem botões', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        paymentIntentPath,
        200,
        PaymentFixtures.intent(
          status: 'UNKNOWN',
          nextAction: {
            'type': 'CHECKING_STATUS',
            'nextCheckAt': '2027-01-10T09:05:00Z',
          },
        ),
      );
    final (_, router) = await openAt(tester, api, '/payments/$paymentIntentId');
    expect(find.byType(CheckingStatus), findsOneWidget);
    expect(
      find.textContaining('Não volte a pagar', findRichText: true),
      findsOneWidget,
    );
    expect(find.text('Próxima verificação às 11:05'), findsOneWidget);
    expect(
      find.descendant(
        of: find.byType(ListView),
        matching: find.byWidgetPredicate((w) => w is ButtonStyleButton),
      ),
      findsNothing,
    );
    await _leave(tester, router);
  });

  testWidgets(
    'entidade e referência: três dados, copiar sem espaços, SMS e PDF; lê logo '
    'ao voltar ao primeiro plano',
    (tester) async {
      String? copied;
      tester.binding.defaultBinaryMessenger.setMockMethodCallHandler(
        SystemChannels.platform,
        (call) async {
          if (call.method == 'Clipboard.setData') {
            copied = (call.arguments as Map)['text'] as String;
          }
          return null;
        },
      );
      addTearDown(
        () => tester.binding.defaultBinaryMessenger.setMockMethodCallHandler(
          SystemChannels.platform,
          null,
        ),
      );
      final shared = <String>[];
      final api = MockApi()
        ..on(
          'GET',
          paymentIntentPath,
          200,
          PaymentFixtures.intent(method: 'BANK_REFERENCE'),
        )
        ..on('POST', '$paymentIntentPath/reference-sms', 202, {
          'sentTo': '+258*****2233',
        })
        ..on('POST', '$paymentIntentPath/reference-document', 200, {
          'fileId': 'file-ref',
        })
        ..onBytes(
          '/api/v1/files/file-ref',
          Uint8List.fromList([37, 80, 68, 70]),
          'application/pdf',
        );
      final (_, router) = await openAt(
        tester,
        api,
        '/payments/$paymentIntentId',
        overrides: [
          pdfSharerProvider.overrideWithValue((bytes, name) async {
            shared.add('$name:${bytes.length}');
          }),
        ],
      );

      expect(find.byType(ReferenceDetails), findsOneWidget);
      expect(
        tester.widget<Text>(find.byKey(const Key('reference-entity'))).data,
        '12345',
      );
      expect(
        tester.widget<Text>(find.byKey(const Key('reference-reference'))).data,
        '005 487 811 08',
      );
      expect(
        tester.widget<Text>(find.byKey(const Key('reference-amount'))).data,
        matches(RegExp(r'^2.?999,00 MT$')),
      );
      expect(
        find.text(
          'Pague exactamente este montante. A rede recusa outro valor.',
        ),
        findsOneWidget,
      );

      await tester.tap(find.byKey(const Key('copy-reference')));
      await tester.pumpAndSettle();
      expect(copied, '00548781108');
      expect(find.text('Copiado'), findsOneWidget);
      await tester.tap(find.byKey(const Key('copy-amount')));
      await tester.pumpAndSettle();
      expect(copied, '2999,00');

      await tester.pump(const Duration(seconds: 5));
      await settle(tester);
      await tapInList(tester, find.byKey(const Key('reference-pdf')));
      await settle(tester);
      expect(shared, ['referencia-00548781108.pdf:4']);

      // Espera que a mensagem "Copiado" desapareça (tapa o fundo do ecrã).
      await tester.pump(const Duration(seconds: 5));
      await settle(tester);
      await tapInList(tester, find.byKey(const Key('reference-sms')));
      await settle(tester);
      final field = find.descendant(
        of: find.byKey(const Key('reference-sms-msisdn')),
        matching: find.byType(EditableText),
      );
      expect(tester.widget<EditableText>(field).controller.text, '841234567');
      await tester.enterText(field, '821112233');
      await tester.tap(find.byKey(const Key('reference-sms-send')));
      await settle(tester);
      expect(
        api.calls('POST', '$paymentIntentPath/reference-sms').single.data,
        {'msisdn': '+258821112233'},
      );
      expect(find.text('SMS enviado para +258*****2233'), findsOneWidget);
      expect(find.byType(CtaButton), findsNothing);

      // Em segundo plano não lê; ao voltar lê logo (sem esperar os 30 s).
      final before = api.calls('GET', paymentIntentPath).length;
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.inactive);
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.hidden);
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.paused);
      await tester.pump(const Duration(seconds: 31));
      expect(api.calls('GET', paymentIntentPath).length, before);
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.hidden);
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.inactive);
      tester.binding.handleAppLifecycleStateChanged(AppLifecycleState.resumed);
      await settle(tester);
      expect(api.calls('GET', paymentIntentPath).length, before + 1);
      await _leave(tester, router);
    },
  );

  testWidgets(
    'FAILED (INSUFFICIENT_BALANCE): "Escolher outra forma de pagamento"',
    (tester) async {
      final api = MockApi()
        ..on(
          'GET',
          paymentIntentPath,
          200,
          PaymentFixtures.intent(
            status: 'FAILED',
            failureCode: 'INSUFFICIENT_BALANCE',
            nextAction: {'type': 'NONE'},
          ),
        )
        ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods());
      await openAt(tester, api, '/payments/$paymentIntentId');
      expect(find.text('Saldo insuficiente.'), findsOneWidget);
      expect(find.text('Falhou'), findsOneWidget);
      await tester.tap(find.byKey(const Key('payment-choose-another')));
      await settle(tester);
      expect(find.byType(PaymentScreen), findsOneWidget);
      expect(find.byType(CtaButton), findsOneWidget);
    },
  );

  testWidgets('pagamento de outro cliente: "Pagamento não encontrado."', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        paymentIntentPath,
        404,
        MockApi.problem(404, 'PAYMENT_INTENT_NOT_FOUND'),
      );
    await openAt(tester, api, '/payments/$paymentIntentId');
    expect(find.text('Pagamento não encontrado.'), findsOneWidget);
    expect(find.text('Ver os meus pagamentos'), findsOneWidget);
  });
}
