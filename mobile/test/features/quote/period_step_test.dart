import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/quote_draft_controller.dart';
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

  void draftFor(ProviderContainer container, String type) {
    container.read(quoteDraftControllerProvider.notifier)
      ..selectVehicle(vehicleId, defaultDriverId: driverId)
      ..selectType(type);
  }

  testWidgets('limites do showDatePicker: hoje a hoje + 60 dias (Maputo)', (
    tester,
  ) async {
    final (container, _) = await openQuote(
      tester,
      api,
      QuoteSteps.period,
      before: (c) => draftFor(c, 'COMPREHENSIVE'),
    );

    expect(find.text('Passo 4 de 5'), findsOneWidget);
    // Sem escolha, começa hoje; fora de TEMPORARY a duração é fixa.
    expect(find.text('10/01/2027'), findsOneWidget);
    expect(find.byKey(const Key('quote-duration-fixed')), findsOneWidget);
    expect(find.byKey(const Key('quote-durations')), findsNothing);
    expect(find.text('Termina a 09/01/2028'), findsOneWidget);

    await tester.tap(find.byKey(const Key('quote-start-date')));
    await tester.pumpAndSettle();
    final picker = tester.widget<DatePickerDialog>(
      find.byType(DatePickerDialog),
    );
    expect(picker.firstDate, DateTime(2027, 1, 10));
    expect(picker.lastDate, DateTime(2027, 3, 11));

    await tester.tap(find.text('15'));
    await tester.tap(find.text('OK'));
    await tester.pumpAndSettle();
    expect(
      container.read(quoteDraftControllerProvider).startDate,
      DateTime(2027, 1, 15),
    );
    expect(find.text('Termina a 14/01/2028'), findsOneWidget);
    expect(find.byType(CtaButton), findsNothing);
    expect(tester.takeException(), isNull);
  });

  testWidgets('TEMPORARY: escolher a duração (7 a 60 dias ou 1 ano)', (
    tester,
  ) async {
    final (container, _) = await openQuote(
      tester,
      api,
      QuoteSteps.period,
      before: (c) => draftFor(c, 'TEMPORARY'),
    );

    for (final label in ['7 dias', '14 dias', '30 dias', '60 dias', '1 ano']) {
      expect(find.text(label), findsOneWidget);
    }
    // Sem duração escolhida não se pode continuar.
    expect(
      tester
          .widget<FilledButton>(find.byKey(const Key('quote-continue')))
          .onPressed,
      isNull,
    );

    await tester.tap(find.byKey(const Key('quote-duration-30')));
    await tester.pumpAndSettle();
    expect(container.read(quoteDraftControllerProvider).durationDays, 30);
    expect(find.text('Termina a 08/02/2027'), findsOneWidget);
    expect(
      tester
          .widget<FilledButton>(find.byKey(const Key('quote-continue')))
          .onPressed,
      isNotNull,
    );
  });
}
