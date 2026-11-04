//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/acceptance_mode.dart';
import 'package:moseguros_api/src/model/proposal_info_request.dart';
import 'package:moseguros_api/src/model/rejection_reason_code.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_decision.g.dart';

/// Decisão da seguradora (S4-05); null em DRAFT antes da primeira submissão
///
/// Properties:
/// * [decidedAt] 
/// * [infoRequest] 
/// * [insurerDueAt] - Prazo de decisão da seguradora (sla_due_at), só em UNDER_REVIEW
/// * [mode] 
/// * [reason] - Motivo da recusa ou nota da aceitação
/// * [rejectionReasonCode] 
@BuiltValue()
abstract class ProposalDecision implements Built<ProposalDecision, ProposalDecisionBuilder> {
  @BuiltValueField(wireName: r'decidedAt')
  DateTime? get decidedAt;

  @BuiltValueField(wireName: r'infoRequest')
  ProposalInfoRequest? get infoRequest;

  /// Prazo de decisão da seguradora (sla_due_at), só em UNDER_REVIEW
  @BuiltValueField(wireName: r'insurerDueAt')
  DateTime? get insurerDueAt;

  @BuiltValueField(wireName: r'mode')
  AcceptanceMode? get mode;
  // enum modeEnum {  AUTOMATIC,  MANUAL,  };

  /// Motivo da recusa ou nota da aceitação
  @BuiltValueField(wireName: r'reason')
  String? get reason;

  @BuiltValueField(wireName: r'rejectionReasonCode')
  RejectionReasonCode? get rejectionReasonCode;
  // enum rejectionReasonCodeEnum {  RISK_NOT_ACCEPTED,  KYC_FAILED,  INSPECTION_FAILED,  FALSE_DECLARATION,  DOCUMENTS_INVALID,  OTHER,  };

  ProposalDecision._();

  factory ProposalDecision([void updates(ProposalDecisionBuilder b)]) = _$ProposalDecision;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalDecisionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalDecision> get serializer => _$ProposalDecisionSerializer();
}

class _$ProposalDecisionSerializer implements PrimitiveSerializer<ProposalDecision> {
  @override
  final Iterable<Type> types = const [ProposalDecision, _$ProposalDecision];

  @override
  final String wireName = r'ProposalDecision';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalDecision object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.decidedAt != null) {
      yield r'decidedAt';
      yield serializers.serialize(
        object.decidedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.infoRequest != null) {
      yield r'infoRequest';
      yield serializers.serialize(
        object.infoRequest,
        specifiedType: const FullType.nullable(ProposalInfoRequest),
      );
    }
    if (object.insurerDueAt != null) {
      yield r'insurerDueAt';
      yield serializers.serialize(
        object.insurerDueAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.mode != null) {
      yield r'mode';
      yield serializers.serialize(
        object.mode,
        specifiedType: const FullType.nullable(AcceptanceMode),
      );
    }
    if (object.reason != null) {
      yield r'reason';
      yield serializers.serialize(
        object.reason,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.rejectionReasonCode != null) {
      yield r'rejectionReasonCode';
      yield serializers.serialize(
        object.rejectionReasonCode,
        specifiedType: const FullType.nullable(RejectionReasonCode),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalDecision object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalDecisionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'decidedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.decidedAt = valueDes;
          break;
        case r'infoRequest':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalInfoRequest),
          ) as ProposalInfoRequest?;
          if (valueDes == null) continue;
          result.infoRequest.replace(valueDes);
          break;
        case r'insurerDueAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.insurerDueAt = valueDes;
          break;
        case r'mode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(AcceptanceMode),
          ) as AcceptanceMode?;
          if (valueDes == null) continue;
          result.mode = valueDes;
          break;
        case r'reason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.reason = valueDes;
          break;
        case r'rejectionReasonCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(RejectionReasonCode),
          ) as RejectionReasonCode?;
          if (valueDes == null) continue;
          result.rejectionReasonCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProposalDecision deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalDecisionBuilder();
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

