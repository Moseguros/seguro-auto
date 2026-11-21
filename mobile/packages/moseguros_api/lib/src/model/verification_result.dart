//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'verification_result.g.dart';

class VerificationResult extends EnumClass {

  @BuiltValueEnumConst(wireName: r'VALID')
  static const VerificationResult VALID = _$VALID;
  @BuiltValueEnumConst(wireName: r'NOT_YET_VALID')
  static const VerificationResult NOT_YET_VALID = _$NOT_YET_VALID;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const VerificationResult EXPIRED = _$EXPIRED;
  @BuiltValueEnumConst(wireName: r'SUSPENDED')
  static const VerificationResult SUSPENDED = _$SUSPENDED;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const VerificationResult CANCELLED = _$CANCELLED;
  @BuiltValueEnumConst(wireName: r'NOT_FOUND')
  static const VerificationResult NOT_FOUND = _$NOT_FOUND;

  static Serializer<VerificationResult> get serializer => _$verificationResultSerializer;

  const VerificationResult._(String name): super(name);

  static BuiltSet<VerificationResult> get values => _$values;
  static VerificationResult valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class VerificationResultMixin = Object with _$VerificationResultMixin;

