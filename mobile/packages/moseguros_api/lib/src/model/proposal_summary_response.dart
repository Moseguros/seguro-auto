//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/proposal_organization.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/proposal_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_summary_response.g.dart';

/// ProposalSummaryResponse
///
/// Properties:
/// * [createdAt] 
/// * [expiresAt] 
/// * [id] 
/// * [insurer] 
/// * [number] 
/// * [productName] 
/// * [productType] 
/// * [startAt] 
/// * [status] 
/// * [total] 
/// * [vehiclePlate] 
@BuiltValue()
abstract class ProposalSummaryResponse implements Built<ProposalSummaryResponse, ProposalSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

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

  @BuiltValueField(wireName: r'productType')
  ProposalSummaryResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'startAt')
  DateTime get startAt;

  @BuiltValueField(wireName: r'status')
  ProposalStatus get status;
  // enum statusEnum {  DRAFT,  UNDER_REVIEW,  INFO_REQUESTED,  ACCEPTED,  REJECTED,  EXPIRED,  PAID,  CONVERTED,  };

  @BuiltValueField(wireName: r'total')
  Money get total;

  @BuiltValueField(wireName: r'vehiclePlate')
  String get vehiclePlate;

  ProposalSummaryResponse._();

  factory ProposalSummaryResponse([void updates(ProposalSummaryResponseBuilder b)]) = _$ProposalSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalSummaryResponse> get serializer => _$ProposalSummaryResponseSerializer();
}

class _$ProposalSummaryResponseSerializer implements PrimitiveSerializer<ProposalSummaryResponse> {
  @override
  final Iterable<Type> types = const [ProposalSummaryResponse, _$ProposalSummaryResponse];

  @override
  final String wireName = r'ProposalSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
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
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(ProposalSummaryResponseProductTypeEnum),
    );
    yield r'startAt';
    yield serializers.serialize(
      object.startAt,
      specifiedType: const FullType(DateTime),
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
    yield r'vehiclePlate';
    yield serializers.serialize(
      object.vehiclePlate,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
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
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProposalSummaryResponseProductTypeEnum),
          ) as ProposalSummaryResponseProductTypeEnum;
          result.productType = valueDes;
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
  ProposalSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalSummaryResponseBuilder();
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

class ProposalSummaryResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const ProposalSummaryResponseProductTypeEnum MTPL_ONLY = _$proposalSummaryResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const ProposalSummaryResponseProductTypeEnum COMPREHENSIVE = _$proposalSummaryResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const ProposalSummaryResponseProductTypeEnum TEMPORARY = _$proposalSummaryResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const ProposalSummaryResponseProductTypeEnum BORDER = _$proposalSummaryResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const ProposalSummaryResponseProductTypeEnum FLEET = _$proposalSummaryResponseProductTypeEnum_FLEET;

  static Serializer<ProposalSummaryResponseProductTypeEnum> get serializer => _$proposalSummaryResponseProductTypeEnumSerializer;

  const ProposalSummaryResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<ProposalSummaryResponseProductTypeEnum> get values => _$proposalSummaryResponseProductTypeEnumValues;
  static ProposalSummaryResponseProductTypeEnum valueOf(String name) => _$proposalSummaryResponseProductTypeEnumValueOf(name);
}

