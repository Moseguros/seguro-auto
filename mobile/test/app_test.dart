import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/api/api_error.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros/core/errors/problem.dart';
import 'package:moseguros/core/l10n/province_name.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/core/theme.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/l10n/app_localizations.dart';

import 'support/mock_api.dart';

void main() {
  group('resolveLocale', () {
    const supported = [Locale('en'), Locale('pt')];

    test('usa a língua do dispositivo quando suportada', () {
      expect(
        resolveLocale(const Locale('en', 'US'), supported),
        const Locale('en'),
      );
      expect(
        resolveLocale(const Locale('pt', 'MZ'), supported),
        const Locale('pt'),
      );
    });

    test('cai para português nas restantes', () {
      expect(resolveLocale(const Locale('fr'), supported), const Locale('pt'));
      expect(resolveLocale(null, supported), const Locale('pt'));
    });
  });

  group('ApiClient', () {
    test('usa o baseUrl do Env e timeout de 15 s', () {
      final client = ApiClient(
        const Env(apiBaseUrl: 'http://10.0.2.2:8080', name: 'dev'),
      );
      expect(client.dio.options.baseUrl, 'http://10.0.2.2:8080');
      expect(client.dio.options.connectTimeout, const Duration(seconds: 15));
      expect(client.dio.options.receiveTimeout, const Duration(seconds: 15));
      expect(client.dio.options.sendTimeout, const Duration(seconds: 15));
    });

    test('Env.fromDefine exige API_BASE_URL', () {
      expect(() => Env.fromDefine('staging'), throwsStateError);
    });
  });

  group('apiErrorCode', () {
    final request = RequestOptions(path: '/x');

    test('sem resposta é NETWORK_ERROR', () {
      expect(
        apiErrorCode(
          DioException.connectionTimeout(
            timeout: const Duration(seconds: 15),
            requestOptions: request,
          ),
        ),
        'NETWORK_ERROR',
      );
    });

    test('lê o code do ProblemDetail (mapa ou texto JSON)', () {
      for (final data in [
        <String, dynamic>{'code': 'FORBIDDEN'},
        '{"code":"FORBIDDEN"}',
      ]) {
        expect(
          apiErrorCode(
            DioException.badResponse(
              statusCode: 403,
              requestOptions: request,
              response: Response(
                requestOptions: request,
                statusCode: 403,
                data: data,
              ),
            ),
          ),
          'FORBIDDEN',
        );
      }
    });

    test('outros erros são UNKNOWN', () {
      expect(apiErrorCode(StateError('x')), 'UNKNOWN');
      expect(
        apiErrorCode(
          DioException.badResponse(
            statusCode: 502,
            requestOptions: request,
            response: Response(
              requestOptions: request,
              statusCode: 502,
              data: '<html>',
            ),
          ),
        ),
        'UNKNOWN',
      );
    });

    test('todas as províncias têm nome em pt e en', () async {
      for (final locale in AppLocalizations.supportedLocales) {
        final l10n = await AppLocalizations.delegate.load(locale);
        for (final code in provinceCodes) {
          expect(
            provinceName(l10n, code),
            isNot(code),
            reason: '$locale $code',
          );
        }
      }
    });
  });

  group('Problem', () {
    final request = RequestOptions(path: '/x');

    Problem read(
      int status,
      Object? data, [
      Map<String, List<String>>? headers,
    ]) => Problem.from(
      DioException.badResponse(
        statusCode: status,
        requestOptions: request,
        response: Response(
          requestOptions: request,
          statusCode: status,
          data: data,
          headers: Headers.fromMap(headers ?? {}),
        ),
      ),
    );

    test('lê code, errors, retryAfterSeconds e attemptsRemaining', () {
      final problem = read(400, {
        'code': 'OTP_INVALID',
        'detail': 'Wrong code',
        'attemptsRemaining': 2,
        'errors': [
          {'field': 'code', 'code': 'PATTERN'},
          {'field': 1, 'code': 'X'},
        ],
      });
      expect(problem.status, 400);
      expect(problem.code, 'OTP_INVALID');
      expect(problem.detail, 'Wrong code');
      expect(problem.attemptsRemaining, 2);
      expect(problem.errors.single.field, 'code');
      expect(problem.errors.single.code, 'PATTERN');
      expect(problem.fieldError(['code'])?.code, 'PATTERN');
      expect(problem.fieldError(['other']), isNull);
    });

    test('retryAfterSeconds do corpo ou do cabeçalho Retry-After', () {
      expect(
        read(429, {
          'code': 'OTP_RATE_LIMITED',
          'retryAfterSeconds': 42,
        }).retryAfterSeconds,
        42,
      );
      expect(
        read(429, '{"code":"OTP_RATE_LIMITED"}', {
          'retry-after': ['17'],
        }).retryAfterSeconds,
        17,
      );
    });

    test('mensagem pelo código; desconhecido usa errorUnknown', () async {
      final l10n = await AppLocalizations.delegate.load(const Locale('pt'));
      expect(
        read(429, {'code': 'OTP_RATE_LIMITED'}).message(l10n),
        'Pediu demasiados códigos. Tente mais tarde.',
      );
      expect(read(400, {'code': 'NEW_CODE'}).message(l10n), l10n.errorUnknown);
      expect(Problem.from(StateError('x')).message(l10n), l10n.errorUnknown);
    });
  });

  testWidgets('rota desconhecida mostra a página de erro', (tester) async {
    final api = MockApi()
      ..on('POST', '/api/v1/auth/refresh', 200, Fixtures.tokens())
      ..on('GET', '/api/v1/me', 200, Fixtures.me());
    final container = await pumpMoSeguros(tester, api, refreshToken: 'r0');

    final GoRouter router = container.read(routerProvider);
    router.go('/xyz');
    await tester.pumpAndSettle();

    expect(find.text('Página não encontrada'), findsOneWidget);
    expect(find.text('Voltar ao início'), findsOneWidget);
  });

  testWidgets('CtaButton é laranja com texto azul', (tester) async {
    await tester.pumpWidget(
      MaterialApp(
        theme: AppTheme.light,
        home: Scaffold(
          body: CtaButton(label: 'Simular seguro', onPressed: () {}),
        ),
      ),
    );
    final button = tester.widget<FilledButton>(find.byType(FilledButton));
    final style = button.style!;
    expect(style.backgroundColor!.resolve({}), BrandColors.accent);
    expect(style.foregroundColor!.resolve({}), BrandColors.primary);
  });

  test('ColorScheme explícito com as cores da marca', () {
    final light = AppTheme.light;
    expect(light.colorScheme.primary, const Color(0xFF142D4E));
    expect(light.colorScheme.secondary, const Color(0xFFF28C28));
    expect(light.colorScheme.onSecondary, const Color(0xFF142D4E));
    expect(light.colorScheme.surface, const Color(0xFFFFFFFF));
    expect(light.scaffoldBackgroundColor, const Color(0xFFE8EFF6));

    final dark = AppTheme.dark;
    expect(dark.colorScheme.primary, const Color(0xFF9DB8D9));
    expect(dark.colorScheme.secondary, const Color(0xFFF28C28));
    expect(dark.colorScheme.onSecondary, const Color(0xFF0E1A2B));
    expect(dark.colorScheme.surface, const Color(0xFF16263B));
    expect(dark.scaffoldBackgroundColor, const Color(0xFF0E1A2B));
  });
}
