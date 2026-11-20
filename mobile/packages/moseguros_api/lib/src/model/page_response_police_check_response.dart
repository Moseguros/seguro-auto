//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/police_check_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_police_check_response.g.dart';

/// PageResponsePoliceCheckResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponsePoliceCheckResponse implements Built<PageResponsePoliceCheckResponse, PageResponsePoliceCheckResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<PoliceCheckResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponsePoliceCheckResponse._();

  factory PageResponsePoliceCheckResponse([void updates(PageResponsePoliceCheckResponseBuilder b)]) = _$PageResponsePoliceCheckResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponsePoliceCheckResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponsePoliceCheckResponse> get serializer => _$PageResponsePoliceCheckResponseSerializer();
}

class _$PageResponsePoliceCheckResponseSerializer implements PrimitiveSerializer<PageResponsePoliceCheckResponse> {
  @override
  final Iterable<Type> types = const [PageResponsePoliceCheckResponse, _$PageResponsePoliceCheckResponse];

  @override
  final String wireName = r'PageResponsePoliceCheckResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponsePoliceCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(PoliceCheckResponse)]),
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
    PageResponsePoliceCheckResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponsePoliceCheckResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PoliceCheckResponse)]),
          ) as BuiltList<PoliceCheckResponse>;
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
  PageResponsePoliceCheckResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponsePoliceCheckResponseBuilder();
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

