//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/catalog_product_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_catalog_product_response.g.dart';

/// PageResponseCatalogProductResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseCatalogProductResponse implements Built<PageResponseCatalogProductResponse, PageResponseCatalogProductResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<CatalogProductResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseCatalogProductResponse._();

  factory PageResponseCatalogProductResponse([void updates(PageResponseCatalogProductResponseBuilder b)]) = _$PageResponseCatalogProductResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseCatalogProductResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseCatalogProductResponse> get serializer => _$PageResponseCatalogProductResponseSerializer();
}

class _$PageResponseCatalogProductResponseSerializer implements PrimitiveSerializer<PageResponseCatalogProductResponse> {
  @override
  final Iterable<Type> types = const [PageResponseCatalogProductResponse, _$PageResponseCatalogProductResponse];

  @override
  final String wireName = r'PageResponseCatalogProductResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseCatalogProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(CatalogProductResponse)]),
    );
    yield r'page';
    yield serializers.serialize(
      object.page,
      specifiedType: const FullType(int),
    );
    yield r'size';
    yield serializers.serialize(
      object.size,
      specifiedType: const FullType(int),
    );
    yield r'totalElements';
    yield serializers.serialize(
      object.totalElements,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PageResponseCatalogProductResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseCatalogProductResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(CatalogProductResponse)]),
          ) as BuiltList<CatalogProductResponse>;
          result.content.replace(valueDes);
          break;
        case r'page':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.page = valueDes;
          break;
        case r'size':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.size = valueDes;
          break;
        case r'totalElements':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.totalElements = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PageResponseCatalogProductResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseCatalogProductResponseBuilder();
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

