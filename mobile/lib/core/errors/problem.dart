import 'dart:convert';

import 'package:dio/dio.dart';

import '../../l10n/app_localizations.dart';
import '../api/api_error.dart';

/// Erro de um campo (`errors[]` do ProblemDetail).
class FieldProblem {
  const FieldProblem(this.field, this.code);

  final String field;

  /// Código de `FieldErrorCodes` (ex.: `INVALID_MSISDN`).
  final String code;
}

/// Erro da API (`application/problem+json`, RFC 7807) já lido.
class Problem {
  const Problem({
    required this.status,
    required this.code,
    this.detail,
    this.errors = const [],
    this.retryAfterSeconds,
    this.attemptsRemaining,
  });

  /// Lê o erro de um pedido: `NETWORK_ERROR` sem resposta do servidor,
  /// `UNKNOWN` quando a resposta não é um ProblemDetail com `code`.
  factory Problem.from(Object error) {
    if (error is! DioException) {
      return const Problem(status: 0, code: 'UNKNOWN');
    }
    final response = error.response;
    if (response == null) {
      return const Problem(status: 0, code: 'NETWORK_ERROR');
    }

    final body = _asMap(response.data);
    final code = body['code'];
    final rawErrors = body['errors'];
    return Problem(
      status: response.statusCode ?? 0,
      code: code is String && code.isNotEmpty ? code : 'UNKNOWN',
      detail: body['detail'] is String ? body['detail'] as String : null,
      errors: [
        if (rawErrors is List)
          for (final item in rawErrors)
            if (item is Map &&
                item['field'] is String &&
                item['code'] is String)
              FieldProblem(item['field'] as String, item['code'] as String),
      ],
      retryAfterSeconds:
          _asInt(body['retryAfterSeconds']) ??
          _asInt(response.headers.value('retry-after')),
      attemptsRemaining: _asInt(body['attemptsRemaining']),
    );
  }

  /// Estado HTTP; 0 sem resposta do servidor.
  final int status;

  /// `code` do ProblemDetail, `NETWORK_ERROR` ou `UNKNOWN`.
  final String code;
  final String? detail;
  final List<FieldProblem> errors;
  final int? retryAfterSeconds;
  final int? attemptsRemaining;

  /// Mensagem para o utilizador: `error<Code>` do ARB, ou `errorUnknown`.
  String message(AppLocalizations l10n) => apiErrorMessage(l10n, code);

  /// Primeiro erro de um dos [fields], se houver.
  FieldProblem? fieldError(Iterable<String> fields) {
    for (final error in errors) {
      if (fields.contains(error.field)) return error;
    }
    return null;
  }

  static Map<String, dynamic> _asMap(Object? data) {
    Object? body = data;
    if (body is String) {
      try {
        body = jsonDecode(body);
      } on FormatException {
        return const {};
      }
    }
    return body is Map<String, dynamic> ? body : const {};
  }

  static int? _asInt(Object? value) {
    if (value is int) return value;
    if (value is num) return value.ceil();
    if (value is String) return int.tryParse(value.trim());
    return null;
  }
}
