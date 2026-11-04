import 'dart:convert';
import 'dart:io';

import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros_api/moseguros_api.dart';

/// `NO_MATCHING_ROW` → `NoMatchingRow` (as chaves ARB usam o código em camelCase).
String _pascal(String code) => code
    .toLowerCase()
    .split('_')
    .map(
      (word) => word.isEmpty ? '' : word[0].toUpperCase() + word.substring(1),
    )
    .join();

/// Textos partilhados pelos ecrãs do S3 (S3-02 §16): um `quoteUnavailable<Reason>` por motivo de
/// `UnavailableReason` e um `coverage<Code>` por código de cobertura, nas duas línguas.
void main() {
  final reasons = [for (final r in UnavailableReason.values) r.name];
  final coverages = [
    for (final c in QuoteCoverageResponseCodeEnum.values) c.name,
  ];

  test('o contrato tem 13 motivos e 12 coberturas', () {
    expect(reasons, hasLength(13));
    expect(coverages, hasLength(12));
  });

  for (final arb in ['app_pt.arb', 'app_en.arb']) {
    // `flutter test` corre na pasta mobile/.
    final messages =
        jsonDecode(File('lib/l10n/$arb').readAsStringSync())
            as Map<String, dynamic>;

    test('todos os motivos têm quoteUnavailable<Reason> em $arb', () {
      final missing = [
        for (final reason in reasons)
          if (messages['quoteUnavailable${_pascal(reason)}'] is! String) reason,
      ];
      expect(missing, isEmpty);
    });

    test('todas as coberturas têm coverage<Code> em $arb', () {
      final missing = [
        for (final code in coverages)
          if (messages['coverage${_pascal(code)}'] is! String) code,
      ];
      expect(missing, isEmpty);
    });
  }
}
