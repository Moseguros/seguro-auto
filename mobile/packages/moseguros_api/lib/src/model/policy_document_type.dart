//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_document_type.g.dart';

class PolicyDocumentType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CERTIFICATE')
  static const PolicyDocumentType CERTIFICATE = _$CERTIFICATE;
  @BuiltValueEnumConst(wireName: r'SCHEDULE')
  static const PolicyDocumentType SCHEDULE = _$SCHEDULE;
  @BuiltValueEnumConst(wireName: r'GENERAL_CONDITIONS')
  static const PolicyDocumentType GENERAL_CONDITIONS = _$GENERAL_CONDITIONS;
  @BuiltValueEnumConst(wireName: r'PRE_CONTRACT_INFO')
  static const PolicyDocumentType PRE_CONTRACT_INFO = _$PRE_CONTRACT_INFO;
  @BuiltValueEnumConst(wireName: r'PROPOSAL')
  static const PolicyDocumentType PROPOSAL = _$PROPOSAL;

  static Serializer<PolicyDocumentType> get serializer => _$policyDocumentTypeSerializer;

  const PolicyDocumentType._(String name): super(name);

  static BuiltSet<PolicyDocumentType> get values => _$values;
  static PolicyDocumentType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PolicyDocumentTypeMixin = Object with _$PolicyDocumentTypeMixin;

