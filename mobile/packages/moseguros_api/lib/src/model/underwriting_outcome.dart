//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'underwriting_outcome.g.dart';

class UnderwritingOutcome extends EnumClass {

  @BuiltValueEnumConst(wireName: r'AUTOMATIC')
  static const UnderwritingOutcome AUTOMATIC = _$AUTOMATIC;
  @BuiltValueEnumConst(wireName: r'MANUAL_REVIEW')
  static const UnderwritingOutcome MANUAL_REVIEW = _$MANUAL_REVIEW;

  static Serializer<UnderwritingOutcome> get serializer => _$underwritingOutcomeSerializer;

  const UnderwritingOutcome._(String name): super(name);

  static BuiltSet<UnderwritingOutcome> get values => _$values;
  static UnderwritingOutcome valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class UnderwritingOutcomeMixin = Object with _$UnderwritingOutcomeMixin;

