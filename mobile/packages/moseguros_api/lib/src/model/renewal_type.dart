//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'renewal_type.g.dart';

class RenewalType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ANNUAL_RENEWABLE')
  static const RenewalType ANNUAL_RENEWABLE = _$ANNUAL_RENEWABLE;
  @BuiltValueEnumConst(wireName: r'FIXED_TERM')
  static const RenewalType FIXED_TERM = _$FIXED_TERM;

  static Serializer<RenewalType> get serializer => _$renewalTypeSerializer;

  const RenewalType._(String name): super(name);

  static BuiltSet<RenewalType> get values => _$values;
  static RenewalType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class RenewalTypeMixin = Object with _$RenewalTypeMixin;

