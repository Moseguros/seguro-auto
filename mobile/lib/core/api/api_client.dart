import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../auth/auth_interceptor.dart';
import '../config/env.dart';
import 'api_serializers.dart';

/// Envolve o Dio e o cliente gerado (`packages/moseguros_api`).
///
/// O Dio leva o [AuthInterceptor]: `Accept-Language`, `Authorization` com o token
/// da [session] e renovação num 401.
class ApiClient {
  ApiClient(Env env, {Dio? dio, String Function()? language})
    : dio = dio ?? Dio(baseOptions(env)),
      language = language ?? deviceLanguage {
    this.dio.interceptors.add(
      // Um envio de ficheiro (até 10 MB) numa rede lenta precisa de mais de 15 s.
      InterceptorsWrapper(
        onRequest: (options, handler) {
          if (options.data is FormData) options.sendTimeout = uploadTimeout;
          handler.next(options);
        },
      ),
    );
    this.dio.interceptors.add(
      AuthInterceptor(
        tokens: () => session,
        retryDio: bareDio,
        language: this.language,
      ),
    );
    _api = MosegurosApi(
      dio: this.dio,
      serializers: apiSerializers,
      interceptors: const [],
    );
  }

  static const timeout = Duration(seconds: 15);

  /// Tempo máximo para enviar um ficheiro.
  static const uploadTimeout = Duration(minutes: 2);

  /// Opções do Dio: `baseUrl` do [Env] e timeout de 15 s.
  static BaseOptions baseOptions(Env env) => BaseOptions(
    baseUrl: env.apiBaseUrl,
    connectTimeout: timeout,
    receiveTimeout: timeout,
    sendTimeout: timeout,
  );

  final Dio dio;
  final String Function() language;
  late final MosegurosApi _api;

  /// Sessão do cliente; o `SessionController` liga-se aqui ao arrancar.
  SessionTokens? session;

  /// Dio com as mesmas opções e o mesmo adaptador HTTP, sem o [AuthInterceptor].
  Dio bareDio() => Dio(dio.options.copyWith())
    ..httpClientAdapter = dio.httpClientAdapter
    ..interceptors.add(
      InterceptorsWrapper(
        onRequest: (options, handler) {
          options.headers['Accept-Language'] = language();
          handler.next(options);
        },
      ),
    );

  /// `GET /api/v1/meta/version` e restantes operações da tag `Meta`.
  MetaApi get meta => _api.getMetaApi();

  /// Operações da tag `Identity` (login, perfil, contactos…).
  IdentityApi get identity => _api.getIdentityApi();

  /// Veículos e condutores do cliente (tag `Vehicle`).
  VehicleApi get vehicle => _api.getVehicleApi();

  /// Listas de referência e formatos de matrícula (tag `Catalog`).
  CatalogApi get catalog => _api.getCatalogApi();

  /// Descarga de ficheiros (tag `Document`).
  DocumentApi get document => _api.getDocumentApi();

  /// `Identity` sem renovação automática: usado pelo `SessionController` para
  /// `refreshToken` e `logoutSession`.
  IdentityApi get sessionIdentity => IdentityApi(bareDio(), apiSerializers);
}

final apiClientProvider = Provider<ApiClient>(
  (ref) => ApiClient(ref.watch(envProvider)),
);
