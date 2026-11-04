//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suitability_question.g.dart';

class SuitabilityQuestion extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SUIT_NEED')
  static const SuitabilityQuestion NEED = _$NEED;
  @BuiltValueEnumConst(wireName: r'SUIT_FINANCED')
  static const SuitabilityQuestion FINANCED = _$FINANCED;
  @BuiltValueEnumConst(wireName: r'SUIT_CAN_AFFORD_REPAIR')
  static const SuitabilityQuestion CAN_AFFORD_REPAIR = _$CAN_AFFORD_REPAIR;
  @BuiltValueEnumConst(wireName: r'SUIT_CROSS_BORDER')
  static const SuitabilityQuestion CROSS_BORDER = _$CROSS_BORDER;
  @BuiltValueEnumConst(wireName: r'SUIT_DEDUCTIBLE')
  static const SuitabilityQuestion DEDUCTIBLE = _$DEDUCTIBLE;

  static Serializer<SuitabilityQuestion> get serializer => _$suitabilityQuestionSerializer;

  const SuitabilityQuestion._(String name): super(name);

  static BuiltSet<SuitabilityQuestion> get values => _$values;
  static SuitabilityQuestion valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class SuitabilityQuestionMixin = Object with _$SuitabilityQuestionMixin;

