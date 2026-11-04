//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/suitability_result.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/suitability_answer_response.dart';
import 'package:moseguros_api/src/model/suitability_reason.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_suitability.g.dart';

/// null até ser respondido
///
/// Properties:
/// * [acknowledgedAt] - Confirmação consciente dos avisos (só em WARNED)
/// * [answeredAt] 
/// * [answers] 
/// * [reasons] - Vazia em SUITABLE
/// * [result] 
@BuiltValue()
abstract class ProposalSuitability implements Built<ProposalSuitability, ProposalSuitabilityBuilder> {
  /// Confirmação consciente dos avisos (só em WARNED)
  @BuiltValueField(wireName: r'acknowledgedAt')
  DateTime? get acknowledgedAt;

  @BuiltValueField(wireName: r'answeredAt')
  DateTime get answeredAt;

  @BuiltValueField(wireName: r'answers')
  BuiltList<SuitabilityAnswerResponse> get answers;

  /// Vazia em SUITABLE
  @BuiltValueField(wireName: r'reasons')
  BuiltList<SuitabilityReason> get reasons;

  @BuiltValueField(wireName: r'result')
  SuitabilityResult get result;
  // enum resultEnum {  SUITABLE,  WARNED,  };

  ProposalSuitability._();

  factory ProposalSuitability([void updates(ProposalSuitabilityBuilder b)]) = _$ProposalSuitability;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalSuitabilityBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalSuitability> get serializer => _$ProposalSuitabilitySerializer();
}

class _$ProposalSuitabilitySerializer implements PrimitiveSerializer<ProposalSuitability> {
  @override
  final Iterable<Type> types = const [ProposalSuitability, _$ProposalSuitability];

  @override
  final String wireName = r'ProposalSuitability';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalSuitability object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.acknowledgedAt != null) {
      yield r'acknowledgedAt';
      yield serializers.serialize(
        object.acknowledgedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'answeredAt';
    yield serializers.serialize(
      object.answeredAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'answers';
    yield serializers.serialize(
      object.answers,
      specifiedType: const FullType(BuiltList, [FullType(SuitabilityAnswerResponse)]),
    );
    yield r'reasons';
    yield serializers.serialize(
      object.reasons,
      specifiedType: const FullType(BuiltList, [FullType(SuitabilityReason)]),
    );
    yield r'result';
    yield serializers.serialize(
      object.result,
      specifiedType: const FullType(SuitabilityResult),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalSuitability object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalSuitabilityBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'acknowledgedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.acknowledgedAt = valueDes;
          break;
        case r'answeredAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.answeredAt = valueDes;
          break;
        case r'answers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(SuitabilityAnswerResponse)]),
          ) as BuiltList<SuitabilityAnswerResponse>;
          result.answers.replace(valueDes);
          break;
        case r'reasons':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(SuitabilityReason)]),
          ) as BuiltList<SuitabilityReason>;
          result.reasons.replace(valueDes);
          break;
        case r'result':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SuitabilityResult),
          ) as SuitabilityResult;
          result.result = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalSuitability deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalSuitabilityBuilder();
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

