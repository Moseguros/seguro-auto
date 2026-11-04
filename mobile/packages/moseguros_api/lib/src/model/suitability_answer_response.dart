//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/suitability_question.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'suitability_answer_response.g.dart';

/// SuitabilityAnswerResponse
///
/// Properties:
/// * [answer] 
/// * [questionCode] 
@BuiltValue()
abstract class SuitabilityAnswerResponse implements Built<SuitabilityAnswerResponse, SuitabilityAnswerResponseBuilder> {
  @BuiltValueField(wireName: r'answer')
  String get answer;

  @BuiltValueField(wireName: r'questionCode')
  SuitabilityQuestion get questionCode;
  // enum questionCodeEnum {  SUIT_NEED,  SUIT_FINANCED,  SUIT_CAN_AFFORD_REPAIR,  SUIT_CROSS_BORDER,  SUIT_DEDUCTIBLE,  };

  SuitabilityAnswerResponse._();

  factory SuitabilityAnswerResponse([void updates(SuitabilityAnswerResponseBuilder b)]) = _$SuitabilityAnswerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SuitabilityAnswerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SuitabilityAnswerResponse> get serializer => _$SuitabilityAnswerResponseSerializer();
}

class _$SuitabilityAnswerResponseSerializer implements PrimitiveSerializer<SuitabilityAnswerResponse> {
  @override
  final Iterable<Type> types = const [SuitabilityAnswerResponse, _$SuitabilityAnswerResponse];

  @override
  final String wireName = r'SuitabilityAnswerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SuitabilityAnswerResponse object, {
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
      specifiedType: const FullType(SuitabilityQuestion),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SuitabilityAnswerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SuitabilityAnswerResponseBuilder result,
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
            specifiedType: const FullType(SuitabilityQuestion),
          ) as SuitabilityQuestion;
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
  SuitabilityAnswerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SuitabilityAnswerResponseBuilder();
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

