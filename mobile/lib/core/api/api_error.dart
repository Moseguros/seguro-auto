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
  _ => l10n.errorUnknown,
};

/// Mensagem traduzida para um erro de campo (`FieldErrorCodes`, em `errors[].code`).
String fieldErrorMessage(AppLocalizations l10n, String code) => switch (code) {
  'INVALID_CURRENCY' => l10n.validationInvalidCurrency,
  'INVALID_MSISDN' => l10n.validationInvalidMsisdn,
  'INVALID_NUIT' => l10n.validationInvalidNuit,
  'INVALID_PLATE' => l10n.validationInvalidPlate,
  _ => l10n.errorUnknown,
};
