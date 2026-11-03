//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'vehicle_category.g.dart';

class VehicleCategory extends EnumClass {

  @BuiltValueEnumConst(wireName: r'LIGHT_PASSENGER')
  static const VehicleCategory LIGHT_PASSENGER = _$LIGHT_PASSENGER;
  @BuiltValueEnumConst(wireName: r'LIGHT_MIXED')
  static const VehicleCategory LIGHT_MIXED = _$LIGHT_MIXED;
  @BuiltValueEnumConst(wireName: r'HEAVY')
  static const VehicleCategory HEAVY = _$HEAVY;
  @BuiltValueEnumConst(wireName: r'MOTORCYCLE')
  static const VehicleCategory MOTORCYCLE = _$MOTORCYCLE;
  @BuiltValueEnumConst(wireName: r'TRAILER')
  static const VehicleCategory TRAILER = _$TRAILER;
  @BuiltValueEnumConst(wireName: r'BUS')
  static const VehicleCategory BUS = _$BUS;

  static Serializer<VehicleCategory> get serializer => _$vehicleCategorySerializer;

  const VehicleCategory._(String name): super(name);

  static BuiltSet<VehicleCategory> get values => _$values;
  static VehicleCategory valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class VehicleCategoryMixin = Object with _$VehicleCategoryMixin;

