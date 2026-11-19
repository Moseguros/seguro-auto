//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:moseguros_api/src/model/payment_method_response.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_methods_response.g.dart';

/// PaymentMethodsResponse
///
/// Properties:
/// * [methods] 
@BuiltValue()
abstract class PaymentMethodsResponse implements Built<PaymentMethodsResponse, PaymentMethodsResponseBuilder> {
  @BuiltValueField(wireName: r'methods')
  BuiltList<PaymentMethodResponse>? get methods;

  PaymentMethodsResponse._();

  factory PaymentMethodsResponse([void updates(PaymentMethodsResponseBuilder b)]) = _$PaymentMethodsResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentMethodsResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentMethodsResponse> get serializer => _$PaymentMethodsResponseSerializer();
}

class _$PaymentMethodsResponseSerializer implements PrimitiveSerializer<PaymentMethodsResponse> {
  @override
  final Iterable<Type> types = const [PaymentMethodsResponse, _$PaymentMethodsResponse];

  @override
  final String wireName = r'PaymentMethodsResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentMethodsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.methods != null) {
      yield r'methods';
      yield serializers.serialize(
        object.methods,
        specifiedType: const FullType(BuiltList, [FullType(PaymentMethodResponse)]),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentMethodsResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentMethodsResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'methods':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(BuiltList, [FullType(PaymentMethodResponse)]),
          ) as BuiltList<PaymentMethodResponse>;
          result.methods.replace(valueDes);
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentMethodsResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentMethodsResponseBuilder();
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

