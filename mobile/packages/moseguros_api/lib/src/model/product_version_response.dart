//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:moseguros_api/src/model/product_document_response.dart';
import 'package:moseguros_api/src/model/coverage_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_version_response.g.dart';

/// ProductVersionResponse
///
/// Properties:
/// * [basedOnVersionId] 
/// * [changeNotes] 
/// * [coverages] 
/// * [createdAt] 
/// * [documents] 
/// * [editable] 
/// * [id] 
/// * [productCode] 
/// * [productId] 
/// * [productName] 
/// * [productType] 
/// * [publishedAt] 
/// * [publishedBy] 
/// * [status] 
/// * [updatedAt] 
/// * [usages] 
/// * [validFrom] 
/// * [validTo] 
/// * [vehicleCategories] 
/// * [version] 
/// * [versionNo] 
@BuiltValue()
abstract class ProductVersionResponse implements Built<ProductVersionResponse, ProductVersionResponseBuilder> {
  @BuiltValueField(wireName: r'basedOnVersionId')
  String? get basedOnVersionId;

  @BuiltValueField(wireName: r'changeNotes')
  String? get changeNotes;

  @BuiltValueField(wireName: r'coverages')
  BuiltList<CoverageResponse> get coverages;

  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'documents')
  BuiltList<ProductDocumentResponse> get documents;

  @BuiltValueField(wireName: r'editable')
  bool get editable;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'productCode')
  String get productCode;

  @BuiltValueField(wireName: r'productId')
  String get productId;

  @BuiltValueField(wireName: r'productName')
  String get productName;

  @BuiltValueField(wireName: r'productType')
  ProductVersionResponseProductTypeEnum get productType;
  // enum productTypeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'publishedAt')
  DateTime? get publishedAt;

  @BuiltValueField(wireName: r'publishedBy')
  String? get publishedBy;

  @BuiltValueField(wireName: r'status')
  ProductVersionResponseStatusEnum get status;
  // enum statusEnum {  DRAFT,  PUBLISHED,  SUPERSEDED,  RETIRED,  };

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  @BuiltValueField(wireName: r'usages')
  BuiltList<VehicleUsage> get usages;

  @BuiltValueField(wireName: r'validFrom')
  Date? get validFrom;

  @BuiltValueField(wireName: r'validTo')
  Date? get validTo;

  @BuiltValueField(wireName: r'vehicleCategories')
  BuiltList<VehicleCategory> get vehicleCategories;

  @BuiltValueField(wireName: r'version')
  int get version;

  @BuiltValueField(wireName: r'versionNo')
  int get versionNo;

  ProductVersionResponse._();

  factory ProductVersionResponse([void updates(ProductVersionResponseBuilder b)]) = _$ProductVersionResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductVersionResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductVersionResponse> get serializer => _$ProductVersionResponseSerializer();
}

class _$ProductVersionResponseSerializer implements PrimitiveSerializer<ProductVersionResponse> {
  @override
  final Iterable<Type> types = const [ProductVersionResponse, _$ProductVersionResponse];

