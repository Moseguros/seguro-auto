//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_method_version_request.g.dart';

/// PaymentMethodVersionRequest
///
/// Properties:
/// * [version] 
@BuiltValue()
abstract class PaymentMethodVersionRequest implements Built<PaymentMethodVersionRequest, PaymentMethodVersionRequestBuilder> {
  @BuiltValueField(wireName: r'version')
  int get version;

  PaymentMethodVersionRequest._();

  factory PaymentMethodVersionRequest([void updates(PaymentMethodVersionRequestBuilder b)]) = _$PaymentMethodVersionRequest;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentMethodVersionRequestBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentMethodVersionRequest> get serializer => _$PaymentMethodVersionRequestSerializer();
}

class _$PaymentMethodVersionRequestSerializer implements PrimitiveSerializer<PaymentMethodVersionRequest> {
  @override
  final Iterable<Type> types = const [PaymentMethodVersionRequest, _$PaymentMethodVersionRequest];

  @override
  final String wireName = r'PaymentMethodVersionRequest';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentMethodVersionRequest object, {
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
    PaymentMethodVersionRequest object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentMethodVersionRequestBuilder result,
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
  PaymentMethodVersionRequest deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentMethodVersionRequestBuilder();
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

