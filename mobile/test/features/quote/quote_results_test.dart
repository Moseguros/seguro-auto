import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/screens/quote_compare_screen.dart';
import 'package:moseguros/features/quote/screens/vehicle_step_screen.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubQuoteBasics(api);
  });

  const results = '/quote/results/$requestId';

  Future<void> tapCompare(WidgetTester tester, String id) async {
    final box = find.byKey(Key('quote-compare-$id'));
    await tester.ensureVisible(box);
    await tester.pumpAndSettle();
    await tester.tap(box);
    await tester.pumpAndSettle();
  }

  VoidCallback? compareAction(WidgetTester tester) => tester
      .widget<CtaButton>(find.byKey(const Key('compare-button')))
      .onPressed;

  testWidgets('ordem da API, "Mais barata" só na primeira, motivos traduzidos', (
    tester,
  ) async {
    api.on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request());
    await openQuote(tester, api, results);
    // Ecrã alto (360 de largura) para ver os dois cartões de uma vez.
    tester.view.physicalSize = const Size(360, 2400);
    await tester.pumpAndSettle();

    final demo = tester.getTopLeft(
      find.byKey(const Key('quote-card-$quoteDemoId')),
    );
    final demob = tester.getTopLeft(
      find.byKey(const Key('quote-card-$quoteDemobId')),
    );
    expect(demo.dy, lessThan(demob.dy));
    expect(find.text(formatMoney(5226200, 'pt')), findsOneWidget);
    expect(
      find.byKey(const Key('quote-cheapest-$quoteDemoId')),
      findsOneWidget,
    );
    expect(find.byKey(const Key('quote-cheapest-$quoteDemobId')), findsNothing);
    expect(
      find.text('Inclui ${formatMoney(127344, 'pt')} de encargos'),
      findsWidgets,
    );
    expect(
      find.text(
        'Esta simulação fica guardada em As minhas cotações e é válida até 25/01/2027.',
      ),
      findsOneWidget,
    );
    // Sem selecção não há barra laranja.
    expect(find.byType(CtaButton), findsNothing);

    // "Sem proposta (1)" recolhido, com o motivo traduzido.
    final unavailable = find.text('Sem proposta (1)');
    await tester.scrollUntilVisible(unavailable, 200);
    await tester.tap(unavailable);
    await tester.pumpAndSettle();
    expect(find.text('A seguradora não respondeu a tempo.'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('CompareBar: só activa com 2 ou 3; a quarta é recusada', (
    tester,
  ) async {
    api.on(
      'GET',
      '/api/v1/quotes/$requestId',
      200,
      QuoteFixtures.request(
        quotes: [
          QuoteFixtures.quote(),
          QuoteFixtures.quote(
            id: quoteDemobId,
            insurer: 'DEMOB',
            total: 6100000,
          ),
          QuoteFixtures.quote(
            id: quoteThirdId,
            insurer: 'DEMOC',
            total: 6500000,
          ),
          QuoteFixtures.quote(
            id: '01930000-0000-7000-8000-000000000605',
            insurer: 'DEMOD',
            total: 7000000,
          ),
        ],
      ),
    );
    await openQuote(tester, api, results);

    await tapCompare(tester, quoteDemoId);
    expect(find.text('1 seleccionadas (máximo 3)'), findsOneWidget);
    expect(compareAction(tester), isNull);
    expect(find.byType(CtaButton), findsOneWidget);

    await tapCompare(tester, quoteDemobId);
    expect(compareAction(tester), isNotNull);
    await tapCompare(tester, quoteThirdId);
    expect(find.text('3 seleccionadas (máximo 3)'), findsOneWidget);
    expect(compareAction(tester), isNotNull);

    await tapCompare(tester, '01930000-0000-7000-8000-000000000605');
    expect(find.text('Pode comparar até 3 propostas'), findsOneWidget);
    expect(find.text('3 seleccionadas (máximo 3)'), findsOneWidget);
    expect(find.byType(CtaButton), findsOneWidget);

    // O aviso desaparece sozinho.
    await tester.pump(const Duration(seconds: 5));
    await tester.pumpAndSettle();
    await tester.tap(find.byKey(const Key('compare-button')));
    await tester.pumpAndSettle();
    final compare = tester.widget<QuoteCompareScreen>(
      find.byType(QuoteCompareScreen),
    );
    expect(compare.ids, [quoteDemoId, quoteDemobId, quoteThirdId]);
  });

  testWidgets('expirada: aviso, "Simular seguro" e sem caixas "Comparar"', (
    tester,
  ) async {
    api.on(
      'GET',
      '/api/v1/quotes/$requestId',
      200,
      QuoteFixtures.request(
        quotes: [
          QuoteFixtures.quote(status: 'EXPIRED'),
          QuoteFixtures.quote(
            id: quoteDemobId,
            insurer: 'DEMOB',
            status: 'EXPIRED',
            total: 6100000,
          ),
        ],
      ),
    );
    await openQuote(tester, api, results);

    expect(find.text('Esta simulação expirou em 25/01/2027.'), findsOneWidget);
    expect(find.byType(Checkbox), findsNothing);
    expect(find.byType(CtaButton), findsOneWidget);
    expect(find.text('Mais barata'), findsNothing);

    await tester.tap(find.text('Simular seguro'));
    await tester.pumpAndSettle();
    final step = tester.widget<VehicleStepScreen>(
      find.byType(VehicleStepScreen),
    );
    expect(step.redo, requestId);
  });

  testWidgets('sem propostas: texto e "Alterar simulação" azul', (
    tester,
  ) async {
    api.on(
      'GET',
      '/api/v1/quotes/$requestId',
      200,
      QuoteFixtures.request(
        quotes: [
          QuoteFixtures.quote(
            id: quoteUnavailableId,
            status: 'NOT_AVAILABLE',
            reason: 'NO_MATCHING_ROW',
          ),
        ],
      ),
    );
    await openQuote(tester, api, results);

    expect(
      find.text('Nenhuma seguradora apresentou proposta para estas condições.'),
      findsOneWidget,
    );
    expect(
      find.widgetWithText(FilledButton, 'Alterar simulação'),
      findsOneWidget,
    );
    expect(find.byType(CtaButton), findsNothing);
  });

  testWidgets('puxar para actualizar volta a pedir a cotação', (tester) async {
    api.on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request());
    await openQuote(tester, api, results);
    final before = api.calls('GET', '/api/v1/quotes/$requestId').length;

    await tester.fling(
      find.byKey(const Key('quote-card-$quoteDemoId')),
      const Offset(0, 400),
      1000,
    );
    await tester.pumpAndSettle();
    expect(
      api.calls('GET', '/api/v1/quotes/$requestId').length,
      greaterThan(before),
    );
  });

  testWidgets('pedido que não existe: "Cotação não encontrada"', (
    tester,
  ) async {
    api.on(
      'GET',
      '/api/v1/quotes/other',
      404,
      MockApi.problem(404, 'QUOTE_REQUEST_NOT_FOUND'),
    );
    await openQuote(tester, api, '/quote/results/other');
    expect(find.text('Cotação não encontrada.'), findsOneWidget);
    expect(find.text('Ver as minhas cotações'), findsOneWidget);
  });
}
