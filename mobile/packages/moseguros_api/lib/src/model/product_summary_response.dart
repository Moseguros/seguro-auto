//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_summary_response.g.dart';

/// ProductSummaryResponse
///
/// Properties:
/// * [code] 
/// * [currentVersionNo] 
/// * [hasDraft] 
/// * [id] 
/// * [name] 
/// * [status] 
/// * [type] 
/// * [updatedAt] 
@BuiltValue()
abstract class ProductSummaryResponse implements Built<ProductSummaryResponse, ProductSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'currentVersionNo')
  int? get currentVersionNo;

  @BuiltValueField(wireName: r'hasDraft')
  bool get hasDraft;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'status')
  ProductSummaryResponseStatusEnum get status;
  // enum statusEnum {  DRAFT,  PUBLISHED,  RETIRED,  };

  @BuiltValueField(wireName: r'type')
  ProductSummaryResponseTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  ProductSummaryResponse._();

  factory ProductSummaryResponse([void updates(ProductSummaryResponseBuilder b)]) = _$ProductSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductSummaryResponse> get serializer => _$ProductSummaryResponseSerializer();
}

class _$ProductSummaryResponseSerializer implements PrimitiveSerializer<ProductSummaryResponse> {
  @override
  final Iterable<Type> types = const [ProductSummaryResponse, _$ProductSummaryResponse];

  @override
  final String wireName = r'ProductSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    if (object.currentVersionNo != null) {
      yield r'currentVersionNo';
      yield serializers.serialize(
        object.currentVersionNo,
        specifiedType: const FullType(int),
      );
    }
    yield r'hasDraft';
    yield serializers.serialize(
      object.hasDraft,
      specifiedType: const FullType(bool),
    );
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
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProductSummaryResponseStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(ProductSummaryResponseTypeEnum),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductSummaryResponseBuilder result,
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
        case r'currentVersionNo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.currentVersionNo = valueDes;
          break;
        case r'hasDraft':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.hasDraft = valueDes;
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
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductSummaryResponseStatusEnum),
          ) as ProductSummaryResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductSummaryResponseTypeEnum),
          ) as ProductSummaryResponseTypeEnum;
          result.type = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProductSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductSummaryResponseBuilder();
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

class ProductSummaryResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const ProductSummaryResponseStatusEnum DRAFT = _$productSummaryResponseStatusEnum_DRAFT;
  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const ProductSummaryResponseStatusEnum PUBLISHED = _$productSummaryResponseStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'RETIRED')
  static const ProductSummaryResponseStatusEnum RETIRED = _$productSummaryResponseStatusEnum_RETIRED;

  static Serializer<ProductSummaryResponseStatusEnum> get serializer => _$productSummaryResponseStatusEnumSerializer;

  const ProductSummaryResponseStatusEnum._(String name): super(name);

  static BuiltSet<ProductSummaryResponseStatusEnum> get values => _$productSummaryResponseStatusEnumValues;
  static ProductSummaryResponseStatusEnum valueOf(String name) => _$productSummaryResponseStatusEnumValueOf(name);
}

class ProductSummaryResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const ProductSummaryResponseTypeEnum MTPL_ONLY = _$productSummaryResponseTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const ProductSummaryResponseTypeEnum COMPREHENSIVE = _$productSummaryResponseTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const ProductSummaryResponseTypeEnum TEMPORARY = _$productSummaryResponseTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const ProductSummaryResponseTypeEnum BORDER = _$productSummaryResponseTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const ProductSummaryResponseTypeEnum FLEET = _$productSummaryResponseTypeEnum_FLEET;

  static Serializer<ProductSummaryResponseTypeEnum> get serializer => _$productSummaryResponseTypeEnumSerializer;

  const ProductSummaryResponseTypeEnum._(String name): super(name);

  static BuiltSet<ProductSummaryResponseTypeEnum> get values => _$productSummaryResponseTypeEnumValues;
  static ProductSummaryResponseTypeEnum valueOf(String name) => _$productSummaryResponseTypeEnumValueOf(name);
}

