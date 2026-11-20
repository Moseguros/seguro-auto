import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:screen_brightness/screen_brightness.dart';
import 'package:wakelock_plus/wakelock_plus.dart';

/// Brilho e ecrã ligado de "Mostrar à polícia" (S6-07 §3). Os testes usam um
/// falso através de [screenKeeperProvider].
abstract interface class ScreenKeeper {
  /// Brilho da app no máximo e o ecrã sempre ligado.
  Future<void> keepBright();

  /// Repõe o brilho anterior e deixa o ecrã desligar-se.
  Future<void> restore();
}

/// `screen_brightness` e `wakelock_plus`. Erros ignorados (sem o plugin o ecrã
/// funciona na mesma).
class DeviceScreenKeeper implements ScreenKeeper {
  const DeviceScreenKeeper();

  @override
  Future<void> keepBright() async {
    try {
      await ScreenBrightness().setApplicationScreenBrightness(1);
    } on Object {
      // Sem controlo do brilho.
    }
    try {
      await WakelockPlus.enable();
    } on Object {
      // Sem wakelock.
    }
  }

  @override
  Future<void> restore() async {
    try {
      await ScreenBrightness().resetApplicationScreenBrightness();
    } on Object {
      // Sem controlo do brilho.
    }
    try {
      await WakelockPlus.disable();
    } on Object {
      // Sem wakelock.
    }
  }
}

final screenKeeperProvider = Provider<ScreenKeeper>(
  (ref) => const DeviceScreenKeeper(),
);
