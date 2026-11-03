//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'publish_product_version_request.g.dart';

/// PublishProductVersionRequest
///
/// Properties:
/// * [version] 
@BuiltValue()
abstract class PublishProductVersionRequest implements Built<PublishProductVersionRequest, PublishProductVersionRequestBuilder> {
  @BuiltValueField(wireName: r'version')
  int get version;

  PublishProductVersionRequest._();

  factory PublishProductVersionRequest([void updates(PublishProductVersionRequestBuilder b)]) = _$PublishProductVersionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PublishProductVersionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PublishProductVersionRequest> get serializer => _$PublishProductVersionRequestSerializer();
}

class _$PublishProductVersionRequestSerializer implements PrimitiveSerializer<PublishProductVersionRequest> {
  @override
  final Iterable<Type> types = const [PublishProductVersionRequest, _$PublishProductVersionRequest];

  @override
  final String wireName = r'PublishProductVersionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PublishProductVersionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PublishProductVersionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PublishProductVersionRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
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
  PublishProductVersionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PublishProductVersionRequestBuilder();
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

