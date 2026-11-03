//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/invitation_organization.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'invitation_details_response.g.dart';

/// InvitationDetailsResponse
///
/// Properties:
/// * [email] 
/// * [expiresAt] 
/// * [fullName] 
/// * [organization] 
@BuiltValue()
abstract class InvitationDetailsResponse implements Built<InvitationDetailsResponse, InvitationDetailsResponseBuilder> {
  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'organization')
  InvitationOrganization? get organization;

  InvitationDetailsResponse._();

  factory InvitationDetailsResponse([void updates(InvitationDetailsResponseBuilder b)]) = _$InvitationDetailsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InvitationDetailsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InvitationDetailsResponse> get serializer => _$InvitationDetailsResponseSerializer();
}

class _$InvitationDetailsResponseSerializer implements PrimitiveSerializer<InvitationDetailsResponse> {
  @override
  final Iterable<Type> types = const [InvitationDetailsResponse, _$InvitationDetailsResponse];

  @override
  final String wireName = r'InvitationDetailsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InvitationDetailsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'email';
    yield serializers.serialize(
      object.email,
      specifiedType: const FullType(String),
    );
    yield r'expiresAt';
    yield serializers.serialize(
      object.expiresAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    if (object.organization != null) {
      yield r'organization';
      yield serializers.serialize(
        object.organization,
        specifiedType: const FullType(InvitationOrganization),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    InvitationDetailsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InvitationDetailsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'email':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.email = valueDes;
          break;
        case r'expiresAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.expiresAt = valueDes;
          break;
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'organization':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InvitationOrganization),
          ) as InvitationOrganization;
          result.organization.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InvitationDetailsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InvitationDetailsResponseBuilder();
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

