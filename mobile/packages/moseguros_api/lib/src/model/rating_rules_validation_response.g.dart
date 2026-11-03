// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_rules_validation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RatingRulesValidationResponse extends RatingRulesValidationResponse {
  @override
  final BuiltList<RatingRuleError> errors;
  @override
  final bool valid;

  factory _$RatingRulesValidationResponse(
          [void Function(RatingRulesValidationResponseBuilder)? updates]) =>
      (RatingRulesValidationResponseBuilder()..update(updates))._build();

  _$RatingRulesValidationResponse._({required this.errors, required this.valid})
      : super._();
  @override
  RatingRulesValidationResponse rebuild(
          void Function(RatingRulesValidationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingRulesValidationResponseBuilder toBuilder() =>
      RatingRulesValidationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingRulesValidationResponse &&
        errors == other.errors &&
        valid == other.valid;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, errors.hashCode);
    _$hash = $jc(_$hash, valid.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RatingRulesValidationResponse')
          ..add('errors', errors)
          ..add('valid', valid))
        .toString();
  }
}

class RatingRulesValidationResponseBuilder
    implements
        Builder<RatingRulesValidationResponse,
            RatingRulesValidationResponseBuilder> {
  _$RatingRulesValidationResponse? _$v;

  ListBuilder<RatingRuleError>? _errors;
  ListBuilder<RatingRuleError> get errors =>
      _$this._errors ??= ListBuilder<RatingRuleError>();
  set errors(ListBuilder<RatingRuleError>? errors) => _$this._errors = errors;

  bool? _valid;
  bool? get valid => _$this._valid;
  set valid(bool? valid) => _$this._valid = valid;

  RatingRulesValidationResponseBuilder() {
    RatingRulesValidationResponse._defaults(this);
  }

  RatingRulesValidationResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _errors = $v.errors.toBuilder();
      _valid = $v.valid;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RatingRulesValidationResponse other) {
    _$v = other as _$RatingRulesValidationResponse;
  }

  @override
  void update(void Function(RatingRulesValidationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingRulesValidationResponse build() => _build();

  _$RatingRulesValidationResponse _build() {
    _$RatingRulesValidationResponse _$result;
    try {
      _$result = _$v ??
          _$RatingRulesValidationResponse._(
            errors: errors.build(),
            valid: BuiltValueNullFieldError.checkNotNull(
                valid, r'RatingRulesValidationResponse', 'valid'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'errors';
        errors.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'RatingRulesValidationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
