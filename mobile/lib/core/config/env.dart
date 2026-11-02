import 'package:flutter_riverpod/flutter_riverpod.dart';

/// Configuração do ambiente (flavor) em que a app corre.
///
/// Cada ponto de entrada (`main_dev.dart`, `main_staging.dart`, `main_prod.dart`)
/// cria o seu [Env] e injecta-o com [envProvider].
class Env {
  const Env({required this.apiBaseUrl, required this.name});

  /// Lê o URL da API de `--dart-define=API_BASE_URL=…` (staging e prod).
  factory Env.fromDefine(String name) {
    const apiBaseUrl = String.fromEnvironment('API_BASE_URL');
    if (apiBaseUrl.isEmpty) {
      throw StateError(
        'API_BASE_URL is required for the "$name" flavor: '
        'use --dart-define=API_BASE_URL=https://…',
      );
    }
    return Env(apiBaseUrl: apiBaseUrl, name: name);
  }

  /// URL base da API, sem `/api/v1` (o cliente gerado acrescenta o caminho).
  final String apiBaseUrl;

  /// Nome do ambiente: `dev`, `staging` ou `prod`.
  final String name;
}

/// Ambiente actual. É sempre substituído no `ProviderScope` do ponto de entrada.
final envProvider = Provider<Env>(
  (ref) =>
      throw UnimplementedError('envProvider must be overridden in main_*.dart'),
);
