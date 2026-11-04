import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/format/money.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/quote_draft_controller.dart';
import 'package:moseguros/features/quote/widgets/step_scaffold.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';
import '../../support/vehicles.dart';

void main() {
  late MockApi api;

  setUp(() => api = MockApi());

  void draftFor(ProviderContainer container, String type) {
    container.read(quoteDraftControllerProvider.notifier)
      ..selectVehicle(vehicleId, defaultDriverId: driverId)
      ..selectType(type);
  }

  bool checked(WidgetTester tester, String code) =>
      tester
          .widget<CheckboxListTile>(find.byKey(Key('quote-coverage-$code')))
          .value ??
      false;

  Future<void> tapCoverage(WidgetTester tester, String code) async {
    final finder = find.byKey(Key('quote-coverage-$code'));
    await tester.ensureVisible(finder);
    await tester.pumpAndSettle();
    await tester.tap(finder);
    await tester.pumpAndSettle();
  }

  bool canContinue(WidgetTester tester) =>
      tester
          .widget<FilledButton>(find.byKey(const Key('quote-continue')))
          .onPressed !=
      null;

  testWidgets('COMPREHENSIVE: obrigatórias, união e regras de "Furto ou roubo"', (
    tester,
  ) async {
    stubQuoteBasics(api);
    final (container, _) = await openQuote(
      tester,
      api,
      QuoteSteps.coverages,
      before: (c) => draftFor(c, 'COMPREHENSIVE'),
    );

    expect(find.text('Passo 3 de 5'), findsOneWidget);
    // Catálogo pela categoria e pelo uso do veículo.
    final catalog = api.calls('GET', '/api/v1/catalog/products').last;
    expect(catalog.queryParameters['vehicleCategory'], 'LIGHT_PASSENGER');
    expect(catalog.queryParameters['usage'], 'PRIVATE');

    // Obrigatória marcada e desactivada.
    final mtpl = tester.widget<CheckboxListTile>(
      find.byKey(const Key('quote-coverage-MTPL')),
    );
    expect(mtpl.value, isTrue);
    expect(mtpl.onChanged, isNull);
    expect(find.text('Obrigatória'), findsOneWidget);
    expect(find.text('Disponível em 1 de 2 produtos'), findsNWidgets(2));

    // Por defeito: choque, incêndio e fenómenos da natureza.
    expect(checked(tester, 'OD_COLLISION'), isTrue);
    expect(checked(tester, 'OD_FIRE'), isTrue);
    expect(checked(tester, 'OD_NATURE'), isTrue);
    expect(checked(tester, 'OD_THEFT'), isFalse);
    expect(canContinue(tester), isTrue);

    // Desmarcar o incêndio e marcar o furto volta a marcar o incêndio.
    await tapCoverage(tester, 'OD_FIRE');
    expect(checked(tester, 'OD_FIRE'), isFalse);
    await tapCoverage(tester, 'OD_THEFT');
    expect(checked(tester, 'OD_THEFT'), isTrue);
    expect(checked(tester, 'OD_FIRE'), isTrue);
    expect(
      find.text(
        'Furto ou roubo inclui obrigatoriamente choque, incêndio e fenómenos da natureza.',
      ),
      findsOneWidget,
    );

    // Desmarcar a natureza desmarca o furto.
    await tapCoverage(tester, 'OD_NATURE');
    expect(checked(tester, 'OD_THEFT'), isFalse);
    expect(
      container.read(quoteDraftControllerProvider).coverages!.keys.toSet(),
      {'MTPL', 'OD_COLLISION', 'OD_FIRE'},
    );
    expect(find.byType(CtaButton), findsNothing);
    expect(tester.takeException(), isNull);
  });

  testWidgets(
    'sem danos próprios: OWN_DAMAGE_REQUIRED e "Continuar" desactivado',
    (tester) async {
      stubQuoteBasics(api);
      await openQuote(
        tester,
        api,
        QuoteSteps.coverages,
        before: (c) => draftFor(c, 'COMPREHENSIVE'),
      );

      await tapCoverage(tester, 'OD_COLLISION');
      await tapCoverage(tester, 'OD_FIRE');
      expect(canContinue(tester), isTrue);
      await tapCoverage(tester, 'OD_NATURE');

      expect(
        find.text('Escolha pelo menos uma cobertura de danos próprios.'),
        findsOneWidget,
      );
      expect(canContinue(tester), isFalse);
    },
  );

  testWidgets('capital OPTIONS: união por ordem crescente, defeito o menor', (
    tester,
  ) async {
    stubQuoteBasics(api);
    final (container, _) = await openQuote(
      tester,
      api,
      QuoteSteps.coverages,
      before: (c) => draftFor(c, 'COMPREHENSIVE'),
    );

    await tapCoverage(tester, 'VTPL');
    expect(find.text(formatMoney(250000000, 'pt')), findsOneWidget);
    expect(
      container.read(quoteDraftControllerProvider).coverages!['VTPL'],
      250000000,
    );
  });

  testWidgets('sem produtos do tipo: aviso e "Continuar" desactivado', (
    tester,
  ) async {
    stubQuoteBasics(api);
    await openQuote(
      tester,
      api,
      QuoteSteps.coverages,
      before: (c) => draftFor(c, 'TEMPORARY'),
    );

    expect(
      find.text('De momento não há produtos deste tipo para este veículo.'),
      findsOneWidget,
    );
    expect(canContinue(tester), isFalse);
  });
}
