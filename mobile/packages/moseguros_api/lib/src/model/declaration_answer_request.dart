//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'declaration_answer_request.g.dart';

/// DeclarationAnswerRequest
///
/// Properties:
/// * [answer] 
/// * [claimsCount] - 1–20; só em CLAIMS_LAST_3_YEARS com resposta sim
/// * [details] - 10–500 caracteres; obrigatório com resposta sim, proibido com não
/// * [questionCode] - Código de api/proposal-questions.json (declarations)
@BuiltValue()
abstract class DeclarationAnswerRequest implements Built<DeclarationAnswerRequest, DeclarationAnswerRequestBuilder> {
  @BuiltValueField(wireName: r'answer')
  bool get answer;

  /// 1–20; só em CLAIMS_LAST_3_YEARS com resposta sim
  @BuiltValueField(wireName: r'claimsCount')
  int? get claimsCount;

  /// 10–500 caracteres; obrigatório com resposta sim, proibido com não
  @BuiltValueField(wireName: r'details')
  String? get details;

  /// Código de api/proposal-questions.json (declarations)
  @BuiltValueField(wireName: r'questionCode')
  String get questionCode;

  DeclarationAnswerRequest._();

  factory DeclarationAnswerRequest([void updates(DeclarationAnswerRequestBuilder b)]) = _$DeclarationAnswerRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DeclarationAnswerRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DeclarationAnswerRequest> get serializer => _$DeclarationAnswerRequestSerializer();
}

class _$DeclarationAnswerRequestSerializer implements PrimitiveSerializer<DeclarationAnswerRequest> {
  @override
  final Iterable<Type> types = const [DeclarationAnswerRequest, _$DeclarationAnswerRequest];

  @override
  final String wireName = r'DeclarationAnswerRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DeclarationAnswerRequest object, {
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
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    DeclarationAnswerRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DeclarationAnswerRequestBuilder result,
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
  DeclarationAnswerRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DeclarationAnswerRequestBuilder();
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

