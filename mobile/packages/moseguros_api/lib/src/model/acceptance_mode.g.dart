// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'acceptance_mode.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const AcceptanceMode _$AUTOMATIC = const AcceptanceMode._('AUTOMATIC');
const AcceptanceMode _$MANUAL = const AcceptanceMode._('MANUAL');

AcceptanceMode _$valueOf(String name) {
  switch (name) {
    case 'AUTOMATIC':
      return _$AUTOMATIC;
    case 'MANUAL':
      return _$MANUAL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<AcceptanceMode> _$values =
    BuiltSet<AcceptanceMode>(const <AcceptanceMode>[
  _$AUTOMATIC,
  _$MANUAL,
]);

class _$AcceptanceModeMeta {
  const _$AcceptanceModeMeta();
  AcceptanceMode get AUTOMATIC => _$AUTOMATIC;
  AcceptanceMode get MANUAL => _$MANUAL;
  AcceptanceMode valueOf(String name) => _$valueOf(name);
  BuiltSet<AcceptanceMode> get values => _$values;
}

abstract class _$AcceptanceModeMixin {
  // ignore: non_constant_identifier_names
  _$AcceptanceModeMeta get AcceptanceMode => const _$AcceptanceModeMeta();
}

Serializer<AcceptanceMode> _$acceptanceModeSerializer =
    _$AcceptanceModeSerializer();

class _$AcceptanceModeSerializer
    implements PrimitiveSerializer<AcceptanceMode> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'AUTOMATIC': 'AUTOMATIC',
    'MANUAL': 'MANUAL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'AUTOMATIC': 'AUTOMATIC',
    'MANUAL': 'MANUAL',
  };

  @override
  final Iterable<Type> types = const <Type>[AcceptanceMode];
  @override
  final String wireName = 'AcceptanceMode';

  @override
  Object serialize(Serializers serializers, AcceptanceMode object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  AcceptanceMode deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      AcceptanceMode.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
