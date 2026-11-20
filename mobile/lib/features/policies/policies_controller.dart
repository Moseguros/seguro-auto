import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/auth/session_controller.dart';
import '../../core/clock.dart';
import '../../core/errors/problem.dart';
import '../../core/network/connectivity.dart';
import 'policy_repository.dart';

/// Dados guardados e o resultado da última actualização pela rede.
class Cached<T> {
  const Cached(
    this.value, {
    this.syncedAt,
    this.problem,
    this.documentsSlow = false,
  });

  final T value;

  /// Quando os dados vieram da rede pela última vez.
  final DateTime? syncedAt;

  /// Erro da última actualização (os dados são os da cache).
  final Problem? problem;

  /// Os documentos `PENDING` não ficaram prontos nos 2 minutos de espera.
  final bool documentsSlow;

  /// Sem ligação: a faixa "Sem ligação. Dados de {syncedAt}."
  bool get offline => problem?.code == 'NETWORK_ERROR';

  Cached<T> copyWith({
    T? value,
    DateTime? syncedAt,
    Problem? problem,
    bool? documentsSlow,
  }) => Cached(
    value ?? this.value,
    syncedAt: syncedAt ?? this.syncedAt,
    problem: problem,
    documentsSlow: documentsSlow ?? this.documentsSlow,
  );
}

/// Erro de rede sem pedido (o telemóvel diz que não há ligação).
DioException offlineError() => DioException(
  requestOptions: RequestOptions(path: '/'),
  type: DioExceptionType.connectionError,
);

DateTime? _latest(Iterable<PolicyEntry> entries) {
  DateTime? latest;
  for (final entry in entries) {
    if (latest == null || entry.syncedAt.isAfter(latest)) {
      latest = entry.syncedAt;
    }
  }
  return latest;
}

/// `/policies`: a cache e depois a rede. Sem cache espera pela rede (erro →
/// "Tentar de novo"); com cache, um erro fica em [Cached.problem].
class PolicyListController extends AsyncNotifier<Cached<List<PolicyEntry>>> {
  bool _syncing = false;

  PolicyRepository get _repository => ref.read(policyRepositoryProvider);

  @override
  Future<Cached<List<PolicyEntry>>> build() async {
    final subscription = _repository.watchAll().listen((entries) {
      final current = state.value;
      if (current == null || _syncing) return;
      state = AsyncData(
        current.copyWith(value: entries, problem: current.problem),
      );
    });
    ref.onDispose(subscription.cancel);

    final cached = await _repository.cached();
    if (cached.isEmpty) {
      final entries = await _sync();
      return Cached(entries, syncedAt: _latest(entries));
    }
    Future.microtask(refresh);
    return Cached(cached, syncedAt: _latest(cached));
  }

  Future<List<PolicyEntry>> _sync() async {
    if (!await isOnlineNow(ref)) throw offlineError();
    _syncing = true;
    try {
      return await _repository.syncAll();
    } finally {
      _syncing = false;
    }
  }

  /// Puxar para actualizar e "Tentar de novo".
  Future<void> refresh() async {
    if (!ref.mounted) return;
    final current = state.value;
    if (current == null) state = const AsyncLoading();
    try {
      final entries = await _sync();
      if (!ref.mounted) return;
      state = AsyncData(Cached(entries, syncedAt: _latest(entries)));
    } on Object catch (error, stack) {
      if (!ref.mounted) return;
      final cached = await _repository.cached();
      if (!ref.mounted) return;
      state = cached.isEmpty
          ? AsyncError(error, stack)
          : AsyncData(
              Cached(
                cached,
                syncedAt: _latest(cached),
                problem: Problem.from(error),
              ),
            );
    }
  }
}

final policyListProvider =
    AsyncNotifierProvider.autoDispose<
      PolicyListController,
      Cached<List<PolicyEntry>>
    >(PolicyListController.new, retry: (_, _) => null);

/// Intervalo e duração da espera pelos documentos `PENDING` (S6-07 §3).
const pendingDocumentsPollInterval = Duration(seconds: 5);
const pendingDocumentsPollDuration = Duration(minutes: 2);

/// `/policies/:id` e "Mostrar à polícia": a cache e depois a rede. Com
/// documentos `PENDING` pede o detalhe de novo a cada 5 s durante 2 minutos (só
/// com rede). `POLICY_NOT_FOUND` → erro (a apólice sai da cache).
class PolicyDetailController extends AsyncNotifier<Cached<PolicyEntry>> {
  PolicyDetailController(this.id);

  final String id;
  Timer? _timer;
  DateTime? _pollUntil;
  bool _syncing = false;

  PolicyRepository get _repository => ref.read(policyRepositoryProvider);

