import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// `SharedPreferences` já carregadas; substituído no arranque (`runMoSeguros`) e nos testes.
final sharedPreferencesProvider = Provider<SharedPreferences>(
  (ref) => throw UnimplementedError(
    'sharedPreferencesProvider must be overridden with SharedPreferences.getInstance()',
  ),
);

/// A apresentação já foi vista (`onboarding_done` em `shared_preferences`).
class OnboardingController extends Notifier<bool> {
  static const key = 'onboarding_done';

  @override
  bool build() => ref.read(sharedPreferencesProvider).getBool(key) ?? false;

  /// "Saltar" ou "Começar".
  Future<void> complete() async {
    await ref.read(sharedPreferencesProvider).setBool(key, true);
    state = true;
  }
}

final onboardingControllerProvider =
    NotifierProvider<OnboardingController, bool>(OnboardingController.new);
