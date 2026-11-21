// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_document_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyDocumentType _$CERTIFICATE =
    const PolicyDocumentType._('CERTIFICATE');
const PolicyDocumentType _$SCHEDULE = const PolicyDocumentType._('SCHEDULE');
const PolicyDocumentType _$GENERAL_CONDITIONS =
    const PolicyDocumentType._('GENERAL_CONDITIONS');
const PolicyDocumentType _$PRE_CONTRACT_INFO =
    const PolicyDocumentType._('PRE_CONTRACT_INFO');
const PolicyDocumentType _$PROPOSAL = const PolicyDocumentType._('PROPOSAL');

PolicyDocumentType _$valueOf(String name) {
  switch (name) {
    case 'CERTIFICATE':
      return _$CERTIFICATE;
    case 'SCHEDULE':
      return _$SCHEDULE;
    case 'GENERAL_CONDITIONS':
      return _$GENERAL_CONDITIONS;
    case 'PRE_CONTRACT_INFO':
      return _$PRE_CONTRACT_INFO;
    case 'PROPOSAL':
      return _$PROPOSAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyDocumentType> _$values =
    BuiltSet<PolicyDocumentType>(const <PolicyDocumentType>[
  _$CERTIFICATE,
  _$SCHEDULE,
  _$GENERAL_CONDITIONS,
  _$PRE_CONTRACT_INFO,
  _$PROPOSAL,
]);

class _$PolicyDocumentTypeMeta {
  const _$PolicyDocumentTypeMeta();
  PolicyDocumentType get CERTIFICATE => _$CERTIFICATE;
  PolicyDocumentType get SCHEDULE => _$SCHEDULE;
  PolicyDocumentType get GENERAL_CONDITIONS => _$GENERAL_CONDITIONS;
  PolicyDocumentType get PRE_CONTRACT_INFO => _$PRE_CONTRACT_INFO;
  PolicyDocumentType get PROPOSAL => _$PROPOSAL;
  PolicyDocumentType valueOf(String name) => _$valueOf(name);
  BuiltSet<PolicyDocumentType> get values => _$values;
}

abstract class _$PolicyDocumentTypeMixin {
  // ignore: non_constant_identifier_names
  _$PolicyDocumentTypeMeta get PolicyDocumentType =>
      const _$PolicyDocumentTypeMeta();
}

Serializer<PolicyDocumentType> _$policyDocumentTypeSerializer =
    _$PolicyDocumentTypeSerializer();

class _$PolicyDocumentTypeSerializer
    implements PrimitiveSerializer<PolicyDocumentType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CERTIFICATE': 'CERTIFICATE',
    'SCHEDULE': 'SCHEDULE',
    'GENERAL_CONDITIONS': 'GENERAL_CONDITIONS',
    'PRE_CONTRACT_INFO': 'PRE_CONTRACT_INFO',
    'PROPOSAL': 'PROPOSAL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CERTIFICATE': 'CERTIFICATE',
    'SCHEDULE': 'SCHEDULE',
    'GENERAL_CONDITIONS': 'GENERAL_CONDITIONS',
    'PRE_CONTRACT_INFO': 'PRE_CONTRACT_INFO',
    'PROPOSAL': 'PROPOSAL',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyDocumentType];
  @override
  final String wireName = 'PolicyDocumentType';

  @override
  Object serialize(Serializers serializers, PolicyDocumentType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyDocumentType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyDocumentType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
