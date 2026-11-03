//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/platform_setting_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'platform_settings_response.g.dart';

/// PlatformSettingsResponse
///
/// Properties:
/// * [settings] 
@BuiltValue()
abstract class PlatformSettingsResponse implements Built<PlatformSettingsResponse, PlatformSettingsResponseBuilder> {
  @BuiltValueField(wireName: r'settings')
  BuiltList<PlatformSettingResponse> get settings;

  PlatformSettingsResponse._();

  factory PlatformSettingsResponse([void updates(PlatformSettingsResponseBuilder b)]) = _$PlatformSettingsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PlatformSettingsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PlatformSettingsResponse> get serializer => _$PlatformSettingsResponseSerializer();
}

class _$PlatformSettingsResponseSerializer implements PrimitiveSerializer<PlatformSettingsResponse> {
  @override
  final Iterable<Type> types = const [PlatformSettingsResponse, _$PlatformSettingsResponse];

  @override
  final String wireName = r'PlatformSettingsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PlatformSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'settings';
    yield serializers.serialize(
      object.settings,
      specifiedType: const FullType(BuiltList, [FullType(PlatformSettingResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PlatformSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PlatformSettingsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'settings':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PlatformSettingResponse)]),
          ) as BuiltList<PlatformSettingResponse>;
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
  PlatformSettingsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PlatformSettingsResponseBuilder();
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

