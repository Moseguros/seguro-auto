import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'core/config/env.dart';
import 'core/router.dart';
import 'core/theme.dart';
import 'features/inspection/upload_queue_runner.dart';
import 'features/onboarding/onboarding_controller.dart';
import 'l10n/app_localizations.dart';

/// Arranque comum aos três flavors.
Future<void> runMoSeguros(Env env) async {
  WidgetsFlutterBinding.ensureInitialized();
  final preferences = await SharedPreferences.getInstance();
  runApp(
    ProviderScope(
      overrides: [
        envProvider.overrideWithValue(env),
        sharedPreferencesProvider.overrideWithValue(preferences),
      ],
      child: const MoSegurosApp(),
    ),
  );
}

/// Língua por defeito quando a do dispositivo não é suportada.
const defaultLocale = Locale('pt');

class MoSegurosApp extends ConsumerWidget {
  const MoSegurosApp({super.key, this.locale});

  /// Força uma língua (testes); por defeito segue o dispositivo.
  final Locale? locale;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    // O executor da fila da vistoria vive enquanto a app vive (S4-09 §5).
    ref.watch(uploadQueueRunnerProvider);
    return MaterialApp.router(
      onGenerateTitle: (context) => AppLocalizations.of(context).appName,
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      locale: locale,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      localeResolutionCallback: resolveLocale,
      routerConfig: ref.watch(routerProvider),
    );
  }
}

/// Usa a língua do dispositivo se for suportada; senão, português.
Locale resolveLocale(Locale? device, Iterable<Locale> supported) {
  for (final locale in supported) {
    if (locale.languageCode == device?.languageCode) return locale;
  }
  return defaultLocale;
}
