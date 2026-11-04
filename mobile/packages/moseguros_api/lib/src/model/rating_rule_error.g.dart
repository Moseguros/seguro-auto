// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rating_rule_error.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RatingRuleError extends RatingRuleError {
  @override
  final String code;
  @override
  final String field;
  @override
  final String? message;

  factory _$RatingRuleError([void Function(RatingRuleErrorBuilder)? updates]) =>
      (RatingRuleErrorBuilder()..update(updates))._build();

  _$RatingRuleError._({required this.code, required this.field, this.message})
      : super._();
  @override
  RatingRuleError rebuild(void Function(RatingRuleErrorBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RatingRuleErrorBuilder toBuilder() => RatingRuleErrorBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RatingRuleError &&
        code == other.code &&
        field == other.field &&
        message == other.message;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, field.hashCode);
    _$hash = $jc(_$hash, message.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RatingRuleError')
          ..add('code', code)
          ..add('field', field)
          ..add('message', message))
        .toString();
  }
}

class RatingRuleErrorBuilder
    implements Builder<RatingRuleError, RatingRuleErrorBuilder> {
  _$RatingRuleError? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _field;
  String? get field => _$this._field;
  set field(String? field) => _$this._field = field;

  String? _message;
  String? get message => _$this._message;
  set message(String? message) => _$this._message = message;

  RatingRuleErrorBuilder() {
    RatingRuleError._defaults(this);
  }

  RatingRuleErrorBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _field = $v.field;
      _message = $v.message;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RatingRuleError other) {
    _$v = other as _$RatingRuleError;
  }

  @override
  void update(void Function(RatingRuleErrorBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RatingRuleError build() => _build();

  _$RatingRuleError _build() {
    final _$result = _$v ??
        _$RatingRuleError._(
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'RatingRuleError', 'code'),
          field: BuiltValueNullFieldError.checkNotNull(
              field, r'RatingRuleError', 'field'),
          message: message,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
