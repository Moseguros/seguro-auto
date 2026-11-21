// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'coverage_type.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const CoverageType _$MTPL_ONLY = const CoverageType._('MTPL_ONLY');
const CoverageType _$COMPREHENSIVE = const CoverageType._('COMPREHENSIVE');
const CoverageType _$TEMPORARY = const CoverageType._('TEMPORARY');
const CoverageType _$BORDER = const CoverageType._('BORDER');

CoverageType _$valueOf(String name) {
  switch (name) {
    case 'MTPL_ONLY':
      return _$MTPL_ONLY;
    case 'COMPREHENSIVE':
      return _$COMPREHENSIVE;
    case 'TEMPORARY':
      return _$TEMPORARY;
    case 'BORDER':
      return _$BORDER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<CoverageType> _$values =
    BuiltSet<CoverageType>(const <CoverageType>[
  _$MTPL_ONLY,
  _$COMPREHENSIVE,
  _$TEMPORARY,
  _$BORDER,
]);

class _$CoverageTypeMeta {
  const _$CoverageTypeMeta();
  CoverageType get MTPL_ONLY => _$MTPL_ONLY;
  CoverageType get COMPREHENSIVE => _$COMPREHENSIVE;
  CoverageType get TEMPORARY => _$TEMPORARY;
  CoverageType get BORDER => _$BORDER;
  CoverageType valueOf(String name) => _$valueOf(name);
  BuiltSet<CoverageType> get values => _$values;
}

abstract class _$CoverageTypeMixin {
  // ignore: non_constant_identifier_names
  _$CoverageTypeMeta get CoverageType => const _$CoverageTypeMeta();
}

Serializer<CoverageType> _$coverageTypeSerializer = _$CoverageTypeSerializer();

class _$CoverageTypeSerializer implements PrimitiveSerializer<CoverageType> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MTPL_ONLY': 'MTPL_ONLY',
    'COMPREHENSIVE': 'COMPREHENSIVE',
    'TEMPORARY': 'TEMPORARY',
    'BORDER': 'BORDER',
  };

  @override
  final Iterable<Type> types = const <Type>[CoverageType];
  @override
  final String wireName = 'CoverageType';

  @override
  Object serialize(Serializers serializers, CoverageType object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  CoverageType deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      CoverageType.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
