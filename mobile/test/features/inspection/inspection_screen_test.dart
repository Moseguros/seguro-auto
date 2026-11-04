import 'package:drift/drift.dart' show Value;
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/features/inspection/inspection_repository.dart';
import 'package:moseguros/features/inspection/inspection_screen.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';
import '../../support/vehicles.dart';

const _inspectionPath = '/proposals/$proposalId/inspection';

Future<void> _insert(
  AppDatabase db,
  String slot,
  String status, {
  String? error,
}) => db
    .into(db.inspectionUploads)
    .insert(
      InspectionUploadsCompanion.insert(
        clientPhotoId: 'local-$slot',
        proposalId: proposalId,
        slot: slot,
        filePath: 'missing/$slot.jpg',
        takenAt: proposalToday,
        status: status,
        nextAttemptAt: proposalToday,
        createdAt: proposalToday,
        lastErrorCode: Value(error),
      ),
    );

Finder _state(String slot) => find.byKey(Key('slot-$slot-state'));

Future<void> _scrollTo(WidgetTester tester, Finder finder) =>
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

void main() {
  late AppDatabase db;

  setUp(() => db = AppDatabase.memory());
  tearDown(() => db.close());

  testWidgets('estado por ângulo: "Enviada", "Na fila", "Erro", "Por tirar"', (
    tester,
  ) async {
    await _insert(db, 'LEFT', UploadStatus.queued);
    await _insert(db, 'RIGHT', UploadStatus.done);
    await _insert(
      db,
      'ODOMETER',
      UploadStatus.failed,
      error: 'INSPECTION_NOT_EDITABLE',
    );
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(
          photos: [
            ProposalFixtures.photo('FRONT'),
            ProposalFixtures.photo('REAR'),
          ],
        ),
      );
    await openAt(
      tester,
      api,
      _inspectionPath,
      database: db,
      connectivity: FakeConnectivity(online: false),
    );

    expect(
      find.text(
        'Sem ligação. As fotografias ficam guardadas no telemóvel e são '
        'enviadas quando a ligação voltar.',
      ),
      findsOneWidget,
    );
    expect(tester.widget<Text>(_state('FRONT')).data, 'Enviada');
    expect(tester.widget<Text>(_state('REAR')).data, 'Enviada');
    expect(tester.widget<Text>(_state('LEFT')).data, 'Na fila');
    expect(tester.widget<Text>(_state('RIGHT')).data, 'Enviada');
    await _scrollTo(tester, _state('CHASSIS'));
    expect(tester.widget<Text>(_state('ODOMETER')).data, 'Erro');
    expect(
      find.text('A vistoria já foi enviada e não pode ser alterada.'),
      findsOneWidget,
    );
    expect(find.byKey(const Key('slot-ODOMETER-retake')), findsOneWidget);
    expect(find.byKey(const Key('slot-ODOMETER-discard')), findsOneWidget);
    expect(tester.widget<Text>(_state('CHASSIS')).data, 'Por tirar');

    // "Descartar" tira a fotografia da fila.
    await tester.tap(find.byKey(const Key('slot-ODOMETER-discard')));
    await settle(tester);
    expect(tester.widget<Text>(_state('ODOMETER')).data, 'Por tirar');
  });

  testWidgets(
    '"Enviar vistoria" só com os 6 ângulos e a quilometragem; sem rede fica na fila',
    (tester) async {
      await _insert(db, 'CHASSIS', UploadStatus.queued);
      final api = MockApi()
        ..on(
          'GET',
          proposalPath,
          200,
          ProposalFixtures.proposal(
            photos: [
              for (final slot in fixedSlots.take(5))
                ProposalFixtures.photo(slot),
            ],
          ),
        );
      await openAt(
        tester,
        api,
        _inspectionPath,
        database: db,
        connectivity: FakeConnectivity(online: false),
      );
      final submit = find.byKey(const Key('inspection-submit'));
      await _scrollTo(tester, submit);
      expect(tester.widget<FilledButton>(submit).onPressed, isNull);

      await tester.enterText(
        find.byKey(const Key('inspection-odometer')),
        '84500',
      );
      await tester.pumpAndSettle();
      expect(tester.widget<FilledButton>(submit).onPressed, isNotNull);

      await tester.tap(submit);
      await settle(tester);

      final submission = await db.select(db.inspectionSubmissions).getSingle();
      expect(submission.odometerKm, 84500);
      expect(submission.status, SubmissionStatus.queued);
      expect(
        find.text('A vistoria será enviada quando houver ligação.'),
        findsOneWidget,
      );
      expect(api.calls('POST', '$proposalPath/inspection/submit'), isEmpty);
    },
  );

  testWidgets('falta um ângulo: "Enviar vistoria" desactivado', (tester) async {
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(
          photos: [
            for (final slot in fixedSlots.take(5)) ProposalFixtures.photo(slot),
          ],
        ),
      );
    await openAt(tester, api, _inspectionPath, database: db);
    final submit = find.byKey(const Key('inspection-submit'));
    await _scrollTo(tester, submit);
    await tester.enterText(find.byKey(const Key('inspection-odometer')), '10');
    await tester.pumpAndSettle();
    expect(tester.widget<FilledButton>(submit).onPressed, isNull);
  });

  testWidgets(
    'câmara guiada: localização pedida uma vez, fotografia sem EXIF na fila',
    (tester) async {
      final device = FakeDevice();
      final location = FakeLocation((latitude: -25.9692, longitude: 32.5732));
      final api = MockApi()
        ..on('GET', proposalPath, 200, ProposalFixtures.proposal());
      await openAt(
        tester,
        api,
        _inspectionPath,
        database: db,
        connectivity: FakeConnectivity(online: false),
        location: location,
        locationAsked: false,
        overrides: device.overrides,
      );

      await tester.tap(find.byKey(const Key('slot-FRONT')));
      await settle(tester);
      expect(
        find.text(
          'A localização ajuda a seguradora a confirmar onde as fotografias '
          'foram tiradas. É opcional.',
        ),
        findsOneWidget,
      );
      await tester.tap(find.byKey(const Key('location-continue')));
      await settle(tester);
      expect(location.permissionRequests, 1);

      expect(find.text('Frente'), findsOneWidget);
      expect(
        find.text('A frente do veículo inteira, com a matrícula legível.'),
        findsOneWidget,
      );
      expect(device.cameras.single.initialized, isTrue);
      await tester.tap(find.byKey(const Key('camera-shutter')));
      await settle(tester);
      expect(find.text('Usar fotografia'), findsOneWidget);
      await tester.tap(find.byKey(const Key('photo-use')));
      // O ficheiro da fila é escrito com I/O real.
      await pumpUntil(
        tester,
        () =>
            find.byKey(const Key('camera-shutter')).evaluate().isEmpty &&
            find.byKey(const Key('photo-use')).evaluate().isEmpty,
      );

      expect(device.compressor.calls.single.quality, 80);
      expect(device.compressor.calls.single.keepExif, isFalse);
      final row = await db.select(db.inspectionUploads).getSingle();
      expect(row.slot, 'FRONT');
      expect(row.status, UploadStatus.queued);
      expect(row.latitude, closeTo(-25.9692, 1e-6));
      expect(row.takenAt.toUtc(), proposalToday);
      expect(tester.widget<Text>(_state('FRONT')).data, 'Na fila');

      // A explicação da localização não volta a aparecer.
      await tester.tap(find.byKey(const Key('slot-REAR')));
      await settle(tester);
      expect(find.byKey(const Key('location-rationale')), findsNothing);
      expect(find.byKey(const Key('camera-shutter')), findsOneWidget);
      await tester.tap(find.byKey(const Key('camera-close')));
      await settle(tester);
    },
  );

  testWidgets('câmara recusada: texto e "Abrir definições"', (tester) async {
    final device = FakeDevice(cameraGranted: false);
    final api = MockApi()
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal());
    await openAt(
      tester,
      api,
      _inspectionPath,
      database: db,
      overrides: device.overrides,
    );
    await tester.tap(find.byKey(const Key('slot-FRONT')));
    await settle(tester);

    expect(
      find.text(
        'Para fazer a vistoria, a MoSeguros precisa de acesso à câmara.',
      ),
      findsOneWidget,
    );
    await tester.tap(find.byKey(const Key('camera-open-settings')));
    expect(device.permission.settingsOpened, 1);
    await tester.tap(find.byKey(const Key('camera-close')));
    await settle(tester);
  });

  testWidgets('vistoria recusada: motivo e ângulos a repetir', (tester) async {
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(
          inspectionStatus: 'REJECTED',
          photos: [for (final slot in fixedSlots) ProposalFixtures.photo(slot)],
          review: {
            'reviewedAt': '2027-01-10T10:00:00Z',
            'rejectionReason': 'A matrícula não se lê.',
            'retakeSlots': ['FRONT'],
          },
        ),
      );
    await openAt(tester, api, _inspectionPath, database: db);

    expect(
      find.text('A seguradora pediu novas fotografias: A matrícula não se lê.'),
      findsOneWidget,
    );
    expect(tester.widget<Text>(_state('FRONT')).data, 'Tirar de novo');
    expect(tester.widget<Text>(_state('REAR')).data, 'Enviada');
  });

  testWidgets('"Retirar" uma fotografia extra já enviada', (tester) async {
    final api = MockApi()
      ..on(
        'GET',
        proposalPath,
        200,
        ProposalFixtures.proposal(
          photos: [ProposalFixtures.photo('EXTRA', id: 'extra-1')],
        ),
      )
      ..on('DELETE', '$proposalPath/inspection/photos/extra-1', 204);
    await openAt(tester, api, _inspectionPath, database: db);
    final remove = find.byKey(const Key('extra-extra-1-remove'));
    await _scrollTo(tester, remove);
    await tester.tap(remove);
    await settle(tester);
    await tester.tap(find.byKey(const Key('inspection-remove-confirm')));
    await settle(tester);

    expect(
      api.calls('DELETE', '$proposalPath/inspection/photos/extra-1'),
      hasLength(1),
    );
    expect(api.calls('GET', proposalPath), hasLength(2));
  });

  test('slotViews e canSubmitInspection', () {
    expect(parseOdometer('84 500'), 84500);
    expect(parseOdometer('2000001'), isNull);
    expect(parseOdometer(''), isNull);
  });
}
