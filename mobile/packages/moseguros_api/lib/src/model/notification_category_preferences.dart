//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/notification_category.dart';
import 'package:moseguros_api/src/model/notification_channel_preference.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_category_preferences.g.dart';

/// NotificationCategoryPreferences
///
/// Properties:
/// * [category] 
/// * [channels] 
/// * [mandatory] 
@BuiltValue()
abstract class NotificationCategoryPreferences implements Built<NotificationCategoryPreferences, NotificationCategoryPreferencesBuilder> {
  @BuiltValueField(wireName: r'category')
  NotificationCategory get category;
  // enum categoryEnum {  PAYMENT,  POLICY,  LEGAL,  PROPOSAL,  QUOTE,  };

  @BuiltValueField(wireName: r'channels')
  BuiltList<NotificationChannelPreference> get channels;

  @BuiltValueField(wireName: r'mandatory')
  bool get mandatory;

  NotificationCategoryPreferences._();

  factory NotificationCategoryPreferences([void updates(NotificationCategoryPreferencesBuilder b)]) = _$NotificationCategoryPreferences;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationCategoryPreferencesBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationCategoryPreferences> get serializer => _$NotificationCategoryPreferencesSerializer();
}

class _$NotificationCategoryPreferencesSerializer implements PrimitiveSerializer<NotificationCategoryPreferences> {
  @override
  final Iterable<Type> types = const [NotificationCategoryPreferences, _$NotificationCategoryPreferences];

  @override
  final String wireName = r'NotificationCategoryPreferences';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationCategoryPreferences object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(NotificationCategory),
    );
    yield r'channels';
    yield serializers.serialize(
      object.channels,
      specifiedType: const FullType(BuiltList, [FullType(NotificationChannelPreference)]),
    );
    yield r'mandatory';
    yield serializers.serialize(
      object.mandatory,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationCategoryPreferences object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationCategoryPreferencesBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(NotificationCategory),
          ) as NotificationCategory;
          result.category = valueDes;
          break;
        case r'channels':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(NotificationChannelPreference)]),
          ) as BuiltList<NotificationChannelPreference>;
          result.channels.replace(valueDes);
          break;
        case r'mandatory':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.mandatory = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationCategoryPreferences deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationCategoryPreferencesBuilder();
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

