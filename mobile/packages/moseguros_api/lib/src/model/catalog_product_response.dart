//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/catalog_insurer_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/date.dart';
import 'package:moseguros_api/src/model/vehicle_category.dart';
import 'package:moseguros_api/src/model/vehicle_usage.dart';
import 'package:moseguros_api/src/model/product_document_response.dart';
import 'package:moseguros_api/src/model/coverage_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'catalog_product_response.g.dart';

/// CatalogProductResponse
///
/// Properties:
/// * [code] 
/// * [coverages] 
/// * [description] 
/// * [documents] 
/// * [insurer] 
/// * [name] 
/// * [productId] 
/// * [productVersionId] 
/// * [status] 
/// * [type] 
/// * [usages] 
/// * [validFrom] 
/// * [validTo] 
/// * [vehicleCategories] 
/// * [versionNo] 
@BuiltValue()
abstract class CatalogProductResponse implements Built<CatalogProductResponse, CatalogProductResponseBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'coverages')
  BuiltList<CoverageResponse> get coverages;

  @BuiltValueField(wireName: r'description')
  String? get description;

  @BuiltValueField(wireName: r'documents')
  BuiltList<ProductDocumentResponse> get documents;

  @BuiltValueField(wireName: r'insurer')
  CatalogInsurerResponse get insurer;

  @BuiltValueField(wireName: r'name')
  String get name;

  @BuiltValueField(wireName: r'productId')
  String get productId;

  @BuiltValueField(wireName: r'productVersionId')
  String get productVersionId;

  @BuiltValueField(wireName: r'status')
  CatalogProductResponseStatusEnum get status;
  // enum statusEnum {  PUBLISHED,  SUPERSEDED,  RETIRED,  };

  @BuiltValueField(wireName: r'type')
  CatalogProductResponseTypeEnum get type;
  // enum typeEnum {  MTPL_ONLY,  COMPREHENSIVE,  TEMPORARY,  BORDER,  FLEET,  };

  @BuiltValueField(wireName: r'usages')
  BuiltList<VehicleUsage> get usages;

  @BuiltValueField(wireName: r'validFrom')
  Date get validFrom;

  @BuiltValueField(wireName: r'validTo')
  Date? get validTo;

  @BuiltValueField(wireName: r'vehicleCategories')
  BuiltList<VehicleCategory> get vehicleCategories;

  @BuiltValueField(wireName: r'versionNo')
  int get versionNo;

  CatalogProductResponse._();

  factory CatalogProductResponse([void updates(CatalogProductResponseBuilder b)]) = _$CatalogProductResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(CatalogProductResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<CatalogProductResponse> get serializer => _$CatalogProductResponseSerializer();
}

class _$CatalogProductResponseSerializer implements PrimitiveSerializer<CatalogProductResponse> {
  @override
  final Iterable<Type> types = const [CatalogProductResponse, _$CatalogProductResponse];

