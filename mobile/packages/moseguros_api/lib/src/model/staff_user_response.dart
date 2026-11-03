//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'staff_user_response.g.dart';

/// StaffUserResponse
///
/// Properties:
/// * [createdAt] 
/// * [displayName] - Omitido se ainda não tiver nome
/// * [email] 
/// * [id] 
/// * [lastLoginAt] - Omitido se nunca entrou
/// * [roles] 
/// * [status] - LOCKED só enquanto o bloqueio não passou
/// * [version] - Para a alteração de perfis
@BuiltValue()
abstract class StaffUserResponse implements Built<StaffUserResponse, StaffUserResponseBuilder> {
  @BuiltValueField(wireName: r'createdAt')
  DateTime get createdAt;

  /// Omitido se ainda não tiver nome
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'id')
  String get id;

  /// Omitido se nunca entrou
  @BuiltValueField(wireName: r'lastLoginAt')
  DateTime? get lastLoginAt;

  @BuiltValueField(wireName: r'roles')
  BuiltList<String> get roles;

  /// LOCKED só enquanto o bloqueio não passou
  @BuiltValueField(wireName: r'status')
  StaffUserResponseStatusEnum get status;
  // enum statusEnum {  PENDING_ACTIVATION,  ACTIVE,  LOCKED,  DISABLED,  };

  /// Para a alteração de perfis
  @BuiltValueField(wireName: r'version')
  int get version;

  StaffUserResponse._();

  factory StaffUserResponse([void updates(StaffUserResponseBuilder b)]) = _$StaffUserResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(StaffUserResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<StaffUserResponse> get serializer => _$StaffUserResponseSerializer();
}

class _$StaffUserResponseSerializer implements PrimitiveSerializer<StaffUserResponse> {
  @override
  final Iterable<Type> types = const [StaffUserResponse, _$StaffUserResponse];

  @override
  final String wireName = r'StaffUserResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    StaffUserResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'createdAt';
    yield serializers.serialize(
      object.createdAt,
      specifiedType: const FullType(DateTime),
    );
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType(String),
      );
    }
    yield r'email';
    yield serializers.serialize(
      object.email,
      specifiedType: const FullType(String),
    );
    yield r'id';
    yield serializers.serialize(
      object.id,
      specifiedType: const FullType(String),
    );
    if (object.lastLoginAt != null) {
      yield r'lastLoginAt';
      yield serializers.serialize(
        object.lastLoginAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'roles';
    yield serializers.serialize(
      object.roles,
      specifiedType: const FullType(BuiltList, [FullType(String)]),
    );
    yield r'status';
    yield serializers.serialize(
      object.status,
      specifiedType: const FullType(StaffUserResponseStatusEnum),
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
    StaffUserResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required StaffUserResponseBuilder result,
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
        case r'id':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.id = valueDes;
          break;
        case r'lastLoginAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.lastLoginAt = valueDes;
          break;
        case r'roles':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(String)]),
          ) as BuiltList<String>;
          result.roles.replace(valueDes);
          break;
        case r'status':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(StaffUserResponseStatusEnum),
          ) as StaffUserResponseStatusEnum;
          result.status = valueDes;
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
  StaffUserResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = StaffUserResponseBuilder();
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

class StaffUserResponseStatusEnum extends EnumClass {

  /// LOCKED só enquanto o bloqueio não passou
  @BuiltValueEnumConst(wireName: r'PENDING_ACTIVATION')
  static const StaffUserResponseStatusEnum PENDING_ACTIVATION = _$staffUserResponseStatusEnum_PENDING_ACTIVATION;
  /// LOCKED só enquanto o bloqueio não passou
  @BuiltValueEnumConst(wireName: r'ACTIVE')
  static const StaffUserResponseStatusEnum ACTIVE = _$staffUserResponseStatusEnum_ACTIVE;
  /// LOCKED só enquanto o bloqueio não passou
  @BuiltValueEnumConst(wireName: r'LOCKED')
  static const StaffUserResponseStatusEnum LOCKED = _$staffUserResponseStatusEnum_LOCKED;
  /// LOCKED só enquanto o bloqueio não passou
  @BuiltValueEnumConst(wireName: r'DISABLED')
  static const StaffUserResponseStatusEnum DISABLED = _$staffUserResponseStatusEnum_DISABLED;

  static Serializer<StaffUserResponseStatusEnum> get serializer => _$staffUserResponseStatusEnumSerializer;

  const StaffUserResponseStatusEnum._(String name): super(name);

  static BuiltSet<StaffUserResponseStatusEnum> get values => _$staffUserResponseStatusEnumValues;
  static StaffUserResponseStatusEnum valueOf(String name) => _$staffUserResponseStatusEnumValueOf(name);
}

