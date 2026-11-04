// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policyholder_source.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PolicyholderSource _$PROFILE = const PolicyholderSource._('PROFILE');
const PolicyholderSource _$BROKER = const PolicyholderSource._('BROKER');

PolicyholderSource _$valueOf(String name) {
  switch (name) {
    case 'PROFILE':
      return _$PROFILE;
    case 'BROKER':
      return _$BROKER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PolicyholderSource> _$values =
    BuiltSet<PolicyholderSource>(const <PolicyholderSource>[
  _$PROFILE,
  _$BROKER,
]);

class _$PolicyholderSourceMeta {
  const _$PolicyholderSourceMeta();
  PolicyholderSource get PROFILE => _$PROFILE;
  PolicyholderSource get BROKER => _$BROKER;
  PolicyholderSource valueOf(String name) => _$valueOf(name);
  BuiltSet<PolicyholderSource> get values => _$values;
}

abstract class _$PolicyholderSourceMixin {
  // ignore: non_constant_identifier_names
  _$PolicyholderSourceMeta get PolicyholderSource =>
      const _$PolicyholderSourceMeta();
}

Serializer<PolicyholderSource> _$policyholderSourceSerializer =
    _$PolicyholderSourceSerializer();

class _$PolicyholderSourceSerializer
    implements PrimitiveSerializer<PolicyholderSource> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PROFILE': 'PROFILE',
    'BROKER': 'BROKER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PROFILE': 'PROFILE',
    'BROKER': 'BROKER',
  };

  @override
  final Iterable<Type> types = const <Type>[PolicyholderSource];
  @override
  final String wireName = 'PolicyholderSource';

  @override
  Object serialize(Serializers serializers, PolicyholderSource object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PolicyholderSource deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PolicyholderSource.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
