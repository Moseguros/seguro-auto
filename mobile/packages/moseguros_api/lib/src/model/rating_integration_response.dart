//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_integration_response.g.dart';

/// RatingIntegrationResponse
///
/// Properties:
/// * [ratingEndpointUrl] 
/// * [ratingMode] 
/// * [ratingSecretRef] 
/// * [version] 
@BuiltValue()
abstract class RatingIntegrationResponse implements Built<RatingIntegrationResponse, RatingIntegrationResponseBuilder> {
  @BuiltValueField(wireName: r'ratingEndpointUrl')
  String? get ratingEndpointUrl;

  @BuiltValueField(wireName: r'ratingMode')
  RatingIntegrationResponseRatingModeEnum get ratingMode;
  // enum ratingModeEnum {  INTERNAL,  EXTERNAL,  };

  @BuiltValueField(wireName: r'ratingSecretRef')
  String? get ratingSecretRef;

  @BuiltValueField(wireName: r'version')
  int get version;

  RatingIntegrationResponse._();

  factory RatingIntegrationResponse([void updates(RatingIntegrationResponseBuilder b)]) = _$RatingIntegrationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingIntegrationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingIntegrationResponse> get serializer => _$RatingIntegrationResponseSerializer();
}

class _$RatingIntegrationResponseSerializer implements PrimitiveSerializer<RatingIntegrationResponse> {
  @override
  final Iterable<Type> types = const [RatingIntegrationResponse, _$RatingIntegrationResponse];

  @override
  final String wireName = r'RatingIntegrationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingIntegrationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.ratingEndpointUrl != null) {
      yield r'ratingEndpointUrl';
      yield serializers.serialize(
        object.ratingEndpointUrl,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'ratingMode';
    yield serializers.serialize(
      object.ratingMode,
      specifiedType: const FullType(RatingIntegrationResponseRatingModeEnum),
    );
    if (object.ratingSecretRef != null) {
      yield r'ratingSecretRef';
      yield serializers.serialize(
        object.ratingSecretRef,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'version';
    yield serializers.serialize(
      object.version,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingIntegrationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingIntegrationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'ratingEndpointUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.ratingEndpointUrl = valueDes;
          break;
        case r'ratingMode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(RatingIntegrationResponseRatingModeEnum),
          ) as RatingIntegrationResponseRatingModeEnum;
          result.ratingMode = valueDes;
          break;
        case r'ratingSecretRef':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.ratingSecretRef = valueDes;
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
  RatingIntegrationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingIntegrationResponseBuilder();
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

class RatingIntegrationResponseRatingModeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INTERNAL')
  static const RatingIntegrationResponseRatingModeEnum INTERNAL = _$ratingIntegrationResponseRatingModeEnum_INTERNAL;
  @BuiltValueEnumConst(wireName: r'EXTERNAL')
  static const RatingIntegrationResponseRatingModeEnum EXTERNAL = _$ratingIntegrationResponseRatingModeEnum_EXTERNAL;

  static Serializer<RatingIntegrationResponseRatingModeEnum> get serializer => _$ratingIntegrationResponseRatingModeEnumSerializer;

  const RatingIntegrationResponseRatingModeEnum._(String name): super(name);

  static BuiltSet<RatingIntegrationResponseRatingModeEnum> get values => _$ratingIntegrationResponseRatingModeEnumValues;
  static RatingIntegrationResponseRatingModeEnum valueOf(String name) => _$ratingIntegrationResponseRatingModeEnumValueOf(name);
}

