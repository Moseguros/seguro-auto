//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/declaration_question.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'declaration_answer_response.g.dart';

/// DeclarationAnswerResponse
///
/// Properties:
/// * [answer] 
/// * [claimsCount] - Só em CLAIMS_LAST_3_YEARS com resposta sim
/// * [details] - Só com resposta sim
/// * [questionCode] 
@BuiltValue()
abstract class DeclarationAnswerResponse implements Built<DeclarationAnswerResponse, DeclarationAnswerResponseBuilder> {
  @BuiltValueField(wireName: r'answer')
  bool get answer;

  /// Só em CLAIMS_LAST_3_YEARS com resposta sim
  @BuiltValueField(wireName: r'claimsCount')
  int? get claimsCount;

  /// Só com resposta sim
  @BuiltValueField(wireName: r'details')
  String? get details;

  @BuiltValueField(wireName: r'questionCode')
  DeclarationQuestion get questionCode;
  // enum questionCodeEnum {  CLAIMS_LAST_3_YEARS,  PREVIOUS_REFUSAL,  USAGE_DIFFERENT,  VEHICLE_MODIFIED,  NOVICE_DRIVERS,  };

  DeclarationAnswerResponse._();

  factory DeclarationAnswerResponse([void updates(DeclarationAnswerResponseBuilder b)]) = _$DeclarationAnswerResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DeclarationAnswerResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DeclarationAnswerResponse> get serializer => _$DeclarationAnswerResponseSerializer();
}

class _$DeclarationAnswerResponseSerializer implements PrimitiveSerializer<DeclarationAnswerResponse> {
  @override
  final Iterable<Type> types = const [DeclarationAnswerResponse, _$DeclarationAnswerResponse];

  @override
  final String wireName = r'DeclarationAnswerResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DeclarationAnswerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'answer';
    yield serializers.serialize(
      object.answer,
      specifiedType: const FullType(bool),
    );
    if (object.claimsCount != null) {
      yield r'claimsCount';
      yield serializers.serialize(
        object.claimsCount,
        specifiedType: const FullType(int),
      );
    }
    if (object.details != null) {
      yield r'details';
      yield serializers.serialize(
        object.details,
        specifiedType: const FullType(String),
      );
    }
    yield r'questionCode';
    yield serializers.serialize(
      object.questionCode,
      specifiedType: const FullType(DeclarationQuestion),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    DeclarationAnswerResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DeclarationAnswerResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'answer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.answer = valueDes;
          break;
        case r'claimsCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.claimsCount = valueDes;
          break;
        case r'details':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.details = valueDes;
          break;
        case r'questionCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DeclarationQuestion),
          ) as DeclarationQuestion;
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
  DeclarationAnswerResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DeclarationAnswerResponseBuilder();
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

