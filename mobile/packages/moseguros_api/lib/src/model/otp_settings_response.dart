//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_settings_response.g.dart';

/// OtpSettingsResponse
///
/// Properties:
/// * [codeLength] 
/// * [customerCanChoose] 
/// * [defaultChannel] 
/// * [enabledChannels] 
/// * [fallbackOrder] - Ordem da alternativa automática
/// * [maxAttempts] 
/// * [maxRequestsPerHour] 
/// * [resendAfterSeconds] 
/// * [updatedAt] 
/// * [updatedBy] - id do utilizador ou 'system'
/// * [validitySeconds] 
/// * [version] - Enviar no PUT seguinte
@BuiltValue()
abstract class OtpSettingsResponse implements Built<OtpSettingsResponse, OtpSettingsResponseBuilder> {
  @BuiltValueField(wireName: r'codeLength')
  int get codeLength;

  @BuiltValueField(wireName: r'customerCanChoose')
  bool get customerCanChoose;

  @BuiltValueField(wireName: r'defaultChannel')
  OtpSettingsResponseDefaultChannelEnum get defaultChannel;
  // enum defaultChannelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'enabledChannels')
  BuiltList<OtpSettingsResponseEnabledChannelsEnum> get enabledChannels;
  // enum enabledChannelsEnum {  SMS,  WHATSAPP,  EMAIL,  };

  /// Ordem da alternativa automática
  @BuiltValueField(wireName: r'fallbackOrder')
  BuiltList<OtpSettingsResponseFallbackOrderEnum> get fallbackOrder;
  // enum fallbackOrderEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'maxAttempts')
  int get maxAttempts;

  @BuiltValueField(wireName: r'maxRequestsPerHour')
  int get maxRequestsPerHour;

  @BuiltValueField(wireName: r'resendAfterSeconds')
  int get resendAfterSeconds;

  @BuiltValueField(wireName: r'updatedAt')
  DateTime get updatedAt;

  /// id do utilizador ou 'system'
  @BuiltValueField(wireName: r'updatedBy')
  String get updatedBy;

  @BuiltValueField(wireName: r'validitySeconds')
  int get validitySeconds;

  /// Enviar no PUT seguinte
  @BuiltValueField(wireName: r'version')
  int get version;

  OtpSettingsResponse._();

  factory OtpSettingsResponse([void updates(OtpSettingsResponseBuilder b)]) = _$OtpSettingsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpSettingsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpSettingsResponse> get serializer => _$OtpSettingsResponseSerializer();
}

class _$OtpSettingsResponseSerializer implements PrimitiveSerializer<OtpSettingsResponse> {
  @override
  final Iterable<Type> types = const [OtpSettingsResponse, _$OtpSettingsResponse];

  @override
  final String wireName = r'OtpSettingsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
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
    yield r'defaultChannel';
    yield serializers.serialize(
      object.defaultChannel,
      specifiedType: const FullType(OtpSettingsResponseDefaultChannelEnum),
    );
    yield r'enabledChannels';
    yield serializers.serialize(
      object.enabledChannels,
      specifiedType: const FullType(BuiltList, [FullType(OtpSettingsResponseEnabledChannelsEnum)]),
    );
    yield r'fallbackOrder';
    yield serializers.serialize(
      object.fallbackOrder,
      specifiedType: const FullType(BuiltList, [FullType(OtpSettingsResponseFallbackOrderEnum)]),
    );
    yield r'maxAttempts';
    yield serializers.serialize(
      object.maxAttempts,
      specifiedType: const FullType(int),
    );
    yield r'maxRequestsPerHour';
    yield serializers.serialize(
      object.maxRequestsPerHour,
      specifiedType: const FullType(int),
    );
    yield r'resendAfterSeconds';
    yield serializers.serialize(
      object.resendAfterSeconds,
      specifiedType: const FullType(int),
    );
    yield r'updatedAt';
    yield serializers.serialize(
      object.updatedAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'updatedBy';
    yield serializers.serialize(
      object.updatedBy,
      specifiedType: const FullType(String),
    );
    yield r'validitySeconds';
    yield serializers.serialize(
      object.validitySeconds,
      specifiedType: const FullType(int),
    );
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OtpSettingsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpSettingsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
        case r'defaultChannel':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(OtpSettingsResponseDefaultChannelEnum),
          ) as OtpSettingsResponseDefaultChannelEnum;
          result.defaultChannel = valueDes;
          break;
        case r'enabledChannels':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(OtpSettingsResponseEnabledChannelsEnum)]),
          ) as BuiltList<OtpSettingsResponseEnabledChannelsEnum>;
          result.enabledChannels.replace(valueDes);
          break;
        case r'fallbackOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(OtpSettingsResponseFallbackOrderEnum)]),
          ) as BuiltList<OtpSettingsResponseFallbackOrderEnum>;
          result.fallbackOrder.replace(valueDes);
          break;
        case r'maxAttempts':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxAttempts = valueDes;
          break;
        case r'maxRequestsPerHour':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.maxRequestsPerHour = valueDes;
          break;
        case r'resendAfterSeconds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.resendAfterSeconds = valueDes;
          break;
        case r'updatedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.updatedAt = valueDes;
          break;
        case r'updatedBy':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.updatedBy = valueDes;
          break;
        case r'validitySeconds':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.validitySeconds = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.version = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OtpSettingsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpSettingsResponseBuilder();
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

class OtpSettingsResponseDefaultChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpSettingsResponseDefaultChannelEnum SMS = _$otpSettingsResponseDefaultChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpSettingsResponseDefaultChannelEnum WHATSAPP = _$otpSettingsResponseDefaultChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpSettingsResponseDefaultChannelEnum EMAIL = _$otpSettingsResponseDefaultChannelEnum_EMAIL;

  static Serializer<OtpSettingsResponseDefaultChannelEnum> get serializer => _$otpSettingsResponseDefaultChannelEnumSerializer;

  const OtpSettingsResponseDefaultChannelEnum._(String name): super(name);

  static BuiltSet<OtpSettingsResponseDefaultChannelEnum> get values => _$otpSettingsResponseDefaultChannelEnumValues;
  static OtpSettingsResponseDefaultChannelEnum valueOf(String name) => _$otpSettingsResponseDefaultChannelEnumValueOf(name);
}

class OtpSettingsResponseEnabledChannelsEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpSettingsResponseEnabledChannelsEnum SMS = _$otpSettingsResponseEnabledChannelsEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpSettingsResponseEnabledChannelsEnum WHATSAPP = _$otpSettingsResponseEnabledChannelsEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpSettingsResponseEnabledChannelsEnum EMAIL = _$otpSettingsResponseEnabledChannelsEnum_EMAIL;

  static Serializer<OtpSettingsResponseEnabledChannelsEnum> get serializer => _$otpSettingsResponseEnabledChannelsEnumSerializer;

  const OtpSettingsResponseEnabledChannelsEnum._(String name): super(name);

  static BuiltSet<OtpSettingsResponseEnabledChannelsEnum> get values => _$otpSettingsResponseEnabledChannelsEnumValues;
  static OtpSettingsResponseEnabledChannelsEnum valueOf(String name) => _$otpSettingsResponseEnabledChannelsEnumValueOf(name);
}

class OtpSettingsResponseFallbackOrderEnum extends EnumClass {

  /// Ordem da alternativa automática
  @BuiltValueEnumConst(wireName: r'SMS')
  static const OtpSettingsResponseFallbackOrderEnum SMS = _$otpSettingsResponseFallbackOrderEnum_SMS;
  /// Ordem da alternativa automática
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const OtpSettingsResponseFallbackOrderEnum WHATSAPP = _$otpSettingsResponseFallbackOrderEnum_WHATSAPP;
  /// Ordem da alternativa automática
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const OtpSettingsResponseFallbackOrderEnum EMAIL = _$otpSettingsResponseFallbackOrderEnum_EMAIL;

  static Serializer<OtpSettingsResponseFallbackOrderEnum> get serializer => _$otpSettingsResponseFallbackOrderEnumSerializer;

  const OtpSettingsResponseFallbackOrderEnum._(String name): super(name);

  static BuiltSet<OtpSettingsResponseFallbackOrderEnum> get values => _$otpSettingsResponseFallbackOrderEnumValues;
  static OtpSettingsResponseFallbackOrderEnum valueOf(String name) => _$otpSettingsResponseFallbackOrderEnumValueOf(name);
}

