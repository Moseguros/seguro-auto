//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'acknowledge_suitability_request.g.dart';

/// AcknowledgeSuitabilityRequest
///
/// Properties:
/// * [version] - Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
@BuiltValue()
abstract class AcknowledgeSuitabilityRequest implements Built<AcknowledgeSuitabilityRequest, AcknowledgeSuitabilityRequestBuilder> {
  /// Versão lida; diferente da gravada → 409 CONCURRENT_MODIFICATION
  @BuiltValueField(wireName: r'version')
  int get version;

  AcknowledgeSuitabilityRequest._();

  factory AcknowledgeSuitabilityRequest([void updates(AcknowledgeSuitabilityRequestBuilder b)]) = _$AcknowledgeSuitabilityRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(AcknowledgeSuitabilityRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<AcknowledgeSuitabilityRequest> get serializer => _$AcknowledgeSuitabilityRequestSerializer();
}

class _$AcknowledgeSuitabilityRequestSerializer implements PrimitiveSerializer<AcknowledgeSuitabilityRequest> {
  @override
  final Iterable<Type> types = const [AcknowledgeSuitabilityRequest, _$AcknowledgeSuitabilityRequest];

  @override
  final String wireName = r'AcknowledgeSuitabilityRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    AcknowledgeSuitabilityRequest object, {
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
    AcknowledgeSuitabilityRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required AcknowledgeSuitabilityRequestBuilder result,
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
  AcknowledgeSuitabilityRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = AcknowledgeSuitabilityRequestBuilder();
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

