//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/declaration_answer_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'save_declarations_request.g.dart';

/// SaveDeclarationsRequest
///
/// Properties:
/// * [answers] - Uma resposta por pergunta (as cinco de api/proposal-questions.json)
/// * [truthfulnessConfirmed] - Tem de ser true
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class SaveDeclarationsRequest implements Built<SaveDeclarationsRequest, SaveDeclarationsRequestBuilder> {
  /// Uma resposta por pergunta (as cinco de api/proposal-questions.json)
  @BuiltValueField(wireName: r'answers')
  BuiltList<DeclarationAnswerRequest>? get answers;

  /// Tem de ser true
  @BuiltValueField(wireName: r'truthfulnessConfirmed')
  bool get truthfulnessConfirmed;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  SaveDeclarationsRequest._();

  factory SaveDeclarationsRequest([void updates(SaveDeclarationsRequestBuilder b)]) = _$SaveDeclarationsRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SaveDeclarationsRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SaveDeclarationsRequest> get serializer => _$SaveDeclarationsRequestSerializer();
}

class _$SaveDeclarationsRequestSerializer implements PrimitiveSerializer<SaveDeclarationsRequest> {
  @override
  final Iterable<Type> types = const [SaveDeclarationsRequest, _$SaveDeclarationsRequest];

  @override
  final String wireName = r'SaveDeclarationsRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SaveDeclarationsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.answers != null) {
      yield r'answers';
      yield serializers.serialize(
        object.answers,
        specifiedType: const FullType(BuiltList, [FullType(DeclarationAnswerRequest)]),
      );
    }
    yield r'truthfulnessConfirmed';
    yield serializers.serialize(
      object.truthfulnessConfirmed,
      specifiedType: const FullType(bool),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SaveDeclarationsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SaveDeclarationsRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'answers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(DeclarationAnswerRequest)]),
          ) as BuiltList<DeclarationAnswerRequest>;
          result.answers.replace(valueDes);
          break;
        case r'truthfulnessConfirmed':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.truthfulnessConfirmed = valueDes;
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
  SaveDeclarationsRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SaveDeclarationsRequestBuilder();
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

