//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/declaration_answer_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_declarations.g.dart';

/// null até serem gravadas
///
/// Properties:
/// * [answers] 
/// * [confirmedAt] 
@BuiltValue()
abstract class ProposalDeclarations implements Built<ProposalDeclarations, ProposalDeclarationsBuilder> {
  @BuiltValueField(wireName: r'answers')
  BuiltList<DeclarationAnswerResponse> get answers;

  @BuiltValueField(wireName: r'confirmedAt')
  DateTime get confirmedAt;

  ProposalDeclarations._();

  factory ProposalDeclarations([void updates(ProposalDeclarationsBuilder b)]) = _$ProposalDeclarations;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalDeclarationsBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalDeclarations> get serializer => _$ProposalDeclarationsSerializer();
}

class _$ProposalDeclarationsSerializer implements PrimitiveSerializer<ProposalDeclarations> {
  @override
  final Iterable<Type> types = const [ProposalDeclarations, _$ProposalDeclarations];

  @override
  final String wireName = r'ProposalDeclarations';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalDeclarations object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'answers';
    yield serializers.serialize(
      object.answers,
      specifiedType: const FullType(BuiltList, [FullType(DeclarationAnswerResponse)]),
    );
    yield r'confirmedAt';
    yield serializers.serialize(
      object.confirmedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalDeclarations object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalDeclarationsBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'answers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(DeclarationAnswerResponse)]),
          ) as BuiltList<DeclarationAnswerResponse>;
          result.answers.replace(valueDes);
          break;
        case r'confirmedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.confirmedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalDeclarations deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalDeclarationsBuilder();
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

