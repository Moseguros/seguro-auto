import 'package:moseguros_api/moseguros_api.dart';

import '../../l10n/app_localizations.dart';

/// Nome visível de uma categoria (`vehicleCategory.<CODE>` na web).
String vehicleCategoryName(AppLocalizations l10n, String code) =>
    switch (code) {
      'LIGHT_PASSENGER' => l10n.vehicleCategoryLightPassenger,
      'LIGHT_MIXED' => l10n.vehicleCategoryLightMixed,
      'HEAVY' => l10n.vehicleCategoryHeavy,
      'MOTORCYCLE' => l10n.vehicleCategoryMotorcycle,
      'TRAILER' => l10n.vehicleCategoryTrailer,
      'BUS' => l10n.vehicleCategoryBus,
      _ => code,
    };

/// Nome visível de um uso (`vehicleUsage.<CODE>` na web).
String vehicleUsageName(AppLocalizations l10n, String code) => switch (code) {
  'PRIVATE' => l10n.vehicleUsagePrivate,
  'RENTAL' => l10n.vehicleUsageRental,
  'PASSENGER_TRANSPORT' => l10n.vehicleUsagePassengerTransport,
  'GOODS' => l10n.vehicleUsageGoods,
  'TAXI' => l10n.vehicleUsageTaxi,
  'DRIVING_SCHOOL' => l10n.vehicleUsageDrivingSchool,
  _ => code,
};

/// Rótulo de um valor de referência na língua do ecrã (`labelPt` por defeito).
String referenceLabel(PublicReferenceValueResponse value, String languageCode) {
  final english = value.labelEn;
  return languageCode == 'en' && english != null && english.isNotEmpty
      ? english
      : value.labelPt;
}

/// Rótulo de um código de uma lista de referência; o próprio código quando não existe.
String referenceLabelFor(
  Iterable<PublicReferenceValueResponse> values,
  String code,
  String languageCode,
) {
  for (final value in values) {
    if (value.code == code) return referenceLabel(value, languageCode);
  }
  return code;
}
