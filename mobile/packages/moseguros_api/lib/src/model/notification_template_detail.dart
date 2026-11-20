//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/notification_channel.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_template_detail.g.dart';

/// NotificationTemplateDetail
///
/// Properties:
/// * [active] 
/// * [body] 
/// * [channel] 
/// * [code] 
/// * [customized] 
/// * [defaultBody] 
/// * [defaultSubject] 
/// * [id] 
/// * [locale] 
/// * [sampleParams] 
/// * [subject] 
/// * [updatedAt] 
/// * [updatedBy] 
/// * [variables] 
/// * [version] 
/// * [whatsappParams] 
/// * [whatsappTemplateName] 
@BuiltValue()
abstract class NotificationTemplateDetail implements Built<NotificationTemplateDetail, NotificationTemplateDetailBuilder> {
  @BuiltValueField(wireName: r'active')
  bool get active;

  @BuiltValueField(wireName: r'body')
  String get body;

  @BuiltValueField(wireName: r'channel')
  NotificationChannel get channel;
  // enum channelEnum {  PUSH,  WHATSAPP,  SMS,  EMAIL,  };

  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'customized')
  bool get customized;

  @BuiltValueField(wireName: r'defaultBody')
  String? get defaultBody;

  @BuiltValueField(wireName: r'defaultSubject')
  String? get defaultSubject;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'locale')
  String get locale;

  @BuiltValueField(wireName: r'sampleParams')
  BuiltMap<String, String> get sampleParams;

  @BuiltValueField(wireName: r'subject')
  String? get subject;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'updatedBy')
  String get updatedBy;

  @BuiltValueField(wireName: r'variables')
  BuiltList<String> get variables;

  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'whatsappParams')
  BuiltList<String>? get whatsappParams;

  @BuiltValueField(wireName: r'whatsappTemplateName')
  String? get whatsappTemplateName;

  NotificationTemplateDetail._();

  factory NotificationTemplateDetail([void updates(NotificationTemplateDetailBuilder b)]) = _$NotificationTemplateDetail;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationTemplateDetailBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationTemplateDetail> get serializer => _$NotificationTemplateDetailSerializer();
}

class _$NotificationTemplateDetailSerializer implements PrimitiveSerializer<NotificationTemplateDetail> {
  @override
  final Iterable<Type> types = const [NotificationTemplateDetail, _$NotificationTemplateDetail];

  @override
  final String wireName = r'NotificationTemplateDetail';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationTemplateDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'active';
    yield serializers.serialize(
      object.active,
      specifiedType: const FullType(bool),
    );
    yield r'body';
    yield serializers.serialize(
      object.body,
      specifiedType: const FullType(String),
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
    if (object.defaultBody != null) {
      yield r'defaultBody';
      yield serializers.serialize(
        object.defaultBody,
        specifiedType: const FullType(String),
      );
    }
    if (object.defaultSubject != null) {
      yield r'defaultSubject';
      yield serializers.serialize(
        object.defaultSubject,
        specifiedType: const FullType(String),
      );
    }
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
    yield r'sampleParams';
    yield serializers.serialize(
      object.sampleParams,
      specifiedType: const FullType(BuiltMap, [FullType(String), FullType(String)]),
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
    yield r'variables';
    yield serializers.serialize(
      object.variables,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
    if (object.whatsappParams != null) {
      yield r'whatsappParams';
      yield serializers.serialize(
        object.whatsappParams,
        specifiedType: const FullType(BuiltList, [FullType(String)]),
      );
    }
    if (object.whatsappTemplateName != null) {
      yield r'whatsappTemplateName';
      yield serializers.serialize(
        object.whatsappTemplateName,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationTemplateDetail object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationTemplateDetailBuilder result,
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
        case r'body':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.body = valueDes;
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
        case r'defaultBody':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.defaultBody = valueDes;
          break;
        case r'defaultSubject':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.defaultSubject = valueDes;
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
        case r'sampleParams':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(String)]),
          ) as BuiltMap<String, String>;
          result.sampleParams.replace(valueDes);
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
        case r'variables':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.variables.replace(valueDes);
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        case r'whatsappParams':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.whatsappParams.replace(valueDes);
          break;
        case r'whatsappTemplateName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.whatsappTemplateName = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationTemplateDetail deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationTemplateDetailBuilder();
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

