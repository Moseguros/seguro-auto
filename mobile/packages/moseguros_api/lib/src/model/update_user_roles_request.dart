//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_user_roles_request.g.dart';

/// UpdateUserRolesRequest
///
/// Properties:
/// * [roleCodes] - Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class UpdateUserRolesRequest implements Built<UpdateUserRolesRequest, UpdateUserRolesRequestBuilder> {
  /// Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)
  @BuiltValueField(wireName: r'roleCodes')
  BuiltList<String> get roleCodes;

  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateUserRolesRequest._();

  factory UpdateUserRolesRequest([void updates(UpdateUserRolesRequestBuilder b)]) = _$UpdateUserRolesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateUserRolesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateUserRolesRequest> get serializer => _$UpdateUserRolesRequestSerializer();
}

class _$UpdateUserRolesRequestSerializer implements PrimitiveSerializer<UpdateUserRolesRequest> {
  @override
  final Iterable<Type> types = const [UpdateUserRolesRequest, _$UpdateUserRolesRequest];

  @override
  final String wireName = r'UpdateUserRolesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateUserRolesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'roleCodes';
    yield serializers.serialize(
      object.roleCodes,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
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
    UpdateUserRolesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateUserRolesRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'roleCodes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roleCodes.replace(valueDes);
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
  UpdateUserRolesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateUserRolesRequestBuilder();
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

