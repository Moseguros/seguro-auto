//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suitability_reason.g.dart';

class SuitabilityReason extends EnumClass {

  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'NEED_NOT_COVERED_OWN_DAMAGE')
  static const SuitabilityReason NEED_NOT_COVERED_OWN_DAMAGE = _$NEED_NOT_COVERED_OWN_DAMAGE;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'NEED_NOT_COVERED_THEFT_FIRE')
  static const SuitabilityReason NEED_NOT_COVERED_THEFT_FIRE = _$NEED_NOT_COVERED_THEFT_FIRE;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'COVER_ABOVE_NEED')
  static const SuitabilityReason COVER_ABOVE_NEED = _$COVER_ABOVE_NEED;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'NEED_UNSURE')
  static const SuitabilityReason NEED_UNSURE = _$NEED_UNSURE;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'FINANCED_WITHOUT_OWN_DAMAGE')
  static const SuitabilityReason FINANCED_WITHOUT_OWN_DAMAGE = _$FINANCED_WITHOUT_OWN_DAMAGE;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'CANNOT_AFFORD_REPAIR')
  static const SuitabilityReason CANNOT_AFFORD_REPAIR = _$CANNOT_AFFORD_REPAIR;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'CROSS_BORDER_NOT_COVERED')
  static const SuitabilityReason CROSS_BORDER_NOT_COVERED = _$CROSS_BORDER_NOT_COVERED;
  /// Vazia em SUITABLE
  @BuiltValueEnumConst(wireName: r'DEDUCTIBLE_NOT_WANTED')
  static const SuitabilityReason DEDUCTIBLE_NOT_WANTED = _$DEDUCTIBLE_NOT_WANTED;

  static Serializer<SuitabilityReason> get serializer => _$suitabilityReasonSerializer;

  const SuitabilityReason._(String name): super(name);

  static BuiltSet<SuitabilityReason> get values => _$values;
  static SuitabilityReason valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class SuitabilityReasonMixin = Object with _$SuitabilityReasonMixin;

