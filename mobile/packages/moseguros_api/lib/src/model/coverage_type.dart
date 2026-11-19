//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'coverage_type.g.dart';

class CoverageType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const CoverageType MTPL_ONLY = _$MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const CoverageType COMPREHENSIVE = _$COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const CoverageType TEMPORARY = _$TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const CoverageType BORDER = _$BORDER;

  static Serializer<CoverageType> get serializer => _$coverageTypeSerializer;

  const CoverageType._(String name): super(name);

  static BuiltSet<CoverageType> get values => _$values;
  static CoverageType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class CoverageTypeMixin = Object with _$CoverageTypeMixin;

