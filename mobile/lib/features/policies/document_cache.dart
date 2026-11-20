import 'dart:io';

import 'package:drift/drift.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';
import 'package:path/path.dart' as p;

import '../../core/api/api_client.dart';
import '../../core/clock.dart';
import '../../core/db/app_database.dart';
import '../../core/files/file_download.dart';

/// Documentos das apólices guardados no telemóvel (S6-07 §2), em
/// `<appFilesDirectory>/certificates/{fileId}.pdf`.
///
/// O certificado das apólices `ACTIVE` descarrega-se sozinho depois de cada
/// sincronização do detalhe ([prefetchCertificate]); os outros documentos só
/// quando o cliente os abre ([open]). A pasta e a tabela apagam-se no logout.
class DocumentCache {
  DocumentCache(this._ref);

  final Ref _ref;

  AppDatabase get _db => _ref.read(appDatabaseProvider);

  Future<Directory> _folder() async {
    final root = await _ref.read(appFilesDirectoryProvider.future);
    return Directory(p.join(root.path, certificatesFolder));
  }

  /// O ficheiro guardado, se existir. Uma linha sem ficheiro sai da tabela.
  Future<File?> cached(String fileId) async {
    final row = await (_db.select(
      _db.cachedDocuments,
    )..where((t) => t.fileId.equals(fileId))).getSingleOrNull();
    if (row == null) return null;
    final file = File(row.localPath);
    if (file.existsSync()) return file;
    await (_db.delete(
      _db.cachedDocuments,
    )..where((t) => t.fileId.equals(fileId))).go();
    return null;
  }

  /// O documento, da cache ou descarregado (e guardado). Falha sem rede se não
  /// estiver guardado.
  Future<File> open({
    required String policyId,
    required PolicyDocument document,
  }) async {
    final fileId = document.fileId;
    if (fileId == null) throw StateError('document without file');
    final existing = await cached(fileId);
    if (existing != null) return existing;
    final downloaded = await downloadFileBytes(
      _ref.read(apiClientProvider),
      fileId,
      fallbackContentType: 'application/pdf',
    );
    final folder = await _folder();
    await folder.create(recursive: true);
    final file = File(p.join(folder.path, '$fileId.pdf'));
    await file.writeAsBytes(downloaded.bytes, flush: true);
    await _db
        .into(_db.cachedDocuments)
        .insertOnConflictUpdate(
          CachedDocumentsCompanion.insert(
            fileId: fileId,
            policyId: policyId,
            type: document.type.name,
            fileName: Value(document.fileName),
            localPath: file.path,
            downloadedAt: _ref.read(clockProvider)().toUtc(),
          ),
        );
    return file;
  }

  /// Descarrega o certificado (`CERTIFICATE` `READY`) de uma apólice `ACTIVE`
  /// que ainda não esteja guardado. Erros ignorados (tenta na próxima vez).
  Future<void> prefetchCertificate(PolicyDetailResponse policy) async {
    if (policy.status != PolicyStatus.ACTIVE) return;
    final certificate = certificateDocument(policy);
    if (certificate == null ||
        certificate.status != PolicyDocumentStatus.READY ||
        certificate.fileId == null) {
      return;
    }
    try {
      await open(policyId: policy.id, document: certificate);
    } on Object {
      // Sem rede: fica para a próxima sincronização.
    }
  }

  /// Apaga os documentos guardados de uma apólice (saiu da conta).
  Future<void> removePolicy(String policyId) async {
    final rows = await (_db.select(
      _db.cachedDocuments,
    )..where((t) => t.policyId.equals(policyId))).get();
    for (final row in rows) {
      final file = File(row.localPath);
      try {
        if (file.existsSync()) await file.delete();
      } on FileSystemException {
        // Já não existe.
      }
    }
    await (_db.delete(
      _db.cachedDocuments,
    )..where((t) => t.policyId.equals(policyId))).go();
  }
}

/// O documento `CERTIFICATE` de uma apólice, se existir.
PolicyDocument? certificateDocument(PolicyDetailResponse policy) {
  for (final document in policy.documents) {
    if (document.type == PolicyDocumentType.CERTIFICATE) return document;
  }
  return null;
}

final documentCacheProvider = Provider<DocumentCache>(DocumentCache.new);
