//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_method_option.g.dart';

/// PaymentMethodOption
///
/// Properties:
/// * [displayName] 
/// * [method] 
/// * [sortOrder] 
@BuiltValue()
abstract class PaymentMethodOption implements Built<PaymentMethodOption, PaymentMethodOptionBuilder> {
  @BuiltValueField(wireName: r'displayName')
  String? get displayName;

  @BuiltValueField(wireName: r'method')
  PaymentMethodOptionMethodEnum get method;
  // enum methodEnum {  MPESA,  BANK_REFERENCE,  };

  @BuiltValueField(wireName: r'sortOrder')
  int get sortOrder;

  PaymentMethodOption._();

  factory PaymentMethodOption([void updates(PaymentMethodOptionBuilder b)]) = _$PaymentMethodOption;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentMethodOptionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentMethodOption> get serializer => _$PaymentMethodOptionSerializer();
}

class _$PaymentMethodOptionSerializer implements PrimitiveSerializer<PaymentMethodOption> {
  @override
  final Iterable<Type> types = const [PaymentMethodOption, _$PaymentMethodOption];

  @override
  final String wireName = r'PaymentMethodOption';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentMethodOption object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.displayName != null) {
      yield r'displayName';
      yield serializers.serialize(
        object.displayName,
        specifiedType: const FullType(String),
      );
    }
    yield r'method';
    yield serializers.serialize(
      object.method,
      specifiedType: const FullType(PaymentMethodOptionMethodEnum),
    );
    yield r'sortOrder';
    yield serializers.serialize(
      object.sortOrder,
      specifiedType: const FullType(int),
    );
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentMethodOption object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentMethodOptionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'displayName':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.displayName = valueDes;
          break;
        case r'method':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentMethodOptionMethodEnum),
          ) as PaymentMethodOptionMethodEnum;
          result.method = valueDes;
          break;
        case r'sortOrder':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(int),
          ) as int;
          result.sortOrder = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentMethodOption deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentMethodOptionBuilder();
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

class PaymentMethodOptionMethodEnum extends EnumClass {

  @BuiltValueEnumConst(wireName: r'MPESA')
  static const PaymentMethodOptionMethodEnum MPESA = _$paymentMethodOptionMethodEnum_MPESA;
  @BuiltValueEnumConst(wireName: r'BANK_REFERENCE')
  static const PaymentMethodOptionMethodEnum BANK_REFERENCE = _$paymentMethodOptionMethodEnum_BANK_REFERENCE;

  static Serializer<PaymentMethodOptionMethodEnum> get serializer => _$paymentMethodOptionMethodEnumSerializer;

  const PaymentMethodOptionMethodEnum._(String name): super(name);

  static BuiltSet<PaymentMethodOptionMethodEnum> get values => _$paymentMethodOptionMethodEnumValues;
  static PaymentMethodOptionMethodEnum valueOf(String name) => _$paymentMethodOptionMethodEnumValueOf(name);
}

