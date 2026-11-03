/// Cálculos de datas em `Africa/Maputo`.
///
/// Moçambique está em UTC+2 todo o ano (sem hora de Verão), por isso basta somar
/// duas horas ao instante UTC; não é preciso uma base de dados de fusos.
library;

const _maputoOffset = Duration(hours: 2);

DateTime _maputoNow(DateTime? now) =>
    (now ?? DateTime.now()).toUtc().add(_maputoOffset);

/// Data de hoje em `Africa/Maputo` (só ano, mês e dia).
DateTime todayInMaputo([DateTime? now]) {
  final maputo = _maputoNow(now);
  return DateTime(maputo.year, maputo.month, maputo.day);
}

/// Ano actual em `Africa/Maputo`.
int currentYearInMaputo([DateTime? now]) => _maputoNow(now).year;
