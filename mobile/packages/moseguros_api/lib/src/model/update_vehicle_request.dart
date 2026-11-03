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

part 'update_vehicle_request.g.dart';

/// UpdateVehicleRequest
///
/// Properties:
/// * [category] 
/// * [chassisNumber] 
/// * [commercialValue] 
/// * [engineCc] - Obrigatório excepto em TRAILER, onde tem de faltar
/// * [grossWeightKg] - Obrigatório em HEAVY e TRAILER
/// * [hasTracker] 
/// * [make] 
/// * [model] 
/// * [plate] 
/// * [provinceOfCirculation] 
/// * [seats] 
/// * [usage] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
/// * [year] 
@BuiltValue()
abstract class UpdateVehicleRequest implements Built<UpdateVehicleRequest, UpdateVehicleRequestBuilder> {
  @BuiltValueField(wireName: r'category')
  VehicleCategory get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'chassisNumber')
  String get chassisNumber;

  @BuiltValueField(wireName: r'commercialValue')
  Money? get commercialValue;

  /// Obrigatório excepto em TRAILER, onde tem de faltar
  @BuiltValueField(wireName: r'engineCc')
  int? get engineCc;

  /// Obrigatório em HEAVY e TRAILER
  @BuiltValueField(wireName: r'grossWeightKg')
  int? get grossWeightKg;

  @BuiltValueField(wireName: r'hasTracker')
  bool? get hasTracker;

  @BuiltValueField(wireName: r'make')
  String get make;

  @BuiltValueField(wireName: r'model')
  String get model;

  @BuiltValueField(wireName: r'plate')
  String get plate;

  @BuiltValueField(wireName: r'provinceOfCirculation')
  UpdateVehicleRequestProvinceOfCirculationEnum get provinceOfCirculation;
  // enum provinceOfCirculationEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'seats')
  int get seats;

  @BuiltValueField(wireName: r'usage')
  VehicleUsage get usage;
  // enum usageEnum {  PRIVATE,  RENTAL,  PASSENGER_TRANSPORT,  GOODS,  TAXI,  DRIVING_SCHOOL,  };

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'year')
  int get year;

  UpdateVehicleRequest._();

  factory UpdateVehicleRequest([void updates(UpdateVehicleRequestBuilder b)]) = _$UpdateVehicleRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateVehicleRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateVehicleRequest> get serializer => _$UpdateVehicleRequestSerializer();
}

class _$UpdateVehicleRequestSerializer implements PrimitiveSerializer<UpdateVehicleRequest> {
  @override
  final Iterable<Type> types = const [UpdateVehicleRequest, _$UpdateVehicleRequest];

  @override
  final String wireName = r'UpdateVehicleRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateVehicleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'category';
    yield serializers.serialize(
      object.category,
      specifiedType: const FullType(VehicleCategory),
    );
    yield r'chassisNumber';
    yield serializers.serialize(
      object.chassisNumber,
      specifiedType: const FullType(String),
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
    yield r'make';
    yield serializers.serialize(
      object.make,
      specifiedType: const FullType(String),
    );
    yield r'model';
    yield serializers.serialize(
      object.model,
      specifiedType: const FullType(String),
    );
    yield r'plate';
    yield serializers.serialize(
      object.plate,
      specifiedType: const FullType(String),
    );
    yield r'provinceOfCirculation';
    yield serializers.serialize(
      object.provinceOfCirculation,
      specifiedType: const FullType(UpdateVehicleRequestProvinceOfCirculationEnum),
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
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
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
    UpdateVehicleRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateVehicleRequestBuilder result,
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
        case r'chassisNumber':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.chassisNumber = valueDes;
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
            specifiedType: const FullType(UpdateVehicleRequestProvinceOfCirculationEnum),
          ) as UpdateVehicleRequestProvinceOfCirculationEnum;
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
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
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
  UpdateVehicleRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateVehicleRequestBuilder();
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

class UpdateVehicleRequestProvinceOfCirculationEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const UpdateVehicleRequestProvinceOfCirculationEnum MAPUTO_CITY = _$updateVehicleRequestProvinceOfCirculationEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const UpdateVehicleRequestProvinceOfCirculationEnum MAPUTO = _$updateVehicleRequestProvinceOfCirculationEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum GAZA = _$updateVehicleRequestProvinceOfCirculationEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const UpdateVehicleRequestProvinceOfCirculationEnum INHAMBANE = _$updateVehicleRequestProvinceOfCirculationEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum SOFALA = _$updateVehicleRequestProvinceOfCirculationEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum MANICA = _$updateVehicleRequestProvinceOfCirculationEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const UpdateVehicleRequestProvinceOfCirculationEnum TETE = _$updateVehicleRequestProvinceOfCirculationEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum ZAMBEZIA = _$updateVehicleRequestProvinceOfCirculationEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum NAMPULA = _$updateVehicleRequestProvinceOfCirculationEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const UpdateVehicleRequestProvinceOfCirculationEnum CABO_DELGADO = _$updateVehicleRequestProvinceOfCirculationEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const UpdateVehicleRequestProvinceOfCirculationEnum NIASSA = _$updateVehicleRequestProvinceOfCirculationEnum_NIASSA;

  static Serializer<UpdateVehicleRequestProvinceOfCirculationEnum> get serializer => _$updateVehicleRequestProvinceOfCirculationEnumSerializer;

  const UpdateVehicleRequestProvinceOfCirculationEnum._(String name): super(name);

  static BuiltSet<UpdateVehicleRequestProvinceOfCirculationEnum> get values => _$updateVehicleRequestProvinceOfCirculationEnumValues;
  static UpdateVehicleRequestProvinceOfCirculationEnum valueOf(String name) => _$updateVehicleRequestProvinceOfCirculationEnumValueOf(name);
}

