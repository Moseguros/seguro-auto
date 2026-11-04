//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'inspection_mode.g.dart';

class InspectionMode extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SELF_PHOTOS')
  static const InspectionMode SELF_PHOTOS = _$SELF_PHOTOS;
  @BuiltValueEnumConst(wireName: r'ON_SITE')
  static const InspectionMode ON_SITE = _$ON_SITE;

  static Serializer<InspectionMode> get serializer => _$inspectionModeSerializer;

  const InspectionMode._(String name): super(name);

  static BuiltSet<InspectionMode> get values => _$values;
  static InspectionMode valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class InspectionModeMixin = Object with _$InspectionModeMixin;

