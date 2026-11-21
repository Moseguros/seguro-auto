//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/notification_channel.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_channel_preference.g.dart';

/// NotificationChannelPreference
///
/// Properties:
/// * [available] 
/// * [channel] 
/// * [enabled] 
@BuiltValue()
abstract class NotificationChannelPreference implements Built<NotificationChannelPreference, NotificationChannelPreferenceBuilder> {
  @BuiltValueField(wireName: r'available')
  bool get available;

  @BuiltValueField(wireName: r'channel')
  NotificationChannel get channel;
  // enum channelEnum {  PUSH,  WHATSAPP,  SMS,  EMAIL,  };

  @BuiltValueField(wireName: r'enabled')
  bool get enabled;

  NotificationChannelPreference._();

  factory NotificationChannelPreference([void updates(NotificationChannelPreferenceBuilder b)]) = _$NotificationChannelPreference;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationChannelPreferenceBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationChannelPreference> get serializer => _$NotificationChannelPreferenceSerializer();
}

class _$NotificationChannelPreferenceSerializer implements PrimitiveSerializer<NotificationChannelPreference> {
  @override
  final Iterable<Type> types = const [NotificationChannelPreference, _$NotificationChannelPreference];

  @override
  final String wireName = r'NotificationChannelPreference';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationChannelPreference object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'available';
    yield serializers.serialize(
      object.available,
      specifiedType: const FullType(bool),
    );
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(NotificationChannel),
    );
    yield r'enabled';
    yield serializers.serialize(
      object.enabled,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationChannelPreference object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationChannelPreferenceBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'available':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.available = valueDes;
          break;
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(NotificationChannel),
          ) as NotificationChannel;
          result.channel = valueDes;
          break;
        case r'enabled':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.enabled = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationChannelPreference deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationChannelPreferenceBuilder();
    final serializedList = (serialized as Iterable<Object?>).toList();
    final unhandled = <Object?>[];
    _deserializeProperties(
      serializers,
      serialized,
      specifiedType: specifiedType,
      serializedList: serializedList,
      unhandled: unhandled,
      result: result,
    );
    return result.build();
  }
}

