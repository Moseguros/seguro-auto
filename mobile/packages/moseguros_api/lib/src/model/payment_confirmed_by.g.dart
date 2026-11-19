// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_confirmed_by.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentConfirmedBy _$PROVIDER = const PaymentConfirmedBy._('PROVIDER');
const PaymentConfirmedBy _$STATUS_QUERY =
    const PaymentConfirmedBy._('STATUS_QUERY');
const PaymentConfirmedBy _$REFERENCE_FILE =
    const PaymentConfirmedBy._('REFERENCE_FILE');
const PaymentConfirmedBy _$MANUAL = const PaymentConfirmedBy._('MANUAL');

PaymentConfirmedBy _$valueOf(String name) {
  switch (name) {
    case 'PROVIDER':
      return _$PROVIDER;
    case 'STATUS_QUERY':
      return _$STATUS_QUERY;
    case 'REFERENCE_FILE':
      return _$REFERENCE_FILE;
    case 'MANUAL':
      return _$MANUAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentConfirmedBy> _$values =
    BuiltSet<PaymentConfirmedBy>(const <PaymentConfirmedBy>[
  _$PROVIDER,
  _$STATUS_QUERY,
  _$REFERENCE_FILE,
  _$MANUAL,
]);

class _$PaymentConfirmedByMeta {
  const _$PaymentConfirmedByMeta();
  PaymentConfirmedBy get PROVIDER => _$PROVIDER;
  PaymentConfirmedBy get STATUS_QUERY => _$STATUS_QUERY;
  PaymentConfirmedBy get REFERENCE_FILE => _$REFERENCE_FILE;
  PaymentConfirmedBy get MANUAL => _$MANUAL;
  PaymentConfirmedBy valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentConfirmedBy> get values => _$values;
}

abstract class _$PaymentConfirmedByMixin {
  // ignore: non_constant_identifier_names
  _$PaymentConfirmedByMeta get PaymentConfirmedBy =>
      const _$PaymentConfirmedByMeta();
}

Serializer<PaymentConfirmedBy> _$paymentConfirmedBySerializer =
    _$PaymentConfirmedBySerializer();

class _$PaymentConfirmedBySerializer
    implements PrimitiveSerializer<PaymentConfirmedBy> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PROVIDER': 'PROVIDER',
    'STATUS_QUERY': 'STATUS_QUERY',
    'REFERENCE_FILE': 'REFERENCE_FILE',
    'MANUAL': 'MANUAL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PROVIDER': 'PROVIDER',
    'STATUS_QUERY': 'STATUS_QUERY',
    'REFERENCE_FILE': 'REFERENCE_FILE',
    'MANUAL': 'MANUAL',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentConfirmedBy];
  @override
  final String wireName = 'PaymentConfirmedBy';

  @override
  Object serialize(Serializers serializers, PaymentConfirmedBy object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentConfirmedBy deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentConfirmedBy.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
