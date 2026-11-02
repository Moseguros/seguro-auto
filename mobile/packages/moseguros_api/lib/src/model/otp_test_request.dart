//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_test_request.g.dart';

/// OtpTestRequest
///
/// Properties:
/// * [channel] 
/// * [identifier] 
/// * [identifierType] 
@BuiltValue()
abstract class OtpTestRequest implements Built<OtpTestRequest, OtpTestRequestBuilder> {
  @BuiltValueField(wireName: r'channel')
  OtpTestRequestChannelEnum get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'identifier')
  String get identifier;

  @BuiltValueField(wireName: r'identifierType')
  OtpTestRequestIdentifierTypeEnum get identifierType;
  // enum identifierTypeEnum {  MSISDN,  EMAIL,  };

  OtpTestRequest._();

  factory OtpTestRequest([void updates(OtpTestRequestBuilder b)]) = _$OtpTestRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpTestRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpTestRequest> get serializer => _$OtpTestRequestSerializer();
}

class _$OtpTestRequestSerializer implements PrimitiveSerializer<OtpTestRequest> {
  @override
  final Iterable<Type> types = const [OtpTestRequest, _$OtpTestRequest];

  @override
  final String wireName = r'OtpTestRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpTestRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channel';
    yield serializers.serialize(
      object.channel,
      specifiedType: const FullType(OtpTestRequestChannelEnum),
    );
    yield r'identifier';
    yield serializers.serialize(
      object.identifier,
      specifiedType: const FullType(String),
    );
    yield r'identifierType';
    yield serializers.serialize(
      object.identifierType,
      specifiedType: const FullType(OtpTestRequestIdentifierTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OtpTestRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpTestRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpTestRequestChannelEnum),
          ) as OtpTestRequestChannelEnum;
          result.channel = valueDes;
          break;
        case r'identifier':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.identifier = valueDes;
          break;
        case r'identifierType':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpTestRequestIdentifierTypeEnum),
          ) as OtpTestRequestIdentifierTypeEnum;
          result.identifierType = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OtpTestRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpTestRequestBuilder();
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

class OtpTestRequestChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpTestRequestChannelEnum SMS = _$otpTestRequestChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpTestRequestChannelEnum WHATSAPP = _$otpTestRequestChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpTestRequestChannelEnum EMAIL = _$otpTestRequestChannelEnum_EMAIL;

  static Serializer<OtpTestRequestChannelEnum> get serializer => _$otpTestRequestChannelEnumSerializer;

  const OtpTestRequestChannelEnum._(String name): super(name);

  static BuiltSet<OtpTestRequestChannelEnum> get values => _$otpTestRequestChannelEnumValues;
  static OtpTestRequestChannelEnum valueOf(String name) => _$otpTestRequestChannelEnumValueOf(name);
}

class OtpTestRequestIdentifierTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MSISDN')
  static const OtpTestRequestIdentifierTypeEnum MSISDN = _$otpTestRequestIdentifierTypeEnum_MSISDN;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpTestRequestIdentifierTypeEnum EMAIL = _$otpTestRequestIdentifierTypeEnum_EMAIL;

  static Serializer<OtpTestRequestIdentifierTypeEnum> get serializer => _$otpTestRequestIdentifierTypeEnumSerializer;

  const OtpTestRequestIdentifierTypeEnum._(String name): super(name);

  static BuiltSet<OtpTestRequestIdentifierTypeEnum> get values => _$otpTestRequestIdentifierTypeEnumValues;
  static OtpTestRequestIdentifierTypeEnum valueOf(String name) => _$otpTestRequestIdentifierTypeEnumValueOf(name);
}

