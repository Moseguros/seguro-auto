//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_request_response.g.dart';

/// OtpRequestResponse
///
/// Properties:
/// * [challengeId] - Usar na validação e na consulta do estado (não muda com a alternativa de canal)
/// * [channel] - Canal efectivamente usado
/// * [expiresAt] 
/// * [fallbackUsed] - O canal mudou por falha do primeiro
/// * [resendAfterSeconds] 
@BuiltValue()
abstract class OtpRequestResponse implements Built<OtpRequestResponse, OtpRequestResponseBuilder> {
  /// Usar na validação e na consulta do estado (não muda com a alternativa de canal)
  @BuiltValueField(wireName: r'challengeId')
  String get challengeId;

  /// Canal efectivamente usado
  @BuiltValueField(wireName: r'channel')
  OtpRequestResponseChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  /// O canal mudou por falha do primeiro
  @BuiltValueField(wireName: r'fallbackUsed')
  bool get fallbackUsed;

  @BuiltValueField(wireName: r'resendAfterSeconds')
  int get resendAfterSeconds;

  OtpRequestResponse._();

  factory OtpRequestResponse([void updates(OtpRequestResponseBuilder b)]) = _$OtpRequestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpRequestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpRequestResponse> get serializer => _$OtpRequestResponseSerializer();
}

class _$OtpRequestResponseSerializer implements PrimitiveSerializer<OtpRequestResponse> {
  @override
  final Iterable<Type> types = const [OtpRequestResponse, _$OtpRequestResponse];

  @override
  final String wireName = r'OtpRequestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpRequestResponse object, {
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
      specifiedType: const FullType(OtpRequestResponseChannelEnum),
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
    OtpRequestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpRequestResponseBuilder result,
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
            specifiedType: const FullType(OtpRequestResponseChannelEnum),
          ) as OtpRequestResponseChannelEnum;
          result.channel = valueDes;
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
  OtpRequestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpRequestResponseBuilder();
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

class OtpRequestResponseChannelEnum extends EnumClass {

  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpRequestResponseChannelEnum SMS = _$otpRequestResponseChannelEnum_SMS;
  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpRequestResponseChannelEnum WHATSAPP = _$otpRequestResponseChannelEnum_WHATSAPP;
  /// Canal efectivamente usado
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpRequestResponseChannelEnum EMAIL = _$otpRequestResponseChannelEnum_EMAIL;

  static Serializer<OtpRequestResponseChannelEnum> get serializer => _$otpRequestResponseChannelEnumSerializer;

  const OtpRequestResponseChannelEnum._(String name): super(name);

  static BuiltSet<OtpRequestResponseChannelEnum> get values => _$otpRequestResponseChannelEnumValues;
  static OtpRequestResponseChannelEnum valueOf(String name) => _$otpRequestResponseChannelEnumValueOf(name);
}

