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
    'COMPREHENSIVE desactivado sem valor comercial; "Indicar valor"',
    (tester) async {
      stubQuoteBasics(
        api,
        vehicles: [VehicleFixtures.vehicle(commercialValue: null)],
      );
      api.on(
        'GET',
        '/api/v1/me/vehicles/$vehicleId',
        200,
        VehicleFixtures.vehicle(commercialValue: null),
      );
      final (container, _) = await openQuote(
        tester,
        api,
        QuoteSteps.type,
        before: (c) => c
            .read(quoteDraftControllerProvider.notifier)
            .selectVehicle(vehicleId),
      );

      expect(find.text('Passo 2 de 5'), findsOneWidget);
      expect(find.text('Só responsabilidade civil'), findsOneWidget);
      expect(
        find.text('O seguro obrigatório: cobre os danos que causar a outros.'),
        findsOneWidget,
      );
      expect(
        find.text('Precisa do valor comercial do veículo'),
        findsOneWidget,
      );

      // Tocar no cartão desactivado não o escolhe.
      await tester.tap(find.text('Responsabilidade civil + danos próprios'));
      await tester.pumpAndSettle();
      expect(container.read(quoteDraftControllerProvider).productType, isNull);

      await tester.tap(find.byKey(const Key('quote-type-MTPL_ONLY')));
      await tester.pumpAndSettle();
      expect(
        container.read(quoteDraftControllerProvider).productType,
        'MTPL_ONLY',
      );
      expect(
        tester
            .widget<FilledButton>(find.byKey(const Key('quote-continue')))
            .onPressed,
        isNotNull,
      );
      expect(find.byType(CtaButton), findsNothing);

      await tester.tap(find.byKey(const Key('quote-set-value')));
      await tester.pumpAndSettle();
      expect(find.byType(VehicleFormScreen), findsOneWidget);
      expect(tester.takeException(), isNull);
    },
  );

  testWidgets('com valor comercial, COMPREHENSIVE pode ser escolhido', (
    tester,
  ) async {
    stubQuoteBasics(api);
    final (container, _) = await openQuote(
      tester,
      api,
      QuoteSteps.type,
      before: (c) => c
          .read(quoteDraftControllerProvider.notifier)
          .selectVehicle(vehicleId),
    );

    expect(find.text('Precisa do valor comercial do veículo'), findsNothing);
    await tester.tap(find.byKey(const Key('quote-type-COMPREHENSIVE')));
    await tester.pumpAndSettle();
    final draft = container.read(quoteDraftControllerProvider);
    expect(draft.productType, 'COMPREHENSIVE');
    expect(draft.durationDays, 365);
  });
}
