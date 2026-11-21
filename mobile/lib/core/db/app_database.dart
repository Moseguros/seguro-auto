import 'dart:io';

import 'package:drift/drift.dart';
import 'package:drift/native.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:path/path.dart' as p;
import 'package:path_provider/path_provider.dart';

import 'policy_tables.dart';

export 'policy_tables.dart';

part 'app_database.g.dart';

/// Fotografias da vistoria por enviar (S4-09 §5). Uma linha por fotografia tirada;
/// `status`: `QUEUED`, `UPLOADING`, `DONE`, `FAILED`.
class InspectionUploads extends Table {
  TextColumn get clientPhotoId => text()(); // chave primária
  TextColumn get proposalId => text()();
  TextColumn get slot => text()(); // FRONT…EXTRA
  TextColumn get filePath => text()();
  DateTimeColumn get takenAt => dateTime()();
  RealColumn get latitude => real().nullable()();
  RealColumn get longitude => real().nullable()();
  TextColumn get status => text()(); // QUEUED, UPLOADING, DONE, FAILED
  IntColumn get attempts => integer().withDefault(const Constant(0))();
  DateTimeColumn get nextAttemptAt => dateTime()();
  TextColumn get lastErrorCode => text().nullable()();
  TextColumn get remotePhotoId => text().nullable()();
  DateTimeColumn get createdAt => dateTime()();

  @override
  Set<Column> get primaryKey => {clientPhotoId};
}

/// Pedidos de submissão da vistoria, enviados depois de todas as fotografias da
/// proposta. `status`: `QUEUED`, `DONE`, `FAILED`.
class InspectionSubmissions extends Table {
  TextColumn get proposalId => text()(); // chave primária
  IntColumn get odometerKm => integer()();
  TextColumn get status => text()(); // QUEUED, DONE, FAILED
  IntColumn get attempts => integer().withDefault(const Constant(0))();
  DateTimeColumn get nextAttemptAt => dateTime()();
  TextColumn get lastErrorCode => text().nullable()();
  DateTimeColumn get createdAt => dateTime()();

  @override
  Set<Column> get primaryKey => {proposalId};
}

/// Base de dados local da app (`<applicationDocumentsDirectory>/moseguros.sqlite`).
///
/// | Versão | Alteração |
/// | --- | --- |
/// | 1 | fila da vistoria (S4-09) |
/// | 2 | apólices e documentos guardados para uso sem rede (S6-07) |
///
/// Nunca se altera uma tabela sem migração.
@DriftDatabase(
  tables: [
    InspectionUploads,
    InspectionSubmissions,
    CachedPolicies,
    CachedDocuments,
  ],
)
class AppDatabase extends _$AppDatabase {
  AppDatabase(super.e);

  /// Base em memória (testes). As consultas observadas fecham de imediato, para
  /// não deixarem temporizadores pendentes nos testes de widgets.
  factory AppDatabase.memory() => AppDatabase(
    DatabaseConnection(
      NativeDatabase.memory(),
      closeStreamsSynchronously: true,
    ),
  );

  /// Base num ficheiro, no isolate actual (testes de integração).
  factory AppDatabase.file(File file) => AppDatabase(
    DatabaseConnection(NativeDatabase(file), closeStreamsSynchronously: true),
  );

  @override
  int get schemaVersion => 2;

  @override
  MigrationStrategy get migration => MigrationStrategy(
    onCreate: (m) => m.createAll(),
    onUpgrade: (m, from, to) async {
      if (from < 2) {
        // As tabelas da vistoria não mudam.
        await m.createTable(cachedPolicies);
        await m.createTable(cachedDocuments);
      }
    },
  );

  /// Apaga os dados de um utilizador (logout): todas as linhas das tabelas.
  Future<void> clearUserData() => transaction(() async {
    await delete(inspectionUploads).go();
    await delete(inspectionSubmissions).go();
    await delete(cachedDocuments).go();
    await delete(cachedPolicies).go();
  });
}

/// Nome do ficheiro da base de dados.
const appDatabaseFileName = 'moseguros.sqlite';

/// Pasta onde ficam os ficheiros da app que pertencem a um utilizador
/// (`<applicationDocumentsDirectory>`). Os testes substituem-na por uma temporária.
final appFilesDirectoryProvider = FutureProvider<Directory>(
  (ref) => getApplicationDocumentsDirectory(),
  retry: (_, _) => null,
);

/// Pastas de `<applicationDocumentsDirectory>` com ficheiros de um utilizador,
/// apagadas no logout: fotografias da vistoria por enviar (S4-09 §5) e
/// documentos das apólices (S6-07 §2). A primeira é a da vistoria
/// (`inspection_repository.dart` usa `userDataFolders.first`).
const userDataFolders = ['inspections', certificatesFolder];

/// Pasta dos documentos das apólices guardados (`{fileId}.pdf`).
const certificatesFolder = 'certificates';

/// Apaga os dados locais do utilizador (base e ficheiros). Chamado no logout.
///
/// A base abre-se no arranque da app (o executor da fila usa-a); sem ela (testes
/// só da sessão) não há nada a apagar.
final localUserDataCleanerProvider = Provider<Future<void> Function()>(
  (ref) => () async {
    if (!ref.exists(appDatabaseProvider)) return;
    await ref.read(appDatabaseProvider).clearUserData();
    final root = await ref.read(appFilesDirectoryProvider.future);
    for (final folder in userDataFolders) {
      final dir = Directory(p.join(root.path, folder));
      if (dir.existsSync()) await dir.delete(recursive: true);
    }
  },
);

/// A base de dados local, aberta uma vez por arranque da app.
final appDatabaseProvider = Provider<AppDatabase>((ref) {
  final database = AppDatabase(
    LazyDatabase(() async {
      final dir = await ref.read(appFilesDirectoryProvider.future);
      return NativeDatabase.createInBackground(
        File(p.join(dir.path, appDatabaseFileName)),
      );
    }),
  );
  ref.onDispose(database.close);
  return database;
});
