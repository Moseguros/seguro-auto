import 'dart:async';

import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/api/api_client.dart';
import '../../core/auth/session_controller.dart';
import '../../core/config/env.dart';
import '../../core/db/app_database.dart';
import '../../core/network/connectivity.dart';
import 'inspection_upload_queue.dart';

/// Intervalo entre passagens enquanto a app está aberta e há trabalho (60 s).
/// `null` desliga o relógio (testes).
final uploadQueueTickProvider = Provider<Duration?>(
  (ref) => const Duration(seconds: 60),
);

/// Executor da fila de envio da vistoria (S4-09 §5): um só em toda a app.
///
/// Corre quando a sessão começa, quando a app volta ao primeiro plano, quando a
/// rede volta, quando uma fotografia entra na fila ([wake]) e a cada 60 s enquanto
/// a app está aberta e há trabalho. Nunca corre duas passagens ao mesmo tempo:
/// um [wake] durante uma passagem pede outra logo a seguir. Não envia com a app
/// fechada (fora do âmbito).
class UploadQueueRunner with WidgetsBindingObserver {
  UploadQueueRunner({
    required this.queue,
    required this.tick,
    required Stream<bool> connectivityChanges,
    void Function(String message)? log,
  }) : _log = log ?? ((_) {}) {
    _connectivity = connectivityChanges.listen((online) {
      if (online) wake();
    });
    WidgetsBinding.instance.addObserver(this);
  }

  final InspectionUploadQueue queue;
  final Duration? tick;
  final void Function(String message) _log;
  late final StreamSubscription<bool> _connectivity;

  Future<void>? _running;
  bool _again = false;
  bool _active = false;
  bool _foreground = true;
  bool _disposed = false;
  Timer? _timer;

  /// A passagem em curso (testes).
  @visibleForTesting
  Future<void>? get running => _running;

  /// Há sessão: começa a enviar.
  void start() {
    if (_disposed) return;
    _active = true;
    wake();
  }

  /// A sessão terminou: pára (a passagem em curso acaba sozinha).
  void stop() {
    _active = false;
    _timer?.cancel();
    _timer = null;
  }

  /// Pede uma passagem. Devolve a passagem em curso (partilhada).
  Future<void> wake() {
    if (!_active || _disposed) return Future.value();
    final running = _running;
    if (running != null) {
      _again = true;
      return running;
    }
    return _running = _loop().whenComplete(() => _running = null);
  }

  Future<void> _loop() async {
    do {
      _again = false;
      try {
        final result = await queue.runOnce();
        _log('inspection queue: $result');
        if (result.sessionEnded) {
          stop();
          return;
        }
      } on Object catch (error) {
        _log('inspection queue failed: ${error.runtimeType}');
      }
    } while (_again && _active && !_disposed);
    await _schedule();
  }

  /// O relógio só fica ligado enquanto há trabalho e a app está à frente.
  Future<void> _schedule() async {
    final tick = this.tick;
    if (tick == null || !_active || !_foreground || _disposed) return;
    bool work;
    try {
      work = await queue.hasWork();
    } on Object {
      work = false;
    }
    if (_disposed) return;
    if (!work) {
      _timer?.cancel();
      _timer = null;
    } else {
      _timer ??= Timer.periodic(tick, (_) => wake());
    }
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _foreground = true;
      wake();
    } else if (state == AppLifecycleState.paused ||
        state == AppLifecycleState.hidden) {
      _foreground = false;
      _timer?.cancel();
      _timer = null;
    }
  }

  void dispose() {
    _disposed = true;
    stop();
    WidgetsBinding.instance.removeObserver(this);
    _connectivity.cancel();
  }
}

final uploadQueueRunnerProvider = Provider<UploadQueueRunner>((ref) {
  final api = ref.watch(apiClientProvider);
  final monitor = ref.watch(connectivityMonitorProvider);
  final isDev = ref.watch(envProvider).name == 'dev';
  final runner = UploadQueueRunner(
    queue: InspectionUploadQueue(
      db: ref.watch(appDatabaseProvider),
      api: () => api.proposal,
      isOnline: monitor.isOnline,
    ),
    tick: ref.watch(uploadQueueTickProvider),
    connectivityChanges: monitor.changes,
    // Só contagens e estados, nunca coordenadas.
    log: isDev ? debugPrint : null,
  );
  ref.listen(sessionControllerProvider, (_, next) {
    if (next.value != null) {
      runner.start();
    } else if (next.hasValue) {
      runner.stop();
    }
  }, fireImmediately: true);
  ref.onDispose(runner.dispose);
  return runner;
});
