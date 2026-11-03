//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_platform_setting_request.g.dart';

/// UpdatePlatformSettingRequest
///
/// Properties:
/// * [value] 
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdatePlatformSettingRequest implements Built<UpdatePlatformSettingRequest, UpdatePlatformSettingRequestBuilder> {
  @BuiltValueField(wireName: r'value')
  String get value;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdatePlatformSettingRequest._();

  factory UpdatePlatformSettingRequest([void updates(UpdatePlatformSettingRequestBuilder b)]) = _$UpdatePlatformSettingRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdatePlatformSettingRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdatePlatformSettingRequest> get serializer => _$UpdatePlatformSettingRequestSerializer();
}

class _$UpdatePlatformSettingRequestSerializer implements PrimitiveSerializer<UpdatePlatformSettingRequest> {
  @override
  final Iterable<Type> types = const [UpdatePlatformSettingRequest, _$UpdatePlatformSettingRequest];

  @override
  final String wireName = r'UpdatePlatformSettingRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdatePlatformSettingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(String),
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
    UpdatePlatformSettingRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdatePlatformSettingRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.value = valueDes;
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
  UpdatePlatformSettingRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdatePlatformSettingRequestBuilder();
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

