import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/features/inspection/inspection_repository.dart';
import 'package:moseguros/features/proposals/proposals_screen.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';

void main() {
  testWidgets('início: "Propostas em curso", fotografias por enviar e "Ver todas"', (
    tester,
  ) async {
    final db = AppDatabase.memory();
    addTearDown(db.close);
    for (final slot in ['FRONT', 'REAR']) {
      await db
          .into(db.inspectionUploads)
          .insert(
            InspectionUploadsCompanion.insert(
              clientPhotoId: 'local-$slot',
              proposalId: proposalId,
              slot: slot,
              filePath: 'missing.jpg',
              takenAt: proposalToday,
              status: UploadStatus.queued,
              nextAttemptAt: proposalToday,
              createdAt: proposalToday,
            ),
          );
    }
    final api = MockApi();
    await openAt(
      tester,
      api,
      '/',
      database: db,
      connectivity: FakeConnectivity(online: false),
      proposals: [ProposalFixtures.summary()],
    );

    final card = find.byKey(const Key('home-proposals'));
    await tester.scrollUntilVisible(
      card,
      200,
      scrollable: find.byType(Scrollable).first,
    );
    expect(find.text('Propostas em curso'), findsOneWidget);
    expect(find.text('2 fotografias por enviar'), findsOneWidget);
    expect(find.text(proposalNumber), findsOneWidget);
    // O "Simular seguro" continua a ser o único laranja do início.
    expect(find.byType(CtaButton), findsOneWidget);
    final request = api.calls('GET', myProposalsPath).first;
    expect(request.queryParameters['size'], 3);

    final all = find.byKey(const Key('home-proposals-all'));
    await tester.scrollUntilVisible(
      all,
      200,
      scrollable: find.byType(Scrollable).first,
    );
    await tester.ensureVisible(all);
    await tester.pumpAndSettle();
    await tester.tap(all);
    await settle(tester);
    expect(find.byType(ProposalsScreen), findsOneWidget);
    expect(find.text('Continuar'), findsOneWidget);
  });

  testWidgets('lista vazia: "Simular seguro" laranja', (tester) async {
    await openAt(tester, MockApi(), '/proposals');

    expect(find.text('Ainda não tem propostas.'), findsOneWidget);
    expect(
      tester.widget<CtaButton>(find.byType(CtaButton)).label,
      'Simular seguro',
    );
  });
}
