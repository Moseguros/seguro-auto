//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/product_version_ref_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_response.g.dart';

/// ProductResponse
///
/// Properties:
/// * [code] 
/// * [createdAt] 
/// * [currentVersion] 
/// * [description] 
/// * [draftVersion] 
/// * [id] 
/// * [name] 
/// * [retiredAt] 
/// * [status] 
/// * [type] 
/// * [updatedAt] 
/// * [version] 
/// * [versions] 
@BuiltValue()
abstract class ProductResponse implements Built<ProductResponse, ProductResponseBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'currentVersion')
  ProductVersionRefResponse? get currentVersion;

  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'draftVersion')
  ProductVersionRefResponse? get draftVersion;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'retiredAt')
  DateTime? get retiredAt;

  @BuiltValueField(wireName: r'status')
  ProductResponseStatusEnum get status;
  // enum statusEnum {  DRAFT,  PUBLISHED,  RETIRED,  };

  @BuiltValueField(wireName: r'type')
  ProductResponseTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'versions')
  BuiltList<ProductVersionRefResponse> get versions;

  ProductResponse._();

  factory ProductResponse([void updates(ProductResponseBuilder b)]) = _$ProductResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductResponse> get serializer => _$ProductResponseSerializer();
}

class _$ProductResponseSerializer implements PrimitiveSerializer<ProductResponse> {
  @override
  final Iterable<Type> types = const [ProductResponse, _$ProductResponse];

  @override
  final String wireName = r'ProductResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.currentVersion != null) {
      yield r'currentVersion';
      yield serializers.serialize(
        object.currentVersion,
        specifiedType: const FullType(ProductVersionRefResponse),
      );
    }
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType(String),
      );
    }
    if (object.draftVersion != null) {
      yield r'draftVersion';
      yield serializers.serialize(
        object.draftVersion,
        specifiedType: const FullType(ProductVersionRefResponse),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    if (object.retiredAt != null) {
      yield r'retiredAt';
      yield serializers.serialize(
        object.retiredAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProductResponseStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ProductResponseTypeEnum),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
    yield r'versions';
    yield serializers.serialize(
      object.versions,
      specifiedType: const FullType(BuiltList, [FullType(ProductVersionRefResponse)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'currentVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductVersionRefResponse),
          ) as ProductVersionRefResponse;
          result.currentVersion.replace(valueDes);
          break;
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.description = valueDes;
          break;
        case r'draftVersion':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductVersionRefResponse),
          ) as ProductVersionRefResponse;
          result.draftVersion.replace(valueDes);
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        case r'retiredAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.retiredAt = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductResponseStatusEnum),
          ) as ProductResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductResponseTypeEnum),
          ) as ProductResponseTypeEnum;
          result.type = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        case r'versions':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ProductVersionRefResponse)]),
          ) as BuiltList<ProductVersionRefResponse>;
          result.versions.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProductResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductResponseBuilder();
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

class ProductResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const ProductResponseStatusEnum DRAFT = _$productResponseStatusEnum_DRAFT;
  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const ProductResponseStatusEnum PUBLISHED = _$productResponseStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'RETIRED')
  static const ProductResponseStatusEnum RETIRED = _$productResponseStatusEnum_RETIRED;

  static Serializer<ProductResponseStatusEnum> get serializer => _$productResponseStatusEnumSerializer;

  const ProductResponseStatusEnum._(String name): super(name);

  static BuiltSet<ProductResponseStatusEnum> get values => _$productResponseStatusEnumValues;
  static ProductResponseStatusEnum valueOf(String name) => _$productResponseStatusEnumValueOf(name);
}

class ProductResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const ProductResponseTypeEnum MTPL_ONLY = _$productResponseTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const ProductResponseTypeEnum COMPREHENSIVE = _$productResponseTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const ProductResponseTypeEnum TEMPORARY = _$productResponseTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const ProductResponseTypeEnum BORDER = _$productResponseTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const ProductResponseTypeEnum FLEET = _$productResponseTypeEnum_FLEET;

  static Serializer<ProductResponseTypeEnum> get serializer => _$productResponseTypeEnumSerializer;

  const ProductResponseTypeEnum._(String name): super(name);

  static BuiltSet<ProductResponseTypeEnum> get values => _$productResponseTypeEnumValues;
  static ProductResponseTypeEnum valueOf(String name) => _$productResponseTypeEnumValueOf(name);
}

