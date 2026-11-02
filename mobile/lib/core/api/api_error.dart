import 'dart:convert';

import 'package:dio/dio.dart';

import '../../l10n/app_localizations.dart';

/// Código de erro a traduzir: o `code` do ProblemDetail (RFC 7807),
/// `NETWORK_ERROR` sem resposta do servidor, ou `UNKNOWN` nos restantes casos.
String apiErrorCode(Object error) {
  if (error is DioException) {
    final response = error.response;
    if (response == null) return 'NETWORK_ERROR';
    final code = _problemCode(response.data);
    if (code != null && code.isNotEmpty) return code;
  }
  return 'UNKNOWN';
}

String? _problemCode(Object? data) {
  Object? body = data;
  if (body is String) {
    try {
      body = jsonDecode(body);
    } on FormatException {
      return null;
    }
  }
  if (body is Map<String, dynamic>) {
    final code = body['code'];
    if (code is String) return code;
  }
  return null;
}

/// Mensagem traduzida para um código de erro de topo (`ErrorCodes`).
String apiErrorMessage(AppLocalizations l10n, String code) => switch (code) {
  'NETWORK_ERROR' => l10n.errorNetworkError,
  'VALIDATION_ERROR' => l10n.errorValidationError,
  'NOT_FOUND' => l10n.errorNotFound,
  'UNAUTHORIZED' => l10n.errorUnauthorized,
  'FORBIDDEN' => l10n.errorForbidden,
  'INTERNAL_ERROR' => l10n.errorInternalError,
  'INVALID_CURRENCY' => l10n.errorInvalidCurrency,
  'INVALID_MSISDN' => l10n.errorInvalidMsisdn,
  'INVALID_NUIT' => l10n.errorInvalidNuit,
  'INVALID_PLATE' => l10n.errorInvalidPlate,
  'INVALID_EMAIL' => l10n.errorInvalidEmail,
  'INVALID_PHONE' => l10n.errorInvalidPhone,
  'TENANT_REQUIRED' => l10n.errorTenantRequired,
  'DECRYPTION_FAILED' => l10n.errorDecryptionFailed,
  'INVALID_STATE_TRANSITION' => l10n.errorInvalidStateTransition,
  'LICENSE_NUMBER_REQUIRED' => l10n.errorLicenseNumberRequired,
  'CHANNELS_EMPTY' => l10n.errorChannelsEmpty,
  'CHANNELS_DUPLICATED' => l10n.errorChannelsDuplicated,
  'DEFAULT_CHANNEL_NOT_ENABLED' => l10n.errorDefaultChannelNotEnabled,
  'FALLBACK_ORDER_MISMATCH' => l10n.errorFallbackOrderMismatch,
  'OUT_OF_RANGE' => l10n.errorOutOfRange,
  'REFRESH_TOKEN_INVALID' => l10n.errorRefreshTokenInvalid,
  'REFRESH_TOKEN_REUSED' => l10n.errorRefreshTokenReused,
  'CONCURRENT_MODIFICATION' => l10n.errorConcurrentModification,
  'OTP_CHANNEL_CHOICE_DISABLED' => l10n.errorOtpChannelChoiceDisabled,
  'OTP_CHANNEL_NOT_AVAILABLE' => l10n.errorOtpChannelNotAvailable,
  'OTP_RATE_LIMITED' => l10n.errorOtpRateLimited,
  'OTP_RESEND_TOO_SOON' => l10n.errorOtpResendTooSoon,
  'OTP_DELIVERY_FAILED' => l10n.errorOtpDeliveryFailed,
  'OTP_CHALLENGE_NOT_FOUND' => l10n.errorOtpChallengeNotFound,
  'OTP_ALREADY_USED' => l10n.errorOtpAlreadyUsed,
  'OTP_SUPERSEDED' => l10n.errorOtpSuperseded,
  'OTP_EXPIRED' => l10n.errorOtpExpired,
  'OTP_INVALID' => l10n.errorOtpInvalid,
  'OTP_ATTEMPTS_EXCEEDED' => l10n.errorOtpAttemptsExceeded,
  'ACCOUNT_DISABLED' => l10n.errorAccountDisabled,
  'ORGANIZATION_NOT_FOUND' => l10n.errorOrganizationNotFound,
  'ORGANIZATION_NOT_ACTIVE' => l10n.errorOrganizationNotActive,
  'NUIT_ALREADY_IN_USE' => l10n.errorNuitAlreadyInUse,
  'SHORT_CODE_ALREADY_IN_USE' => l10n.errorShortCodeAlreadyInUse,
  'OMBUDSMAN_INVALID' => l10n.errorOmbudsmanInvalid,
  'ROLE_NOT_ALLOWED_FOR_ORGANIZATION' =>
    l10n.errorRoleNotAllowedForOrganization,
  'EMAIL_ALREADY_IN_USE' => l10n.errorEmailAlreadyInUse,
  'INVITATION_INVALID' => l10n.errorInvitationInvalid,
  'INVITATION_NOT_ACCEPTED' => l10n.errorInvitationNotAccepted,
  'TOTP_INVALID' => l10n.errorTotpInvalid,
  'PASSWORD_TOO_WEAK' => l10n.errorPasswordTooWeak,
  'INVALID_CREDENTIALS' => l10n.errorInvalidCredentials,
  'ACCOUNT_LOCKED' => l10n.errorAccountLocked,
  'ORGANIZATION_SUSPENDED' => l10n.errorOrganizationSuspended,
  'RESET_TOKEN_INVALID' => l10n.errorResetTokenInvalid,
  'USER_NOT_FOUND' => l10n.errorUserNotFound,
  'CANNOT_MODIFY_SELF' => l10n.errorCannotModifySelf,
  'LAST_MANAGER' => l10n.errorLastManager,
  'CONTACT_ALREADY_IN_USE' => l10n.errorContactAlreadyInUse,
  'CONTACT_ALREADY_VERIFIED' => l10n.errorContactAlreadyVerified,
  'LAST_CONTACT' => l10n.errorLastContact,
  _ => l10n.errorUnknown,
};

