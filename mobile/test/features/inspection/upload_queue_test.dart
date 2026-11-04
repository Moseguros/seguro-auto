import 'dart:io';
import 'dart:typed_data';

import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:moseguros/features/inspection/inspection_repository.dart';
import 'package:moseguros/features/inspection/inspection_upload_queue.dart';

import '../../support/mock_api.dart';
import '../../support/proposals.dart';

const _photosPath = '$proposalPath/inspection/photos';
const _submitPath = '$proposalPath/inspection/submit';

String? _field(RequestOptions request, String name) {
  final data = request.data;
  if (data is! FormData) return null;
  return data.fields.where((f) => f.key == name).firstOrNull?.value;
}

void main() {
  late AppDatabase db;
  late Directory files;
  late MockApi api;
  late DateTime now;
  late bool online;
  late InspectionRepository repository;
  late InspectionUploadQueue queue;
  var ids = 0;

  setUp(() {
    db = AppDatabase.memory();
    files = tempFilesDirectory();
    api = MockApi();
    now = DateTime.utc(2027, 1, 10, 9);
    online = true;
    ids = 0;
    repository = InspectionRepository(
      db: db,
      filesDirectory: () async => files,
      now: () => now,
      newId: () => 'client-${++ids}',
    );
    queue = InspectionUploadQueue(
      db: db,
      api: () => api.client.proposal,
      isOnline: () async => online,
      now: () => now,
    );
  });

  tearDown(() async {
    await db.close();
    if (files.existsSync()) files.deleteSync(recursive: true);
  });

  Future<InspectionUpload> enqueue(String slot, {DateTime? takenAt}) =>
      repository.enqueuePhoto(
        proposalId: proposalId,
        slot: slot,
        bytes: Uint8List.fromList([1, 2, 3]),
        takenAt: takenAt ?? now,
      );

  Future<InspectionUpload> row(String clientPhotoId) => (db.select(
    db.inspectionUploads,
  )..where((t) => t.clientPhotoId.equals(clientPhotoId))).getSingle();

  test('as respostas simuladas desserializam no cliente gerado', () async {
    api.on('GET', proposalPath, 200, ProposalFixtures.proposal());
    final proposal = (await api.client.proposal.getProposal(id: proposalId))
        .data!;
    expect(proposal.inspection.required_, isTrue);
    expect(proposal.inspection.missingSlots, hasLength(6));
  });

  test('sucesso: DONE, remotePhotoId e ficheiro local apagado', () async {
    final upload = await enqueue('FRONT');
    expect(File(upload.filePath).existsSync(), isTrue);
    api.on(
      'POST',
      _photosPath,
      201,
      ProposalFixtures.photoResponse('FRONT', 'remote-1'),
    );

    final result = await queue.runOnce();

    expect((await row(upload.clientPhotoId)).lastErrorCode, isNull);
    expect(result.uploaded, 1);
    final saved = await row(upload.clientPhotoId);
    expect(saved.status, UploadStatus.done);
    expect(saved.remotePhotoId, 'remote-1');
    expect(File(upload.filePath).existsSync(), isFalse);
    final request = api.calls('POST', _photosPath).single;
    expect(_field(request, 'clientPhotoId'), upload.clientPhotoId);
    expect(_field(request, 'slot'), 'FRONT');
    expect(_field(request, 'source'), 'CAMERA');
  });

  test('503: volta a QUEUED com +30 s e depois +1 min; o mesmo clientPhotoId',
      () async {
    final upload = await enqueue('REAR');
    api.on('POST', _photosPath, 503, MockApi.problem(503, 'INTERNAL_ERROR'));

    await queue.runOnce();
    var saved = await row(upload.clientPhotoId);
    expect(saved.status, UploadStatus.queued);
    expect(saved.attempts, 1);
    expect(
      saved.nextAttemptAt.toUtc(),
      now.add(const Duration(seconds: 30)),
    );

    // Antes da hora não tenta.
    await queue.runOnce();
    expect(api.calls('POST', _photosPath), hasLength(1));

    now = now.add(const Duration(seconds: 30));
    await queue.runOnce();
    saved = await row(upload.clientPhotoId);
    expect(saved.attempts, 2);
    expect(saved.nextAttemptAt.toUtc(), now.add(const Duration(minutes: 1)));

    // Repetir o envio leva o mesmo clientPhotoId (idempotência do S4-03).
    now = now.add(const Duration(minutes: 1));
    api.on(
      'POST',
      _photosPath,
      200,
      ProposalFixtures.photoResponse('REAR', 'remote-2'),
    );
    await queue.runOnce();
    saved = await row(upload.clientPhotoId);
    expect(saved.status, UploadStatus.done);
    final sent = api.calls('POST', _photosPath);
    expect(sent, hasLength(3));
    expect(
      sent.map((r) => _field(r, 'clientPhotoId')).toSet(),
      {upload.clientPhotoId},
    );
  });

  test('espera: 30 s, 1, 2, 5, 10 e depois 15 min', () {
    expect(
      [for (var i = 1; i <= 8; i++) retryDelay(i).inSeconds],
      [30, 60, 120, 300, 600, 900, 900, 900],
    );
  });

  test('INSPECTION_NOT_EDITABLE: FAILED com o código', () async {
    final upload = await enqueue('LEFT');
    api.on(
      'POST',
      _photosPath,
      409,
      MockApi.problem(409, 'INSPECTION_NOT_EDITABLE'),
    );

    final result = await queue.runOnce();

    expect(result.failed, 1);
    final saved = await row(upload.clientPhotoId);
    expect(saved.status, UploadStatus.failed);
    expect(saved.lastErrorCode, 'INSPECTION_NOT_EDITABLE');
    // O ficheiro fica para "Tirar de novo" / "Descartar".
    expect(File(upload.filePath).existsSync(), isTrue);
  });

  test('erro de campo (400): guarda o código do campo', () async {
    final upload = await enqueue('RIGHT');
    api.on(
      'POST',
      _photosPath,
      400,
      MockApi.problem(
        400,
        'VALIDATION_ERROR',
        errors: [
          {'field': 'takenAt', 'code': 'PHOTO_IN_FUTURE'},
        ],
      ),
    );

    await queue.runOnce();

    expect((await row(upload.clientPhotoId)).lastErrorCode, 'PHOTO_IN_FUTURE');
  });

  test('fotografia com 8 dias: FAILED (PHOTO_TOO_OLD) sem pedido', () async {
    final upload = await enqueue(
      'ODOMETER',
      takenAt: now.subtract(const Duration(days: 8)),
    );

    await queue.runOnce();

    final saved = await row(upload.clientPhotoId);
    expect(saved.status, UploadStatus.failed);
    expect(saved.lastErrorCode, 'PHOTO_TOO_OLD');
    expect(api.requests, isEmpty);
  });

  test('sem rede não tenta', () async {
    await enqueue('CHASSIS');
    online = false;

    final result = await queue.runOnce();

    expect(result.offline, isTrue);
    expect(api.requests, isEmpty);
  });

  test('envia pela ordem de entrada', () async {
    for (final slot in fixedSlots) {
      await enqueue(slot);
      now = now.add(const Duration(seconds: 1));
    }
    api.on(
      'POST',
      _photosPath,
      201,
      (RequestOptions r) =>
          ProposalFixtures.photoResponse(_field(r, 'slot')!, 'r-${r.hashCode}'),
    );

    await queue.runOnce();

    expect(
      api.calls('POST', _photosPath).map((r) => _field(r, 'slot')),
      fixedSlots,
    );
  });

  test(
    'submissão: só depois de todas as fotografias DONE e com o version lido antes',
    () async {
      await enqueue('FRONT');
      await repository.queueSubmission(proposalId, 84500);
      api
        ..on('POST', _photosPath, 503, MockApi.problem(503, 'INTERNAL_ERROR'))
        ..on('GET', proposalPath, 200, ProposalFixtures.proposal(version: 7))
        ..on(
          'POST',
          _submitPath,
          200,
          ProposalFixtures.proposal(version: 8, inspectionStatus: 'SUBMITTED'),
        );

      await queue.runOnce();
      expect(api.calls('POST', _submitPath), isEmpty);

      now = now.add(const Duration(seconds: 30));
      api.on(
        'POST',
        _photosPath,
        201,
        ProposalFixtures.photoResponse('FRONT', 'remote-1'),
      );
      final result = await queue.runOnce();

      expect(result.submitted, 1);
      final order = [
        for (final r in api.requests) '${r.method} ${r.path}',
      ];
      expect(order.indexOf('GET $proposalPath'), greaterThan(
        order.lastIndexOf('POST $_photosPath'),
      ));
      final submit = api.calls('POST', _submitPath).single;
      expect(submit.data, {'odometerKm': 84500, 'version': 7});
      final submission = await (db.select(
        db.inspectionSubmissions,
      )).getSingle();
      expect(submission.status, SubmissionStatus.done);
    },
  );

  test('submissão com uma fotografia FAILED não segue', () async {
    await enqueue('FRONT', takenAt: now.subtract(const Duration(days: 8)));
    await repository.queueSubmission(proposalId, 1000);

    await queue.runOnce();

    expect(api.requests, isEmpty);
  });

  test('INSPECTION_PHOTOS_MISSING: a submissão fica FAILED', () async {
    await repository.queueSubmission(proposalId, 1000);
    api
      ..on('GET', proposalPath, 200, ProposalFixtures.proposal(version: 3))
      ..on(
        'POST',
        _submitPath,
        409,
        MockApi.problem(409, 'INSPECTION_PHOTOS_MISSING'),
      );

    await queue.runOnce();

    final submission = await (db.select(db.inspectionSubmissions)).getSingle();
    expect(submission.status, SubmissionStatus.failed);
    expect(submission.lastErrorCode, 'INSPECTION_PHOTOS_MISSING');
  });

  test('uma fotografia nova do mesmo ângulo substitui a que está na fila',
      () async {
    final first = await enqueue('FRONT');
    final second = await enqueue('FRONT');
    await enqueue('EXTRA');
    await enqueue('EXTRA');

    final rows = await db.select(db.inspectionUploads).get();
    expect(
      rows.map((r) => r.clientPhotoId),
      containsAll([second.clientPhotoId]),
    );
    expect(rows.where((r) => r.slot == 'FRONT'), hasLength(1));
    expect(rows.where((r) => r.slot == 'EXTRA'), hasLength(2));
    expect(File(first.filePath).existsSync(), isFalse);
    expect(await repository.pendingCount(proposalId: proposalId), 3);
  });
}
