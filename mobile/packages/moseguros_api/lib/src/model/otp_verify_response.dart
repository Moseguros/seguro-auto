//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'otp_verify_response.g.dart';

/// OtpVerifyResponse
///
/// Properties:
/// * [accessToken] - JWT de acesso (Bearer), válido 15 minutos
/// * [expiresIn] 
/// * [isNewUser] - A conta foi criada agora
/// * [refreshToken] - Refresh token opaco
@BuiltValue()
abstract class OtpVerifyResponse implements Built<OtpVerifyResponse, OtpVerifyResponseBuilder> {
  /// JWT de acesso (Bearer), válido 15 minutos
  @BuiltValueField(wireName: r'accessToken')
  String get accessToken;

  @BuiltValueField(wireName: r'expiresIn')
  int get expiresIn;

  /// A conta foi criada agora
  @BuiltValueField(wireName: r'isNewUser')
  bool get isNewUser;

  /// Refresh token opaco
  @BuiltValueField(wireName: r'refreshToken')
  String get refreshToken;

  OtpVerifyResponse._();

  factory OtpVerifyResponse([void updates(OtpVerifyResponseBuilder b)]) = _$OtpVerifyResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(OtpVerifyResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<OtpVerifyResponse> get serializer => _$OtpVerifyResponseSerializer();
}

class _$OtpVerifyResponseSerializer implements PrimitiveSerializer<OtpVerifyResponse> {
  @override
  final Iterable<Type> types = const [OtpVerifyResponse, _$OtpVerifyResponse];

  @override
  final String wireName = r'OtpVerifyResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    OtpVerifyResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'accessToken';
    yield serializers.serialize(
      object.accessToken,
      specifiedType: const FullType(String),
    );
    yield r'expiresIn';
    yield serializers.serialize(
      object.expiresIn,
      specifiedType: const FullType(int),
    );
    yield r'isNewUser';
    yield serializers.serialize(
      object.isNewUser,
      specifiedType: const FullType(bool),
    );
    yield r'refreshToken';
    yield serializers.serialize(
      object.refreshToken,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    OtpVerifyResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required OtpVerifyResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'accessToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.accessToken = valueDes;
          break;
        case r'expiresIn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.expiresIn = valueDes;
          break;
        case r'isNewUser':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.isNewUser = valueDes;
          break;
        case r'refreshToken':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.refreshToken = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  OtpVerifyResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = OtpVerifyResponseBuilder();
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

