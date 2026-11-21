//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'notification_template_test_result.g.dart';

/// NotificationTemplateTestResult
///
/// Properties:
/// * [reason] 
/// * [status] 
@BuiltValue()
abstract class NotificationTemplateTestResult implements Built<NotificationTemplateTestResult, NotificationTemplateTestResultBuilder> {
  @BuiltValueField(wireName: r'reason')
  String? get reason;

  @BuiltValueField(wireName: r'status')
  String get status;

  NotificationTemplateTestResult._();

  factory NotificationTemplateTestResult([void updates(NotificationTemplateTestResultBuilder b)]) = _$NotificationTemplateTestResult;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(NotificationTemplateTestResultBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<NotificationTemplateTestResult> get serializer => _$NotificationTemplateTestResultSerializer();
}

class _$NotificationTemplateTestResultSerializer implements PrimitiveSerializer<NotificationTemplateTestResult> {
  @override
  final Iterable<Type> types = const [NotificationTemplateTestResult, _$NotificationTemplateTestResult];

  @override
  final String wireName = r'NotificationTemplateTestResult';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    NotificationTemplateTestResult object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.reason != null) {
      yield r'reason';
      yield serializers.serialize(
        object.reason,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    NotificationTemplateTestResult object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required NotificationTemplateTestResultBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reason = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.status = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  NotificationTemplateTestResult deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = NotificationTemplateTestResultBuilder();
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

