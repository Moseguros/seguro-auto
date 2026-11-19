// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_purpose.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentPurpose _$NEW_POLICY = const PaymentPurpose._('NEW_POLICY');
const PaymentPurpose _$INSTALMENT = const PaymentPurpose._('INSTALMENT');
const PaymentPurpose _$RENEWAL = const PaymentPurpose._('RENEWAL');
const PaymentPurpose _$ENDORSEMENT = const PaymentPurpose._('ENDORSEMENT');
const PaymentPurpose _$FLEET_INCLUSION =
    const PaymentPurpose._('FLEET_INCLUSION');

PaymentPurpose _$valueOf(String name) {
  switch (name) {
    case 'NEW_POLICY':
      return _$NEW_POLICY;
    case 'INSTALMENT':
      return _$INSTALMENT;
    case 'RENEWAL':
      return _$RENEWAL;
    case 'ENDORSEMENT':
      return _$ENDORSEMENT;
    case 'FLEET_INCLUSION':
      return _$FLEET_INCLUSION;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentPurpose> _$values =
    BuiltSet<PaymentPurpose>(const <PaymentPurpose>[
  _$NEW_POLICY,
  _$INSTALMENT,
  _$RENEWAL,
  _$ENDORSEMENT,
  _$FLEET_INCLUSION,
]);

class _$PaymentPurposeMeta {
  const _$PaymentPurposeMeta();
  PaymentPurpose get NEW_POLICY => _$NEW_POLICY;
  PaymentPurpose get INSTALMENT => _$INSTALMENT;
  PaymentPurpose get RENEWAL => _$RENEWAL;
  PaymentPurpose get ENDORSEMENT => _$ENDORSEMENT;
  PaymentPurpose get FLEET_INCLUSION => _$FLEET_INCLUSION;
  PaymentPurpose valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentPurpose> get values => _$values;
}

abstract class _$PaymentPurposeMixin {
  // ignore: non_constant_identifier_names
  _$PaymentPurposeMeta get PaymentPurpose => const _$PaymentPurposeMeta();
}

Serializer<PaymentPurpose> _$paymentPurposeSerializer =
    _$PaymentPurposeSerializer();

class _$PaymentPurposeSerializer
    implements PrimitiveSerializer<PaymentPurpose> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NEW_POLICY': 'NEW_POLICY',
    'INSTALMENT': 'INSTALMENT',
    'RENEWAL': 'RENEWAL',
    'ENDORSEMENT': 'ENDORSEMENT',
    'FLEET_INCLUSION': 'FLEET_INCLUSION',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'NEW_POLICY': 'NEW_POLICY',
    'INSTALMENT': 'INSTALMENT',
    'RENEWAL': 'RENEWAL',
    'ENDORSEMENT': 'ENDORSEMENT',
    'FLEET_INCLUSION': 'FLEET_INCLUSION',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentPurpose];
  @override
  final String wireName = 'PaymentPurpose';

  @override
  Object serialize(Serializers serializers, PaymentPurpose object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentPurpose deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentPurpose.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
