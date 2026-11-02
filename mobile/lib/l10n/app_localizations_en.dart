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
  String get errorRefreshTokenInvalid =>
      'Your session has ended. Please sign in again.';

  @override
  String get errorRefreshTokenReused =>
      'For your security, your session was ended. Please sign in again.';

  @override
  String get validationNotBlank => 'This field is required.';

  @override
  String get validationNotNull => 'This field is required.';

  @override
  String get errorConcurrentModification =>
      'Someone else changed this data in the meantime. Refresh and try again.';

  @override
  String get errorOtpChannelChoiceDisabled =>
      'Choosing the delivery channel is not allowed.';

  @override
  String get errorOtpChannelNotAvailable =>
      'This channel is not available. Use another channel or sign in with the other identifier.';

  @override
  String get errorOtpRateLimited =>
      'You requested too many codes. Please try again later.';

  @override
  String get errorOtpResendTooSoon =>
      'Please wait a moment before requesting a new code.';

  @override
  String get errorOtpDeliveryFailed =>
      'The code could not be sent. Please try again in a moment.';

  @override
  String get errorOtpChallengeNotFound =>
      'Unknown code request. Please request a new code.';

  @override
  String get errorOtpAlreadyUsed =>
      'This code was already used. Please request a new code.';

  @override
  String get errorOtpSuperseded =>
      'You requested a newer code. Use the last code you received.';

  @override
  String get errorOtpExpired =>
      'The code has expired. Please request a new code.';

  @override
  String get errorOtpInvalid => 'Wrong code. Please check it and try again.';

  @override
  String get errorOtpAttemptsExceeded =>
      'Too many wrong attempts. Please request a new code.';

  @override
  String get errorAccountDisabled =>
      'This account is disabled. Please contact customer support.';

  @override
  String get errorOrganizationNotFound => 'Organization not found.';

  @override
  String get errorOrganizationNotActive => 'The organization is suspended.';

  @override
  String get errorNuitAlreadyInUse =>
      'There is already an organization with this NUIT.';

  @override
  String get errorShortCodeAlreadyInUse =>
      'This short code is already used by another insurer.';

  @override
  String get errorOmbudsmanInvalid => 'Choose an existing customer ombudsman.';

  @override
  String get errorRoleNotAllowedForOrganization =>
      'One of the selected roles cannot be assigned in this organization.';

  @override
  String get errorEmailAlreadyInUse =>
      'This e-mail address already belongs to another user.';

  @override
  String get errorInvitationInvalid =>
      'This invitation is no longer valid. Ask the person who invited you for a new one.';

  @override
  String get errorInvitationNotAccepted => 'Set your password first.';

  @override
  String get errorTotpInvalid =>
      'Wrong code. Check your authenticator app and try again.';

  @override
  String get errorPasswordTooWeak =>
      'The password does not meet the security rules.';

  @override
  String get errorInvalidCredentials =>
      'Wrong e-mail, password or authentication code.';

  @override
  String get errorAccountLocked =>
      'Too many failed attempts. The account is temporarily locked; please try again later.';

  @override
  String get errorOrganizationSuspended =>
      'Your organization is suspended. Please contact the MoSeguros administration.';

  @override
  String get errorResetTokenInvalid =>
      'This recovery link is no longer valid. Please request a new one.';

  @override
  String get errorUserNotFound => 'User not found.';

  @override
  String get errorCannotModifySelf => 'You cannot change your own account.';

  @override
  String get errorLastManager =>
      'The organization must keep at least one active manager.';

  @override
  String get validationNotAllowed => 'This field does not apply here.';

  @override
  String get validationRequired => 'This field is required.';

  @override
  String get validationInvalidSort => 'Unsupported sort order.';

  @override
  String get validationTooShort =>
      'The password must have at least 12 characters.';

  @override
  String get validationTooLong =>
      'The password can have at most 128 characters.';

  @override
  String get validationContainsEmail =>
      'The password must not contain your e-mail address.';

  @override
  String get validationTooCommon =>
      'This password is too common. Choose another one.';

  @override
  String get validationSize => 'Length outside the allowed limits.';

  @override
  String get validationPattern => 'Invalid format.';

  @override
  String get validationTypeMismatch => 'Invalid value.';

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
