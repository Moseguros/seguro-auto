import 'dart:io';

import 'package:drift/drift.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path/path.dart' as p;
import 'package:uuid/uuid.dart';

import '../../core/db/app_database.dart';
import '../../core/location/location_service.dart';
import 'inspection_slots.dart';

/// Estados de uma fotografia na fila (`InspectionUploads.status`).
abstract final class UploadStatus {
  static const queued = 'QUEUED';
  static const uploading = 'UPLOADING';
  static const done = 'DONE';
  static const failed = 'FAILED';

  /// Ainda não chegou ao servidor.
  static const pending = [queued, uploading, failed];
}

/// Estados de um pedido de submissão (`InspectionSubmissions.status`).
abstract final class SubmissionStatus {
  static const queued = 'QUEUED';
  static const done = 'DONE';
  static const failed = 'FAILED';
}

/// Pasta das fotografias da fila: `<documentos>/inspections` (apagada no logout).
final inspectionsFolder = userDataFolders.first;

/// Fila local da vistoria (S4-09 §5): fotografias tiradas sem rede e o pedido de
/// submissão, guardados na base drift e em ficheiros
/// `<documentos>/inspections/{proposalId}/{clientPhotoId}.jpg`.
class InspectionRepository {
  InspectionRepository({
    required this.db,
    required Future<Directory> Function() filesDirectory,
    DateTime Function()? now,
    String Function()? newId,
  }) : _filesDirectory = filesDirectory,
       _now = now ?? DateTime.now,
       _newId = newId ?? const Uuid().v4;

  final AppDatabase db;
  final Future<Directory> Function() _filesDirectory;
  final DateTime Function() _now;
  final String Function() _newId;

  DateTime get _utcNow => _now().toUtc();

  /// Fotografias da proposta na fila, pela ordem de entrada.
  Stream<List<InspectionUpload>> watchUploads(String proposalId) =>
      (db.select(db.inspectionUploads)
            ..where((t) => t.proposalId.equals(proposalId))
            ..orderBy([(t) => OrderingTerm.asc(t.createdAt)]))
          .watch();

  /// Pedido de submissão da proposta, se houver.
  Stream<InspectionSubmission?> watchSubmission(String proposalId) =>
      (db.select(
        db.inspectionSubmissions,
      )..where((t) => t.proposalId.equals(proposalId))).watchSingleOrNull();

  /// Fotografias por enviar (`QUEUED`, `UPLOADING`, `FAILED`), de uma proposta
  /// ou de todas.
  Stream<int> watchPendingCount({String? proposalId}) =>
      _countQuery(proposalId).watchSingle();

  /// O mesmo que [watchPendingCount], lido uma vez.
  Future<int> pendingCount({String? proposalId}) =>
      _countQuery(proposalId).getSingle();

  Selectable<int> _countQuery(String? proposalId) {
    final count = db.inspectionUploads.clientPhotoId.count();
    final query = db.selectOnly(db.inspectionUploads)
      ..addColumns([count])
      ..where(_pending(proposalId));
    return query.map((row) => row.read(count) ?? 0);
  }

  Expression<bool> _pending(String? proposalId) {
    final t = db.inspectionUploads;
    final pending = t.status.isIn(UploadStatus.pending);
    return proposalId == null
        ? pending
        : pending & t.proposalId.equals(proposalId);
  }

