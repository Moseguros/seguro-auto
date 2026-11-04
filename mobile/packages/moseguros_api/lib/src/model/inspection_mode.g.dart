// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'inspection_mode.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InspectionMode _$SELF_PHOTOS = const InspectionMode._('SELF_PHOTOS');
const InspectionMode _$ON_SITE = const InspectionMode._('ON_SITE');

InspectionMode _$valueOf(String name) {
  switch (name) {
    case 'SELF_PHOTOS':
      return _$SELF_PHOTOS;
    case 'ON_SITE':
      return _$ON_SITE;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InspectionMode> _$values =
    BuiltSet<InspectionMode>(const <InspectionMode>[
  _$SELF_PHOTOS,
  _$ON_SITE,
]);

class _$InspectionModeMeta {
  const _$InspectionModeMeta();
  InspectionMode get SELF_PHOTOS => _$SELF_PHOTOS;
  InspectionMode get ON_SITE => _$ON_SITE;
  InspectionMode valueOf(String name) => _$valueOf(name);
  BuiltSet<InspectionMode> get values => _$values;
}

abstract class _$InspectionModeMixin {
  // ignore: non_constant_identifier_names
  _$InspectionModeMeta get InspectionMode => const _$InspectionModeMeta();
}

Serializer<InspectionMode> _$inspectionModeSerializer =
    _$InspectionModeSerializer();

class _$InspectionModeSerializer
    implements PrimitiveSerializer<InspectionMode> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'SELF_PHOTOS': 'SELF_PHOTOS',
    'ON_SITE': 'ON_SITE',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'SELF_PHOTOS': 'SELF_PHOTOS',
    'ON_SITE': 'ON_SITE',
  };

  @override
  final Iterable<Type> types = const <Type>[InspectionMode];
  @override
  final String wireName = 'InspectionMode';

  @override
  Object serialize(Serializers serializers, InspectionMode object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InspectionMode deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InspectionMode.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
