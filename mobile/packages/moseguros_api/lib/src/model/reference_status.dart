//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reference_status.g.dart';

class ReferenceStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ACTIVE')
  static const ReferenceStatus ACTIVE = _$ACTIVE;
  @BuiltValueEnumConst(wireName: r'PAID')
  static const ReferenceStatus PAID = _$PAID;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const ReferenceStatus EXPIRED = _$EXPIRED;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const ReferenceStatus CANCELLED = _$CANCELLED;

  static Serializer<ReferenceStatus> get serializer => _$referenceStatusSerializer;

  const ReferenceStatus._(String name): super(name);

  static BuiltSet<ReferenceStatus> get values => _$values;
  static ReferenceStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class ReferenceStatusMixin = Object with _$ReferenceStatusMixin;

