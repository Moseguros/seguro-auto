// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_target_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentTargetType _$PROPOSAL = const PaymentTargetType._('PROPOSAL');
const PaymentTargetType _$INSTALMENT = const PaymentTargetType._('INSTALMENT');
const PaymentTargetType _$RENEWAL = const PaymentTargetType._('RENEWAL');
const PaymentTargetType _$ENDORSEMENT =
    const PaymentTargetType._('ENDORSEMENT');
const PaymentTargetType _$FLEET_INCLUSION =
    const PaymentTargetType._('FLEET_INCLUSION');

PaymentTargetType _$valueOf(String name) {
  switch (name) {
    case 'PROPOSAL':
      return _$PROPOSAL;
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

final BuiltSet<PaymentTargetType> _$values =
    BuiltSet<PaymentTargetType>(const <PaymentTargetType>[
  _$PROPOSAL,
  _$INSTALMENT,
  _$RENEWAL,
  _$ENDORSEMENT,
  _$FLEET_INCLUSION,
]);

class _$PaymentTargetTypeMeta {
  const _$PaymentTargetTypeMeta();
  PaymentTargetType get PROPOSAL => _$PROPOSAL;
  PaymentTargetType get INSTALMENT => _$INSTALMENT;
  PaymentTargetType get RENEWAL => _$RENEWAL;
  PaymentTargetType get ENDORSEMENT => _$ENDORSEMENT;
  PaymentTargetType get FLEET_INCLUSION => _$FLEET_INCLUSION;
  PaymentTargetType valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentTargetType> get values => _$values;
}

abstract class _$PaymentTargetTypeMixin {
  // ignore: non_constant_identifier_names
  _$PaymentTargetTypeMeta get PaymentTargetType =>
      const _$PaymentTargetTypeMeta();
}

Serializer<PaymentTargetType> _$paymentTargetTypeSerializer =
    _$PaymentTargetTypeSerializer();

class _$PaymentTargetTypeSerializer
    implements PrimitiveSerializer<PaymentTargetType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PROPOSAL': 'PROPOSAL',
    'INSTALMENT': 'INSTALMENT',
    'RENEWAL': 'RENEWAL',
    'ENDORSEMENT': 'ENDORSEMENT',
    'FLEET_INCLUSION': 'FLEET_INCLUSION',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PROPOSAL': 'PROPOSAL',
    'INSTALMENT': 'INSTALMENT',
    'RENEWAL': 'RENEWAL',
    'ENDORSEMENT': 'ENDORSEMENT',
    'FLEET_INCLUSION': 'FLEET_INCLUSION',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentTargetType];
  @override
  final String wireName = 'PaymentTargetType';

  @override
  Object serialize(Serializers serializers, PaymentTargetType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentTargetType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentTargetType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
