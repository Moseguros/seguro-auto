//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_event_type.g.dart';

class PolicyEventType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'ISSUED')
  static const PolicyEventType ISSUED = _$ISSUED;
  @BuiltValueEnumConst(wireName: r'CERTIFICATE_ISSUED')
  static const PolicyEventType CERTIFICATE_ISSUED = _$CERTIFICATE_ISSUED;
  @BuiltValueEnumConst(wireName: r'DOCUMENTS_READY')
  static const PolicyEventType DOCUMENTS_READY = _$DOCUMENTS_READY;

  static Serializer<PolicyEventType> get serializer => _$policyEventTypeSerializer;

  const PolicyEventType._(String name): super(name);

  static BuiltSet<PolicyEventType> get values => _$values;
  static PolicyEventType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PolicyEventTypeMixin = Object with _$PolicyEventTypeMixin;

