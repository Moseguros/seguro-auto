//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'vehicle_driver_assignment.g.dart';

/// Até 5 condutores; vazia = sem condutores
///
/// Properties:
/// * [driverId] - Condutor activo do cliente; senão 404 DRIVER_NOT_FOUND
/// * [habitual] 
@BuiltValue()
abstract class VehicleDriverAssignment implements Built<VehicleDriverAssignment, VehicleDriverAssignmentBuilder> {
  /// Condutor activo do cliente; senão 404 DRIVER_NOT_FOUND
  @BuiltValueField(wireName: r'driverId')
  String get driverId;

  @BuiltValueField(wireName: r'habitual')
  bool get habitual;

  VehicleDriverAssignment._();

  factory VehicleDriverAssignment([void updates(VehicleDriverAssignmentBuilder b)]) = _$VehicleDriverAssignment;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VehicleDriverAssignmentBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VehicleDriverAssignment> get serializer => _$VehicleDriverAssignmentSerializer();
}

class _$VehicleDriverAssignmentSerializer implements PrimitiveSerializer<VehicleDriverAssignment> {
  @override
  final Iterable<Type> types = const [VehicleDriverAssignment, _$VehicleDriverAssignment];

  @override
  final String wireName = r'VehicleDriverAssignment';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VehicleDriverAssignment object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'driverId';
    yield serializers.serialize(
      object.driverId,
      specifiedType: const FullType(String),
    );
    yield r'habitual';
    yield serializers.serialize(
      object.habitual,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    VehicleDriverAssignment object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VehicleDriverAssignmentBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'driverId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.driverId = valueDes;
          break;
        case r'habitual':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.habitual = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  VehicleDriverAssignment deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VehicleDriverAssignmentBuilder();
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

