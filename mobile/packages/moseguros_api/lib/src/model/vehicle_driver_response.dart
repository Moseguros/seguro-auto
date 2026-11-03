//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'vehicle_driver_response.g.dart';

/// Condutores activos, o habitual primeiro
///
/// Properties:
/// * [driverId] 
/// * [fullName] 
/// * [habitual] 
@BuiltValue()
abstract class VehicleDriverResponse implements Built<VehicleDriverResponse, VehicleDriverResponseBuilder> {
  @BuiltValueField(wireName: r'driverId')
  String get driverId;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'habitual')
  bool get habitual;

  VehicleDriverResponse._();

  factory VehicleDriverResponse([void updates(VehicleDriverResponseBuilder b)]) = _$VehicleDriverResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VehicleDriverResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VehicleDriverResponse> get serializer => _$VehicleDriverResponseSerializer();
}

class _$VehicleDriverResponseSerializer implements PrimitiveSerializer<VehicleDriverResponse> {
  @override
  final Iterable<Type> types = const [VehicleDriverResponse, _$VehicleDriverResponse];

  @override
  final String wireName = r'VehicleDriverResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VehicleDriverResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'driverId';
    yield serializers.serialize(
      object.driverId,
      specifiedType: const FullType(String),
    );
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
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
    VehicleDriverResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VehicleDriverResponseBuilder result,
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
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
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
  VehicleDriverResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VehicleDriverResponseBuilder();
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

