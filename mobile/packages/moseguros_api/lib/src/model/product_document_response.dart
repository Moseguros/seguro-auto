//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_document_response.g.dart';

/// ProductDocumentResponse
///
/// Properties:
/// * [docType] 
/// * [fileId] 
/// * [id] 
/// * [language] 
/// * [title] 
@BuiltValue()
abstract class ProductDocumentResponse implements Built<ProductDocumentResponse, ProductDocumentResponseBuilder> {
  @BuiltValueField(wireName: r'docType')
  ProductDocumentResponseDocTypeEnum get docType;
  // enum docTypeEnum {  GENERAL_CONDITIONS,  PRE_CONTRACT_INFO,  SPECIAL_CONDITIONS,  OTHER,  };

  @BuiltValueField(wireName: r'fileId')
  String get fileId;

  @BuiltValueField(wireName: r'id')
  String get id;

  @BuiltValueField(wireName: r'language')
  ProductDocumentResponseLanguageEnum get language;
  // enum languageEnum {  pt,  en,  };

  @BuiltValueField(wireName: r'title')
  String? get title;

  ProductDocumentResponse._();

  factory ProductDocumentResponse([void updates(ProductDocumentResponseBuilder b)]) = _$ProductDocumentResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductDocumentResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductDocumentResponse> get serializer => _$ProductDocumentResponseSerializer();
}

class _$ProductDocumentResponseSerializer implements PrimitiveSerializer<ProductDocumentResponse> {
  @override
  final Iterable<Type> types = const [ProductDocumentResponse, _$ProductDocumentResponse];

  @override
  final String wireName = r'ProductDocumentResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductDocumentResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'docType';
    yield serializers.serialize(
      object.docType,
      specifiedType: const FullType(ProductDocumentResponseDocTypeEnum),
    );
    yield r'fileId';
    yield serializers.serialize(
      object.fileId,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    yield r'language';
    yield serializers.serialize(
      object.language,
      specifiedType: const FullType(ProductDocumentResponseLanguageEnum),
    );
    if (object.title != null) {
      yield r'title';
      yield serializers.serialize(
        object.title,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductDocumentResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductDocumentResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'docType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductDocumentResponseDocTypeEnum),
          ) as ProductDocumentResponseDocTypeEnum;
          result.docType = valueDes;
          break;
        case r'fileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fileId = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'language':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(ProductDocumentResponseLanguageEnum),
          ) as ProductDocumentResponseLanguageEnum;
          result.language = valueDes;
          break;
        case r'title':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.title = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProductDocumentResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductDocumentResponseBuilder();
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

class ProductDocumentResponseDocTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'GENERAL_CONDITIONS')
  static const ProductDocumentResponseDocTypeEnum GENERAL_CONDITIONS = _$productDocumentResponseDocTypeEnum_GENERAL_CONDITIONS;
  @BuiltValueEnumConst(wireName: r'PRE_CONTRACT_INFO')
  static const ProductDocumentResponseDocTypeEnum PRE_CONTRACT_INFO = _$productDocumentResponseDocTypeEnum_PRE_CONTRACT_INFO;
  @BuiltValueEnumConst(wireName: r'SPECIAL_CONDITIONS')
  static const ProductDocumentResponseDocTypeEnum SPECIAL_CONDITIONS = _$productDocumentResponseDocTypeEnum_SPECIAL_CONDITIONS;
  @BuiltValueEnumConst(wireName: r'OTHER')
  static const ProductDocumentResponseDocTypeEnum OTHER = _$productDocumentResponseDocTypeEnum_OTHER;

  static Serializer<ProductDocumentResponseDocTypeEnum> get serializer => _$productDocumentResponseDocTypeEnumSerializer;

  const ProductDocumentResponseDocTypeEnum._(String name): super(name);

  static BuiltSet<ProductDocumentResponseDocTypeEnum> get values => _$productDocumentResponseDocTypeEnumValues;
  static ProductDocumentResponseDocTypeEnum valueOf(String name) => _$productDocumentResponseDocTypeEnumValueOf(name);
}

class ProductDocumentResponseLanguageEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'pt')
  static const ProductDocumentResponseLanguageEnum pt = _$productDocumentResponseLanguageEnum_pt;
  @BuiltValueEnumConst(wireName: r'en')
  static const ProductDocumentResponseLanguageEnum en = _$productDocumentResponseLanguageEnum_en;

  static Serializer<ProductDocumentResponseLanguageEnum> get serializer => _$productDocumentResponseLanguageEnumSerializer;

  const ProductDocumentResponseLanguageEnum._(String name): super(name);

  static BuiltSet<ProductDocumentResponseLanguageEnum> get values => _$productDocumentResponseLanguageEnumValues;
  static ProductDocumentResponseLanguageEnum valueOf(String name) => _$productDocumentResponseLanguageEnumValueOf(name);
}

