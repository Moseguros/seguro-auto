//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'rating_rule_error.g.dart';

/// RatingRuleError
///
/// Properties:
/// * [code] 
/// * [field] 
/// * [message] 
@BuiltValue()
abstract class RatingRuleError implements Built<RatingRuleError, RatingRuleErrorBuilder> {
  @BuiltValueField(wireName: r'code')
  String get code;

  @BuiltValueField(wireName: r'field')
  String get field;

  @BuiltValueField(wireName: r'message')
  String? get message;

  RatingRuleError._();

  factory RatingRuleError([void updates(RatingRuleErrorBuilder b)]) = _$RatingRuleError;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(RatingRuleErrorBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<RatingRuleError> get serializer => _$RatingRuleErrorSerializer();
}

class _$RatingRuleErrorSerializer implements PrimitiveSerializer<RatingRuleError> {
  @override
  final Iterable<Type> types = const [RatingRuleError, _$RatingRuleError];

  @override
  final String wireName = r'RatingRuleError';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    RatingRuleError object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'code';
    yield serializers.serialize(
      object.code,
      specifiedType: const FullType(String),
    );
    yield r'field';
    yield serializers.serialize(
      object.field,
      specifiedType: const FullType(String),
    );
    if (object.message != null) {
      yield r'message';
      yield serializers.serialize(
        object.message,
        specifiedType: const FullType(String),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    RatingRuleError object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required RatingRuleErrorBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'code':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.code = valueDes;
          break;
        case r'field':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.field = valueDes;
          break;
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.message = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  RatingRuleError deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = RatingRuleErrorBuilder();
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

