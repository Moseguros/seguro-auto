//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'test_notification_template_request.g.dart';

/// TestNotificationTemplateRequest
///
/// Properties:
/// * [destination] 
@BuiltValue()
abstract class TestNotificationTemplateRequest implements Built<TestNotificationTemplateRequest, TestNotificationTemplateRequestBuilder> {
  @BuiltValueField(wireName: r'destination')
  String get destination;

  TestNotificationTemplateRequest._();

  factory TestNotificationTemplateRequest([void updates(TestNotificationTemplateRequestBuilder b)]) = _$TestNotificationTemplateRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TestNotificationTemplateRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TestNotificationTemplateRequest> get serializer => _$TestNotificationTemplateRequestSerializer();
}

class _$TestNotificationTemplateRequestSerializer implements PrimitiveSerializer<TestNotificationTemplateRequest> {
  @override
  final Iterable<Type> types = const [TestNotificationTemplateRequest, _$TestNotificationTemplateRequest];

  @override
  final String wireName = r'TestNotificationTemplateRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TestNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'destination';
    yield serializers.serialize(
      object.destination,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TestNotificationTemplateRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TestNotificationTemplateRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'destination':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.destination = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TestNotificationTemplateRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TestNotificationTemplateRequestBuilder();
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

