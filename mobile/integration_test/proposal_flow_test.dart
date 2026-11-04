import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/features/inspection/inspection_screen.dart';
import 'package:moseguros/features/proposals/proposal_screen.dart';

import '../test/support/mock_api.dart';
import '../test/support/proposals.dart';
import '../test/support/quotes.dart';
import '../test/support/vehicles.dart';

/// Fluxo principal do S4-09 com a API simulada: cotação → "Adquirir apólice" →
/// passos → vistoria com a câmara falsa → assinatura → "Em análise".
///
/// Sem emulador: `flutter test integration_test/proposal_flow_test.dart -d flutter-tester`.
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

/// Toca num elemento da lista de um passo (constrói-a com scroll até ele).
Future<void> tapInStep(WidgetTester tester, Finder finder) async {
  await tester.scrollUntilVisible(
    finder,
    200,
    scrollable: find
        .descendant(
          of: find.byType(PageView),
          matching: find.byType(Scrollable),
        )
        .at(1),
  );
  await tapVisible(tester, finder);
}

/// Faz scroll na vistoria até [finder] estar construído.
Future<void> scrollToInspection(WidgetTester tester, Finder finder) =>
    tester.scrollUntilVisible(
      finder,
      200,
      scrollable: find
          .descendant(
            of: find.byType(InspectionScreen),
            matching: find.byType(Scrollable),
          )
          .first,
    );

String? _field(RequestOptions request, String name) {
  final data = request.data;
  if (data is! FormData) return null;
  return data.fields.where((f) => f.key == name).firstOrNull?.value;
}

/// Estado da proposta no "servidor" simulado.
class _Server {
  int version = 1;
  String declarations = 'MISSING';
  String suitability = 'MISSING';
  String inspection = 'MISSING';
  String inspectionStatus = 'PENDING';
  String status = 'DRAFT';
  bool signed = false;
  final photos = <Map<String, Object?>>[];

  Map<String, Object?> proposal() => {
    ...ProposalFixtures.proposal(
      version: version,
      status: status,
      checklistDeclarations: declarations,
      checklistSuitability: suitability,
      checklistInspection: inspection,
      inspectionStatus: inspectionStatus,
      photos: photos,
      declarations: declarations == 'DONE'
          ? ProposalFixtures.declarationsAllNo()
          : null,
      suitability: suitability == 'DONE'
          ? ProposalFixtures.suitabilityResult()
          : null,
      signature: signed ? ProposalFixtures.signature() : null,
      submittedAt: status == 'UNDER_REVIEW' ? '2027-01-10T10:00:00Z' : null,
      decision: status == 'UNDER_REVIEW'
          ? {'insurerDueAt': '2027-01-25T10:00:00Z'}
          : null,
    ),
  };

