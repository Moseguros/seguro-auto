// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'charge_value.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ChargeValue extends ChargeValue {
  @override
  final OneOf oneOf;

  factory _$ChargeValue([void Function(ChargeValueBuilder)? updates]) =>
      (ChargeValueBuilder()..update(updates))._build();

  _$ChargeValue._({required this.oneOf}) : super._();
  @override
  ChargeValue rebuild(void Function(ChargeValueBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ChargeValueBuilder toBuilder() => ChargeValueBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ChargeValue && oneOf == other.oneOf;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, oneOf.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ChargeValue')..add('oneOf', oneOf))
        .toString();
  }
}

class ChargeValueBuilder implements Builder<ChargeValue, ChargeValueBuilder> {
  _$ChargeValue? _$v;

  OneOf? _oneOf;
  OneOf? get oneOf => _$this._oneOf;
  set oneOf(OneOf? oneOf) => _$this._oneOf = oneOf;

  ChargeValueBuilder() {
    ChargeValue._defaults(this);
  }

  ChargeValueBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _oneOf = $v.oneOf;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ChargeValue other) {
    _$v = other as _$ChargeValue;
  }

  @override
  void update(void Function(ChargeValueBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ChargeValue build() => _build();

  _$ChargeValue _build() {
    final _$result = _$v ??
        _$ChargeValue._(
          oneOf: BuiltValueNullFieldError.checkNotNull(
              oneOf, r'ChargeValue', 'oneOf'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
