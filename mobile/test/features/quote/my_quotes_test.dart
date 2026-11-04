import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/screens/quote_results_screen.dart';
import 'package:moseguros/features/quote/screens/vehicle_step_screen.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';

void main() {
  late MockApi api;

  setUp(() => api = MockApi());

  testWidgets('vazio: "Ainda não fez simulações." e "Simular seguro"', (
    tester,
  ) async {
    stubQuoteBasics(api);
    await openQuote(tester, api, '/quotes');

    expect(find.text('Ainda não fez simulações.'), findsOneWidget);
    expect(find.byType(CtaButton), findsOneWidget);
    await tester.tap(find.text('Simular seguro'));
    await tester.pumpAndSettle();
    expect(find.byType(VehicleStepScreen), findsOneWidget);
  });

  testWidgets('cartões, "Pelo mediador", "Expirada" e mais páginas no fim', (
    tester,
  ) async {
    stubQuoteBasics(api);
    final first = [
      QuoteFixtures.summary(origin: 'BROKER'),
      for (var i = 1; i < 20; i++)
        QuoteFixtures.summary(
          id: '01930000-0000-7000-8000-0000000008${i.toString().padLeft(2, '0')}',
        ),
    ];
    api.on('GET', '/api/v1/me/quotes', 200, (RequestOptions options) {
      final page = options.queryParameters['page'] as int? ?? 0;
      return page == 0
          ? QuoteFixtures.page(first, total: 21)
          : QuoteFixtures.page(
              [
                QuoteFixtures.summary(
                  id: '01930000-0000-7000-8000-000000000899',
                  expired: true,
                  offeredCount: 0,
                ),
              ],
              total: 21,
              page: 1,
            );
    });
    await openQuote(tester, api, '/quotes');

    expect(find.text('Pelo mediador'), findsOneWidget);
    expect(find.text('2 propostas'), findsWidgets);
    expect(find.text('desde ${formatMoney(5226200, 'pt')}'), findsWidgets);
    expect(find.text('Válida até 25/01/2027'), findsWidgets);
    expect(find.byType(CtaButton), findsNothing);

    final last = find.byKey(
      const Key('quote-summary-01930000-0000-7000-8000-000000000899'),
    );
    await tester.scrollUntilVisible(last, 400);
    await tester.pumpAndSettle();
    final pages = [
      for (final call in api.calls('GET', '/api/v1/me/quotes'))
        call.queryParameters['page'],
    ];
    expect(pages, containsAll([0, 1]));
    expect(find.text('Expirada'), findsOneWidget);

    await tester.tap(last);
    await tester.pumpAndSettle();
    expect(find.byType(QuoteResultsScreen), findsOneWidget);
  });
}
