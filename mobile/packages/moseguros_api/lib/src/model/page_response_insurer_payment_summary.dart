//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/insurer_payment_summary.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_insurer_payment_summary.g.dart';

/// PageResponseInsurerPaymentSummary
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseInsurerPaymentSummary implements Built<PageResponseInsurerPaymentSummary, PageResponseInsurerPaymentSummaryBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<InsurerPaymentSummary> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseInsurerPaymentSummary._();

  factory PageResponseInsurerPaymentSummary([void updates(PageResponseInsurerPaymentSummaryBuilder b)]) = _$PageResponseInsurerPaymentSummary;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseInsurerPaymentSummaryBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseInsurerPaymentSummary> get serializer => _$PageResponseInsurerPaymentSummarySerializer();
}

class _$PageResponseInsurerPaymentSummarySerializer implements PrimitiveSerializer<PageResponseInsurerPaymentSummary> {
  @override
  final Iterable<Type> types = const [PageResponseInsurerPaymentSummary, _$PageResponseInsurerPaymentSummary];

  @override
  final String wireName = r'PageResponseInsurerPaymentSummary';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseInsurerPaymentSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(InsurerPaymentSummary)]),
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
    PageResponseInsurerPaymentSummary object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseInsurerPaymentSummaryBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(InsurerPaymentSummary)]),
          ) as BuiltList<InsurerPaymentSummary>;
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
  PageResponseInsurerPaymentSummary deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseInsurerPaymentSummaryBuilder();
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

