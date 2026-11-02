//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'totp_setup_response.g.dart';

/// TotpSetupResponse
///
/// Properties:
/// * [otpauthUri] - URI para o QR code
/// * [totpSecret] - Segredo em Base32 (entrada manual)
@BuiltValue()
abstract class TotpSetupResponse implements Built<TotpSetupResponse, TotpSetupResponseBuilder> {
  /// URI para o QR code
  @BuiltValueField(wireName: r'otpauthUri')
  String get otpauthUri;

  /// Segredo em Base32 (entrada manual)
  @BuiltValueField(wireName: r'totpSecret')
  String get totpSecret;

  TotpSetupResponse._();

  factory TotpSetupResponse([void updates(TotpSetupResponseBuilder b)]) = _$TotpSetupResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(TotpSetupResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<TotpSetupResponse> get serializer => _$TotpSetupResponseSerializer();
}

class _$TotpSetupResponseSerializer implements PrimitiveSerializer<TotpSetupResponse> {
  @override
  final Iterable<Type> types = const [TotpSetupResponse, _$TotpSetupResponse];

  @override
  final String wireName = r'TotpSetupResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    TotpSetupResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'otpauthUri';
    yield serializers.serialize(
      object.otpauthUri,
      specifiedType: const FullType(String),
    );
    yield r'totpSecret';
    yield serializers.serialize(
      object.totpSecret,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    TotpSetupResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required TotpSetupResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'otpauthUri':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.otpauthUri = valueDes;
          break;
        case r'totpSecret':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.totpSecret = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  TotpSetupResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = TotpSetupResponseBuilder();
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

