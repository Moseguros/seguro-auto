//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/proposal_status.dart';
import 'package:moseguros_api/src/model/inspection_status.dart';
import 'package:moseguros_api/src/model/acceptance_mode.dart';
import 'package:moseguros_api/src/model/sla_status.dart';
import 'package:moseguros_api/src/model/proposal_channel.dart';
import 'package:moseguros_api/src/model/kyc_state.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_proposal_summary_response.g.dart';

/// InsurerProposalSummaryResponse
///
/// Properties:
/// * [acceptanceMode] 
/// * [brokerName] 
/// * [channel] 
/// * [customerName] 
/// * [id] 
/// * [inspectionStatus] 
/// * [kycState] 
/// * [number] 
/// * [productName] 
/// * [productType] 
/// * [slaDueAt] - Fim do prazo de decisão; só em UNDER_REVIEW
/// * [slaStatus] 
/// * [status] 
/// * [submittedAt] 
/// * [total] 
/// * [vehiclePlate] 
@BuiltValue()
abstract class InsurerProposalSummaryResponse implements Built<InsurerProposalSummaryResponse, InsurerProposalSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'acceptanceMode')
  AcceptanceMode? get acceptanceMode;
  // enum acceptanceModeEnum {  AUTOMATIC,  MANUAL,  };

  @BuiltValueField(wireName: r'brokerName')
  String? get brokerName;

  @BuiltValueField(wireName: r'channel')
  ProposalChannel get channel;
  // enum channelEnum {  APP,  WEB,  BROKER,  };

  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'inspectionStatus')
  InspectionStatus? get inspectionStatus;
  // enum inspectionStatusEnum {  PENDING,  SUBMITTED,  APPROVED,  REJECTED,  };

  @BuiltValueField(wireName: r'kycState')
  KycState get kycState;
  // enum kycStateEnum {  NONE,  PENDING,  VERIFIED,  REJECTED,  OUTDATED,  };

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'productName')
  String get productName;

  @BuiltValueField(wireName: r'productType')
  InsurerProposalSummaryResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  /// Fim do prazo de decisão; só em UNDER_REVIEW
  @BuiltValueField(wireName: r'slaDueAt')
  DateTime? get slaDueAt;

  @BuiltValueField(wireName: r'slaStatus')
  SlaStatus? get slaStatus;
  // enum slaStatusEnum {  ON_TIME,  ALERT,  OVERDUE,  };

  @BuiltValueField(wireName: r'status')
  ProposalStatus get status;
  // enum statusEnum {  DRAFT,  UNDER_REVIEW,  INFO_REQUESTED,  ACCEPTED,  REJECTED,  EXPIRED,  PAID,  CONVERTED,  };

  @BuiltValueField(wireName: r'submittedAt')
  DateTime get submittedAt;

  @BuiltValueField(wireName: r'total')
  Money get total;

  @BuiltValueField(wireName: r'vehiclePlate')
  String get vehiclePlate;

  InsurerProposalSummaryResponse._();

  factory InsurerProposalSummaryResponse([void updates(InsurerProposalSummaryResponseBuilder b)]) = _$InsurerProposalSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerProposalSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerProposalSummaryResponse> get serializer => _$InsurerProposalSummaryResponseSerializer();
}

class _$InsurerProposalSummaryResponseSerializer implements PrimitiveSerializer<InsurerProposalSummaryResponse> {
  @override
  final Iterable<Type> types = const [InsurerProposalSummaryResponse, _$InsurerProposalSummaryResponse];

  @override
  final String wireName = r'InsurerProposalSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.acceptanceMode != null) {
      yield r'acceptanceMode';
      yield serializers.serialize(
        object.acceptanceMode,
        specifiedType: const FullType.nullable(AcceptanceMode),
      );
    }
    if (object.brokerName != null) {
      yield r'brokerName';
      yield serializers.serialize(
        object.brokerName,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(ProposalChannel),
    );
    yield r'customerName';
    yield serializers.serialize(
      object.customerName,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.inspectionStatus != null) {
      yield r'inspectionStatus';
      yield serializers.serialize(
        object.inspectionStatus,
        specifiedType: const FullType(InspectionStatus),
      );
    }
    yield r'kycState';
    yield serializers.serialize(
      object.kycState,
      specifiedType: const FullType(KycState),
    );
    yield r'number';
    yield serializers.serialize(
      object.number,
      specifiedType: const FullType(String),
    );
    yield r'productName';
    yield serializers.serialize(
      object.productName,
      specifiedType: const FullType(String),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(InsurerProposalSummaryResponseProductTypeEnum),
    );
    if (object.slaDueAt != null) {
      yield r'slaDueAt';
      yield serializers.serialize(
        object.slaDueAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    if (object.slaStatus != null) {
      yield r'slaStatus';
      yield serializers.serialize(
        object.slaStatus,
        specifiedType: const FullType.nullable(SlaStatus),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProposalStatus),
    );
    yield r'submittedAt';
    yield serializers.serialize(
      object.submittedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'total';
    yield serializers.serialize(
      object.total,
      specifiedType: const FullType(Money),
    );
    yield r'vehiclePlate';
    yield serializers.serialize(
      object.vehiclePlate,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerProposalSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'acceptanceMode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(AcceptanceMode),
          ) as AcceptanceMode?;
          if (valueDes == null) continue;
          result.acceptanceMode = valueDes;
          break;
        case r'brokerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.brokerName = valueDes;
          break;
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChannel),
          ) as ProposalChannel;
          result.channel = valueDes;
          break;
        case r'customerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerName = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'inspectionStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InspectionStatus),
          ) as InspectionStatus;
          result.inspectionStatus = valueDes;
          break;
        case r'kycState':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(KycState),
          ) as KycState;
          result.kycState = valueDes;
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.number = valueDes;
          break;
        case r'productName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productName = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InsurerProposalSummaryResponseProductTypeEnum),
          ) as InsurerProposalSummaryResponseProductTypeEnum;
          result.productType = valueDes;
          break;
        case r'slaDueAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.slaDueAt = valueDes;
          break;
        case r'slaStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(SlaStatus),
          ) as SlaStatus?;
          if (valueDes == null) continue;
          result.slaStatus = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalStatus),
          ) as ProposalStatus;
          result.status = valueDes;
          break;
        case r'submittedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.submittedAt = valueDes;
          break;
        case r'total':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.total.replace(valueDes);
          break;
        case r'vehiclePlate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehiclePlate = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerProposalSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerProposalSummaryResponseBuilder();
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

class InsurerProposalSummaryResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const InsurerProposalSummaryResponseProductTypeEnum MTPL_ONLY = _$insurerProposalSummaryResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const InsurerProposalSummaryResponseProductTypeEnum COMPREHENSIVE = _$insurerProposalSummaryResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const InsurerProposalSummaryResponseProductTypeEnum TEMPORARY = _$insurerProposalSummaryResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const InsurerProposalSummaryResponseProductTypeEnum BORDER = _$insurerProposalSummaryResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const InsurerProposalSummaryResponseProductTypeEnum FLEET = _$insurerProposalSummaryResponseProductTypeEnum_FLEET;

  static Serializer<InsurerProposalSummaryResponseProductTypeEnum> get serializer => _$insurerProposalSummaryResponseProductTypeEnumSerializer;

  const InsurerProposalSummaryResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<InsurerProposalSummaryResponseProductTypeEnum> get values => _$insurerProposalSummaryResponseProductTypeEnumValues;
  static InsurerProposalSummaryResponseProductTypeEnum valueOf(String name) => _$insurerProposalSummaryResponseProductTypeEnumValueOf(name);
}

