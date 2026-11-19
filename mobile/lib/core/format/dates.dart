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

/// Data de um instante no calendário de `Africa/Maputo`.
DateTime dateInMaputo(DateTime instant) => todayInMaputo(instant);

String _two(int value) => value.toString().padLeft(2, '0');

/// Data no formato `dd/MM/yyyy` (só o ano, o mês e o dia de [date]).
String formatDate(DateTime date) =>
    '${_two(date.day)}/${_two(date.month)}/${date.year}';

/// Instante apresentado como data `dd/MM/yyyy` em `Africa/Maputo`.
String formatInstantDate(DateTime instant) => formatDate(dateInMaputo(instant));

/// Instante apresentado como hora `HH:mm` em `Africa/Maputo`.
String formatInstantTime(DateTime instant) {
  final maputo = instant.toUtc().add(_maputoOffset);
  return '${_two(maputo.hour)}:${_two(maputo.minute)}';
}

/// Instante apresentado como `dd/MM/yyyy HH:mm` em `Africa/Maputo`.
String formatInstantDateTime(DateTime instant) =>
    '${formatInstantDate(instant)} ${formatInstantTime(instant)}';
