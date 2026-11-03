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

part 'quoted_vehicle.g.dart';

/// QuotedVehicle
///
/// Properties:
/// * [category] 
/// * [commercialValue] 
/// * [engineCc] 
/// * [grossWeightKg] 
/// * [hasTracker] 
/// * [make] 
/// * [model] 
/// * [plate] 
/// * [provinceOfCirculation] 
/// * [seats] 
/// * [usage] 
/// * [vehicleId] 
/// * [year] 
@BuiltValue()
abstract class QuotedVehicle implements Built<QuotedVehicle, QuotedVehicleBuilder> {
  @BuiltValueField(wireName: r'category')
  VehicleCategory get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'commercialValue')
  Money? get commercialValue;

  @BuiltValueField(wireName: r'engineCc')
  int? get engineCc;

  @BuiltValueField(wireName: r'grossWeightKg')
  int? get grossWeightKg;

  @BuiltValueField(wireName: r'hasTracker')
  bool get hasTracker;

  @BuiltValueField(wireName: r'make')
  String? get make;

  @BuiltValueField(wireName: r'model')
  String? get model;

  @BuiltValueField(wireName: r'plate')
  String? get plate;

  @BuiltValueField(wireName: r'provinceOfCirculation')
  QuotedVehicleProvinceOfCirculationEnum get provinceOfCirculation;
  // enum provinceOfCirculationEnum {  MAPUTO_CITY,  MAPUTO,  GAZA,  INHAMBANE,  SOFALA,  MANICA,  TETE,  ZAMBEZIA,  NAMPULA,  CABO_DELGADO,  NIASSA,  };

  @BuiltValueField(wireName: r'seats')
  int? get seats;

  @BuiltValueField(wireName: r'usage')
  VehicleUsage get usage;
  // enum usageEnum {  PRIVATE,  RENTAL,  PASSENGER_TRANSPORT,  GOODS,  TAXI,  DRIVING_SCHOOL,  };

  @BuiltValueField(wireName: r'vehicleId')
  String? get vehicleId;

  @BuiltValueField(wireName: r'year')
  int get year;

  QuotedVehicle._();

  factory QuotedVehicle([void updates(QuotedVehicleBuilder b)]) = _$QuotedVehicle;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(QuotedVehicleBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<QuotedVehicle> get serializer => _$QuotedVehicleSerializer();
}

class _$QuotedVehicleSerializer implements PrimitiveSerializer<QuotedVehicle> {
  @override
  final Iterable<Type> types = const [QuotedVehicle, _$QuotedVehicle];

