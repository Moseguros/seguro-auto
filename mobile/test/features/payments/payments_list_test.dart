import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/payments/payment_intent_screen.dart';
import 'package:moseguros/features/payments/payments_list_screen.dart';
import 'package:moseguros/features/profile/profile_screen.dart';

import '../../support/mock_api.dart';
import '../../support/payments.dart';
import '../../support/proposals.dart';

void main() {
  testWidgets('lista: data, proposta, seguradora, método, montante e estado', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        myPaymentsPath,
        200,
        PaymentFixtures.page([
          PaymentFixtures.intent(
            status: 'SUCCEEDED',
            nextAction: {'type': 'NONE'},
          ),
        ]),
      )
      ..on(
        'GET',
        paymentIntentPath,
        200,
        PaymentFixtures.intent(
          status: 'SUCCEEDED',
          nextAction: {'type': 'NONE'},
        ),
      );
    await openAt(tester, api, '/payments');

    expect(find.byType(PaymentsListScreen), findsOneWidget);
    final request = api.calls('GET', myPaymentsPath).first;
    expect(request.queryParameters['size'], 20);
    expect(request.queryParameters['sort'], 'createdAt,desc');
    expect(find.text(proposalNumber), findsOneWidget);
    expect(find.text('10/01/2027 11:00 · Seguradora Demo'), findsOneWidget);
    expect(find.text('M-Pesa'), findsOneWidget);
    expect(find.text('Pago'), findsOneWidget);
    expect(find.byType(CtaButton), findsNothing);

    await tester.tap(find.text(proposalNumber));
    await settle(tester);
    expect(find.byType(PaymentIntentScreen), findsOneWidget);
    expect(find.text('Pagamento confirmado'), findsOneWidget);
  });

  testWidgets('lista vazia', (tester) async {
    final api = MockApi()
      ..on('GET', myPaymentsPath, 200, PaymentFixtures.page(const []));
    await openAt(tester, api, '/payments');
    expect(find.text('Ainda não tem pagamentos.'), findsOneWidget);
  });

  testWidgets('o perfil tem a entrada "Pagamentos"', (tester) async {
    final api = MockApi()
      ..on('GET', myPaymentsPath, 200, PaymentFixtures.page(const []));
    await openAt(tester, api, '/profile');
    await tapInList(
      tester,
      find.byKey(const Key('profile-payments')),
      scrollable: find
          .descendant(
            of: find.byType(ProfileScreen),
            matching: find.byType(Scrollable),
          )
          .first,
    );
    await settle(tester);
    expect(find.byType(PaymentsListScreen), findsOneWidget);
  });

  testWidgets('proposta aceite: "Adquirir apólice" laranja abre o pagamento', (
    tester,
  ) async {
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(status: 'ACCEPTED'),
      )
      ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods());
    await openAt(tester, api, '/proposals/$proposalId');
    final pay = find.byKey(const Key('proposal-pay'));
    expect(find.byType(CtaButton), findsOneWidget);
    expect(tester.widget<CtaButton>(pay).label, 'Adquirir apólice');
    await tapInList(tester, pay);
    await settle(tester);
    expect(find.text('Forma de pagamento'), findsOneWidget);
  });

  testWidgets('proposta paga: texto e nenhum botão laranja', (tester) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal(status: 'PAID'));
    await openAt(tester, api, '/proposals/$proposalId');
    expect(
      find.text('Pagamento recebido. A sua apólice vai ser emitida em breve.'),
      findsOneWidget,
    );
    expect(find.text('Paga'), findsOneWidget);
    expect(find.byType(CtaButton), findsNothing);
  });
}
