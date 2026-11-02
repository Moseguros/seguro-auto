import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../config/env.dart';

/// Envolve o Dio e o cliente gerado (`packages/moseguros_api`).
class ApiClient {
  ApiClient(Env env, {Dio? dio}) : this._(dio ?? Dio(baseOptions(env)));

  ApiClient._(this.dio) : _api = MosegurosApi(dio: dio);

  static const timeout = Duration(seconds: 15);

  /// Opções do Dio: `baseUrl` do [Env] e timeout de 15 s.
  static BaseOptions baseOptions(Env env) => BaseOptions(
    baseUrl: env.apiBaseUrl,
    connectTimeout: timeout,
    receiveTimeout: timeout,
    sendTimeout: timeout,
  );

  final Dio dio;
  final MosegurosApi _api;

  /// `GET /api/v1/meta/version` e restantes operações da tag `Meta`.
  MetaApi get meta => _api.getMetaApi();
}

final apiClientProvider = Provider<ApiClient>(
  (ref) => ApiClient(ref.watch(envProvider)),
);
