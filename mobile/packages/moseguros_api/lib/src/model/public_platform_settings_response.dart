//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/public_platform_setting_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_platform_settings_response.g.dart';

/// PublicPlatformSettingsResponse
///
/// Properties:
/// * [settings] 
@BuiltValue()
abstract class PublicPlatformSettingsResponse implements Built<PublicPlatformSettingsResponse, PublicPlatformSettingsResponseBuilder> {
  @BuiltValueField(wireName: r'settings')
  BuiltList<PublicPlatformSettingResponse> get settings;

  PublicPlatformSettingsResponse._();

  factory PublicPlatformSettingsResponse([void updates(PublicPlatformSettingsResponseBuilder b)]) = _$PublicPlatformSettingsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicPlatformSettingsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicPlatformSettingsResponse> get serializer => _$PublicPlatformSettingsResponseSerializer();
}

class _$PublicPlatformSettingsResponseSerializer implements PrimitiveSerializer<PublicPlatformSettingsResponse> {
  @override
  final Iterable<Type> types = const [PublicPlatformSettingsResponse, _$PublicPlatformSettingsResponse];

  @override
  final String wireName = r'PublicPlatformSettingsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicPlatformSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'settings';
    yield serializers.serialize(
      object.settings,
      specifiedType: const FullType(BuiltList, [FullType(PublicPlatformSettingResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicPlatformSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicPlatformSettingsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'settings':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PublicPlatformSettingResponse)]),
          ) as BuiltList<PublicPlatformSettingResponse>;
          result.settings.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicPlatformSettingsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicPlatformSettingsResponseBuilder();
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

