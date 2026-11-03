// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'rule_error.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RuleError extends RuleError {
  @override
  final String code;
  @override
  final String field;

  factory _$RuleError([void Function(RuleErrorBuilder)? updates]) =>
      (RuleErrorBuilder()..update(updates))._build();

  _$RuleError._({required this.code, required this.field}) : super._();
  @override
  RuleError rebuild(void Function(RuleErrorBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RuleErrorBuilder toBuilder() => RuleErrorBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RuleError && code == other.code && field == other.field;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, field.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RuleError')
          ..add('code', code)
          ..add('field', field))
        .toString();
  }
}

class RuleErrorBuilder implements Builder<RuleError, RuleErrorBuilder> {
  _$RuleError? _$v;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  String? _field;
  String? get field => _$this._field;
  set field(String? field) => _$this._field = field;

  RuleErrorBuilder() {
    RuleError._defaults(this);
  }

  RuleErrorBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _code = $v.code;
      _field = $v.field;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RuleError other) {
    _$v = other as _$RuleError;
  }

  @override
  void update(void Function(RuleErrorBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RuleError build() => _build();

  _$RuleError _build() {
    final _$result = _$v ??
        _$RuleError._(
          code:
              BuiltValueNullFieldError.checkNotNull(code, r'RuleError', 'code'),
          field: BuiltValueNullFieldError.checkNotNull(
              field, r'RuleError', 'field'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
