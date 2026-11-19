/// Formatação da entidade, da referência e do montante (S5-11 §4), iguais à web
/// (`web/src/features/payments/formatReference.ts`).
library;

final _nonDigits = RegExp(r'\D');
final _groups = RegExp(r'(\d{3})(?=\d)');

/// A referência sem espaços nem outros separadores (o que se copia).
String referenceCopyValue(String reference) =>
    reference.replaceAll(_nonDigits, '');

/// Referência Ponto24 (11 dígitos) em grupos de 3 a partir da esquerda:
/// `00548781108` → `005 487 811 08`.
String formatReference(String reference) => referenceCopyValue(
  reference,
).replaceAllMapped(_groups, (m) => '${m.group(1)} ');

/// O montante a copiar: só os algarismos e a vírgula (`299900` → `2999,00`).
String amountCopyValue(int cents) {
  final value = cents.abs();
  final units = value ~/ 100;
  final rest = (value % 100).toString().padLeft(2, '0');
  return '$units,$rest';
}
