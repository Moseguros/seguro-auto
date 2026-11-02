import '../../l10n/app_localizations.dart';

/// Códigos de província do backend (`Province`), pela ordem em que se mostram.
const provinceCodes = [
  'MAPUTO_CITY',
  'MAPUTO',
  'GAZA',
  'INHAMBANE',
  'SOFALA',
  'MANICA',
  'TETE',
  'ZAMBEZIA',
  'NAMPULA',
  'CABO_DELGADO',
  'NIASSA',
];

/// Nome visível de uma província (`province.<CODE>` na web); código desconhecido → o próprio código.
String provinceName(AppLocalizations l10n, String code) => switch (code) {
  'MAPUTO_CITY' => l10n.provinceMaputoCity,
  'MAPUTO' => l10n.provinceMaputo,
  'GAZA' => l10n.provinceGaza,
  'INHAMBANE' => l10n.provinceInhambane,
  'SOFALA' => l10n.provinceSofala,
  'MANICA' => l10n.provinceManica,
  'TETE' => l10n.provinceTete,
  'ZAMBEZIA' => l10n.provinceZambezia,
  'NAMPULA' => l10n.provinceNampula,
  'CABO_DELGADO' => l10n.provinceCaboDelgado,
  'NIASSA' => l10n.provinceNiassa,
  _ => code,
};
