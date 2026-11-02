import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../api/api_client.dart';
import 'auth_interceptor.dart';
import 'token_storage.dart';

/// Sessão do cliente: tokens e o resultado de `getMe`.
class Session {
  const Session({
    required this.accessToken,
    required this.refreshToken,
    this.me,
    this.isNewUser = false,
  });

  /// Só em memória.
  final String accessToken;
  final String refreshToken;
  final MeResponse? me;

  /// A conta foi criada neste login (`verifyOtp`).
  final bool isNewUser;

  /// Conta nova ou perfil incompleto: à entrada vai-se para `/profile?complete=1`.
  bool get needsProfile => isNewUser || me?.profileComplete == false;

  Session copyWith({
    String? accessToken,
    String? refreshToken,
    MeResponse? me,
    bool? isNewUser,
  }) => Session(
    accessToken: accessToken ?? this.accessToken,
    refreshToken: refreshToken ?? this.refreshToken,
    me: me ?? this.me,
    isNewUser: isNewUser ?? this.isNewUser,
  );
}

/// Sessão do cliente na app. `null` = anónimo.
///
/// Ao arrancar, se houver refresh token guardado, chama `refreshToken`; se o servidor
/// o recusar, apaga-o e a sessão fica anónima. Sem resposta do servidor (sem rede),
/// a sessão fica anónima mas o refresh token mantém-se para a próxima abertura.
class SessionController extends AsyncNotifier<Session?>
    implements SessionTokens {
  Session? _current;
  Future<String?>? _renewing;

  ApiClient get _api => ref.read(apiClientProvider);

  TokenStorage get _storage => ref.read(tokenStorageProvider);

  @override
  String? get accessToken => _current?.accessToken;

  @override
  Future<Session?> build() async {
    _api.session = this;
    _current = null;
    final stored = await _storage.readRefreshToken();
    if (stored == null) return null;
    try {
      final tokens = await _refresh(stored);
      _current = Session(
        accessToken: tokens.accessToken,
        refreshToken: tokens.refreshToken,
      );
    } on DioException catch (error) {
      if (error.response != null) await _storage.deleteRefreshToken();
      return null;
    }
    final me = await _fetchMe();
    return _current = _current?.copyWith(me: me);
  }

  /// Guarda os tokens de `verifyOtp` e lê o utilizador.
  Future<Session> login({
    required String accessToken,
    required String refreshToken,
    bool isNewUser = false,
  }) async {
    await _storage.writeRefreshToken(refreshToken);
    _current = Session(
      accessToken: accessToken,
      refreshToken: refreshToken,
      isNewUser: isNewUser,
    );
    final me = await _fetchMe();
    final session = _current!.copyWith(me: me);
    _set(session);
    return session;
  }

  /// Volta a ler `getMe` (nome e `profileComplete` mudaram).
  Future<void> refreshMe() async {
    final me = await _fetchMe();
    final current = _current;
    if (me == null || current == null) return;
    _set(current.copyWith(me: me, isNewUser: false));
  }

  /// Termina a sessão: avisa a API (ignora erros) e apaga o refresh token.
  Future<void> logout() async {
    final refreshToken = _current?.refreshToken;
    _set(null);
    await _storage.deleteRefreshToken();
    if (refreshToken != null) {
      unawaited(
        _api.sessionIdentity
            .logoutSession(
              logoutRequest: LogoutRequest(
                (b) => b..refreshToken = refreshToken,
              ),
            )
            .then<void>((_) {}, onError: (Object _) {}),
      );
    }
  }

  @override
  Future<String?> renewAccessToken() =>
      _renewing ??= _renew().whenComplete(() => _renewing = null);

  Future<String?> _renew() async {
    final current = _current;
    if (current == null) return null;
    try {
      final tokens = await _refresh(current.refreshToken);
      // A sessão pode ter terminado entretanto.
      if (!identical(_current, current)) return _current?.accessToken;
      _set(
        current.copyWith(
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
        ),
      );
      return tokens.accessToken;
    } on DioException catch (error) {
      // Recusa do servidor: a sessão acabou. Sem rede: mantém-se.
      if (error.response != null && identical(_current, current)) {
        _set(null);
        await _storage.deleteRefreshToken();
      }
      return null;
    }
  }

  Future<TokenResponse> _refresh(String refreshToken) async {
    final response = await _api.sessionIdentity.refreshToken(
      refreshTokenRequest: RefreshTokenRequest(
        (b) => b..refreshToken = refreshToken,
      ),
    );
    final tokens = response.data;
    if (tokens == null) {
      throw DioException.badResponse(
        statusCode: response.statusCode ?? 0,
        requestOptions: response.requestOptions,
        response: response,
      );
    }
    if (tokens.refreshToken != refreshToken) {
      await _storage.writeRefreshToken(tokens.refreshToken);
    }
    return tokens;
  }

  Future<MeResponse?> _fetchMe() async {
    try {
      return (await _api.identity.getMe()).data;
    } on DioException {
      return null;
    }
  }

  void _set(Session? session) {
    _current = session;
    state = AsyncData(session);
  }
}

final sessionControllerProvider =
    AsyncNotifierProvider<SessionController, Session?>(
      SessionController.new,
      retry: (_, _) => null,
    );
