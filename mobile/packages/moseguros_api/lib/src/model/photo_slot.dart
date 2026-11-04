//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'photo_slot.g.dart';

class PhotoSlot extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FRONT')
  static const PhotoSlot FRONT = _$FRONT;
  @BuiltValueEnumConst(wireName: r'REAR')
  static const PhotoSlot REAR = _$REAR;
  @BuiltValueEnumConst(wireName: r'LEFT')
  static const PhotoSlot LEFT = _$LEFT;
  @BuiltValueEnumConst(wireName: r'RIGHT')
  static const PhotoSlot RIGHT = _$RIGHT;
  @BuiltValueEnumConst(wireName: r'ODOMETER')
  static const PhotoSlot ODOMETER = _$ODOMETER;
  @BuiltValueEnumConst(wireName: r'CHASSIS')
  static const PhotoSlot CHASSIS = _$CHASSIS;
  @BuiltValueEnumConst(wireName: r'EXTRA')
  static const PhotoSlot EXTRA = _$EXTRA;

  static Serializer<PhotoSlot> get serializer => _$photoSlotSerializer;

  const PhotoSlot._(String name): super(name);

  static BuiltSet<PhotoSlot> get values => _$values;
  static PhotoSlot valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PhotoSlotMixin = Object with _$PhotoSlotMixin;

