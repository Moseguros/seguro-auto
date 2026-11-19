import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/payments/payment_intent_screen.dart';
import 'package:moseguros/features/payments/payment_screen.dart';
import 'package:moseguros/features/proposals/proposal_screen.dart';

import '../test/support/mock_api.dart';
import '../test/support/payments.dart';
import '../test/support/proposals.dart';

/// Fluxo principal do S5-11 com a API simulada: proposta aceite → "Adquirir
/// apólice" → M-Pesa → espera → confirmado.
///
/// Sem emulador: `flutter test integration_test/payment_flow_test.dart -d flutter-tester`.
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('proposta aceite → pagar com M-Pesa → espera → confirmado', (
    tester,
  ) async {
    var reads = 0;
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(status: 'ACCEPTED'),
      )
      ..on('GET', paymentMethodsPath, 200, PaymentFixtures.methods())
      ..on('POST', proposalPayPath, 202, PaymentFixtures.intent())
      ..on('GET', paymentIntentPath, 200, (_) {
        reads++;
        return reads < 3
            ? PaymentFixtures.intent()
            : PaymentFixtures.intent(
                status: 'SUCCEEDED',
                nextAction: {'type': 'NONE'},
              );
      });
    await openAt(tester, api, '/proposals/$proposalId');
    expect(find.byType(ProposalScreen), findsOneWidget);

    // "Adquirir apólice" na proposta aceite.
    await tapInList(tester, find.byKey(const Key('proposal-pay')));
    await settle(tester);
    expect(find.byType(PaymentScreen), findsOneWidget);

    // M-Pesa com o telemóvel da conta e "Adquirir apólice".
    await chooseMethod(tester, 'MPESA');
    await tester.tap(find.byType(CtaButton));
    await settle(tester);
    expect(find.byType(PaymentIntentScreen), findsOneWidget);
    expect(find.text('Confirme o pagamento no seu telemóvel'), findsOneWidget);
    expect(
      api.calls('POST', proposalPayPath).single.data,
      containsPair('payerMsisdn', '+258841234567'),
    );

    // Espera: duas leituras de 3 em 3 s até à confirmação.
    for (var i = 0; i < 2; i++) {
      await tester.pump(const Duration(seconds: 3));
      await settle(tester);
    }
    expect(find.text('Pagamento confirmado'), findsOneWidget);

    await tester.tap(find.byKey(const Key('payment-view-proposal')));
    await settle(tester);
    expect(find.byType(ProposalScreen), findsOneWidget);
  });
}
