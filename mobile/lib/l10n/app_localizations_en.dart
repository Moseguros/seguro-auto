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
  String get errorContactAlreadyInUse =>
      'This contact already belongs to another account.';

  @override
  String get errorContactAlreadyVerified =>
      'This contact is already confirmed on your account.';

  @override
  String get errorLastContact =>
      'The account must keep at least one confirmed contact.';

  @override
  String get errorFileNotFound =>
      'The file does not exist or you do not have access to it.';

  @override
  String get errorFileEmpty => 'The file is empty.';

  @override
  String get errorFileTooLarge => 'The file is larger than 10 MB.';

  @override
  String get errorFileTypeNotAllowed =>
      'File type not accepted. Use JPEG, PNG, HEIC or PDF.';

  @override
  String get errorFileStorageError =>
      'The file could not be read. Please try again later.';

  @override
  String get errorReferenceDomainUnknown =>
      'This reference list does not exist.';

  @override
  String get errorReferenceValueNotFound =>
      'The value does not exist in this list.';

  @override
  String get errorReferenceCodeAlreadyExists =>
      'A value with this code already exists in this list.';

  @override
  String get errorPlatformSettingNotFound => 'This setting does not exist.';

  @override
  String get errorPlateFormatNotFound => 'The plate format does not exist.';

  @override
  String get errorPlateFormatCodeAlreadyExists =>
      'A plate format with this code already exists.';

  @override
  String get errorLastActivePlateFormat =>
      'At least one plate format must stay active.';

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
  String get validationInvalidName =>
      'Enter your full name (at least two words, only letters, spaces, \' and -).';

  @override
  String get validationUnderage => 'You must be at least 18 years old.';

  @override
  String get validationInvalidDate => 'Invalid date.';

  @override
  String get validationInvalidDocumentNumber =>
      'Invalid document number (5 to 20 upper-case letters or digits).';

  @override
  String get validationInvalidCode =>
      'Use only upper-case letters, digits and _.';

  @override
  String get validationParentNotFound => 'The make does not exist.';

  @override
  String get validationInvalidValue => 'Invalid value for this setting.';

  @override
  String get validationInvalidPattern => 'The regular expression is not valid.';

  @override
  String get validationPatternNotAnchored =>
      'The expression must start with ^ and end with \$.';

  @override
  String get validationPatternWithoutGroups =>
      'The expression needs at least one named group.';

  @override
  String get validationTemplateGroupUnknown =>
      'The template uses a group that is not in the expression.';

  @override
  String get validationExampleDoesNotMatch =>
      'The example does not match the format.';

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

  @override
  String get aboutTitle => 'About';

  @override
  String get loading => 'Loading…';

  @override
  String get cancel => 'Cancel';

  @override
  String get onboardingSkip => 'Skip';

  @override
  String get onboardingNext => 'Next';

  @override
  String get onboardingStart => 'Get started';

  @override
  String onboardingPage(int current, int total) {
    return 'Page $current of $total';
  }

  @override
  String get onboarding1Title => 'Compare insurers';

  @override
  String get onboarding1Text =>
      'See offers from several insurers side by side and pick the one that suits you.';

  @override
  String get onboarding2Title => 'Buy in minutes';

  @override
  String get onboarding2Text =>
      'Get a quote, pay with M-Pesa or by reference and receive your policy on your phone.';

  @override
  String get onboarding3Title => 'Everything in one place';

  @override
  String get onboarding3Text =>
      'Policies, payments and claims always with you.';

  @override
  String get authLoginTitle => 'Sign in to your customer area';

  @override
  String get authLoginSubtitle =>
      'Use your mobile number or e-mail. We send you a code to sign in, no password needed.';

  @override
  String get authIdentifierTypeLabel => 'Sign in with';

  @override
  String get authIdentifierTypeMsisdn => 'Mobile';

  @override
  String get authIdentifierTypeEmail => 'E-mail';

  @override
  String get authMsisdnLabel => 'Mobile number';

  @override
  String get authMsisdnHelper => '9 digits, for example 84 123 4567.';

  @override
  String get authEmailLabel => 'E-mail';

  @override
  String get authChannelLabel => 'Receive the code by';

  @override
  String get authSubmit => 'Get code';

  @override
  String authRetryIn(int seconds) {
    return 'Try again in $seconds s.';
  }

  @override
  String get authCodeSubtitle => 'Enter the code you received.';

  @override
  String get authCodeInputLabel => 'Code received';

  @override
  String authCodeSentTo(String channel, String destination) {
    return 'We sent a code by $channel to $destination.';
  }

  @override
  String get authCodeChange => 'Change';

  @override
  String authCodeFallback(String requested, String used) {
    return 'We could not send it by $requested. We sent the code by $used.';
  }

  @override
  String authCodeWrongCode(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: 'Wrong code. $count attempts left.',
      one: 'Wrong code. 1 attempt left.',
    );
    return '$_temp0';
  }

  @override
  String get authCodeResent => 'We sent a new code.';

  @override
  String get authCodeResend => 'Resend code';

  @override
  String authCodeResendIn(int seconds) {
    return 'Resend code ($seconds s)';
  }

  @override
  String get channelSms => 'SMS';

  @override
  String get channelWhatsapp => 'WhatsApp';

  @override
  String get channelEmail => 'E-mail';

  @override
  String get logout => 'Sign out';

  @override
  String get logoutConfirmTitle => 'Sign out?';

  @override
  String get logoutConfirmText => 'You will need a new code to sign in again.';

  @override
  String homeGreeting(String name) {
    return 'Hello, $name';
  }

  @override
  String get homeGreetingNoName => 'Hello';

  @override
  String get homeIncompleteProfile =>
      'Complete your profile to be able to buy insurance.';

  @override
  String get homeCompleteProfile => 'Complete profile';

  @override
  String get homeQuoteTitle => 'Car insurance';

  @override
  String get homeQuoteText => 'Compare insurers\' prices for your vehicle.';

  @override
  String get comingSoon => 'Coming soon';

  @override
  String get profileOpen => 'Profile';

  @override
  String get profileTitle => 'Profile';

  @override
  String get profilePersonalData => 'Personal details';

  @override
  String get profileCompleteNotice =>
      'We need these details to issue your policies.';

  @override
  String get profileFieldFullName => 'Full name';

  @override
  String get profileFieldBirthDate => 'Date of birth';

  @override
  String get profileFieldIdDocType => 'Document type';

  @override
  String get profileFieldIdDocNumber => 'Document number';

  @override
  String get profileFieldNuit => 'NUIT (tax number)';

  @override
  String get profileFieldAddress => 'Address';

  @override
  String get profileFieldProvince => 'Province';

  @override
  String get profileChoose => 'Choose…';

  @override
  String get profileChange => 'Change';

  @override
  String get profileChangeDocument => 'Change the document number';

  @override
  String get profileChangeNuit => 'Change the NUIT';

  @override
  String get profileNuitHelper => '9 digits.';

  @override
  String get profileOptional => 'Optional.';

  @override
  String get profileSave => 'Save';

  @override
  String get profileSaved => 'Profile saved';

  @override
  String get idDocTypeBi => 'Identity Card (BI)';

  @override
  String get idDocTypePassport => 'Passport';

  @override
  String get idDocTypeDire => 'DIRE (residence permit)';

  @override
  String get contactsTitle => 'Contacts';

  @override
  String get contactTypeMsisdn => 'Mobile';

  @override
  String get contactTypeEmail => 'E-mail';

  @override
  String get contactsVerified => 'Verified';

  @override
  String get contactsAddMsisdn => 'Add mobile';

  @override
  String get contactsAddEmail => 'Add e-mail';

  @override
  String get contactsChangeMsisdn => 'Change mobile';

  @override
  String get contactsChangeEmail => 'Change e-mail';

  @override
  String get contactsChangeShort => 'Change';

  @override
  String get contactsRemoveMsisdn => 'Remove mobile';

  @override
  String get contactsRemoveEmail => 'Remove e-mail';

  @override
  String get contactsRemoveShort => 'Remove';

  @override
  String get contactsSaved => 'Contact confirmed';

  @override
  String get contactsRemoved => 'Contact removed';

  @override
  String get contactsConfirmRemoveTitle => 'Remove contact?';

  @override
  String contactsConfirmRemoveText(String value) {
    return 'You will no longer be able to sign in or receive notices at $value.';
  }

  @override
  String get contactsConfirmRemove => 'Remove';
}
