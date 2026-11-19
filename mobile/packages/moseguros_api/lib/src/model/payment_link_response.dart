//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_link_response.g.dart';

/// PaymentLinkResponse
///
/// Properties:
/// * [sentAt] 
/// * [sentTo] 
@BuiltValue()
abstract class PaymentLinkResponse implements Built<PaymentLinkResponse, PaymentLinkResponseBuilder> {
  @BuiltValueField(wireName: r'sentAt')
  DateTime get sentAt;

  @BuiltValueField(wireName: r'sentTo')
  String get sentTo;

  PaymentLinkResponse._();

  factory PaymentLinkResponse([void updates(PaymentLinkResponseBuilder b)]) = _$PaymentLinkResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentLinkResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentLinkResponse> get serializer => _$PaymentLinkResponseSerializer();
}

class _$PaymentLinkResponseSerializer implements PrimitiveSerializer<PaymentLinkResponse> {
  @override
  final Iterable<Type> types = const [PaymentLinkResponse, _$PaymentLinkResponse];

  @override
  final String wireName = r'PaymentLinkResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentLinkResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    yield r'sentAt';
    yield serializers.serialize(
      object.sentAt,
      specifiedType: const FullType(DateTime),
    );
    yield r'sentTo';
    yield serializers.serialize(
      object.sentTo,
      specifiedType: const FullType(String),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentLinkResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentLinkResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'sentAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.sentAt = valueDes;
          break;
        case r'sentTo':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.sentTo = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentLinkResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentLinkResponseBuilder();
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

