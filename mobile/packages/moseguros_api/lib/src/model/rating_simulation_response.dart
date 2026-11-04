//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/unavailable_reason.dart';
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_simulation_response.g.dart';

/// RatingSimulationResponse
///
/// Properties:
/// * [breakdown] - Decomposição do prémio (S3-01 §10): provider, variables, durationFactor, coverages (capitalAmount, deductible, base, factors, bonusMalusFactor, exactPremium, premiumAmount), commercialPremiumAmount, charges, subtotalAmount, roundingAdjustmentAmount e totalAmount. Decimais em texto, centavos em inteiro.
/// * [status] 
/// * [unavailableDetail] - Pormenores do motivo de uma cotação NOT_AVAILABLE (ex.: {\"coverageCode\": \"OD_COLLISION\", \"table\": \"BASE\"} ou {\"declineReason\": \"…\"}). Valores em texto, números ou listas de texto.
/// * [unavailableReason] 
@BuiltValue()
abstract class RatingSimulationResponse implements Built<RatingSimulationResponse, RatingSimulationResponseBuilder> {
  /// Decomposição do prémio (S3-01 §10): provider, variables, durationFactor, coverages (capitalAmount, deductible, base, factors, bonusMalusFactor, exactPremium, premiumAmount), commercialPremiumAmount, charges, subtotalAmount, roundingAdjustmentAmount e totalAmount. Decimais em texto, centavos em inteiro.
  @BuiltValueField(wireName: r'breakdown')
  BuiltMap<String, JsonObject?>? get breakdown;

  @BuiltValueField(wireName: r'status')
  RatingSimulationResponseStatusEnum get status;
  // enum statusEnum {  OFFERED,  NOT_AVAILABLE,  EXPIRED,  USED,  };

  /// Pormenores do motivo de uma cotação NOT_AVAILABLE (ex.: {\"coverageCode\": \"OD_COLLISION\", \"table\": \"BASE\"} ou {\"declineReason\": \"…\"}). Valores em texto, números ou listas de texto.
  @BuiltValueField(wireName: r'unavailableDetail')
  BuiltMap<String, JsonObject?>? get unavailableDetail;

  @BuiltValueField(wireName: r'unavailableReason')
  UnavailableReason? get unavailableReason;
  // enum unavailableReasonEnum {  TARIFF_MISSING,  TARIFF_INVALID,  COVERAGE_NOT_OFFERED,  COVERAGE_REQUIREMENTS_NOT_MET,  CAPITAL_NOT_OFFERED,  VEHICLE_VALUE_REQUIRED,  DURATION_NOT_RATED,  NO_MATCHING_ROW,  INSURER_TIMEOUT,  INSURER_UNAVAILABLE,  INSURER_DECLINED,  EXTERNAL_INVALID_RESPONSE,  EVALUATION_ERROR,  };

  RatingSimulationResponse._();

  factory RatingSimulationResponse([void updates(RatingSimulationResponseBuilder b)]) = _$RatingSimulationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingSimulationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingSimulationResponse> get serializer => _$RatingSimulationResponseSerializer();
}

class _$RatingSimulationResponseSerializer implements PrimitiveSerializer<RatingSimulationResponse> {
  @override
  final Iterable<Type> types = const [RatingSimulationResponse, _$RatingSimulationResponse];

  @override
  final String wireName = r'RatingSimulationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingSimulationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.breakdown != null) {
      yield r'breakdown';
      yield serializers.serialize(
        object.breakdown,
        specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(RatingSimulationResponseStatusEnum),
    );
    if (object.unavailableDetail != null) {
      yield r'unavailableDetail';
      yield serializers.serialize(
        object.unavailableDetail,
        specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
      );
    }
    if (object.unavailableReason != null) {
      yield r'unavailableReason';
      yield serializers.serialize(
        object.unavailableReason,
        specifiedType: const FullType.nullable(UnavailableReason),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingSimulationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingSimulationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'breakdown':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
          ) as BuiltMap<String, JsonObject?>;
          result.breakdown.replace(valueDes);
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RatingSimulationResponseStatusEnum),
          ) as RatingSimulationResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'unavailableDetail':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType.nullable(JsonObject)]),
          ) as BuiltMap<String, JsonObject?>;
          result.unavailableDetail.replace(valueDes);
          break;
        case r'unavailableReason':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(UnavailableReason),
          ) as UnavailableReason?;
          if (valueDes == null) continue;
          result.unavailableReason = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RatingSimulationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingSimulationResponseBuilder();
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

class RatingSimulationResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OFFERED')
  static const RatingSimulationResponseStatusEnum OFFERED = _$ratingSimulationResponseStatusEnum_OFFERED;
  @BuiltValueEnumConst(wireName: r'NOT_AVAILABLE')
  static const RatingSimulationResponseStatusEnum NOT_AVAILABLE = _$ratingSimulationResponseStatusEnum_NOT_AVAILABLE;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const RatingSimulationResponseStatusEnum EXPIRED = _$ratingSimulationResponseStatusEnum_EXPIRED;
  @BuiltValueEnumConst(wireName: r'USED')
  static const RatingSimulationResponseStatusEnum USED = _$ratingSimulationResponseStatusEnum_USED;

  static Serializer<RatingSimulationResponseStatusEnum> get serializer => _$ratingSimulationResponseStatusEnumSerializer;

  const RatingSimulationResponseStatusEnum._(String name): super(name);

  static BuiltSet<RatingSimulationResponseStatusEnum> get values => _$ratingSimulationResponseStatusEnumValues;
  static RatingSimulationResponseStatusEnum valueOf(String name) => _$ratingSimulationResponseStatusEnumValueOf(name);
}

