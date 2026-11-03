//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_version_ref_response.g.dart';

/// ProductVersionRefResponse
///
/// Properties:
/// * [changeNotes] 
/// * [id] 
/// * [publishedAt] 
/// * [publishedBy] 
/// * [status] 
/// * [validFrom] 
/// * [validTo] 
/// * [versionNo] 
@BuiltValue()
abstract class ProductVersionRefResponse implements Built<ProductVersionRefResponse, ProductVersionRefResponseBuilder> {
  @BuiltValueField(wireName: r'changeNotes')
  String? get changeNotes;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'publishedAt')
  DateTime? get publishedAt;

  @BuiltValueField(wireName: r'publishedBy')
  String? get publishedBy;

  @BuiltValueField(wireName: r'status')
  ProductVersionRefResponseStatusEnum get status;
  // enum statusEnum {  DRAFT,  PUBLISHED,  SUPERSEDED,  RETIRED,  };

  @BuiltValueField(wireName: r'validFrom')
  Date? get validFrom;

  @BuiltValueField(wireName: r'validTo')
  Date? get validTo;

  @BuiltValueField(wireName: r'versionNo')
  int get versionNo;

  ProductVersionRefResponse._();

  factory ProductVersionRefResponse([void updates(ProductVersionRefResponseBuilder b)]) = _$ProductVersionRefResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductVersionRefResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductVersionRefResponse> get serializer => _$ProductVersionRefResponseSerializer();
}

class _$ProductVersionRefResponseSerializer implements PrimitiveSerializer<ProductVersionRefResponse> {
  @override
  final Iterable<Type> types = const [ProductVersionRefResponse, _$ProductVersionRefResponse];

  @override
  final String wireName = r'ProductVersionRefResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductVersionRefResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.changeNotes != null) {
      yield r'changeNotes';
      yield serializers.serialize(
        object.changeNotes,
        specifiedType: const FullType(String),
      );
    }
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.publishedAt != null) {
      yield r'publishedAt';
      yield serializers.serialize(
        object.publishedAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.publishedBy != null) {
      yield r'publishedBy';
      yield serializers.serialize(
        object.publishedBy,
        specifiedType: const FullType(String),
      );
    }
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(ProductVersionRefResponseStatusEnum),
    );
    if (object.validFrom != null) {
      yield r'validFrom';
      yield serializers.serialize(
        object.validFrom,
        specifiedType: const FullType(Date),
      );
    }
    if (object.validTo != null) {
      yield r'validTo';
      yield serializers.serialize(
        object.validTo,
        specifiedType: const FullType(Date),
      );
    }
    yield r'versionNo';
    yield serializers.serialize(
      object.versionNo,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductVersionRefResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductVersionRefResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'changeNotes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.changeNotes = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'publishedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.publishedAt = valueDes;
          break;
        case r'publishedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.publishedBy = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductVersionRefResponseStatusEnum),
          ) as ProductVersionRefResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'validFrom':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.validFrom = valueDes;
          break;
        case r'validTo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Date),
          ) as Date;
          result.validTo = valueDes;
          break;
        case r'versionNo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.versionNo = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProductVersionRefResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductVersionRefResponseBuilder();
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

class ProductVersionRefResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const ProductVersionRefResponseStatusEnum DRAFT = _$productVersionRefResponseStatusEnum_DRAFT;
  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const ProductVersionRefResponseStatusEnum PUBLISHED = _$productVersionRefResponseStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'SUPERSEDED')
  static const ProductVersionRefResponseStatusEnum SUPERSEDED = _$productVersionRefResponseStatusEnum_SUPERSEDED;
  @BuiltValueEnumConst(wireName: r'RETIRED')
  static const ProductVersionRefResponseStatusEnum RETIRED = _$productVersionRefResponseStatusEnum_RETIRED;

  static Serializer<ProductVersionRefResponseStatusEnum> get serializer => _$productVersionRefResponseStatusEnumSerializer;

  const ProductVersionRefResponseStatusEnum._(String name): super(name);

  static BuiltSet<ProductVersionRefResponseStatusEnum> get values => _$productVersionRefResponseStatusEnumValues;
  static ProductVersionRefResponseStatusEnum valueOf(String name) => _$productVersionRefResponseStatusEnumValueOf(name);
}

