//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_checklist.dart';
import 'package:moseguros_api/src/model/proposal_organization.dart';
import 'package:moseguros_api/src/model/proposal_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'broker_proposal_summary_response.g.dart';

/// BrokerProposalSummaryResponse
///
/// Properties:
/// * [checklist] 
/// * [createdAt] 
/// * [customerMsisdnMasked] 
/// * [customerName] - Nome do tomador
/// * [expiresAt] 
/// * [id] 
/// * [insurer] 
/// * [number] 
/// * [productName] 
/// * [status] 
/// * [total] 
@BuiltValue()
abstract class BrokerProposalSummaryResponse implements Built<BrokerProposalSummaryResponse, BrokerProposalSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'checklist')
  ProposalChecklist get checklist;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'customerMsisdnMasked')
  String? get customerMsisdnMasked;

  /// Nome do tomador
  @BuiltValueField(wireName: r'customerName')
  String get customerName;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime? get expiresAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'insurer')
  ProposalOrganization? get insurer;

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'productName')
  String get productName;

  @BuiltValueField(wireName: r'status')
  ProposalStatus get status;
  // enum statusEnum {  DRAFT,  UNDER_REVIEW,  INFO_REQUESTED,  ACCEPTED,  REJECTED,  EXPIRED,  PAID,  CONVERTED,  };

  @BuiltValueField(wireName: r'total')
  Money get total;

  BrokerProposalSummaryResponse._();

  factory BrokerProposalSummaryResponse([void updates(BrokerProposalSummaryResponseBuilder b)]) = _$BrokerProposalSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(BrokerProposalSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<BrokerProposalSummaryResponse> get serializer => _$BrokerProposalSummaryResponseSerializer();
}

class _$BrokerProposalSummaryResponseSerializer implements PrimitiveSerializer<BrokerProposalSummaryResponse> {
  @override
  final Iterable<Type> types = const [BrokerProposalSummaryResponse, _$BrokerProposalSummaryResponse];

  @override
  final String wireName = r'BrokerProposalSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    BrokerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'checklist';
    yield serializers.serialize(
      object.checklist,
      specifiedType: const FullType(ProposalChecklist),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.customerMsisdnMasked != null) {
      yield r'customerMsisdnMasked';
      yield serializers.serialize(
        object.customerMsisdnMasked,
        specifiedType: const FullType(String),
      );
    }
    yield r'customerName';
    yield serializers.serialize(
      object.customerName,
      specifiedType: const FullType(String),
    );
    if (object.expiresAt != null) {
      yield r'expiresAt';
      yield serializers.serialize(
        object.expiresAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'insurer';
    yield object.insurer == null ? null : serializers.serialize(
      object.insurer,
      specifiedType: const FullType.nullable(ProposalOrganization),
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
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProposalStatus),
    );
    yield r'total';
    yield serializers.serialize(
      object.total,
      specifiedType: const FullType(Money),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    BrokerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required BrokerProposalSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'checklist':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalChecklist),
          ) as ProposalChecklist;
          result.checklist.replace(valueDes);
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'customerMsisdnMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerMsisdnMasked = valueDes;
          break;
        case r'customerName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.customerName = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.expiresAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(ProposalOrganization),
          ) as ProposalOrganization?;
          if (valueDes == null) continue;
          result.insurer.replace(valueDes);
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
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalStatus),
          ) as ProposalStatus;
          result.status = valueDes;
          break;
        case r'total':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.total.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  BrokerProposalSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = BrokerProposalSummaryResponseBuilder();
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

