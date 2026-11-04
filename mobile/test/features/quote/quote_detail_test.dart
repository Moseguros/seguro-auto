import 'package:dio/dio.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/core/widgets/cta_button.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';
import '../../support/vehicles.dart';

void main() {
  late MockApi api;
  late FakeDevice device;

  setUp(() {
    api = MockApi();
    device = FakeDevice();
    stubQuoteBasics(api);
    api.on(
      'GET',
      '/api/v1/quotes/$requestId',
      200,
      QuoteFixtures.request(
        quotes: [
          QuoteFixtures.quote(),
          QuoteFixtures.quote(
            id: quoteUnavailableId,
            insurer: 'DEMOX',
            status: 'NOT_AVAILABLE',
            reason: 'INSURER_DECLINED',
          ),
        ],
      ),
    );
  });

  testWidgets('coberturas, encargos, total e "Informação pré-contratual"', (
    tester,
  ) async {
    api.onBytes('/api/v1/files/$preContractFileId', tinyPng, 'application/pdf');
    final sent = <String>[];
    api.client.dio.interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          sent.add('${options.method} ${options.path}');
          handler.next(options);
        },
      ),
    );
    await openQuote(
      tester,
      api,
      '/quote/results/$requestId/quotes/$quoteDemoId',
      overrides: device.overrides,
    );

    expect(find.text('Seguradora DEMO'), findsOneWidget);
    expect(find.text('Auto DEMO · versão 1'), findsOneWidget);
    expect(find.text('Responsabilidade civil obrigatória'), findsOneWidget);
    expect(
      find.text('Franquia 10 % (mín. ${formatMoney(2500000, 'pt')})'),
      findsOneWidget,
    );
    await tester.scrollUntilVisible(find.text('Prémio total'), 200);
    expect(find.text('Imposto de selo'), findsOneWidget);
    expect(find.text('Ajuste de arredondamento'), findsOneWidget);
    expect(find.text(formatMoney(5226200, 'pt')), findsOneWidget);

    final button = find.text('Informação pré-contratual');
    await tester.scrollUntilVisible(button, 200);
    await tester.tap(button);
    await tester.pumpAndSettle();

    expect(sent, contains('GET /api/v1/files/$preContractFileId'));
    expect(device.opener.opened.single.contentType, 'application/pdf');
    expect(device.opener.opened.single.name, 'informacao-pre-contratual.pdf');
    // S4-09 §2: "Adquirir apólice" é o único botão laranja do ecrã.
    await tester.scrollUntilVisible(find.text('Adquirir apólice'), 200);
    expect(find.byType(CtaButton), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('"Adquirir apólice" abre /proposals/new?quoteId=', (
    tester,
  ) async {
    api.on('POST', '/api/v1/proposals', 409, MockApi.problem(409, 'QUOTE_EXPIRED'));
    final (_, router) = await openQuote(
      tester,
      api,
      '/quote/results/$requestId/quotes/$quoteDemoId',
    );
    final button = find.byKey(const Key('quote-acquire'));
    await tester.scrollUntilVisible(button, 200);
    await tester.ensureVisible(button);
    await tester.pumpAndSettle();
    await tester.tap(button);
    await tester.pumpAndSettle();
    await tester.pump(const Duration(milliseconds: 50));
    await tester.pumpAndSettle();

    expect(
      router.routerDelegate.currentConfiguration.last.matchedLocation,
      '/proposals/new',
    );
    final body = api.calls('POST', '/api/v1/proposals').single.data;
    expect(body, {'quoteId': quoteDemoId, 'channel': 'APP'});
    expect(
      find.text('Esta cotação já expirou. Faça uma nova simulação.'),
      findsOneWidget,
    );
    expect(find.byKey(const Key('new-proposal-simulate')), findsOneWidget);
  });

  testWidgets('NOT_AVAILABLE mostra só o motivo', (tester) async {
    await openQuote(
      tester,
      api,
      '/quote/results/$requestId/quotes/$quoteUnavailableId',
    );
    expect(find.text('Sem proposta'), findsOneWidget);
    expect(find.text('A seguradora não apresentou proposta.'), findsOneWidget);
    expect(find.text('Prémio total'), findsNothing);
  });
}
