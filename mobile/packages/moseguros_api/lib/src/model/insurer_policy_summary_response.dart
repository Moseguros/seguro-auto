//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/cover_status.dart';
import 'package:moseguros_api/src/model/coverage_type.dart';
import 'package:moseguros_api/src/model/policy_status.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'insurer_policy_summary_response.g.dart';

/// InsurerPolicySummaryResponse
///
/// Properties:
/// * [coverStatus] 
/// * [coverageType] 
/// * [endAt] 
/// * [id] 
/// * [issuedAt] 
/// * [number] 
/// * [plate] 
/// * [policyholderName] 
/// * [productName] 
/// * [startAt] 
/// * [status] 
/// * [totalPremium] 
@BuiltValue()
abstract class InsurerPolicySummaryResponse implements Built<InsurerPolicySummaryResponse, InsurerPolicySummaryResponseBuilder> {
  @BuiltValueField(wireName: r'coverStatus')
  CoverStatus get coverStatus;
  // enum coverStatusEnum {  COVERED,  NOT_STARTED,  ENDED,  SUSPENDED,  CANCELLED,  LAPSED,  };

  @BuiltValueField(wireName: r'coverageType')
  CoverageType get coverageType;
  // enum coverageTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  };

  @BuiltValueField(wireName: r'endAt')
  DateTime get endAt;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'issuedAt')
  DateTime get issuedAt;

  @BuiltValueField(wireName: r'number')
  String get number;

  @BuiltValueField(wireName: r'plate')
  String get plate;

  @BuiltValueField(wireName: r'policyholderName')
  String get policyholderName;

  @BuiltValueField(wireName: r'productName')
  String get productName;

  @BuiltValueField(wireName: r'startAt')
  DateTime get startAt;

  @BuiltValueField(wireName: r'status')
  PolicyStatus get status;
  // enum statusEnum {  ACTIVE,  SUSPENDED,  CANCELLED,  EXPIRED,  LAPSED,  };

  @BuiltValueField(wireName: r'totalPremium')
  Money get totalPremium;

  InsurerPolicySummaryResponse._();

  factory InsurerPolicySummaryResponse([void updates(InsurerPolicySummaryResponseBuilder b)]) = _$InsurerPolicySummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InsurerPolicySummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InsurerPolicySummaryResponse> get serializer => _$InsurerPolicySummaryResponseSerializer();
}

class _$InsurerPolicySummaryResponseSerializer implements PrimitiveSerializer<InsurerPolicySummaryResponse> {
  @override
  final Iterable<Type> types = const [InsurerPolicySummaryResponse, _$InsurerPolicySummaryResponse];

  @override
  final String wireName = r'InsurerPolicySummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InsurerPolicySummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    yield r'endAt';
    yield serializers.serialize(
      object.endAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
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
    yield r'plate';
    yield serializers.serialize(
      object.plate,
      specifiedType: const FullType(String),
    );
    yield r'policyholderName';
    yield serializers.serialize(
      object.policyholderName,
      specifiedType: const FullType(String),
    );
    yield r'productName';
    yield serializers.serialize(
      object.productName,
      specifiedType: const FullType(String),
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
  }

  @override
  Object serialize(
    Serializers serializers,
    InsurerPolicySummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InsurerPolicySummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        case r'endAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.endAt = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
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
        case r'plate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.plate = valueDes;
          break;
        case r'policyholderName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.policyholderName = valueDes;
          break;
        case r'productName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productName = valueDes;
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InsurerPolicySummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InsurerPolicySummaryResponseBuilder();
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

