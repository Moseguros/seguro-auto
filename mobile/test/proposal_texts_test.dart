import 'dart:convert';
import 'dart:io';

import 'package:flutter_test/flutter_test.dart';

/// `CLAIMS_LAST_3_YEARS` → `ClaimsLast3Years`; `SUIT_NEED` → `Need` (o prefixo
/// `SUIT_` das perguntas de adequação não entra no nome da chave).
String _pascal(String code) => code
    .replaceFirst(RegExp(r'^SUIT_'), '')
    .toLowerCase()
    .split('_')
    .map((w) => w.isEmpty ? '' : w[0].toUpperCase() + w.substring(1))
    .join();

/// `claimsCount` → `ClaimsCount`.
String _upperFirst(String value) => value[0].toUpperCase() + value.substring(1);

/// Chaves ARB que cada pergunta, resposta e aviso de `api/proposal-questions.json`
/// (S4-02 §11) exige na app.
List<String> requiredKeys(Map<String, dynamic> questions) => [
  for (final question in questions['declarations'] as List<dynamic>) ...[
    'declaration${_pascal((question as Map)['code'] as String)}Question',
    for (final followUp in question['followUp'] as List<dynamic>)
      'declaration${_pascal(question['code'] as String)}'
          '${_upperFirst(followUp as String)}',
  ],
  for (final question in questions['suitability'] as List<dynamic>) ...[
    'suitability${_pascal((question as Map)['code'] as String)}Question',
    for (final option in question['options'] as List<dynamic>)
      question['type'] == 'SINGLE_CHOICE'
          ? 'suitability${_pascal(question['code'] as String)}'
                'Option${_pascal(option as String)}'
          : 'suitabilityAnswer${_pascal(option as String)}',
  ],
  for (final reason in questions['suitabilityReasons'] as List<dynamic>)
    'suitabilityReason${_pascal(reason as String)}',
];

void main() {
  // `flutter test` corre na pasta mobile/.
  final questions =
      jsonDecode(File('../api/proposal-questions.json').readAsStringSync())
          as Map<String, dynamic>;
  final keys = requiredKeys(questions);

  test('api/proposal-questions.json tem perguntas, respostas e avisos', () {
    expect(keys, contains('declarationClaimsLast3YearsQuestion'));
    expect(keys, contains('declarationClaimsLast3YearsClaimsCount'));
    expect(keys, contains('suitabilityNeedOptionMandatoryOnly'));
    expect(keys, contains('suitabilityAnswerUnsure'));
    expect(keys, contains('suitabilityReasonNeedUnsure'));
  });

  for (final arb in ['app_pt.arb', 'app_en.arb']) {
    test('$arb tem todas as chaves das perguntas da proposta', () {
      final messages =
          jsonDecode(File('lib/l10n/$arb').readAsStringSync())
              as Map<String, dynamic>;
      expect([
        for (final key in keys)
          if (messages[key] is! String) key,
      ], isEmpty);
    });
  }

  test('as instruções dos ângulos existem nos dois idiomas', () {
    for (final arb in ['app_pt.arb', 'app_en.arb']) {
      final messages =
          jsonDecode(File('lib/l10n/$arb').readAsStringSync())
              as Map<String, dynamic>;
      for (final slot in [
        'Front',
        'Rear',
        'Left',
        'Right',
        'Odometer',
        'Chassis',
        'Extra',
      ]) {
        expect(messages['inspectionSlot$slot'], isA<String>(), reason: arb);
        expect(
          messages['inspectionInstruction$slot'],
          isA<String>(),
          reason: arb,
        );
      }
    }
  });
}
