//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/quote_request_summary_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_quote_request_summary_response.g.dart';

/// PageResponseQuoteRequestSummaryResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseQuoteRequestSummaryResponse implements Built<PageResponseQuoteRequestSummaryResponse, PageResponseQuoteRequestSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<QuoteRequestSummaryResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseQuoteRequestSummaryResponse._();

  factory PageResponseQuoteRequestSummaryResponse([void updates(PageResponseQuoteRequestSummaryResponseBuilder b)]) = _$PageResponseQuoteRequestSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseQuoteRequestSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseQuoteRequestSummaryResponse> get serializer => _$PageResponseQuoteRequestSummaryResponseSerializer();
}

class _$PageResponseQuoteRequestSummaryResponseSerializer implements PrimitiveSerializer<PageResponseQuoteRequestSummaryResponse> {
  @override
  final Iterable<Type> types = const [PageResponseQuoteRequestSummaryResponse, _$PageResponseQuoteRequestSummaryResponse];

  @override
  final String wireName = r'PageResponseQuoteRequestSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseQuoteRequestSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(QuoteRequestSummaryResponse)]),
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
    PageResponseQuoteRequestSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseQuoteRequestSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(QuoteRequestSummaryResponse)]),
          ) as BuiltList<QuoteRequestSummaryResponse>;
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
  PageResponseQuoteRequestSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseQuoteRequestSummaryResponseBuilder();
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

