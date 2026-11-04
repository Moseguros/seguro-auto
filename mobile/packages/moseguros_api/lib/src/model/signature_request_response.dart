//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'signature_request_response.g.dart';

/// SignatureRequestResponse
///
/// Properties:
/// * [challengeId] - Enviar na confirmação
/// * [channel] - Canal efectivamente usado
/// * [destinationMasked] 
/// * [documentFileId] - PDF da proposta a assinar; descarrega-se em GET /api/v1/files/{documentFileId}
/// * [documentHash] - SHA-256 do PDF, hexadecimal minúsculo
/// * [expiresAt] 
/// * [fallbackUsed] - O canal mudou por falha do primeiro
/// * [resendAfterSeconds] 
@BuiltValue()
abstract class SignatureRequestResponse implements Built<SignatureRequestResponse, SignatureRequestResponseBuilder> {
  /// Enviar na confirmação
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  /// Canal efectivamente usado
  @BuiltValueField(wireName: r'channel')
  SignatureRequestResponseChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'destinationMasked')
  String get destinationMasked;

  /// PDF da proposta a assinar; descarrega-se em GET /api/v1/files/{documentFileId}
  @BuiltValueField(wireName: r'documentFileId')
  String get documentFileId;

  /// SHA-256 do PDF, hexadecimal minúsculo
  @BuiltValueField(wireName: r'documentHash')
  String get documentHash;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  /// O canal mudou por falha do primeiro
  @BuiltValueField(wireName: r'fallbackUsed')
  bool get fallbackUsed;

  @BuiltValueField(wireName: r'resendAfterSeconds')
  int get resendAfterSeconds;

  SignatureRequestResponse._();

  factory SignatureRequestResponse([void updates(SignatureRequestResponseBuilder b)]) = _$SignatureRequestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(SignatureRequestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<SignatureRequestResponse> get serializer => _$SignatureRequestResponseSerializer();
}

class _$SignatureRequestResponseSerializer implements PrimitiveSerializer<SignatureRequestResponse> {
  @override
  final Iterable<Type> types = const [SignatureRequestResponse, _$SignatureRequestResponse];

  @override
  final String wireName = r'SignatureRequestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    SignatureRequestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'challengeId';
    yield serializers.serialize(
      object.challengeId,
      specifiedType: const FullType(String),
    );
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(SignatureRequestResponseChannelEnum),
    );
    yield r'destinationMasked';
    yield serializers.serialize(
      object.destinationMasked,
      specifiedType: const FullType(String),
    );
    yield r'documentFileId';
    yield serializers.serialize(
      object.documentFileId,
      specifiedType: const FullType(String),
    );
    yield r'documentHash';
    yield serializers.serialize(
      object.documentHash,
      specifiedType: const FullType(String),
    );
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'fallbackUsed';
    yield serializers.serialize(
      object.fallbackUsed,
      specifiedType: const FullType(bool),
    );
    yield r'resendAfterSeconds';
    yield serializers.serialize(
      object.resendAfterSeconds,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    SignatureRequestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required SignatureRequestResponseBuilder result,
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
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(SignatureRequestResponseChannelEnum),
          ) as SignatureRequestResponseChannelEnum;
          result.channel = valueDes;
          break;
        case r'destinationMasked':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.destinationMasked = valueDes;
          break;
        case r'documentFileId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.documentFileId = valueDes;
          break;
        case r'documentHash':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.documentHash = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
          break;
        case r'fallbackUsed':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.fallbackUsed = valueDes;
          break;
        case r'resendAfterSeconds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.resendAfterSeconds = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  SignatureRequestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = SignatureRequestResponseBuilder();
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

class SignatureRequestResponseChannelEnum extends EnumClass {

  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'SMS')
  static const SignatureRequestResponseChannelEnum SMS = _$signatureRequestResponseChannelEnum_SMS;
  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const SignatureRequestResponseChannelEnum WHATSAPP = _$signatureRequestResponseChannelEnum_WHATSAPP;
  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const SignatureRequestResponseChannelEnum EMAIL = _$signatureRequestResponseChannelEnum_EMAIL;

  static Serializer<SignatureRequestResponseChannelEnum> get serializer => _$signatureRequestResponseChannelEnumSerializer;

  const SignatureRequestResponseChannelEnum._(String name): super(name);

  static BuiltSet<SignatureRequestResponseChannelEnum> get values => _$signatureRequestResponseChannelEnumValues;
  static SignatureRequestResponseChannelEnum valueOf(String name) => _$signatureRequestResponseChannelEnumValueOf(name);
}

