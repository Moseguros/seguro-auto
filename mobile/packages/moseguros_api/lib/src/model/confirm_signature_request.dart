//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'confirm_signature_request.g.dart';

/// ConfirmSignatureRequest
///
/// Properties:
/// * [challengeId] - O challengeId do pedido em curso
/// * [code] 
@BuiltValue()
abstract class ConfirmSignatureRequest implements Built<ConfirmSignatureRequest, ConfirmSignatureRequestBuilder> {
  /// O challengeId do pedido em curso
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  @BuiltValueField(wireName: r'code')
  String get code;

  ConfirmSignatureRequest._();

  factory ConfirmSignatureRequest([void updates(ConfirmSignatureRequestBuilder b)]) = _$ConfirmSignatureRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ConfirmSignatureRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ConfirmSignatureRequest> get serializer => _$ConfirmSignatureRequestSerializer();
}

class _$ConfirmSignatureRequestSerializer implements PrimitiveSerializer<ConfirmSignatureRequest> {
  @override
  final Iterable<Type> types = const [ConfirmSignatureRequest, _$ConfirmSignatureRequest];

  @override
  final String wireName = r'ConfirmSignatureRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ConfirmSignatureRequest object, {
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
    ConfirmSignatureRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ConfirmSignatureRequestBuilder result,
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
  ConfirmSignatureRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ConfirmSignatureRequestBuilder();
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

