// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_insurer_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentInsurerRef extends PaymentInsurerRef {
  @override
  final String? displayName;
  @override
  final String id;

  factory _$PaymentInsurerRef(
          [void Function(PaymentInsurerRefBuilder)? updates]) =>
      (PaymentInsurerRefBuilder()..update(updates))._build();

  _$PaymentInsurerRef._({this.displayName, required this.id}) : super._();
  @override
  PaymentInsurerRef rebuild(void Function(PaymentInsurerRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentInsurerRefBuilder toBuilder() =>
      PaymentInsurerRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentInsurerRef &&
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
    return (newBuiltValueToStringHelper(r'PaymentInsurerRef')
          ..add('displayName', displayName)
          ..add('id', id))
        .toString();
  }
}

class PaymentInsurerRefBuilder
    implements Builder<PaymentInsurerRef, PaymentInsurerRefBuilder> {
  _$PaymentInsurerRef? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  PaymentInsurerRefBuilder() {
    PaymentInsurerRef._defaults(this);
  }

  PaymentInsurerRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentInsurerRef other) {
    _$v = other as _$PaymentInsurerRef;
  }

  @override
  void update(void Function(PaymentInsurerRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentInsurerRef build() => _build();

  _$PaymentInsurerRef _build() {
    final _$result = _$v ??
        _$PaymentInsurerRef._(
          displayName: displayName,
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PaymentInsurerRef', 'id'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
