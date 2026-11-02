//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'verify_contact_request.g.dart';

/// VerifyContactRequest
///
/// Properties:
/// * [challengeId] - challengeId devolvido no pedido
/// * [code] 
@BuiltValue()
abstract class VerifyContactRequest implements Built<VerifyContactRequest, VerifyContactRequestBuilder> {
  /// challengeId devolvido no pedido
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  @BuiltValueField(wireName: r'code')
  String get code;

  VerifyContactRequest._();

  factory VerifyContactRequest([void updates(VerifyContactRequestBuilder b)]) = _$VerifyContactRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VerifyContactRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VerifyContactRequest> get serializer => _$VerifyContactRequestSerializer();
}

class _$VerifyContactRequestSerializer implements PrimitiveSerializer<VerifyContactRequest> {
  @override
  final Iterable<Type> types = const [VerifyContactRequest, _$VerifyContactRequest];

  @override
  final String wireName = r'VerifyContactRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VerifyContactRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'challengeId';
    yield serializers.serialize(
      object.challengeId,
      specifiedType: const FullType(String),
    );
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    VerifyContactRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VerifyContactRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'challengeId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.challengeId = valueDes;
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
  VerifyContactRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VerifyContactRequestBuilder();
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

