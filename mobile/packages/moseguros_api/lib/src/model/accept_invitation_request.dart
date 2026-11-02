//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'accept_invitation_request.g.dart';

/// AcceptInvitationRequest
///
/// Properties:
/// * [password] - 12 a 128 caracteres, sem a parte local do e-mail e fora das mais comuns (400 PASSWORD_TOO_WEAK com os motivos em errors[])
@BuiltValue()
abstract class AcceptInvitationRequest implements Built<AcceptInvitationRequest, AcceptInvitationRequestBuilder> {
  /// 12 a 128 caracteres, sem a parte local do e-mail e fora das mais comuns (400 PASSWORD_TOO_WEAK com os motivos em errors[])
  @BuiltValueField(wireName: r'password')
  String get password;

  AcceptInvitationRequest._();

  factory AcceptInvitationRequest([void updates(AcceptInvitationRequestBuilder b)]) = _$AcceptInvitationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AcceptInvitationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AcceptInvitationRequest> get serializer => _$AcceptInvitationRequestSerializer();
}

class _$AcceptInvitationRequestSerializer implements PrimitiveSerializer<AcceptInvitationRequest> {
  @override
  final Iterable<Type> types = const [AcceptInvitationRequest, _$AcceptInvitationRequest];

  @override
  final String wireName = r'AcceptInvitationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AcceptInvitationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'password';
    yield serializers.serialize(
      object.password,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    AcceptInvitationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AcceptInvitationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'password':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.password = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AcceptInvitationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AcceptInvitationRequestBuilder();
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

