//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'broker_vehicle_request.g.dart';

/// BrokerVehicleRequest
///
/// Properties:
/// * [category] 
/// * [commercialValue] 
/// * [engineCc] - 1–30 000; em TRAILER tem de faltar
/// * [grossWeightKg] - 1–100 000; obrigatório em HEAVY e TRAILER
/// * [hasTracker] - Localizador instalado (defeito false)
/// * [make] 
/// * [model] 
/// * [plate] - Opcional; num dos formatos activos (INVALID_PLATE)
/// * [provinceOfCirculation] 
/// * [seats] - TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100
/// * [usage] 
/// * [year] - De 1900 ao próximo ano
@BuiltValue()
abstract class BrokerVehicleRequest implements Built<BrokerVehicleRequest, BrokerVehicleRequestBuilder> {
  @BuiltValueField(wireName: r'category')
  VehicleCategory get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'commercialValue')
  Money? get commercialValue;

  /// 1–30 000; em TRAILER tem de faltar
  @BuiltValueField(wireName: r'engineCc')
  int? get engineCc;

  /// 1–100 000; obrigatório em HEAVY e TRAILER
  @BuiltValueField(wireName: r'grossWeightKg')
  int? get grossWeightKg;

  /// Localizador instalado (defeito false)
  @BuiltValueField(wireName: r'hasTracker')
  bool? get hasTracker;

  @BuiltValueField(wireName: r'make')
  String? get make;

  @BuiltValueField(wireName: r'model')
  String? get model;

  /// Opcional; num dos formatos activos (INVALID_PLATE)
  @BuiltValueField(wireName: r'plate')
  String? get plate;

  @BuiltValueField(wireName: r'provinceOfCirculation')
  BrokerVehicleRequestProvinceOfCirculationEnum get provinceOfCirculation;
  // enum provinceOfCirculationEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  /// TRAILER: 0; MOTORCYCLE: 1–3; outras: 1–100
  @BuiltValueField(wireName: r'seats')
  int get seats;

  @BuiltValueField(wireName: r'usage')
  VehicleUsage get usage;
  // enum usageEnum {  PRIVATE,  RENTAL,  PASSENGER_TRANSPORT,  GOODS,  TAXI,  DRIVING_SCHOOL,  };

  /// De 1900 ao próximo ano
  @BuiltValueField(wireName: r'year')
  int get year;

  BrokerVehicleRequest._();

  factory BrokerVehicleRequest([void updates(BrokerVehicleRequestBuilder b)]) = _$BrokerVehicleRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(BrokerVehicleRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<BrokerVehicleRequest> get serializer => _$BrokerVehicleRequestSerializer();
}

class _$BrokerVehicleRequestSerializer implements PrimitiveSerializer<BrokerVehicleRequest> {
  @override
  final Iterable<Type> types = const [BrokerVehicleRequest, _$BrokerVehicleRequest];

  @override
  final String wireName = r'BrokerVehicleRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    BrokerVehicleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(VehicleCategory),
    );
    if (object.commercialValue != null) {
      yield r'commercialValue';
      yield serializers.serialize(
        object.commercialValue,
        specifiedType: const FullType(Money),
      );
    }
    if (object.engineCc != null) {
      yield r'engineCc';
      yield serializers.serialize(
        object.engineCc,
        specifiedType: const FullType(int),
      );
    }
    if (object.grossWeightKg != null) {
      yield r'grossWeightKg';
      yield serializers.serialize(
        object.grossWeightKg,
        specifiedType: const FullType(int),
      );
    }
    if (object.hasTracker != null) {
      yield r'hasTracker';
      yield serializers.serialize(
        object.hasTracker,
        specifiedType: const FullType(bool),
      );
    }
    if (object.make != null) {
      yield r'make';
      yield serializers.serialize(
        object.make,
        specifiedType: const FullType(String),
      );
    }
    if (object.model != null) {
      yield r'model';
      yield serializers.serialize(
        object.model,
        specifiedType: const FullType(String),
      );
    }
    if (object.plate != null) {
      yield r'plate';
      yield serializers.serialize(
        object.plate,
        specifiedType: const FullType(String),
      );
    }
    yield r'provinceOfCirculation';
    yield serializers.serialize(
      object.provinceOfCirculation,
      specifiedType: const FullType(BrokerVehicleRequestProvinceOfCirculationEnum),
    );
    yield r'seats';
    yield serializers.serialize(
      object.seats,
      specifiedType: const FullType(int),
    );
    yield r'usage';
    yield serializers.serialize(
      object.usage,
      specifiedType: const FullType(VehicleUsage),
    );
    yield r'year';
    yield serializers.serialize(
      object.year,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    BrokerVehicleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required BrokerVehicleRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'category':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(VehicleCategory),
          ) as VehicleCategory;
          result.category = valueDes;
          break;
        case r'commercialValue':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.commercialValue.replace(valueDes);
          break;
        case r'engineCc':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.engineCc = valueDes;
          break;
        case r'grossWeightKg':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.grossWeightKg = valueDes;
          break;
        case r'hasTracker':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.hasTracker = valueDes;
          break;
        case r'make':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.make = valueDes;
          break;
        case r'model':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.model = valueDes;
          break;
        case r'plate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.plate = valueDes;
          break;
        case r'provinceOfCirculation':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BrokerVehicleRequestProvinceOfCirculationEnum),
          ) as BrokerVehicleRequestProvinceOfCirculationEnum;
          result.provinceOfCirculation = valueDes;
          break;
        case r'seats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.seats = valueDes;
          break;
        case r'usage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(VehicleUsage),
          ) as VehicleUsage;
          result.usage = valueDes;
          break;
        case r'year':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.year = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  BrokerVehicleRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = BrokerVehicleRequestBuilder();
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

class BrokerVehicleRequestProvinceOfCirculationEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const BrokerVehicleRequestProvinceOfCirculationEnum MAPUTO_CITY = _$brokerVehicleRequestProvinceOfCirculationEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const BrokerVehicleRequestProvinceOfCirculationEnum MAPUTO = _$brokerVehicleRequestProvinceOfCirculationEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum GAZA = _$brokerVehicleRequestProvinceOfCirculationEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const BrokerVehicleRequestProvinceOfCirculationEnum INHAMBANE = _$brokerVehicleRequestProvinceOfCirculationEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum SOFALA = _$brokerVehicleRequestProvinceOfCirculationEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum MANICA = _$brokerVehicleRequestProvinceOfCirculationEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const BrokerVehicleRequestProvinceOfCirculationEnum TETE = _$brokerVehicleRequestProvinceOfCirculationEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum ZAMBEZIA = _$brokerVehicleRequestProvinceOfCirculationEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum NAMPULA = _$brokerVehicleRequestProvinceOfCirculationEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const BrokerVehicleRequestProvinceOfCirculationEnum CABO_DELGADO = _$brokerVehicleRequestProvinceOfCirculationEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const BrokerVehicleRequestProvinceOfCirculationEnum NIASSA = _$brokerVehicleRequestProvinceOfCirculationEnum_NIASSA;

  static Serializer<BrokerVehicleRequestProvinceOfCirculationEnum> get serializer => _$brokerVehicleRequestProvinceOfCirculationEnumSerializer;

  const BrokerVehicleRequestProvinceOfCirculationEnum._(String name): super(name);

  static BuiltSet<BrokerVehicleRequestProvinceOfCirculationEnum> get values => _$brokerVehicleRequestProvinceOfCirculationEnumValues;
  static BrokerVehicleRequestProvinceOfCirculationEnum valueOf(String name) => _$brokerVehicleRequestProvinceOfCirculationEnumValueOf(name);
}

