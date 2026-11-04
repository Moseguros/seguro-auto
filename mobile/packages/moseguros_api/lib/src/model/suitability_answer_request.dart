//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suitability_answer_request.g.dart';

/// SuitabilityAnswerRequest
///
/// Properties:
/// * [answer] - Uma das options da pergunta
/// * [questionCode] - Código de api/proposal-questions.json (suitability)
@BuiltValue()
abstract class SuitabilityAnswerRequest implements Built<SuitabilityAnswerRequest, SuitabilityAnswerRequestBuilder> {
  /// Uma das options da pergunta
  @BuiltValueField(wireName: r'answer')
  String get answer;

  /// Código de api/proposal-questions.json (suitability)
  @BuiltValueField(wireName: r'questionCode')
  String get questionCode;

  SuitabilityAnswerRequest._();

  factory SuitabilityAnswerRequest([void updates(SuitabilityAnswerRequestBuilder b)]) = _$SuitabilityAnswerRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SuitabilityAnswerRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SuitabilityAnswerRequest> get serializer => _$SuitabilityAnswerRequestSerializer();
}

class _$SuitabilityAnswerRequestSerializer implements PrimitiveSerializer<SuitabilityAnswerRequest> {
  @override
  final Iterable<Type> types = const [SuitabilityAnswerRequest, _$SuitabilityAnswerRequest];

  @override
  final String wireName = r'SuitabilityAnswerRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SuitabilityAnswerRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'answer';
    yield serializers.serialize(
      object.answer,
      specifiedType: const FullType(String),
    );
    yield r'questionCode';
    yield serializers.serialize(
      object.questionCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SuitabilityAnswerRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SuitabilityAnswerRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'answer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.answer = valueDes;
          break;
        case r'questionCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.questionCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SuitabilityAnswerRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SuitabilityAnswerRequestBuilder();
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

