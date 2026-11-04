import 'dart:io';

import 'package:dio/dio.dart';
import 'package:drift/drift.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/db/app_database.dart';
import '../../core/errors/problem.dart';
import 'inspection_repository.dart';
import 'inspection_slots.dart';

/// Espera antes da tentativa seguinte, depois de [attempts] falhas temporárias:
/// 30 s, 1 min, 2 min, 5 min, 10 min e depois 15 min.
Duration retryDelay(int attempts) {
  const delays = [
    Duration(seconds: 30),
    Duration(minutes: 1),
    Duration(minutes: 2),
    Duration(minutes: 5),
    Duration(minutes: 10),
    Duration(minutes: 15),
  ];
  if (attempts <= 0) return delays.first;
  return delays[(attempts - 1).clamp(0, delays.length - 1)];
}

/// Falha temporária: sem rede, tempo esgotado, 5xx, 408 ou 429.
bool isTransient(Problem problem) =>
    problem.status == 0 ||
    problem.status == 408 ||
    problem.status == 429 ||
    problem.status >= 500;

/// Código guardado em `lastErrorCode` quando o ficheiro local desapareceu.
const localFileMissing = 'FILE_NOT_FOUND';

/// Resultado de uma passagem pela fila (contagens, para o registo).
class QueueRunResult {
  int uploaded = 0;
  int retried = 0;
  int failed = 0;
  int submitted = 0;

  /// A sessão terminou (401 depois da renovação): o executor pára.
  bool sessionEnded = false;

  /// Sem rede: nada se tentou.
  bool offline = false;

  @override
  String toString() =>
      'uploaded=$uploaded retried=$retried failed=$failed submitted=$submitted'
      '${sessionEnded ? ' sessionEnded' : ''}${offline ? ' offline' : ''}';
}

/// Uma passagem pela fila de envio da vistoria (S4-09 §5 e §5a).
///
/// Envia as fotografias uma a uma pela ordem de entrada (só as que já podem ser
/// tentadas) e depois os pedidos de submissão cujas fotografias já estão todas
/// enviadas. O [UploadQueueRunner] garante que nunca há duas passagens ao mesmo
/// tempo.
class InspectionUploadQueue {
  InspectionUploadQueue({
    required this.db,
    required ProposalApi Function() api,
    required Future<bool> Function() isOnline,
    DateTime Function()? now,
  }) : _api = api,
       _isOnline = isOnline,
       _now = now ?? DateTime.now;

  final AppDatabase db;
  final ProposalApi Function() _api;
  final Future<bool> Function() _isOnline;
  final DateTime Function() _now;

  DateTime get _utcNow => _now().toUtc();

  $InspectionUploadsTable get _uploads => db.inspectionUploads;
  $InspectionSubmissionsTable get _submissions => db.inspectionSubmissions;

  /// Uma passagem. Nunca lança: os erros ficam nas linhas.
  Future<QueueRunResult> runOnce() async {
    final result = QueueRunResult();
    await _recoverInterrupted();
    await _expireOldPhotos(result);
    if (!await _isOnline()) {
      result.offline = true;
      return result;
    }
    final uploadsStopped = await _uploadPhotos(result);
    if (!uploadsStopped && !result.sessionEnded) {
      await _sendSubmissions(result);
    }
    return result;
  }

  /// Há trabalho por fazer (fotografias ou submissões por enviar).
  Future<bool> hasWork() async {
    final photo = await (db.select(_uploads)
          ..where((t) => t.status.isIn([UploadStatus.queued, UploadStatus.uploading]))
          ..limit(1))
        .getSingleOrNull();
    if (photo != null) return true;
    final submission = await (db.select(_submissions)
          ..where((t) => t.status.equals(SubmissionStatus.queued))
          ..limit(1))
        .getSingleOrNull();
    return submission != null;
  }

  /// Uma passagem interrompida (app fechada a meio de um envio) deixa linhas em
  /// `UPLOADING`: voltam para a fila. Só corre uma passagem de cada vez.
  Future<void> _recoverInterrupted() =>
      (db.update(_uploads)..where((t) => t.status.equals(UploadStatus.uploading)))
          .write(const InspectionUploadsCompanion(status: Value(UploadStatus.queued)));

