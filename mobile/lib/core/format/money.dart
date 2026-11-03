/// Dinheiro em meticais: a API usa centavos (`{ amount, currency: "MZN" }`).
///
/// As mesmas regras da web (`web/src/format/money.ts`, ficha S2-05 §2).
library;

import 'package:intl/intl.dart';

/// Formato dos números: pt de Moçambique (o mesmo de Portugal, com espaço nos
/// milhares e vírgula decimal) ou inglês.
String _numberLocale(String languageCode) =>
    languageCode == 'en' ? 'en' : 'pt_PT';

/// Valor sem a moeda, com 2 casas decimais (ex.: `3 000 000,00`).
String formatAmount(int cents, String languageCode) =>
    NumberFormat.decimalPatternDigits(
      locale: _numberLocale(languageCode),
      decimalDigits: 2,
    ).format(cents / 100);

/// Valor com a moeda (ex.: `3 000 000,00 MT`).
String formatMoney(int cents, String languageCode) =>
    '${formatAmount(cents, languageCode)} MT';

/// Resultado de [parseMeticais]: `cents` é `null` com o texto vazio; `invalid`
/// quando o texto não é um valor com no máximo 2 casas decimais.
typedef ParsedMeticais = ({int? cents, bool invalid});

const ParsedMeticais _empty = (cents: null, invalid: false);
const ParsedMeticais _invalid = (cents: null, invalid: true);

final _spaces = RegExp(r'[\s  ]');
final _amount = RegExp(r'^(\d+)(?:,(\d{1,2}))?$');

/// Texto escrito pelo cliente → centavos, sem vírgula flutuante.
///
/// Tira espaços (incluindo os inseparáveis) e pontos de milhares; a vírgula é o
/// separador decimal, com no máximo 2 casas.
ParsedMeticais parseMeticais(String text) {
  final cleaned = text.replaceAll(_spaces, '').replaceAll('.', '');
  if (cleaned.isEmpty) return _empty;
  final match = _amount.firstMatch(cleaned);
  if (match == null) return _invalid;
  final units = BigInt.parse(match.group(1)!);
  final decimals = (match.group(2) ?? '').padRight(2, '0');
  final cents = units * BigInt.from(100) + BigInt.parse(decimals);
  // Acima do maior inteiro seguro não é um valor que faça sentido.
  if (cents > BigInt.from(9007199254740991)) return _invalid;
  return (cents: cents.toInt(), invalid: false);
}
