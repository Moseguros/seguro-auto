//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'declaration_question.g.dart';

class DeclarationQuestion extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CLAIMS_LAST_3_YEARS')
  static const DeclarationQuestion cLAIMSLAST3YEARS = _$cLAIMSLAST3YEARS;
  @BuiltValueEnumConst(wireName: r'PREVIOUS_REFUSAL')
  static const DeclarationQuestion PREVIOUS_REFUSAL = _$PREVIOUS_REFUSAL;
  @BuiltValueEnumConst(wireName: r'USAGE_DIFFERENT')
  static const DeclarationQuestion USAGE_DIFFERENT = _$USAGE_DIFFERENT;
  @BuiltValueEnumConst(wireName: r'VEHICLE_MODIFIED')
  static const DeclarationQuestion VEHICLE_MODIFIED = _$VEHICLE_MODIFIED;
  @BuiltValueEnumConst(wireName: r'NOVICE_DRIVERS')
  static const DeclarationQuestion NOVICE_DRIVERS = _$NOVICE_DRIVERS;

  static Serializer<DeclarationQuestion> get serializer => _$declarationQuestionSerializer;

  const DeclarationQuestion._(String name): super(name);

  static BuiltSet<DeclarationQuestion> get values => _$values;
  static DeclarationQuestion valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class DeclarationQuestionMixin = Object with _$DeclarationQuestionMixin;

