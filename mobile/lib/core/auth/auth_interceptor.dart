import 'dart:ui';

import 'package:dio/dio.dart';

/// O que o [AuthInterceptor] precisa da sessão (implementado pelo `SessionController`).
abstract interface class SessionTokens {
  /// Token de acesso actual (só em memória), ou `null` sem sessão.
  String? get accessToken;

  /// Renova o token de acesso uma única vez, mesmo com várias chamadas em simultâneo.
  /// Devolve o token novo, ou `null` se não foi possível (a sessão termina se o
  /// servidor recusar o refresh token).
  Future<String?> renewAccessToken();
}

/// Língua dos pedidos: a do dispositivo se for inglês; senão, português de Moçambique.
String deviceLanguage() =>
    PlatformDispatcher.instance.locale.languageCode == 'en' ? 'en' : 'pt-MZ';

/// Acrescenta `Accept-Language` e `Authorization` e, num 401, renova o token e repete o pedido.
///
/// É um [QueuedInterceptorsWrapper]: os erros são tratados um de cada vez, por isso
/// três 401 em simultâneo levam a um só `refreshToken`; os seguintes vêem que o token
/// já mudou e só repetem o pedido. A renovação e a repetição usam um Dio sem este
/// interceptor ([retryDio]), para não ficarem à espera da própria fila.
class AuthInterceptor extends QueuedInterceptorsWrapper {
  AuthInterceptor({
    required this.tokens,
    required this.retryDio,
    String Function()? language,
  }) : language = language ?? deviceLanguage;

  /// Sessão actual (lida em cada pedido).
  final SessionTokens? Function() tokens;

  /// Dio sem interceptores de sessão, com o mesmo adaptador HTTP.
  final Dio Function() retryDio;

  final String Function() language;

  static const authPathPrefix = '/api/v1/auth/';
  static const _retriedKey = 'moseguros.authRetried';

  /// Pedidos públicos (`/api/v1/auth/**`): nunca levam `Authorization`, para um token
  /// expirado não estragar o login nem a renovação.
  static bool isAuthPath(RequestOptions options) =>
      options.uri.path.startsWith(authPathPrefix);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    options.headers['Accept-Language'] = language();
    if (isAuthPath(options)) {
      options.headers.remove('Authorization');
    } else {
      final token = tokens()?.accessToken;
      if (token != null) options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  }

  @override
  Future<void> onError(
    DioException err,
    ErrorInterceptorHandler handler,
  ) async {
    final options = err.requestOptions;
    final session = tokens();
    final sent = _bearer(options.headers['Authorization']);
    if (err.response?.statusCode != 401 ||
        isAuthPath(options) ||
        options.extra[_retriedKey] == true ||
        session == null ||
        sent == null) {
      handler.next(err);
      return;
    }

    var token = session.accessToken;
    if (token == null || token == sent) {
      token = await session.renewAccessToken();
    }
    if (token == null) {
      handler.next(err);
      return;
    }

    try {
      final response = await retryDio().fetch<Object?>(
        options.copyWith(
          headers: {...options.headers, 'Authorization': 'Bearer $token'},
          extra: {...options.extra, _retriedKey: true},
        ),
      );
      handler.resolve(response);
    } on DioException catch (retryError) {
      handler.next(retryError);
    }
  }

  static String? _bearer(Object? header) {
    if (header is! String || !header.startsWith('Bearer ')) return null;
    return header.substring('Bearer '.length);
  }
}
