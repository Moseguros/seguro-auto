//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_vehicle.g.dart';

/// ProposalVehicle
///
/// Properties:
/// * [category] 
/// * [commercialValue] 
/// * [id] 
/// * [make] 
/// * [model] 
/// * [plate] 
/// * [usage] 
/// * [year] 
@BuiltValue()
abstract class ProposalVehicle implements Built<ProposalVehicle, ProposalVehicleBuilder> {
  @BuiltValueField(wireName: r'category')
  VehicleCategory get category;
  // enum categoryEnum {  LIGHT_PASSENGER,  LIGHT_MIXED,  HEAVY,  MOTORCYCLE,  TRAILER,  BUS,  };

  @BuiltValueField(wireName: r'commercialValue')
  Money? get commercialValue;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'make')
  String get make;

  @BuiltValueField(wireName: r'model')
  String get model;

  @BuiltValueField(wireName: r'plate')
  String get plate;

  @BuiltValueField(wireName: r'usage')
  VehicleUsage get usage;
  // enum usageEnum {  PRIVATE,  RENTAL,  PASSENGER_TRANSPORT,  GOODS,  TAXI,  DRIVING_SCHOOL,  };

  @BuiltValueField(wireName: r'year')
  int get year;

  ProposalVehicle._();

  factory ProposalVehicle([void updates(ProposalVehicleBuilder b)]) = _$ProposalVehicle;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProposalVehicleBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProposalVehicle> get serializer => _$ProposalVehicleSerializer();
}

class _$ProposalVehicleSerializer implements PrimitiveSerializer<ProposalVehicle> {
  @override
  final Iterable<Type> types = const [ProposalVehicle, _$ProposalVehicle];

  @override
  final String wireName = r'ProposalVehicle';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProposalVehicle object, {
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
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
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
    ProposalVehicle object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProposalVehicleBuilder result,
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
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
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
  ProposalVehicle deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProposalVehicleBuilder();
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

