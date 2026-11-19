// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_event_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyEventType _$ISSUED = const PolicyEventType._('ISSUED');
const PolicyEventType _$CERTIFICATE_ISSUED =
    const PolicyEventType._('CERTIFICATE_ISSUED');
const PolicyEventType _$DOCUMENTS_READY =
    const PolicyEventType._('DOCUMENTS_READY');

PolicyEventType _$valueOf(String name) {
  switch (name) {
    case 'ISSUED':
      return _$ISSUED;
    case 'CERTIFICATE_ISSUED':
      return _$CERTIFICATE_ISSUED;
    case 'DOCUMENTS_READY':
      return _$DOCUMENTS_READY;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyEventType> _$values =
    BuiltSet<PolicyEventType>(const <PolicyEventType>[
  _$ISSUED,
  _$CERTIFICATE_ISSUED,
  _$DOCUMENTS_READY,
]);

class _$PolicyEventTypeMeta {
  const _$PolicyEventTypeMeta();
  PolicyEventType get ISSUED => _$ISSUED;
  PolicyEventType get CERTIFICATE_ISSUED => _$CERTIFICATE_ISSUED;
  PolicyEventType get DOCUMENTS_READY => _$DOCUMENTS_READY;
  PolicyEventType valueOf(String name) => _$valueOf(name);
  BuiltSet<PolicyEventType> get values => _$values;
}

abstract class _$PolicyEventTypeMixin {
  // ignore: non_constant_identifier_names
  _$PolicyEventTypeMeta get PolicyEventType => const _$PolicyEventTypeMeta();
}

Serializer<PolicyEventType> _$policyEventTypeSerializer =
    _$PolicyEventTypeSerializer();

class _$PolicyEventTypeSerializer
    implements PrimitiveSerializer<PolicyEventType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ISSUED': 'ISSUED',
    'CERTIFICATE_ISSUED': 'CERTIFICATE_ISSUED',
    'DOCUMENTS_READY': 'DOCUMENTS_READY',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ISSUED': 'ISSUED',
    'CERTIFICATE_ISSUED': 'CERTIFICATE_ISSUED',
    'DOCUMENTS_READY': 'DOCUMENTS_READY',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyEventType];
  @override
  final String wireName = 'PolicyEventType';

  @override
  Object serialize(Serializers serializers, PolicyEventType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyEventType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyEventType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
