// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentStatus _$CREATED = const PaymentStatus._('CREATED');
const PaymentStatus _$PENDING = const PaymentStatus._('PENDING');
const PaymentStatus _$UNKNOWN = const PaymentStatus._('UNKNOWN');
const PaymentStatus _$SUCCEEDED = const PaymentStatus._('SUCCEEDED');
const PaymentStatus _$FAILED = const PaymentStatus._('FAILED');
const PaymentStatus _$EXPIRED = const PaymentStatus._('EXPIRED');
const PaymentStatus _$CANCELLED = const PaymentStatus._('CANCELLED');
const PaymentStatus _$REFUNDED = const PaymentStatus._('REFUNDED');

PaymentStatus _$valueOf(String name) {
  switch (name) {
    case 'CREATED':
      return _$CREATED;
    case 'PENDING':
      return _$PENDING;
    case 'UNKNOWN':
      return _$UNKNOWN;
    case 'SUCCEEDED':
      return _$SUCCEEDED;
    case 'FAILED':
      return _$FAILED;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'CANCELLED':
      return _$CANCELLED;
    case 'REFUNDED':
      return _$REFUNDED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentStatus> _$values =
    BuiltSet<PaymentStatus>(const <PaymentStatus>[
  _$CREATED,
  _$PENDING,
  _$UNKNOWN,
  _$SUCCEEDED,
  _$FAILED,
  _$EXPIRED,
  _$CANCELLED,
  _$REFUNDED,
]);

class _$PaymentStatusMeta {
  const _$PaymentStatusMeta();
  PaymentStatus get CREATED => _$CREATED;
  PaymentStatus get PENDING => _$PENDING;
  PaymentStatus get UNKNOWN => _$UNKNOWN;
  PaymentStatus get SUCCEEDED => _$SUCCEEDED;
  PaymentStatus get FAILED => _$FAILED;
  PaymentStatus get EXPIRED => _$EXPIRED;
  PaymentStatus get CANCELLED => _$CANCELLED;
  PaymentStatus get REFUNDED => _$REFUNDED;
  PaymentStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentStatus> get values => _$values;
}

abstract class _$PaymentStatusMixin {
  // ignore: non_constant_identifier_names
  _$PaymentStatusMeta get PaymentStatus => const _$PaymentStatusMeta();
}

Serializer<PaymentStatus> _$paymentStatusSerializer =
    _$PaymentStatusSerializer();

class _$PaymentStatusSerializer implements PrimitiveSerializer<PaymentStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CREATED': 'CREATED',
    'PENDING': 'PENDING',
    'UNKNOWN': 'UNKNOWN',
    'SUCCEEDED': 'SUCCEEDED',
    'FAILED': 'FAILED',
    'EXPIRED': 'EXPIRED',
    'CANCELLED': 'CANCELLED',
    'REFUNDED': 'REFUNDED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CREATED': 'CREATED',
    'PENDING': 'PENDING',
    'UNKNOWN': 'UNKNOWN',
    'SUCCEEDED': 'SUCCEEDED',
    'FAILED': 'FAILED',
    'EXPIRED': 'EXPIRED',
    'CANCELLED': 'CANCELLED',
    'REFUNDED': 'REFUNDED',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentStatus];
  @override
  final String wireName = 'PaymentStatus';

  @override
  Object serialize(Serializers serializers, PaymentStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
