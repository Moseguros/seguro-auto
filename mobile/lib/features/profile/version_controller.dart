import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/api/api_client.dart';

/// Versão da API (`GET /api/v1/meta/version`), no cartão "Sobre" do perfil.
class VersionController extends AsyncNotifier<VersionResponse> {
  @override
  Future<VersionResponse> build() => _fetch();

  /// Botão "Tentar de novo".
  Future<void> retry() async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(_fetch);
  }

  Future<VersionResponse> _fetch() async {
    final response = await ref.read(apiClientProvider).meta.getVersion();
    return response.data ?? VersionResponse();
  }
}

final versionControllerProvider =
    AsyncNotifierProvider<VersionController, VersionResponse>(
      VersionController.new,
      // Sem novas tentativas automáticas: o utilizador decide com "Tentar de novo".
      retry: (_, _) => null,
    );
