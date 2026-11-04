/// Coberturas que o cliente pode escolher, a partir do catálogo público
/// (`listCatalogProducts`, S2-04), como o `coverageOptions.ts` da web (S3-04 §2).
library;

import 'package:moseguros_api/moseguros_api.dart';

import 'quote_rules.dart';

/// Tipo de capital de uma cobertura no catálogo.
abstract final class CapitalTypes {
  static const fixed = 'FIXED';
  static const vehicleValue = 'VEHICLE_VALUE';
  static const options = 'OPTIONS';
  static const perPerson = 'PER_PERSON';
}

/// Uma cobertura na união dos produtos do tipo escolhido.
class CoverageOption {
  const CoverageOption({
    required this.code,
    required this.mandatory,
    required this.availableIn,
    required this.total,
    required this.capitalType,
    this.capitalOptions = const [],
    this.capital,
  });

  final String code;

  /// Obrigatória em todos os produtos (aparece marcada e desactivada).
  final bool mandatory;

  /// Em quantos produtos existe.
  final int availableIn;

  /// Número de produtos do tipo.
  final int total;

  /// Tipo de capital do primeiro produto que a tem.
  final String capitalType;

  /// `OPTIONS`: união dos capitais (centavos), por ordem crescente.
  final List<int> capitalOptions;

  /// Capital fixo ou por pessoa (o menor entre os produtos), em centavos.
  final int? capital;

  bool get hasCapitalChoice =>
      capitalType == CapitalTypes.options && capitalOptions.isNotEmpty;

  /// Capital por defeito: o menor das opções.
  int? get defaultCapital => hasCapitalChoice ? capitalOptions.first : null;
}

/// Resultado de [buildCoverageOptions].
class CoverageOptions {
  const CoverageOptions({
    required this.total,
    required this.mandatory,
    required this.optional,
  });

  /// Produtos do tipo escolhido para o veículo.
  final int total;
  final List<CoverageOption> mandatory;
  final List<CoverageOption> optional;

  bool get isEmpty => total == 0;

  Iterable<CoverageOption> get all => [...mandatory, ...optional];

  CoverageOption? find(String code) {
    for (final option in all) {
      if (option.code == code) return option;
    }
    return null;
  }

  Set<String> get optionalCodes => {for (final o in optional) o.code};
}

int _codeOrder(String code) {
  final index = CoverageCodes.all.indexOf(code);
  return index < 0 ? CoverageCodes.all.length : index;
}

/// Junta as coberturas dos produtos de [productType]:
/// - obrigatórias: as que são obrigatórias em todos os produtos;
/// - opcionais: a união das restantes, com "Disponível em {n} de {total}";
/// - `OPTIONS`: a união dos capitais, por ordem crescente.
CoverageOptions buildCoverageOptions(
  Iterable<CatalogProductResponse> products,
  String productType,
) {
  final ofType = [
    for (final product in products)
      if (product.type.name == productType) product,
  ];
  final total = ofType.length;
  final byCode = <String, List<CoverageResponse>>{};
  for (final product in ofType) {
    final codes = <String>{};
    for (final coverage in product.coverages) {
      // Um produto conta uma vez por cobertura.
      if (codes.add(coverage.code.name)) {
        byCode.putIfAbsent(coverage.code.name, () => []).add(coverage);
      }
    }
  }

  final mandatory = <CoverageOption>[];
  final optional = <CoverageOption>[];
  final codes = byCode.keys.toList()
    ..sort((a, b) => _codeOrder(a).compareTo(_codeOrder(b)));
  for (final code in codes) {
    final coverages = byCode[code]!;
    final capitalType = coverages.first.capitalType.name;
    final options = <int>{
      for (final coverage in coverages)
        if (coverage.capitalType.name == CapitalTypes.options)
          for (final money in coverage.capitalOptions ?? const <Money>[])
            money.amount,
    }.toList()..sort();
    final capitals = [
      for (final coverage in coverages)
        if (coverage.capital != null) coverage.capital!.amount,
    ]..sort();
    final option = CoverageOption(
      code: code,
      mandatory:
          coverages.length == total && coverages.every((c) => c.mandatory),
      availableIn: coverages.length,
      total: total,
      capitalType: capitalType,
      capitalOptions: options,
      capital: capitals.isEmpty ? null : capitals.first,
    );
    (option.mandatory ? mandatory : optional).add(option);
  }
  return CoverageOptions(
    total: total,
    mandatory: mandatory,
    optional: optional,
  );
}
