//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/quote_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/requested_coverage_response.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/quoted_driver.dart';
import 'package:moseguros_api/src/model/quoted_vehicle.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'quote_request_response.g.dart';

/// QuoteRequestResponse
///
/// Properties:
/// * [clientMsisdn] 
/// * [createdAt] 
/// * [driver] 
/// * [durationDays] 
/// * [endDate] - Inclusiva
/// * [id] 
/// * [origin] 
/// * [productType] 
/// * [quotes] 
/// * [requestedCoverages] 
/// * [startDate] 
/// * [vehicle] 
@BuiltValue()
abstract class QuoteRequestResponse implements Built<QuoteRequestResponse, QuoteRequestResponseBuilder> {
  @BuiltValueField(wireName: r'clientMsisdn')
  String? get clientMsisdn;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'driver')
  QuotedDriver get driver;

  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  /// Inclusiva
  @BuiltValueField(wireName: r'endDate')
  Date get endDate;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'origin')
  QuoteRequestResponseOriginEnum get origin;
  // enum originEnum {  CUSTOMER,  BROKER,  };

  @BuiltValueField(wireName: r'productType')
  QuoteRequestResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'quotes')
  BuiltList<QuoteResponse> get quotes;

  @BuiltValueField(wireName: r'requestedCoverages')
  BuiltList<RequestedCoverageResponse> get requestedCoverages;

  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  @BuiltValueField(wireName: r'vehicle')
  QuotedVehicle get vehicle;

  QuoteRequestResponse._();

  factory QuoteRequestResponse([void updates(QuoteRequestResponseBuilder b)]) = _$QuoteRequestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuoteRequestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuoteRequestResponse> get serializer => _$QuoteRequestResponseSerializer();
}

class _$QuoteRequestResponseSerializer implements PrimitiveSerializer<QuoteRequestResponse> {
  @override
  final Iterable<Type> types = const [QuoteRequestResponse, _$QuoteRequestResponse];

  @override
  final String wireName = r'QuoteRequestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuoteRequestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    yield r'driver';
    yield serializers.serialize(
      object.driver,
      specifiedType: const FullType(QuotedDriver),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'endDate';
    yield serializers.serialize(
      object.endDate,
      specifiedType: const FullType(Date),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'origin';
    yield serializers.serialize(
      object.origin,
      specifiedType: const FullType(QuoteRequestResponseOriginEnum),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(QuoteRequestResponseProductTypeEnum),
    );
    yield r'quotes';
    yield serializers.serialize(
      object.quotes,
      specifiedType: const FullType(BuiltList, [FullType(QuoteResponse)]),
    );
    yield r'requestedCoverages';
    yield serializers.serialize(
      object.requestedCoverages,
      specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageResponse)]),
    );
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(QuotedVehicle),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuoteRequestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuoteRequestResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        case r'driver':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuotedDriver),
          ) as QuotedDriver;
          result.driver.replace(valueDes);
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'endDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.endDate = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'origin':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteRequestResponseOriginEnum),
          ) as QuoteRequestResponseOriginEnum;
          result.origin = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuoteRequestResponseProductTypeEnum),
          ) as QuoteRequestResponseProductTypeEnum;
          result.productType = valueDes;
          break;
        case r'quotes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(QuoteResponse)]),
          ) as BuiltList<QuoteResponse>;
          result.quotes.replace(valueDes);
          break;
        case r'requestedCoverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageResponse)]),
          ) as BuiltList<RequestedCoverageResponse>;
          result.requestedCoverages.replace(valueDes);
          break;
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
          break;
        case r'vehicle':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuotedVehicle),
          ) as QuotedVehicle;
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
  QuoteRequestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuoteRequestResponseBuilder();
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

class QuoteRequestResponseOriginEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CUSTOMER')
  static const QuoteRequestResponseOriginEnum CUSTOMER = _$quoteRequestResponseOriginEnum_CUSTOMER;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const QuoteRequestResponseOriginEnum BROKER = _$quoteRequestResponseOriginEnum_BROKER;

  static Serializer<QuoteRequestResponseOriginEnum> get serializer => _$quoteRequestResponseOriginEnumSerializer;

  const QuoteRequestResponseOriginEnum._(String name): super(name);

  static BuiltSet<QuoteRequestResponseOriginEnum> get values => _$quoteRequestResponseOriginEnumValues;
  static QuoteRequestResponseOriginEnum valueOf(String name) => _$quoteRequestResponseOriginEnumValueOf(name);
}

class QuoteRequestResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const QuoteRequestResponseProductTypeEnum MTPL_ONLY = _$quoteRequestResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const QuoteRequestResponseProductTypeEnum COMPREHENSIVE = _$quoteRequestResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const QuoteRequestResponseProductTypeEnum TEMPORARY = _$quoteRequestResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const QuoteRequestResponseProductTypeEnum BORDER = _$quoteRequestResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const QuoteRequestResponseProductTypeEnum FLEET = _$quoteRequestResponseProductTypeEnum_FLEET;

  static Serializer<QuoteRequestResponseProductTypeEnum> get serializer => _$quoteRequestResponseProductTypeEnumSerializer;

  const QuoteRequestResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<QuoteRequestResponseProductTypeEnum> get values => _$quoteRequestResponseProductTypeEnumValues;
  static QuoteRequestResponseProductTypeEnum valueOf(String name) => _$quoteRequestResponseProductTypeEnumValueOf(name);
}

