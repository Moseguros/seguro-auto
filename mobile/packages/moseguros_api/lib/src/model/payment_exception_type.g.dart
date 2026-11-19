// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_exception_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentExceptionType _$AMOUNT_MISMATCH =
    const PaymentExceptionType._('AMOUNT_MISMATCH');
const PaymentExceptionType _$EXPIRED_REFERENCE =
    const PaymentExceptionType._('EXPIRED_REFERENCE');
const PaymentExceptionType _$ALREADY_PAID =
    const PaymentExceptionType._('ALREADY_PAID');
const PaymentExceptionType _$UNKNOWN_REFERENCE =
    const PaymentExceptionType._('UNKNOWN_REFERENCE');
const PaymentExceptionType _$CANCELLED_REFERENCE =
    const PaymentExceptionType._('CANCELLED_REFERENCE');
const PaymentExceptionType _$TARGET_NOT_PAYABLE =
    const PaymentExceptionType._('TARGET_NOT_PAYABLE');
const PaymentExceptionType _$UNRESOLVED_STATUS =
    const PaymentExceptionType._('UNRESOLVED_STATUS');

PaymentExceptionType _$valueOf(String name) {
  switch (name) {
    case 'AMOUNT_MISMATCH':
      return _$AMOUNT_MISMATCH;
    case 'EXPIRED_REFERENCE':
      return _$EXPIRED_REFERENCE;
    case 'ALREADY_PAID':
      return _$ALREADY_PAID;
    case 'UNKNOWN_REFERENCE':
      return _$UNKNOWN_REFERENCE;
    case 'CANCELLED_REFERENCE':
      return _$CANCELLED_REFERENCE;
    case 'TARGET_NOT_PAYABLE':
      return _$TARGET_NOT_PAYABLE;
    case 'UNRESOLVED_STATUS':
      return _$UNRESOLVED_STATUS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentExceptionType> _$values =
    BuiltSet<PaymentExceptionType>(const <PaymentExceptionType>[
  _$AMOUNT_MISMATCH,
  _$EXPIRED_REFERENCE,
  _$ALREADY_PAID,
  _$UNKNOWN_REFERENCE,
  _$CANCELLED_REFERENCE,
  _$TARGET_NOT_PAYABLE,
  _$UNRESOLVED_STATUS,
]);

class _$PaymentExceptionTypeMeta {
  const _$PaymentExceptionTypeMeta();
  PaymentExceptionType get AMOUNT_MISMATCH => _$AMOUNT_MISMATCH;
  PaymentExceptionType get EXPIRED_REFERENCE => _$EXPIRED_REFERENCE;
  PaymentExceptionType get ALREADY_PAID => _$ALREADY_PAID;
  PaymentExceptionType get UNKNOWN_REFERENCE => _$UNKNOWN_REFERENCE;
  PaymentExceptionType get CANCELLED_REFERENCE => _$CANCELLED_REFERENCE;
  PaymentExceptionType get TARGET_NOT_PAYABLE => _$TARGET_NOT_PAYABLE;
  PaymentExceptionType get UNRESOLVED_STATUS => _$UNRESOLVED_STATUS;
  PaymentExceptionType valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentExceptionType> get values => _$values;
}

abstract class _$PaymentExceptionTypeMixin {
  // ignore: non_constant_identifier_names
  _$PaymentExceptionTypeMeta get PaymentExceptionType =>
      const _$PaymentExceptionTypeMeta();
}

Serializer<PaymentExceptionType> _$paymentExceptionTypeSerializer =
    _$PaymentExceptionTypeSerializer();

class _$PaymentExceptionTypeSerializer
    implements PrimitiveSerializer<PaymentExceptionType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'AMOUNT_MISMATCH': 'AMOUNT_MISMATCH',
    'EXPIRED_REFERENCE': 'EXPIRED_REFERENCE',
    'ALREADY_PAID': 'ALREADY_PAID',
    'UNKNOWN_REFERENCE': 'UNKNOWN_REFERENCE',
    'CANCELLED_REFERENCE': 'CANCELLED_REFERENCE',
    'TARGET_NOT_PAYABLE': 'TARGET_NOT_PAYABLE',
    'UNRESOLVED_STATUS': 'UNRESOLVED_STATUS',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'AMOUNT_MISMATCH': 'AMOUNT_MISMATCH',
    'EXPIRED_REFERENCE': 'EXPIRED_REFERENCE',
    'ALREADY_PAID': 'ALREADY_PAID',
    'UNKNOWN_REFERENCE': 'UNKNOWN_REFERENCE',
    'CANCELLED_REFERENCE': 'CANCELLED_REFERENCE',
    'TARGET_NOT_PAYABLE': 'TARGET_NOT_PAYABLE',
    'UNRESOLVED_STATUS': 'UNRESOLVED_STATUS',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentExceptionType];
  @override
  final String wireName = 'PaymentExceptionType';

  @override
  Object serialize(Serializers serializers, PaymentExceptionType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentExceptionType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentExceptionType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
