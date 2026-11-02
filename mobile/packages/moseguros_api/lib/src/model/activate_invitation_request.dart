//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'activate_invitation_request.g.dart';

/// ActivateInvitationRequest
///
/// Properties:
/// * [totpCode] 
@BuiltValue()
abstract class ActivateInvitationRequest implements Built<ActivateInvitationRequest, ActivateInvitationRequestBuilder> {
  @BuiltValueField(wireName: r'totpCode')
  String get totpCode;

  ActivateInvitationRequest._();

  factory ActivateInvitationRequest([void updates(ActivateInvitationRequestBuilder b)]) = _$ActivateInvitationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ActivateInvitationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ActivateInvitationRequest> get serializer => _$ActivateInvitationRequestSerializer();
}

class _$ActivateInvitationRequestSerializer implements PrimitiveSerializer<ActivateInvitationRequest> {
  @override
  final Iterable<Type> types = const [ActivateInvitationRequest, _$ActivateInvitationRequest];

  @override
  final String wireName = r'ActivateInvitationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ActivateInvitationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'totpCode';
    yield serializers.serialize(
      object.totpCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ActivateInvitationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ActivateInvitationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'totpCode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.totpCode = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ActivateInvitationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ActivateInvitationRequestBuilder();
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

