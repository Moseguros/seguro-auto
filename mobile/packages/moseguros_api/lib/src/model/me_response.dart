//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/me_organization.dart';
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'me_response.g.dart';

/// MeResponse
///
/// Properties:
/// * [displayName] - Staff: nome da conta; cliente: nome completo do perfil
/// * [email] 
/// * [emailVerified] 
/// * [id] 
/// * [msisdn] - Telemóvel moçambicano em E.164
/// * [msisdnVerified] 
/// * [organization] 
/// * [permissions] 
/// * [preferredLanguage] 
/// * [profileComplete] - Só clientes: o perfil tem todos os dados obrigatórios
/// * [roles] 
/// * [type] 
@BuiltValue()
abstract class MeResponse implements Built<MeResponse, MeResponseBuilder> {
  /// Staff: nome da conta; cliente: nome completo do perfil
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'email')
  String? get email;

  @BuiltValueField(wireName: r'emailVerified')
  bool get emailVerified;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// Telemóvel moçambicano em E.164
  @BuiltValueField(wireName: r'msisdn')
  String? get msisdn;

  @BuiltValueField(wireName: r'msisdnVerified')
  bool get msisdnVerified;

  @BuiltValueField(wireName: r'organization')
  MeOrganization? get organization;

  @BuiltValueField(wireName: r'permissions')
  BuiltList<String> get permissions;

  @BuiltValueField(wireName: r'preferredLanguage')
  String get preferredLanguage;

  /// Só clientes: o perfil tem todos os dados obrigatórios
  @BuiltValueField(wireName: r'profileComplete')
  bool? get profileComplete;

  @BuiltValueField(wireName: r'roles')
  BuiltList<String> get roles;

  @BuiltValueField(wireName: r'type')
  MeResponseTypeEnum get type;
  // enum typeEnum {  CUSTOMER,  STAFF,  };

  MeResponse._();

  factory MeResponse([void updates(MeResponseBuilder b)]) = _$MeResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(MeResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<MeResponse> get serializer => _$MeResponseSerializer();
}

class _$MeResponseSerializer implements PrimitiveSerializer<MeResponse> {
  @override
  final Iterable<Type> types = const [MeResponse, _$MeResponse];

  @override
  final String wireName = r'MeResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    MeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType(String),
      );
    }
    if (object.email != null) {
      yield r'email';
      yield serializers.serialize(
        object.email,
        specifiedType: const FullType(String),
      );
    }
    yield r'emailVerified';
    yield serializers.serialize(
      object.emailVerified,
      specifiedType: const FullType(bool),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.msisdn != null) {
      yield r'msisdn';
      yield serializers.serialize(
        object.msisdn,
        specifiedType: const FullType(String),
      );
    }
    yield r'msisdnVerified';
    yield serializers.serialize(
      object.msisdnVerified,
      specifiedType: const FullType(bool),
    );
    if (object.organization != null) {
      yield r'organization';
      yield serializers.serialize(
        object.organization,
        specifiedType: const FullType(MeOrganization),
      );
    }
    yield r'permissions';
    yield serializers.serialize(
      object.permissions,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'preferredLanguage';
    yield serializers.serialize(
      object.preferredLanguage,
      specifiedType: const FullType(String),
    );
    if (object.profileComplete != null) {
      yield r'profileComplete';
      yield serializers.serialize(
        object.profileComplete,
        specifiedType: const FullType(bool),
      );
    }
    yield r'roles';
    yield serializers.serialize(
      object.roles,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(MeResponseTypeEnum),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    MeResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required MeResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'email':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.email = valueDes;
          break;
        case r'emailVerified':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.emailVerified = valueDes;
          break;
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'msisdn':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.msisdn = valueDes;
          break;
        case r'msisdnVerified':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.msisdnVerified = valueDes;
          break;
        case r'organization':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(MeOrganization),
          ) as MeOrganization;
          result.organization.replace(valueDes);
          break;
        case r'permissions':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.permissions.replace(valueDes);
          break;
        case r'preferredLanguage':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.preferredLanguage = valueDes;
          break;
        case r'profileComplete':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.profileComplete = valueDes;
          break;
        case r'roles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roles.replace(valueDes);
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(MeResponseTypeEnum),
          ) as MeResponseTypeEnum;
          result.type = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  MeResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = MeResponseBuilder();
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

class MeResponseTypeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CUSTOMER')
  static const MeResponseTypeEnum CUSTOMER = _$meResponseTypeEnum_CUSTOMER;
  @BuiltValueEnumConst(wireName: r'STAFF')
  static const MeResponseTypeEnum STAFF = _$meResponseTypeEnum_STAFF;

  static Serializer<MeResponseTypeEnum> get serializer => _$meResponseTypeEnumSerializer;

  const MeResponseTypeEnum._(String name): super(name);

  static BuiltSet<MeResponseTypeEnum> get values => _$meResponseTypeEnumValues;
  static MeResponseTypeEnum valueOf(String name) => _$meResponseTypeEnumValueOf(name);
}

