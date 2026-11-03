//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'vehicle_usage.g.dart';

class VehicleUsage extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PRIVATE')
  static const VehicleUsage PRIVATE = _$PRIVATE;
  @BuiltValueEnumConst(wireName: r'RENTAL')
  static const VehicleUsage RENTAL = _$RENTAL;
  @BuiltValueEnumConst(wireName: r'PASSENGER_TRANSPORT')
  static const VehicleUsage PASSENGER_TRANSPORT = _$PASSENGER_TRANSPORT;
  @BuiltValueEnumConst(wireName: r'GOODS')
  static const VehicleUsage GOODS = _$GOODS;
  @BuiltValueEnumConst(wireName: r'TAXI')
  static const VehicleUsage TAXI = _$TAXI;
  @BuiltValueEnumConst(wireName: r'DRIVING_SCHOOL')
  static const VehicleUsage DRIVING_SCHOOL = _$DRIVING_SCHOOL;

  static Serializer<VehicleUsage> get serializer => _$vehicleUsageSerializer;

  const VehicleUsage._(String name): super(name);

  static BuiltSet<VehicleUsage> get values => _$values;
  static VehicleUsage valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class VehicleUsageMixin = Object with _$VehicleUsageMixin;

