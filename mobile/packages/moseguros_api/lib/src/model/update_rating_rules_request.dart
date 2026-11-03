//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'update_rating_rules_request.g.dart';

/// UpdateRatingRulesRequest
///
/// Properties:
/// * [rating] - Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
/// * [version] 
@BuiltValue()
abstract class UpdateRatingRulesRequest implements Built<UpdateRatingRulesRequest, UpdateRatingRulesRequestBuilder> {
  /// Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
  @BuiltValueField(wireName: r'rating')
  JsonObject get rating;

  @BuiltValueField(wireName: r'version')
  int get version;

  UpdateRatingRulesRequest._();

  factory UpdateRatingRulesRequest([void updates(UpdateRatingRulesRequestBuilder b)]) = _$UpdateRatingRulesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(UpdateRatingRulesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<UpdateRatingRulesRequest> get serializer => _$UpdateRatingRulesRequestSerializer();
}

class _$UpdateRatingRulesRequestSerializer implements PrimitiveSerializer<UpdateRatingRulesRequest> {
  @override
  final Iterable<Type> types = const [UpdateRatingRulesRequest, _$UpdateRatingRulesRequest];

  @override
  final String wireName = r'UpdateRatingRulesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    UpdateRatingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'rating';
    yield serializers.serialize(
      object.rating,
      specifiedType: const FullType(JsonObject),
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
    UpdateRatingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required UpdateRatingRulesRequestBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'rating':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(JsonObject),
          ) as JsonObject;
          result.rating = valueDes;
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
  UpdateRatingRulesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = UpdateRatingRulesRequestBuilder();
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

