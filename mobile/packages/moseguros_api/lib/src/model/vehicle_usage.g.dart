// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'vehicle_usage.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const VehicleUsage _$PRIVATE = const VehicleUsage._('PRIVATE');
const VehicleUsage _$RENTAL = const VehicleUsage._('RENTAL');
const VehicleUsage _$PASSENGER_TRANSPORT =
    const VehicleUsage._('PASSENGER_TRANSPORT');
const VehicleUsage _$GOODS = const VehicleUsage._('GOODS');
const VehicleUsage _$TAXI = const VehicleUsage._('TAXI');
const VehicleUsage _$DRIVING_SCHOOL = const VehicleUsage._('DRIVING_SCHOOL');

VehicleUsage _$valueOf(String name) {
  switch (name) {
    case 'PRIVATE':
      return _$PRIVATE;
    case 'RENTAL':
      return _$RENTAL;
    case 'PASSENGER_TRANSPORT':
      return _$PASSENGER_TRANSPORT;
    case 'GOODS':
      return _$GOODS;
    case 'TAXI':
      return _$TAXI;
    case 'DRIVING_SCHOOL':
      return _$DRIVING_SCHOOL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<VehicleUsage> _$values =
    BuiltSet<VehicleUsage>(const <VehicleUsage>[
  _$PRIVATE,
  _$RENTAL,
  _$PASSENGER_TRANSPORT,
  _$GOODS,
  _$TAXI,
  _$DRIVING_SCHOOL,
]);

class _$VehicleUsageMeta {
  const _$VehicleUsageMeta();
  VehicleUsage get PRIVATE => _$PRIVATE;
  VehicleUsage get RENTAL => _$RENTAL;
  VehicleUsage get PASSENGER_TRANSPORT => _$PASSENGER_TRANSPORT;
  VehicleUsage get GOODS => _$GOODS;
  VehicleUsage get TAXI => _$TAXI;
  VehicleUsage get DRIVING_SCHOOL => _$DRIVING_SCHOOL;
  VehicleUsage valueOf(String name) => _$valueOf(name);
  BuiltSet<VehicleUsage> get values => _$values;
}

abstract class _$VehicleUsageMixin {
  // ignore: non_constant_identifier_names
  _$VehicleUsageMeta get VehicleUsage => const _$VehicleUsageMeta();
}

Serializer<VehicleUsage> _$vehicleUsageSerializer = _$VehicleUsageSerializer();

class _$VehicleUsageSerializer implements PrimitiveSerializer<VehicleUsage> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PRIVATE': 'PRIVATE',
    'RENTAL': 'RENTAL',
    'PASSENGER_TRANSPORT': 'PASSENGER_TRANSPORT',
    'GOODS': 'GOODS',
    'TAXI': 'TAXI',
    'DRIVING_SCHOOL': 'DRIVING_SCHOOL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PRIVATE': 'PRIVATE',
    'RENTAL': 'RENTAL',
    'PASSENGER_TRANSPORT': 'PASSENGER_TRANSPORT',
    'GOODS': 'GOODS',
    'TAXI': 'TAXI',
    'DRIVING_SCHOOL': 'DRIVING_SCHOOL',
  };

  @override
  final Iterable<Type> types = const <Type>[VehicleUsage];
  @override
  final String wireName = 'VehicleUsage';

  @override
  Object serialize(Serializers serializers, VehicleUsage object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  VehicleUsage deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      VehicleUsage.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
