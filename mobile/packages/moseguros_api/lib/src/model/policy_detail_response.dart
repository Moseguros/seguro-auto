//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/renewal_type.dart';
import 'package:moseguros_api/src/model/policy_proposal_ref.dart';
import 'package:moseguros_api/src/model/policy_insurer_ref.dart';
import 'package:moseguros_api/src/model/policy_instalment.dart';
import 'package:moseguros_api/src/model/policy_certificate.dart';
import 'package:moseguros_api/src/model/coverage_type.dart';
import 'package:moseguros_api/src/model/policy_product_ref.dart';
import 'package:moseguros_api/src/model/policy_status.dart';
import 'package:moseguros_api/src/model/payment_plan.dart';
import 'package:moseguros_api/src/model/policy_vehicle_ref.dart';
import 'package:moseguros_api/src/model/cover_status.dart';
import 'package:moseguros_api/src/model/policy_timeline_event.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/policy_coverage.dart';
import 'package:moseguros_api/src/model/policy_document.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'policy_detail_response.g.dart';

/// PolicyDetailResponse
///
/// Properties:
/// * [certificate] 
/// * [coverStatus] 
/// * [coverageType] 
/// * [coverages] 
/// * [documents] - Documentos da apólice; descarregam-se por GET /api/v1/files/{fileId}
/// * [endAt] 
/// * [events] - Só no detalhe da seguradora
/// * [id] 
/// * [instalments] 
/// * [insurer] 
/// * [issuedAt] 
/// * [number] 
/// * [paymentPlan] 
/// * [policyholderName] 
/// * [product] 
/// * [proposal] 
/// * [renewalType] 
/// * [startAt] 
/// * [status] 
/// * [totalPremium] 
/// * [vehicle] 
@BuiltValue()
abstract class PolicyDetailResponse implements Built<PolicyDetailResponse, PolicyDetailResponseBuilder> {
  @BuiltValueField(wireName: r'certificate')
  PolicyCertificate? get certificate;

  @BuiltValueField(wireName: r'coverStatus')
  CoverStatus get coverStatus;
  // enum coverStatusEnum {  COVERED,  NOT_STARTED,  ENDED,  SUSPENDED,  CANCELLED,  LAPSED,  };

  @BuiltValueField(wireName: r'coverageType')
  CoverageType get coverageType;
  // enum coverageTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  };

  @BuiltValueField(wireName: r'coverages')
  BuiltList<PolicyCoverage> get coverages;

  /// Documentos da apólice; descarregam-se por GET /api/v1/files/{fileId}
  @BuiltValueField(wireName: r'documents')
  BuiltList<PolicyDocument> get documents;

  @BuiltValueField(wireName: r'endAt')
  DateTime get endAt;

  /// Só no detalhe da seguradora
  @BuiltValueField(wireName: r'events')
  BuiltList<PolicyTimelineEvent>? get events;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'instalments')
  BuiltList<PolicyInstalment> get instalments;

  @BuiltValueField(wireName: r'insurer')
  PolicyInsurerRef get insurer;

  @BuiltValueField(wireName: r'issuedAt')
  DateTime get issuedAt;

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'paymentPlan')
  PaymentPlan get paymentPlan;
  // enum paymentPlanEnum {  FULL,  SEMIANNUAL,  QUARTERLY,  MONTHLY,  };

  @BuiltValueField(wireName: r'policyholderName')
  String get policyholderName;

  @BuiltValueField(wireName: r'product')
  PolicyProductRef get product;

  @BuiltValueField(wireName: r'proposal')
  PolicyProposalRef? get proposal;

  @BuiltValueField(wireName: r'renewalType')
  RenewalType get renewalType;
  // enum renewalTypeEnum {  ANNUAL_RENEWABLE,  FIXED_TERM,  };

  @BuiltValueField(wireName: r'startAt')
  DateTime get startAt;

  @BuiltValueField(wireName: r'status')
  PolicyStatus get status;
  // enum statusEnum {  ACTIVE,  SUSPENDED,  CANCELLED,  EXPIRED,  LAPSED,  };

  @BuiltValueField(wireName: r'totalPremium')
  Money get totalPremium;

  @BuiltValueField(wireName: r'vehicle')
  PolicyVehicleRef get vehicle;

  PolicyDetailResponse._();

  factory PolicyDetailResponse([void updates(PolicyDetailResponseBuilder b)]) = _$PolicyDetailResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PolicyDetailResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PolicyDetailResponse> get serializer => _$PolicyDetailResponseSerializer();
}

