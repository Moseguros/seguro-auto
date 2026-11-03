//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/vehicle_driver_assignment.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_vehicle_drivers_request.g.dart';

/// UpdateVehicleDriversRequest
///
/// Properties:
/// * [drivers] - Até 5 condutores; vazia = sem condutores
/// * [version] - Versão do veículo lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateVehicleDriversRequest implements Built<UpdateVehicleDriversRequest, UpdateVehicleDriversRequestBuilder> {
  /// Até 5 condutores; vazia = sem condutores
  @BuiltValueField(wireName: r'drivers')
  BuiltList<VehicleDriverAssignment> get drivers;

  /// Versão do veículo lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateVehicleDriversRequest._();

  factory UpdateVehicleDriversRequest([void updates(UpdateVehicleDriversRequestBuilder b)]) = _$UpdateVehicleDriversRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateVehicleDriversRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateVehicleDriversRequest> get serializer => _$UpdateVehicleDriversRequestSerializer();
}

class _$UpdateVehicleDriversRequestSerializer implements PrimitiveSerializer<UpdateVehicleDriversRequest> {
  @override
  final Iterable<Type> types = const [UpdateVehicleDriversRequest, _$UpdateVehicleDriversRequest];

  @override
  final String wireName = r'UpdateVehicleDriversRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateVehicleDriversRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'drivers';
    yield serializers.serialize(
      object.drivers,
      specifiedType: const FullType(BuiltList, [FullType(VehicleDriverAssignment)]),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    UpdateVehicleDriversRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateVehicleDriversRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'drivers':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleDriverAssignment)]),
          ) as BuiltList<VehicleDriverAssignment>;
          result.drivers.replace(valueDes);
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  UpdateVehicleDriversRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateVehicleDriversRequestBuilder();
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

