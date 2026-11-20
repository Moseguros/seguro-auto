//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_document_status.g.dart';

class PolicyDocumentStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'READY')
  static const PolicyDocumentStatus READY = _$READY;
  @BuiltValueEnumConst(wireName: r'PENDING')
  static const PolicyDocumentStatus PENDING = _$PENDING;

  static Serializer<PolicyDocumentStatus> get serializer => _$policyDocumentStatusSerializer;

  const PolicyDocumentStatus._(String name): super(name);

  static BuiltSet<PolicyDocumentStatus> get values => _$values;
  static PolicyDocumentStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PolicyDocumentStatusMixin = Object with _$PolicyDocumentStatusMixin;

