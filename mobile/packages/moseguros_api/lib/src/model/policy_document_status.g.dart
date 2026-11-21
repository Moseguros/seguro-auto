// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_document_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyDocumentStatus _$READY = const PolicyDocumentStatus._('READY');
const PolicyDocumentStatus _$PENDING = const PolicyDocumentStatus._('PENDING');

PolicyDocumentStatus _$valueOf(String name) {
  switch (name) {
    case 'READY':
      return _$READY;
    case 'PENDING':
      return _$PENDING;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyDocumentStatus> _$values =
    BuiltSet<PolicyDocumentStatus>(const <PolicyDocumentStatus>[
  _$READY,
  _$PENDING,
]);

class _$PolicyDocumentStatusMeta {
  const _$PolicyDocumentStatusMeta();
  PolicyDocumentStatus get READY => _$READY;
  PolicyDocumentStatus get PENDING => _$PENDING;
  PolicyDocumentStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<PolicyDocumentStatus> get values => _$values;
}

abstract class _$PolicyDocumentStatusMixin {
  // ignore: non_constant_identifier_names
  _$PolicyDocumentStatusMeta get PolicyDocumentStatus =>
      const _$PolicyDocumentStatusMeta();
}

Serializer<PolicyDocumentStatus> _$policyDocumentStatusSerializer =
    _$PolicyDocumentStatusSerializer();

class _$PolicyDocumentStatusSerializer
    implements PrimitiveSerializer<PolicyDocumentStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'READY': 'READY',
    'PENDING': 'PENDING',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'READY': 'READY',
    'PENDING': 'PENDING',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyDocumentStatus];
  @override
  final String wireName = 'PolicyDocumentStatus';

  @override
  Object serialize(Serializers serializers, PolicyDocumentStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyDocumentStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyDocumentStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
