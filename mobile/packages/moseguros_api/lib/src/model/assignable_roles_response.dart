//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'assignable_roles_response.g.dart';

/// AssignableRolesResponse
///
/// Properties:
/// * [managerInviteRoles] - Perfis a dar ao primeiro gestor no ecrã de criação de organização
/// * [managerRoles] - Perfis que fazem de alguém gestor (INSURER_ADMIN, ORG_ADMIN ou PLATFORM_ADMIN)
/// * [roles] - Perfis atribuíveis
@BuiltValue()
abstract class AssignableRolesResponse implements Built<AssignableRolesResponse, AssignableRolesResponseBuilder> {
  /// Perfis a dar ao primeiro gestor no ecrã de criação de organização
  @BuiltValueField(wireName: r'managerInviteRoles')
  BuiltList<String> get managerInviteRoles;

  /// Perfis que fazem de alguém gestor (INSURER_ADMIN, ORG_ADMIN ou PLATFORM_ADMIN)
  @BuiltValueField(wireName: r'managerRoles')
  BuiltList<String> get managerRoles;

  /// Perfis atribuíveis
  @BuiltValueField(wireName: r'roles')
  BuiltList<String> get roles;

  AssignableRolesResponse._();

  factory AssignableRolesResponse([void updates(AssignableRolesResponseBuilder b)]) = _$AssignableRolesResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AssignableRolesResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AssignableRolesResponse> get serializer => _$AssignableRolesResponseSerializer();
}

class _$AssignableRolesResponseSerializer implements PrimitiveSerializer<AssignableRolesResponse> {
  @override
  final Iterable<Type> types = const [AssignableRolesResponse, _$AssignableRolesResponse];

  @override
  final String wireName = r'AssignableRolesResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AssignableRolesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'managerInviteRoles';
    yield serializers.serialize(
      object.managerInviteRoles,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'managerRoles';
    yield serializers.serialize(
      object.managerRoles,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'roles';
    yield serializers.serialize(
      object.roles,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    AssignableRolesResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AssignableRolesResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'managerInviteRoles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.managerInviteRoles.replace(valueDes);
          break;
        case r'managerRoles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.managerRoles.replace(valueDes);
          break;
        case r'roles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roles.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  AssignableRolesResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AssignableRolesResponseBuilder();
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

