//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/notification_channel.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_template_summary.g.dart';

/// NotificationTemplateSummary
///
/// Properties:
/// * [active] 
/// * [channel] 
/// * [code] 
/// * [customized] 
/// * [id] 
/// * [locale] 
/// * [subject] 
/// * [updatedAt] 
/// * [updatedBy] 
@BuiltValue()
abstract class NotificationTemplateSummary implements Built<NotificationTemplateSummary, NotificationTemplateSummaryBuilder> {
  @BuiltValueField(wireName: r'active')
  bool get active;

  @BuiltValueField(wireName: r'channel')
  NotificationChannel get channel;
  // enum channelEnum {  PUSH,  WHATSAPP,  SMS,  EMAIL,  };

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'customized')
  bool get customized;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'locale')
  String get locale;

  @BuiltValueField(wireName: r'subject')
  String? get subject;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'updatedBy')
  String get updatedBy;

  NotificationTemplateSummary._();

  factory NotificationTemplateSummary([void updates(NotificationTemplateSummaryBuilder b)]) = _$NotificationTemplateSummary;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationTemplateSummaryBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationTemplateSummary> get serializer => _$NotificationTemplateSummarySerializer();
}

class _$NotificationTemplateSummarySerializer implements PrimitiveSerializer<NotificationTemplateSummary> {
  @override
  final Iterable<Type> types = const [NotificationTemplateSummary, _$NotificationTemplateSummary];

  @override
  final String wireName = r'NotificationTemplateSummary';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationTemplateSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'active';
    yield serializers.serialize(
      object.active,
      specifiedType: const FullType(bool),
    );
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(NotificationChannel),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'customized';
    yield serializers.serialize(
      object.customized,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'locale';
    yield serializers.serialize(
      object.locale,
      specifiedType: const FullType(String),
    );
    if (object.subject != null) {
      yield r'subject';
      yield serializers.serialize(
        object.subject,
        specifiedType: const FullType(String),
      );
    }
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'updatedBy';
    yield serializers.serialize(
      object.updatedBy,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationTemplateSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationTemplateSummaryBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'active':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.active = valueDes;
          break;
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(NotificationChannel),
          ) as NotificationChannel;
          result.channel = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'customized':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.customized = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'locale':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.locale = valueDes;
          break;
        case r'subject':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.subject = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'updatedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.updatedBy = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationTemplateSummary deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationTemplateSummaryBuilder();
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

