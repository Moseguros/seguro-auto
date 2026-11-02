/// As mesmas regras do backend (`CustomerProfileRules`, S1-06) e da web
/// (`profileSchema.ts`). Os erros são códigos `validation*`.
library;

const idDocTypes = ['BI', 'PASSPORT', 'DIRE'];

const minAge = 18;
const maxAge = 120;

final _namePattern = RegExp(r"^[\p{L}\p{M}' -]+$", unicode: true);
final _letterPattern = RegExp(r'\p{L}', unicode: true);
final _documentPattern = RegExp(r'^[A-Z0-9]{5,20}$');
final _nuitPattern = RegExp(r'^\d{9}$');
final _spaces = RegExp(r'\s+');

/// Sem espaços nas pontas e um só espaço entre palavras.
String normalizeName(String value) => value.trim().replaceAll(_spaces, ' ');

/// Maiúsculas e sem espaços.
String normalizeDocumentNumber(String value) =>
    value.replaceAll(_spaces, '').toUpperCase();

String normalizeNuit(String value) => value.replaceAll(_spaces, '');

bool isValidName(String value) {
  final name = normalizeName(value);
  if (name.length < 2 || name.length > 120 || !_namePattern.hasMatch(name)) {
    return false;
  }
  final words = name.split(' ');
  return words.length >= 2 && words.every(_letterPattern.hasMatch);
}

/// Data de hoje em `Africa/Maputo` (UTC+2, sem hora de Verão).
DateTime todayInMaputo([DateTime? now]) {
  final maputo = (now ?? DateTime.now()).toUtc().add(const Duration(hours: 2));
  return DateTime(maputo.year, maputo.month, maputo.day);
}

/// Data de nascimento mais recente aceite: hoje − 18 anos.
DateTime latestBirthDate(DateTime today) =>
    DateTime(today.year - minAge, today.month, today.day);

/// Data de nascimento mais antiga aceite: hoje − 120 anos.
DateTime earliestBirthDate(DateTime today) =>
    DateTime(today.year - maxAge, today.month, today.day);

/// Anos completos entre duas datas (como `Period.between(...).getYears()`).
int ageInYears(DateTime birth, DateTime today) {
  var years = today.year - birth.year;
  if (today.month < birth.month ||
      (today.month == birth.month && today.day < birth.day)) {
    years -= 1;
  }
  return years;
}

String? nameError(String value) {
  if (normalizeName(value).isEmpty) return 'REQUIRED';
  if (!isValidName(value)) return 'INVALID_NAME';
  return null;
}

String? birthDateError(DateTime? value, DateTime today) {
  if (value == null) return 'REQUIRED';
  final birth = DateTime(value.year, value.month, value.day);
  if (birth.isAfter(today)) return 'UNDERAGE';
  final age = ageInYears(birth, today);
  if (age < minAge) return 'UNDERAGE';
  if (age > maxAge) return 'INVALID_DATE';
  return null;
}

String? idDocTypeError(String? value) =>
    idDocTypes.contains(value) ? null : 'REQUIRED';

String? documentNumberError(String value, {required bool required}) {
  final document = normalizeDocumentNumber(value);
  if (document.isEmpty) return required ? 'REQUIRED' : null;
  if (!_documentPattern.hasMatch(document)) return 'INVALID_DOCUMENT_NUMBER';
  return null;
}

String? nuitError(String value, {required bool required}) {
  final nuit = normalizeNuit(value);
  if (nuit.isEmpty) return required ? 'REQUIRED' : null;
  if (!_nuitPattern.hasMatch(nuit)) return 'INVALID_NUIT';
  return null;
}

String? addressError(String value) => value.trim().length > 300 ? 'SIZE' : null;

String? provinceError(String? value, List<String> provinces) =>
    provinces.contains(value) ? null : 'REQUIRED';
