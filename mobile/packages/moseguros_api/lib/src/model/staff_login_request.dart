//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'staff_login_request.g.dart';

/// StaffLoginRequest
///
/// Properties:
/// * [email] 
/// * [password] 
/// * [totpCode] 
@BuiltValue()
abstract class StaffLoginRequest implements Built<StaffLoginRequest, StaffLoginRequestBuilder> {
  @BuiltValueField(wireName: r'email')
  String get email;

  @BuiltValueField(wireName: r'password')
  String get password;

  @BuiltValueField(wireName: r'totpCode')
  String get totpCode;

  StaffLoginRequest._();

  factory StaffLoginRequest([void updates(StaffLoginRequestBuilder b)]) = _$StaffLoginRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(StaffLoginRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<StaffLoginRequest> get serializer => _$StaffLoginRequestSerializer();
}

class _$StaffLoginRequestSerializer implements PrimitiveSerializer<StaffLoginRequest> {
  @override
  final Iterable<Type> types = const [StaffLoginRequest, _$StaffLoginRequest];

  @override
  final String wireName = r'StaffLoginRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    StaffLoginRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'email';
    yield serializers.serialize(
      object.email,
      specifiedType: const FullType(String),
    );
    yield r'password';
    yield serializers.serialize(
      object.password,
      specifiedType: const FullType(String),
    );
    yield r'totpCode';
    yield serializers.serialize(
      object.totpCode,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    StaffLoginRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required StaffLoginRequestBuilder result,
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
        case r'password':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.password = valueDes;
          break;
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
  StaffLoginRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = StaffLoginRequestBuilder();
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

