// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_channel.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const NotificationChannel _$PUSH = const NotificationChannel._('PUSH');
const NotificationChannel _$WHATSAPP = const NotificationChannel._('WHATSAPP');
const NotificationChannel _$SMS = const NotificationChannel._('SMS');
const NotificationChannel _$EMAIL = const NotificationChannel._('EMAIL');

NotificationChannel _$valueOf(String name) {
  switch (name) {
    case 'PUSH':
      return _$PUSH;
    case 'WHATSAPP':
      return _$WHATSAPP;
    case 'SMS':
      return _$SMS;
    case 'EMAIL':
      return _$EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<NotificationChannel> _$values =
    BuiltSet<NotificationChannel>(const <NotificationChannel>[
  _$PUSH,
  _$WHATSAPP,
  _$SMS,
  _$EMAIL,
]);

class _$NotificationChannelMeta {
  const _$NotificationChannelMeta();
  NotificationChannel get PUSH => _$PUSH;
  NotificationChannel get WHATSAPP => _$WHATSAPP;
  NotificationChannel get SMS => _$SMS;
  NotificationChannel get EMAIL => _$EMAIL;
  NotificationChannel valueOf(String name) => _$valueOf(name);
  BuiltSet<NotificationChannel> get values => _$values;
}

abstract class _$NotificationChannelMixin {
  // ignore: non_constant_identifier_names
  _$NotificationChannelMeta get NotificationChannel =>
      const _$NotificationChannelMeta();
}

Serializer<NotificationChannel> _$notificationChannelSerializer =
    _$NotificationChannelSerializer();

class _$NotificationChannelSerializer
    implements PrimitiveSerializer<NotificationChannel> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PUSH': 'PUSH',
    'WHATSAPP': 'WHATSAPP',
    'SMS': 'SMS',
    'EMAIL': 'EMAIL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PUSH': 'PUSH',
    'WHATSAPP': 'WHATSAPP',
    'SMS': 'SMS',
    'EMAIL': 'EMAIL',
  };

  @override
  final Iterable<Type> types = const <Type>[NotificationChannel];
  @override
  final String wireName = 'NotificationChannel';

  @override
  Object serialize(Serializers serializers, NotificationChannel object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  NotificationChannel deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      NotificationChannel.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
