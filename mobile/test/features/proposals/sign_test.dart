import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/features/inspection/inspection_repository.dart';
import 'package:moseguros/features/proposals/steps/review_step.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';

Map<String, Object?> _ready({int version = 5}) => ProposalFixtures.proposal(
  version: version,
  checklistDeclarations: 'DONE',
  checklistSuitability: 'DONE',
  checklistInspection: 'DONE',
  kycState: 'PENDING',
  declarations: ProposalFixtures.declarationsAllNo(),
  suitability: ProposalFixtures.suitabilityResult(),
  photos: [for (final slot in fixedSlots) ProposalFixtures.photo(slot)],
  inspectionStatus: 'SUBMITTED',
);

Finder _reviewList() => find
    .descendant(of: find.byType(ReviewStep), matching: find.byType(Scrollable))
    .first;

Future<void> _insertQueued(AppDatabase db) => db
    .into(db.inspectionUploads)
    .insert(
      InspectionUploadsCompanion.insert(
        clientPhotoId: 'local-extra',
        proposalId: proposalId,
        slot: 'EXTRA',
        filePath: 'missing.jpg',
        takenAt: proposalToday,
        status: UploadStatus.queued,
        nextAttemptAt: proposalToday,
        createdAt: proposalToday,
      ),
    );

void main() {
  testWidgets('assinatura: código → confirmProposalSignature → submitProposal', (
    tester,
  ) async {
    final api = MockApi()
      ..on('GET', proposalPath, 200, _ready())
      ..on(
        'POST',
        '$proposalPath/sign/request',
        200,
        ProposalFixtures.signatureRequest(),
      )
      ..on(
        'POST',
        '$proposalPath/sign/confirm',
        200,
        {..._ready(version: 6), 'signature': ProposalFixtures.signature()},
      )
      ..on('POST', '$proposalPath/submit', 200, {
        ..._ready(version: 7),
        'status': 'UNDER_REVIEW',
        'submittedAt': '2027-01-10T08:32:00Z',
        'signature': ProposalFixtures.signature(),
        'decision': {'insurerDueAt': '2027-01-25T08:32:00Z'},
      });
    await openAt(tester, api, '/proposals/$proposalId?step=review');
    expect(find.text('Passo 6 de 6 · Resumo'), findsOneWidget);

    final sign = find.byKey(const Key('review-sign'));
    await tester.scrollUntilVisible(sign, 200, scrollable: _reviewList());
    expect(tester.widget<FilledButton>(sign).onPressed, isNull);
    await tester.tap(find.byKey(const Key('review-documents-acknowledged')));
    await tester.pumpAndSettle();
    expect(find.text('Assinar e enviar à seguradora'), findsOneWidget);
    await tester.tap(sign);
    await settle(tester);

    expect(
      api.calls('POST', '$proposalPath/sign/request').single.data,
      {'version': 5, 'documentsAcknowledged': true},
    );
    expect(
      find.text('Enviámos um código por SMS para +258*****4567.'),
      findsOneWidget,
    );
    expect(find.text('Ver a proposta (PDF)'), findsOneWidget);

    await tester.enterText(find.byType(EditableText).last, '123456');
    await settle(tester);

    expect(api.calls('POST', '$proposalPath/sign/confirm').single.data, {
      'challengeId': 'sign-challenge-1',
      'code': '123456',
    });
    expect(api.calls('POST', '$proposalPath/submit').single.data, {
      'version': 6,
    });
    expect(find.text('Em análise'), findsOneWidget);
    expect(
      find.text(
        'A Seguradora Demo está a analisar a sua proposta. Tem até 25/01/2027 '
        'para responder.',
      ),
      findsOneWidget,
    );
  });

  testWidgets('com fotografias na fila o botão fica desactivado', (
    tester,
  ) async {
    final db = AppDatabase.memory();
    addTearDown(db.close);
    await _insertQueued(db);
    final api = MockApi()..on('GET', proposalPath, 200, _ready());
    await openAt(
      tester,
      api,
      '/proposals/$proposalId?step=review',
      database: db,
      connectivity: FakeConnectivity(online: false),
    );
    final sign = find.byKey(const Key('review-sign'));
    await tester.scrollUntilVisible(sign, 200, scrollable: _reviewList());
    await tester.tap(find.byKey(const Key('review-documents-acknowledged')));
    await tester.pumpAndSettle();

    expect(tester.widget<FilledButton>(sign).onPressed, isNull);
    expect(
      find.text('Aguarde o envio das fotografias da vistoria.'),
      findsOneWidget,
    );
  });

  testWidgets('sem rede: errorNoConnection antes de pedir o código', (
    tester,
  ) async {
    final api = MockApi()..on('GET', proposalPath, 200, _ready());
    await openAt(
      tester,
      api,
      '/proposals/$proposalId?step=review',
      connectivity: FakeConnectivity(online: false),
    );
    final sign = find.byKey(const Key('review-sign'));
    await tester.scrollUntilVisible(sign, 200, scrollable: _reviewList());
    await tester.tap(find.byKey(const Key('review-documents-acknowledged')));
    await tester.pumpAndSettle();
    await tester.tap(sign);
    await settle(tester);

    expect(find.text('Sem ligação à internet.'), findsOneWidget);
    expect(api.calls('POST', '$proposalPath/sign/request'), isEmpty);
  });

  testWidgets('logout com fotografias na fila pede confirmação e apaga-as', (
    tester,
  ) async {
    final db = AppDatabase.memory();
    addTearDown(db.close);
    await _insertQueued(db);
    final api = MockApi()
      ..on('POST', '/api/v1/auth/logout', 204);
    await openAt(
      tester,
      api,
      '/profile',
      database: db,
      connectivity: FakeConnectivity(online: false),
    );
    final logout = find.byKey(const Key('logout'));
    await tester.scrollUntilVisible(
      logout,
      200,
      scrollable: find.byType(Scrollable).first,
    );
    await tester.tap(logout);
    await settle(tester);

    expect(
      find.text('Tem 1 fotografia por enviar. Se terminar a sessão, perde-a.'),
      findsOneWidget,
    );
    expect(find.text('Terminar na mesma'), findsOneWidget);
    await tester.tap(find.text('Cancelar'));
    await settle(tester);
    expect(await db.select(db.inspectionUploads).get(), hasLength(1));

    await tester.tap(logout);
    await settle(tester);
    await tester.tap(find.byKey(const Key('confirm-logout')));
    await pumpUntil(tester, () => false, tries: 5);

    expect(await db.select(db.inspectionUploads).get(), isEmpty);
    expect(find.text('Entrar na área do cliente'), findsOneWidget);
  });
}
