import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/features/quote/quote_labels.dart';
import 'package:moseguros/features/quote/screens/quote_compare_screen.dart';
import 'package:moseguros/features/quote/screens/quote_detail_screen.dart';

import '../test/support/mock_api.dart';
import '../test/support/quotes.dart';
import '../test/support/vehicles.dart';

/// Fluxo principal do S3-05 com a API simulada.
///
/// Num emulador Android:
///   flutter test integration_test/quote_flow_test.dart --flavor dev
/// Sem emulador: `flutter test integration_test/quote_flow_test.dart -d flutter-tester`.
Future<void> pumpUntilFound(WidgetTester tester, Finder finder) async {
  for (var i = 0; i < 100 && finder.evaluate().isEmpty; i++) {
    await tester.pump(const Duration(milliseconds: 100));
  }
  await tester.pumpAndSettle();
}

Future<void> tapVisible(WidgetTester tester, Finder finder) async {
  await pumpUntilFound(tester, finder);
  await tester.ensureVisible(finder);
  await tester.pumpAndSettle();
  await tester.tap(finder);
  await tester.pumpAndSettle();
}

Future<void> next(WidgetTester tester) =>
    tapVisible(tester, find.byKey(const Key('quote-continue')));

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'início → "Simular seguro" → veículo → RC + danos próprios → coberturas → '
    'data → "Simular seguro" → duas propostas → comparar → detalhe',
    (tester) async {
      final api = MockApi();
      stubQuoteBasics(api);
      api
        ..on(
          'POST',
          '/api/v1/quotes',
          201,
          QuoteFixtures.request(quotes: QuoteFixtures.defaultQuotes()),
        )
        ..on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request());

      await pumpMoSeguros(
        tester,
        api,
        refreshToken: 'refresh-0',
        overrides: [quoteTodayProvider.overrideWithValue(() => quoteToday)],
      );

      // Início.
      await tapVisible(tester, find.byKey(const Key('home-quote')));
      expect(find.text('Passo 1 de 5'), findsOneWidget);

      // Veículo: o condutor habitual fica escolhido.
      await tapVisible(
        tester,
        find.byKey(const Key('quote-vehicle-$vehicleId')),
      );
      expect(find.text('Ana Maria Mussa'), findsOneWidget);
      await next(tester);

      // Tipo.
      await tapVisible(
        tester,
        find.text('Responsabilidade civil + danos próprios'),
      );
      await next(tester);

      // Coberturas por defeito.
      expect(find.text('Passo 3 de 5'), findsOneWidget);
      await next(tester);

      // Data de início: 15/01/2027.
      expect(find.text('Passo 4 de 5'), findsOneWidget);
      await tapVisible(tester, find.byKey(const Key('quote-start-date')));
      await tester.tap(find.text('15'));
      await tester.tap(find.text('OK'));
      await tester.pumpAndSettle();
      expect(find.text('Termina a 14/01/2028'), findsOneWidget);
      await next(tester);

      // Resumo → "Simular seguro".
      expect(find.text('Passo 5 de 5'), findsOneWidget);
      await tapVisible(tester, find.text('Simular seguro'));
      final body = api.calls('POST', '/api/v1/quotes').single.data as Map;
      expect(body['startDate'], '2027-01-15');
      expect(body['productType'], 'COMPREHENSIVE');

      // Duas propostas, a mais barata primeiro.
      await pumpUntilFound(tester, find.text(formatMoney(5226200, 'pt')));
      expect(find.text('Mais barata'), findsOneWidget);
      await tapVisible(
        tester,
        find.byKey(const Key('quote-compare-$quoteDemoId')),
      );
      await tapVisible(
        tester,
        find.byKey(const Key('quote-compare-$quoteDemobId')),
      );
      expect(find.text('2 seleccionadas (máximo 3)'), findsOneWidget);
      await tapVisible(tester, find.text('Comparar propostas'));

      // Comparação.
      expect(find.byType(QuoteCompareScreen), findsOneWidget);
      expect(find.text('Seguradora DEMOB'), findsOneWidget);
      await tapVisible(
        tester,
        find.byKey(const Key('compare-detail-$quoteDemoId')),
      );

      // Detalhe.
      expect(find.byType(QuoteDetailScreen), findsOneWidget);
      expect(find.text('Auto DEMO · versão 1'), findsOneWidget);
      expect(tester.takeException(), isNull);
    },
  );
}
