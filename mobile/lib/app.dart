import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'core/config/env.dart';
import 'core/router.dart';
import 'core/theme.dart';
import 'l10n/app_localizations.dart';

/// Arranque comum aos três flavors.
void runMoSeguros(Env env) {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(
    ProviderScope(
      overrides: [envProvider.overrideWithValue(env)],
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
