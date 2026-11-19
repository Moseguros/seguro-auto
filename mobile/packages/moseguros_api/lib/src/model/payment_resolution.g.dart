// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_resolution.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentResolution _$LINKED = const PaymentResolution._('LINKED');
const PaymentResolution _$REFUND = const PaymentResolution._('REFUND');
const PaymentResolution _$IGNORED = const PaymentResolution._('IGNORED');
const PaymentResolution _$MARK_SUCCEEDED =
    const PaymentResolution._('MARK_SUCCEEDED');
const PaymentResolution _$MARK_FAILED =
    const PaymentResolution._('MARK_FAILED');

PaymentResolution _$valueOf(String name) {
  switch (name) {
    case 'LINKED':
      return _$LINKED;
    case 'REFUND':
      return _$REFUND;
    case 'IGNORED':
      return _$IGNORED;
    case 'MARK_SUCCEEDED':
      return _$MARK_SUCCEEDED;
    case 'MARK_FAILED':
      return _$MARK_FAILED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentResolution> _$values =
    BuiltSet<PaymentResolution>(const <PaymentResolution>[
  _$LINKED,
  _$REFUND,
  _$IGNORED,
  _$MARK_SUCCEEDED,
  _$MARK_FAILED,
]);

class _$PaymentResolutionMeta {
  const _$PaymentResolutionMeta();
  PaymentResolution get LINKED => _$LINKED;
  PaymentResolution get REFUND => _$REFUND;
  PaymentResolution get IGNORED => _$IGNORED;
  PaymentResolution get MARK_SUCCEEDED => _$MARK_SUCCEEDED;
  PaymentResolution get MARK_FAILED => _$MARK_FAILED;
  PaymentResolution valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentResolution> get values => _$values;
}

abstract class _$PaymentResolutionMixin {
  // ignore: non_constant_identifier_names
  _$PaymentResolutionMeta get PaymentResolution =>
      const _$PaymentResolutionMeta();
}

Serializer<PaymentResolution> _$paymentResolutionSerializer =
    _$PaymentResolutionSerializer();

class _$PaymentResolutionSerializer
    implements PrimitiveSerializer<PaymentResolution> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'LINKED': 'LINKED',
    'REFUND': 'REFUND',
    'IGNORED': 'IGNORED',
    'MARK_SUCCEEDED': 'MARK_SUCCEEDED',
    'MARK_FAILED': 'MARK_FAILED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'LINKED': 'LINKED',
    'REFUND': 'REFUND',
    'IGNORED': 'IGNORED',
    'MARK_SUCCEEDED': 'MARK_SUCCEEDED',
    'MARK_FAILED': 'MARK_FAILED',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentResolution];
  @override
  final String wireName = 'PaymentResolution';

  @override
  Object serialize(Serializers serializers, PaymentResolution object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentResolution deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentResolution.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
