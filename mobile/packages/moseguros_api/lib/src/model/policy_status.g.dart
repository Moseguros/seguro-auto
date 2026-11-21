// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyStatus _$ACTIVE = const PolicyStatus._('ACTIVE');
const PolicyStatus _$SUSPENDED = const PolicyStatus._('SUSPENDED');
const PolicyStatus _$CANCELLED = const PolicyStatus._('CANCELLED');
const PolicyStatus _$EXPIRED = const PolicyStatus._('EXPIRED');
const PolicyStatus _$LAPSED = const PolicyStatus._('LAPSED');

PolicyStatus _$valueOf(String name) {
  switch (name) {
    case 'ACTIVE':
      return _$ACTIVE;
    case 'SUSPENDED':
      return _$SUSPENDED;
    case 'CANCELLED':
      return _$CANCELLED;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'LAPSED':
      return _$LAPSED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyStatus> _$values =
    BuiltSet<PolicyStatus>(const <PolicyStatus>[
  _$ACTIVE,
  _$SUSPENDED,
  _$CANCELLED,
  _$EXPIRED,
  _$LAPSED,
]);

class _$PolicyStatusMeta {
  const _$PolicyStatusMeta();
  PolicyStatus get ACTIVE => _$ACTIVE;
  PolicyStatus get SUSPENDED => _$SUSPENDED;
  PolicyStatus get CANCELLED => _$CANCELLED;
  PolicyStatus get EXPIRED => _$EXPIRED;
  PolicyStatus get LAPSED => _$LAPSED;
  PolicyStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<PolicyStatus> get values => _$values;
}

abstract class _$PolicyStatusMixin {
  // ignore: non_constant_identifier_names
  _$PolicyStatusMeta get PolicyStatus => const _$PolicyStatusMeta();
}

Serializer<PolicyStatus> _$policyStatusSerializer = _$PolicyStatusSerializer();

class _$PolicyStatusSerializer implements PrimitiveSerializer<PolicyStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ACTIVE': 'ACTIVE',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'EXPIRED': 'EXPIRED',
    'LAPSED': 'LAPSED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ACTIVE': 'ACTIVE',
    'SUSPENDED': 'SUSPENDED',
    'CANCELLED': 'CANCELLED',
    'EXPIRED': 'EXPIRED',
    'LAPSED': 'LAPSED',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyStatus];
  @override
  final String wireName = 'PolicyStatus';

  @override
  Object serialize(Serializers serializers, PolicyStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
