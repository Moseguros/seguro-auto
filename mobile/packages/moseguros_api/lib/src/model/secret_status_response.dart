//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'secret_status_response.g.dart';

/// Campo do segredo (apiKey, publicKey, securityCredential, sftp.password, sftp.privateKey, sftp.passphrase) → estado
///
/// Properties:
/// * [configured] 
/// * [required_] 
/// * [updatedAt] 
@BuiltValue()
abstract class SecretStatusResponse implements Built<SecretStatusResponse, SecretStatusResponseBuilder> {
  @BuiltValueField(wireName: r'configured')
  bool get configured;

  @BuiltValueField(wireName: r'required')
  bool get required_;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime? get updatedAt;

  SecretStatusResponse._();

  factory SecretStatusResponse([void updates(SecretStatusResponseBuilder b)]) = _$SecretStatusResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SecretStatusResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SecretStatusResponse> get serializer => _$SecretStatusResponseSerializer();
}

class _$SecretStatusResponseSerializer implements PrimitiveSerializer<SecretStatusResponse> {
  @override
  final Iterable<Type> types = const [SecretStatusResponse, _$SecretStatusResponse];

  @override
  final String wireName = r'SecretStatusResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SecretStatusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'configured';
    yield serializers.serialize(
      object.configured,
      specifiedType: const FullType(bool),
    );
    yield r'required';
    yield serializers.serialize(
      object.required_,
      specifiedType: const FullType(bool),
    );
    if (object.updatedAt != null) {
      yield r'updatedAt';
      yield serializers.serialize(
        object.updatedAt,
        specifiedType: const FullType.nullable(DateTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    SecretStatusResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SecretStatusResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'configured':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.configured = valueDes;
          break;
        case r'required':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.required_ = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(DateTime),
          ) as DateTime?;
          if (valueDes == null) continue;
          result.updatedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SecretStatusResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SecretStatusResponseBuilder();
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

