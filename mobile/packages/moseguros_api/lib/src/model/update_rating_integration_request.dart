//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_rating_integration_request.g.dart';

/// UpdateRatingIntegrationRequest
///
/// Properties:
/// * [ratingEndpointUrl] 
/// * [ratingMode] 
/// * [ratingSecretRef] 
/// * [version] 
@BuiltValue()
abstract class UpdateRatingIntegrationRequest implements Built<UpdateRatingIntegrationRequest, UpdateRatingIntegrationRequestBuilder> {
  @BuiltValueField(wireName: r'ratingEndpointUrl')
  String? get ratingEndpointUrl;

  @BuiltValueField(wireName: r'ratingMode')
  UpdateRatingIntegrationRequestRatingModeEnum get ratingMode;
  // enum ratingModeEnum {  INTERNAL,  EXTERNAL,  };

  @BuiltValueField(wireName: r'ratingSecretRef')
  String? get ratingSecretRef;

  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateRatingIntegrationRequest._();

  factory UpdateRatingIntegrationRequest([void updates(UpdateRatingIntegrationRequestBuilder b)]) = _$UpdateRatingIntegrationRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateRatingIntegrationRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateRatingIntegrationRequest> get serializer => _$UpdateRatingIntegrationRequestSerializer();
}

class _$UpdateRatingIntegrationRequestSerializer implements PrimitiveSerializer<UpdateRatingIntegrationRequest> {
  @override
  final Iterable<Type> types = const [UpdateRatingIntegrationRequest, _$UpdateRatingIntegrationRequest];

  @override
  final String wireName = r'UpdateRatingIntegrationRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateRatingIntegrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.ratingEndpointUrl != null) {
      yield r'ratingEndpointUrl';
      yield serializers.serialize(
        object.ratingEndpointUrl,
        specifiedType: const FullType(String),
      );
    }
    yield r'ratingMode';
    yield serializers.serialize(
      object.ratingMode,
      specifiedType: const FullType(UpdateRatingIntegrationRequestRatingModeEnum),
    );
    if (object.ratingSecretRef != null) {
      yield r'ratingSecretRef';
      yield serializers.serialize(
        object.ratingSecretRef,
        specifiedType: const FullType(String),
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
    UpdateRatingIntegrationRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateRatingIntegrationRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'ratingEndpointUrl':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.ratingEndpointUrl = valueDes;
          break;
        case r'ratingMode':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(UpdateRatingIntegrationRequestRatingModeEnum),
          ) as UpdateRatingIntegrationRequestRatingModeEnum;
          result.ratingMode = valueDes;
          break;
        case r'ratingSecretRef':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
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
  UpdateRatingIntegrationRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateRatingIntegrationRequestBuilder();
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

class UpdateRatingIntegrationRequestRatingModeEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'INTERNAL')
  static const UpdateRatingIntegrationRequestRatingModeEnum INTERNAL = _$updateRatingIntegrationRequestRatingModeEnum_INTERNAL;
  @BuiltValueEnumConst(wireName: r'EXTERNAL')
  static const UpdateRatingIntegrationRequestRatingModeEnum EXTERNAL = _$updateRatingIntegrationRequestRatingModeEnum_EXTERNAL;

  static Serializer<UpdateRatingIntegrationRequestRatingModeEnum> get serializer => _$updateRatingIntegrationRequestRatingModeEnumSerializer;

  const UpdateRatingIntegrationRequestRatingModeEnum._(String name): super(name);

  static BuiltSet<UpdateRatingIntegrationRequestRatingModeEnum> get values => _$updateRatingIntegrationRequestRatingModeEnumValues;
  static UpdateRatingIntegrationRequestRatingModeEnum valueOf(String name) => _$updateRatingIntegrationRequestRatingModeEnumValueOf(name);
}