/// Mensagem traduzida para um erro de campo (`FieldErrorCodes`, em `errors[].code`).
String fieldErrorMessage(AppLocalizations l10n, String code) => switch (code) {
  'INVALID_CURRENCY' => l10n.validationInvalidCurrency,
  'INVALID_MSISDN' => l10n.validationInvalidMsisdn,
  'INVALID_NUIT' => l10n.validationInvalidNuit,
  'INVALID_PLATE' => l10n.validationInvalidPlate,
  'INVALID_EMAIL' => l10n.validationInvalidEmail,
  'INVALID_PHONE' => l10n.validationInvalidPhone,
  'LICENSE_NUMBER_REQUIRED' => l10n.validationLicenseNumberRequired,
  'CHANNELS_EMPTY' => l10n.validationChannelsEmpty,
  'CHANNELS_DUPLICATED' => l10n.validationChannelsDuplicated,
  'DEFAULT_CHANNEL_NOT_ENABLED' => l10n.validationDefaultChannelNotEnabled,
  'FALLBACK_ORDER_MISMATCH' => l10n.validationFallbackOrderMismatch,
  'OUT_OF_RANGE' => l10n.validationOutOfRange,
  'NOT_BLANK' => l10n.validationNotBlank,
  'NOT_NULL' => l10n.validationNotNull,
  'NOT_ALLOWED' => l10n.validationNotAllowed,
  'REQUIRED' => l10n.validationRequired,
  'INVALID_SORT' => l10n.validationInvalidSort,
  'TOO_SHORT' => l10n.validationTooShort,
  'TOO_LONG' => l10n.validationTooLong,
  'CONTAINS_EMAIL' => l10n.validationContainsEmail,
  'TOO_COMMON' => l10n.validationTooCommon,
  'SIZE' => l10n.validationSize,
  'PATTERN' => l10n.validationPattern,
  'TYPE_MISMATCH' => l10n.validationTypeMismatch,
  'INVALID_NAME' => l10n.validationInvalidName,
  'UNDERAGE' => l10n.validationUnderage,
  'INVALID_DATE' => l10n.validationInvalidDate,
  'INVALID_DOCUMENT_NUMBER' => l10n.validationInvalidDocumentNumber,
  _ => l10n.errorUnknown,
};
