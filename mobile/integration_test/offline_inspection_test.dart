import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/features/inspection/inspection_repository.dart';
import 'package:moseguros/features/inspection/inspection_screen.dart';
import 'package:path/path.dart' as p;

import '../test/support/mock_api.dart';
import '../test/support/proposals.dart';
import '../test/support/vehicles.dart';

/// Vistoria sem rede (S4-09 §5): tiram-se as 6 fotografias e envia-se a vistoria
/// sem ligação; a app reinicia (novo `ProviderContainer` sobre a mesma base em
/// ficheiro); a rede volta e as 6 fotografias e a submissão seguem por esta ordem.
///
/// Sem emulador: `flutter test integration_test/offline_inspection_test.dart -d flutter-tester`.
Future<void> pumpUntil(
  WidgetTester tester,
  bool Function() done, {
  int tries = 150,
}) async {
  for (var i = 0; i < tries && !done(); i++) {
    await tester.pump(const Duration(milliseconds: 100));
  }
  await tester.pumpAndSettle();
}

Future<void> tapVisible(WidgetTester tester, Finder finder) async {
  await pumpUntil(tester, () => finder.evaluate().isNotEmpty);
  await tester.ensureVisible(finder);
  await tester.pumpAndSettle();
  await tester.tap(finder);
  await tester.pumpAndSettle();
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

String? _slot(RequestOptions request) {
  final data = request.data;
  if (data is! FormData) return null;
  return data.fields.where((f) => f.key == 'slot').firstOrNull?.value;
}

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'sem rede: 6 fotografias e a vistoria ficam na fila; depois de reiniciar, '
    'a rede volta e seguem por ordem',
    (tester) async {
      final root = Directory.systemTemp.createTempSync('moseguros-offline-');
      addTearDown(() {
        try {
          if (root.existsSync()) root.deleteSync(recursive: true);
        } on FileSystemException {
          // A base ainda aberta depois de uma falha: fica no temporário.
        }
      });
      final dbFile = File(p.join(root.path, appDatabaseFileName));
      final photos = <Map<String, Object?>>[];

      void stub(MockApi api) => api
        ..on(
          'GET',
          proposalPath,
          200,
          (_) => ProposalFixtures.proposal(version: 3, photos: photos),
        )
        ..on('POST', '$proposalPath/inspection/photos', 201, (request) {
          final photo = ProposalFixtures.photoResponse(
            _slot(request)!,
            'remote-${_slot(request)}',
          );
          photos.add(photo);
          return photo;
        })
        ..on(
          'POST',
          '$proposalPath/inspection/submit',
          200,
          (_) => ProposalFixtures.proposal(
            version: 4,
            photos: photos,
            inspectionStatus: 'SUBMITTED',
            checklistInspection: 'DONE',
          ),
        );

      // 1.ª abertura, sem rede.
      final firstDb = AppDatabase.file(dbFile);
      final firstApi = MockApi();
      stub(firstApi);
      final offline = FakeConnectivity(online: false);
      final device = FakeDevice();
      final (firstContainer, _) = await openAt(
        tester,
        firstApi,
        '/proposals/$proposalId/inspection',
        database: firstDb,
        connectivity: offline,
        filesDirectory: root,
        overrides: device.overrides,
      );
      expect(
        find.text(
          'Sem ligação. As fotografias ficam guardadas no telemóvel e são '
          'enviadas quando a ligação voltar.',
        ),
        findsOneWidget,
      );
      await scrollToInspection(tester, find.byKey(const Key('inspection-start')));
      await tapVisible(tester, find.byKey(const Key('inspection-start')));
      for (var i = 0; i < 6; i++) {
        await tapVisible(tester, find.byKey(const Key('camera-shutter')));
        await tapVisible(tester, find.byKey(const Key('photo-use')));
        await pumpUntil(
          tester,
          () => (i < 5
                  ? find.byKey(const Key('camera-shutter'))
                  : find.byKey(const Key('inspection-submit')))
              .evaluate()
              .isNotEmpty,
        );
      }
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
      expect(
        find.text('A vistoria será enviada quando houver ligação.'),
        findsOneWidget,
      );
      expect(
        firstApi.requests.where(
          (r) => r.method == 'POST' && r.path.startsWith(proposalPath),
        ),
        isEmpty,
      );
      expect(
        await firstDb.select(firstDb.inspectionUploads).get(),
        hasLength(6),
      );

      // A app fecha: a árvore sai, o container e a base fecham.
      await tester.pumpWidget(const SizedBox());
      firstContainer.dispose();
      await firstDb.close();

      // 2.ª abertura sobre a mesma base, ainda sem rede.
      final secondDb = AppDatabase.file(dbFile);
      addTearDown(secondDb.close);
      final secondApi = MockApi();
      stub(secondApi);
      final network = FakeConnectivity(online: false);
      final (secondContainer, _) = await openAt(
        tester,
        secondApi,
        '/proposals/$proposalId/inspection',
        database: secondDb,
        connectivity: network,
        filesDirectory: root,
        overrides: FakeDevice().overrides,
      );
      expect(secondContainer, isNot(same(firstContainer)));
      expect(
        await secondDb.select(secondDb.inspectionUploads).get(),
        hasLength(6),
      );

      // A rede volta.
      network.online = true;
      await pumpUntil(
        tester,
        () => secondApi.calls('POST', '$proposalPath/inspection/submit').isNotEmpty,
      );

      final order = [
        for (final r in secondApi.requests)
          if (r.method == 'POST' && r.path.startsWith(proposalPath))
            '${r.path}:${_slot(r) ?? ''}',
      ];
      expect(order, [
        for (final slot in fixedSlots) '$proposalPath/inspection/photos:$slot',
        '$proposalPath/inspection/submit:',
      ]);
      expect(
        secondApi.calls('POST', '$proposalPath/inspection/submit').single.data,
        {'odometerKm': 84500, 'version': 3},
      );
      final rows = await secondDb.select(secondDb.inspectionUploads).get();
      expect(rows.every((r) => r.status == UploadStatus.done), isTrue);
      expect(
        Directory(p.join(root.path, 'inspections', proposalId))
            .listSync()
            .whereType<File>(),
        isEmpty,
      );
      final submission = await secondDb
          .select(secondDb.inspectionSubmissions)
          .getSingle();
      expect(submission.status, SubmissionStatus.done);
      expect(secondContainer.read(appDatabaseProvider), same(secondDb));
    },
  );
}
