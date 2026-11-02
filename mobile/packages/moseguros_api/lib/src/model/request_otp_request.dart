//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'request_otp_request.g.dart';

/// RequestOtpRequest
///
/// Properties:
/// * [channel] - Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
/// * [identifier] - Telemóvel (841234567, +258841234567) ou e-mail
/// * [identifierType] 
@BuiltValue()
abstract class RequestOtpRequest implements Built<RequestOtpRequest, RequestOtpRequestBuilder> {
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueField(wireName: r'channel')
  RequestOtpRequestChannelEnum? get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  /// Telemóvel (841234567, +258841234567) ou e-mail
  @BuiltValueField(wireName: r'identifier')
  String get identifier;

  @BuiltValueField(wireName: r'identifierType')
  RequestOtpRequestIdentifierTypeEnum get identifierType;
  // enum identifierTypeEnum {  MSISDN,  EMAIL,  };

  RequestOtpRequest._();

  factory RequestOtpRequest([void updates(RequestOtpRequestBuilder b)]) = _$RequestOtpRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RequestOtpRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RequestOtpRequest> get serializer => _$RequestOtpRequestSerializer();
}

class _$RequestOtpRequestSerializer implements PrimitiveSerializer<RequestOtpRequest> {
  @override
  final Iterable<Type> types = const [RequestOtpRequest, _$RequestOtpRequest];

  @override
  final String wireName = r'RequestOtpRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RequestOtpRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.channel != null) {
      yield r'channel';
      yield serializers.serialize(
        object.channel,
        specifiedType: const FullType(RequestOtpRequestChannelEnum),
      );
    }
    yield r'identifier';
    yield serializers.serialize(
      object.identifier,
      specifiedType: const FullType(String),
    );
    yield r'identifierType';
    yield serializers.serialize(
      object.identifierType,
      specifiedType: const FullType(RequestOtpRequestIdentifierTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RequestOtpRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RequestOtpRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RequestOtpRequestChannelEnum),
          ) as RequestOtpRequestChannelEnum;
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
            specifiedType: const FullType(RequestOtpRequestIdentifierTypeEnum),
          ) as RequestOtpRequestIdentifierTypeEnum;
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
  RequestOtpRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RequestOtpRequestBuilder();
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

class RequestOtpRequestChannelEnum extends EnumClass {

  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'SMS')
  static const RequestOtpRequestChannelEnum SMS = _$requestOtpRequestChannelEnum_SMS;
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const RequestOtpRequestChannelEnum WHATSAPP = _$requestOtpRequestChannelEnum_WHATSAPP;
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const RequestOtpRequestChannelEnum EMAIL = _$requestOtpRequestChannelEnum_EMAIL;

  static Serializer<RequestOtpRequestChannelEnum> get serializer => _$requestOtpRequestChannelEnumSerializer;

  const RequestOtpRequestChannelEnum._(String name): super(name);

  static BuiltSet<RequestOtpRequestChannelEnum> get values => _$requestOtpRequestChannelEnumValues;
  static RequestOtpRequestChannelEnum valueOf(String name) => _$requestOtpRequestChannelEnumValueOf(name);
}

class RequestOtpRequestIdentifierTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MSISDN')
  static const RequestOtpRequestIdentifierTypeEnum MSISDN = _$requestOtpRequestIdentifierTypeEnum_MSISDN;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const RequestOtpRequestIdentifierTypeEnum EMAIL = _$requestOtpRequestIdentifierTypeEnum_EMAIL;

  static Serializer<RequestOtpRequestIdentifierTypeEnum> get serializer => _$requestOtpRequestIdentifierTypeEnumSerializer;

  const RequestOtpRequestIdentifierTypeEnum._(String name): super(name);

  static BuiltSet<RequestOtpRequestIdentifierTypeEnum> get values => _$requestOtpRequestIdentifierTypeEnumValues;
  static RequestOtpRequestIdentifierTypeEnum valueOf(String name) => _$requestOtpRequestIdentifierTypeEnumValueOf(name);
}

