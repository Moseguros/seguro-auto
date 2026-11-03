/// As mesmas regras do backend (`DriverRules`, S2-03) e da web (`driverSchema.ts`,
/// S2-06). Os erros são códigos `validation*`.
library;

import '../profile/profile_rules.dart' show ageInYears;

export '../profile/profile_rules.dart' show nameError;

/// Idade máxima de um condutor.
const maxDriverAge = 100;

final _spaces = RegExp(r'\s+');
final _licencePattern = RegExp(r'^[A-Z0-9/-]{5,20}$');

/// Maiúsculas e sem espaços.
String normalizeLicenceNumber(String value) =>
    value.replaceAll(_spaces, '').toUpperCase();

/// Data de nascimento mais antiga aceite: hoje − 100 anos.
DateTime earliestDriverBirthDate(DateTime today) =>
    DateTime(today.year - maxDriverAge, today.month, today.day);

/// No passado e idade ≤ 100 anos.
String? driverBirthDateError(DateTime? value, DateTime today) {
  if (value == null) return 'REQUIRED';
  final birth = DateTime(value.year, value.month, value.day);
  if (!birth.isBefore(today)) return 'INVALID_DATE';
  if (ageInYears(birth, today) > maxDriverAge) return 'INVALID_DATE';
  return null;
}

/// `^[A-Z0-9/-]{5,20}$` depois de normalizar. Ao alterar um condutor sem mexer
/// na carta, o campo não aparece e não se valida.
String? licenceNumberError(String value, {required bool required}) {
  final licence = normalizeLicenceNumber(value);
  if (licence.isEmpty) return required ? 'REQUIRED' : null;
  if (!_licencePattern.hasMatch(licence)) return 'INVALID_LICENCE_NUMBER';
  return null;
}

/// Pelo menos uma categoria.
String? licenceCategoriesError(Iterable<String> categories) =>
    categories.isEmpty ? 'REQUIRED' : null;

/// ≥ data de nascimento e ≤ hoje.
String? licenceIssueDateError(
  DateTime? value,
  DateTime? birthDate,
  DateTime today,
) {
  if (value == null) return 'REQUIRED';
  final issue = DateTime(value.year, value.month, value.day);
  if (issue.isAfter(today)) return 'LICENCE_ISSUE_DATE_INVALID';
  if (birthDate != null &&
      issue.isBefore(
        DateTime(birthDate.year, birthDate.month, birthDate.day),
      )) {
    return 'LICENCE_ISSUE_DATE_INVALID';
  }
  return null;
}
