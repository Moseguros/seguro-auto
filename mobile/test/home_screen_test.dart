import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mocktail/mocktail.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros_api/moseguros_api.dart';

class _MockApiClient extends Mock implements ApiClient {}

class _MockMetaApi extends Mock implements MetaApi {}

const _env = Env(apiBaseUrl: 'http://localhost:8080', name: 'test');
final _request = RequestOptions(path: '/api/v1/meta/version');

void main() {
  late _MockMetaApi meta;

  setUp(() {
    meta = _MockMetaApi();
  });

  Future<void> pumpApp(WidgetTester tester, {Locale? locale}) async {
    // Ecrã de 360 px de largura (Definição de Pronto).
    tester.view.physicalSize = const Size(360, 740);
    tester.view.devicePixelRatio = 1;
    addTearDown(tester.view.reset);

    final api = _MockApiClient();
    when(() => api.meta).thenReturn(meta);
    await tester.pumpWidget(
      ProviderScope(
        overrides: [
          envProvider.overrideWithValue(_env),
          apiClientProvider.overrideWithValue(api),
        ],
        child: MoSegurosApp(locale: locale),
      ),
    );
  }

  testWidgets('mostra nome, frase e a versão da API (sucesso)', (tester) async {
    when(() => meta.getVersion()).thenAnswer(
      (_) async => Response(
        requestOptions: _request,
        statusCode: 200,
        data: VersionResponse(
          (b) => b
            ..name = 'moseguros-backend'
            ..version = '1.2.3'
            ..commit = 'abc1234',
        ),
      ),
    );

    await pumpApp(tester, locale: const Locale('pt'));
    expect(find.byKey(const Key('system-status-loading')), findsOneWidget);
    expect(find.text('A verificar a API…'), findsOneWidget);

    await tester.pumpAndSettle();

    expect(find.text('MoSeguros'), findsWidgets);
    expect(
      find.text('Compare, compre e gira o seu seguro automóvel num só lugar.'),
      findsOneWidget,
    );
    expect(find.text('Estado do sistema'), findsOneWidget);
    expect(
      tester.widget<Text>(find.byKey(const Key('api-version'))).data,
      '1.2.3',
    );
    expect(
      tester.widget<Text>(find.byKey(const Key('api-commit'))).data,
      'abc1234',
    );
    expect(tester.takeException(), isNull);
  });

  testWidgets('mostra o erro e volta a tentar com sucesso', (tester) async {
    var calls = 0;
    when(() => meta.getVersion()).thenAnswer((_) async {
      calls++;
      if (calls == 1) {
        throw DioException.connectionError(
          requestOptions: _request,
          reason: 'connection refused',
        );
      }
      return Response(
        requestOptions: _request,
        statusCode: 200,
        data: VersionResponse((b) => b..version = '1.2.3'),
      );
    });

    await pumpApp(tester, locale: const Locale('pt'));
    await tester.pumpAndSettle();

    expect(find.byKey(const Key('system-status-error')), findsOneWidget);
    expect(
      find.textContaining('Não foi possível obter o estado da API.'),
      findsOneWidget,
    );
    expect(find.textContaining('Sem ligação ao servidor.'), findsOneWidget);

    await tester.tap(find.text('Tentar de novo'));
    await tester.pumpAndSettle();

    expect(calls, 2);
    expect(find.byKey(const Key('system-status-error')), findsNothing);
    expect(
      tester.widget<Text>(find.byKey(const Key('api-version'))).data,
      '1.2.3',
    );
    expect(
      tester.widget<Text>(find.byKey(const Key('api-commit'))).data,
      'desconhecido',
    );
  });

  testWidgets('traduz o código do ProblemDetail (en)', (tester) async {
    when(() => meta.getVersion()).thenThrow(
      DioException.badResponse(
        statusCode: 500,
        requestOptions: _request,
        response: Response(
          requestOptions: _request,
          statusCode: 500,
          data: <String, dynamic>{
            'type': 'about:blank',
            'title': 'Internal Server Error',
            'status': 500,
            'code': 'INTERNAL_ERROR',
          },
        ),
      ),
    );

    await pumpApp(tester, locale: const Locale('en'));
    await tester.pumpAndSettle();

    expect(
      find.text('Compare, buy and manage your car insurance in one place.'),
      findsOneWidget,
    );
    expect(
      find.textContaining('Could not get the API status.'),
      findsOneWidget,
    );
    expect(find.textContaining('Internal server error.'), findsOneWidget);
    expect(find.text('Try again'), findsOneWidget);
  });
}
