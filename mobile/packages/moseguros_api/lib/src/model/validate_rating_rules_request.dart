//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/json_object.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'validate_rating_rules_request.g.dart';

/// ValidateRatingRulesRequest
///
/// Properties:
/// * [rating] - Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
@BuiltValue()
abstract class ValidateRatingRulesRequest implements Built<ValidateRatingRulesRequest, ValidateRatingRulesRequestBuilder> {
  /// Tarifa interna no formato rating-v1 (backend/src/main/resources/schemas/rating-v1.json): schemaVersion, durationFactors e coverages (base e factores). Decimais em texto, dinheiro em centavos.
  @BuiltValueField(wireName: r'rating')
  JsonObject get rating;

  ValidateRatingRulesRequest._();

  factory ValidateRatingRulesRequest([void updates(ValidateRatingRulesRequestBuilder b)]) = _$ValidateRatingRulesRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ValidateRatingRulesRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ValidateRatingRulesRequest> get serializer => _$ValidateRatingRulesRequestSerializer();
}

class _$ValidateRatingRulesRequestSerializer implements PrimitiveSerializer<ValidateRatingRulesRequest> {
  @override
  final Iterable<Type> types = const [ValidateRatingRulesRequest, _$ValidateRatingRulesRequest];

  @override
  final String wireName = r'ValidateRatingRulesRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ValidateRatingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'rating';
    yield serializers.serialize(
      object.rating,
      specifiedType: const FullType(JsonObject),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ValidateRatingRulesRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ValidateRatingRulesRequestBuilder result,
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
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ValidateRatingRulesRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ValidateRatingRulesRequestBuilder();
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

