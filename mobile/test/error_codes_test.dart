import 'dart:convert';
import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/api/api_error.dart';
import 'package:moseguros/l10n/app_localizations.dart';

/// `OTP_INVALID` → `OtpInvalid` (o nome da chave ARB é o código em camelCase).
String _pascal(String code) => code
    .toLowerCase()
    .split('_')
    .map(
      (word) => word.isEmpty ? '' : word[0].toUpperCase() + word.substring(1),
    )
    .join();

void main() {
  // `flutter test` corre na pasta mobile/.
  final codes =
      jsonDecode(File('../api/error-codes.json').readAsStringSync())
          as Map<String, dynamic>;
  final errors = [
    for (final error in codes['errors'] as List<dynamic>)
      (error as Map<String, dynamic>)['code'] as String,
  ];
  final validation = (codes['validation'] as List<dynamic>).cast<String>();

  test('api/error-codes.json não está vazio', () {
    expect(errors, isNotEmpty);
    expect(validation, isNotEmpty);
  });

  for (final arb in ['app_pt.arb', 'app_en.arb']) {
    test('todos os códigos têm error<Code>/validation<Code> em $arb', () {
      final messages =
          jsonDecode(File('lib/l10n/$arb').readAsStringSync())
              as Map<String, dynamic>;
      final missing = [
        for (final code in errors)
          if (messages['error${_pascal(code)}'] is! String)
            'error${_pascal(code)}',
        for (final code in validation)
          if (messages['validation${_pascal(code)}'] is! String)
            'validation${_pascal(code)}',
      ];
      expect(missing, isEmpty);
    });
  }

  test('a app traduz todos os códigos (nenhum cai em errorUnknown)', () async {
    for (final locale in AppLocalizations.supportedLocales) {
      final l10n = await AppLocalizations.delegate.load(locale);
      for (final code in errors) {
        expect(
          apiErrorMessage(l10n, code),
          isNot(l10n.errorUnknown),
          reason: '$locale $code',
        );
      }
      for (final code in validation) {
        expect(
          fieldErrorMessage(l10n, code),
          isNot(l10n.errorUnknown),
          reason: '$locale $code',
        );
      }
      expect(apiErrorMessage(l10n, 'SOMETHING_NEW'), l10n.errorUnknown);
    }
  });
}
