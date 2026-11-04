//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'kyc_state.g.dart';

class KycState extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NONE')
  static const KycState NONE = _$NONE;
  @BuiltValueEnumConst(wireName: r'PENDING')
  static const KycState PENDING = _$PENDING;
  @BuiltValueEnumConst(wireName: r'VERIFIED')
  static const KycState VERIFIED = _$VERIFIED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const KycState REJECTED = _$REJECTED;
  @BuiltValueEnumConst(wireName: r'OUTDATED')
  static const KycState OUTDATED = _$OUTDATED;

  static Serializer<KycState> get serializer => _$kycStateSerializer;

  const KycState._(String name): super(name);

  static BuiltSet<KycState> get values => _$values;
  static KycState valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class KycStateMixin = Object with _$KycStateMixin;

