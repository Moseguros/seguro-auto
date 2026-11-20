import 'package:drift/drift.dart';

/// Apólices do cliente guardadas para uso sem rede (S6-07 §2).
///
/// `json` é a resposta de `getMyPolicy` (`null` até ao primeiro detalhe) e
/// `summary_json` o item de `listMyPolicies` (`null` se a apólice só chegou pelo
/// detalhe, ex.: uma ligação de um push).
@DataClassName('CachedPolicy')
class CachedPolicies extends Table {
  TextColumn get id => text()(); // chave primária
  TextColumn get json => text().nullable()();
  TextColumn get summaryJson => text().nullable()();
  TextColumn get status => text()(); // ACTIVE, SUSPENDED…
  DateTimeColumn get endAt => dateTime()();
  DateTimeColumn get syncedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

/// Documentos das apólices descarregados (`<appFilesDirectory>/certificates/`).
@DataClassName('CachedDocument')
class CachedDocuments extends Table {
  TextColumn get fileId => text()(); // chave primária
  TextColumn get policyId => text()();
  TextColumn get type => text()(); // CERTIFICATE, SCHEDULE…
  TextColumn get fileName => text().nullable()();
  TextColumn get localPath => text()();
  DateTimeColumn get downloadedAt => dateTime()();

  @override
  Set<Column> get primaryKey => {fileId};
}
