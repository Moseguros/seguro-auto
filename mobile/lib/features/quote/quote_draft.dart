import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/moseguros_api.dart';

import 'quote_rules.dart';

/// Rascunho do assistente "Simular seguro" (S3-05 §2).
///
/// [coverages] é `null` até o passo das coberturas o preencher (as coberturas
/// por defeito dependem do catálogo); depois tem os códigos escolhidos
/// (obrigatórios incluídos) e o capital escolhido nas coberturas `OPTIONS`.
class QuoteDraft {
  const QuoteDraft({
    this.vehicleId,
    this.driverId,
    this.productType,
    this.coverages,
    this.startDate,
    this.durationDays,
    this.fieldErrors = const {},
  });

  final String? vehicleId;
  final String? driverId;
  final String? productType;
  final Map<String, int?>? coverages;
  final DateTime? startDate;
  final int? durationDays;

  /// Erros de campo devolvidos pelo `createQuoteRequest` (`VALIDATION_ERROR`):
  /// campo da API → código de `FieldErrorCodes`. Cada passo mostra os seus.
  final Map<String, String> fieldErrors;

  static const empty = QuoteDraft();

  QuoteDraft copyWith({
    String? Function()? vehicleId,
    String? Function()? driverId,
    String? Function()? productType,
    Map<String, int?>? Function()? coverages,
    DateTime? Function()? startDate,
    int? Function()? durationDays,
    Map<String, String>? fieldErrors,
  }) => QuoteDraft(
    vehicleId: vehicleId == null ? this.vehicleId : vehicleId(),
    driverId: driverId == null ? this.driverId : driverId(),
    productType: productType == null ? this.productType : productType(),
    coverages: coverages == null ? this.coverages : coverages(),
    startDate: startDate == null ? this.startDate : startDate(),
    durationDays: durationDays == null ? this.durationDays : durationDays(),
    fieldErrors: fieldErrors ?? this.fieldErrors,
  );

  /// Primeiro erro de campo cujo nome começa por um dos [prefixes].
  String? fieldError(List<String> prefixes) {
    for (final entry in fieldErrors.entries) {
      if (prefixes.any(entry.key.startsWith)) return entry.value;
    }
    return null;
  }

  /// Pedido `createQuoteRequest` (todos os passos preenchidos).
  CreateQuoteRequest toRequest() {
    final start = startDate!;
    return CreateQuoteRequest(
      (b) => b
        ..vehicleId = vehicleId
        ..driverId = driverId
        ..productType = CreateQuoteRequestProductTypeEnum.valueOf(productType!)
        ..startDate = Date(start.year, start.month, start.day)
        ..durationDays = durationDays
        ..coverages = ListBuilder([
          for (final code in CoverageCodes.all)
            if (coverages?.containsKey(code) ?? false)
              RequestedCoverageRequest(
                (c) => c
                  ..code = RequestedCoverageRequestCodeEnum.valueOf(code)
                  ..capital = coverages![code] == null
                      ? null
                      : (MoneyBuilder()
                          ..amount = coverages![code]
                          ..currency = MoneyCurrencyEnum.MZN),
              ),
        ]),
    );
  }
}
