// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'photo_slot.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const PhotoSlot _$FRONT = const PhotoSlot._('FRONT');
const PhotoSlot _$REAR = const PhotoSlot._('REAR');
const PhotoSlot _$LEFT = const PhotoSlot._('LEFT');
const PhotoSlot _$RIGHT = const PhotoSlot._('RIGHT');
const PhotoSlot _$ODOMETER = const PhotoSlot._('ODOMETER');
const PhotoSlot _$CHASSIS = const PhotoSlot._('CHASSIS');
const PhotoSlot _$EXTRA = const PhotoSlot._('EXTRA');

PhotoSlot _$valueOf(String name) {
  switch (name) {
    case 'FRONT':
      return _$FRONT;
    case 'REAR':
      return _$REAR;
    case 'LEFT':
      return _$LEFT;
    case 'RIGHT':
      return _$RIGHT;
    case 'ODOMETER':
      return _$ODOMETER;
    case 'CHASSIS':
      return _$CHASSIS;
    case 'EXTRA':
      return _$EXTRA;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<PhotoSlot> _$values = BuiltSet<PhotoSlot>(const <PhotoSlot>[
  _$FRONT,
  _$REAR,
  _$LEFT,
  _$RIGHT,
  _$ODOMETER,
  _$CHASSIS,
  _$EXTRA,
]);

class _$PhotoSlotMeta {
  const _$PhotoSlotMeta();
  PhotoSlot get FRONT => _$FRONT;
  PhotoSlot get REAR => _$REAR;
  PhotoSlot get LEFT => _$LEFT;
  PhotoSlot get RIGHT => _$RIGHT;
  PhotoSlot get ODOMETER => _$ODOMETER;
  PhotoSlot get CHASSIS => _$CHASSIS;
  PhotoSlot get EXTRA => _$EXTRA;
  PhotoSlot valueOf(String name) => _$valueOf(name);
  BuiltSet<PhotoSlot> get values => _$values;
}

abstract class _$PhotoSlotMixin {
  // ignore: non_constant_identifier_names
  _$PhotoSlotMeta get PhotoSlot => const _$PhotoSlotMeta();
}

Serializer<PhotoSlot> _$photoSlotSerializer = _$PhotoSlotSerializer();

class _$PhotoSlotSerializer implements PrimitiveSerializer<PhotoSlot> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'FRONT': 'FRONT',
    'REAR': 'REAR',
    'LEFT': 'LEFT',
    'RIGHT': 'RIGHT',
    'ODOMETER': 'ODOMETER',
    'CHASSIS': 'CHASSIS',
    'EXTRA': 'EXTRA',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'FRONT': 'FRONT',
    'REAR': 'REAR',
    'LEFT': 'LEFT',
    'RIGHT': 'RIGHT',
    'ODOMETER': 'ODOMETER',
    'CHASSIS': 'CHASSIS',
    'EXTRA': 'EXTRA',
  };

  @override
  final Iterable<Type> types = const <Type>[PhotoSlot];
  @override
  final String wireName = 'PhotoSlot';

  @override
  Object serialize(Serializers serializers, PhotoSlot object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  PhotoSlot deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      PhotoSlot.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
