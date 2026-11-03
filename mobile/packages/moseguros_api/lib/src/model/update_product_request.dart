//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_product_request.g.dart';

/// UpdateProductRequest
///
/// Properties:
/// * [description] 
/// * [name] 
/// * [type] 
/// * [version] 
@BuiltValue()
abstract class UpdateProductRequest implements Built<UpdateProductRequest, UpdateProductRequestBuilder> {
  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'type')
  UpdateProductRequestTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateProductRequest._();

  factory UpdateProductRequest([void updates(UpdateProductRequestBuilder b)]) = _$UpdateProductRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateProductRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateProductRequest> get serializer => _$UpdateProductRequestSerializer();
}

class _$UpdateProductRequestSerializer implements PrimitiveSerializer<UpdateProductRequest> {
  @override
  final Iterable<Type> types = const [UpdateProductRequest, _$UpdateProductRequest];

  @override
  final String wireName = r'UpdateProductRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateProductRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType(String),
      );
    }
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(UpdateProductRequestTypeEnum),
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
    UpdateProductRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateProductRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.description = valueDes;
          break;
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UpdateProductRequestTypeEnum),
          ) as UpdateProductRequestTypeEnum;
          result.type = valueDes;
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
  UpdateProductRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateProductRequestBuilder();
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

class UpdateProductRequestTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const UpdateProductRequestTypeEnum MTPL_ONLY = _$updateProductRequestTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const UpdateProductRequestTypeEnum COMPREHENSIVE = _$updateProductRequestTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const UpdateProductRequestTypeEnum TEMPORARY = _$updateProductRequestTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const UpdateProductRequestTypeEnum BORDER = _$updateProductRequestTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const UpdateProductRequestTypeEnum FLEET = _$updateProductRequestTypeEnum_FLEET;

  static Serializer<UpdateProductRequestTypeEnum> get serializer => _$updateProductRequestTypeEnumSerializer;

  const UpdateProductRequestTypeEnum._(String name): super(name);

  static BuiltSet<UpdateProductRequestTypeEnum> get values => _$updateProductRequestTypeEnumValues;
  static UpdateProductRequestTypeEnum valueOf(String name) => _$updateProductRequestTypeEnumValueOf(name);
}

