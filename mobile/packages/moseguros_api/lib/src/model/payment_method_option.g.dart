// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_method_option.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentMethodOptionMethodEnum _$paymentMethodOptionMethodEnum_MPESA =
    const PaymentMethodOptionMethodEnum._('MPESA');
const PaymentMethodOptionMethodEnum
    _$paymentMethodOptionMethodEnum_BANK_REFERENCE =
    const PaymentMethodOptionMethodEnum._('BANK_REFERENCE');

PaymentMethodOptionMethodEnum _$paymentMethodOptionMethodEnumValueOf(
    String name) {
  switch (name) {
    case 'MPESA':
      return _$paymentMethodOptionMethodEnum_MPESA;
    case 'BANK_REFERENCE':
      return _$paymentMethodOptionMethodEnum_BANK_REFERENCE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentMethodOptionMethodEnum>
    _$paymentMethodOptionMethodEnumValues = BuiltSet<
        PaymentMethodOptionMethodEnum>(const <PaymentMethodOptionMethodEnum>[
  _$paymentMethodOptionMethodEnum_MPESA,
  _$paymentMethodOptionMethodEnum_BANK_REFERENCE,
]);

Serializer<PaymentMethodOptionMethodEnum>
    _$paymentMethodOptionMethodEnumSerializer =
    _$PaymentMethodOptionMethodEnumSerializer();

class _$PaymentMethodOptionMethodEnumSerializer
    implements PrimitiveSerializer<PaymentMethodOptionMethodEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MPESA': 'MPESA',
    'BANK_REFERENCE': 'BANK_REFERENCE',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentMethodOptionMethodEnum];
  @override
  final String wireName = 'PaymentMethodOptionMethodEnum';

  @override
  Object serialize(
          Serializers serializers, PaymentMethodOptionMethodEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentMethodOptionMethodEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentMethodOptionMethodEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$PaymentMethodOption extends PaymentMethodOption {
  @override
  final String? displayName;
  @override
  final PaymentMethodOptionMethodEnum method;
  @override
  final int sortOrder;

  factory _$PaymentMethodOption(
          [void Function(PaymentMethodOptionBuilder)? updates]) =>
      (PaymentMethodOptionBuilder()..update(updates))._build();

  _$PaymentMethodOption._(
      {this.displayName, required this.method, required this.sortOrder})
      : super._();
  @override
  PaymentMethodOption rebuild(
          void Function(PaymentMethodOptionBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PaymentMethodOptionBuilder toBuilder() =>
      PaymentMethodOptionBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PaymentMethodOption &&
        displayName == other.displayName &&
        method == other.method &&
        sortOrder == other.sortOrder;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, method.hashCode);
    _$hash = $jc(_$hash, sortOrder.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PaymentMethodOption')
          ..add('displayName', displayName)
          ..add('method', method)
          ..add('sortOrder', sortOrder))
        .toString();
  }
}

class PaymentMethodOptionBuilder
    implements Builder<PaymentMethodOption, PaymentMethodOptionBuilder> {
  _$PaymentMethodOption? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  PaymentMethodOptionMethodEnum? _method;
  PaymentMethodOptionMethodEnum? get method => _$this._method;
  set method(PaymentMethodOptionMethodEnum? method) => _$this._method = method;

  int? _sortOrder;
  int? get sortOrder => _$this._sortOrder;
  set sortOrder(int? sortOrder) => _$this._sortOrder = sortOrder;

  PaymentMethodOptionBuilder() {
    PaymentMethodOption._defaults(this);
  }

  PaymentMethodOptionBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _method = $v.method;
      _sortOrder = $v.sortOrder;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PaymentMethodOption other) {
    _$v = other as _$PaymentMethodOption;
  }

  @override
  void update(void Function(PaymentMethodOptionBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PaymentMethodOption build() => _build();

  _$PaymentMethodOption _build() {
    final _$result = _$v ??
        _$PaymentMethodOption._(
          displayName: displayName,
          method: BuiltValueNullFieldError.checkNotNull(
              method, r'PaymentMethodOption', 'method'),
          sortOrder: BuiltValueNullFieldError.checkNotNull(
              sortOrder, r'PaymentMethodOption', 'sortOrder'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
