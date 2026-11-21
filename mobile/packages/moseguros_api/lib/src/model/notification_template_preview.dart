//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_template_preview.g.dart';

/// NotificationTemplatePreview
///
/// Properties:
/// * [body] 
/// * [fits] 
/// * [length] 
/// * [limit] 
/// * [smsText] 
/// * [subject] 
@BuiltValue()
abstract class NotificationTemplatePreview implements Built<NotificationTemplatePreview, NotificationTemplatePreviewBuilder> {
  @BuiltValueField(wireName: r'body')
  String? get body;

  @BuiltValueField(wireName: r'fits')
  bool? get fits;

  @BuiltValueField(wireName: r'length')
  int? get length;

  @BuiltValueField(wireName: r'limit')
  int? get limit;

  @BuiltValueField(wireName: r'smsText')
  String? get smsText;

  @BuiltValueField(wireName: r'subject')
  String? get subject;

  NotificationTemplatePreview._();

  factory NotificationTemplatePreview([void updates(NotificationTemplatePreviewBuilder b)]) = _$NotificationTemplatePreview;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationTemplatePreviewBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationTemplatePreview> get serializer => _$NotificationTemplatePreviewSerializer();
}

class _$NotificationTemplatePreviewSerializer implements PrimitiveSerializer<NotificationTemplatePreview> {
  @override
  final Iterable<Type> types = const [NotificationTemplatePreview, _$NotificationTemplatePreview];

  @override
  final String wireName = r'NotificationTemplatePreview';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationTemplatePreview object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.body != null) {
      yield r'body';
      yield serializers.serialize(
        object.body,
        specifiedType: const FullType(String),
      );
    }
    if (object.fits != null) {
      yield r'fits';
      yield serializers.serialize(
        object.fits,
        specifiedType: const FullType(bool),
      );
    }
    if (object.length != null) {
      yield r'length';
      yield serializers.serialize(
        object.length,
        specifiedType: const FullType(int),
      );
    }
    if (object.limit != null) {
      yield r'limit';
      yield serializers.serialize(
        object.limit,
        specifiedType: const FullType(int),
      );
    }
    if (object.smsText != null) {
      yield r'smsText';
      yield serializers.serialize(
        object.smsText,
        specifiedType: const FullType(String),
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
    NotificationTemplatePreview object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationTemplatePreviewBuilder result,
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
        case r'fits':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.fits = valueDes;
          break;
        case r'length':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.length = valueDes;
          break;
        case r'limit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.limit = valueDes;
          break;
        case r'smsText':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.smsText = valueDes;
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
  NotificationTemplatePreview deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationTemplatePreviewBuilder();
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

