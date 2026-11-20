import 'dart:async';
import 'dart:convert';

import 'package:built_value/serializer.dart';
import 'package:drift/drift.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';
import '../../core/api/api_serializers.dart';
import '../../core/clock.dart';
import '../../core/db/app_database.dart';
import '../../core/errors/problem.dart';
import 'document_cache.dart';

/// Uma apólice guardada: o item da lista ([summary]) e/ou o detalhe ([detail]).
/// Os campos comuns lêem-se do detalhe quando existe.
class PolicyEntry {
  const PolicyEntry({
    required this.id,
    required this.status,
    required this.endAt,
    required this.syncedAt,
    this.summary,
    this.detail,
  });

  final String id;
  final MyPolicySummaryResponse? summary;
  final PolicyDetailResponse? detail;

  /// `status` da apólice (`ACTIVE`…), em texto.
  final String status;
  final DateTime endAt;

  /// Última actualização pela rede.
  final DateTime syncedAt;

  bool get isActive => status == PolicyStatus.ACTIVE.name;

  PolicyVehicleRef get vehicle => detail?.vehicle ?? summary!.vehicle;

  PolicyInsurerRef get insurer => detail?.insurer ?? summary!.insurer;

  String get productName => detail?.product.name ?? summary!.productName;

  String get number => detail?.number ?? summary!.number;

  PolicyStatus get policyStatus => detail?.status ?? summary!.status;

  CoverStatus get coverStatus => detail?.coverStatus ?? summary!.coverStatus;

  CoverageType get coverageType =>
      detail?.coverageType ?? summary!.coverageType;

  DateTime get startAt => detail?.startAt ?? summary!.startAt;
}

/// Apólices do cliente com cache local (S6-07 §2): mostra-se sempre primeiro o
/// que está guardado e depois actualiza-se pela rede (*stale-while-revalidate*).
class PolicyRepository {
  PolicyRepository(this._ref);

  final Ref _ref;

  /// Tamanho de cada página pedida a `listMyPolicies`.
  static const pageSize = 50;

  /// Máximo de apólices guardadas.
  static const maxPolicies = 100;

  AppDatabase get _db => _ref.read(appDatabaseProvider);

  ApiClient get _api => _ref.read(apiClientProvider);

  DocumentCache get _documents => _ref.read(documentCacheProvider);

  DateTime _now() => _ref.read(clockProvider)().toUtc();

  // --- Cache -----------------------------------------------------------------

  static T? _decode<T>(Serializer<T> serializer, String? text) {
    if (text == null) return null;
    try {
      return apiSerializers.deserializeWith(serializer, jsonDecode(text));
    } on Object {
      return null;
    }
  }

  static String _encode<T>(Serializer<T> serializer, T value) =>
      jsonEncode(apiSerializers.serializeWith(serializer, value));

  static PolicyEntry? _entry(CachedPolicy row) {
    final summary = _decode(
      MyPolicySummaryResponse.serializer,
      row.summaryJson,
    );
    final detail = _decode(PolicyDetailResponse.serializer, row.json);
    if (summary == null && detail == null) return null;
    return PolicyEntry(
      id: row.id,
      summary: summary,
      detail: detail,
      status: row.status,
      // O drift devolve as datas na hora local.
      endAt: row.endAt.toUtc(),
      syncedAt: row.syncedAt.toUtc(),
    );
  }

  /// Mais recentes primeiro (início da apólice).
  static List<PolicyEntry> _entries(List<CachedPolicy> rows) =>
      [for (final row in rows) ?_entry(row)]
        ..sort((a, b) => b.startAt.compareTo(a.startAt));

  /// Todas as apólices guardadas.
  Future<List<PolicyEntry>> cached() async =>
      _entries(await _db.select(_db.cachedPolicies).get());

  /// As apólices guardadas, actualizadas a cada sincronização.
  Stream<List<PolicyEntry>> watchAll() =>
      _db.select(_db.cachedPolicies).watch().map(_entries);

  /// Uma apólice guardada.
  Future<PolicyEntry?> cachedOne(String id) async {
    final row = await (_db.select(
      _db.cachedPolicies,
    )..where((t) => t.id.equals(id))).getSingleOrNull();
    return row == null ? null : _entry(row);
  }

