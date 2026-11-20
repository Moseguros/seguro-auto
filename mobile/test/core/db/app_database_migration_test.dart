import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/db/app_database.dart';
import 'package:path/path.dart' as p;

/// Esquema da versão 1 (S4-09), como o drift a criou nos telemóveis.
const _v1Schema = [
  'CREATE TABLE "inspection_uploads" ("client_photo_id" TEXT NOT NULL, '
      '"proposal_id" TEXT NOT NULL, "slot" TEXT NOT NULL, '
      '"file_path" TEXT NOT NULL, "taken_at" INTEGER NOT NULL, '
      '"latitude" REAL NULL, "longitude" REAL NULL, "status" TEXT NOT NULL, '
      '"attempts" INTEGER NOT NULL DEFAULT 0, '
      '"next_attempt_at" INTEGER NOT NULL, "last_error_code" TEXT NULL, '
      '"remote_photo_id" TEXT NULL, "created_at" INTEGER NOT NULL, '
      'PRIMARY KEY ("client_photo_id"))',
  'CREATE TABLE "inspection_submissions" ("proposal_id" TEXT NOT NULL, '
      '"odometer_km" INTEGER NOT NULL, "status" TEXT NOT NULL, '
      '"attempts" INTEGER NOT NULL DEFAULT 0, '
      '"next_attempt_at" INTEGER NOT NULL, "last_error_code" TEXT NULL, '
      '"created_at" INTEGER NOT NULL, PRIMARY KEY ("proposal_id"))',
  // Uma fotografia e uma submissão na fila.
  'INSERT INTO inspection_uploads (client_photo_id, proposal_id, slot, '
      'file_path, taken_at, status, next_attempt_at, created_at) VALUES '
      "('photo-1', 'proposal-1', 'FRONT', '/x/1.jpg', 1, 'QUEUED', 1, 1)",
  'INSERT INTO inspection_submissions (proposal_id, odometer_km, status, '
      "next_attempt_at, created_at) VALUES ('proposal-1', 12000, 'QUEUED', "
      '1, 1)',
  'PRAGMA user_version = 1',
];

/// Utilizador da base na versão 1 (o esquema vem do `setup`).
class _V1 implements QueryExecutorUser {
  @override
  int get schemaVersion => 1;

  @override
  Future<void> beforeOpen(
    QueryExecutor executor,
    OpeningDetails details,
  ) async {}
}

void main() {
  test(
    'migração 1 → 2 cria as tabelas das apólices sem perder a fila da vistoria',
    () async {
      final dir = Directory.systemTemp.createTempSync('ms-migration-');
      addTearDown(() {
        try {
          dir.deleteSync(recursive: true);
        } on FileSystemException {
          // O Windows ainda tem o ficheiro aberto.
        }
      });
      final file = File(p.join(dir.path, appDatabaseFileName));

      final v1 = NativeDatabase(
        file,
        setup: (raw) {
          for (final statement in _v1Schema) {
            raw.execute(statement);
          }
        },
      );
      await v1.ensureOpen(_V1());
      await v1.close();

      final db = AppDatabase.file(file);
      addTearDown(db.close);

      expect(await db.select(db.inspectionUploads).get(), hasLength(1));
      expect(
        (await db.select(db.inspectionSubmissions).getSingle()).odometerKm,
        12000,
      );
      expect(await db.select(db.cachedPolicies).get(), isEmpty);
      expect(await db.select(db.cachedDocuments).get(), isEmpty);
      final version = await db.customSelect('PRAGMA user_version').getSingle();
      expect(version.read<int>('user_version'), 2);

      // As tabelas novas funcionam.
      await db
          .into(db.cachedPolicies)
          .insert(
            CachedPoliciesCompanion.insert(
              id: 'p1',
              status: 'ACTIVE',
              endAt: DateTime.utc(2028),
              syncedAt: DateTime.utc(2027),
            ),
          );
      expect(await db.select(db.cachedPolicies).get(), hasLength(1));
    },
  );

  test('clearUserData apaga também as apólices e os documentos', () async {
    final db = AppDatabase.memory();
    addTearDown(db.close);
    await db
        .into(db.cachedPolicies)
        .insert(
          CachedPoliciesCompanion.insert(
            id: 'p1',
            status: 'ACTIVE',
            endAt: DateTime.utc(2028),
            syncedAt: DateTime.utc(2027),
          ),
        );
    await db
        .into(db.cachedDocuments)
        .insert(
          CachedDocumentsCompanion.insert(
            fileId: 'f1',
            policyId: 'p1',
            type: 'CERTIFICATE',
            localPath: '/x/f1.pdf',
            downloadedAt: DateTime.utc(2027),
          ),
        );
    await db.clearUserData();
    expect(await db.select(db.cachedPolicies).get(), isEmpty);
    expect(await db.select(db.cachedDocuments).get(), isEmpty);
    expect(userDataFolders.first, 'inspections');
    expect(userDataFolders, contains('certificates'));
  });
}
