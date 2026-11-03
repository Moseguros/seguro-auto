//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_challenge_response.g.dart';

/// OtpChallengeResponse
///
/// Properties:
/// * [channel] 
/// * [deliveryStatus] 
/// * [expiresAt] 
/// * [fallbackUsed] 
@BuiltValue()
abstract class OtpChallengeResponse implements Built<OtpChallengeResponse, OtpChallengeResponseBuilder> {
  @BuiltValueField(wireName: r'channel')
  OtpChallengeResponseChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'deliveryStatus')
  OtpChallengeResponseDeliveryStatusEnum get deliveryStatus;
  // enum deliveryStatusEnum {  PENDING,  SENT,  DELIVERED,  FAILED,  };

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  @BuiltValueField(wireName: r'fallbackUsed')
  bool get fallbackUsed;

  OtpChallengeResponse._();

  factory OtpChallengeResponse([void updates(OtpChallengeResponseBuilder b)]) = _$OtpChallengeResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpChallengeResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpChallengeResponse> get serializer => _$OtpChallengeResponseSerializer();
}

class _$OtpChallengeResponseSerializer implements PrimitiveSerializer<OtpChallengeResponse> {
  @override
  final Iterable<Type> types = const [OtpChallengeResponse, _$OtpChallengeResponse];

  @override
  final String wireName = r'OtpChallengeResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpChallengeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(OtpChallengeResponseChannelEnum),
    );
    yield r'deliveryStatus';
    yield serializers.serialize(
      object.deliveryStatus,
      specifiedType: const FullType(OtpChallengeResponseDeliveryStatusEnum),
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
  }

  @override
  Object serialize(
    Serializers serializers,
    OtpChallengeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpChallengeResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpChallengeResponseChannelEnum),
          ) as OtpChallengeResponseChannelEnum;
          result.channel = valueDes;
          break;
        case r'deliveryStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpChallengeResponseDeliveryStatusEnum),
          ) as OtpChallengeResponseDeliveryStatusEnum;
          result.deliveryStatus = valueDes;
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OtpChallengeResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpChallengeResponseBuilder();
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

class OtpChallengeResponseChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpChallengeResponseChannelEnum SMS = _$otpChallengeResponseChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpChallengeResponseChannelEnum WHATSAPP = _$otpChallengeResponseChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpChallengeResponseChannelEnum EMAIL = _$otpChallengeResponseChannelEnum_EMAIL;

  static Serializer<OtpChallengeResponseChannelEnum> get serializer => _$otpChallengeResponseChannelEnumSerializer;

  const OtpChallengeResponseChannelEnum._(String name): super(name);

  static BuiltSet<OtpChallengeResponseChannelEnum> get values => _$otpChallengeResponseChannelEnumValues;
  static OtpChallengeResponseChannelEnum valueOf(String name) => _$otpChallengeResponseChannelEnumValueOf(name);
}

class OtpChallengeResponseDeliveryStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PENDING')
  static const OtpChallengeResponseDeliveryStatusEnum PENDING = _$otpChallengeResponseDeliveryStatusEnum_PENDING;
  @BuiltValueEnumConst(wireName: r'SENT')
  static const OtpChallengeResponseDeliveryStatusEnum SENT = _$otpChallengeResponseDeliveryStatusEnum_SENT;
  @BuiltValueEnumConst(wireName: r'DELIVERED')
  static const OtpChallengeResponseDeliveryStatusEnum DELIVERED = _$otpChallengeResponseDeliveryStatusEnum_DELIVERED;
  @BuiltValueEnumConst(wireName: r'FAILED')
  static const OtpChallengeResponseDeliveryStatusEnum FAILED = _$otpChallengeResponseDeliveryStatusEnum_FAILED;

  static Serializer<OtpChallengeResponseDeliveryStatusEnum> get serializer => _$otpChallengeResponseDeliveryStatusEnumSerializer;

  const OtpChallengeResponseDeliveryStatusEnum._(String name): super(name);

  static BuiltSet<OtpChallengeResponseDeliveryStatusEnum> get values => _$otpChallengeResponseDeliveryStatusEnumValues;
  static OtpChallengeResponseDeliveryStatusEnum valueOf(String name) => _$otpChallengeResponseDeliveryStatusEnumValueOf(name);
}

