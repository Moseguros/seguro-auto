//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/notification_category_preferences_change.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_notification_preferences_request.g.dart';

/// UpdateNotificationPreferencesRequest
///
/// Properties:
/// * [categories] 
@BuiltValue()
abstract class UpdateNotificationPreferencesRequest implements Built<UpdateNotificationPreferencesRequest, UpdateNotificationPreferencesRequestBuilder> {
  @BuiltValueField(wireName: r'categories')
  BuiltList<NotificationCategoryPreferencesChange> get categories;

  UpdateNotificationPreferencesRequest._();

  factory UpdateNotificationPreferencesRequest([void updates(UpdateNotificationPreferencesRequestBuilder b)]) = _$UpdateNotificationPreferencesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateNotificationPreferencesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateNotificationPreferencesRequest> get serializer => _$UpdateNotificationPreferencesRequestSerializer();
}

class _$UpdateNotificationPreferencesRequestSerializer implements PrimitiveSerializer<UpdateNotificationPreferencesRequest> {
  @override
  final Iterable<Type> types = const [UpdateNotificationPreferencesRequest, _$UpdateNotificationPreferencesRequest];

  @override
  final String wireName = r'UpdateNotificationPreferencesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateNotificationPreferencesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'categories';
    yield serializers.serialize(
      object.categories,
      specifiedType: const FullType(BuiltList, [FullType(NotificationCategoryPreferencesChange)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateNotificationPreferencesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateNotificationPreferencesRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'categories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(NotificationCategoryPreferencesChange)]),
          ) as BuiltList<NotificationCategoryPreferencesChange>;
          result.categories.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateNotificationPreferencesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateNotificationPreferencesRequestBuilder();
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

