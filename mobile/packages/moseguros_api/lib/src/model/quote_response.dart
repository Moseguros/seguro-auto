//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/quote_charge_response.dart';
import 'package:moseguros_api/src/model/quote_coverage_response.dart';
import 'package:moseguros_api/src/model/quote_product.dart';
import 'package:moseguros_api/src/model/quote_insurer.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/unavailable_reason.dart';
import 'package:built_value/json_object.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_response.g.dart';

/// QuoteResponse
///
/// Properties:
/// * [charges] 
/// * [commercialPremium] 
/// * [coverages] 
/// * [id] 
/// * [insurer] 
/// * [product] 
/// * [roundingAdjustment] 
/// * [status] 
/// * [totalAmount] 
/// * [unavailableDetail] - Pormenores do motivo de uma cotação NOT_AVAILABLE (ex.: {\"coverageCode\": \"OD_COLLISION\", \"table\": \"BASE\"} ou {\"declineReason\": \"…\"}). Valores em texto, números ou listas de texto.
/// * [unavailableReason] 
/// * [validUntil] - Até quando se pode usar numa proposta
@BuiltValue()
abstract class QuoteResponse implements Built<QuoteResponse, QuoteResponseBuilder> {
  @BuiltValueField(wireName: r'charges')
  BuiltList<QuoteChargeResponse>? get charges;

  @BuiltValueField(wireName: r'commercialPremium')
  Money? get commercialPremium;

  @BuiltValueField(wireName: r'coverages')
  BuiltList<QuoteCoverageResponse>? get coverages;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'insurer')
  QuoteInsurer get insurer;

  @BuiltValueField(wireName: r'product')
  QuoteProduct get product;

  @BuiltValueField(wireName: r'roundingAdjustment')
  Money? get roundingAdjustment;

  @BuiltValueField(wireName: r'status')
  QuoteResponseStatusEnum get status;
  // enum statusEnum {  OFFERED,  NOT_AVAILABLE,  EXPIRED,  USED,  };

  @BuiltValueField(wireName: r'totalAmount')
  Money? get totalAmount;

  /// Pormenores do motivo de uma cotação NOT_AVAILABLE (ex.: {\"coverageCode\": \"OD_COLLISION\", \"table\": \"BASE\"} ou {\"declineReason\": \"…\"}). Valores em texto, números ou listas de texto.
  @BuiltValueField(wireName: r'unavailableDetail')
  BuiltMap<String, JsonObject?>? get unavailableDetail;

  @BuiltValueField(wireName: r'unavailableReason')
  UnavailableReason? get unavailableReason;
  // enum unavailableReasonEnum {  TARIFF_MISSING,  TARIFF_INVALID,  COVERAGE_NOT_OFFERED,  COVERAGE_REQUIREMENTS_NOT_MET,  CAPITAL_NOT_OFFERED,  VEHICLE_VALUE_REQUIRED,  DURATION_NOT_RATED,  NO_MATCHING_ROW,  INSURER_TIMEOUT,  INSURER_UNAVAILABLE,  INSURER_DECLINED,  EXTERNAL_INVALID_RESPONSE,  EVALUATION_ERROR,  };

  /// Até quando se pode usar numa proposta
  @BuiltValueField(wireName: r'validUntil')
  DateTime? get validUntil;

  QuoteResponse._();

  factory QuoteResponse([void updates(QuoteResponseBuilder b)]) = _$QuoteResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteResponse> get serializer => _$QuoteResponseSerializer();
}

class _$QuoteResponseSerializer implements PrimitiveSerializer<QuoteResponse> {
  @override
  final Iterable<Type> types = const [QuoteResponse, _$QuoteResponse];

  @override
  final String wireName = r'QuoteResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.charges != null) {
      yield r'charges';
      yield serializers.serialize(
        object.charges,
        specifiedType: const FullType(BuiltList, [FullType(QuoteChargeResponse)]),
      );
    }
    if (object.commercialPremium != null) {
      yield r'commercialPremium';
      yield serializers.serialize(
        object.commercialPremium,
        specifiedType: const FullType(Money),
      );
    }
    if (object.coverages != null) {
      yield r'coverages';
      yield serializers.serialize(
        object.coverages,
        specifiedType: const FullType(BuiltList, [FullType(QuoteCoverageResponse)]),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(QuoteInsurer),
    );
    yield r'product';
    yield serializers.serialize(
      object.product,
      specifiedType: const FullType(QuoteProduct),
    );
    if (object.roundingAdjustment != null) {
      yield r'roundingAdjustment';
      yield serializers.serialize(
        object.roundingAdjustment,
        specifiedType: const FullType(Money),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(QuoteResponseStatusEnum),
    );
    if (object.totalAmount != null) {
      yield r'totalAmount';
      yield serializers.serialize(
        object.totalAmount,
        specifiedType: const FullType(Money),
      );
    }
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
    if (object.validUntil != null) {
      yield r'validUntil';
      yield serializers.serialize(
        object.validUntil,
        specifiedType: const FullType(DateTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'charges':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(QuoteChargeResponse)]),
          ) as BuiltList<QuoteChargeResponse>;
          result.charges.replace(valueDes);
          break;
        case r'commercialPremium':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.commercialPremium.replace(valueDes);
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(QuoteCoverageResponse)]),
          ) as BuiltList<QuoteCoverageResponse>;
          result.coverages.replace(valueDes);
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
            specifiedType: const FullType(QuoteInsurer),
          ) as QuoteInsurer;
          result.insurer.replace(valueDes);
          break;
        case r'product':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteProduct),
          ) as QuoteProduct;
          result.product.replace(valueDes);
          break;
        case r'roundingAdjustment':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.roundingAdjustment.replace(valueDes);
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteResponseStatusEnum),
          ) as QuoteResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'totalAmount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.totalAmount.replace(valueDes);
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
        case r'validUntil':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.validUntil = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  QuoteResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteResponseBuilder();
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

class QuoteResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OFFERED')
  static const QuoteResponseStatusEnum OFFERED = _$quoteResponseStatusEnum_OFFERED;
  @BuiltValueEnumConst(wireName: r'NOT_AVAILABLE')
  static const QuoteResponseStatusEnum NOT_AVAILABLE = _$quoteResponseStatusEnum_NOT_AVAILABLE;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const QuoteResponseStatusEnum EXPIRED = _$quoteResponseStatusEnum_EXPIRED;
  @BuiltValueEnumConst(wireName: r'USED')
  static const QuoteResponseStatusEnum USED = _$quoteResponseStatusEnum_USED;

  static Serializer<QuoteResponseStatusEnum> get serializer => _$quoteResponseStatusEnumSerializer;

  const QuoteResponseStatusEnum._(String name): super(name);

  static BuiltSet<QuoteResponseStatusEnum> get values => _$quoteResponseStatusEnumValues;
  static QuoteResponseStatusEnum valueOf(String name) => _$quoteResponseStatusEnumValueOf(name);
}

