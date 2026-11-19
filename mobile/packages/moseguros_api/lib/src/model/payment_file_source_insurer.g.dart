// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_file_source_insurer.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentFileSourceInsurer extends PaymentFileSourceInsurer {
  @override
  final String displayName;
  @override
  final String id;

  factory _$PaymentFileSourceInsurer(
          [void Function(PaymentFileSourceInsurerBuilder)? updates]) =>
      (PaymentFileSourceInsurerBuilder()..update(updates))._build();

  _$PaymentFileSourceInsurer._({required this.displayName, required this.id})
      : super._();
  @override
  PaymentFileSourceInsurer rebuild(
          void Function(PaymentFileSourceInsurerBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentFileSourceInsurerBuilder toBuilder() =>
      PaymentFileSourceInsurerBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentFileSourceInsurer &&
        displayName == other.displayName &&
        id == other.id;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentFileSourceInsurer')
          ..add('displayName', displayName)
          ..add('id', id))
        .toString();
  }
}

class PaymentFileSourceInsurerBuilder
    implements
        Builder<PaymentFileSourceInsurer, PaymentFileSourceInsurerBuilder> {
  _$PaymentFileSourceInsurer? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  PaymentFileSourceInsurerBuilder() {
    PaymentFileSourceInsurer._defaults(this);
  }

  PaymentFileSourceInsurerBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentFileSourceInsurer other) {
    _$v = other as _$PaymentFileSourceInsurer;
  }

  @override
  void update(void Function(PaymentFileSourceInsurerBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentFileSourceInsurer build() => _build();

  _$PaymentFileSourceInsurer _build() {
    final _$result = _$v ??
        _$PaymentFileSourceInsurer._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'PaymentFileSourceInsurer', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PaymentFileSourceInsurer', 'id'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
