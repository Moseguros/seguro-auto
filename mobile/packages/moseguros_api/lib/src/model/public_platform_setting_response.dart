//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_platform_setting_response.g.dart';

/// PublicPlatformSettingResponse
///
/// Properties:
/// * [key] 
/// * [value] 
/// * [valueType] 
@BuiltValue()
abstract class PublicPlatformSettingResponse implements Built<PublicPlatformSettingResponse, PublicPlatformSettingResponseBuilder> {
  @BuiltValueField(wireName: r'key')
  String get key;

  @BuiltValueField(wireName: r'value')
  String get value;

  @BuiltValueField(wireName: r'valueType')
  PublicPlatformSettingResponseValueTypeEnum get valueType;
  // enum valueTypeEnum {  MONEY,  INTEGER,  BOOLEAN,  STRING,  };

  PublicPlatformSettingResponse._();

  factory PublicPlatformSettingResponse([void updates(PublicPlatformSettingResponseBuilder b)]) = _$PublicPlatformSettingResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicPlatformSettingResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicPlatformSettingResponse> get serializer => _$PublicPlatformSettingResponseSerializer();
}

class _$PublicPlatformSettingResponseSerializer implements PrimitiveSerializer<PublicPlatformSettingResponse> {
  @override
  final Iterable<Type> types = const [PublicPlatformSettingResponse, _$PublicPlatformSettingResponse];

  @override
  final String wireName = r'PublicPlatformSettingResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicPlatformSettingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'key';
    yield serializers.serialize(
      object.key,
      specifiedType: const FullType(String),
    );
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(String),
    );
    yield r'valueType';
    yield serializers.serialize(
      object.valueType,
      specifiedType: const FullType(PublicPlatformSettingResponseValueTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicPlatformSettingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicPlatformSettingResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'key':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.key = valueDes;
          break;
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.value = valueDes;
          break;
        case r'valueType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PublicPlatformSettingResponseValueTypeEnum),
          ) as PublicPlatformSettingResponseValueTypeEnum;
          result.valueType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicPlatformSettingResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicPlatformSettingResponseBuilder();
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

class PublicPlatformSettingResponseValueTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MONEY')
  static const PublicPlatformSettingResponseValueTypeEnum MONEY = _$publicPlatformSettingResponseValueTypeEnum_MONEY;
  @BuiltValueEnumConst(wireName: r'INTEGER')
  static const PublicPlatformSettingResponseValueTypeEnum INTEGER = _$publicPlatformSettingResponseValueTypeEnum_INTEGER;
  @BuiltValueEnumConst(wireName: r'BOOLEAN')
  static const PublicPlatformSettingResponseValueTypeEnum BOOLEAN = _$publicPlatformSettingResponseValueTypeEnum_BOOLEAN;
  @BuiltValueEnumConst(wireName: r'STRING')
  static const PublicPlatformSettingResponseValueTypeEnum STRING = _$publicPlatformSettingResponseValueTypeEnum_STRING;

  static Serializer<PublicPlatformSettingResponseValueTypeEnum> get serializer => _$publicPlatformSettingResponseValueTypeEnumSerializer;

  const PublicPlatformSettingResponseValueTypeEnum._(String name): super(name);

  static BuiltSet<PublicPlatformSettingResponseValueTypeEnum> get values => _$publicPlatformSettingResponseValueTypeEnumValues;
  static PublicPlatformSettingResponseValueTypeEnum valueOf(String name) => _$publicPlatformSettingResponseValueTypeEnumValueOf(name);
}

