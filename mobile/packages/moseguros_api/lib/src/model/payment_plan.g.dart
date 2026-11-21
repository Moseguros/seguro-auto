// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'payment_plan.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PaymentPlan _$FULL = const PaymentPlan._('FULL');
const PaymentPlan _$SEMIANNUAL = const PaymentPlan._('SEMIANNUAL');
const PaymentPlan _$QUARTERLY = const PaymentPlan._('QUARTERLY');
const PaymentPlan _$MONTHLY = const PaymentPlan._('MONTHLY');

PaymentPlan _$valueOf(String name) {
  switch (name) {
    case 'FULL':
      return _$FULL;
    case 'SEMIANNUAL':
      return _$SEMIANNUAL;
    case 'QUARTERLY':
      return _$QUARTERLY;
    case 'MONTHLY':
      return _$MONTHLY;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PaymentPlan> _$values =
    BuiltSet<PaymentPlan>(const <PaymentPlan>[
  _$FULL,
  _$SEMIANNUAL,
  _$QUARTERLY,
  _$MONTHLY,
]);

class _$PaymentPlanMeta {
  const _$PaymentPlanMeta();
  PaymentPlan get FULL => _$FULL;
  PaymentPlan get SEMIANNUAL => _$SEMIANNUAL;
  PaymentPlan get QUARTERLY => _$QUARTERLY;
  PaymentPlan get MONTHLY => _$MONTHLY;
  PaymentPlan valueOf(String name) => _$valueOf(name);
  BuiltSet<PaymentPlan> get values => _$values;
}

abstract class _$PaymentPlanMixin {
  // ignore: non_constant_identifier_names
  _$PaymentPlanMeta get PaymentPlan => const _$PaymentPlanMeta();
}

Serializer<PaymentPlan> _$paymentPlanSerializer = _$PaymentPlanSerializer();

class _$PaymentPlanSerializer implements PrimitiveSerializer<PaymentPlan> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'FULL': 'FULL',
    'SEMIANNUAL': 'SEMIANNUAL',
    'QUARTERLY': 'QUARTERLY',
    'MONTHLY': 'MONTHLY',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'FULL': 'FULL',
    'SEMIANNUAL': 'SEMIANNUAL',
    'QUARTERLY': 'QUARTERLY',
    'MONTHLY': 'MONTHLY',
  };

  @override
  final Iterable<Type> types = const <Type>[PaymentPlan];
  @override
  final String wireName = 'PaymentPlan';

  @override
  Object serialize(Serializers serializers, PaymentPlan object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PaymentPlan deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PaymentPlan.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
