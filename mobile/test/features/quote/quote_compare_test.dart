import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/screens/quote_compare_screen.dart';
import 'package:moseguros/features/quote/screens/quote_detail_screen.dart';
import 'package:moseguros/features/quote/screens/quote_results_screen.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';

void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubQuoteBasics(api);
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
            total: 6100000,
          ),
          QuoteFixtures.quote(
            id: quoteUnavailableId,
            insurer: 'DEMOX',
            status: 'NOT_AVAILABLE',
          ),
        ],
      ),
    );
  });

  String compare(List<String> ids) =>
      '/quote/results/$requestId/compare?ids=${ids.join(',')}';

  testWidgets('linhas diferentes destacadas e "Mais barata" no cabeçalho', (
    tester,
  ) async {
    await openQuote(tester, api, compare([quoteDemoId, quoteDemobId]));

    expect(find.text('Seguradora'), findsOneWidget);
    expect(find.text('Seguradora DEMO'), findsOneWidget);
    expect(
      find.byKey(const Key('compare-cheapest-$quoteDemoId')),
      findsOneWidget,
    );
    expect(
      find.byKey(const Key('compare-cheapest-$quoteDemobId')),
      findsNothing,
    );
    // Total e prémio comercial diferem; a validade é igual.
    expect(
      find.byKey(const Key('compare-differs-Prémio total')),
      findsOneWidget,
    );
    expect(
      find.byKey(const Key('compare-differs-Prémio comercial')),
      findsOneWidget,
    );
    expect(find.byKey(const Key('compare-differs-Válida até')), findsNothing);
    final total = tester.widget<Text>(
      find.byKey(const Key('compare-differs-Prémio total')),
    );
    expect(total.style?.fontWeight, FontWeight.w700);
    // Os encargos têm códigos diferentes: "—" onde uma seguradora não o tem.
    expect(find.text('—'), findsNWidgets(2));
    expect(find.byType(CtaButton), findsNothing);
    expect(tester.takeException(), isNull);
  });

  testWidgets('três colunas com scroll horizontal, sem overflow a 360 dp', (
    tester,
  ) async {
    await openQuote(
      tester,
      api,
      compare([quoteDemoId, quoteDemobId, quoteThirdId]),
    );
    expect(tester.takeException(), isNull);

    expect(find.byKey(const Key('compare-scroll')), findsOneWidget);
    final third = find.byKey(const Key('compare-column-$quoteThirdId'));
    // A terceira coluna começa fora do ecrã (112 + 2 × 152 > 360).
    expect(tester.getTopLeft(third).dx, greaterThan(360));
    final labelBefore = tester.getTopLeft(find.text('Produto'));

    await tester.dragFrom(
      tester.getCenter(find.text('Seguradora DEMOB')),
      const Offset(-300, 0),
    );
    await tester.pumpAndSettle();
    expect(tester.getTopLeft(third).dx, lessThan(360));
    // A coluna dos nomes fica fixa.
    expect(tester.getTopLeft(find.text('Produto')).dx, labelBefore.dx);
    expect(tester.takeException(), isNull);
  });

  testWidgets('"Ver detalhe" abre o detalhe da coluna', (tester) async {
    await openQuote(tester, api, compare([quoteDemoId, quoteDemobId]));
    final detail = find.byKey(const Key('compare-detail-$quoteDemoId'));
    await tester.scrollUntilVisible(
      detail,
      200,
      scrollable: find.byType(Scrollable).first,
    );
    await tester.tap(detail);
    await tester.pumpAndSettle();
    final screen = tester.widget<QuoteDetailScreen>(
      find.byType(QuoteDetailScreen),
    );
    expect(screen.quoteId, quoteDemoId);
  });

  testWidgets('um só id volta aos resultados', (tester) async {
    await openQuote(tester, api, compare([quoteDemoId]));
    await tester.pumpAndSettle();
    expect(find.byType(QuoteResultsScreen), findsOneWidget);
    expect(find.byType(QuoteCompareScreen), findsNothing);
  });

  testWidgets('uma cotação sem proposta volta aos resultados', (tester) async {
    await openQuote(tester, api, compare([quoteDemoId, quoteUnavailableId]));
    await tester.pumpAndSettle();
    expect(find.byType(QuoteResultsScreen), findsOneWidget);
    expect(find.byType(QuoteCompareScreen), findsNothing);
  });
}
