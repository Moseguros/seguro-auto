//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'start_contact_verification_request.g.dart';

/// StartContactVerificationRequest
///
/// Properties:
/// * [channel] - Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
/// * [type] 
/// * [value] - Telemóvel (841234567, +258841234567) ou e-mail
@BuiltValue()
abstract class StartContactVerificationRequest implements Built<StartContactVerificationRequest, StartContactVerificationRequestBuilder> {
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueField(wireName: r'channel')
  StartContactVerificationRequestChannelEnum? get channel;
  // enum channelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'type')
  StartContactVerificationRequestTypeEnum get type;
  // enum typeEnum {  MSISDN,  EMAIL,  };

  /// Telemóvel (841234567, +258841234567) ou e-mail
  @BuiltValueField(wireName: r'value')
  String get value;

  StartContactVerificationRequest._();

  factory StartContactVerificationRequest([void updates(StartContactVerificationRequestBuilder b)]) = _$StartContactVerificationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(StartContactVerificationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<StartContactVerificationRequest> get serializer => _$StartContactVerificationRequestSerializer();
}

class _$StartContactVerificationRequestSerializer implements PrimitiveSerializer<StartContactVerificationRequest> {
  @override
  final Iterable<Type> types = const [StartContactVerificationRequest, _$StartContactVerificationRequest];

  @override
  final String wireName = r'StartContactVerificationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    StartContactVerificationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.channel != null) {
      yield r'channel';
      yield serializers.serialize(
        object.channel,
        specifiedType: const FullType(StartContactVerificationRequestChannelEnum),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(StartContactVerificationRequestTypeEnum),
    );
    yield r'value';
    yield serializers.serialize(
      object.value,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    StartContactVerificationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required StartContactVerificationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StartContactVerificationRequestChannelEnum),
          ) as StartContactVerificationRequestChannelEnum;
          result.channel = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StartContactVerificationRequestTypeEnum),
          ) as StartContactVerificationRequestTypeEnum;
          result.type = valueDes;
          break;
        case r'value':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.value = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  StartContactVerificationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = StartContactVerificationRequestBuilder();
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

class StartContactVerificationRequestChannelEnum extends EnumClass {

  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'SMS')
  static const StartContactVerificationRequestChannelEnum SMS = _$startContactVerificationRequestChannelEnum_SMS;
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const StartContactVerificationRequestChannelEnum WHATSAPP = _$startContactVerificationRequestChannelEnum_WHATSAPP;
  /// Canal escolhido pelo cliente (só se customerCanChoose); sem ele usa-se o canal por defeito
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const StartContactVerificationRequestChannelEnum EMAIL = _$startContactVerificationRequestChannelEnum_EMAIL;

  static Serializer<StartContactVerificationRequestChannelEnum> get serializer => _$startContactVerificationRequestChannelEnumSerializer;

  const StartContactVerificationRequestChannelEnum._(String name): super(name);

  static BuiltSet<StartContactVerificationRequestChannelEnum> get values => _$startContactVerificationRequestChannelEnumValues;
  static StartContactVerificationRequestChannelEnum valueOf(String name) => _$startContactVerificationRequestChannelEnumValueOf(name);
}

class StartContactVerificationRequestTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MSISDN')
  static const StartContactVerificationRequestTypeEnum MSISDN = _$startContactVerificationRequestTypeEnum_MSISDN;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const StartContactVerificationRequestTypeEnum EMAIL = _$startContactVerificationRequestTypeEnum_EMAIL;

  static Serializer<StartContactVerificationRequestTypeEnum> get serializer => _$startContactVerificationRequestTypeEnumSerializer;

  const StartContactVerificationRequestTypeEnum._(String name): super(name);

  static BuiltSet<StartContactVerificationRequestTypeEnum> get values => _$startContactVerificationRequestTypeEnumValues;
  static StartContactVerificationRequestTypeEnum valueOf(String name) => _$startContactVerificationRequestTypeEnumValueOf(name);
}

