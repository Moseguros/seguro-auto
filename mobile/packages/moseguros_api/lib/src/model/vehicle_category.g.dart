// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_category.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const VehicleCategory _$LIGHT_PASSENGER =
    const VehicleCategory._('LIGHT_PASSENGER');
const VehicleCategory _$LIGHT_MIXED = const VehicleCategory._('LIGHT_MIXED');
const VehicleCategory _$HEAVY = const VehicleCategory._('HEAVY');
const VehicleCategory _$MOTORCYCLE = const VehicleCategory._('MOTORCYCLE');
const VehicleCategory _$TRAILER = const VehicleCategory._('TRAILER');
const VehicleCategory _$BUS = const VehicleCategory._('BUS');

VehicleCategory _$valueOf(String name) {
  switch (name) {
    case 'LIGHT_PASSENGER':
      return _$LIGHT_PASSENGER;
    case 'LIGHT_MIXED':
      return _$LIGHT_MIXED;
    case 'HEAVY':
      return _$HEAVY;
    case 'MOTORCYCLE':
      return _$MOTORCYCLE;
    case 'TRAILER':
      return _$TRAILER;
    case 'BUS':
      return _$BUS;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<VehicleCategory> _$values =
    BuiltSet<VehicleCategory>(const <VehicleCategory>[
  _$LIGHT_PASSENGER,
  _$LIGHT_MIXED,
  _$HEAVY,
  _$MOTORCYCLE,
  _$TRAILER,
  _$BUS,
]);

class _$VehicleCategoryMeta {
  const _$VehicleCategoryMeta();
  VehicleCategory get LIGHT_PASSENGER => _$LIGHT_PASSENGER;
  VehicleCategory get LIGHT_MIXED => _$LIGHT_MIXED;
  VehicleCategory get HEAVY => _$HEAVY;
  VehicleCategory get MOTORCYCLE => _$MOTORCYCLE;
  VehicleCategory get TRAILER => _$TRAILER;
  VehicleCategory get BUS => _$BUS;
  VehicleCategory valueOf(String name) => _$valueOf(name);
  BuiltSet<VehicleCategory> get values => _$values;
}

abstract class _$VehicleCategoryMixin {
  // ignore: non_constant_identifier_names
  _$VehicleCategoryMeta get VehicleCategory => const _$VehicleCategoryMeta();
}

Serializer<VehicleCategory> _$vehicleCategorySerializer =
    _$VehicleCategorySerializer();

class _$VehicleCategorySerializer
    implements PrimitiveSerializer<VehicleCategory> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'LIGHT_PASSENGER': 'LIGHT_PASSENGER',
    'LIGHT_MIXED': 'LIGHT_MIXED',
    'HEAVY': 'HEAVY',
    'MOTORCYCLE': 'MOTORCYCLE',
    'TRAILER': 'TRAILER',
    'BUS': 'BUS',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'LIGHT_PASSENGER': 'LIGHT_PASSENGER',
    'LIGHT_MIXED': 'LIGHT_MIXED',
    'HEAVY': 'HEAVY',
    'MOTORCYCLE': 'MOTORCYCLE',
    'TRAILER': 'TRAILER',
    'BUS': 'BUS',
  };

  @override
  final Iterable<Type> types = const <Type>[VehicleCategory];
  @override
  final String wireName = 'VehicleCategory';

  @override
  Object serialize(Serializers serializers, VehicleCategory object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  VehicleCategory deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      VehicleCategory.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
