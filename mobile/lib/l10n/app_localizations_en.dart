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
  String get errorProductNotFound => 'Product not found.';

  @override
  String get errorProductVersionNotFound => 'Product version not found.';

  @override
  String get errorProductDocumentNotFound => 'Document not found.';

  @override
  String get errorProductCodeAlreadyExists =>
      'A product with this code already exists.';

  @override
  String get errorProductTypeLocked =>
      'The type cannot change after the product has been published.';

  @override
  String get errorProductRetired =>
      'This product was retired and can no longer be changed.';

  @override
  String get errorProductDraftExists =>
      'There is already a version of this product being prepared.';

  @override
  String get errorProductVersionNotEditable =>
      'This version has already been published and cannot be changed. Create a new version.';

  @override
  String get errorProductDocumentLimitReached =>
      'Each version can have at most 10 documents.';

  @override
  String get errorProductVersionNotPublishable =>
      'The version does not meet the publication rules yet.';

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
  String get validationDuplicated => 'Repeated value.';

  @override
  String get validationInvalidRequirement =>
      'A coverage cannot require itself.';

  @override
  String get validationCapitalOptionsInvalid =>
      'Enter 2 to 10 values, from lowest to highest.';

  @override
  String get validationInvalidCountryCode => 'Invalid country code.';

  @override
  String get validationMzScopeRequired => 'The scope must include Mozambique.';

  @override
  String get validationInvalidCapitalType =>
      'Capital type not allowed for this coverage.';

  @override
  String get validationMtplRequired =>
      'The product must include compulsory third-party liability.';

  @override
  String get validationMtplMustBeMandatory =>
      'Compulsory third-party liability cannot be optional.';

  @override
  String get validationMtplCapitalBelowMinimum =>
      'The capital is below the configured legal minimum capital.';

  @override
  String get validationBorderOnlyMtpl =>
      'Border insurance can only have compulsory third-party liability.';

  @override
  String get validationOdNotAllowedForType =>
      'This product type does not allow own damage coverages.';

  @override
  String get validationOdRequiredForComprehensive =>
      'An own damage product needs at least one own damage coverage.';

  @override
  String get validationOdCapitalMustBeVehicleValue =>
      'Own damage coverages use the vehicle value as capital.';

  @override
  String get validationOdTheftRequiresOdCoverages =>
      'Theft requires collision, fire and natural events.';

  @override
  String get validationRequiredCoverageMissing =>
      'A coverage required by this one is missing.';

  @override
  String get validationVtplCapitalTooLow =>
      'The voluntary third-party liability capital must be higher than the legal minimum capital.';

  @override
  String get validationCrossBorderScopeRequired =>
      'Enter at least one country besides Mozambique.';

  @override
  String get validationDocumentRequired =>
      'This document in Portuguese is missing.';

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

  @override
  String get errorVehicleNotFound => 'Vehicle not found.';

  @override
  String get errorVehicleAlreadyRegistered =>
      'You already have a vehicle with this plate.';

  @override
  String get errorVehicleLimitReached =>
      'You have reached the maximum of 20 vehicles.';

  @override
  String get errorVehicleRemovalBlocked =>
      'This vehicle has a policy in force and cannot be removed.';

  @override
  String get errorVehicleRegistryNotFound =>
      'We could not find this plate in the registry. Fill in the registration document details.';

  @override
  String get errorVehicleRegistryUnavailable =>
      'The vehicle registry is not available. Fill in the registration document details.';

  @override
  String get errorDriverNotFound => 'Driver not found.';

  @override
  String get errorDriverAlreadyRegistered =>
      'You already have a driver with this licence.';

  @override
  String get errorDriverLimitReached =>
      'You have reached the maximum of 10 drivers.';

  @override
  String get errorOwnerDriverAlreadyExists =>
      'You are already registered as a driver.';

  @override
  String get errorDriverIsHabitual =>
      'This driver is the habitual driver of a vehicle. Choose another habitual driver first.';

  @override
  String get errorRatingRulesTooLarge =>
      'The tariff is too large (maximum 256 KB).';

  @override
  String get validationInvalidChassis => 'Invalid chassis number.';

  @override
  String get validationInvalidYear => 'Invalid year.';

  @override
  String get validationInvalidLicenceNumber =>
      'Invalid driving licence number.';

  @override
  String get validationLicenceCategoryUnknown => 'Unknown licence category.';

  @override
  String get validationLicenceIssueDateInvalid =>
      'The issue date must be after the birth date and not after today.';

  @override
  String get validationHabitualDriverRequired => 'Choose the habitual driver.';

  @override
  String get validationMultipleHabitualDrivers =>
      'There can only be one habitual driver.';

  @override
  String get validationSchemaViolation => 'Invalid format.';

  @override
  String get validationCoverageTariffMissing =>
      'A coverage of the product has no tariff.';

  @override
  String get validationCoverageNotInProduct =>
      'This coverage does not exist in the product.';

  @override
  String get validationDuplicateCoverage => 'Repeated coverage.';

  @override
  String get validationBaseTypeNotAllowed =>
      'This base type does not suit the capital of this coverage.';

  @override
  String get validationValueOutOfRange => 'Value outside the allowed limits.';

  @override
  String get validationInvalidRange =>
      'The lower limit must be less than the upper limit.';

  @override
  String get validationDuplicateFactor => 'Repeated factor.';

  @override
  String get validationReservedFactorCode =>
      'This code is reserved for the system.';

  @override
  String get validationDuplicateDuration => 'Repeated duration.';

  @override
  String get validationDuration365NotOne => 'The 365-day factor must be 1.';

  @override
  String get validationDurationFactorsRequired =>
      'A temporary product needs the duration table.';

  @override
  String get validationDurationNotAllowed =>
      'Duration not allowed for this product.';

  @override
  String get validationDuplicateCharge => 'Repeated charge.';

  @override
  String get save => 'Save';

  @override
  String get close => 'Close';

  @override
  String get reload => 'Reload';

  @override
  String get yes => 'Yes';

  @override
  String get no => 'No';

  @override
  String get valueMissing => '—';

  @override
  String get moreOptions => 'More options';

  @override
  String get navHome => 'Home';

  @override
  String get navVehicles => 'Vehicles';

  @override
  String get navProfile => 'Profile';

  @override
  String get validationInvalidMoney =>
      'Invalid amount. Use at most 2 decimal places.';

  @override
  String get vehicleCategoryLightPassenger => 'Light passenger car';

  @override
  String get vehicleCategoryLightMixed => 'Light mixed-use vehicle';

  @override
  String get vehicleCategoryHeavy => 'Heavy vehicle';

  @override
  String get vehicleCategoryMotorcycle => 'Motorcycle';

  @override
  String get vehicleCategoryTrailer => 'Trailer';

  @override
  String get vehicleCategoryBus => 'Bus';

  @override
  String get vehicleUsagePrivate => 'Private';

  @override
  String get vehicleUsageRental => 'Rental';

  @override
  String get vehicleUsagePassengerTransport => 'Passenger transport';

  @override
  String get vehicleUsageGoods => 'Goods transport';

  @override
  String get vehicleUsageTaxi => 'Taxi';

  @override
  String get vehicleUsageDrivingSchool => 'Driving school';

  @override
  String get vehiclesAddButton => 'Add vehicle';

  @override
  String get vehiclesEmpty => 'You have not registered any vehicles yet.';

  @override
  String get vehiclesBackToList => 'See my vehicles';

  @override
  String vehicleMakeModelYear(String make, String model, String year) {
    return '$make $model · $year';
  }

  @override
  String vehicleHabitualDriver(String name) {
    return 'Usual driver: $name';
  }

  @override
  String get vehicleNoHabitualDriver => 'No usual driver';

  @override
  String get vehicleDocMissing => 'Registration document missing';

  @override
  String get vehicleRegistryVerified => 'Confirmed in the registry';

  @override
  String get vehicleRegistryNotVerified => 'Not confirmed in the registry';

  @override
  String get vehicleNewTitle => 'Add vehicle';

  @override
  String get vehicleEditTitle => 'Edit vehicle';

  @override
  String get vehicleFieldPlate => 'Number plate';

  @override
  String vehiclePlateHelper(String examples) {
    return 'E.g. $examples';
  }

  @override
  String get vehicleOr => ' or ';

  @override
  String get vehicleRegistryLookup => 'Look up in the registry';

  @override
  String get vehicleRegistryFilled =>
      'We filled in the details from the vehicle registry. Check them against your registration document.';

  @override
  String get vehicleFieldChassis => 'Chassis number';

  @override
  String get vehicleFieldMake => 'Make';

  @override
  String get vehicleFieldModel => 'Model';

  @override
  String get vehicleFieldYear => 'Year';

  @override
  String get vehicleFieldCategory => 'Category';

  @override
  String get vehicleFieldEngineCc => 'Engine size (cc)';

  @override
  String get vehicleFieldSeats => 'Seats';

  @override
  String get vehicleFieldGrossWeight => 'Gross weight (kg)';

  @override
  String get vehicleFieldUsage => 'Use';

  @override
  String get vehicleFieldProvince => 'Province of circulation';

  @override
  String get vehicleFieldCommercialValue => 'Market value';

  @override
  String get vehicleCommercialValueHelper => 'Needed for own-damage insurance.';

  @override
  String get vehicleFieldHasTracker => 'Has a tracker (GPS)';

  @override
  String get vehicleCreated =>
      'Vehicle added. Now send a photo of the registration document.';

  @override
  String get vehicleConflict => 'This vehicle was changed on another device';

  @override
  String get vehicleEdit => 'Edit';

  @override
  String get vehicleRemove => 'Remove vehicle';

  @override
  String get vehicleRemoveConfirmTitle => 'Remove vehicle?';

  @override
  String get vehicleRemoveConfirmText =>
      'The vehicle will no longer appear in your account.';

  @override
  String get vehicleRemoveConfirm => 'Remove';

  @override
  String get vehicleRemoved => 'Vehicle removed';

  @override
  String get vehicleSectionData => 'Details';

  @override
  String get vehicleSectionDoc => 'Registration document';

  @override
  String get vehicleSectionDrivers => 'Drivers';

  @override
  String get vehicleDocNone =>
      'You have not sent the registration document yet.';

  @override
  String get vehicleDocUpload => 'Send registration document';

  @override
  String get vehicleDocReplace => 'Replace';

  @override
  String get vehicleDocOpen => 'Open';

  @override
  String get vehicleDocUploading => 'Sending…';

  @override
  String get vehicleDocSaved => 'Registration document saved';

  @override
  String get fileOpenFailed => 'There is no app to open this file.';

  @override
  String get vehicleNoDrivers => 'No drivers assigned.';

  @override
  String get vehicleDriverHabitual => 'Usual';

  @override
  String get vehicleManageDrivers => 'Manage drivers';

  @override
  String manageDriversHelp(int max) {
    return 'Tick up to $max drivers and choose the usual one.';
  }

  @override
  String get docSourceCamera => 'Take photo';

  @override
  String get docSourceGallery => 'Choose from gallery';

  @override
  String get docSourcePdf => 'Choose PDF';

  @override
  String get cameraFrameHint =>
      'Fit the registration document inside the frame, in good light and without glare.';

  @override
  String get cameraPermissionDenied =>
      'To do the inspection, MoSeguros needs access to the camera.';

  @override
  String get cameraOpenSettings => 'Open settings';

  @override
  String get cameraUnavailable =>
      'The camera could not be opened. Choose a photo from the gallery.';

  @override
  String get cameraCapture => 'Take photo';

  @override
  String get cameraFlashOff => 'Flash off';

  @override
  String get cameraFlashAuto => 'Flash auto';

  @override
  String get cameraFlashOn => 'Flash always on';

  @override
  String get photoRetake => 'Retake';

  @override
  String get photoUse => 'Use photo';

  @override
  String get driversTitle => 'Drivers';

  @override
  String get driversAddButton => 'Register driver';

  @override
  String get driversEmpty => 'You have not registered any drivers yet.';

  @override
  String get driversBackToList => 'See my drivers';

  @override
  String get driverEditTitle => 'Edit driver';

  @override
  String get driverOwnerTag => 'Me';

  @override
  String driverCategories(String categories) {
    return 'Categories: $categories';
  }

  @override
  String driverLicenceMasked(String masked) {
    return 'Licence: $masked';
  }

  @override
  String driverVehicleCount(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: '$count vehicles',
      one: '1 vehicle',
      zero: 'No vehicles',
    );
    return '$_temp0';
  }

  @override
  String get driverEdit => 'Edit';

  @override
  String get driverRemove => 'Remove';

  @override
  String get driverRemoveConfirmTitle => 'Remove driver?';

  @override
  String get driverRemoveConfirmText =>
      'The driver will no longer appear in your account.';

  @override
  String get driverRemoved => 'Driver removed';

  @override
  String get driverFieldIsMe => 'This is me';

  @override
  String get driverFieldLicenceNumber => 'Licence number';

  @override
  String get driverChangeLicence => 'Change the licence number';

  @override
  String get driverFieldLicenceCategories => 'Categories';

  @override
  String get driverFieldLicenceIssueDate => 'Licence issue date';

  @override
  String get driverConflict => 'This driver was changed on another device';

  @override
  String get errorQuoteRequestNotFound => 'Quote not found.';

  @override
  String get errorQuoteNotFound => 'Quote not found.';

  @override
  String get errorQuoteExpired =>
      'This quote has expired. Run a new simulation.';

  @override
  String get errorQuoteNotAvailable => 'This insurer did not make an offer.';

  @override
  String get errorQuoteAlreadyUsed =>
      'This quote has already been used in a proposal.';

  @override
  String get errorQuoteRateLimited =>
      'You ran too many simulations in the last hour. Try again later.';

  @override
  String get errorVehicleValueRequired =>
      'For own damage, enter the commercial value of the vehicle.';

  @override
  String get errorOrganizationNotInsurer =>
      'This organization is not an insurer.';

  @override
  String get validationStartDateOutOfRange =>
      'The start date must be between today and 60 days from now.';

  @override
  String get validationCoverageNotAllowedForType =>
      'This coverage does not exist in this type of insurance.';

  @override
  String get validationOwnDamageRequired =>
      'Choose at least one own damage coverage.';

  @override
  String get validationCoverageRequires =>
      'Theft or robbery requires collision, fire and natural events.';

  @override
  String get validationInvalidUrl =>
      'Invalid address (it must start with https://).';

  @override
  String get validationInvalidSecretRef => 'Invalid secret reference.';

  @override
  String get errorProfileIncomplete =>
      'Complete your profile before continuing.';

  @override
  String get errorKycAlreadyPending =>
      'You already have an identity check under review.';

  @override
  String get errorKycAlreadyVerified => 'Your identity is already verified.';

  @override
  String get errorKycNotFound => 'Identity check not found.';

  @override
  String get validationInvalidInsurer => 'Invalid insurer.';

  @override
  String get errorQuoteDataChanged =>
      'The vehicle or driver details changed after the quote. Request a new quote.';

  @override
  String get errorProposalAlreadyExists =>
      'A proposal already exists for this quote.';

  @override
  String get errorProposalNotFound => 'Proposal not found.';

  @override
  String get errorProposalNotEditable =>
      'This proposal can no longer be changed.';

  @override
  String get errorInsurerNotActive =>
      'This insurer is not accepting proposals at the moment.';

  @override
  String get errorSuitabilityNotWarned =>
      'There are no suitability warnings to confirm.';

  @override
  String get errorPdfTemplateNotFound =>
      'We could not generate the document. Try again.';

  @override
  String get errorPdfRenderFailed =>
      'We could not generate the document. Try again.';

  @override
  String get errorSignerContactMissing =>
      'There is no phone number or verified e-mail to send the code to.';

  @override
  String get errorProposalIncomplete =>
      'There are still steps to complete in this proposal.';

  @override
  String get errorSignRequestNotFound =>
      'This code is no longer valid. Request a new one.';

  @override
  String get errorProposalChangedSinceSignRequest =>
      'The proposal changed after the code was requested. Request a new one.';

  @override
  String get proposalMissingDeclarations => 'Risk declarations';

  @override
  String get proposalMissingSuitability => 'Suitability questionnaire';

  @override
  String get proposalMissingSuitabilityAcknowledgement =>
      'Confirm the suitability warnings';

  @override
  String get proposalMissingInspection => 'Vehicle inspection';

  @override
  String get proposalMissingStartDate => 'Start date';

  @override
  String get proposalMissingSignature => 'Signature';

  @override
  String get validationAnswersIncomplete => 'Answer all the questions.';

  @override
  String get validationAnswerDuplicated => 'Duplicated question.';

  @override
  String get validationUnknownQuestion => 'Unknown question.';

  @override
  String get validationInvalidAnswer => 'Invalid answer.';

  @override
  String get validationMustBeTrue => 'You must confirm to continue.';

  @override
  String get errorInspectionNotRequired =>
      'This proposal does not need an inspection.';

  @override
  String get errorInspectionNotFound => 'Inspection not found.';

  @override
  String get errorInspectionNotEditable =>
      'The inspection has been sent and cannot be changed.';

  @override
  String get errorInspectionPhotosMissing => 'Required photos are missing.';

  @override
  String get errorPhotoNotFound => 'Photo not found.';

  @override
  String get errorProposalNotUnderReview => 'The proposal is not under review.';

  @override
  String get validationInvalidSlot => 'Invalid angle.';

  @override
  String get validationExtraPhotosLimit =>
      'You have already sent the maximum number of extra photos.';

  @override
  String get validationPhotoTooOld =>
      'This photo was taken more than 7 days ago. Take a new one.';

  @override
  String get validationPhotoInFuture =>
      'The photo date is invalid. Check your phone\'s clock.';

  @override
  String get validationInvalidCoordinates => 'Invalid location.';

  @override
  String get inspectionSlotFront => 'Front';

  @override
  String get inspectionSlotRear => 'Rear';

  @override
  String get inspectionSlotLeft => 'Left side';

  @override
  String get inspectionSlotRight => 'Right side';

  @override
  String get inspectionSlotOdometer => 'Odometer';

  @override
  String get inspectionSlotChassis => 'Chassis number';

  @override
  String get inspectionSlotExtra => 'Other photo';

  @override
  String get quoteUnavailableTariffMissing =>
      'This insurer does not have a price for this product yet.';

  @override
  String get quoteUnavailableTariffInvalid =>
      'The price of this product could not be calculated.';

  @override
  String get quoteUnavailableCoverageNotOffered =>
      'This product does not include all the chosen coverages.';

  @override
  String get quoteUnavailableCoverageRequirementsNotMet =>
      'In this product, the chosen coverages require others you did not choose.';

  @override
  String get quoteUnavailableCapitalNotOffered =>
      'This product does not offer the chosen capital.';

  @override
  String get quoteUnavailableVehicleValueRequired =>
      'The commercial value of the vehicle is required.';

  @override
  String get quoteUnavailableDurationNotRated =>
      'This product is not available for the chosen duration.';

  @override
  String get quoteUnavailableNoMatchingRow =>
      'This insurer does not cover this vehicle or driver under these conditions.';

  @override
  String get quoteUnavailableInsurerTimeout =>
      'The insurer did not answer in time.';

  @override
  String get quoteUnavailableInsurerUnavailable =>
      'The insurer is unavailable at the moment.';

  @override
  String get quoteUnavailableInsurerDeclined =>
      'The insurer did not make an offer.';

  @override
  String get quoteUnavailableExternalInvalidResponse =>
      'The insurer returned an invalid answer.';

  @override
  String get quoteUnavailableEvaluationError =>
      'The price could not be calculated.';

  @override
  String get coverageMtpl => 'Compulsory third-party liability';

  @override
  String get coverageVtpl => 'Voluntary third-party liability';

  @override
  String get coverageOdCollision => 'Collision and overturning';

  @override
  String get coverageOdGlass => 'Glass breakage';

  @override
  String get coverageOdFire => 'Fire, lightning and explosion';

  @override
  String get coverageOdNature => 'Natural events';

  @override
  String get coverageOdTheft => 'Theft or robbery';

  @override
  String get coverageOccupants => 'Occupants';

  @override
  String get coverageAssistance => 'Roadside assistance';

  @override
  String get coverageLegal => 'Legal protection';

  @override
  String get coverageSrcc => 'Strikes, riots and civil commotion';

  @override
  String get coverageCrossBorder => 'Driving abroad';

  @override
  String get quoteWizardTitle => 'Get a quote';

  @override
  String quoteStepOf(int step, int total) {
    return 'Step $step of $total';
  }

  @override
  String get quoteStepVehicle => 'Vehicle and driver';

  @override
  String get quoteStepType => 'Type of insurance';

  @override
  String get quoteStepCoverages => 'Coverages';

  @override
  String get quoteStepPeriod => 'Start and duration';

  @override
  String get quoteStepSummary => 'Summary';

  @override
  String get quoteBack => 'Back';

  @override
  String get quoteContinue => 'Continue';

  @override
  String get quoteVehicleLabel => 'Vehicle';

  @override
  String quoteVehicleLine(String make, String model, int year) {
    return '$make $model · $year';
  }

  @override
  String get quoteNoVehicles => 'You have no registered vehicles yet.';

  @override
  String get quoteAddVehicle => 'Add vehicle';

  @override
  String get quoteDriverLabel => 'Driver';

  @override
  String get quoteNoDrivers => 'You have no registered drivers yet.';

  @override
  String get quoteAddDriver => 'Add driver';

  @override
  String get quoteStartDateMoved => 'We updated the start date to today.';

  @override
  String get quoteTypeMtplOnly => 'Third-party liability only';

  @override
  String get quoteTypeMtplOnlyText =>
      'The compulsory insurance: covers the damage you cause to others.';

  @override
  String get quoteTypeComprehensive => 'Third-party liability + own damage';

  @override
  String get quoteTypeComprehensiveText =>
      'Also covers the damage to your vehicle.';

  @override
  String get quoteTypeTemporary => 'Temporary insurance';

  @override
  String get quoteTypeTemporaryText =>
      'Third-party liability for 7 to 60 days, or one year.';

  @override
  String get quoteNeedsCommercialValue =>
      'Needs the vehicle’s commercial value';

  @override
  String get quoteSetCommercialValue => 'Enter value';

  @override
  String get quoteCoverageMandatory => 'Mandatory';

  @override
  String quoteCoverageAvailableIn(int count, int total) {
    return 'Available in $count of $total products';
  }

  @override
  String get quoteCoverageCapital => 'Sum insured';

  @override
  String quoteCapitalPerPerson(String capital) {
    return '$capital per person';
  }

  @override
  String get quoteTheftNotice =>
      'Theft or robbery always includes collision, fire and natural events.';

  @override
  String get quoteNoProducts =>
      'There are currently no products of this type for this vehicle.';

  @override
  String get quoteStartDate => 'Start date';

  @override
  String get quoteDuration => 'Duration';

  @override
  String quoteDurationDays(int count) {
    return '$count days';
  }

  @override
  String get quoteDurationYear => '1 year';

  @override
  String quoteEndsOn(String date) {
    return 'Ends on $date';
  }

  @override
  String get quoteChange => 'Change';

  @override
  String quoteDriverAges(int age, int years) {
    return '$age years old, licence held for $years years';
  }

  @override
  String quotePeriodRange(String start, String end) {
    return '$start to $end';
  }

  @override
  String get quoteRequesting => 'Asking the insurers for offers…';

  @override
  String quoteRetryInMinutes(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: 'Try again in $count minutes.',
      one: 'Try again in 1 minute.',
    );
    return '$_temp0';
  }

  @override
  String get quoteResultsTitle => 'Offers';

  @override
  String get quoteChangeSimulation => 'Change quote';

  @override
  String quoteSavedUntil(String date) {
    return 'This quote is saved in My quotes and is valid until $date.';
  }

  @override
  String get quoteByBroker => 'Quote prepared by your intermediary.';

  @override
  String get quoteGoToMyQuotes => 'See my quotes';

  @override
  String quoteIncludesCharges(String amount) {
    return 'Includes $amount in charges';
  }

  @override
  String quoteDeductible(String value) {
    return 'Deductible $value';
  }

  @override
  String quoteDeductiblePercent(String percent) {
    return '$percent%';
  }

  @override
  String quoteDeductiblePercentMin(String percent, String min) {
    return '$percent% (min. $min)';
  }

  @override
  String get quoteNoDeductible => 'No deductible';

  @override
  String quoteValidUntil(String date) {
    return 'Valid until $date';
  }

  @override
  String get quoteCheapest => 'Cheapest';

  @override
  String get quoteViewDetail => 'See details';

  @override
  String get quoteCompare => 'Compare';

  @override
  String get quoteUsed => 'Used in a proposal';

  @override
  String get quoteExpiredLabel => 'Expired';

  @override
  String get quoteNotAvailableLabel => 'No offer';

  @override
  String quoteCompareSelected(int count) {
    return '$count selected (maximum 3)';
  }

  @override
  String get quoteCompareMax => 'You can compare up to 3 offers';

  @override
  String quoteUnavailableTitle(int count) {
    return 'No offer ($count)';
  }

  @override
  String get quoteNoOffers => 'No insurer made an offer for these conditions.';

  @override
  String quoteExpiredOn(String date) {
    return 'This quote expired on $date.';
  }

  @override
  String get quoteGeneralConditions => 'General conditions';

  @override
  String get quotePreContractInfo => 'Pre-contractual information';

  @override
  String get compareTitle => 'Compare offers';

  @override
  String get compareInsurer => 'Insurer';

  @override
  String get compareProduct => 'Product';

  @override
  String get compareTotal => 'Total premium';

  @override
  String get compareCommercialPremium => 'Commercial premium';

  @override
  String get compareNotIncluded => 'Not included';

  @override
  String get compareValidUntil => 'Valid until';

  @override
  String get compareDocuments => 'Documents';

  @override
  String compareCoverageWithDeductible(
    String capital,
    String deductible,
    String premium,
  ) {
    return '$capital · deductible $deductible · $premium';
  }

  @override
  String compareCoverageNoDeductible(String capital, String premium) {
    return '$capital · $premium';
  }

  @override
  String get quoteDetailTitle => 'Offer details';

  @override
  String quoteProductVersion(String product, int version) {
    return '$product · version $version';
  }

  @override
  String get quoteDetailCoverages => 'Coverages';

  @override
  String quoteDetailCapital(String value) {
    return 'Sum insured: $value';
  }

  @override
  String get quoteDetailPremium => 'Premium';

  @override
  String get quoteRoundingAdjustment => 'Rounding adjustment';

  @override
  String get quoteDetailPeriod => 'Period';

  @override
  String get quoteDetailValidity => 'Validity';

  @override
  String get myQuotesTitle => 'My quotes';

  @override
  String get myQuotesEmpty => 'You have not requested any quotes yet.';

  @override
  String get myQuotesByBroker => 'By your intermediary';

  @override
  String myQuotesStart(String date) {
    return 'Starts on $date';
  }

  @override
  String myQuotesOffers(int count) {
    String _temp0 = intl.Intl.pluralLogic(
      count,
      locale: localeName,
      other: '$count offers',
      one: '1 offer',
      zero: 'No offers',
    );
    return '$_temp0';
  }

  @override
  String myQuotesFrom(String amount) {
    return 'from $amount';
  }

  @override
  String get myQuotesSeeAll => 'See all';

  @override
  String get errorKycNotVerified =>
      'The customer\'s identity has not been verified yet.';

  @override
  String get errorInspectionNotApproved =>
      'The inspection has not been approved yet.';

  @override
  String get errorClientAccountDisabled =>
      'This customer\'s account is disabled. Contact MoSeguros support.';

  @override
  String get validationInvalidRules => 'Invalid rule.';

  @override
  String get errorPaymentMethodNotFound => 'Payment method not found.';

  @override
  String get errorPaymentMethodIncomplete =>
      'Some details are missing to enable this payment method. Fix the highlighted fields.';

  @override
  String get errorSecretStoreUnavailable =>
      'The secret store is unavailable. Nothing was saved; try again in a moment.';

  @override
  String get validationInvalidFormat => 'Invalid format.';

  @override
  String get validationDirectoryConflict =>
      'The inbound, processed and error folders must be different.';

  @override
  String get validationInvalidPublicKey =>
      'Invalid public key (RSA in PEM or Base64).';

  @override
  String get validationMinGreaterThanMax =>
      'The minimum cannot be greater than the maximum.';

  @override
  String get validationEntityInUse =>
      'This entity is already used by another insurer.';

  @override
  String get errorIdempotencyKeyRequired =>
      'The request could not be identified. Try again.';

  @override
  String get errorIdempotencyKeyReused =>
      'This request was already sent with other data. Go back and try again.';

  @override
  String get errorPaymentTargetNotPayable =>
      'This proposal can no longer be paid.';

  @override
  String get errorPaymentTargetAlreadyPaid => 'This proposal is already paid.';

  @override
  String get errorPaymentMethodNotAvailable =>
      'This payment method is not available for this insurer or this amount.';

  @override
  String get errorPaymentInProgress =>
      'An M-Pesa payment is already in progress. Confirm it on your phone or wait.';

  @override
  String get errorPaymentIntentNotFound => 'Payment not found.';

  @override
  String get errorNotAReferencePayment =>
      'This payment is not by entity and reference.';

  @override
  String get errorReferenceNotActive => 'This reference is no longer active.';

  @override
  String get errorReferenceSmsRateLimited =>
      'You have already sent this reference by SMS 3 times.';

  @override
  String get errorReferenceGenerationFailed =>
      'The reference could not be generated. Try again in a moment.';

  @override
  String get errorCustomerHasNoMsisdn =>
      'The customer has no mobile number to receive the link.';

  @override
  String get errorPaymentLinkRateLimited =>
      'You have already sent the payment link 3 times in the last 24 hours.';

  @override
  String get errorExportTooLarge =>
      'The export has too many rows. Use narrower filters.';

  @override
  String get validationMsisdnNotMpesa =>
      'This is not an M-Pesa number (it must start with 84 or 85).';

  @override
  String get validationInvalidUuid => 'Invalid identifier.';

  @override
  String get paymentStatusCreated => 'Created';

  @override
  String get paymentStatusPending => 'Pending';

  @override
  String get paymentStatusUnknown => 'Being confirmed';

  @override
  String get paymentStatusSucceeded => 'Paid';

  @override
  String get paymentStatusFailed => 'Failed';

  @override
  String get paymentStatusExpired => 'Expired';

  @override
  String get paymentStatusCancelled => 'Cancelled';

  @override
  String get paymentStatusRefunded => 'Refunded';

  @override
  String get paymentMethodMpesa => 'M-Pesa';

  @override
  String get paymentMethodBankReference => 'Entity and reference';

  @override
  String get paymentFailureCustomerCancelled => 'Cancelled on the phone.';

  @override
  String get paymentFailureInsufficientBalance => 'Insufficient balance.';

  @override
  String get paymentFailureInvalidMsisdn => 'Invalid M-Pesa number.';

  @override
  String get paymentFailureProviderRejected => 'The payment was declined.';

  @override
  String get paymentFailureProviderConfigError =>
      'The insurer\'s payment method has a configuration problem.';

  @override
  String get paymentFailureProviderUnavailable =>
      'The payment service is unavailable.';

  @override
  String get paymentFailureTimeoutExpired =>
      'The payment was not confirmed in time.';

  @override
  String get paymentFailureExpired => 'The payment deadline has passed.';

  @override
  String get paymentFailureSuperseded => 'Replaced by another payment.';

  @override
  String get paymentFailureManual => 'Resolved by the insurer.';

  @override
  String get proposalUnderwritingFailureAutoAcceptDisabled =>
      'Automatic acceptance is turned off for the product';

  @override
  String get proposalUnderwritingFailureKycRequired =>
      'Identity not verified and premium above the simplified KYC limit';

  @override
  String get proposalUnderwritingFailureDeclarationPositive =>
      'Some risk declarations were answered \"yes\"';

  @override
  String get proposalUnderwritingFailureInspectionNotApproved =>
      'Inspection not approved yet';

  @override
  String get proposalUnderwritingFailureVehicleValueMissing =>
      'Vehicle value not declared';

  @override
  String get proposalUnderwritingFailureVehicleValueAboveLimit =>
      'Vehicle value above the automatic acceptance limit';

  @override
  String get proposalRejectionReasonRiskNotAccepted => 'Risk not accepted';

  @override
  String get proposalRejectionReasonKycFailed => 'Identity not confirmed';

  @override
  String get proposalRejectionReasonInspectionFailed =>
      'Inspection not approved';

  @override
  String get proposalRejectionReasonFalseDeclaration =>
      'Incorrect declarations';

  @override
  String get proposalRejectionReasonDocumentsInvalid => 'Invalid documents';

  @override
  String get proposalRejectionReasonOther => 'Other reason';

  @override
  String get proposalInfoRequestItemDeclarations => 'Risk declarations';

  @override
  String get proposalInfoRequestItemSuitability => 'Suitability questionnaire';

  @override
  String get proposalInfoRequestItemInspection => 'Inspection';

  @override
  String get proposalInfoRequestItemKyc => 'Identity check';

  @override
  String get proposalInfoRequestItemPolicyholder => 'Policyholder details';

  @override
  String get proposalInfoRequestItemOther => 'Other';

  @override
  String get proposalSlaStatusOnTime => 'On time';

  @override
  String get proposalSlaStatusAlert => 'Deadline approaching';

  @override
  String get proposalSlaStatusOverdue => 'Overdue';

  @override
  String get proposalStatusDraft => 'Draft';

  @override
  String get proposalStatusUnderReview => 'Under review';

  @override
  String get proposalStatusInfoRequested => 'Information requested';

  @override
  String get proposalStatusAccepted => 'Accepted';

  @override
  String get proposalStatusRejected => 'Not accepted';

  @override
  String get proposalStatusExpired => 'Expired';

  @override
  String get proposalStatusPaid => 'Paid';

  @override
  String get proposalStatusConverted => 'Policy issued';

  @override
  String get homeProposalsTitle => 'Proposals in progress';

  @override
  String get homeProposalsSeeAll => 'See all';

  @override
  String inspectionPendingUploads(int n) {
    String _temp0 = intl.Intl.pluralLogic(
      n,
      locale: localeName,
      other: '$n photos waiting to upload',
      one: '1 photo waiting to upload',
    );
    return '$_temp0';
  }

  @override
  String get proposalsTitle => 'Proposals';

  @override
  String get proposalsEmpty => 'You don\'t have any proposals yet.';

  @override
  String get proposalsFilterActive => 'In progress';

  @override
  String get proposalsFilterAll => 'All';

  @override
  String proposalExpiresOn(String date) {
    return 'Expires on $date';
  }

  @override
  String get proposalContinue => 'Continue';

  @override
  String get proposalView => 'View';

  @override
  String get proposalCreating => 'Preparing the proposal…';

  @override
  String proposalTitle(String number) {
    return 'Proposal $number';
  }

  @override
  String get proposalChangedElsewhere => 'The proposal was changed elsewhere.';

  @override
  String get quoteAcquire => 'Buy policy';

  @override
  String proposalStepOf(int step, int total, String name) {
    return 'Step $step of $total · $name';
  }

  @override
  String get proposalStepPolicyholder => 'Policyholder and date';

  @override
  String get proposalStepDeclarations => 'Declarations';

  @override
  String get proposalStepSuitability => 'Suitability';

  @override
  String get proposalStepInspection => 'Inspection';

  @override
  String get proposalStepIdentity => 'Identity';

  @override
  String get proposalStepReview => 'Review';

  @override
  String get proposalBack => 'Back';

  @override
  String get proposalNext => 'Continue';

  @override
  String get proposalCoverages => 'Covers';

  @override
  String get proposalTotal => 'Total';

  @override
  String get proposalPolicyholderTitle => 'Policyholder';

  @override
  String get proposalPolicyholderFromProfile =>
      'These details come from your profile.';

  @override
  String get proposalEditProfile => 'Edit profile';

  @override
  String get proposalRefreshFromProfile => 'Update from profile';

  @override
  String get proposalStartDate => 'Start date';

  @override
  String proposalEndDate(String date) {
    return 'Ends: $date';
  }

  @override
  String get proposalVehicle => 'Vehicle';

  @override
  String get proposalDriver => 'Driver';

  @override
  String get declarationClaimsLast3YearsQuestion =>
      'In the last 3 years, have you had any accident or claim involving a vehicle, as a driver or as an owner?';

  @override
  String get declarationClaimsLast3YearsClaimsCount => 'How many?';

  @override
  String get declarationClaimsLast3YearsDetails =>
      'Briefly describe when, what happened and whether anyone was injured.';

  @override
  String get declarationPreviousRefusalQuestion =>
      'Has any insurer ever refused, cancelled or declined to renew a motor insurance policy of yours?';

  @override
  String get declarationPreviousRefusalDetails => 'Which insurer and why?';

  @override
  String declarationUsageDifferentQuestion(String usage) {
    return 'Will the vehicle be used for any purpose other than \"$usage\"? For example, carrying passengers or goods for payment.';
  }

  @override
  String get declarationUsageDifferentDetails => 'For what other purpose?';

  @override
  String get declarationVehicleModifiedQuestion =>
      'Has the vehicle been modified from its factory specification (engine, suspension, bodywork, gas conversion or other)?';

  @override
  String get declarationVehicleModifiedDetails => 'What modifications?';

  @override
  String get declarationNoviceDriversQuestion =>
      'Will anyone who has held a driving licence for less than 2 years drive this vehicle regularly?';

  @override
  String get declarationNoviceDriversDetails =>
      'Who, and how long have they held a licence?';

  @override
  String get declarationTruthfulness =>
      'I declare that my answers are true and complete. I understand that false statements or omissions may lead to the contract being voided or claims being refused.';

  @override
  String get proposalAnswerRequired => 'Choose an answer.';

  @override
  String get proposalDetailsLength => 'Write between 10 and 500 characters.';

  @override
  String get proposalClaimsCountRange => 'Enter a number from 1 to 20.';

  @override
  String get proposalTruthfulnessRequired =>
      'Confirm the declaration to continue.';

  @override
  String get suitabilityIntro =>
      'These questions help confirm that the proposal matches what you are looking for.';

  @override
  String get suitabilityNeedQuestion =>
      'What protection are you looking for for this vehicle?';

  @override
  String get suitabilityNeedOptionMandatoryOnly =>
      'Only the mandatory insurance: damage I cause to others';

  @override
  String get suitabilityNeedOptionOwnDamage =>
      'Also damage to my own vehicle in an accident';

  @override
  String get suitabilityNeedOptionFull => 'Also theft, fire and natural events';

  @override
  String get suitabilityNeedOptionUnsure => 'I\'m not sure';

  @override
  String get suitabilityFinancedQuestion =>
      'Are you still paying for the vehicle (loan or lease)?';

  @override
  String get suitabilityCanAffordRepairQuestion =>
      'If your vehicle were damaged in an accident you caused, could you pay for the repair without insurance?';

  @override
  String get suitabilityCrossBorderQuestion =>
      'Will you drive this vehicle outside Mozambique during the insurance period?';

  @override
  String get suitabilityDeductibleQuestion =>
      'Would you accept paying part of each repair (an excess) in exchange for a lower premium?';

  @override
  String get suitabilityAnswerYes => 'Yes';

  @override
  String get suitabilityAnswerNo => 'No';

  @override
  String get suitabilityAnswerUnsure => 'I\'m not sure';

  @override
  String get suitabilityReasonNeedNotCoveredOwnDamage =>
      'You said you want cover for your own vehicle, but this proposal does not cover damage to it.';

  @override
  String get suitabilityReasonNeedNotCoveredTheftFire =>
      'You said you want cover against theft, fire and natural events, but this proposal does not include all of them.';

  @override
  String get suitabilityReasonCoverAboveNeed =>
      'You said you only want the mandatory insurance, but this proposal includes own-damage cover, which increases the premium.';

  @override
  String get suitabilityReasonNeedUnsure =>
      'You are not sure what cover you need. Read the cover summary and the pre-contract information before continuing.';

  @override
  String get suitabilityReasonFinancedWithoutOwnDamage =>
      'You are still paying for the vehicle and this proposal does not cover damage to it. Check whether your loan or lease requires that cover.';

  @override
  String get suitabilityReasonCannotAffordRepair =>
      'You said you could not pay to repair your vehicle, but this proposal does not cover damage to it.';

  @override
  String get suitabilityReasonCrossBorderNotCovered =>
      'You will drive outside Mozambique, but this proposal does not include cover abroad.';

  @override
  String get suitabilityReasonDeductibleNotWanted =>
      'You said you do not want an excess, but at least one own-damage cover in this proposal has one.';

  @override
  String get suitabilityAcknowledge =>
      'I have read the warnings and want to continue with this proposal';

  @override
  String get suitabilityRequestOtherQuote => 'Request another quote';

  @override
  String get suitabilityAcknowledged =>
      'You confirmed that you read the warnings.';

  @override
  String get inspectionTitle => 'Inspection';

  @override
  String get inspectionIntro =>
      'For own-damage cover, the insurer needs photos of the vehicle. Take them in daylight, with the vehicle clean and fully in the picture.';

  @override
  String get inspectionInstructionFront =>
      'The whole front of the vehicle, with the number plate readable.';

  @override
  String get inspectionInstructionRear =>
      'The whole rear, with the number plate readable.';

  @override
  String get inspectionInstructionLeft =>
      'The whole left side, from end to end.';

  @override
  String get inspectionInstructionRight =>
      'The whole right side, from end to end.';

  @override
  String get inspectionInstructionOdometer =>
      'The dashboard with the odometer readable, with the ignition on.';

  @override
  String get inspectionInstructionChassis =>
      'The chassis number on the vehicle (usually on the windscreen or the driver\'s door).';

  @override
  String get inspectionInstructionExtra =>
      'Any existing damage, or other details.';

  @override
  String get inspectionSideHint => 'Step back until the whole vehicle fits';

  @override
  String get inspectionToTake => 'Not taken';

  @override
  String get inspectionQueued => 'Queued';

  @override
  String get inspectionUploading => 'Uploading';

  @override
  String get inspectionUploaded => 'Uploaded';

  @override
  String get inspectionFailed => 'Error';

  @override
  String get inspectionStart => 'Start inspection';

  @override
  String get inspectionOpen => 'Do the inspection';

  @override
  String inspectionProgress(int done, int total) {
    return '$done of $total angles';
  }

  @override
  String get inspectionOdometer => 'Current mileage';

  @override
  String get inspectionOdometerInvalid => 'Enter a value from 0 to 2,000,000.';

  @override
  String get inspectionSubmit => 'Send inspection';

  @override
  String get inspectionWillSubmitWhenOnline =>
      'The inspection will be sent when you are back online.';

  @override
  String get inspectionSubmitQueued =>
      'The inspection is sent to the insurer as soon as the photos finish uploading.';

  @override
  String get inspectionStatusPending => 'Not sent';

  @override
  String get inspectionStatusSubmitted => 'Sent to the insurer';

  @override
  String get inspectionStatusApproved => 'Approved';

  @override
  String get inspectionStatusRejected => 'New photos requested';

  @override
  String get inspectionOnSiteRequested =>
      'You asked for an on-site inspection. The insurer will contact you.';

  @override
  String inspectionRejectedBanner(String reason) {
    return 'The insurer asked for new photos: $reason';
  }

  @override
  String get inspectionRetakeMarker => 'Retake';

  @override
  String get inspectionTakeAgain => 'Take again';

  @override
  String get inspectionDiscard => 'Discard';

  @override
  String get inspectionExtras => 'Extra photos';

  @override
  String get inspectionAddExtra => 'Add photo';

  @override
  String get inspectionOnSiteLink => 'I prefer an on-site inspection';

  @override
  String get inspectionOnSiteTitle => 'On-site inspection';

  @override
  String get inspectionOnSiteNotes => 'Preferred place and times';

  @override
  String get inspectionOnSiteSend => 'Request on-site inspection';

  @override
  String get inspectionOfflineBanner =>
      'No connection. The photos are kept on your phone and sent when the connection is back.';

  @override
  String inspectionMissingSlots(String slots) {
    return 'Missing photos: $slots';
  }

  @override
  String get inspectionUsePhoto => 'Use photo';

  @override
  String get inspectionNotEditable =>
      'The inspection has already been sent to the insurer.';

  @override
  String get registrationDocCameraPermissionDenied =>
      'We need the camera to photograph the registration document.';

  @override
  String get locationRationale =>
      'Your location helps the insurer confirm where the photos were taken. It is optional.';

  @override
  String get locationContinue => 'Continue';

  @override
  String get kycTitle => 'Identity check';

  @override
  String get kycStateVerified => 'Identity verified';

  @override
  String get kycStatePending => 'Being checked';

  @override
  String get kycStateRejected => 'We could not verify your identity.';

  @override
  String get kycStateOutdated =>
      'Your profile details have changed. Verify your identity again.';

  @override
  String get kycRequiredForAutoAcceptance =>
      'Verify your identity so that the proposal can be accepted straight away. Without it, the insurer reviews it, which can take up to 15 days.';

  @override
  String get kycOptional => 'Optional: the check speeds up future proposals.';

  @override
  String get kycReasonDocumentUnreadable =>
      'The document photos are not readable.';

  @override
  String get kycReasonDocumentExpired => 'The document has expired.';

  @override
  String get kycReasonDataMismatch =>
      'The document details do not match your profile.';

  @override
  String get kycReasonSelfieMismatch =>
      'The selfie does not confirm that you are the document holder.';

  @override
  String get kycReasonOther => 'Other reason.';

  @override
  String get kycDocFront => 'Front of the document';

  @override
  String get kycDocBack => 'Back of the document';

  @override
  String get kycSelfie => 'Selfie';

  @override
  String get kycTakePhoto => 'Take photo';

  @override
  String get kycRetakePhoto => 'Retake';

  @override
  String get kycSubmit => 'Send';

  @override
  String get kycNotNow => 'Not now';

  @override
  String get kycNoProposal =>
      'The identity check is requested when you make a proposal.';

  @override
  String get kycDocumentInstruction =>
      'Place the document inside the frame, without glare, with all corners visible.';

  @override
  String get kycSelfieInstruction =>
      'Look straight at the camera, without sunglasses or a hat, somewhere with good light.';

  @override
  String get kycCameraPermissionDenied =>
      'To verify your identity, MoSeguros needs access to the camera.';

  @override
  String get kycPhotosMissing => 'Take the requested photos before sending.';

  @override
  String get proposalDeclarationsAnswers => 'Declaration answers';

  @override
  String get proposalSuitabilityAnswers => 'Suitability answers';

  @override
  String get proposalDocuments => 'Product documents';

  @override
  String get proposalMissingTitle => 'Still to complete:';

  @override
  String get proposalDocumentsAcknowledged =>
      'I have received and read the General Conditions and the Pre-contract Information.';

  @override
  String get proposalSignAndSend => 'Sign and send to the insurer';

  @override
  String get proposalSendOnly => 'Send to the insurer';

  @override
  String get proposalWaitUploads => 'Wait for the inspection photos to upload.';

  @override
  String get errorNoConnection => 'No internet connection.';

  @override
  String get signTitle => 'Sign the proposal';

  @override
  String signCodeSent(String channel, String destination) {
    return 'We sent a code by $channel to $destination.';
  }

  @override
  String get signViewPdf => 'View the proposal (PDF)';

  @override
  String signFallback(String channel) {
    return 'We could not send it through the usual channel. We sent the code by $channel.';
  }

  @override
  String get signSubmitting => 'Sending to the insurer…';

  @override
  String get proposalTimelineCreated => 'Created';

  @override
  String get proposalTimelineSubmitted => 'Sent to the insurer';

  @override
  String get proposalTimelineDecided => 'Decision';

  @override
  String proposalUnderReviewText(String insurer, String date) {
    return '$insurer is reviewing your proposal. It has until $date to reply.';
  }

  @override
  String proposalUnderReviewNoDue(String insurer) {
    return '$insurer is reviewing your proposal.';
  }

  @override
  String get proposalAcceptedText => 'Proposal accepted.';

  @override
  String get proposalPaymentSoon => 'Payment will be available soon.';

  @override
  String get proposalRejectedText => 'The proposal was not accepted.';

  @override
  String get proposalExpiredText => 'This proposal has expired.';

  @override
  String get proposalDownloadSigned => 'Download the signed proposal (PDF)';

  @override
  String get proposalInfoRequestedTitle =>
      'The insurer asked for more information';

  @override
  String logoutPendingUploads(int n) {
    String _temp0 = intl.Intl.pluralLogic(
      n,
      locale: localeName,
      other:
          'You have $n photos waiting to upload. If you log out, you will lose them.',
      one:
          'You have 1 photo waiting to upload. If you log out, you will lose it.',
    );
    return '$_temp0';
  }

  @override
  String get logoutAnyway => 'Log out anyway';

  @override
  String get cameraUnavailableNoGallery => 'Could not open the camera.';

  @override
  String get inspectionRemove => 'Remove';

  @override
  String get inspectionRemoveConfirm =>
      'Remove this photo from the inspection?';
}