  Map<String, Object?> bump() {
    version++;
    return proposal();
  }
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'cotação → "Adquirir apólice" → passos → vistoria → assinar → "Em análise"',
    (tester) async {
      final server = _Server();
      final device = FakeDevice();
      final api = MockApi();
      stubQuoteBasics(api);
      api
        ..on('GET', '/api/v1/quotes/$requestId', 200, QuoteFixtures.request())
        ..on('POST', '/api/v1/proposals', 201, (_) => server.proposal())
        ..on('GET', proposalPath, 200, (_) => server.proposal())
        ..on('POST', '$proposalPath/declarations', 200, (_) {
          server.declarations = 'DONE';
          return server.bump();
        })
        ..on('POST', '$proposalPath/suitability', 200, (_) {
          server.suitability = 'DONE';
          return server.bump();
        })
        ..on('POST', '$proposalPath/inspection/photos', 201, (request) {
          final slot = _field(request, 'slot')!;
          final photo = ProposalFixtures.photoResponse(slot, 'remote-$slot');
          server.photos.add(photo);
          server.version++;
          return photo;
        })
        ..on('POST', '$proposalPath/inspection/submit', 200, (_) {
          server
            ..inspection = 'DONE'
            ..inspectionStatus = 'SUBMITTED';
          return server.bump();
        })
        ..on(
          'POST',
          '$proposalPath/sign/request',
          200,
          ProposalFixtures.signatureRequest(),
        )
        ..on('POST', '$proposalPath/sign/confirm', 200, (_) {
          server.signed = true;
          return server.bump();
        })
        ..on('POST', '$proposalPath/submit', 200, (_) {
          server.status = 'UNDER_REVIEW';
          return server.bump();
        });

      final (_, router) = await openAt(
        tester,
        api,
        '/quote/results/$requestId/quotes/$quoteDemoId',
        overrides: device.overrides,
      );
      expect(router, isNotNull);

      // Detalhe da cotação → "Adquirir apólice".
      await tester.scrollUntilVisible(
        find.text('Adquirir apólice'),
        200,
        scrollable: find.byType(Scrollable).first,
      );
      await tapVisible(tester, find.text('Adquirir apólice'));
      await pumpUntilFound(tester, find.byType(ProposalWizard));
      expect(
        api.calls('POST', '/api/v1/proposals').single.data,
        {'quoteId': quoteDemoId, 'channel': 'APP'},
      );
      expect(find.text('Passo 2 de 6 · Declarações'), findsOneWidget);

      // Declarações: todas "Não" e a veracidade.
      for (final code in [
        'CLAIMS_LAST_3_YEARS',
        'PREVIOUS_REFUSAL',
        'USAGE_DIFFERENT',
        'VEHICLE_MODIFIED',
        'NOVICE_DRIVERS',
      ]) {
        await tapInStep(tester, find.byKey(Key('declaration-$code-no')));
      }
      await tapInStep(tester, find.byKey(const Key('declaration-truthfulness')));
      await tapVisible(tester, find.byKey(const Key('proposal-continue')));

      // Adequação sem avisos.
      await pumpUntilFound(tester, find.text('Passo 3 de 6 · Adequação'));
      for (final entry in {
        'SUIT_NEED': 'OWN_DAMAGE',
        'SUIT_FINANCED': 'NO',
        'SUIT_CAN_AFFORD_REPAIR': 'YES',
        'SUIT_CROSS_BORDER': 'NO',
        'SUIT_DEDUCTIBLE': 'YES',
      }.entries) {
        await tapInStep(
          tester,
          find.byKey(Key('suitability-${entry.key}-${entry.value}')),
        );
      }
      await tapVisible(tester, find.byKey(const Key('proposal-continue')));

      // Vistoria com a câmara falsa: os 6 ângulos seguidos.
      await pumpUntilFound(tester, find.text('Passo 4 de 6 · Vistoria'));
      await tapVisible(tester, find.byKey(const Key('inspection-step-open')));
      await scrollToInspection(tester, find.byKey(const Key('inspection-start')));
      await tapVisible(tester, find.byKey(const Key('inspection-start')));
      for (var i = 0; i < 6; i++) {
        await tapVisible(tester, find.byKey(const Key('camera-shutter')));
        await tapVisible(tester, find.byKey(const Key('photo-use')));
        await pumpUntilFound(
          tester,
          i < 5
              ? find.byKey(const Key('camera-shutter'))
              : find.byKey(const Key('inspection-submit')),
        );
      }
      expect(device.cameras, hasLength(6));
      await scrollToInspection(
        tester,
        find.byKey(const Key('inspection-odometer')),
      );
      await tester.enterText(
        find.byKey(const Key('inspection-odometer')),
        '84500',
      );
      await tester.pumpAndSettle();
      await tapVisible(tester, find.byKey(const Key('inspection-submit')));
      for (var i = 0; i < 100 && server.inspection != 'DONE'; i++) {
        await tester.pump(const Duration(milliseconds: 100));
      }
      expect(api.calls('POST', '$proposalPath/inspection/photos'), hasLength(6));
      expect(
        api.calls('POST', '$proposalPath/inspection/submit').single.data,
        containsPair('odometerKm', 84500),
      );

      // De volta aos passos: identidade "Agora não" e resumo.
      // Botão "voltar" do sistema.
      await tester.binding.handlePopRoute();
      await tester.pumpAndSettle();
      await tapVisible(tester, find.byKey(const Key('proposal-continue')));
      await pumpUntilFound(tester, find.text('Passo 5 de 6 · Identidade'));
      await tapInStep(tester, find.byKey(const Key('kyc-not-now')));
      await pumpUntilFound(tester, find.text('Passo 6 de 6 · Resumo'));

      await tapInStep(
        tester,
        find.byKey(const Key('review-documents-acknowledged')),
      );
      await tapInStep(tester, find.byKey(const Key('review-sign')));
      await pumpUntilFound(
        tester,
        find.text('Enviámos um código por SMS para +258*****4567.'),
      );
      await tester.enterText(find.byType(EditableText).last, '123456');
      await pumpUntilFound(tester, find.text('Em análise'));

      expect(api.calls('POST', '$proposalPath/submit'), hasLength(1));
      expect(find.text('Em análise'), findsOneWidget);
      expect(tester.takeException(), isNull);
    },
  );
}
