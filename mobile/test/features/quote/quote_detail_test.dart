import 'package:dio/dio.dart';
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
    expect(find.byType(CtaButton), findsNothing);

    final button = find.text('Informação pré-contratual');
    await tester.scrollUntilVisible(button, 200);
    await tester.tap(button);
    await tester.pumpAndSettle();

    expect(sent, contains('GET /api/v1/files/$preContractFileId'));
    expect(device.opener.opened.single.contentType, 'application/pdf');
    expect(device.opener.opened.single.name, 'informacao-pre-contratual.pdf');
    expect(
      find.text('Em breve vai poder avançar daqui para a proposta.'),
      findsOneWidget,
    );
    expect(tester.takeException(), isNull);
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
