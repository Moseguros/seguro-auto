//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_options_response.g.dart';

/// OtpOptionsResponse
///
/// Properties:
/// * [channels] - Canais activos de cada tipo, pela ordem alternativa
/// * [codeLength] 
/// * [customerCanChoose] 
/// * [defaultChannels] - Canal por defeito de cada tipo
/// * [identifierTypes] 
/// * [resendAfterSeconds] 
@BuiltValue()
abstract class OtpOptionsResponse implements Built<OtpOptionsResponse, OtpOptionsResponseBuilder> {
  /// Canais activos de cada tipo, pela ordem alternativa
  @BuiltValueField(wireName: r'channels')
  BuiltMap<String, BuiltList<InnerEnum>> get channels;
  // enum channelsEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'codeLength')
  int get codeLength;

  @BuiltValueField(wireName: r'customerCanChoose')
  bool get customerCanChoose;

  /// Canal por defeito de cada tipo
  @BuiltValueField(wireName: r'defaultChannels')
  BuiltMap<String, OtpOptionsResponseDefaultChannelsEnum> get defaultChannels;
  // enum defaultChannelsEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'identifierTypes')
  BuiltList<OtpOptionsResponseIdentifierTypesEnum> get identifierTypes;
  // enum identifierTypesEnum {  MSISDN,  EMAIL,  };

  @BuiltValueField(wireName: r'resendAfterSeconds')
  int get resendAfterSeconds;

  OtpOptionsResponse._();

  factory OtpOptionsResponse([void updates(OtpOptionsResponseBuilder b)]) = _$OtpOptionsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpOptionsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpOptionsResponse> get serializer => _$OtpOptionsResponseSerializer();
}

class _$OtpOptionsResponseSerializer implements PrimitiveSerializer<OtpOptionsResponse> {
  @override
  final Iterable<Type> types = const [OtpOptionsResponse, _$OtpOptionsResponse];

  @override
  final String wireName = r'OtpOptionsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpOptionsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'channels';
    yield serializers.serialize(
      object.channels,
      specifiedType: const FullType(BuiltMap, [FullType(String), FullType(BuiltList, [FullType(InnerEnum)])]),
    );
    yield r'codeLength';
    yield serializers.serialize(
      object.codeLength,
      specifiedType: const FullType(int),
    );
    yield r'customerCanChoose';
    yield serializers.serialize(
      object.customerCanChoose,
      specifiedType: const FullType(bool),
    );
    yield r'defaultChannels';
    yield serializers.serialize(
      object.defaultChannels,
      specifiedType: const FullType(BuiltMap, [FullType(String), FullType(OtpOptionsResponseDefaultChannelsEnum)]),
    );
    yield r'identifierTypes';
    yield serializers.serialize(
      object.identifierTypes,
      specifiedType: const FullType(BuiltList, [FullType(OtpOptionsResponseIdentifierTypesEnum)]),
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
    OtpOptionsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpOptionsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'channels':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(BuiltList, [FullType(InnerEnum)])]),
          ) as BuiltMap<String, BuiltList<InnerEnum>>;
          result.channels.replace(valueDes);
          break;
        case r'codeLength':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.codeLength = valueDes;
          break;
        case r'customerCanChoose':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.customerCanChoose = valueDes;
          break;
        case r'defaultChannels':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltMap, [FullType(String), FullType(OtpOptionsResponseDefaultChannelsEnum)]),
          ) as BuiltMap<String, OtpOptionsResponseDefaultChannelsEnum>;
          result.defaultChannels.replace(valueDes);
          break;
        case r'identifierTypes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(OtpOptionsResponseIdentifierTypesEnum)]),
          ) as BuiltList<OtpOptionsResponseIdentifierTypesEnum>;
          result.identifierTypes.replace(valueDes);
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
  OtpOptionsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpOptionsResponseBuilder();
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

class InnerEnum extends EnumClass {

  /// Canais activos de cada tipo, pela ordem alternativa
  @BuiltValueEnumConst(wireName: r'SMS')
  static const InnerEnum SMS = _$innerEnum_SMS;
  /// Canais activos de cada tipo, pela ordem alternativa
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const InnerEnum WHATSAPP = _$innerEnum_WHATSAPP;
  /// Canais activos de cada tipo, pela ordem alternativa
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const InnerEnum EMAIL = _$innerEnum_EMAIL;

  static Serializer<InnerEnum> get serializer => _$innerEnumSerializer;

  const InnerEnum._(String name): super(name);

  static BuiltSet<InnerEnum> get values => _$innerEnumValues;
  static InnerEnum valueOf(String name) => _$innerEnumValueOf(name);
}

class OtpOptionsResponseDefaultChannelsEnum extends EnumClass {

  /// Canal por defeito de cada tipo
  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpOptionsResponseDefaultChannelsEnum SMS = _$otpOptionsResponseDefaultChannelsEnum_SMS;
  /// Canal por defeito de cada tipo
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpOptionsResponseDefaultChannelsEnum WHATSAPP = _$otpOptionsResponseDefaultChannelsEnum_WHATSAPP;
  /// Canal por defeito de cada tipo
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpOptionsResponseDefaultChannelsEnum EMAIL = _$otpOptionsResponseDefaultChannelsEnum_EMAIL;

  static Serializer<OtpOptionsResponseDefaultChannelsEnum> get serializer => _$otpOptionsResponseDefaultChannelsEnumSerializer;

  const OtpOptionsResponseDefaultChannelsEnum._(String name): super(name);

  static BuiltSet<OtpOptionsResponseDefaultChannelsEnum> get values => _$otpOptionsResponseDefaultChannelsEnumValues;
  static OtpOptionsResponseDefaultChannelsEnum valueOf(String name) => _$otpOptionsResponseDefaultChannelsEnumValueOf(name);
}

class OtpOptionsResponseIdentifierTypesEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MSISDN')
  static const OtpOptionsResponseIdentifierTypesEnum MSISDN = _$otpOptionsResponseIdentifierTypesEnum_MSISDN;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpOptionsResponseIdentifierTypesEnum EMAIL = _$otpOptionsResponseIdentifierTypesEnum_EMAIL;

  static Serializer<OtpOptionsResponseIdentifierTypesEnum> get serializer => _$otpOptionsResponseIdentifierTypesEnumSerializer;

  const OtpOptionsResponseIdentifierTypesEnum._(String name): super(name);

  static BuiltSet<OtpOptionsResponseIdentifierTypesEnum> get values => _$otpOptionsResponseIdentifierTypesEnumValues;
  static OtpOptionsResponseIdentifierTypesEnum valueOf(String name) => _$otpOptionsResponseIdentifierTypesEnumValueOf(name);
}

