import 'dart:async';

import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Estado da rede (`connectivity_plus`). Os testes usam uma implementação falsa
/// através de [connectivityMonitorProvider].
///
/// "Com rede" quer dizer que há uma interface ligada (Wi-Fi, dados móveis…); não
/// garante que o servidor responda: os pedidos tratam esse caso como erro de rede.
abstract interface class ConnectivityMonitor {
  Future<bool> isOnline();

  /// Muda quando a ligação cai ou volta.
  Stream<bool> get changes;
}

class DeviceConnectivityMonitor implements ConnectivityMonitor {
  DeviceConnectivityMonitor([Connectivity? connectivity])
    : _connectivity = connectivity ?? Connectivity();

  final Connectivity _connectivity;

  static bool _online(List<ConnectivityResult> results) =>
      results.any((result) => result != ConnectivityResult.none);

  @override
  Future<bool> isOnline() async {
    try {
      return _online(await _connectivity.checkConnectivity());
    } on Object {
      // Sem o plugin (ex.: plataforma sem suporte), tenta-se sempre.
      return true;
    }
  }

  @override
  Stream<bool> get changes =>
      _connectivity.onConnectivityChanged.map(_online).distinct();
}

final connectivityMonitorProvider = Provider<ConnectivityMonitor>(
  (ref) => DeviceConnectivityMonitor(),
);

/// `true` com rede. Começa pelo estado actual e segue as mudanças.
final onlineProvider = StreamProvider<bool>((ref) async* {
  final monitor = ref.watch(connectivityMonitorProvider);
  yield await monitor.isOnline();
  yield* monitor.changes;
});

/// Lê o estado da rede no momento (sem esperar pelo [onlineProvider]).
Future<bool> isOnlineNow(Ref ref) =>
    ref.read(connectivityMonitorProvider).isOnline();
