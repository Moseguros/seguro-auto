//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'underwriting_failure.g.dart';

class UnderwritingFailure extends EnumClass {

  @BuiltValueEnumConst(wireName: r'AUTO_ACCEPT_DISABLED')
  static const UnderwritingFailure AUTO_ACCEPT_DISABLED = _$AUTO_ACCEPT_DISABLED;
  @BuiltValueEnumConst(wireName: r'KYC_REQUIRED')
  static const UnderwritingFailure KYC_REQUIRED = _$KYC_REQUIRED;
  @BuiltValueEnumConst(wireName: r'DECLARATION_POSITIVE')
  static const UnderwritingFailure DECLARATION_POSITIVE = _$DECLARATION_POSITIVE;
  @BuiltValueEnumConst(wireName: r'INSPECTION_NOT_APPROVED')
  static const UnderwritingFailure INSPECTION_NOT_APPROVED = _$INSPECTION_NOT_APPROVED;
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE_MISSING')
  static const UnderwritingFailure VEHICLE_VALUE_MISSING = _$VEHICLE_VALUE_MISSING;
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE_ABOVE_LIMIT')
  static const UnderwritingFailure VEHICLE_VALUE_ABOVE_LIMIT = _$VEHICLE_VALUE_ABOVE_LIMIT;

  static Serializer<UnderwritingFailure> get serializer => _$underwritingFailureSerializer;

  const UnderwritingFailure._(String name): super(name);

  static BuiltSet<UnderwritingFailure> get values => _$values;
  static UnderwritingFailure valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class UnderwritingFailureMixin = Object with _$UnderwritingFailureMixin;

