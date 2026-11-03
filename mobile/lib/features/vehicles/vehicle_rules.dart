/// As mesmas regras do backend (`VehicleRules`, S2-03) e da web (`vehicleSchema.ts`,
/// S2-06). Os erros são códigos `validation*` (`FieldErrorCodes`), mais
/// `INVALID_MONEY`, que só existe nos ecrãs.
library;

import '../../core/format/money.dart';

const vehicleCategories = [
  'LIGHT_PASSENGER',
  'LIGHT_MIXED',
  'HEAVY',
  'MOTORCYCLE',
  'TRAILER',
  'BUS',
];

const vehicleUsages = [
  'PRIVATE',
  'RENTAL',
  'PASSENGER_TRANSPORT',
  'GOODS',
  'TAXI',
  'DRIVING_SCHOOL',
];

const trailer = 'TRAILER';
const heavy = 'HEAVY';
const motorcycle = 'MOTORCYCLE';

const maxPlateLength = 15;
const minYear = 1900;
const maxEngineCc = 30000;
const maxGrossWeightKg = 100000;

/// Maior valor comercial aceite pelo backend, em centavos.
const maxCommercialValueCents = 100000000000;

final _plateSeparators = RegExp(r'[\s-]');
final _chassisPattern = RegExp(r'^[A-Z0-9]{5,30}$');
final _digits = RegExp(r'^\d+$');

/// Maiúsculas, sem espaços nem hífens (só para contar o comprimento; a matrícula
/// envia-se como foi escrita e o servidor valida o formato).
String normalizePlate(String value) =>
    value.replaceAll(_plateSeparators, '').toUpperCase();

/// Maiúsculas, sem espaços nem hífens.
String normalizeChassis(String value) =>
    value.replaceAll(_plateSeparators, '').toUpperCase();

/// A cilindrada só existe fora de `TRAILER`.
bool hasEngine(String? category) => category != trailer;

/// O peso bruto é obrigatório em `HEAVY` e `TRAILER`.
bool requiresGrossWeight(String? category) =>
    category == heavy || category == trailer;

/// Lugares fixos (0) num reboque.
bool hasFixedSeats(String? category) => category == trailer;

int? _parseInt(String value) {
  final text = value.trim();
  return _digits.hasMatch(text) ? int.tryParse(text) : null;
}

String? plateError(String value) {
  final plate = normalizePlate(value);
  if (plate.isEmpty) return 'REQUIRED';
  if (plate.length > maxPlateLength) return 'INVALID_PLATE';
  return null;
}

String? chassisError(String value) {
  final chassis = normalizeChassis(value);
  if (chassis.isEmpty) return 'REQUIRED';
  if (!_chassisPattern.hasMatch(chassis)) return 'INVALID_CHASSIS';
  return null;
}

/// Marca ou modelo: 1–60 caracteres depois de `trim`.
String? makeOrModelError(String value) {
  final text = value.trim();
  if (text.isEmpty) return 'REQUIRED';
  if (text.length > 60) return 'OUT_OF_RANGE';
  return null;
}

/// Ano entre 1900 e o ano actual em Maputo + 1.
String? yearError(String value, int currentYear) {
  if (value.trim().isEmpty) return 'REQUIRED';
  final year = _parseInt(value);
  if (year == null || year < minYear || year > currentYear + 1) {
    return 'INVALID_YEAR';
  }
  return null;
}

String? categoryError(String? value) =>
    vehicleCategories.contains(value) ? null : 'REQUIRED';

String? usageError(String? value) =>
    vehicleUsages.contains(value) ? null : 'REQUIRED';

/// Cilindrada: obrigatória e 1–30 000, excepto em `TRAILER` (campo escondido).
String? engineCcError(String value, String? category) {
  if (!hasEngine(category)) return null;
  if (value.trim().isEmpty) return 'REQUIRED';
  final cc = _parseInt(value);
  if (cc == null || cc < 1 || cc > maxEngineCc) return 'OUT_OF_RANGE';
  return null;
}

/// Lugares: `TRAILER` 0; `MOTORCYCLE` 1–3; outras 1–100.
String? seatsError(String value, String? category) {
  if (value.trim().isEmpty) return 'REQUIRED';
  final seats = _parseInt(value);
  if (seats == null) return 'OUT_OF_RANGE';
  final (min, max) = switch (category) {
    trailer => (0, 0),
    motorcycle => (1, 3),
    _ => (1, 100),
  };
  return seats < min || seats > max ? 'OUT_OF_RANGE' : null;
}

/// Peso bruto: obrigatório em `HEAVY` e `TRAILER`; 1–100 000.
String? grossWeightError(String value, String? category) {
  if (value.trim().isEmpty) {
    return requiresGrossWeight(category) ? 'REQUIRED' : null;
  }
  final weight = _parseInt(value);
  if (weight == null || weight < 1 || weight > maxGrossWeightKg) {
    return 'OUT_OF_RANGE';
  }
  return null;
}

/// Valor comercial (opcional): > 0 e ≤ 1 000 000 000 MT, com no máximo 2 casas.
String? commercialValueError(String value) {
  final parsed = parseMeticais(value);
  if (parsed.invalid) return 'INVALID_MONEY';
  final cents = parsed.cents;
  if (cents == null) return null;
  if (cents <= 0 || cents > maxCommercialValueCents) return 'OUT_OF_RANGE';
  return null;
}

String? provinceOfCirculationError(String? value, List<String> provinces) =>
    provinces.contains(value) ? null : 'REQUIRED';

/// Número de um campo já validado (`null` quando vazio).
int? parseOptionalInt(String value) => _parseInt(value);
