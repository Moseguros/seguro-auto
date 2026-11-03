import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/core/format/dates.dart';
import 'package:moseguros/core/format/money.dart';

void main() {
  group('parseMeticais (casos da ficha S2-05 §2)', () {
    for (final (text, cents) in [
      ('3 000 000', 300000000),
      ('3.000.000,50', 300000050),
      ('3000000,5', 300000050),
      ('0,01', 1),
    ]) {
      test('"$text" → $cents', () {
        final parsed = parseMeticais(text);
        expect(parsed.invalid, isFalse);
        expect(parsed.cents, cents);
      });
    }

    for (final text in ['1,234', 'abc']) {
      test('"$text" → inválido', () {
        expect(parseMeticais(text).invalid, isTrue);
        expect(parseMeticais(text).cents, isNull);
      });
    }

    test('vazio → null', () {
      expect(parseMeticais(''), (cents: null, invalid: false));
      expect(parseMeticais('   '), (cents: null, invalid: false));
    });

    test('espaços inseparáveis (os do formato pt) são aceites', () {
      expect(parseMeticais('3 000 000,00').cents, 300000000);
    });

    test('vírgula sem casas, sinal e duas vírgulas são inválidos', () {
      expect(parseMeticais('12,').invalid, isTrue);
      expect(parseMeticais('-5').invalid, isTrue);
      expect(parseMeticais('1,2,3').invalid, isTrue);
    });
  });

  group('formatMoney', () {
    test('2 casas decimais e "MT"; o resultado volta a ler-se igual', () {
      final pt = formatMoney(300000000, 'pt');
      expect(pt, endsWith(',00 MT'));
      expect(pt.replaceAll(RegExp(r'[\s  ]'), ''), '3000000,00MT');
      expect(parseMeticais(formatAmount(300000050, 'pt')).cents, 300000050);
      expect(formatMoney(85000000, 'en'), '850,000.00 MT');
      expect(formatMoney(1, 'pt'), '0,01 MT');
    });
  });

  group('datas em Africa/Maputo (UTC+2 fixo)', () {
    test('hoje e ano mudam às 22:00 UTC', () {
      expect(
        todayInMaputo(DateTime.utc(2026, 12, 31, 21, 59)),
        DateTime(2026, 12, 31),
      );
      expect(
        todayInMaputo(DateTime.utc(2026, 12, 31, 22, 30)),
        DateTime(2027, 1, 1),
      );
      expect(currentYearInMaputo(DateTime.utc(2026, 12, 31, 22, 30)), 2027);
      expect(currentYearInMaputo(DateTime.utc(2026, 12, 31, 21, 0)), 2026);
    });
  });
}
