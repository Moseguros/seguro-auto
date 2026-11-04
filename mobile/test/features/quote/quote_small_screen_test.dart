import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/quote/quote_draft_controller.dart';
import 'package:moseguros/features/quote/widgets/step_scaffold.dart';

import '../../support/mock_api.dart';
import '../../support/quotes.dart';
import '../../support/vehicles.dart';

/// Ecrãs a 360×740 com o texto a 1,3: sem overflow e com os botões fixos e o
/// total visíveis (S3-05 §9); no máximo um `CtaButton` em cada ecrã.
void main() {
  late MockApi api;

  setUp(() {
    api = MockApi();
    stubQuoteBasics(api, recent: [QuoteFixtures.summary()]);
    api.on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request());
  });

  void fullDraft(ProviderContainer container) {
    container.read(quoteDraftControllerProvider.notifier)
      ..selectVehicle(vehicleId, defaultDriverId: driverId)
      ..selectType('COMPREHENSIVE')
      ..setCoverages({
        'MTPL': null,
        'OD_COLLISION': null,
        'OD_FIRE': null,
        'OD_NATURE': null,
      })
      ..setStartDate(DateTime(2027, 1, 15))
      ..setDuration(365);
  }

  final screens = {
    QuoteSteps.vehicle: 'quote-continue',
    QuoteSteps.type: 'quote-continue',
    QuoteSteps.coverages: 'quote-continue',
    QuoteSteps.period: 'quote-continue',
    QuoteSteps.summary: 'quote-submit',
    '/quote/results/$requestId': 'quote-total-$quoteDemoId',
    '/quote/results/$requestId/compare?ids=$quoteDemoId,$quoteDemobId':
        'compare-column-$quoteDemoId',
    '/quote/results/$requestId/quotes/$quoteDemoId': 'quote-status-none',
    '/quotes': 'quote-summary-$requestId',
  };

  for (final MapEntry(key: location, value: visible) in screens.entries) {
    testWidgets('$location a 360 dp com texto a 1,3', (tester) async {
      tester.platformDispatcher.textScaleFactorTestValue = 1.3;
      addTearDown(tester.platformDispatcher.clearTextScaleFactorTestValue);
      await openQuote(tester, api, location, before: fullDraft);

      expect(tester.takeException(), isNull);
      expect(find.byType(CtaButton).evaluate().length, lessThanOrEqualTo(1));
      if (visible != 'quote-status-none') {
        // Os botões fixos já estão à vista; o resto, depois do scroll.
        await tester.ensureVisible(find.byKey(Key(visible)));
        await tester.pumpAndSettle();
        final rect = tester.getRect(find.byKey(Key(visible)));
        expect(rect.right, lessThanOrEqualTo(360));
        expect(rect.bottom, lessThanOrEqualTo(740));
      }
    });
  }
}
