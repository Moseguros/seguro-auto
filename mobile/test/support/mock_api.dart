import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:http_mock_adapter/http_mock_adapter.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/auth/token_storage.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros/features/onboarding/onboarding_controller.dart';
import 'package:shared_preferences/shared_preferences.dart';

const testEnv = Env(apiBaseUrl: 'http://localhost:8080', name: 'test');

/// API simulada com `http_mock_adapter`, sobre o Dio verdadeiro do [ApiClient]
/// (com o `AuthInterceptor`). Regista todos os pedidos que chegam ao "servidor".
class MockApi {
  MockApi() {
    adapter = DioAdapter(dio: client.dio);
  }

  final client = ApiClient(testEnv, language: () => 'pt-MZ');
  late final DioAdapter adapter;

  /// Pedidos recebidos, pela ordem de chegada.
  final List<RequestOptions> requests = [];

  /// Responde a `METHOD path` com [status] e [body] (um valor ou uma função do pedido).
  /// Com [whenHeaders], só responde aos pedidos com esses cabeçalhos. A última
  /// resposta registada para o mesmo pedido é a que vale.
  void on(
    String method,
    String path,
    int status, [
    Object? body,
    Map<String, List<String>> headers = const {},
    Map<String, dynamic>? whenHeaders,
    Duration? delay,
  ]) {
    adapter.onRoute(
      path,
      (server) => server.replyCallback(
        status,
        (options) {
          requests.add(options);
          return body is Object? Function(RequestOptions)
              ? body(options)
              : body;
        },
        headers: {
          Headers.contentTypeHeader: [Headers.jsonContentType],
          ...headers,
        },
        delay: delay,
      ),
      request: Request(
        route: path,
        method: RequestMethods.forName(name: method),
        data: Matchers.any,
        headers: whenHeaders,
      ),
    );
  }

  /// Pedidos a `METHOD path` que chegaram ao servidor.
  List<RequestOptions> calls(String method, String path) => [
    for (final request in requests)
      if (request.method == method && request.path == path) request,
  ];

  /// Erro RFC 7807 com `code` e propriedades extra.
  static Map<String, Object?> problem(
    int status,
    String code, {
    Map<String, Object?> extra = const {},
    List<Map<String, String>> errors = const [],
  }) => {
    'type': 'about:blank',
    'title': code,
    'status': status,
    'code': code,
    if (errors.isNotEmpty) 'errors': errors,
    ...extra,
  };
}

/// Respostas habituais da API.
abstract final class Fixtures {
  static Map<String, Object?> otpOptions({
    List<String> identifierTypes = const ['MSISDN', 'EMAIL'],
    bool customerCanChoose = true,
    List<String> msisdnChannels = const ['SMS', 'WHATSAPP'],
    int codeLength = 6,
  }) => {
    'identifierTypes': identifierTypes,
    'channels': {
      'MSISDN': msisdnChannels,
      'EMAIL': ['EMAIL'],
    },
    'defaultChannels': {'MSISDN': msisdnChannels.first, 'EMAIL': 'EMAIL'},
    'customerCanChoose': customerCanChoose,
    'codeLength': codeLength,
    'resendAfterSeconds': 30,
  };

  static Map<String, Object?> otpRequest({
    String challengeId = 'c1',
    String channel = 'SMS',
    bool fallbackUsed = false,
    int resendAfterSeconds = 30,
  }) => {
    'challengeId': challengeId,
    'channel': channel,
    'expiresAt': '2026-11-02T10:05:00Z',
    'fallbackUsed': fallbackUsed,
    'resendAfterSeconds': resendAfterSeconds,
  };

  static Map<String, Object?> challenge({
    String channel = 'SMS',
    bool fallbackUsed = false,
  }) => {
    'channel': channel,
    'deliveryStatus': 'SENT',
    'expiresAt': '2026-11-02T10:05:00Z',
    'fallbackUsed': fallbackUsed,
  };

  static Map<String, Object?> tokens({
    String access = 'access-1',
    String refresh = 'refresh-1',
    bool? isNewUser,
  }) => {
    'accessToken': access,
    'refreshToken': refresh,
    'expiresIn': 900,
    'isNewUser': ?isNewUser,
  };

  static Map<String, Object?> me({
    String? displayName = 'Ana Maria Machava',
    bool profileComplete = true,
  }) => {
    'id': '0190f0f0-0000-7000-8000-000000000001',
    'type': 'CUSTOMER',
    'displayName': ?displayName,
    'msisdn': '+258841234567',
    'msisdnVerified': true,
    'emailVerified': false,
    'permissions': <String>[],
    'roles': ['CUSTOMER'],
    'preferredLanguage': 'pt',
    'profileComplete': profileComplete,
  };

  static Map<String, Object?> profile({
    bool complete = false,
    int version = 0,
    String? fullName,
    String? birthDate,
    String? idDocType,
    String? idDocNumberMasked,
    String? nuitMasked,
    String? province,
  }) => {
    'complete': complete,
    'version': version,
    'fullName': ?fullName,
    'birthDate': ?birthDate,
    'idDocType': ?idDocType,
    'idDocNumberMasked': ?idDocNumberMasked,
    'nuitMasked': ?nuitMasked,
    'province': ?province,
  };

  static Map<String, Object?> contacts([
    List<Map<String, Object?>> contacts = const [
      {'type': 'MSISDN', 'value': '+258841234567', 'verified': true},
    ],
  ]) => {'contacts': contacts};
}

/// Ecrã de 360 px de largura (Definição de Pronto).
void useSmallPhone(WidgetTester tester) {
  tester.view.physicalSize = const Size(360, 740);
  tester.view.devicePixelRatio = 1;
  addTearDown(tester.view.reset);
}

/// Abre a app completa (router, sessão e apresentação) com a API simulada.
Future<ProviderContainer> pumpMoSeguros(
  WidgetTester tester,
  MockApi api, {
  bool onboardingDone = true,
  String? refreshToken,
  TokenStorage? storage,
  Locale locale = const Locale('pt'),
}) async {
  useSmallPhone(tester);
  SharedPreferences.setMockInitialValues({
    if (onboardingDone) OnboardingController.key: true,
  });
  final preferences = await SharedPreferences.getInstance();
  final container = ProviderContainer(
    overrides: [
      envProvider.overrideWithValue(testEnv),
      apiClientProvider.overrideWithValue(api.client),
      tokenStorageProvider.overrideWithValue(
        storage ?? MemoryTokenStorage(refreshToken),
      ),
      sharedPreferencesProvider.overrideWithValue(preferences),
    ],
  );
  addTearDown(container.dispose);
  await tester.pumpWidget(
    UncontrolledProviderScope(
      container: container,
      child: MoSegurosApp(locale: locale),
    ),
  );
  await tester.pumpAndSettle();
  return container;
}

/// Erro de um pedido com resposta [status] e [body] (para widgets testados sem a API).
DioException dioError(int status, Object? body) {
  final request = RequestOptions(path: '/test');
  return DioException.badResponse(
    statusCode: status,
    requestOptions: request,
    response: Response(requestOptions: request, statusCode: status, data: body),
  );
}
