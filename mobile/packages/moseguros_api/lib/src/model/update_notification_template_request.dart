//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_notification_template_request.g.dart';

/// UpdateNotificationTemplateRequest
///
/// Properties:
/// * [active] 
/// * [body] 
/// * [subject] 
/// * [version] 
/// * [whatsappParams] 
/// * [whatsappTemplateName] 
@BuiltValue()
abstract class UpdateNotificationTemplateRequest implements Built<UpdateNotificationTemplateRequest, UpdateNotificationTemplateRequestBuilder> {
  @BuiltValueField(wireName: r'active')
  bool? get active;

  @BuiltValueField(wireName: r'body')
  String get body;

  @BuiltValueField(wireName: r'subject')
  String? get subject;

  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'whatsappParams')
  BuiltList<String>? get whatsappParams;

  @BuiltValueField(wireName: r'whatsappTemplateName')
  String? get whatsappTemplateName;

  UpdateNotificationTemplateRequest._();

  factory UpdateNotificationTemplateRequest([void updates(UpdateNotificationTemplateRequestBuilder b)]) = _$UpdateNotificationTemplateRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateNotificationTemplateRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateNotificationTemplateRequest> get serializer => _$UpdateNotificationTemplateRequestSerializer();
}

class _$UpdateNotificationTemplateRequestSerializer implements PrimitiveSerializer<UpdateNotificationTemplateRequest> {
  @override
  final Iterable<Type> types = const [UpdateNotificationTemplateRequest, _$UpdateNotificationTemplateRequest];

  @override
  final String wireName = r'UpdateNotificationTemplateRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.active != null) {
      yield r'active';
      yield serializers.serialize(
        object.active,
        specifiedType: const FullType(bool),
      );
    }
    yield r'body';
    yield serializers.serialize(
      object.body,
      specifiedType: const FullType(String),
    );
    if (object.subject != null) {
      yield r'subject';
      yield serializers.serialize(
        object.subject,
        specifiedType: const FullType(String),
      );
    }
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
    UpdateNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateNotificationTemplateRequestBuilder result,
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
        case r'subject':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.subject = valueDes;
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
  UpdateNotificationTemplateRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateNotificationTemplateRequestBuilder();
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

