// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_next_action_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentNextActionType _$AWAIT_CUSTOMER_CONFIRMATION =
    const PaymentNextActionType._('AWAIT_CUSTOMER_CONFIRMATION');
const PaymentNextActionType _$CHECKING_STATUS =
    const PaymentNextActionType._('CHECKING_STATUS');
const PaymentNextActionType _$PAY_BY_REFERENCE =
    const PaymentNextActionType._('PAY_BY_REFERENCE');
const PaymentNextActionType _$NONE = const PaymentNextActionType._('NONE');

PaymentNextActionType _$valueOf(String name) {
  switch (name) {
    case 'AWAIT_CUSTOMER_CONFIRMATION':
      return _$AWAIT_CUSTOMER_CONFIRMATION;
    case 'CHECKING_STATUS':
      return _$CHECKING_STATUS;
    case 'PAY_BY_REFERENCE':
      return _$PAY_BY_REFERENCE;
    case 'NONE':
      return _$NONE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentNextActionType> _$values =
    BuiltSet<PaymentNextActionType>(const <PaymentNextActionType>[
  _$AWAIT_CUSTOMER_CONFIRMATION,
  _$CHECKING_STATUS,
  _$PAY_BY_REFERENCE,
  _$NONE,
]);

class _$PaymentNextActionTypeMeta {
  const _$PaymentNextActionTypeMeta();
  PaymentNextActionType get AWAIT_CUSTOMER_CONFIRMATION =>
      _$AWAIT_CUSTOMER_CONFIRMATION;
  PaymentNextActionType get CHECKING_STATUS => _$CHECKING_STATUS;
  PaymentNextActionType get PAY_BY_REFERENCE => _$PAY_BY_REFERENCE;
  PaymentNextActionType get NONE => _$NONE;
  PaymentNextActionType valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentNextActionType> get values => _$values;
}

abstract class _$PaymentNextActionTypeMixin {
  // ignore: non_constant_identifier_names
  _$PaymentNextActionTypeMeta get PaymentNextActionType =>
      const _$PaymentNextActionTypeMeta();
}

Serializer<PaymentNextActionType> _$paymentNextActionTypeSerializer =
    _$PaymentNextActionTypeSerializer();

class _$PaymentNextActionTypeSerializer
    implements PrimitiveSerializer<PaymentNextActionType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'AWAIT_CUSTOMER_CONFIRMATION': 'AWAIT_CUSTOMER_CONFIRMATION',
    'CHECKING_STATUS': 'CHECKING_STATUS',
    'PAY_BY_REFERENCE': 'PAY_BY_REFERENCE',
    'NONE': 'NONE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'AWAIT_CUSTOMER_CONFIRMATION': 'AWAIT_CUSTOMER_CONFIRMATION',
    'CHECKING_STATUS': 'CHECKING_STATUS',
    'PAY_BY_REFERENCE': 'PAY_BY_REFERENCE',
    'NONE': 'NONE',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentNextActionType];
  @override
  final String wireName = 'PaymentNextActionType';

  @override
  Object serialize(Serializers serializers, PaymentNextActionType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentNextActionType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentNextActionType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