  @override
  Future<Cached<PolicyEntry>> build() async {
    ref.onDispose(() => _timer?.cancel());
    final subscription = _repository.watchOne(id).listen((entry) {
      final current = state.value;
      if (entry == null || current == null || _syncing) return;
      state = AsyncData(
        current.copyWith(value: entry, problem: current.problem),
      );
    });
    ref.onDispose(subscription.cancel);

    final cached = await _repository.cachedOne(id);
    if (cached?.detail == null) {
      final entry = await _sync();
      _schedule(entry);
      return Cached(entry, syncedAt: entry.syncedAt);
    }
    Future.microtask(refresh);
    return Cached(cached!, syncedAt: cached.syncedAt);
  }

  Future<PolicyEntry> _sync() async {
    if (!await isOnlineNow(ref)) throw offlineError();
    _syncing = true;
    try {
      return await _repository.syncDetail(id);
    } finally {
      _syncing = false;
    }
  }

  /// Puxar para actualizar, "Tentar de novo" e a espera pelos documentos.
  Future<void> refresh() async {
    if (!ref.mounted) return;
    final current = state.value;
    if (current == null) state = const AsyncLoading();
    try {
      final entry = await _sync();
      if (!ref.mounted) return;
      state = AsyncData(Cached(entry, syncedAt: entry.syncedAt));
      _schedule(entry);
    } on Object catch (error, stack) {
      if (!ref.mounted) return;
      final problem = Problem.from(error);
      final cached = await _repository.cachedOne(id);
      if (!ref.mounted) return;
      state = cached?.detail == null || problem.code == 'POLICY_NOT_FOUND'
          ? AsyncError(error, stack)
          : AsyncData(
              Cached(cached!, syncedAt: cached.syncedAt, problem: problem),
            );
    }
  }

  void _schedule(PolicyEntry entry) {
    _timer?.cancel();
    final detail = entry.detail;
    final pending =
        detail != null &&
        detail.documents.any(
          (document) => document.status == PolicyDocumentStatus.PENDING,
        );
    if (!pending) return;
    final now = ref.read(clockProvider)();
    final until = _pollUntil ??= now.add(pendingDocumentsPollDuration);
    if (now.isAfter(until)) {
      final current = state.value;
      if (current != null && !current.documentsSlow) {
        state = AsyncData(
          current.copyWith(problem: current.problem, documentsSlow: true),
        );
      }
      return;
    }
    _timer = Timer(pendingDocumentsPollInterval, () => unawaited(refresh()));
  }
}

final policyDetailProvider = AsyncNotifierProvider.autoDispose
    .family<PolicyDetailController, Cached<PolicyEntry>, String>(
      PolicyDetailController.new,
      retry: (_, _) => null,
    );

/// Apólices `ACTIVE` guardadas (atalho "Mostrar à polícia" do início).
final activePoliciesProvider = StreamProvider.autoDispose<List<PolicyEntry>>(
  (ref) => ref
      .watch(policyRepositoryProvider)
      .watchAll()
      .map(
        (entries) => [
          for (final e in entries)
            if (e.isActive) e,
        ],
      ),
);

/// Intervalo mínimo entre sincronizações ao voltar ao primeiro plano.
const policyResumeSyncInterval = Duration(minutes: 15);

/// Sincronização das apólices em segundo plano (S6-07 §2): ao abrir a app com
/// sessão, ao voltar ao primeiro plano (no máximo uma vez a cada 15 min) e
/// quando chega um push de uma apólice ([syncPolicy]). Vive com a app.
class PolicySync extends Notifier<DateTime?> {
  bool _running = false;

  @override
  DateTime? build() {
    final lifecycle = AppLifecycleListener(
      onResume: () {
        final last = state;
        final now = ref.read(clockProvider)();
        if (last == null || now.difference(last) >= policyResumeSyncInterval) {
          unawaited(syncAll());
        }
      },
    );
    ref.onDispose(lifecycle.dispose);
    ref.listen(sessionControllerProvider, (previous, next) {
      if (next.value != null && previous?.value == null) unawaited(syncAll());
    }, fireImmediately: true);
    return null;
  }

  bool get _hasSession => ref.read(sessionControllerProvider).value != null;

  /// Lista e detalhe das apólices activas. Erros ignorados.
  Future<void> syncAll() async {
    if (_running || !_hasSession) return;
    _running = true;
    try {
      if (!await isOnlineNow(ref)) return;
      await ref.read(policyRepositoryProvider).syncAll();
      if (ref.mounted) state = ref.read(clockProvider)();
    } on Object {
      // Sem rede ou erro do servidor: fica a cache.
    } finally {
      _running = false;
    }
  }

  /// Uma apólice (push `moseguros://policy/{id}`). Erros ignorados.
  Future<void> syncPolicy(String id) async {
    if (!_hasSession) return;
    try {
      if (!await isOnlineNow(ref)) return;
      await ref.read(policyRepositoryProvider).syncDetail(id);
    } on Object {
      // Fica a versão guardada.
    }
  }
}

final policySyncProvider = NotifierProvider<PolicySync, DateTime?>(
  PolicySync.new,
);
