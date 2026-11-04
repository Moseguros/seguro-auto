import 'package:flutter_test/flutter_test.dart';
import 'package:moseguros/features/quote/period.dart';

/// Fim do período (S3-02 §3.4), com os exemplos da S3-04.
void main() {
  test('1 ano a partir de 2027-03-01 → 2028-02-29 (ano bissexto)', () {
    expect(quoteEndDate(DateTime(2027, 3, 1), 365), DateTime(2028, 2, 29));
  });

  test('30 dias a partir de 2027-01-15 → 2027-02-13', () {
    expect(quoteEndDate(DateTime(2027, 1, 15), 30), DateTime(2027, 2, 13));
  });

  test('outros casos: 1 ano, 29 de Fevereiro e 7 dias', () {
    expect(quoteEndDate(DateTime(2027, 1, 15), 365), DateTime(2028, 1, 14));
    // Como o LocalDate.plusYears(1): 29/02/2028 → 28/02/2029, menos um dia.
    expect(quoteEndDate(DateTime(2028, 2, 29), 365), DateTime(2029, 2, 27));
    expect(quoteEndDate(DateTime(2027, 1, 1), 7), DateTime(2027, 1, 7));
    expect(quoteEndDate(DateTime(2027, 12, 31), 14), DateTime(2028, 1, 13));
  });
}
