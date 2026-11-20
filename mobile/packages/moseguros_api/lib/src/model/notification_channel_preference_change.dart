//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/notification_channel.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_channel_preference_change.g.dart';

/// NotificationChannelPreferenceChange
///
/// Properties:
/// * [channel] 
/// * [enabled] 
@BuiltValue()
abstract class NotificationChannelPreferenceChange implements Built<NotificationChannelPreferenceChange, NotificationChannelPreferenceChangeBuilder> {
  @BuiltValueField(wireName: r'channel')
  NotificationChannel get channel;
  // enum channelEnum {  PUSH,  WHATSAPP,  SMS,  EMAIL,  };

  @BuiltValueField(wireName: r'enabled')
  bool get enabled;

  NotificationChannelPreferenceChange._();

  factory NotificationChannelPreferenceChange([void updates(NotificationChannelPreferenceChangeBuilder b)]) = _$NotificationChannelPreferenceChange;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationChannelPreferenceChangeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationChannelPreferenceChange> get serializer => _$NotificationChannelPreferenceChangeSerializer();
}

class _$NotificationChannelPreferenceChangeSerializer implements PrimitiveSerializer<NotificationChannelPreferenceChange> {
  @override
  final Iterable<Type> types = const [NotificationChannelPreferenceChange, _$NotificationChannelPreferenceChange];

  @override
  final String wireName = r'NotificationChannelPreferenceChange';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationChannelPreferenceChange object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    NotificationChannelPreferenceChange object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationChannelPreferenceChangeBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  NotificationChannelPreferenceChange deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationChannelPreferenceChangeBuilder();
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

