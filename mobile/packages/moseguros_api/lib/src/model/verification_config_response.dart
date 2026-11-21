//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/captcha_provider.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'verification_config_response.g.dart';

/// VerificationConfigResponse
///
/// Properties:
/// * [captchaProvider] 
/// * [captchaSiteKey] - Chave pública do widget; omitida com NONE
@BuiltValue()
abstract class VerificationConfigResponse implements Built<VerificationConfigResponse, VerificationConfigResponseBuilder> {
  @BuiltValueField(wireName: r'captchaProvider')
  CaptchaProvider get captchaProvider;
  // enum captchaProviderEnum {  TURNSTILE,  NONE,  };

  /// Chave pública do widget; omitida com NONE
  @BuiltValueField(wireName: r'captchaSiteKey')
  String? get captchaSiteKey;

  VerificationConfigResponse._();

  factory VerificationConfigResponse([void updates(VerificationConfigResponseBuilder b)]) = _$VerificationConfigResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VerificationConfigResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VerificationConfigResponse> get serializer => _$VerificationConfigResponseSerializer();
}

class _$VerificationConfigResponseSerializer implements PrimitiveSerializer<VerificationConfigResponse> {
  @override
  final Iterable<Type> types = const [VerificationConfigResponse, _$VerificationConfigResponse];

  @override
  final String wireName = r'VerificationConfigResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VerificationConfigResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'captchaProvider';
    yield serializers.serialize(
      object.captchaProvider,
      specifiedType: const FullType(CaptchaProvider),
    );
    if (object.captchaSiteKey != null) {
      yield r'captchaSiteKey';
      yield serializers.serialize(
        object.captchaSiteKey,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    VerificationConfigResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VerificationConfigResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'captchaProvider':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(CaptchaProvider),
          ) as CaptchaProvider;
          result.captchaProvider = valueDes;
          break;
        case r'captchaSiteKey':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.captchaSiteKey = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  VerificationConfigResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VerificationConfigResponseBuilder();
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

