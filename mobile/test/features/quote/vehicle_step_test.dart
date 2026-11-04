import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/quote_draft_controller.dart';
import 'package:moseguros/features/quote/widgets/step_scaffold.dart';
import 'package:moseguros/features/vehicles/vehicle_form_screen.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';
import '../../support/vehicles.dart';

void main() {
  late MockApi api;

  setUp(() => api = MockApi());

  testWidgets(
    'sem veículos mostra "Acrescentar veículo", que abre o formulário',
    (tester) async {
      stubQuoteBasics(api, vehicles: [], drivers: []);
      await openQuote(tester, api, QuoteSteps.vehicle);

      expect(find.text('Passo 1 de 5'), findsOneWidget);
      expect(find.text('Ainda não tem veículos registados.'), findsOneWidget);
      final continueButton = tester.widget<FilledButton>(
        find.byKey(const Key('quote-continue')),
      );
      expect(continueButton.onPressed, isNull);
      expect(find.byType(CtaButton), findsNothing);

      await tester.tap(find.byKey(const Key('quote-add-vehicle')));
      await tester.pumpAndSettle();
      expect(find.byType(VehicleFormScreen), findsOneWidget);
      expect(find.text('Passo 1 de 5').hitTestable(), findsNothing);

      // Voltar do formulário regressa ao passo, com a lista recarregada.
      final before = api.calls('GET', '/api/v1/me/vehicles').length;
      final back = find.byType(BackButton).hitTestable();
      expect(back, findsOneWidget);
      await tester.tap(back);
      await tester.pumpAndSettle();
      expect(find.text('Passo 1 de 5'), findsOneWidget);
      expect(
        api.calls('GET', '/api/v1/me/vehicles').length,
        greaterThan(before),
      );
      expect(tester.takeException(), isNull);
    },
  );

  testWidgets('escolher o veículo põe o condutor habitual por defeito', (
    tester,
  ) async {
    stubQuoteBasics(
      api,
      drivers: [
        VehicleFixtures.driver(
          id: '01930000-0000-7000-8000-000000000299',
          fullName: 'Rui Mussa',
          isOwner: true,
        ),
        VehicleFixtures.driver(vehicleIds: [vehicleId]),
      ],
      recent: [QuoteFixtures.summary()],
    );
    final (container, _) = await openQuote(tester, api, QuoteSteps.vehicle);

    expect(find.text('AAA-100-MC'), findsWidgets);
    expect(find.text('Toyota Corolla · 2018'), findsOneWidget);
    // As simulações recentes aparecem por baixo.
    expect(find.byKey(const Key('recent-quotes')), findsOneWidget);

    await tester.tap(find.byKey(const Key('quote-vehicle-$vehicleId')));
    await tester.pumpAndSettle();

    final draft = container.read(quoteDraftControllerProvider);
    expect(draft.vehicleId, vehicleId);
    // O habitual do veículo ganha ao condutor que é o próprio cliente.
    expect(draft.driverId, driverId);
    expect(find.text('Ana Maria Mussa'), findsOneWidget);
    final continueButton = tester.widget<FilledButton>(
      find.byKey(const Key('quote-continue')),
    );
    expect(continueButton.onPressed, isNotNull);
    expect(find.byType(CtaButton), findsNothing);
    expect(tester.takeException(), isNull);
  });

  testWidgets('sem habitual, o condutor por defeito é o próprio cliente', (
    tester,
  ) async {
    stubQuoteBasics(
      api,
      vehicles: [VehicleFixtures.vehicle()],
      drivers: [
        VehicleFixtures.driver(),
        VehicleFixtures.driver(
          id: '01930000-0000-7000-8000-000000000299',
          fullName: 'Rui Mussa',
          isOwner: true,
        ),
      ],
    );
    final (container, _) = await openQuote(tester, api, QuoteSteps.vehicle);
    await tester.tap(find.byKey(const Key('quote-vehicle-$vehicleId')));
    await tester.pumpAndSettle();

    expect(
      container.read(quoteDraftControllerProvider).driverId,
      '01930000-0000-7000-8000-000000000299',
    );
    expect(find.text('Rui Mussa'), findsOneWidget);
  });

  testWidgets('?redo= preenche o rascunho e avisa da data de início', (
    tester,
  ) async {
    stubQuoteBasics(api);
    api.on(
      'GET',
      '/api/v1/quotes/$requestId',
      200,
      QuoteFixtures.request(startDate: '2027-01-05'),
    );
    final (container, _) = await openQuote(
      tester,
      api,
      '${QuoteSteps.vehicle}?redo=$requestId',
    );

    final draft = container.read(quoteDraftControllerProvider);
    expect(draft.vehicleId, vehicleId);
    expect(draft.driverId, driverId);
    expect(draft.productType, 'COMPREHENSIVE');
    expect(draft.coverages!.keys, contains('OD_COLLISION'));
    expect(draft.startDate, quoteToday);
    expect(draft.durationDays, 365);
    expect(
      find.text('Actualizámos a data de início para hoje.'),
      findsOneWidget,
    );
  });
}
