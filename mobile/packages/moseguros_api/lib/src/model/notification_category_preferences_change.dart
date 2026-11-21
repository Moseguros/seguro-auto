//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/notification_channel_preference_change.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/notification_category.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_category_preferences_change.g.dart';

/// NotificationCategoryPreferencesChange
///
/// Properties:
/// * [category] 
/// * [channels] 
@BuiltValue()
abstract class NotificationCategoryPreferencesChange implements Built<NotificationCategoryPreferencesChange, NotificationCategoryPreferencesChangeBuilder> {
  @BuiltValueField(wireName: r'category')
  NotificationCategory get category;
  // enum categoryEnum {  PAYMENT,  POLICY,  LEGAL,  PROPOSAL,  QUOTE,  };

  @BuiltValueField(wireName: r'channels')
  BuiltList<NotificationChannelPreferenceChange> get channels;

  NotificationCategoryPreferencesChange._();

  factory NotificationCategoryPreferencesChange([void updates(NotificationCategoryPreferencesChangeBuilder b)]) = _$NotificationCategoryPreferencesChange;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationCategoryPreferencesChangeBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationCategoryPreferencesChange> get serializer => _$NotificationCategoryPreferencesChangeSerializer();
}

class _$NotificationCategoryPreferencesChangeSerializer implements PrimitiveSerializer<NotificationCategoryPreferencesChange> {
  @override
  final Iterable<Type> types = const [NotificationCategoryPreferencesChange, _$NotificationCategoryPreferencesChange];

  @override
  final String wireName = r'NotificationCategoryPreferencesChange';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationCategoryPreferencesChange object, {
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
      specifiedType: const FullType(BuiltList, [FullType(NotificationChannelPreferenceChange)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationCategoryPreferencesChange object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationCategoryPreferencesChangeBuilder result,
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
            specifiedType: const FullType(BuiltList, [FullType(NotificationChannelPreferenceChange)]),
          ) as BuiltList<NotificationChannelPreferenceChange>;
          result.channels.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationCategoryPreferencesChange deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationCategoryPreferencesChangeBuilder();
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

