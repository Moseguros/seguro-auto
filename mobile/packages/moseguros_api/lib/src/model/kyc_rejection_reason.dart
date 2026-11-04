//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_rejection_reason.g.dart';

class KycRejectionReason extends EnumClass {

  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'DOCUMENT_UNREADABLE')
  static const KycRejectionReason DOCUMENT_UNREADABLE = _$DOCUMENT_UNREADABLE;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'DOCUMENT_EXPIRED')
  static const KycRejectionReason DOCUMENT_EXPIRED = _$DOCUMENT_EXPIRED;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'DATA_MISMATCH')
  static const KycRejectionReason DATA_MISMATCH = _$DATA_MISMATCH;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'SELFIE_MISMATCH')
  static const KycRejectionReason SELFIE_MISMATCH = _$SELFIE_MISMATCH;
  /// Só em REJECTED
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const KycRejectionReason OTHER = _$OTHER;

  static Serializer<KycRejectionReason> get serializer => _$kycRejectionReasonSerializer;

  const KycRejectionReason._(String name): super(name);

  static BuiltSet<KycRejectionReason> get values => _$values;
  static KycRejectionReason valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class KycRejectionReasonMixin = Object with _$KycRejectionReasonMixin;

