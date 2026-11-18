//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_method_test_response.g.dart';

/// PaymentMethodTestResponse
///
/// Properties:
/// * [message] 
/// * [result] 
/// * [testedAt] 
@BuiltValue()
abstract class PaymentMethodTestResponse implements Built<PaymentMethodTestResponse, PaymentMethodTestResponseBuilder> {
  @BuiltValueField(wireName: r'message')
  String? get message;

  @BuiltValueField(wireName: r'result')
  PaymentMethodTestResponseResultEnum get result;
  // enum resultEnum {  OK,  FAILED,  NOT_SUPPORTED,  };

  @BuiltValueField(wireName: r'testedAt')
  DateTime get testedAt;

  PaymentMethodTestResponse._();

  factory PaymentMethodTestResponse([void updates(PaymentMethodTestResponseBuilder b)]) = _$PaymentMethodTestResponse;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentMethodTestResponseBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentMethodTestResponse> get serializer => _$PaymentMethodTestResponseSerializer();
}

class _$PaymentMethodTestResponseSerializer implements PrimitiveSerializer<PaymentMethodTestResponse> {
  @override
  final Iterable<Type> types = const [PaymentMethodTestResponse, _$PaymentMethodTestResponse];

  @override
  final String wireName = r'PaymentMethodTestResponse';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentMethodTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.message != null) {
      yield r'message';
      yield serializers.serialize(
        object.message,
        specifiedType: const FullType.nullable(String),
      );
    }
    yield r'result';
    yield serializers.serialize(
      object.result,
      specifiedType: const FullType(PaymentMethodTestResponseResultEnum),
    );
    yield r'testedAt';
    yield serializers.serialize(
      object.testedAt,
      specifiedType: const FullType(DateTime),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentMethodTestResponse object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentMethodTestResponseBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'message':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType.nullable(String),
          ) as String?;
          if (valueDes == null) continue;
          result.message = valueDes;
          break;
        case r'result':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentMethodTestResponseResultEnum),
          ) as PaymentMethodTestResponseResultEnum;
          result.result = valueDes;
          break;
        case r'testedAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.testedAt = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentMethodTestResponse deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentMethodTestResponseBuilder();
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

class PaymentMethodTestResponseResultEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'OK')
  static const PaymentMethodTestResponseResultEnum OK = _$paymentMethodTestResponseResultEnum_OK;
  @BuiltValueEnumConst(wireName: r'FAILED')
  static const PaymentMethodTestResponseResultEnum FAILED = _$paymentMethodTestResponseResultEnum_FAILED;
  @BuiltValueEnumConst(wireName: r'NOT_SUPPORTED')
  static const PaymentMethodTestResponseResultEnum NOT_SUPPORTED = _$paymentMethodTestResponseResultEnum_NOT_SUPPORTED;

  static Serializer<PaymentMethodTestResponseResultEnum> get serializer => _$paymentMethodTestResponseResultEnumSerializer;

  const PaymentMethodTestResponseResultEnum._(String name): super(name);

  static BuiltSet<PaymentMethodTestResponseResultEnum> get values => _$paymentMethodTestResponseResultEnumValues;
  static PaymentMethodTestResponseResultEnum valueOf(String name) => _$paymentMethodTestResponseResultEnumValueOf(name);
}

