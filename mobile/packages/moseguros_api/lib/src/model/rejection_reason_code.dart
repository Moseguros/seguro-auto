//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rejection_reason_code.g.dart';

class RejectionReasonCode extends EnumClass {

  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'RISK_NOT_ACCEPTED')
  static const RejectionReasonCode RISK_NOT_ACCEPTED = _$RISK_NOT_ACCEPTED;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'KYC_FAILED')
  static const RejectionReasonCode KYC_FAILED = _$KYC_FAILED;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'INSPECTION_FAILED')
  static const RejectionReasonCode INSPECTION_FAILED = _$INSPECTION_FAILED;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'FALSE_DECLARATION')
  static const RejectionReasonCode FALSE_DECLARATION = _$FALSE_DECLARATION;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'DOCUMENTS_INVALID')
  static const RejectionReasonCode DOCUMENTS_INVALID = _$DOCUMENTS_INVALID;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const RejectionReasonCode OTHER = _$OTHER;

  static Serializer<RejectionReasonCode> get serializer => _$rejectionReasonCodeSerializer;

  const RejectionReasonCode._(String name): super(name);

  static BuiltSet<RejectionReasonCode> get values => _$values;
  static RejectionReasonCode valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class RejectionReasonCodeMixin = Object with _$RejectionReasonCodeMixin;

