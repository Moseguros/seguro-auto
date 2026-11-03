//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'test_plate_format_request.g.dart';

/// TestPlateFormatRequest
///
/// Properties:
/// * [displayTemplate] 
/// * [pattern] 
/// * [value] - Valor como o utilizador o escreveria
@BuiltValue()
abstract class TestPlateFormatRequest implements Built<TestPlateFormatRequest, TestPlateFormatRequestBuilder> {
  @BuiltValueField(wireName: r'displayTemplate')
  String get displayTemplate;

  @BuiltValueField(wireName: r'pattern')
  String get pattern;

  /// Valor como o utilizador o escreveria
  @BuiltValueField(wireName: r'value')
  String get value;

  TestPlateFormatRequest._();

  factory TestPlateFormatRequest([void updates(TestPlateFormatRequestBuilder b)]) = _$TestPlateFormatRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TestPlateFormatRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TestPlateFormatRequest> get serializer => _$TestPlateFormatRequestSerializer();
}

class _$TestPlateFormatRequestSerializer implements PrimitiveSerializer<TestPlateFormatRequest> {
  @override
  final Iterable<Type> types = const [TestPlateFormatRequest, _$TestPlateFormatRequest];

  @override
  final String wireName = r'TestPlateFormatRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TestPlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'displayTemplate';
    yield serializers.serialize(
      object.displayTemplate,
      specifiedType: const FullType(String),
    );
    yield r'pattern';
    yield serializers.serialize(
      object.pattern,
      specifiedType: const FullType(String),
    );
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TestPlateFormatRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TestPlateFormatRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayTemplate':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayTemplate = valueDes;
          break;
        case r'pattern':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.pattern = valueDes;
          break;
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.value = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TestPlateFormatRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TestPlateFormatRequestBuilder();
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

