//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/staff_user_response.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'page_response_staff_user_response.g.dart';

/// PageResponseStaffUserResponse
///
/// Properties:
/// * [content] 
/// * [page] 
/// * [size] 
/// * [totalElements] 
@BuiltValue()
abstract class PageResponseStaffUserResponse implements Built<PageResponseStaffUserResponse, PageResponseStaffUserResponseBuilder> {
  @BuiltValueField(wireName: r'content')
  BuiltList<StaffUserResponse> get content;

  @BuiltValueField(wireName: r'page')
  int get page;

  @BuiltValueField(wireName: r'size')
  int get size;

  @BuiltValueField(wireName: r'totalElements')
  int get totalElements;

  PageResponseStaffUserResponse._();

  factory PageResponseStaffUserResponse([void updates(PageResponseStaffUserResponseBuilder b)]) = _$PageResponseStaffUserResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PageResponseStaffUserResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PageResponseStaffUserResponse> get serializer => _$PageResponseStaffUserResponseSerializer();
}

class _$PageResponseStaffUserResponseSerializer implements PrimitiveSerializer<PageResponseStaffUserResponse> {
  @override
  final Iterable<Type> types = const [PageResponseStaffUserResponse, _$PageResponseStaffUserResponse];

  @override
  final String wireName = r'PageResponseStaffUserResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PageResponseStaffUserResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'content';
    yield serializers.serialize(
      object.content,
      specifiedType: const FullType(BuiltList, [FullType(StaffUserResponse)]),
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
    PageResponseStaffUserResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PageResponseStaffUserResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'content':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(StaffUserResponse)]),
          ) as BuiltList<StaffUserResponse>;
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
  PageResponseStaffUserResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PageResponseStaffUserResponseBuilder();
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

