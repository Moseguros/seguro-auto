//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'invitation_response.g.dart';

/// InvitationResponse
///
/// Properties:
/// * [createdAt] 
/// * [email] 
/// * [expiresAt] 
/// * [fullName] 
/// * [id] 
/// * [organizationId] - Omitido nos convites da plataforma
/// * [roleCodes] 
/// * [status] 
@BuiltValue()
abstract class InvitationResponse implements Built<InvitationResponse, InvitationResponseBuilder> {
  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'expiresAt')
  DateTime get expiresAt;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// Omitido nos convites da plataforma
  @BuiltValueField(wireName: r'organizationId')
  String? get organizationId;

  @BuiltValueField(wireName: r'roleCodes')
  BuiltList<String> get roleCodes;

  @BuiltValueField(wireName: r'status')
  InvitationResponseStatusEnum get status;
  // enum statusEnum {  PENDING,  ACCEPTED,  EXPIRED,  REVOKED,  };

  InvitationResponse._();

  factory InvitationResponse([void updates(InvitationResponseBuilder b)]) = _$InvitationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InvitationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InvitationResponse> get serializer => _$InvitationResponseSerializer();
}

class _$InvitationResponseSerializer implements PrimitiveSerializer<InvitationResponse> {
  @override
  final Iterable<Type> types = const [InvitationResponse, _$InvitationResponse];

  @override
  final String wireName = r'InvitationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InvitationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
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
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.organizationId != null) {
      yield r'organizationId';
      yield serializers.serialize(
        object.organizationId,
        specifiedType: const FullType(String),
      );
    }
    yield r'roleCodes';
    yield serializers.serialize(
      object.roleCodes,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(InvitationResponseStatusEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InvitationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InvitationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'createdAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.createdAt = valueDes;
          break;
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
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'organizationId':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.organizationId = valueDes;
          break;
        case r'roleCodes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roleCodes.replace(valueDes);
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(InvitationResponseStatusEnum),
          ) as InvitationResponseStatusEnum;
          result.status = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InvitationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InvitationResponseBuilder();
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

class InvitationResponseStatusEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PENDING')
  static const InvitationResponseStatusEnum PENDING = _$invitationResponseStatusEnum_PENDING;
  @BuiltValueEnumConst(wireName: r'ACCEPTED')
  static const InvitationResponseStatusEnum ACCEPTED = _$invitationResponseStatusEnum_ACCEPTED;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const InvitationResponseStatusEnum EXPIRED = _$invitationResponseStatusEnum_EXPIRED;
  @BuiltValueEnumConst(wireName: r'REVOKED')
  static const InvitationResponseStatusEnum REVOKED = _$invitationResponseStatusEnum_REVOKED;

  static Serializer<InvitationResponseStatusEnum> get serializer => _$invitationResponseStatusEnumSerializer;

  const InvitationResponseStatusEnum._(String name): super(name);

  static BuiltSet<InvitationResponseStatusEnum> get values => _$invitationResponseStatusEnumValues;
  static InvitationResponseStatusEnum valueOf(String name) => _$invitationResponseStatusEnumValueOf(name);
}

