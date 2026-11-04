//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'photo_source.g.dart';

class PhotoSource extends EnumClass {

  /// Por defeito UPLOAD
  @BuiltValueEnumConst(wireName: r'CAMERA')
  static const PhotoSource CAMERA = _$CAMERA;
  /// Por defeito UPLOAD
  @BuiltValueEnumConst(wireName: r'UPLOAD')
  static const PhotoSource UPLOAD = _$UPLOAD;

  static Serializer<PhotoSource> get serializer => _$photoSourceSerializer;

  const PhotoSource._(String name): super(name);

  static BuiltSet<PhotoSource> get values => _$values;
  static PhotoSource valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PhotoSourceMixin = Object with _$PhotoSourceMixin;

