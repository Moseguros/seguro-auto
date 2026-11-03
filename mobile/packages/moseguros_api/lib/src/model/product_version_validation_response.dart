//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/rule_error.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'product_version_validation_response.g.dart';

/// ProductVersionValidationResponse
///
/// Properties:
/// * [errors] 
/// * [publishable] 
@BuiltValue()
abstract class ProductVersionValidationResponse implements Built<ProductVersionValidationResponse, ProductVersionValidationResponseBuilder> {
  @BuiltValueField(wireName: r'errors')
  BuiltList<RuleError> get errors;

  @BuiltValueField(wireName: r'publishable')
  bool get publishable;

  ProductVersionValidationResponse._();

  factory ProductVersionValidationResponse([void updates(ProductVersionValidationResponseBuilder b)]) = _$ProductVersionValidationResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(ProductVersionValidationResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<ProductVersionValidationResponse> get serializer => _$ProductVersionValidationResponseSerializer();
}

class _$ProductVersionValidationResponseSerializer implements PrimitiveSerializer<ProductVersionValidationResponse> {
  @override
  final Iterable<Type> types = const [ProductVersionValidationResponse, _$ProductVersionValidationResponse];

  @override
  final String wireName = r'ProductVersionValidationResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    ProductVersionValidationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'errors';
    yield serializers.serialize(
      object.errors,
      specifiedType: const FullType(BuiltList, [FullType(RuleError)]),
    );
    yield r'publishable';
    yield serializers.serialize(
      object.publishable,
      specifiedType: const FullType(bool),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    ProductVersionValidationResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required ProductVersionValidationResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'errors':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(RuleError)]),
          ) as BuiltList<RuleError>;
          result.errors.replace(valueDes);
          break;
        case r'publishable':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(bool),
          ) as bool;
          result.publishable = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  ProductVersionValidationResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = ProductVersionValidationResponseBuilder();
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

