//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/quote_summary_vehicle.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_request_summary_response.g.dart';

/// QuoteRequestSummaryResponse
///
/// Properties:
/// * [bestTotal] 
/// * [clientMsisdn] 
/// * [createdAt] 
/// * [durationDays] 
/// * [expired] 
/// * [id] 
/// * [offeredCount] 
/// * [origin] 
/// * [productType] 
/// * [startDate] 
/// * [validUntil] 
/// * [vehicle] 
@BuiltValue()
abstract class QuoteRequestSummaryResponse implements Built<QuoteRequestSummaryResponse, QuoteRequestSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'bestTotal')
  Money? get bestTotal;

  @BuiltValueField(wireName: r'clientMsisdn')
  String? get clientMsisdn;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  @BuiltValueField(wireName: r'expired')
  bool get expired;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'offeredCount')
  int get offeredCount;

  @BuiltValueField(wireName: r'origin')
  QuoteRequestSummaryResponseOriginEnum get origin;
  // enum originEnum {  CUSTOMER,  BROKER,  };

  @BuiltValueField(wireName: r'productType')
  QuoteRequestSummaryResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  @BuiltValueField(wireName: r'validUntil')
  DateTime? get validUntil;

  @BuiltValueField(wireName: r'vehicle')
  QuoteSummaryVehicle get vehicle;

  QuoteRequestSummaryResponse._();

  factory QuoteRequestSummaryResponse([void updates(QuoteRequestSummaryResponseBuilder b)]) = _$QuoteRequestSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteRequestSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteRequestSummaryResponse> get serializer => _$QuoteRequestSummaryResponseSerializer();
}

class _$QuoteRequestSummaryResponseSerializer implements PrimitiveSerializer<QuoteRequestSummaryResponse> {
  @override
  final Iterable<Type> types = const [QuoteRequestSummaryResponse, _$QuoteRequestSummaryResponse];

  @override
  final String wireName = r'QuoteRequestSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteRequestSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.bestTotal != null) {
      yield r'bestTotal';
      yield serializers.serialize(
        object.bestTotal,
        specifiedType: const FullType(Money),
      );
    }
    if (object.clientMsisdn != null) {
      yield r'clientMsisdn';
      yield serializers.serialize(
        object.clientMsisdn,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'expired';
    yield serializers.serialize(
      object.expired,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'offeredCount';
    yield serializers.serialize(
      object.offeredCount,
      specifiedType: const FullType(int),
    );
    yield r'origin';
    yield serializers.serialize(
      object.origin,
      specifiedType: const FullType(QuoteRequestSummaryResponseOriginEnum),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(QuoteRequestSummaryResponseProductTypeEnum),
    );
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    if (object.validUntil != null) {
      yield r'validUntil';
      yield serializers.serialize(
        object.validUntil,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(QuoteSummaryVehicle),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteRequestSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteRequestSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'bestTotal':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.bestTotal.replace(valueDes);
          break;
        case r'clientMsisdn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.clientMsisdn = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'expired':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.expired = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'offeredCount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.offeredCount = valueDes;
          break;
        case r'origin':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteRequestSummaryResponseOriginEnum),
          ) as QuoteRequestSummaryResponseOriginEnum;
          result.origin = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteRequestSummaryResponseProductTypeEnum),
          ) as QuoteRequestSummaryResponseProductTypeEnum;
          result.productType = valueDes;
          break;
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
          break;
        case r'validUntil':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.validUntil = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteSummaryVehicle),
          ) as QuoteSummaryVehicle;
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
  QuoteRequestSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteRequestSummaryResponseBuilder();
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

class QuoteRequestSummaryResponseOriginEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CUSTOMER')
  static const QuoteRequestSummaryResponseOriginEnum CUSTOMER = _$quoteRequestSummaryResponseOriginEnum_CUSTOMER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const QuoteRequestSummaryResponseOriginEnum BROKER = _$quoteRequestSummaryResponseOriginEnum_BROKER;

  static Serializer<QuoteRequestSummaryResponseOriginEnum> get serializer => _$quoteRequestSummaryResponseOriginEnumSerializer;

  const QuoteRequestSummaryResponseOriginEnum._(String name): super(name);

  static BuiltSet<QuoteRequestSummaryResponseOriginEnum> get values => _$quoteRequestSummaryResponseOriginEnumValues;
  static QuoteRequestSummaryResponseOriginEnum valueOf(String name) => _$quoteRequestSummaryResponseOriginEnumValueOf(name);
}

class QuoteRequestSummaryResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const QuoteRequestSummaryResponseProductTypeEnum MTPL_ONLY = _$quoteRequestSummaryResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const QuoteRequestSummaryResponseProductTypeEnum COMPREHENSIVE = _$quoteRequestSummaryResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const QuoteRequestSummaryResponseProductTypeEnum TEMPORARY = _$quoteRequestSummaryResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const QuoteRequestSummaryResponseProductTypeEnum BORDER = _$quoteRequestSummaryResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const QuoteRequestSummaryResponseProductTypeEnum FLEET = _$quoteRequestSummaryResponseProductTypeEnum_FLEET;

  static Serializer<QuoteRequestSummaryResponseProductTypeEnum> get serializer => _$quoteRequestSummaryResponseProductTypeEnumSerializer;

  const QuoteRequestSummaryResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<QuoteRequestSummaryResponseProductTypeEnum> get values => _$quoteRequestSummaryResponseProductTypeEnumValues;
  static QuoteRequestSummaryResponseProductTypeEnum valueOf(String name) => _$quoteRequestSummaryResponseProductTypeEnumValueOf(name);
}

