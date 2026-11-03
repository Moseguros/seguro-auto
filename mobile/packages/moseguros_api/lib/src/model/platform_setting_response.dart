//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'platform_setting_response.g.dart';

/// PlatformSettingResponse
///
/// Properties:
/// * [key] 
/// * [updatedAt] 
/// * [updatedBy] 
/// * [value] 
/// * [valueType] 
/// * [version] 
@BuiltValue()
abstract class PlatformSettingResponse implements Built<PlatformSettingResponse, PlatformSettingResponseBuilder> {
  @BuiltValueField(wireName: r'key')
  String get key;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'updatedBy')
  String get updatedBy;

  @BuiltValueField(wireName: r'value')
  String get value;

  @BuiltValueField(wireName: r'valueType')
  PlatformSettingResponseValueTypeEnum get valueType;
  // enum valueTypeEnum {  MONEY,  INTEGER,  BOOLEAN,  STRING,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  PlatformSettingResponse._();

  factory PlatformSettingResponse([void updates(PlatformSettingResponseBuilder b)]) = _$PlatformSettingResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PlatformSettingResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PlatformSettingResponse> get serializer => _$PlatformSettingResponseSerializer();
}

class _$PlatformSettingResponseSerializer implements PrimitiveSerializer<PlatformSettingResponse> {
  @override
  final Iterable<Type> types = const [PlatformSettingResponse, _$PlatformSettingResponse];

  @override
  final String wireName = r'PlatformSettingResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PlatformSettingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'key';
    yield serializers.serialize(
      object.key,
      specifiedType: const FullType(String),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'updatedBy';
    yield serializers.serialize(
      object.updatedBy,
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
      specifiedType: const FullType(PlatformSettingResponseValueTypeEnum),
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
    PlatformSettingResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PlatformSettingResponseBuilder result,
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
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'updatedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.updatedBy = valueDes;
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
            specifiedType: const FullType(PlatformSettingResponseValueTypeEnum),
          ) as PlatformSettingResponseValueTypeEnum;
          result.valueType = valueDes;
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
  PlatformSettingResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PlatformSettingResponseBuilder();
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

class PlatformSettingResponseValueTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MONEY')
  static const PlatformSettingResponseValueTypeEnum MONEY = _$platformSettingResponseValueTypeEnum_MONEY;
  @BuiltValueEnumConst(wireName: r'INTEGER')
  static const PlatformSettingResponseValueTypeEnum INTEGER = _$platformSettingResponseValueTypeEnum_INTEGER;
  @BuiltValueEnumConst(wireName: r'BOOLEAN')
  static const PlatformSettingResponseValueTypeEnum BOOLEAN = _$platformSettingResponseValueTypeEnum_BOOLEAN;
  @BuiltValueEnumConst(wireName: r'STRING')
  static const PlatformSettingResponseValueTypeEnum STRING = _$platformSettingResponseValueTypeEnum_STRING;

  static Serializer<PlatformSettingResponseValueTypeEnum> get serializer => _$platformSettingResponseValueTypeEnumSerializer;

  const PlatformSettingResponseValueTypeEnum._(String name): super(name);

  static BuiltSet<PlatformSettingResponseValueTypeEnum> get values => _$platformSettingResponseValueTypeEnumValues;
  static PlatformSettingResponseValueTypeEnum valueOf(String name) => _$platformSettingResponseValueTypeEnumValueOf(name);
}

