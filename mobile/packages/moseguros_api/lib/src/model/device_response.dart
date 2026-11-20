//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/device_platform.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'device_response.g.dart';

/// DeviceResponse
///
/// Properties:
/// * [active] 
/// * [id] 
/// * [platform] 
@BuiltValue()
abstract class DeviceResponse implements Built<DeviceResponse, DeviceResponseBuilder> {
  @BuiltValueField(wireName: r'active')
  bool get active;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'platform')
  DevicePlatform get platform;
  // enum platformEnum {  ANDROID,  IOS,  };

  DeviceResponse._();

  factory DeviceResponse([void updates(DeviceResponseBuilder b)]) = _$DeviceResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(DeviceResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<DeviceResponse> get serializer => _$DeviceResponseSerializer();
}

class _$DeviceResponseSerializer implements PrimitiveSerializer<DeviceResponse> {
  @override
  final Iterable<Type> types = const [DeviceResponse, _$DeviceResponse];

  @override
  final String wireName = r'DeviceResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    DeviceResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'active';
    yield serializers.serialize(
      object.active,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'platform';
    yield serializers.serialize(
      object.platform,
      specifiedType: const FullType(DevicePlatform),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    DeviceResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required DeviceResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'active':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.active = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'platform':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DevicePlatform),
          ) as DevicePlatform;
          result.platform = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  DeviceResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = DeviceResponseBuilder();
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

