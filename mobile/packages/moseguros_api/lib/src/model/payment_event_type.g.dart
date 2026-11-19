// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_event_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentEventType _$CREATED = const PaymentEventType._('CREATED');
const PaymentEventType _$DISPATCHED = const PaymentEventType._('DISPATCHED');
const PaymentEventType _$PROVIDER_RESPONSE =
    const PaymentEventType._('PROVIDER_RESPONSE');
const PaymentEventType _$STATUS_QUERY =
    const PaymentEventType._('STATUS_QUERY');
const PaymentEventType _$STATUS_CHANGED =
    const PaymentEventType._('STATUS_CHANGED');
const PaymentEventType _$REFERENCE_PAID =
    const PaymentEventType._('REFERENCE_PAID');
const PaymentEventType _$MANUAL_RESOLUTION =
    const PaymentEventType._('MANUAL_RESOLUTION');

PaymentEventType _$valueOf(String name) {
  switch (name) {
    case 'CREATED':
      return _$CREATED;
    case 'DISPATCHED':
      return _$DISPATCHED;
    case 'PROVIDER_RESPONSE':
      return _$PROVIDER_RESPONSE;
    case 'STATUS_QUERY':
      return _$STATUS_QUERY;
    case 'STATUS_CHANGED':
      return _$STATUS_CHANGED;
    case 'REFERENCE_PAID':
      return _$REFERENCE_PAID;
    case 'MANUAL_RESOLUTION':
      return _$MANUAL_RESOLUTION;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentEventType> _$values =
    BuiltSet<PaymentEventType>(const <PaymentEventType>[
  _$CREATED,
  _$DISPATCHED,
  _$PROVIDER_RESPONSE,
  _$STATUS_QUERY,
  _$STATUS_CHANGED,
  _$REFERENCE_PAID,
  _$MANUAL_RESOLUTION,
]);

class _$PaymentEventTypeMeta {
  const _$PaymentEventTypeMeta();
  PaymentEventType get CREATED => _$CREATED;
  PaymentEventType get DISPATCHED => _$DISPATCHED;
  PaymentEventType get PROVIDER_RESPONSE => _$PROVIDER_RESPONSE;
  PaymentEventType get STATUS_QUERY => _$STATUS_QUERY;
  PaymentEventType get STATUS_CHANGED => _$STATUS_CHANGED;
  PaymentEventType get REFERENCE_PAID => _$REFERENCE_PAID;
  PaymentEventType get MANUAL_RESOLUTION => _$MANUAL_RESOLUTION;
  PaymentEventType valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentEventType> get values => _$values;
}

abstract class _$PaymentEventTypeMixin {
  // ignore: non_constant_identifier_names
  _$PaymentEventTypeMeta get PaymentEventType => const _$PaymentEventTypeMeta();
}

Serializer<PaymentEventType> _$paymentEventTypeSerializer =
    _$PaymentEventTypeSerializer();

class _$PaymentEventTypeSerializer
    implements PrimitiveSerializer<PaymentEventType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CREATED': 'CREATED',
    'DISPATCHED': 'DISPATCHED',
    'PROVIDER_RESPONSE': 'PROVIDER_RESPONSE',
    'STATUS_QUERY': 'STATUS_QUERY',
    'STATUS_CHANGED': 'STATUS_CHANGED',
    'REFERENCE_PAID': 'REFERENCE_PAID',
    'MANUAL_RESOLUTION': 'MANUAL_RESOLUTION',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CREATED': 'CREATED',
    'DISPATCHED': 'DISPATCHED',
    'PROVIDER_RESPONSE': 'PROVIDER_RESPONSE',
    'STATUS_QUERY': 'STATUS_QUERY',
    'STATUS_CHANGED': 'STATUS_CHANGED',
    'REFERENCE_PAID': 'REFERENCE_PAID',
    'MANUAL_RESOLUTION': 'MANUAL_RESOLUTION',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentEventType];
  @override
  final String wireName = 'PaymentEventType';

  @override
  Object serialize(Serializers serializers, PaymentEventType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentEventType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentEventType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
