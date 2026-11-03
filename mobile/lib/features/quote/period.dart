/// Fim do período de uma cotação (S3-02 §3.4), igual ao `period.ts` da web.
library;

import 'quote_rules.dart';

bool _isLeap(int year) => (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;

/// `LocalDate.plusYears(1)`: 29 de Fevereiro passa a 28 num ano comum.
DateTime _plusOneYear(DateTime date) {
  final year = date.year + 1;
  final day = date.month == 2 && date.day == 29 && !_isLeap(year)
      ? 28
      : date.day;
  return DateTime(year, date.month, day);
}

/// Data de fim (inclusiva): 1 ano → mais um ano menos um dia; senão
/// início + dias − 1.
DateTime quoteEndDate(DateTime start, int durationDays) {
  if (durationDays == oneYearDays) {
    final next = _plusOneYear(start);
    return DateTime(next.year, next.month, next.day - 1);
  }
  return DateTime(start.year, start.month, start.day + durationDays - 1);
}
