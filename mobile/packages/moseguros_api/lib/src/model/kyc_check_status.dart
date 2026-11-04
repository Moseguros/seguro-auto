//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_check_status.g.dart';

class KycCheckStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PENDING')
  static const KycCheckStatus PENDING = _$PENDING;
  @BuiltValueEnumConst(wireName: r'VERIFIED')
  static const KycCheckStatus VERIFIED = _$VERIFIED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const KycCheckStatus REJECTED = _$REJECTED;

  static Serializer<KycCheckStatus> get serializer => _$kycCheckStatusSerializer;

  const KycCheckStatus._(String name): super(name);

  static BuiltSet<KycCheckStatus> get values => _$values;
  static KycCheckStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class KycCheckStatusMixin = Object with _$KycCheckStatusMixin;