  /// §5a: fotografias com mais de 7 dias ficam `FAILED` (`PHOTO_TOO_OLD`) sem envio.
  Future<void> _expireOldPhotos(QueueRunResult result) async {
    final limit = _utcNow.subtract(maxPhotoAge);
    result.failed +=
        await (db.update(_uploads)..where(
              (t) =>
                  t.status.equals(UploadStatus.queued) &
                  t.takenAt.isSmallerThanValue(limit),
            ))
            .write(
              const InspectionUploadsCompanion(
                status: Value(UploadStatus.failed),
                lastErrorCode: Value('PHOTO_TOO_OLD'),
              ),
            );
  }

  /// Envia as fotografias. `true` se parou numa falha temporária.
  Future<bool> _uploadPhotos(QueueRunResult result) async {
    final sent = <String>{};
    while (true) {
      final now = _utcNow;
      final next =
          await (db.select(_uploads)
                ..where(
                  (t) =>
                      t.status.equals(UploadStatus.queued) &
                      t.nextAttemptAt.isSmallerOrEqualValue(now) &
                      t.clientPhotoId.isNotIn(sent),
                )
                ..orderBy([(t) => OrderingTerm.asc(t.createdAt)])
                ..limit(1))
              .getSingleOrNull();
      if (next == null) return false;
      sent.add(next.clientPhotoId);
      final outcome = await _uploadOne(next);
      switch (outcome) {
        case _Outcome.done:
          result.uploaded++;
        case _Outcome.failed:
          result.failed++;
        case _Outcome.retry:
          result.retried++;
          return true;
        case _Outcome.sessionEnded:
          result.sessionEnded = true;
          return true;
      }
    }
  }

  Future<_Outcome> _uploadOne(InspectionUpload row) async {
    final file = File(row.filePath);
    if (!file.existsSync()) {
      await _setUpload(row.clientPhotoId, failedWith: localFileMissing);
      return _Outcome.failed;
    }
    await (db.update(_uploads)
          ..where((t) => t.clientPhotoId.equals(row.clientPhotoId)))
        .write(const InspectionUploadsCompanion(status: Value(UploadStatus.uploading)));
    try {
      final response = await _api().uploadInspectionPhoto(
        id: row.proposalId,
        clientPhotoId: row.clientPhotoId,
        // Lido para memória (≤ 1920 px): o ficheiro não fica aberto durante o
        // envio e pode apagar-se logo a seguir.
        file: MultipartFile.fromBytes(
          await file.readAsBytes(),
          filename: '${row.clientPhotoId}.jpg',
          contentType: DioMediaType('image', 'jpeg'),
        ),
        slot: PhotoSlot.valueOf(row.slot),
        source_: PhotoSource.CAMERA,
        takenAt: row.takenAt.toUtc(),
        latitude: row.latitude,
        longitude: row.longitude,
      );
      await (db.update(_uploads)
            ..where((t) => t.clientPhotoId.equals(row.clientPhotoId)))
          .write(
            InspectionUploadsCompanion(
              status: const Value(UploadStatus.done),
              remotePhotoId: Value(response.data?.id),
              lastErrorCode: const Value(null),
            ),
          );
      await InspectionRepository.deleteFile(row.filePath);
      return _Outcome.done;
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (problem.status == 401) {
        // O AuthInterceptor já tentou renovar: a sessão terminou.
        await (db.update(_uploads)
              ..where((t) => t.clientPhotoId.equals(row.clientPhotoId)))
            .write(const InspectionUploadsCompanion(status: Value(UploadStatus.queued)));
        return _Outcome.sessionEnded;
      }
      if (isTransient(problem) || error is! DioException) {
        final attempts = row.attempts + 1;
        await (db.update(_uploads)
              ..where((t) => t.clientPhotoId.equals(row.clientPhotoId)))
            .write(
              InspectionUploadsCompanion(
                status: const Value(UploadStatus.queued),
                attempts: Value(attempts),
                nextAttemptAt: Value(_utcNow.add(retryDelay(attempts))),
                lastErrorCode: Value(problem.code),
              ),
            );
        return _Outcome.retry;
      }
      await _setUpload(row.clientPhotoId, failedWith: _fieldOrTopCode(problem));
      return _Outcome.failed;
    }
  }

