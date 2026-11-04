//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/insurer_inspection_summary_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_insurer_inspection_summary_response.g.dart';

/// PageResponseInsurerInspectionSummaryResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseInsurerInspectionSummaryResponse implements Built<PageResponseInsurerInspectionSummaryResponse, PageResponseInsurerInspectionSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<InsurerInspectionSummaryResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseInsurerInspectionSummaryResponse._();

  factory PageResponseInsurerInspectionSummaryResponse([void updates(PageResponseInsurerInspectionSummaryResponseBuilder b)]) = _$PageResponseInsurerInspectionSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseInsurerInspectionSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseInsurerInspectionSummaryResponse> get serializer => _$PageResponseInsurerInspectionSummaryResponseSerializer();
}

class _$PageResponseInsurerInspectionSummaryResponseSerializer implements PrimitiveSerializer<PageResponseInsurerInspectionSummaryResponse> {
  @override
  final Iterable<Type> types = const [PageResponseInsurerInspectionSummaryResponse, _$PageResponseInsurerInspectionSummaryResponse];

  @override
  final String wireName = r'PageResponseInsurerInspectionSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseInsurerInspectionSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(InsurerInspectionSummaryResponse)]),
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
    PageResponseInsurerInspectionSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseInsurerInspectionSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(InsurerInspectionSummaryResponse)]),
          ) as BuiltList<InsurerInspectionSummaryResponse>;
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
  PageResponseInsurerInspectionSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseInsurerInspectionSummaryResponseBuilder();
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