  /// Grava a fotografia num ficheiro e põe-na na fila (`QUEUED`, para já).
  ///
  /// Uma fotografia nova de um ângulo fixo substitui a que ainda está na fila
  /// (`QUEUED` ou `FAILED`) para o mesmo ângulo: apaga a linha e o ficheiro.
  Future<InspectionUpload> enqueuePhoto({
    required String proposalId,
    required String slot,
    required Uint8List bytes,
    required DateTime takenAt,
    GeoPoint? location,
  }) async {
    final clientPhotoId = _newId();
    final dir = Directory(await _proposalDirectory(proposalId));
    await dir.create(recursive: true);
    final file = File(p.join(dir.path, '$clientPhotoId.jpg'));
    await file.writeAsBytes(bytes, flush: true);

    final now = _utcNow;
    final replaced = <InspectionUpload>[];
    await db.transaction(() async {
      if (slot != extraSlot) {
        final t = db.inspectionUploads;
        final old =
            await (db.select(t)..where(
                  (row) =>
                      row.proposalId.equals(proposalId) &
                      row.slot.equals(slot) &
                      row.status.isIn([
                        UploadStatus.queued,
                        UploadStatus.failed,
                      ]),
                ))
                .get();
        replaced.addAll(old);
        for (final row in old) {
          await (db.delete(
            t,
          )..where((r) => r.clientPhotoId.equals(row.clientPhotoId))).go();
        }
      }
      await db
          .into(db.inspectionUploads)
          .insert(
            InspectionUploadsCompanion.insert(
              clientPhotoId: clientPhotoId,
              proposalId: proposalId,
              slot: slot,
              filePath: file.path,
              takenAt: takenAt.toUtc(),
              latitude: Value(location?.latitude),
              longitude: Value(location?.longitude),
              status: UploadStatus.queued,
              nextAttemptAt: now,
              createdAt: now,
            ),
          );
    });
    for (final row in replaced) {
      await deleteFile(row.filePath);
    }
    return (db.select(
      db.inspectionUploads,
    )..where((t) => t.clientPhotoId.equals(clientPhotoId))).getSingle();
  }

  /// Descarta uma fotografia que não chegou ao servidor (linha e ficheiro).
  Future<void> discard(String clientPhotoId) async {
    final row = await (db.select(
      db.inspectionUploads,
    )..where((t) => t.clientPhotoId.equals(clientPhotoId))).getSingleOrNull();
    if (row == null || row.status == UploadStatus.uploading) return;
    await (db.delete(
      db.inspectionUploads,
    )..where((t) => t.clientPhotoId.equals(clientPhotoId))).go();
    await deleteFile(row.filePath);
  }

  /// Põe na fila o envio da vistoria (substitui um pedido anterior da proposta).
  Future<void> queueSubmission(String proposalId, int odometerKm) async {
    final now = _utcNow;
    await db
        .into(db.inspectionSubmissions)
        .insertOnConflictUpdate(
          InspectionSubmissionsCompanion.insert(
            proposalId: proposalId,
            odometerKm: odometerKm,
            status: SubmissionStatus.queued,
            attempts: const Value(0),
            nextAttemptAt: now,
            lastErrorCode: const Value(null),
            createdAt: now,
          ),
        );
  }

  /// Retira o pedido de submissão (ex.: depois de `INSPECTION_PHOTOS_MISSING`).
  Future<void> clearSubmission(String proposalId) => (db.delete(
    db.inspectionSubmissions,
  )..where((t) => t.proposalId.equals(proposalId))).go();

  /// Esquece as fotografias já enviadas que o servidor já mostra na proposta.
  Future<void> forgetUploaded(String proposalId, Set<String> remoteIds) async {
    if (remoteIds.isEmpty) return;
    await (db.delete(db.inspectionUploads)..where(
          (t) =>
              t.proposalId.equals(proposalId) &
              t.status.equals(UploadStatus.done) &
              t.remotePhotoId.isIn(remoteIds),
        ))
        .go();
  }

  Future<String> _proposalDirectory(String proposalId) async =>
      p.join((await _filesDirectory()).path, inspectionsFolder, proposalId);

  static Future<void> deleteFile(String path) async {
    try {
      final file = File(path);
      if (file.existsSync()) await file.delete();
    } on FileSystemException {
      // Já não existe.
    }
  }
}

final inspectionRepositoryProvider = Provider<InspectionRepository>(
  (ref) => InspectionRepository(
    db: ref.watch(appDatabaseProvider),
    filesDirectory: () => ref.read(appFilesDirectoryProvider.future),
  ),
);

/// Fotografias da proposta na fila.
final inspectionUploadsProvider = StreamProvider.autoDispose
    .family<List<InspectionUpload>, String>(
      (ref, proposalId) =>
          ref.watch(inspectionRepositoryProvider).watchUploads(proposalId),
    );

/// Pedido de submissão da proposta na fila.
final inspectionSubmissionProvider = StreamProvider.autoDispose
    .family<InspectionSubmission?, String>(
      (ref, proposalId) =>
          ref.watch(inspectionRepositoryProvider).watchSubmission(proposalId),
    );

/// Fotografias por enviar (`null` = todas as propostas).
final pendingUploadsProvider = StreamProvider.autoDispose.family<int, String?>(
  (ref, proposalId) => ref
      .watch(inspectionRepositoryProvider)
      .watchPendingCount(proposalId: proposalId),
);
