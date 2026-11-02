//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_test_response.g.dart';

/// OtpTestResponse
///
/// Properties:
/// * [channel] 
/// * [deliveryStatus] - SENT ou FAILED
/// * [messageId] - id da mensagem enviada
@BuiltValue()
abstract class OtpTestResponse implements Built<OtpTestResponse, OtpTestResponseBuilder> {
  @BuiltValueField(wireName: r'channel')
  OtpTestResponseChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  /// SENT ou FAILED
  @BuiltValueField(wireName: r'deliveryStatus')
  OtpTestResponseDeliveryStatusEnum get deliveryStatus;
  // enum deliveryStatusEnum {  PENDING,  SENT,  DELIVERED,  FAILED,  };

  /// id da mensagem enviada
  @BuiltValueField(wireName: r'messageId')
  String get messageId;

  OtpTestResponse._();

  factory OtpTestResponse([void updates(OtpTestResponseBuilder b)]) = _$OtpTestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpTestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpTestResponse> get serializer => _$OtpTestResponseSerializer();
}

class _$OtpTestResponseSerializer implements PrimitiveSerializer<OtpTestResponse> {
  @override
  final Iterable<Type> types = const [OtpTestResponse, _$OtpTestResponse];

  @override
  final String wireName = r'OtpTestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(OtpTestResponseChannelEnum),
    );
    yield r'deliveryStatus';
    yield serializers.serialize(
      object.deliveryStatus,
      specifiedType: const FullType(OtpTestResponseDeliveryStatusEnum),
    );
    yield r'messageId';
    yield serializers.serialize(
      object.messageId,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OtpTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpTestResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpTestResponseChannelEnum),
          ) as OtpTestResponseChannelEnum;
          result.channel = valueDes;
          break;
        case r'deliveryStatus':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpTestResponseDeliveryStatusEnum),
          ) as OtpTestResponseDeliveryStatusEnum;
          result.deliveryStatus = valueDes;
          break;
        case r'messageId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.messageId = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OtpTestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpTestResponseBuilder();
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

class OtpTestResponseChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpTestResponseChannelEnum SMS = _$otpTestResponseChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpTestResponseChannelEnum WHATSAPP = _$otpTestResponseChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpTestResponseChannelEnum EMAIL = _$otpTestResponseChannelEnum_EMAIL;

  static Serializer<OtpTestResponseChannelEnum> get serializer => _$otpTestResponseChannelEnumSerializer;

  const OtpTestResponseChannelEnum._(String name): super(name);

  static BuiltSet<OtpTestResponseChannelEnum> get values => _$otpTestResponseChannelEnumValues;
  static OtpTestResponseChannelEnum valueOf(String name) => _$otpTestResponseChannelEnumValueOf(name);
}

class OtpTestResponseDeliveryStatusEnum extends EnumClass {

  /// SENT ou FAILED
  @BuiltValueEnumConst(wireName: r'PENDING')
  static const OtpTestResponseDeliveryStatusEnum PENDING = _$otpTestResponseDeliveryStatusEnum_PENDING;
  /// SENT ou FAILED
  @BuiltValueEnumConst(wireName: r'SENT')
  static const OtpTestResponseDeliveryStatusEnum SENT = _$otpTestResponseDeliveryStatusEnum_SENT;
  /// SENT ou FAILED
  @BuiltValueEnumConst(wireName: r'DELIVERED')
  static const OtpTestResponseDeliveryStatusEnum DELIVERED = _$otpTestResponseDeliveryStatusEnum_DELIVERED;
  /// SENT ou FAILED
  @BuiltValueEnumConst(wireName: r'FAILED')
  static const OtpTestResponseDeliveryStatusEnum FAILED = _$otpTestResponseDeliveryStatusEnum_FAILED;

  static Serializer<OtpTestResponseDeliveryStatusEnum> get serializer => _$otpTestResponseDeliveryStatusEnumSerializer;

  const OtpTestResponseDeliveryStatusEnum._(String name): super(name);

  static BuiltSet<OtpTestResponseDeliveryStatusEnum> get values => _$otpTestResponseDeliveryStatusEnumValues;
  static OtpTestResponseDeliveryStatusEnum valueOf(String name) => _$otpTestResponseDeliveryStatusEnumValueOf(name);
}

