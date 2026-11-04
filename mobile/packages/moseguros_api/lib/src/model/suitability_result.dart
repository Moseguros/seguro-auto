//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suitability_result.g.dart';

class SuitabilityResult extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SUITABLE')
  static const SuitabilityResult SUITABLE = _$SUITABLE;
  @BuiltValueEnumConst(wireName: r'WARNED')
  static const SuitabilityResult WARNED = _$WARNED;

  static Serializer<SuitabilityResult> get serializer => _$suitabilityResultSerializer;

  const SuitabilityResult._(String name): super(name);

  static BuiltSet<SuitabilityResult> get values => _$values;
  static SuitabilityResult valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class SuitabilityResultMixin = Object with _$SuitabilityResultMixin;

