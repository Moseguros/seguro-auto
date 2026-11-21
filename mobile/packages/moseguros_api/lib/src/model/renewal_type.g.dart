// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'renewal_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RenewalType _$ANNUAL_RENEWABLE = const RenewalType._('ANNUAL_RENEWABLE');
const RenewalType _$FIXED_TERM = const RenewalType._('FIXED_TERM');

RenewalType _$valueOf(String name) {
  switch (name) {
    case 'ANNUAL_RENEWABLE':
      return _$ANNUAL_RENEWABLE;
    case 'FIXED_TERM':
      return _$FIXED_TERM;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RenewalType> _$values =
    BuiltSet<RenewalType>(const <RenewalType>[
  _$ANNUAL_RENEWABLE,
  _$FIXED_TERM,
]);

class _$RenewalTypeMeta {
  const _$RenewalTypeMeta();
  RenewalType get ANNUAL_RENEWABLE => _$ANNUAL_RENEWABLE;
  RenewalType get FIXED_TERM => _$FIXED_TERM;
  RenewalType valueOf(String name) => _$valueOf(name);
  BuiltSet<RenewalType> get values => _$values;
}

abstract class _$RenewalTypeMixin {
  // ignore: non_constant_identifier_names
  _$RenewalTypeMeta get RenewalType => const _$RenewalTypeMeta();
}

Serializer<RenewalType> _$renewalTypeSerializer = _$RenewalTypeSerializer();

class _$RenewalTypeSerializer implements PrimitiveSerializer<RenewalType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'ANNUAL_RENEWABLE': 'ANNUAL_RENEWABLE',
    'FIXED_TERM': 'FIXED_TERM',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'ANNUAL_RENEWABLE': 'ANNUAL_RENEWABLE',
    'FIXED_TERM': 'FIXED_TERM',
  };

  @override
  final Iterable<Type> types = const <Type>[RenewalType];
  @override
  final String wireName = 'RenewalType';

  @override
  Object serialize(Serializers serializers, RenewalType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RenewalType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RenewalType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
