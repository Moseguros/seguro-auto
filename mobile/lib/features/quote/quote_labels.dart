import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:moseguros_api/moseguros_api.dart';

import '../../core/format/dates.dart';
import '../../core/format/money.dart';
import '../../l10n/app_localizations.dart';
import 'quote_rules.dart';

/// "Hoje" em `Africa/Maputo` (os testes controlam-no).
final quoteTodayProvider = Provider<DateTime Function()>(
  (ref) => todayInMaputo,
);

/// Nome visível de uma cobertura (`coverage.<CODE>` na web).
String coverageName(AppLocalizations l10n, String code) => switch (code) {
  CoverageCodes.mtpl => l10n.coverageMtpl,
  CoverageCodes.vtpl => l10n.coverageVtpl,
  CoverageCodes.odCollision => l10n.coverageOdCollision,
  CoverageCodes.odGlass => l10n.coverageOdGlass,
  CoverageCodes.odFire => l10n.coverageOdFire,
  CoverageCodes.odNature => l10n.coverageOdNature,
  CoverageCodes.odTheft => l10n.coverageOdTheft,
  CoverageCodes.occupants => l10n.coverageOccupants,
  CoverageCodes.assistance => l10n.coverageAssistance,
  CoverageCodes.legal => l10n.coverageLegal,
  CoverageCodes.srcc => l10n.coverageSrcc,
  CoverageCodes.crossBorder => l10n.coverageCrossBorder,
  _ => code,
};

/// Motivo de uma cotação `NOT_AVAILABLE` (`quoteUnavailable.<REASON>` na web).
String unavailableReasonText(AppLocalizations l10n, String? reason) =>
    switch (reason) {
      'TARIFF_MISSING' => l10n.quoteUnavailableTariffMissing,
      'TARIFF_INVALID' => l10n.quoteUnavailableTariffInvalid,
      'COVERAGE_NOT_OFFERED' => l10n.quoteUnavailableCoverageNotOffered,
      'COVERAGE_REQUIREMENTS_NOT_MET' =>
        l10n.quoteUnavailableCoverageRequirementsNotMet,
      'CAPITAL_NOT_OFFERED' => l10n.quoteUnavailableCapitalNotOffered,
      'VEHICLE_VALUE_REQUIRED' => l10n.quoteUnavailableVehicleValueRequired,
      'DURATION_NOT_RATED' => l10n.quoteUnavailableDurationNotRated,
      'NO_MATCHING_ROW' => l10n.quoteUnavailableNoMatchingRow,
      'INSURER_TIMEOUT' => l10n.quoteUnavailableInsurerTimeout,
      'INSURER_UNAVAILABLE' => l10n.quoteUnavailableInsurerUnavailable,
      'INSURER_DECLINED' => l10n.quoteUnavailableInsurerDeclined,
      'EXTERNAL_INVALID_RESPONSE' =>
        l10n.quoteUnavailableExternalInvalidResponse,
      _ => l10n.quoteUnavailableEvaluationError,
    };

/// Nome de um tipo de produto (os títulos dos cartões do passo Tipo).
String productTypeName(AppLocalizations l10n, String type) => switch (type) {
  QuoteProductTypes.mtplOnly => l10n.quoteTypeMtplOnly,
  QuoteProductTypes.comprehensive => l10n.quoteTypeComprehensive,
  QuoteProductTypes.temporary => l10n.quoteTypeTemporary,
  _ => type,
};

/// "7 dias" … "1 ano".
String durationLabel(AppLocalizations l10n, int days) =>
    days == oneYearDays ? l10n.quoteDurationYear : l10n.quoteDurationDays(days);

/// Dinheiro da API (`Money`) com a língua do ecrã.
String money(AppLocalizations l10n, Money value) =>
    formatMoney(value.amount, l10n.localeName);

/// Centavos com a língua do ecrã.
String moneyCents(AppLocalizations l10n, int cents) =>
    formatMoney(cents, l10n.localeName);

/// Valor da franquia, sem a palavra "Franquia" ("10 % (mín. 25 000,00 MT)",
/// "5 000,00 MT"); `null` sem franquia.
String? deductibleValue(AppLocalizations l10n, QuoteDeductible deductible) {
  switch (deductible.type) {
    case QuoteDeductibleTypeEnum.FIXED:
      final amount = deductible.amount;
      return amount == null ? null : money(l10n, amount);
    case QuoteDeductibleTypeEnum.PERCENT:
      final percent = deductible.percent ?? '';
      final min = deductible.minAmount;
      return min == null
          ? l10n.quoteDeductiblePercent(percent)
          : l10n.quoteDeductiblePercentMin(percent, money(l10n, min));
    default:
      return null;
  }
}

/// Data da API (`Date`) como `dd/MM/yyyy`.
String apiDate(Date date) => formatDate(date.toDateTime());

/// "Toyota Corolla · ABC-123-MC" de um veículo cotado (campos opcionais no
/// pedido do mediador).
String quotedVehicleTitle({String? plate, String? make, String? model}) => [
  [?make, ?model].join(' '),
  ?plate,
].where((part) => part.isNotEmpty).join(' · ');

/// Maior `validUntil` das cotações com os [statuses].
DateTime? latestValidUntil(
  Iterable<QuoteResponse> quotes,
  Set<QuoteResponseStatusEnum> statuses,
) {
  DateTime? latest;
  for (final quote in quotes) {
    final until = quote.validUntil;
    if (until == null || !statuses.contains(quote.status)) continue;
    if (latest == null || until.isAfter(latest)) latest = until;
  }
  return latest;
}

/// Soma dos encargos de uma cotação, em centavos.
int chargesTotal(QuoteResponse quote) =>
    (quote.charges ?? const <QuoteChargeResponse>[]).fold(
      0,
      (sum, charge) => sum + charge.amount.amount,
    );

/// Rótulo de um encargo na língua do ecrã.
String chargeLabel(AppLocalizations l10n, QuoteChargeResponse charge) =>
    l10n.localeName == 'en' && charge.labelEn.isNotEmpty
    ? charge.labelEn
    : charge.labelPt;

/// Iniciais da seguradora para o avatar (até 2 letras).
String insurerInitials(String name) {
  final words = name
      .trim()
      .split(RegExp(r'\s+'))
      .where((word) => word.isNotEmpty)
      .toList();
  if (words.isEmpty) return '?';
  if (words.length == 1) {
    return words.first
        .substring(0, words.first.length >= 2 ? 2 : 1)
        .toUpperCase();
  }
  return (words[0][0] + words[1][0]).toUpperCase();
}
