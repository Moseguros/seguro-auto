import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:go_router/go_router.dart';
import 'package:moseguros/app.dart';
import 'package:moseguros/core/api/api_client.dart';
import 'package:moseguros/core/api/api_error.dart';
import 'package:moseguros/core/config/env.dart';
import 'package:moseguros/core/l10n/province_name.dart';
import 'package:moseguros/core/router.dart';
import 'package:moseguros/core/theme.dart';
import 'package:moseguros/core/widgets/cta_button.dart';
import 'package:moseguros/l10n/app_localizations.dart';

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

    test('todos os códigos têm tradução em pt e en', () async {
      const codes = [
        'NETWORK_ERROR',
        'VALIDATION_ERROR',
        'NOT_FOUND',
        'UNAUTHORIZED',
        'FORBIDDEN',
        'INTERNAL_ERROR',
        'INVALID_CURRENCY',
        'INVALID_MSISDN',
        'INVALID_NUIT',
        'INVALID_PLATE',
        'INVALID_EMAIL',
        'INVALID_PHONE',
        'TENANT_REQUIRED',
        'DECRYPTION_FAILED',
        'INVALID_STATE_TRANSITION',
        'LICENSE_NUMBER_REQUIRED',
        'CHANNELS_EMPTY',
        'CHANNELS_DUPLICATED',
        'DEFAULT_CHANNEL_NOT_ENABLED',
        'FALLBACK_ORDER_MISMATCH',
        'OUT_OF_RANGE',
        'REFRESH_TOKEN_INVALID',
        'REFRESH_TOKEN_REUSED',
        'CONCURRENT_MODIFICATION',
        'OTP_CHANNEL_CHOICE_DISABLED',
        'OTP_CHANNEL_NOT_AVAILABLE',
        'OTP_RATE_LIMITED',
        'OTP_RESEND_TOO_SOON',
        'OTP_DELIVERY_FAILED',
        'OTP_CHALLENGE_NOT_FOUND',
        'OTP_ALREADY_USED',
        'OTP_SUPERSEDED',
        'OTP_EXPIRED',
        'OTP_INVALID',
        'OTP_ATTEMPTS_EXCEEDED',
        'ACCOUNT_DISABLED',
        'ORGANIZATION_NOT_FOUND',
        'ORGANIZATION_NOT_ACTIVE',
        'NUIT_ALREADY_IN_USE',
        'SHORT_CODE_ALREADY_IN_USE',
        'OMBUDSMAN_INVALID',
        'ROLE_NOT_ALLOWED_FOR_ORGANIZATION',
        'EMAIL_ALREADY_IN_USE',
        'INVITATION_INVALID',
        'INVITATION_NOT_ACCEPTED',
        'TOTP_INVALID',
        'PASSWORD_TOO_WEAK',
      ];
      const fieldCodes = [
        'INVALID_CURRENCY',
        'INVALID_MSISDN',
        'INVALID_NUIT',
        'INVALID_PLATE',
        'INVALID_EMAIL',
        'INVALID_PHONE',
        'LICENSE_NUMBER_REQUIRED',
        'CHANNELS_EMPTY',
        'CHANNELS_DUPLICATED',
        'DEFAULT_CHANNEL_NOT_ENABLED',
        'FALLBACK_ORDER_MISMATCH',
        'OUT_OF_RANGE',
        'NOT_BLANK',
        'NOT_NULL',
        'NOT_ALLOWED',
        'REQUIRED',
        'INVALID_SORT',
        'TOO_SHORT',
        'TOO_LONG',
        'CONTAINS_EMAIL',
        'TOO_COMMON',
        'SIZE',
        'PATTERN',
        'TYPE_MISMATCH',
      ];
      for (final locale in AppLocalizations.supportedLocales) {
        final l10n = await AppLocalizations.delegate.load(locale);
        for (final code in codes) {
          expect(
            apiErrorMessage(l10n, code),
            isNot(l10n.errorUnknown),
            reason: '$locale $code',
          );
        }
        for (final code in provinceCodes) {
          expect(
            provinceName(l10n, code),
            isNot(code),
            reason: '$locale $code',
          );
        }
        for (final code in fieldCodes) {
          expect(
            fieldErrorMessage(l10n, code),
            isNot(l10n.errorUnknown),
            reason: '$locale $code',
          );
        }
      }
    });
  });

  testWidgets('rota desconhecida mostra a página de erro', (tester) async {
    final container = ProviderContainer(
      overrides: [
        envProvider.overrideWithValue(
          const Env(apiBaseUrl: 'http://localhost:1', name: 'test'),
        ),
        apiClientProvider.overrideWithValue(
          ApiClient(const Env(apiBaseUrl: 'http://localhost:1', name: 'test')),
        ),
      ],
    );
    addTearDown(container.dispose);

    await tester.pumpWidget(
      UncontrolledProviderScope(
        container: container,
        child: const MoSegurosApp(locale: Locale('pt')),
      ),
    );
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
