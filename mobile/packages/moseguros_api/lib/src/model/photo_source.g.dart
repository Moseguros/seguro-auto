// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'photo_source.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PhotoSource _$CAMERA = const PhotoSource._('CAMERA');
const PhotoSource _$UPLOAD = const PhotoSource._('UPLOAD');

PhotoSource _$valueOf(String name) {
  switch (name) {
    case 'CAMERA':
      return _$CAMERA;
    case 'UPLOAD':
      return _$UPLOAD;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PhotoSource> _$values =
    BuiltSet<PhotoSource>(const <PhotoSource>[
  _$CAMERA,
  _$UPLOAD,
]);

class _$PhotoSourceMeta {
  const _$PhotoSourceMeta();
  PhotoSource get CAMERA => _$CAMERA;
  PhotoSource get UPLOAD => _$UPLOAD;
  PhotoSource valueOf(String name) => _$valueOf(name);
  BuiltSet<PhotoSource> get values => _$values;
}

abstract class _$PhotoSourceMixin {
  // ignore: non_constant_identifier_names
  _$PhotoSourceMeta get PhotoSource => const _$PhotoSourceMeta();
}

Serializer<PhotoSource> _$photoSourceSerializer = _$PhotoSourceSerializer();

class _$PhotoSourceSerializer implements PrimitiveSerializer<PhotoSource> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CAMERA': 'CAMERA',
    'UPLOAD': 'UPLOAD',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CAMERA': 'CAMERA',
    'UPLOAD': 'UPLOAD',
  };

  @override
  final Iterable<Type> types = const <Type>[PhotoSource];
  @override
  final String wireName = 'PhotoSource';

  @override
  Object serialize(Serializers serializers, PhotoSource object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PhotoSource deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PhotoSource.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