  /// Uma apólice guardada, actualizada a cada sincronização.
  Stream<PolicyEntry?> watchOne(String id) =>
      (_db.select(_db.cachedPolicies)..where((t) => t.id.equals(id)))
          .watchSingleOrNull()
          .map((row) => row == null ? null : _entry(row));

  /// Tira uma apólice da cache, com os seus documentos.
  Future<void> remove(String id) async {
    await _documents.removePolicy(id);
    await (_db.delete(_db.cachedPolicies)..where((t) => t.id.equals(id))).go();
  }

  // --- Rede ------------------------------------------------------------------

  /// `listMyPolicies` (todas as páginas, até [maxPolicies]) → `summary_json`.
  /// As apólices que deixaram de vir saem da cache.
  Future<List<PolicyEntry>> syncList() async {
    final summaries = <MyPolicySummaryResponse>[];
    for (var page = 0; summaries.length < maxPolicies; page++) {
      final response = await _api.policy.listMyPolicies(
        page: page,
        size: pageSize,
      );
      final data = response.data;
      if (data == null || data.content.isEmpty) break;
      summaries.addAll(data.content);
      if (summaries.length >= data.totalElements) break;
    }
    if (summaries.length > maxPolicies) summaries.length = maxPolicies;

    final now = _now();
    final ids = {for (final summary in summaries) summary.id};
    final stale = await (_db.select(
      _db.cachedPolicies,
    )..where((t) => t.id.isNotIn(ids))).get();
    for (final row in stale) {
      await _documents.removePolicy(row.id);
    }
    await _db.transaction(() async {
      await (_db.delete(
        _db.cachedPolicies,
      )..where((t) => t.id.isNotIn(ids))).go();
      for (final summary in summaries) {
        await _db
            .into(_db.cachedPolicies)
            .insertOnConflictUpdate(
              CachedPoliciesCompanion(
                id: Value(summary.id),
                summaryJson: Value(
                  _encode(MyPolicySummaryResponse.serializer, summary),
                ),
                status: Value(summary.status.name),
                endAt: Value(summary.endAt.toUtc()),
                syncedAt: Value(now),
              ),
            );
      }
    });
    return cached();
  }

  /// `getMyPolicy` → `json`, `synced_at`; depois descarrega o certificado se
  /// for preciso. `POLICY_NOT_FOUND` tira a apólice da cache.
  Future<PolicyEntry> syncDetail(String id) async {
    final PolicyDetailResponse detail;
    try {
      final response = await _api.policy.getMyPolicy(id: id);
      detail = response.data!;
    } on Object catch (error) {
      if (Problem.from(error).code == 'POLICY_NOT_FOUND') await remove(id);
      rethrow;
    }
    await _db
        .into(_db.cachedPolicies)
        .insertOnConflictUpdate(
          CachedPoliciesCompanion(
            id: Value(detail.id),
            json: Value(_encode(PolicyDetailResponse.serializer, detail)),
            status: Value(detail.status.name),
            endAt: Value(detail.endAt.toUtc()),
            syncedAt: Value(_now()),
          ),
        );
    unawaited(_documents.prefetchCertificate(detail));
    return (await cachedOne(detail.id))!;
  }

  /// A lista e o detalhe das apólices `ACTIVE` (para "Mostrar à polícia" sem
  /// rede). Um detalhe que falhe não impede os outros.
  Future<List<PolicyEntry>> syncAll() async {
    final entries = await syncList();
    for (final entry in entries.where((entry) => entry.isActive)) {
      try {
        await syncDetail(entry.id);
      } on Object {
        // Fica a versão guardada.
      }
    }
    return cached();
  }

  /// A cache e depois a rede: emite as apólices guardadas e, depois de
  /// [syncList], as actualizadas. Um erro da rede chega depois da cache.
  Stream<List<PolicyEntry>> list() async* {
    yield await cached();
    yield await syncList();
  }

  /// O mesmo que [list] para uma apólice.
  Stream<PolicyEntry?> detail(String id) async* {
    yield await cachedOne(id);
    yield await syncDetail(id);
  }
}

final policyRepositoryProvider = Provider<PolicyRepository>(
  PolicyRepository.new,
);