  Future<void> _setUpload(String clientPhotoId, {required String failedWith}) =>
      (db.update(_uploads)..where((t) => t.clientPhotoId.equals(clientPhotoId)))
          .write(
            InspectionUploadsCompanion(
              status: const Value(UploadStatus.failed),
              lastErrorCode: Value(failedWith),
            ),
          );

  /// Num 400, o código útil está no primeiro erro de campo (ex.: `PHOTO_TOO_OLD`).
  static String _fieldOrTopCode(Problem problem) =>
      problem.code == 'VALIDATION_ERROR' && problem.errors.isNotEmpty
      ? problem.errors.first.code
      : problem.code;

  Future<void> _sendSubmissions(QueueRunResult result) async {
    final now = _utcNow;
    final due =
        await (db.select(_submissions)
              ..where(
                (t) =>
                    t.status.equals(SubmissionStatus.queued) &
                    t.nextAttemptAt.isSmallerOrEqualValue(now),
              )
              ..orderBy([(t) => OrderingTerm.asc(t.createdAt)]))
            .get();
    for (final submission in due) {
      final waiting =
          await (db.select(_uploads)..where(
                (t) =>
                    t.proposalId.equals(submission.proposalId) &
                    t.status.isIn(UploadStatus.pending),
              ))
              .get();
      // Só segue depois de todas as fotografias da proposta estarem enviadas.
      if (waiting.isNotEmpty) continue;
      final outcome = await _submitOne(submission);
      if (outcome == _Outcome.done) result.submitted++;
      if (outcome == _Outcome.failed) result.failed++;
      if (outcome == _Outcome.retry) {
        result.retried++;
        return;
      }
      if (outcome == _Outcome.sessionEnded) {
        result.sessionEnded = true;
        return;
      }
    }
  }

  Future<_Outcome> _submitOne(InspectionSubmission submission) async {
    final where = db.update(_submissions)
      ..where((t) => t.proposalId.equals(submission.proposalId));
    try {
      // O `version` actual da proposta, lido antes do envio.
      final proposal = (await _api().getProposal(id: submission.proposalId)).data!;
      final status = proposal.inspection.status;
      if (status == InspectionStatus.SUBMITTED ||
          status == InspectionStatus.APPROVED) {
        // Um envio anterior chegou ao servidor, mas a resposta perdeu-se.
        await where.write(
          const InspectionSubmissionsCompanion(status: Value(SubmissionStatus.done)),
        );
        return _Outcome.done;
      }
      await _api().submitInspection(
        id: submission.proposalId,
        submitInspectionRequest: SubmitInspectionRequest(
          (b) => b
            ..odometerKm = submission.odometerKm
            ..version = proposal.version,
        ),
      );
      await where.write(
        const InspectionSubmissionsCompanion(
          status: Value(SubmissionStatus.done),
          lastErrorCode: Value(null),
        ),
      );
      return _Outcome.done;
    } on Object catch (error) {
      final problem = Problem.from(error);
      if (problem.status == 401) return _Outcome.sessionEnded;
      // 409 CONCURRENT_MODIFICATION: a proposta mudou entre a leitura e o envio.
      if (isTransient(problem) ||
          problem.code == 'CONCURRENT_MODIFICATION' ||
          error is! DioException) {
        final attempts = submission.attempts + 1;
        await where.write(
          InspectionSubmissionsCompanion(
            attempts: Value(attempts),
            nextAttemptAt: Value(_utcNow.add(retryDelay(attempts))),
            lastErrorCode: Value(problem.code),
          ),
        );
        return _Outcome.retry;
      }
      await where.write(
        InspectionSubmissionsCompanion(
          status: const Value(SubmissionStatus.failed),
          lastErrorCode: Value(problem.code),
        ),
      );
      return _Outcome.failed;
    }
  }
}

enum _Outcome { done, retry, failed, sessionEnded }
