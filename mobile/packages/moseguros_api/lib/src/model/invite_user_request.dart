//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'invite_user_request.g.dart';

/// InviteUserRequest
///
/// Properties:
/// * [email] 
/// * [fullName] 
/// * [roleCodes] - Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)
@BuiltValue()
abstract class InviteUserRequest implements Built<InviteUserRequest, InviteUserRequestBuilder> {
  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'fullName')
  String get fullName;

  /// Perfis permitidos no tipo da organização (400 ROLE_NOT_ALLOWED_FOR_ORGANIZATION)
  @BuiltValueField(wireName: r'roleCodes')
  BuiltList<String> get roleCodes;

  InviteUserRequest._();

  factory InviteUserRequest([void updates(InviteUserRequestBuilder b)]) = _$InviteUserRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(InviteUserRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<InviteUserRequest> get serializer => _$InviteUserRequestSerializer();
}

class _$InviteUserRequestSerializer implements PrimitiveSerializer<InviteUserRequest> {
  @override
  final Iterable<Type> types = const [InviteUserRequest, _$InviteUserRequest];

  @override
  final String wireName = r'InviteUserRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    InviteUserRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'email';
    yield serializers.serialize(
      object.email,
      specifiedType: const FullType(String),
    );
    yield r'fullName';
    yield serializers.serialize(
      object.fullName,
      specifiedType: const FullType(String),
    );
    yield r'roleCodes';
    yield serializers.serialize(
      object.roleCodes,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    InviteUserRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required InviteUserRequestBuilder result,
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
        case r'fullName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.fullName = valueDes;
          break;
        case r'roleCodes':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roleCodes.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  InviteUserRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = InviteUserRequestBuilder();
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