class _$PolicyDetailResponseSerializer implements PrimitiveSerializer<PolicyDetailResponse> {
  @override
  final Iterable<Type> types = const [PolicyDetailResponse, _$PolicyDetailResponse];

  @override
  final String wireName = r'PolicyDetailResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PolicyDetailResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.certificate != null) {
      yield r'certificate';
      yield serializers.serialize(
        object.certificate,
        specifiedType: const FullType(PolicyCertificate),
      );
    }
    yield r'coverStatus';
    yield serializers.serialize(
      object.coverStatus,
      specifiedType: const FullType(CoverStatus),
    );
    yield r'coverageType';
    yield serializers.serialize(
      object.coverageType,
      specifiedType: const FullType(CoverageType),
    );
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(PolicyCoverage)]),
    );
    yield r'documents';
    yield serializers.serialize(
      object.documents,
      specifiedType: const FullType(BuiltList, [FullType(PolicyDocument)]),
    );
    yield r'endAt';
    yield serializers.serialize(
      object.endAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.events != null) {
      yield r'events';
      yield serializers.serialize(
        object.events,
        specifiedType: const FullType(BuiltList, [FullType(PolicyTimelineEvent)]),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'instalments';
    yield serializers.serialize(
      object.instalments,
      specifiedType: const FullType(BuiltList, [FullType(PolicyInstalment)]),
    );
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(PolicyInsurerRef),
    );
    yield r'issuedAt';
    yield serializers.serialize(
      object.issuedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'number';
    yield serializers.serialize(
      object.number,
      specifiedType: const FullType(String),
    );
    yield r'paymentPlan';
    yield serializers.serialize(
      object.paymentPlan,
      specifiedType: const FullType(PaymentPlan),
    );
    yield r'policyholderName';
    yield serializers.serialize(
      object.policyholderName,
      specifiedType: const FullType(String),
    );
    yield r'product';
    yield serializers.serialize(
      object.product,
      specifiedType: const FullType(PolicyProductRef),
    );
    if (object.proposal != null) {
      yield r'proposal';
      yield serializers.serialize(
        object.proposal,
        specifiedType: const FullType(PolicyProposalRef),
      );
    }
    yield r'renewalType';
    yield serializers.serialize(
      object.renewalType,
      specifiedType: const FullType(RenewalType),
    );
    yield r'startAt';
    yield serializers.serialize(
      object.startAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(PolicyStatus),
    );
    yield r'totalPremium';
    yield serializers.serialize(
      object.totalPremium,
      specifiedType: const FullType(Money),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(PolicyVehicleRef),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PolicyDetailResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PolicyDetailResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'certificate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyCertificate),
          ) as PolicyCertificate;
          result.certificate.replace(valueDes);
          break;
        case r'coverStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverStatus),
          ) as CoverStatus;
          result.coverStatus = valueDes;
          break;
        case r'coverageType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CoverageType),
          ) as CoverageType;
          result.coverageType = valueDes;
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PolicyCoverage)]),
          ) as BuiltList<PolicyCoverage>;
          result.coverages.replace(valueDes);
          break;
        case r'documents':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PolicyDocument)]),
          ) as BuiltList<PolicyDocument>;
          result.documents.replace(valueDes);
          break;
        case r'endAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.endAt = valueDes;
          break;
        case r'events':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PolicyTimelineEvent)]),
          ) as BuiltList<PolicyTimelineEvent>;
          result.events.replace(valueDes);
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'instalments':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PolicyInstalment)]),
          ) as BuiltList<PolicyInstalment>;
          result.instalments.replace(valueDes);
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyInsurerRef),
          ) as PolicyInsurerRef;
          result.insurer.replace(valueDes);
          break;
        case r'issuedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.issuedAt = valueDes;
          break;
        case r'number':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.number = valueDes;
          break;
        case r'paymentPlan':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentPlan),
          ) as PaymentPlan;
          result.paymentPlan = valueDes;
          break;
        case r'policyholderName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.policyholderName = valueDes;
          break;
        case r'product':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyProductRef),
          ) as PolicyProductRef;
          result.product.replace(valueDes);
          break;
        case r'proposal':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyProposalRef),
          ) as PolicyProposalRef;
          result.proposal.replace(valueDes);
          break;
        case r'renewalType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RenewalType),
          ) as RenewalType;
          result.renewalType = valueDes;
          break;
        case r'startAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.startAt = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyStatus),
          ) as PolicyStatus;
          result.status = valueDes;
          break;
        case r'totalPremium':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.totalPremium.replace(valueDes);
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PolicyVehicleRef),
          ) as PolicyVehicleRef;
          result.vehicle.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PolicyDetailResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PolicyDetailResponseBuilder();
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

