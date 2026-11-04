//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/suitability_answer_request.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'save_suitability_request.g.dart';

/// SaveSuitabilityRequest
///
/// Properties:
/// * [answers] - Uma resposta por pergunta (as cinco de api/proposal-questions.json)
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class SaveSuitabilityRequest implements Built<SaveSuitabilityRequest, SaveSuitabilityRequestBuilder> {
  /// Uma resposta por pergunta (as cinco de api/proposal-questions.json)
  @BuiltValueField(wireName: r'answers')
  BuiltList<SuitabilityAnswerRequest>? get answers;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  SaveSuitabilityRequest._();

  factory SaveSuitabilityRequest([void updates(SaveSuitabilityRequestBuilder b)]) = _$SaveSuitabilityRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SaveSuitabilityRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SaveSuitabilityRequest> get serializer => _$SaveSuitabilityRequestSerializer();
}

class _$SaveSuitabilityRequestSerializer implements PrimitiveSerializer<SaveSuitabilityRequest> {
  @override
  final Iterable<Type> types = const [SaveSuitabilityRequest, _$SaveSuitabilityRequest];

  @override
  final String wireName = r'SaveSuitabilityRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SaveSuitabilityRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.answers != null) {
      yield r'answers';
      yield serializers.serialize(
        object.answers,
        specifiedType: const FullType(BuiltList, [FullType(SuitabilityAnswerRequest)]),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SaveSuitabilityRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SaveSuitabilityRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'answers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(SuitabilityAnswerRequest)]),
          ) as BuiltList<SuitabilityAnswerRequest>;
          result.answers.replace(valueDes);
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SaveSuitabilityRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SaveSuitabilityRequestBuilder();
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

