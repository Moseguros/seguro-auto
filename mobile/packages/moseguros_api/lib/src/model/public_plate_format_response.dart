//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'public_plate_format_response.g.dart';

/// PublicPlateFormatResponse
///
/// Properties:
/// * [code] 
/// * [example] 
/// * [label] 
@BuiltValue()
abstract class PublicPlateFormatResponse implements Built<PublicPlateFormatResponse, PublicPlateFormatResponseBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'example')
  String get example;

  @BuiltValueField(wireName: r'label')
  String get label;

  PublicPlateFormatResponse._();

  factory PublicPlateFormatResponse([void updates(PublicPlateFormatResponseBuilder b)]) = _$PublicPlateFormatResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublicPlateFormatResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublicPlateFormatResponse> get serializer => _$PublicPlateFormatResponseSerializer();
}

class _$PublicPlateFormatResponseSerializer implements PrimitiveSerializer<PublicPlateFormatResponse> {
  @override
  final Iterable<Type> types = const [PublicPlateFormatResponse, _$PublicPlateFormatResponse];

  @override
  final String wireName = r'PublicPlateFormatResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublicPlateFormatResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'example';
    yield serializers.serialize(
      object.example,
      specifiedType: const FullType(String),
    );
    yield r'label';
    yield serializers.serialize(
      object.label,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublicPlateFormatResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublicPlateFormatResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'example':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.example = valueDes;
          break;
        case r'label':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.label = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PublicPlateFormatResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublicPlateFormatResponseBuilder();
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

