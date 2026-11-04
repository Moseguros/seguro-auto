// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suitability_reason.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SuitabilityReason _$NEED_NOT_COVERED_OWN_DAMAGE =
    const SuitabilityReason._('NEED_NOT_COVERED_OWN_DAMAGE');
const SuitabilityReason _$NEED_NOT_COVERED_THEFT_FIRE =
    const SuitabilityReason._('NEED_NOT_COVERED_THEFT_FIRE');
const SuitabilityReason _$COVER_ABOVE_NEED =
    const SuitabilityReason._('COVER_ABOVE_NEED');
const SuitabilityReason _$NEED_UNSURE =
    const SuitabilityReason._('NEED_UNSURE');
const SuitabilityReason _$FINANCED_WITHOUT_OWN_DAMAGE =
    const SuitabilityReason._('FINANCED_WITHOUT_OWN_DAMAGE');
const SuitabilityReason _$CANNOT_AFFORD_REPAIR =
    const SuitabilityReason._('CANNOT_AFFORD_REPAIR');
const SuitabilityReason _$CROSS_BORDER_NOT_COVERED =
    const SuitabilityReason._('CROSS_BORDER_NOT_COVERED');
const SuitabilityReason _$DEDUCTIBLE_NOT_WANTED =
    const SuitabilityReason._('DEDUCTIBLE_NOT_WANTED');

SuitabilityReason _$valueOf(String name) {
  switch (name) {
    case 'NEED_NOT_COVERED_OWN_DAMAGE':
      return _$NEED_NOT_COVERED_OWN_DAMAGE;
    case 'NEED_NOT_COVERED_THEFT_FIRE':
      return _$NEED_NOT_COVERED_THEFT_FIRE;
    case 'COVER_ABOVE_NEED':
      return _$COVER_ABOVE_NEED;
    case 'NEED_UNSURE':
      return _$NEED_UNSURE;
    case 'FINANCED_WITHOUT_OWN_DAMAGE':
      return _$FINANCED_WITHOUT_OWN_DAMAGE;
    case 'CANNOT_AFFORD_REPAIR':
      return _$CANNOT_AFFORD_REPAIR;
    case 'CROSS_BORDER_NOT_COVERED':
      return _$CROSS_BORDER_NOT_COVERED;
    case 'DEDUCTIBLE_NOT_WANTED':
      return _$DEDUCTIBLE_NOT_WANTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SuitabilityReason> _$values =
    BuiltSet<SuitabilityReason>(const <SuitabilityReason>[
  _$NEED_NOT_COVERED_OWN_DAMAGE,
  _$NEED_NOT_COVERED_THEFT_FIRE,
  _$COVER_ABOVE_NEED,
  _$NEED_UNSURE,
  _$FINANCED_WITHOUT_OWN_DAMAGE,
  _$CANNOT_AFFORD_REPAIR,
  _$CROSS_BORDER_NOT_COVERED,
  _$DEDUCTIBLE_NOT_WANTED,
]);

class _$SuitabilityReasonMeta {
  const _$SuitabilityReasonMeta();
  SuitabilityReason get NEED_NOT_COVERED_OWN_DAMAGE =>
      _$NEED_NOT_COVERED_OWN_DAMAGE;
  SuitabilityReason get NEED_NOT_COVERED_THEFT_FIRE =>
      _$NEED_NOT_COVERED_THEFT_FIRE;
  SuitabilityReason get COVER_ABOVE_NEED => _$COVER_ABOVE_NEED;
  SuitabilityReason get NEED_UNSURE => _$NEED_UNSURE;
  SuitabilityReason get FINANCED_WITHOUT_OWN_DAMAGE =>
      _$FINANCED_WITHOUT_OWN_DAMAGE;
  SuitabilityReason get CANNOT_AFFORD_REPAIR => _$CANNOT_AFFORD_REPAIR;
  SuitabilityReason get CROSS_BORDER_NOT_COVERED => _$CROSS_BORDER_NOT_COVERED;
  SuitabilityReason get DEDUCTIBLE_NOT_WANTED => _$DEDUCTIBLE_NOT_WANTED;
  SuitabilityReason valueOf(String name) => _$valueOf(name);
  BuiltSet<SuitabilityReason> get values => _$values;
}

abstract class _$SuitabilityReasonMixin {
  // ignore: non_constant_identifier_names
  _$SuitabilityReasonMeta get SuitabilityReason =>
      const _$SuitabilityReasonMeta();
}

Serializer<SuitabilityReason> _$suitabilityReasonSerializer =
    _$SuitabilityReasonSerializer();

class _$SuitabilityReasonSerializer
    implements PrimitiveSerializer<SuitabilityReason> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'NEED_NOT_COVERED_OWN_DAMAGE': 'NEED_NOT_COVERED_OWN_DAMAGE',
    'NEED_NOT_COVERED_THEFT_FIRE': 'NEED_NOT_COVERED_THEFT_FIRE',
    'COVER_ABOVE_NEED': 'COVER_ABOVE_NEED',
    'NEED_UNSURE': 'NEED_UNSURE',
    'FINANCED_WITHOUT_OWN_DAMAGE': 'FINANCED_WITHOUT_OWN_DAMAGE',
    'CANNOT_AFFORD_REPAIR': 'CANNOT_AFFORD_REPAIR',
    'CROSS_BORDER_NOT_COVERED': 'CROSS_BORDER_NOT_COVERED',
    'DEDUCTIBLE_NOT_WANTED': 'DEDUCTIBLE_NOT_WANTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'NEED_NOT_COVERED_OWN_DAMAGE': 'NEED_NOT_COVERED_OWN_DAMAGE',
    'NEED_NOT_COVERED_THEFT_FIRE': 'NEED_NOT_COVERED_THEFT_FIRE',
    'COVER_ABOVE_NEED': 'COVER_ABOVE_NEED',
    'NEED_UNSURE': 'NEED_UNSURE',
    'FINANCED_WITHOUT_OWN_DAMAGE': 'FINANCED_WITHOUT_OWN_DAMAGE',
    'CANNOT_AFFORD_REPAIR': 'CANNOT_AFFORD_REPAIR',
    'CROSS_BORDER_NOT_COVERED': 'CROSS_BORDER_NOT_COVERED',
    'DEDUCTIBLE_NOT_WANTED': 'DEDUCTIBLE_NOT_WANTED',
  };

  @override
  final Iterable<Type> types = const <Type>[SuitabilityReason];
  @override
  final String wireName = 'SuitabilityReason';

  @override
  Object serialize(Serializers serializers, SuitabilityReason object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SuitabilityReason deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SuitabilityReason.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
