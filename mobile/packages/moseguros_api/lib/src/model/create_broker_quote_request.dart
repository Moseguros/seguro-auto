//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/broker_vehicle_request.dart';
import 'package:moseguros_api/src/model/risk_driver_request.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/requested_coverage_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_broker_quote_request.g.dart';

/// CreateBrokerQuoteRequest
///
/// Properties:
/// * [clientMsisdn] - Telemóvel do cliente (INVALID_MSISDN)
/// * [coverages] - Coberturas pedidas além das obrigatórias (pode ser vazia)
/// * [driver] 
/// * [durationDays] - TEMPORARY: 7, 14, 30, 60 ou 365; os outros tipos: 365
/// * [productType] - MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
/// * [startDate] - Entre hoje e hoje + 60 dias (Africa/Maputo)
/// * [vehicle] 
@BuiltValue()
abstract class CreateBrokerQuoteRequest implements Built<CreateBrokerQuoteRequest, CreateBrokerQuoteRequestBuilder> {
  /// Telemóvel do cliente (INVALID_MSISDN)
  @BuiltValueField(wireName: r'clientMsisdn')
  String get clientMsisdn;

  /// Coberturas pedidas além das obrigatórias (pode ser vazia)
  @BuiltValueField(wireName: r'coverages')
  BuiltList<RequestedCoverageRequest>? get coverages;

  @BuiltValueField(wireName: r'driver')
  RiskDriverRequest get driver;

  /// TEMPORARY: 7, 14, 30, 60 ou 365; os outros tipos: 365
  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueField(wireName: r'productType')
  CreateBrokerQuoteRequestProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  /// Entre hoje e hoje + 60 dias (Africa/Maputo)
  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  @BuiltValueField(wireName: r'vehicle')
  BrokerVehicleRequest get vehicle;

  CreateBrokerQuoteRequest._();

  factory CreateBrokerQuoteRequest([void updates(CreateBrokerQuoteRequestBuilder b)]) = _$CreateBrokerQuoteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateBrokerQuoteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateBrokerQuoteRequest> get serializer => _$CreateBrokerQuoteRequestSerializer();
}

class _$CreateBrokerQuoteRequestSerializer implements PrimitiveSerializer<CreateBrokerQuoteRequest> {
  @override
  final Iterable<Type> types = const [CreateBrokerQuoteRequest, _$CreateBrokerQuoteRequest];

  @override
  final String wireName = r'CreateBrokerQuoteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateBrokerQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'clientMsisdn';
    yield serializers.serialize(
      object.clientMsisdn,
      specifiedType: const FullType(String),
    );
    if (object.coverages != null) {
      yield r'coverages';
      yield serializers.serialize(
        object.coverages,
        specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
      );
    }
    yield r'driver';
    yield serializers.serialize(
      object.driver,
      specifiedType: const FullType(RiskDriverRequest),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(CreateBrokerQuoteRequestProductTypeEnum),
    );
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    yield r'vehicle';
    yield serializers.serialize(
      object.vehicle,
      specifiedType: const FullType(BrokerVehicleRequest),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateBrokerQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateBrokerQuoteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'clientMsisdn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.clientMsisdn = valueDes;
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
          ) as BuiltList<RequestedCoverageRequest>;
          result.coverages.replace(valueDes);
          break;
        case r'driver':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RiskDriverRequest),
          ) as RiskDriverRequest;
          result.driver.replace(valueDes);
          break;
        case r'durationDays':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.durationDays = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CreateBrokerQuoteRequestProductTypeEnum),
          ) as CreateBrokerQuoteRequestProductTypeEnum;
          result.productType = valueDes;
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
            specifiedType: const FullType(BrokerVehicleRequest),
          ) as BrokerVehicleRequest;
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
  CreateBrokerQuoteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateBrokerQuoteRequestBuilder();
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

class CreateBrokerQuoteRequestProductTypeEnum extends EnumClass {

  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const CreateBrokerQuoteRequestProductTypeEnum MTPL_ONLY = _$createBrokerQuoteRequestProductTypeEnum_MTPL_ONLY;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const CreateBrokerQuoteRequestProductTypeEnum COMPREHENSIVE = _$createBrokerQuoteRequestProductTypeEnum_COMPREHENSIVE;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const CreateBrokerQuoteRequestProductTypeEnum TEMPORARY = _$createBrokerQuoteRequestProductTypeEnum_TEMPORARY;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const CreateBrokerQuoteRequestProductTypeEnum BORDER = _$createBrokerQuoteRequestProductTypeEnum_BORDER;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const CreateBrokerQuoteRequestProductTypeEnum FLEET = _$createBrokerQuoteRequestProductTypeEnum_FLEET;

  static Serializer<CreateBrokerQuoteRequestProductTypeEnum> get serializer => _$createBrokerQuoteRequestProductTypeEnumSerializer;

  const CreateBrokerQuoteRequestProductTypeEnum._(String name): super(name);

  static BuiltSet<CreateBrokerQuoteRequestProductTypeEnum> get values => _$createBrokerQuoteRequestProductTypeEnumValues;
  static CreateBrokerQuoteRequestProductTypeEnum valueOf(String name) => _$createBrokerQuoteRequestProductTypeEnumValueOf(name);
}

