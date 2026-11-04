import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/quote_draft_controller.dart';
import 'package:moseguros/features/quote/screens/quote_results_screen.dart';
import 'package:moseguros/features/quote/screens/summary_step_screen.dart';
import 'package:moseguros/features/quote/widgets/step_scaffold.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';
import '../../support/vehicles.dart';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubQuoteBasics(api);
  });

  void fullDraft(ProviderContainer container) {
    container.read(quoteDraftControllerProvider.notifier)
      ..selectVehicle(vehicleId, defaultDriverId: driverId)
      ..selectType('COMPREHENSIVE')
      ..setCoverages({
        'MTPL': null,
        'VTPL': 250000000,
        'OD_COLLISION': null,
        'OD_FIRE': null,
        'OD_NATURE': null,
      })
      ..setStartDate(DateTime(2027, 1, 15))
      ..setDuration(365);
  }

  /// Pilha como no uso real: os 5 passos empilhados.
  Future<(ProviderContainer, GoRouter)> openSummary(WidgetTester tester) async {
    final (container, router) = await openQuote(
      tester,
      api,
      QuoteSteps.vehicle,
      before: fullDraft,
    );
    for (final step in [
      QuoteSteps.type,
      QuoteSteps.coverages,
      QuoteSteps.period,
      QuoteSteps.summary,
    ]) {
      router.push(step);
      await tester.pumpAndSettle();
    }
    return (container, router);
  }

  testWidgets('blocos do resumo e um só CtaButton "Simular seguro"', (
    tester,
  ) async {
    await openSummary(tester);

    expect(find.text('Passo 5 de 5'), findsOneWidget);
    expect(find.text('AAA-100-MC'), findsOneWidget);
    expect(find.text('Ana Maria Mussa'), findsOneWidget);
    // Idades na data de início (15/01/2027).
    expect(find.text('36 anos, carta há 16 anos'), findsOneWidget);
    expect(
      find.text('Responsabilidade civil + danos próprios'),
      findsOneWidget,
    );
    await tester.scrollUntilVisible(find.text('15/01/2027 a 14/01/2028'), 200);
    expect(find.text('15/01/2027 a 14/01/2028'), findsOneWidget);
    expect(find.text('Responsabilidade civil obrigatória'), findsOneWidget);
    expect(
      find.text(
        'Responsabilidade civil facultativa · ${formatMoney(250000000, 'pt')}',
      ),
      findsOneWidget,
    );
    expect(find.byType(CtaButton), findsOneWidget);
    expect(find.byKey(const Key('quote-continue')), findsNothing);
    expect(tester.takeException(), isNull);
  });

  testWidgets('QUOTE_RATE_LIMITED mostra o erro e os minutos de espera', (
    tester,
  ) async {
    api.on(
      'POST',
      '/api/v1/quotes',
      429,
      MockApi.problem(
        429,
        'QUOTE_RATE_LIMITED',
        extra: {'retryAfterSeconds': 1450},
      ),
    );
    await openSummary(tester);

    await tester.tap(find.text('Simular seguro'));
    await tester.pumpAndSettle();

    expect(
      find.text('Fez muitas simulações na última hora. Tente mais tarde.'),
      findsOneWidget,
    );
    expect(find.text('Tente de novo dentro de 25 minutos.'), findsOneWidget);
    expect(find.byType(SummaryStepScreen), findsOneWidget);
    expect(retryMinutes(30), 1);
    expect(retryMinutes(60), 1);
    expect(retryMinutes(61), 2);
  });

  testWidgets('sucesso: envia o pedido, limpa o rascunho e mostra resultados', (
    tester,
  ) async {
    api
      ..on(
        'POST',
        '/api/v1/quotes',
        201,
        QuoteFixtures.request(),
        const {},
        null,
        const Duration(milliseconds: 300),
      )
      ..on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request());
    final (container, _) = await openSummary(tester);

    await tester.tap(find.text('Simular seguro'));
    await tester.pump(const Duration(milliseconds: 50));
    expect(find.text('A pedir propostas às seguradoras…'), findsOneWidget);
    await tester.pumpAndSettle();

    final body = api.calls('POST', '/api/v1/quotes').single.data as Map;
    expect(body['vehicleId'], vehicleId);
    expect(body['driverId'], driverId);
    expect(body['productType'], 'COMPREHENSIVE');
    expect(body['startDate'], '2027-01-15');
    expect(body['durationDays'], 365);
    expect(body['coverages'], [
      {'code': 'MTPL'},
      {
        'code': 'VTPL',
        'capital': {'amount': 250000000, 'currency': 'MZN'},
      },
      {'code': 'OD_COLLISION'},
      {'code': 'OD_FIRE'},
      {'code': 'OD_NATURE'},
    ]);

    expect(find.byType(QuoteResultsScreen), findsOneWidget);
    expect(find.byType(SummaryStepScreen), findsNothing);
    expect(container.read(quoteDraftControllerProvider).vehicleId, isNull);
  });

  testWidgets('VALIDATION_ERROR volta ao passo do campo e mostra o erro', (
    tester,
  ) async {
    api.on(
      'POST',
      '/api/v1/quotes',
      400,
      MockApi.problem(
        400,
        'VALIDATION_ERROR',
        errors: [
          {'field': 'startDate', 'code': 'START_DATE_OUT_OF_RANGE'},
        ],
      ),
    );
    await openSummary(tester);

    await tester.tap(find.text('Simular seguro'));
    await tester.pumpAndSettle();

    expect(find.text('Passo 4 de 5'), findsOneWidget);
    expect(
      find.text('A data de início tem de ser entre hoje e daqui a 60 dias.'),
      findsOneWidget,
    );
  });

  testWidgets('VEHICLE_NOT_FOUND volta ao passo do veículo com a mensagem', (
    tester,
  ) async {
    api.on(
      'POST',
      '/api/v1/quotes',
      404,
      MockApi.problem(404, 'VEHICLE_NOT_FOUND'),
    );
    await openSummary(tester);

    await tester.tap(find.text('Simular seguro'));
    await tester.pumpAndSettle();

    expect(find.text('Passo 1 de 5'), findsOneWidget);
    expect(find.text('Veículo não encontrado.'), findsOneWidget);
  });

  testWidgets('"Alterar" do tipo volta ao passo 2', (tester) async {
    await openSummary(tester);
    await tester.tap(find.byKey(const Key('quote-change-Tipo de seguro')));
    await tester.pumpAndSettle();
    expect(find.text('Passo 2 de 5'), findsOneWidget);
  });
}
