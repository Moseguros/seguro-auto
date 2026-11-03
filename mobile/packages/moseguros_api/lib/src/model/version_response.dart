//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'version_response.g.dart';

/// VersionResponse
///
/// Properties:
/// * [builtAt] 
/// * [commit] 
/// * [name] 
/// * [version] 
@BuiltValue()
abstract class VersionResponse implements Built<VersionResponse, VersionResponseBuilder> {
  @BuiltValueField(wireName: r'builtAt')
  DateTime? get builtAt;

  @BuiltValueField(wireName: r'commit')
  String? get commit;

  @BuiltValueField(wireName: r'name')
  String? get name;

  @BuiltValueField(wireName: r'version')
  String? get version;

  VersionResponse._();

  factory VersionResponse([void updates(VersionResponseBuilder b)]) = _$VersionResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(VersionResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<VersionResponse> get serializer => _$VersionResponseSerializer();
}

class _$VersionResponseSerializer implements PrimitiveSerializer<VersionResponse> {
  @override
  final Iterable<Type> types = const [VersionResponse, _$VersionResponse];

  @override
  final String wireName = r'VersionResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    VersionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.builtAt != null) {
      yield r'builtAt';
      yield serializers.serialize(
        object.builtAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.commit != null) {
      yield r'commit';
      yield serializers.serialize(
        object.commit,
        specifiedType: const FullType(String),
      );
    }
    if (object.name != null) {
      yield r'name';
      yield serializers.serialize(
        object.name,
        specifiedType: const FullType(String),
      );
    }
    if (object.version != null) {
      yield r'version';
      yield serializers.serialize(
        object.version,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    VersionResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required VersionResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'builtAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.builtAt = valueDes;
          break;
        case r'commit':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.commit = valueDes;
          break;
        case r'name':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.name = valueDes;
          break;
        case r'version':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
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
  VersionResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = VersionResponseBuilder();
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

