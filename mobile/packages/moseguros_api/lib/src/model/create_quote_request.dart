//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/requested_coverage_request.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'create_quote_request.g.dart';

/// CreateQuoteRequest
///
/// Properties:
/// * [coverages] - Coberturas pedidas além das obrigatórias (pode ser vazia)
/// * [driverId] - Condutor do cliente
/// * [durationDays] - TEMPORARY: 7, 14, 30, 60 ou 365; os outros tipos: 365
/// * [productType] - MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
/// * [startDate] - Entre hoje e hoje + 60 dias (Africa/Maputo)
/// * [vehicleId] - Veículo activo do cliente
@BuiltValue()
abstract class CreateQuoteRequest implements Built<CreateQuoteRequest, CreateQuoteRequestBuilder> {
  /// Coberturas pedidas além das obrigatórias (pode ser vazia)
  @BuiltValueField(wireName: r'coverages')
  BuiltList<RequestedCoverageRequest>? get coverages;

  /// Condutor do cliente
  @BuiltValueField(wireName: r'driverId')
  String get driverId;

  /// TEMPORARY: 7, 14, 30, 60 ou 365; os outros tipos: 365
  @BuiltValueField(wireName: r'durationDays')
  int get durationDays;

  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueField(wireName: r'productType')
  CreateQuoteRequestProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  /// Entre hoje e hoje + 60 dias (Africa/Maputo)
  @BuiltValueField(wireName: r'startDate')
  Date get startDate;

  /// Veículo activo do cliente
  @BuiltValueField(wireName: r'vehicleId')
  String get vehicleId;

  CreateQuoteRequest._();

  factory CreateQuoteRequest([void updates(CreateQuoteRequestBuilder b)]) = _$CreateQuoteRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CreateQuoteRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CreateQuoteRequest> get serializer => _$CreateQuoteRequestSerializer();
}

class _$CreateQuoteRequestSerializer implements PrimitiveSerializer<CreateQuoteRequest> {
  @override
  final Iterable<Type> types = const [CreateQuoteRequest, _$CreateQuoteRequest];

  @override
  final String wireName = r'CreateQuoteRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CreateQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.coverages != null) {
      yield r'coverages';
      yield serializers.serialize(
        object.coverages,
        specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
      );
    }
    yield r'driverId';
    yield serializers.serialize(
      object.driverId,
      specifiedType: const FullType(String),
    );
    yield r'durationDays';
    yield serializers.serialize(
      object.durationDays,
      specifiedType: const FullType(int),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(CreateQuoteRequestProductTypeEnum),
    );
    yield r'startDate';
    yield serializers.serialize(
      object.startDate,
      specifiedType: const FullType(Date),
    );
    yield r'vehicleId';
    yield serializers.serialize(
      object.vehicleId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CreateQuoteRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CreateQuoteRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RequestedCoverageRequest)]),
          ) as BuiltList<RequestedCoverageRequest>;
          result.coverages.replace(valueDes);
          break;
        case r'driverId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.driverId = valueDes;
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
            specifiedType: const FullType(CreateQuoteRequestProductTypeEnum),
          ) as CreateQuoteRequestProductTypeEnum;
          result.productType = valueDes;
          break;
        case r'startDate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.startDate = valueDes;
          break;
        case r'vehicleId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.vehicleId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  CreateQuoteRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CreateQuoteRequestBuilder();
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

class CreateQuoteRequestProductTypeEnum extends EnumClass {

  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const CreateQuoteRequestProductTypeEnum MTPL_ONLY = _$createQuoteRequestProductTypeEnum_MTPL_ONLY;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const CreateQuoteRequestProductTypeEnum COMPREHENSIVE = _$createQuoteRequestProductTypeEnum_COMPREHENSIVE;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const CreateQuoteRequestProductTypeEnum TEMPORARY = _$createQuoteRequestProductTypeEnum_TEMPORARY;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const CreateQuoteRequestProductTypeEnum BORDER = _$createQuoteRequestProductTypeEnum_BORDER;
  /// MTPL_ONLY, COMPREHENSIVE ou TEMPORARY (BORDER e FLEET → NOT_ALLOWED)
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const CreateQuoteRequestProductTypeEnum FLEET = _$createQuoteRequestProductTypeEnum_FLEET;

  static Serializer<CreateQuoteRequestProductTypeEnum> get serializer => _$createQuoteRequestProductTypeEnumSerializer;

  const CreateQuoteRequestProductTypeEnum._(String name): super(name);

  static BuiltSet<CreateQuoteRequestProductTypeEnum> get values => _$createQuoteRequestProductTypeEnumValues;
  static CreateQuoteRequestProductTypeEnum valueOf(String name) => _$createQuoteRequestProductTypeEnumValueOf(name);
}

