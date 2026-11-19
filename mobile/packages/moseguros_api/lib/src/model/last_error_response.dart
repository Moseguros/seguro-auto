//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'last_error_response.g.dart';

/// LastErrorResponse
///
/// Properties:
/// * [at] 
/// * [code] 
@BuiltValue()
abstract class LastErrorResponse implements Built<LastErrorResponse, LastErrorResponseBuilder> {
  @BuiltValueField(wireName: r'at')
  DateTime? get at;

  @BuiltValueField(wireName: r'code')
  String get code;

  LastErrorResponse._();

  factory LastErrorResponse([void updates(LastErrorResponseBuilder b)]) = _$LastErrorResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(LastErrorResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<LastErrorResponse> get serializer => _$LastErrorResponseSerializer();
}

class _$LastErrorResponseSerializer implements PrimitiveSerializer<LastErrorResponse> {
  @override
  final Iterable<Type> types = const [LastErrorResponse, _$LastErrorResponse];

  @override
  final String wireName = r'LastErrorResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    LastErrorResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.at != null) {
      yield r'at';
      yield serializers.serialize(
        object.at,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    LastErrorResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required LastErrorResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'at':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.at = valueDes;
          break;
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  LastErrorResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = LastErrorResponseBuilder();
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

