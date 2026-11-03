//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/rating_rule_error.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_rules_validation_response.g.dart';

/// RatingRulesValidationResponse
///
/// Properties:
/// * [errors] 
/// * [valid] 
@BuiltValue()
abstract class RatingRulesValidationResponse implements Built<RatingRulesValidationResponse, RatingRulesValidationResponseBuilder> {
  @BuiltValueField(wireName: r'errors')
  BuiltList<RatingRuleError> get errors;

  @BuiltValueField(wireName: r'valid')
  bool get valid;

  RatingRulesValidationResponse._();

  factory RatingRulesValidationResponse([void updates(RatingRulesValidationResponseBuilder b)]) = _$RatingRulesValidationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingRulesValidationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingRulesValidationResponse> get serializer => _$RatingRulesValidationResponseSerializer();
}

class _$RatingRulesValidationResponseSerializer implements PrimitiveSerializer<RatingRulesValidationResponse> {
  @override
  final Iterable<Type> types = const [RatingRulesValidationResponse, _$RatingRulesValidationResponse];

  @override
  final String wireName = r'RatingRulesValidationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingRulesValidationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'errors';
    yield serializers.serialize(
      object.errors,
      specifiedType: const FullType(BuiltList, [FullType(RatingRuleError)]),
    );
    yield r'valid';
    yield serializers.serialize(
      object.valid,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingRulesValidationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingRulesValidationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'errors':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RatingRuleError)]),
          ) as BuiltList<RatingRuleError>;
          result.errors.replace(valueDes);
          break;
        case r'valid':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.valid = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RatingRulesValidationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingRulesValidationResponseBuilder();
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

