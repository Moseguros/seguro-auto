// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_target_ref.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PaymentTargetRef extends PaymentTargetRef {
  @override
  final String id;
  @override
  final String label;
  @override
  final PaymentTargetType type;

  factory _$PaymentTargetRef(
          [void Function(PaymentTargetRefBuilder)? updates]) =>
      (PaymentTargetRefBuilder()..update(updates))._build();

  _$PaymentTargetRef._(
      {required this.id, required this.label, required this.type})
      : super._();
  @override
  PaymentTargetRef rebuild(void Function(PaymentTargetRefBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentTargetRefBuilder toBuilder() =>
      PaymentTargetRefBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentTargetRef &&
        id == other.id &&
        label == other.label &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, label.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentTargetRef')
          ..add('id', id)
          ..add('label', label)
          ..add('type', type))
        .toString();
  }
}

class PaymentTargetRefBuilder
    implements Builder<PaymentTargetRef, PaymentTargetRefBuilder> {
  _$PaymentTargetRef? _$v;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _label;
  String? get label => _$this._label;
  set label(String? label) => _$this._label = label;

  PaymentTargetType? _type;
  PaymentTargetType? get type => _$this._type;
  set type(PaymentTargetType? type) => _$this._type = type;

  PaymentTargetRefBuilder() {
    PaymentTargetRef._defaults(this);
  }

  PaymentTargetRefBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _id = $v.id;
      _label = $v.label;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentTargetRef other) {
    _$v = other as _$PaymentTargetRef;
  }

  @override
  void update(void Function(PaymentTargetRefBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentTargetRef build() => _build();

  _$PaymentTargetRef _build() {
    final _$result = _$v ??
        _$PaymentTargetRef._(
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'PaymentTargetRef', 'id'),
          label: BuiltValueNullFieldError.checkNotNull(
              label, r'PaymentTargetRef', 'label'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'PaymentTargetRef', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
