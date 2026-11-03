/// Regras do pedido de cotação que se aplicam ao cliente (S3-02 §3), iguais às
/// do backend (`QuoteRequestValidator`) e da web (`quoteRules.ts`, S3-04 §9).
/// Os erros são códigos de `FieldErrorCodes` (`validation*` no ARB). O backend
/// continua a ser quem decide.
library;

/// Tipos de produto que se podem cotar no S3.
abstract final class QuoteProductTypes {
  static const mtplOnly = 'MTPL_ONLY';
  static const comprehensive = 'COMPREHENSIVE';
  static const temporary = 'TEMPORARY';

  static const all = [mtplOnly, comprehensive, temporary];
}

/// Códigos das coberturas (S2-04), pela ordem em que aparecem nos ecrãs.
abstract final class CoverageCodes {
  static const mtpl = 'MTPL';
  static const vtpl = 'VTPL';
  static const odCollision = 'OD_COLLISION';
  static const odGlass = 'OD_GLASS';
  static const odFire = 'OD_FIRE';
  static const odNature = 'OD_NATURE';
  static const odTheft = 'OD_THEFT';
  static const occupants = 'OCCUPANTS';
  static const assistance = 'ASSISTANCE';
  static const legal = 'LEGAL';
  static const srcc = 'SRCC';
  static const crossBorder = 'CROSS_BORDER';

  static const all = [
    mtpl,
    vtpl,
    odCollision,
    odGlass,
    odFire,
    odNature,
    odTheft,
    occupants,
    assistance,
    legal,
    srcc,
    crossBorder,
  ];

  /// O que "Furto ou roubo" exige (regra fixa do catálogo do S2).
  static const theftRequires = [odCollision, odFire, odNature];

  static bool isOwnDamage(String code) => code.startsWith('OD_');
}

/// Dias de antecedência máxima da data de início (`max-start-days-ahead` do
/// backend; provisório, S3-02 Questões).
const quoteMaxStartDaysAhead = 60;

/// Duração de um ano.
const oneYearDays = 365;

/// Durações do seguro temporário.
const temporaryDurations = [7, 14, 30, 60, oneYearDays];

/// Regra 1: só `MTPL_ONLY`, `COMPREHENSIVE` e `TEMPORARY`.
String? productTypeError(String? type) {
  if (type == null) return 'REQUIRED';
  return QuoteProductTypes.all.contains(type) ? null : 'NOT_ALLOWED';
}

/// Durações permitidas para um tipo (regra 2).
List<int> durationsFor(String? type) => type == QuoteProductTypes.temporary
    ? temporaryDurations
    : const [oneYearDays];

/// Regra 2: `TEMPORARY` → 7, 14, 30, 60 ou 365; os outros → 365.
String? durationError(String? type, int? days) {
  if (days == null) return 'REQUIRED';
  return durationsFor(type).contains(days) ? null : 'DURATION_NOT_ALLOWED';
}

DateTime _day(DateTime value) => DateTime(value.year, value.month, value.day);

/// Última data de início aceite: hoje + [quoteMaxStartDaysAhead].
DateTime latestStartDate(DateTime today) =>
    DateTime(today.year, today.month, today.day + quoteMaxStartDaysAhead);

/// Regra 3: entre hoje e hoje + 60 dias, com "hoje" em `Africa/Maputo`.
String? startDateError(DateTime? start, DateTime today) {
  if (start == null) return 'REQUIRED';
  final day = _day(start);
  if (day.isBefore(_day(today)) || day.isAfter(latestStartDate(today))) {
    return 'START_DATE_OUT_OF_RANGE';
  }
  return null;
}

/// Regra 5 sobre a lista de coberturas pedidas (códigos, pela ordem do pedido).
/// Devolve o primeiro erro, ou `null`.
String? coveragesError(String? type, List<String> codes) {
  final seen = <String>{};
  for (final code in codes) {
    if (!CoverageCodes.all.contains(code)) return 'NOT_ALLOWED';
    if (!seen.add(code)) return 'DUPLICATE_COVERAGE';
  }
  final hasOwnDamage = seen.any(CoverageCodes.isOwnDamage);
  if (hasOwnDamage && type != QuoteProductTypes.comprehensive) {
    return 'COVERAGE_NOT_ALLOWED_FOR_TYPE';
  }
  if (type == QuoteProductTypes.comprehensive && !hasOwnDamage) {
    return 'OWN_DAMAGE_REQUIRED';
  }
  if (seen.contains(CoverageCodes.odTheft) &&
      !CoverageCodes.theftRequires.every(seen.contains)) {
    return 'COVERAGE_REQUIRES';
  }
  return null;
}

/// Regra 5: o capital, quando vem, é `> 0`.
String? capitalError(int? cents) =>
    cents != null && cents <= 0 ? 'OUT_OF_RANGE' : null;

/// Regra 6: há `OD_*` e o veículo não tem valor comercial → `VEHICLE_VALUE_REQUIRED`
/// (código de topo).
bool needsCommercialValue(Iterable<String> codes) =>
    codes.any(CoverageCodes.isOwnDamage);

/// `COMPREHENSIVE` exige o valor comercial do veículo (o cartão fica desactivado).
bool typeAllowedForVehicle(String type, {required bool hasCommercialValue}) =>
    type != QuoteProductTypes.comprehensive || hasCommercialValue;

/// Coberturas marcadas por defeito em `COMPREHENSIVE` (se o catálogo as tiver).
List<String> defaultCoverages(String? type, Iterable<String> available) => [
  if (type == QuoteProductTypes.comprehensive)
    for (final code in CoverageCodes.theftRequires)
      if (available.contains(code)) code,
];

/// Marca ou desmarca [code] com as regras automáticas de "Furto ou roubo"
/// (S3-02 §3.5, S3-04 §2):
/// - marcar furto marca choque, incêndio e fenómenos da natureza;
/// - desmarcar uma destas três desmarca o furto.
Set<String> toggleCoverage(
  Set<String> selected,
  String code, {
  required bool checked,
}) {
  final next = {...selected};
  if (checked) {
    next.add(code);
    if (code == CoverageCodes.odTheft) next.addAll(CoverageCodes.theftRequires);
  } else {
    next.remove(code);
    if (CoverageCodes.theftRequires.contains(code)) {
      next.remove(CoverageCodes.odTheft);
    }
  }
  return next;
}
