import 'dart:async';

import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import 'payment_controller.dart';
import 'payment_labels.dart';

const mpesaPollInterval = Duration(seconds: 3);
const referencePollInterval = Duration(seconds: 30);

/// Intervalo entre leituras (S5-11 §4): 3 s com M-Pesa por terminar, 30 s com
/// entidade e referência pendente, nenhum num estado final.
Duration? pollIntervalFor(IntentView intent) {
  if (isFinalPayment(intent.status)) return null;
  return intent.method == IntentViewMethodEnum.MPESA
      ? mpesaPollInterval
      : referencePollInterval;
}

/// Um pagamento (`getPaymentIntent`), lido de novo pelo [pollIntervalFor] com
/// um `Timer.periodic`. Com a app em segundo plano pára; ao voltar ao primeiro
/// plano (o cliente volta da app do M-Pesa ou do banco) lê logo e retoma.
class PaymentIntentController extends AsyncNotifier<IntentView> {
  PaymentIntentController(this.id);

  final String id;
  Timer? _timer;
  Duration? _interval;
  bool _foreground = true;
  bool _reading = false;

  @override
  Future<IntentView> build() async {
    final lifecycle = AppLifecycleListener(onStateChange: _onLifecycle);
    ref.onDispose(() {
      _cancel();
      lifecycle.dispose();
    });
    final intent = await ref.read(paymentRepositoryProvider).intent(id);
    _schedule(intent);
    return intent;
  }

  void _onLifecycle(AppLifecycleState state) {
    switch (state) {
      case AppLifecycleState.resumed:
        if (_foreground) return;
        _foreground = true;
        unawaited(refresh());
      case AppLifecycleState.paused || AppLifecycleState.hidden:
        _foreground = false;
        _cancel();
      default:
        break;
    }
  }

  void _cancel() {
    _timer?.cancel();
    _timer = null;
    _interval = null;
  }

  void _schedule(IntentView intent) {
    final interval = pollIntervalFor(intent);
    if (!_foreground || interval == null) {
      _cancel();
      return;
    }
    if (_timer != null && _interval == interval) return;
    _cancel();
    _interval = interval;
    _timer = Timer.periodic(interval, (_) => unawaited(refresh()));
  }

  /// Lê de novo. Um erro de rede não esconde o pagamento já mostrado.
  Future<void> refresh() async {
    if (_reading) return;
    _reading = true;
    try {
      final next = await AsyncValue.guard(
        () => ref.read(paymentRepositoryProvider).intent(id),
      );
      if (!ref.mounted) return;
      if (next.hasValue) {
        state = next;
        _schedule(next.requireValue);
      } else if (!state.hasValue) {
        state = next;
      }
    } finally {
      _reading = false;
    }
  }
}

final paymentIntentProvider = AsyncNotifierProvider.autoDispose
    .family<PaymentIntentController, IntentView, String>(
      PaymentIntentController.new,
      retry: (_, _) => null,
    );
