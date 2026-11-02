//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_otp_settings_request.g.dart';

/// UpdateOtpSettingsRequest
///
/// Properties:
/// * [codeLength] - 4 a 8
/// * [customerCanChoose] 
/// * [defaultChannel] 
/// * [enabledChannels] 
/// * [fallbackOrder] 
/// * [maxAttempts] - 1 a 10
/// * [maxRequestsPerHour] - 1 a 50
/// * [resendAfterSeconds] - 15 a 600
/// * [validitySeconds] - 60 a 1800
/// * [version] - version lida no GET
@BuiltValue()
abstract class UpdateOtpSettingsRequest implements Built<UpdateOtpSettingsRequest, UpdateOtpSettingsRequestBuilder> {
  /// 4 a 8
  @BuiltValueField(wireName: r'codeLength')
  int get codeLength;

  @BuiltValueField(wireName: r'customerCanChoose')
  bool get customerCanChoose;

  @BuiltValueField(wireName: r'defaultChannel')
  UpdateOtpSettingsRequestDefaultChannelEnum get defaultChannel;
  // enum defaultChannelEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'enabledChannels')
  BuiltList<UpdateOtpSettingsRequestEnabledChannelsEnum> get enabledChannels;
  // enum enabledChannelsEnum {  SMS,  WHATSAPP,  EMAIL,  };

  @BuiltValueField(wireName: r'fallbackOrder')
  BuiltList<UpdateOtpSettingsRequestFallbackOrderEnum> get fallbackOrder;
  // enum fallbackOrderEnum {  SMS,  WHATSAPP,  EMAIL,  };

  /// 1 a 10
  @BuiltValueField(wireName: r'maxAttempts')
  int get maxAttempts;

  /// 1 a 50
  @BuiltValueField(wireName: r'maxRequestsPerHour')
  int get maxRequestsPerHour;

  /// 15 a 600
  @BuiltValueField(wireName: r'resendAfterSeconds')
  int get resendAfterSeconds;

  /// 60 a 1800
  @BuiltValueField(wireName: r'validitySeconds')
  int get validitySeconds;

  /// version lida no GET
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateOtpSettingsRequest._();

  factory UpdateOtpSettingsRequest([void updates(UpdateOtpSettingsRequestBuilder b)]) = _$UpdateOtpSettingsRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateOtpSettingsRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateOtpSettingsRequest> get serializer => _$UpdateOtpSettingsRequestSerializer();
}

class _$UpdateOtpSettingsRequestSerializer implements PrimitiveSerializer<UpdateOtpSettingsRequest> {
  @override
  final Iterable<Type> types = const [UpdateOtpSettingsRequest, _$UpdateOtpSettingsRequest];

  @override
  final String wireName = r'UpdateOtpSettingsRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateOtpSettingsRequest object, {
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
      specifiedType: const FullType(UpdateOtpSettingsRequestDefaultChannelEnum),
    );
    yield r'enabledChannels';
    yield serializers.serialize(
      object.enabledChannels,
      specifiedType: const FullType(BuiltList, [FullType(UpdateOtpSettingsRequestEnabledChannelsEnum)]),
    );
    yield r'fallbackOrder';
    yield serializers.serialize(
      object.fallbackOrder,
      specifiedType: const FullType(BuiltList, [FullType(UpdateOtpSettingsRequestFallbackOrderEnum)]),
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
    UpdateOtpSettingsRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateOtpSettingsRequestBuilder result,
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
            specifiedType: const FullType(UpdateOtpSettingsRequestDefaultChannelEnum),
          ) as UpdateOtpSettingsRequestDefaultChannelEnum;
          result.defaultChannel = valueDes;
          break;
        case r'enabledChannels':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(UpdateOtpSettingsRequestEnabledChannelsEnum)]),
          ) as BuiltList<UpdateOtpSettingsRequestEnabledChannelsEnum>;
          result.enabledChannels.replace(valueDes);
          break;
        case r'fallbackOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(UpdateOtpSettingsRequestFallbackOrderEnum)]),
          ) as BuiltList<UpdateOtpSettingsRequestFallbackOrderEnum>;
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
  UpdateOtpSettingsRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateOtpSettingsRequestBuilder();
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

class UpdateOtpSettingsRequestDefaultChannelEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const UpdateOtpSettingsRequestDefaultChannelEnum SMS = _$updateOtpSettingsRequestDefaultChannelEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const UpdateOtpSettingsRequestDefaultChannelEnum WHATSAPP = _$updateOtpSettingsRequestDefaultChannelEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const UpdateOtpSettingsRequestDefaultChannelEnum EMAIL = _$updateOtpSettingsRequestDefaultChannelEnum_EMAIL;

  static Serializer<UpdateOtpSettingsRequestDefaultChannelEnum> get serializer => _$updateOtpSettingsRequestDefaultChannelEnumSerializer;

  const UpdateOtpSettingsRequestDefaultChannelEnum._(String name): super(name);

  static BuiltSet<UpdateOtpSettingsRequestDefaultChannelEnum> get values => _$updateOtpSettingsRequestDefaultChannelEnumValues;
  static UpdateOtpSettingsRequestDefaultChannelEnum valueOf(String name) => _$updateOtpSettingsRequestDefaultChannelEnumValueOf(name);
}

class UpdateOtpSettingsRequestEnabledChannelsEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const UpdateOtpSettingsRequestEnabledChannelsEnum SMS = _$updateOtpSettingsRequestEnabledChannelsEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const UpdateOtpSettingsRequestEnabledChannelsEnum WHATSAPP = _$updateOtpSettingsRequestEnabledChannelsEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const UpdateOtpSettingsRequestEnabledChannelsEnum EMAIL = _$updateOtpSettingsRequestEnabledChannelsEnum_EMAIL;

  static Serializer<UpdateOtpSettingsRequestEnabledChannelsEnum> get serializer => _$updateOtpSettingsRequestEnabledChannelsEnumSerializer;

  const UpdateOtpSettingsRequestEnabledChannelsEnum._(String name): super(name);

  static BuiltSet<UpdateOtpSettingsRequestEnabledChannelsEnum> get values => _$updateOtpSettingsRequestEnabledChannelsEnumValues;
  static UpdateOtpSettingsRequestEnabledChannelsEnum valueOf(String name) => _$updateOtpSettingsRequestEnabledChannelsEnumValueOf(name);
}

class UpdateOtpSettingsRequestFallbackOrderEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'SMS')
  static const UpdateOtpSettingsRequestFallbackOrderEnum SMS = _$updateOtpSettingsRequestFallbackOrderEnum_SMS;
  @BuiltValueEnumConst(wireName: r'WHATSAPP')
  static const UpdateOtpSettingsRequestFallbackOrderEnum WHATSAPP = _$updateOtpSettingsRequestFallbackOrderEnum_WHATSAPP;
  @BuiltValueEnumConst(wireName: r'EMAIL')
  static const UpdateOtpSettingsRequestFallbackOrderEnum EMAIL = _$updateOtpSettingsRequestFallbackOrderEnum_EMAIL;

  static Serializer<UpdateOtpSettingsRequestFallbackOrderEnum> get serializer => _$updateOtpSettingsRequestFallbackOrderEnumSerializer;

  const UpdateOtpSettingsRequestFallbackOrderEnum._(String name): super(name);

  static BuiltSet<UpdateOtpSettingsRequestFallbackOrderEnum> get values => _$updateOtpSettingsRequestFallbackOrderEnumValues;
  static UpdateOtpSettingsRequestFallbackOrderEnum valueOf(String name) => _$updateOtpSettingsRequestFallbackOrderEnumValueOf(name);
}