  @override
  final String wireName = r'ProductVersionResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductVersionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.basedOnVersionId != null) {
      yield r'basedOnVersionId';
      yield serializers.serialize(
        object.basedOnVersionId,
        specifiedType: const FullType(String),
      );
    }
    if (object.changeNotes != null) {
      yield r'changeNotes';
      yield serializers.serialize(
        object.changeNotes,
        specifiedType: const FullType(String),
      );
    }
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(CoverageResponse)]),
    );
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'documents';
    yield serializers.serialize(
      object.documents,
      specifiedType: const FullType(BuiltList, [FullType(ProductDocumentResponse)]),
    );
    yield r'editable';
    yield serializers.serialize(
      object.editable,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'productCode';
    yield serializers.serialize(
      object.productCode,
      specifiedType: const FullType(String),
    );
    yield r'productId';
    yield serializers.serialize(
      object.productId,
      specifiedType: const FullType(String),
    );
    yield r'productName';
    yield serializers.serialize(
      object.productName,
      specifiedType: const FullType(String),
    );
    yield r'productType';
    yield serializers.serialize(
      object.productType,
      specifiedType: const FullType(ProductVersionResponseProductTypeEnum),
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
      specifiedType: const FullType(ProductVersionResponseStatusEnum),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'usages';
    yield serializers.serialize(
      object.usages,
      specifiedType: const FullType(BuiltList, [FullType(VehicleUsage)]),
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
    yield r'vehicleCategories';
    yield serializers.serialize(
      object.vehicleCategories,
      specifiedType: const FullType(BuiltList, [FullType(VehicleCategory)]),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
    yield r'versionNo';
    yield serializers.serialize(
      object.versionNo,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductVersionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductVersionResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'basedOnVersionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.basedOnVersionId = valueDes;
          break;
        case r'changeNotes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.changeNotes = valueDes;
          break;
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CoverageResponse)]),
          ) as BuiltList<CoverageResponse>;
          result.coverages.replace(valueDes);
          break;
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
        case r'documents':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ProductDocumentResponse)]),
          ) as BuiltList<ProductDocumentResponse>;
          result.documents.replace(valueDes);
          break;
        case r'editable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.editable = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'productCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productCode = valueDes;
          break;
        case r'productId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productId = valueDes;
          break;
        case r'productName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productName = valueDes;
          break;
        case r'productType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductVersionResponseProductTypeEnum),
          ) as ProductVersionResponseProductTypeEnum;
          result.productType = valueDes;
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
            specifiedType: const FullType(ProductVersionResponseStatusEnum),
          ) as ProductVersionResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'usages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleUsage)]),
          ) as BuiltList<VehicleUsage>;
          result.usages.replace(valueDes);
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
        case r'vehicleCategories':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(VehicleCategory)]),
          ) as BuiltList<VehicleCategory>;
          result.vehicleCategories.replace(valueDes);
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
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
  ProductVersionResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductVersionResponseBuilder();
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

class ProductVersionResponseProductTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const ProductVersionResponseProductTypeEnum MTPL_ONLY = _$productVersionResponseProductTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const ProductVersionResponseProductTypeEnum COMPREHENSIVE = _$productVersionResponseProductTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const ProductVersionResponseProductTypeEnum TEMPORARY = _$productVersionResponseProductTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const ProductVersionResponseProductTypeEnum BORDER = _$productVersionResponseProductTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const ProductVersionResponseProductTypeEnum FLEET = _$productVersionResponseProductTypeEnum_FLEET;

  static Serializer<ProductVersionResponseProductTypeEnum> get serializer => _$productVersionResponseProductTypeEnumSerializer;

  const ProductVersionResponseProductTypeEnum._(String name): super(name);

  static BuiltSet<ProductVersionResponseProductTypeEnum> get values => _$productVersionResponseProductTypeEnumValues;
  static ProductVersionResponseProductTypeEnum valueOf(String name) => _$productVersionResponseProductTypeEnumValueOf(name);
}

class ProductVersionResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const ProductVersionResponseStatusEnum DRAFT = _$productVersionResponseStatusEnum_DRAFT;
  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const ProductVersionResponseStatusEnum PUBLISHED = _$productVersionResponseStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'SUPERSEDED')
  static const ProductVersionResponseStatusEnum SUPERSEDED = _$productVersionResponseStatusEnum_SUPERSEDED;
  @BuiltValueEnumConst(wireName: r'RETIRED')
  static const ProductVersionResponseStatusEnum RETIRED = _$productVersionResponseStatusEnum_RETIRED;

  static Serializer<ProductVersionResponseStatusEnum> get serializer => _$productVersionResponseStatusEnumSerializer;

  const ProductVersionResponseStatusEnum._(String name): super(name);

  static BuiltSet<ProductVersionResponseStatusEnum> get values => _$productVersionResponseStatusEnumValues;
  static ProductVersionResponseStatusEnum valueOf(String name) => _$productVersionResponseStatusEnumValueOf(name);
}