  @override
  final String wireName = r'CatalogProductResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    CatalogProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'coverages';
    yield serializers.serialize(
      object.coverages,
      specifiedType: const FullType(BuiltList, [FullType(CoverageResponse)]),
    );
    if (object.description != null) {
      yield r'description';
      yield serializers.serialize(
        object.description,
        specifiedType: const FullType(String),
      );
    }
    yield r'documents';
    yield serializers.serialize(
      object.documents,
      specifiedType: const FullType(BuiltList, [FullType(ProductDocumentResponse)]),
    );
    yield r'insurer';
    yield serializers.serialize(
      object.insurer,
      specifiedType: const FullType(CatalogInsurerResponse),
    );
    yield r'name';
    yield serializers.serialize(
      object.name,
      specifiedType: const FullType(String),
    );
    yield r'productId';
    yield serializers.serialize(
      object.productId,
      specifiedType: const FullType(String),
    );
    yield r'productVersionId';
    yield serializers.serialize(
      object.productVersionId,
      specifiedType: const FullType(String),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(CatalogProductResponseStatusEnum),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(CatalogProductResponseTypeEnum),
    );
    yield r'usages';
    yield serializers.serialize(
      object.usages,
      specifiedType: const FullType(BuiltList, [FullType(VehicleUsage)]),
    );
    yield r'validFrom';
    yield serializers.serialize(
      object.validFrom,
      specifiedType: const FullType(Date),
    );
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
    yield r'versionNo';
    yield serializers.serialize(
      object.versionNo,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    CatalogProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required CatalogProductResponseBuilder result,
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
        case r'coverages':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CoverageResponse)]),
          ) as BuiltList<CoverageResponse>;
          result.coverages.replace(valueDes);
          break;
        case r'description':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.description = valueDes;
          break;
        case r'documents':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(ProductDocumentResponse)]),
          ) as BuiltList<ProductDocumentResponse>;
          result.documents.replace(valueDes);
          break;
        case r'insurer':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CatalogInsurerResponse),
          ) as CatalogInsurerResponse;
          result.insurer.replace(valueDes);
          break;
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        case r'productId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productId = valueDes;
          break;
        case r'productVersionId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.productVersionId = valueDes;
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CatalogProductResponseStatusEnum),
          ) as CatalogProductResponseStatusEnum;
          result.status = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CatalogProductResponseTypeEnum),
          ) as CatalogProductResponseTypeEnum;
          result.type = valueDes;
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
  CatalogProductResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = CatalogProductResponseBuilder();
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

class CatalogProductResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PUBLISHED')
  static const CatalogProductResponseStatusEnum PUBLISHED = _$catalogProductResponseStatusEnum_PUBLISHED;
  @BuiltValueEnumConst(wireName: r'SUPERSEDED')
  static const CatalogProductResponseStatusEnum SUPERSEDED = _$catalogProductResponseStatusEnum_SUPERSEDED;
  @BuiltValueEnumConst(wireName: r'RETIRED')
  static const CatalogProductResponseStatusEnum RETIRED = _$catalogProductResponseStatusEnum_RETIRED;

  static Serializer<CatalogProductResponseStatusEnum> get serializer => _$catalogProductResponseStatusEnumSerializer;

  const CatalogProductResponseStatusEnum._(String name): super(name);

  static BuiltSet<CatalogProductResponseStatusEnum> get values => _$catalogProductResponseStatusEnumValues;
  static CatalogProductResponseStatusEnum valueOf(String name) => _$catalogProductResponseStatusEnumValueOf(name);
}

class CatalogProductResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MTPL_ONLY')
  static const CatalogProductResponseTypeEnum MTPL_ONLY = _$catalogProductResponseTypeEnum_MTPL_ONLY;
  @BuiltValueEnumConst(wireName: r'COMPREHENSIVE')
  static const CatalogProductResponseTypeEnum COMPREHENSIVE = _$catalogProductResponseTypeEnum_COMPREHENSIVE;
  @BuiltValueEnumConst(wireName: r'TEMPORARY')
  static const CatalogProductResponseTypeEnum TEMPORARY = _$catalogProductResponseTypeEnum_TEMPORARY;
  @BuiltValueEnumConst(wireName: r'BORDER')
  static const CatalogProductResponseTypeEnum BORDER = _$catalogProductResponseTypeEnum_BORDER;
  @BuiltValueEnumConst(wireName: r'FLEET')
  static const CatalogProductResponseTypeEnum FLEET = _$catalogProductResponseTypeEnum_FLEET;

  static Serializer<CatalogProductResponseTypeEnum> get serializer => _$catalogProductResponseTypeEnumSerializer;

  const CatalogProductResponseTypeEnum._(String name): super(name);

  static BuiltSet<CatalogProductResponseTypeEnum> get values => _$catalogProductResponseTypeEnumValues;
  static CatalogProductResponseTypeEnum valueOf(String name) => _$catalogProductResponseTypeEnumValueOf(name);
}

