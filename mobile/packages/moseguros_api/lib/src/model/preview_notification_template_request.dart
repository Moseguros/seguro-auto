//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'preview_notification_template_request.g.dart';

/// PreviewNotificationTemplateRequest
///
/// Properties:
/// * [body] 
/// * [params] 
/// * [subject] 
@BuiltValue()
abstract class PreviewNotificationTemplateRequest implements Built<PreviewNotificationTemplateRequest, PreviewNotificationTemplateRequestBuilder> {
  @BuiltValueField(wireName: r'body')
  String get body;

  @BuiltValueField(wireName: r'params')
  BuiltMap<String, String>? get params;

  @BuiltValueField(wireName: r'subject')
  String? get subject;

  PreviewNotificationTemplateRequest._();

  factory PreviewNotificationTemplateRequest([void updates(PreviewNotificationTemplateRequestBuilder b)]) = _$PreviewNotificationTemplateRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PreviewNotificationTemplateRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PreviewNotificationTemplateRequest> get serializer => _$PreviewNotificationTemplateRequestSerializer();
}

class _$PreviewNotificationTemplateRequestSerializer implements PrimitiveSerializer<PreviewNotificationTemplateRequest> {
  @override
  final Iterable<Type> types = const [PreviewNotificationTemplateRequest, _$PreviewNotificationTemplateRequest];

  @override
  final String wireName = r'PreviewNotificationTemplateRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PreviewNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'body';
    yield serializers.serialize(
      object.body,
      specifiedType: const FullType(String),
    );
    if (object.params != null) {
      yield r'params';
      yield serializers.serialize(
        object.params,
        specifiedType: const FullType(BuiltMap, [FullType(String), FullType(String)]),
      );
    }
    if (object.subject != null) {
      yield r'subject';
      yield serializers.serialize(
        object.subject,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PreviewNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PreviewNotificationTemplateRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'body':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.body = valueDes;
          break;
        case r'params':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(String)]),
          ) as BuiltMap<String, String>;
          result.params.replace(valueDes);
          break;
        case r'subject':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.subject = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PreviewNotificationTemplateRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PreviewNotificationTemplateRequestBuilder();
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

