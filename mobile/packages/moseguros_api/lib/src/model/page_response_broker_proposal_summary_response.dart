//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/broker_proposal_summary_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_broker_proposal_summary_response.g.dart';

/// PageResponseBrokerProposalSummaryResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseBrokerProposalSummaryResponse implements Built<PageResponseBrokerProposalSummaryResponse, PageResponseBrokerProposalSummaryResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<BrokerProposalSummaryResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseBrokerProposalSummaryResponse._();

  factory PageResponseBrokerProposalSummaryResponse([void updates(PageResponseBrokerProposalSummaryResponseBuilder b)]) = _$PageResponseBrokerProposalSummaryResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseBrokerProposalSummaryResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseBrokerProposalSummaryResponse> get serializer => _$PageResponseBrokerProposalSummaryResponseSerializer();
}

class _$PageResponseBrokerProposalSummaryResponseSerializer implements PrimitiveSerializer<PageResponseBrokerProposalSummaryResponse> {
  @override
  final Iterable<Type> types = const [PageResponseBrokerProposalSummaryResponse, _$PageResponseBrokerProposalSummaryResponse];

  @override
  final String wireName = r'PageResponseBrokerProposalSummaryResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseBrokerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(BrokerProposalSummaryResponse)]),
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
    PageResponseBrokerProposalSummaryResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseBrokerProposalSummaryResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(BrokerProposalSummaryResponse)]),
          ) as BuiltList<BrokerProposalSummaryResponse>;
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
  PageResponseBrokerProposalSummaryResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseBrokerProposalSummaryResponseBuilder();
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

