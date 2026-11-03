//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'test_plate_format_response.g.dart';

/// TestPlateFormatResponse
///
/// Properties:
/// * [formatted] 
/// * [matches] 
@BuiltValue()
abstract class TestPlateFormatResponse implements Built<TestPlateFormatResponse, TestPlateFormatResponseBuilder> {
  @BuiltValueField(wireName: r'formatted')
  String? get formatted;

  @BuiltValueField(wireName: r'matches')
  bool get matches;

  TestPlateFormatResponse._();

  factory TestPlateFormatResponse([void updates(TestPlateFormatResponseBuilder b)]) = _$TestPlateFormatResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TestPlateFormatResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TestPlateFormatResponse> get serializer => _$TestPlateFormatResponseSerializer();
}

class _$TestPlateFormatResponseSerializer implements PrimitiveSerializer<TestPlateFormatResponse> {
  @override
  final Iterable<Type> types = const [TestPlateFormatResponse, _$TestPlateFormatResponse];

  @override
  final String wireName = r'TestPlateFormatResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TestPlateFormatResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.formatted != null) {
      yield r'formatted';
      yield serializers.serialize(
        object.formatted,
        specifiedType: const FullType(String),
      );
    }
    yield r'matches';
    yield serializers.serialize(
      object.matches,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TestPlateFormatResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TestPlateFormatResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'formatted':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.formatted = valueDes;
          break;
        case r'matches':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.matches = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TestPlateFormatResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TestPlateFormatResponseBuilder();
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