  @override
  final String wireName = r'QuotedVehicle';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    QuotedVehicle object, {
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
        specifiedType: const FullType.nullable(int),
      );
    }
    if (object.grossWeightKg != null) {
      yield r'grossWeightKg';
      yield serializers.serialize(
        object.grossWeightKg,
        specifiedType: const FullType.nullable(int),
      );
    }
    yield r'hasTracker';
    yield serializers.serialize(
      object.hasTracker,
      specifiedType: const FullType(bool),
    );
    if (object.make != null) {
      yield r'make';
      yield serializers.serialize(
        object.make,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.model != null) {
      yield r'model';
      yield serializers.serialize(
        object.model,
        specifiedType: const FullType.nullable(String),
      );
    }
    if (object.plate != null) {
      yield r'plate';
      yield serializers.serialize(
        object.plate,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'provinceOfCirculation';
    yield serializers.serialize(
      object.provinceOfCirculation,
      specifiedType: const FullType(QuotedVehicleProvinceOfCirculationEnum),
    );
    if (object.seats != null) {
      yield r'seats';
      yield serializers.serialize(
        object.seats,
        specifiedType: const FullType.nullable(int),
      );
    }
    yield r'usage';
    yield serializers.serialize(
      object.usage,
      specifiedType: const FullType(VehicleUsage),
    );
    if (object.vehicleId != null) {
      yield r'vehicleId';
      yield serializers.serialize(
        object.vehicleId,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'year';
    yield serializers.serialize(
      object.year,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    QuotedVehicle object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required QuotedVehicleBuilder result,
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
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.engineCc = valueDes;
          break;
        case r'grossWeightKg':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
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
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.make = valueDes;
          break;
        case r'model':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.model = valueDes;
          break;
        case r'plate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.plate = valueDes;
          break;
        case r'provinceOfCirculation':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(QuotedVehicleProvinceOfCirculationEnum),
          ) as QuotedVehicleProvinceOfCirculationEnum;
          result.provinceOfCirculation = valueDes;
          break;
        case r'seats':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(int),
          ) as int?;
          if (valueDes == null) continue;
          result.seats = valueDes;
          break;
        case r'usage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(VehicleUsage),
          ) as VehicleUsage;
          result.usage = valueDes;
          break;
        case r'vehicleId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.vehicleId = valueDes;
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
  QuotedVehicle deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = QuotedVehicleBuilder();
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

class QuotedVehicleProvinceOfCirculationEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MAPUTO_CITY')
  static const QuotedVehicleProvinceOfCirculationEnum MAPUTO_CITY = _$quotedVehicleProvinceOfCirculationEnum_MAPUTO_CITY;
  @BuiltValueEnumConst(wireName: r'MAPUTO')
  static const QuotedVehicleProvinceOfCirculationEnum MAPUTO = _$quotedVehicleProvinceOfCirculationEnum_MAPUTO;
  @BuiltValueEnumConst(wireName: r'GAZA')
  static const QuotedVehicleProvinceOfCirculationEnum GAZA = _$quotedVehicleProvinceOfCirculationEnum_GAZA;
  @BuiltValueEnumConst(wireName: r'INHAMBANE')
  static const QuotedVehicleProvinceOfCirculationEnum INHAMBANE = _$quotedVehicleProvinceOfCirculationEnum_INHAMBANE;
  @BuiltValueEnumConst(wireName: r'SOFALA')
  static const QuotedVehicleProvinceOfCirculationEnum SOFALA = _$quotedVehicleProvinceOfCirculationEnum_SOFALA;
  @BuiltValueEnumConst(wireName: r'MANICA')
  static const QuotedVehicleProvinceOfCirculationEnum MANICA = _$quotedVehicleProvinceOfCirculationEnum_MANICA;
  @BuiltValueEnumConst(wireName: r'TETE')
  static const QuotedVehicleProvinceOfCirculationEnum TETE = _$quotedVehicleProvinceOfCirculationEnum_TETE;
  @BuiltValueEnumConst(wireName: r'ZAMBEZIA')
  static const QuotedVehicleProvinceOfCirculationEnum ZAMBEZIA = _$quotedVehicleProvinceOfCirculationEnum_ZAMBEZIA;
  @BuiltValueEnumConst(wireName: r'NAMPULA')
  static const QuotedVehicleProvinceOfCirculationEnum NAMPULA = _$quotedVehicleProvinceOfCirculationEnum_NAMPULA;
  @BuiltValueEnumConst(wireName: r'CABO_DELGADO')
  static const QuotedVehicleProvinceOfCirculationEnum CABO_DELGADO = _$quotedVehicleProvinceOfCirculationEnum_CABO_DELGADO;
  @BuiltValueEnumConst(wireName: r'NIASSA')
  static const QuotedVehicleProvinceOfCirculationEnum NIASSA = _$quotedVehicleProvinceOfCirculationEnum_NIASSA;

  static Serializer<QuotedVehicleProvinceOfCirculationEnum> get serializer => _$quotedVehicleProvinceOfCirculationEnumSerializer;

  const QuotedVehicleProvinceOfCirculationEnum._(String name): super(name);

  static BuiltSet<QuotedVehicleProvinceOfCirculationEnum> get values => _$quotedVehicleProvinceOfCirculationEnumValues;
  static QuotedVehicleProvinceOfCirculationEnum valueOf(String name) => _$quotedVehicleProvinceOfCirculationEnumValueOf(name);
}

