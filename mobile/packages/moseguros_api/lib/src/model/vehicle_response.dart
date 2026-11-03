//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/vehicle_driver_response.dart';
import 'package:moseguros_api/src/model/registration_doc_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'vehicle_response.g.dart';

/// VehicleResponse
///
/// Properties:
/// * [category] 
/// * [chassisNumber] 
/// * [commercialValue] 
/// * [createdAt] 
/// * [drivers] - Condutores activos, o habitual primeiro
/// * [engineCc] 
/// * [grossWeightKg] 
/// * [hasTracker] 
/// * [id] 
/// * [inatroVerified] - true se o registo automóvel confirmou a matrícula e o chassis
/// * [make] 
/// * [model] 
/// * [plate] 
/// * [provinceOfCirculation] 
/// * [registrationDoc] 
/// * [seats] 
/// * [updatedAt] 
/// * [usage] 
/// * [version] 
/// * [year] 
@BuiltValue()
abstract class VehicleResponse implements Built<VehicleResponse, VehicleResponseBuilder> {
  @BuiltValueField(wireName: r'category')
  VehicleCategory get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'chassisNumber')
  String get chassisNumber;

  @BuiltValueField(wireName: r'commercialValue')
  Money? get commercialValue;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  /// Condutores activos, o habitual primeiro
  @BuiltValueField(wireName: r'drivers')
  BuiltList<VehicleDriverResponse> get drivers;

  @BuiltValueField(wireName: r'engineCc')
  int? get engineCc;

  @BuiltValueField(wireName: r'grossWeightKg')
  int? get grossWeightKg;

  @BuiltValueField(wireName: r'hasTracker')
  bool get hasTracker;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// true se o registo automóvel confirmou a matrícula e o chassis
  @BuiltValueField(wireName: r'inatroVerified')
  bool get inatroVerified;

  @BuiltValueField(wireName: r'make')
  String get make;

  @BuiltValueField(wireName: r'model')
  String get model;

  @BuiltValueField(wireName: r'plate')
  String get plate;

  @BuiltValueField(wireName: r'provinceOfCirculation')
  VehicleResponseProvinceOfCirculationEnum get provinceOfCirculation;
  // enum provinceOfCirculationEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'registrationDoc')
  RegistrationDocResponse? get registrationDoc;

  @BuiltValueField(wireName: r'seats')
  int get seats;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'usage')
  VehicleUsage get usage;
  // enum usageEnum {  PRIVATE,  RENTAL,  PASSENGER_TRANSPORT,  GOODS,  TAXI,  DRIVING_SCHOOL,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'year')
  int get year;

  VehicleResponse._();

  factory VehicleResponse([void updates(VehicleResponseBuilder b)]) = _$VehicleResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VehicleResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VehicleResponse> get serializer => _$VehicleResponseSerializer();
}

class _$VehicleResponseSerializer implements PrimitiveSerializer<VehicleResponse> {
  @override
  final Iterable<Type> types = const [VehicleResponse, _$VehicleResponse];

  @override
  final String wireName = r'VehicleResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VehicleResponse object, {
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
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'drivers';
    yield serializers.serialize(
      object.drivers,
      specifiedType: const FullType(BuiltList, [FullType(VehicleDriverResponse)]),
    );
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
    yield r'hasTracker';
    yield serializers.serialize(
      object.hasTracker,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'inatroVerified';
    yield serializers.serialize(
      object.inatroVerified,
      specifiedType: const FullType(bool),
    );
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
      specifiedType: const FullType(VehicleResponseProvinceOfCirculationEnum),
    );
    if (object.registrationDoc != null) {
      yield r'registrationDoc';
      yield serializers.serialize(
        object.registrationDoc,
        specifiedType: const FullType(RegistrationDocResponse),
      );
    }
    yield r'seats';
    yield serializers.serialize(
      object.seats,
      specifiedType: const FullType(int),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
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
    VehicleResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VehicleResponseBuilder result,
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
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'drivers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleDriverResponse)]),
          ) as BuiltList<VehicleDriverResponse>;
          result.drivers.replace(valueDes);
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
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'inatroVerified':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.inatroVerified = valueDes;
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
            specifiedType: const FullType(VehicleResponseProvinceOfCirculationEnum),
          ) as VehicleResponseProvinceOfCirculationEnum;
          result.provinceOfCirculation = valueDes;
          break;
        case r'registrationDoc':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RegistrationDocResponse),
          ) as RegistrationDocResponse;
          result.registrationDoc.replace(valueDes);
          break;
        case r'seats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.seats = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
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
  VehicleResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VehicleResponseBuilder();
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

class VehicleResponseProvinceOfCirculationEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const VehicleResponseProvinceOfCirculationEnum MAPUTO_CITY = _$vehicleResponseProvinceOfCirculationEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const VehicleResponseProvinceOfCirculationEnum MAPUTO = _$vehicleResponseProvinceOfCirculationEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const VehicleResponseProvinceOfCirculationEnum GAZA = _$vehicleResponseProvinceOfCirculationEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const VehicleResponseProvinceOfCirculationEnum INHAMBANE = _$vehicleResponseProvinceOfCirculationEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const VehicleResponseProvinceOfCirculationEnum SOFALA = _$vehicleResponseProvinceOfCirculationEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const VehicleResponseProvinceOfCirculationEnum MANICA = _$vehicleResponseProvinceOfCirculationEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const VehicleResponseProvinceOfCirculationEnum TETE = _$vehicleResponseProvinceOfCirculationEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const VehicleResponseProvinceOfCirculationEnum ZAMBEZIA = _$vehicleResponseProvinceOfCirculationEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const VehicleResponseProvinceOfCirculationEnum NAMPULA = _$vehicleResponseProvinceOfCirculationEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const VehicleResponseProvinceOfCirculationEnum CABO_DELGADO = _$vehicleResponseProvinceOfCirculationEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const VehicleResponseProvinceOfCirculationEnum NIASSA = _$vehicleResponseProvinceOfCirculationEnum_NIASSA;

  static Serializer<VehicleResponseProvinceOfCirculationEnum> get serializer => _$vehicleResponseProvinceOfCirculationEnumSerializer;

  const VehicleResponseProvinceOfCirculationEnum._(String name): super(name);

  static BuiltSet<VehicleResponseProvinceOfCirculationEnum> get values => _$vehicleResponseProvinceOfCirculationEnumValues;
  static VehicleResponseProvinceOfCirculationEnum valueOf(String name) => _$vehicleResponseProvinceOfCirculationEnumValueOf(name);
}

