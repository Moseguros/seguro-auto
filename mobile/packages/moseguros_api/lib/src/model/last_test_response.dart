//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'last_test_response.g.dart';

/// LastTestResponse
///
/// Properties:
/// * [at] 
/// * [message] 
/// * [result] 
@BuiltValue()
abstract class LastTestResponse implements Built<LastTestResponse, LastTestResponseBuilder> {
  @BuiltValueField(wireName: r'at')
  DateTime get at;

  @BuiltValueField(wireName: r'message')
  String? get message;

  @BuiltValueField(wireName: r'result')
  LastTestResponseResultEnum get result;
  // enum resultEnum {  OK,  FAILED,  NOT_SUPPORTED,  };

  LastTestResponse._();

  factory LastTestResponse([void updates(LastTestResponseBuilder b)]) = _$LastTestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(LastTestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<LastTestResponse> get serializer => _$LastTestResponseSerializer();
}

class _$LastTestResponseSerializer implements PrimitiveSerializer<LastTestResponse> {
  @override
  final Iterable<Type> types = const [LastTestResponse, _$LastTestResponse];

  @override
  final String wireName = r'LastTestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    LastTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'at';
    yield serializers.serialize(
      object.at,
      specifiedType: const FullType(DateTime),
    );
    if (object.message != null) {
      yield r'message';
      yield serializers.serialize(
        object.message,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'result';
    yield serializers.serialize(
      object.result,
      specifiedType: const FullType(LastTestResponseResultEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    LastTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required LastTestResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'at':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.at = valueDes;
          break;
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.message = valueDes;
          break;
        case r'result':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(LastTestResponseResultEnum),
          ) as LastTestResponseResultEnum;
          result.result = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  LastTestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = LastTestResponseBuilder();
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

class LastTestResponseResultEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OK')
  static const LastTestResponseResultEnum OK = _$lastTestResponseResultEnum_OK;
  @BuiltValueEnumConst(wireName: r'FAILED')
  static const LastTestResponseResultEnum FAILED = _$lastTestResponseResultEnum_FAILED;
  @BuiltValueEnumConst(wireName: r'NOT_SUPPORTED')
  static const LastTestResponseResultEnum NOT_SUPPORTED = _$lastTestResponseResultEnum_NOT_SUPPORTED;

  static Serializer<LastTestResponseResultEnum> get serializer => _$lastTestResponseResultEnumSerializer;

  const LastTestResponseResultEnum._(String name): super(name);

  static BuiltSet<LastTestResponseResultEnum> get values => _$lastTestResponseResultEnumValues;
  static LastTestResponseResultEnum valueOf(String name) => _$lastTestResponseResultEnumValueOf(name);
}

