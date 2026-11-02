//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'jwk.g.dart';

/// Jwk
///
/// Properties:
/// * [alg] 
/// * [e] - Expoente (Base64 URL)
/// * [kid] 
/// * [kty] 
/// * [n] - Módulo (Base64 URL)
/// * [use] 
@BuiltValue()
abstract class Jwk implements Built<Jwk, JwkBuilder> {
  @BuiltValueField(wireName: r'alg')
  String get alg;

  /// Expoente (Base64 URL)
  @BuiltValueField(wireName: r'e')
  String get e;

  @BuiltValueField(wireName: r'kid')
  String get kid;

  @BuiltValueField(wireName: r'kty')
  String get kty;

  /// Módulo (Base64 URL)
  @BuiltValueField(wireName: r'n')
  String get n;

  @BuiltValueField(wireName: r'use')
  String get use;

  Jwk._();

  factory Jwk([void updates(JwkBuilder b)]) = _$Jwk;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(JwkBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<Jwk> get serializer => _$JwkSerializer();
}

class _$JwkSerializer implements PrimitiveSerializer<Jwk> {
  @override
  final Iterable<Type> types = const [Jwk, _$Jwk];

  @override
  final String wireName = r'Jwk';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    Jwk object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'alg';
    yield serializers.serialize(
      object.alg,
      specifiedType: const FullType(String),
    );
    yield r'e';
    yield serializers.serialize(
      object.e,
      specifiedType: const FullType(String),
    );
    yield r'kid';
    yield serializers.serialize(
      object.kid,
      specifiedType: const FullType(String),
    );
    yield r'kty';
    yield serializers.serialize(
      object.kty,
      specifiedType: const FullType(String),
    );
    yield r'n';
    yield serializers.serialize(
      object.n,
      specifiedType: const FullType(String),
    );
    yield r'use';
    yield serializers.serialize(
      object.use,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    Jwk object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required JwkBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'alg':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.alg = valueDes;
          break;
        case r'e':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.e = valueDes;
          break;
        case r'kid':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.kid = valueDes;
          break;
        case r'kty':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.kty = valueDes;
          break;
        case r'n':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.n = valueDes;
          break;
        case r'use':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.use = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  Jwk deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = JwkBuilder();
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

