// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get appName => 'MoSeguros';

  @override
  String get homeTagline =>
      'Compare, buy and manage your car insurance in one place.';

  @override
  String get systemStatusTitle => 'System status';

  @override
  String get systemStatusLoading => 'Checking the API…';

  @override
  String get systemStatusVersion => 'Version';

  @override
  String get systemStatusCommit => 'Commit';

  @override
  String get systemStatusUnknown => 'unknown';

  @override
  String get systemStatusError => 'Could not get the API status.';

  @override
  String get retry => 'Try again';

  @override
  String get notFoundTitle => 'Page not found';

  @override
  String get notFoundDescription =>
      'The address you are looking for does not exist.';

  @override
  String get backHome => 'Back to home';

  @override
  String get errorNetworkError =>
      'No connection to the server. Check your Internet connection.';

  @override
  String get errorUnknown => 'An unexpected error occurred.';

  @override
  String get errorValidationError => 'The data sent is not valid.';

  @override
  String get errorNotFound => 'The requested resource does not exist.';

  @override
  String get errorUnauthorized => 'You need to sign in.';

  @override
  String get errorForbidden => 'You do not have permission for this operation.';

  @override
  String get errorInternalError =>
      'Internal server error. Please try again later.';

  @override
  String get errorInvalidCurrency => 'Invalid currency.';

  @override
  String get errorInvalidMsisdn => 'Invalid mobile number.';

  @override
  String get errorInvalidNuit => 'Invalid NUIT.';

  @override
  String get errorInvalidPlate => 'Invalid number plate.';

  @override
  String get validationInvalidCurrency => 'Invalid currency.';

  @override
  String get validationInvalidMsisdn =>
      'Invalid mobile number (e.g. +258 84 123 4567).';

  @override
  String get validationInvalidNuit => 'The NUIT has 9 digits.';

  @override
  String get validationInvalidPlate => 'Invalid number plate.';

  @override
  String get ctaQuote => 'Get a quote';

  @override
  String get ctaCompare => 'Compare proposals';

  @override
  String get ctaBuy => 'Buy policy';

  @override
  String get ctaRenew => 'Renew insurance';

  @override
  String get errorInvalidEmail => 'Invalid e-mail address.';

  @override
  String get errorInvalidPhone => 'Invalid phone number.';

  @override
  String get errorTenantRequired =>
      'Internal server error. Please try again later.';

  @override
  String get errorDecryptionFailed =>
      'Internal server error. Please try again later.';

  @override
  String get errorInvalidStateTransition =>
      'This operation is not possible in the current state.';

  @override
  String get errorLicenseNumberRequired =>
      'The ASFPM licence number is required for insurers, brokers and agents.';

  @override
  String get errorChannelsEmpty => 'Choose at least one channel.';

  @override
  String get errorChannelsDuplicated => 'Each channel can only appear once.';

  @override
  String get errorDefaultChannelNotEnabled =>
      'The default channel must be enabled.';

  @override
  String get errorFallbackOrderMismatch =>
      'The fallback order must contain exactly the enabled channels.';

  @override
  String get errorOutOfRange => 'Value outside the allowed range.';

  @override
  String get validationInvalidEmail =>
      'Invalid e-mail (e.g. name@example.co.mz).';

  @override
  String get validationInvalidPhone => 'Invalid phone number (9 to 15 digits).';

  @override
  String get validationLicenseNumberRequired =>
      'Enter the ASFPM licence number.';

  @override
  String get validationChannelsEmpty => 'Choose at least one channel.';

  @override
  String get validationChannelsDuplicated =>
      'Each channel can only appear once.';

  @override
  String get validationDefaultChannelNotEnabled =>
      'The default channel must be enabled.';

  @override
  String get validationFallbackOrderMismatch =>
      'The fallback order must contain exactly the enabled channels.';

  @override
  String get validationOutOfRange => 'Value outside the allowed range.';

  @override
  String get provinceMaputoCity => 'Maputo City';

  @override
  String get provinceMaputo => 'Maputo Province';

  @override
  String get provinceGaza => 'Gaza';

  @override
  String get provinceInhambane => 'Inhambane';

  @override
  String get provinceSofala => 'Sofala';

  @override
  String get provinceManica => 'Manica';

  @override
  String get provinceTete => 'Tete';

  @override
  String get provinceZambezia => 'Zambezia';

  @override
  String get provinceNampula => 'Nampula';

  @override
  String get provinceCaboDelgado => 'Cabo Delgado';

  @override
  String get provinceNiassa => 'Niassa';
}
