//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'reference_sms_response.g.dart';

/// ReferenceSmsResponse
///
/// Properties:
/// * [sentTo] 
@BuiltValue()
abstract class ReferenceSmsResponse implements Built<ReferenceSmsResponse, ReferenceSmsResponseBuilder> {
  @BuiltValueField(wireName: r'sentTo')
  String get sentTo;

  ReferenceSmsResponse._();

  factory ReferenceSmsResponse([void updates(ReferenceSmsResponseBuilder b)]) = _$ReferenceSmsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ReferenceSmsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ReferenceSmsResponse> get serializer => _$ReferenceSmsResponseSerializer();
}

class _$ReferenceSmsResponseSerializer implements PrimitiveSerializer<ReferenceSmsResponse> {
  @override
  final Iterable<Type> types = const [ReferenceSmsResponse, _$ReferenceSmsResponse];

  @override
  final String wireName = r'ReferenceSmsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ReferenceSmsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sentTo';
    yield serializers.serialize(
      object.sentTo,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ReferenceSmsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ReferenceSmsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sentTo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.sentTo = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ReferenceSmsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ReferenceSmsResponseBuilder();
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

