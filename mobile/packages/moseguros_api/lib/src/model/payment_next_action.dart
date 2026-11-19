//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:moseguros_api/src/model/payment_next_action_type.dart';
import 'package:moseguros_api/src/model/money.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_next_action.g.dart';

/// PaymentNextAction
///
/// Properties:
/// * [amount] 
/// * [entity] 
/// * [nextCheckAt] 
/// * [reference] 
/// * [timeoutAt] 
/// * [type] 
/// * [validUntil] 
@BuiltValue()
abstract class PaymentNextAction implements Built<PaymentNextAction, PaymentNextActionBuilder> {
  @BuiltValueField(wireName: r'amount')
  Money? get amount;

  @BuiltValueField(wireName: r'entity')
  String? get entity;

  @BuiltValueField(wireName: r'nextCheckAt')
  DateTime? get nextCheckAt;

  @BuiltValueField(wireName: r'reference')
  String? get reference;

  @BuiltValueField(wireName: r'timeoutAt')
  DateTime? get timeoutAt;

  @BuiltValueField(wireName: r'type')
  PaymentNextActionType get type;
  // enum typeEnum {  AWAIT_CUSTOMER_CONFIRMATION,  CHECKING_STATUS,  PAY_BY_REFERENCE,  NONE,  };

  @BuiltValueField(wireName: r'validUntil')
  DateTime? get validUntil;

  PaymentNextAction._();

  factory PaymentNextAction([void updates(PaymentNextActionBuilder b)]) = _$PaymentNextAction;

  @BuiltValueHook(initializeBuilder: true)
  static void _defaults(PaymentNextActionBuilder b) => b;

  @BuiltValueSerializer(custom: true)
  static Serializer<PaymentNextAction> get serializer => _$PaymentNextActionSerializer();
}

class _$PaymentNextActionSerializer implements PrimitiveSerializer<PaymentNextAction> {
  @override
  final Iterable<Type> types = const [PaymentNextAction, _$PaymentNextAction];

  @override
  final String wireName = r'PaymentNextAction';

  Iterable<Object?> _serializeProperties(
    Serializers serializers,
    PaymentNextAction object, {
    FullType specifiedType = FullType.unspecified,
  }) sync* {
    if (object.amount != null) {
      yield r'amount';
      yield serializers.serialize(
        object.amount,
        specifiedType: const FullType(Money),
      );
    }
    if (object.entity != null) {
      yield r'entity';
      yield serializers.serialize(
        object.entity,
        specifiedType: const FullType(String),
      );
    }
    if (object.nextCheckAt != null) {
      yield r'nextCheckAt';
      yield serializers.serialize(
        object.nextCheckAt,
        specifiedType: const FullType(DateTime),
      );
    }
    if (object.reference != null) {
      yield r'reference';
      yield serializers.serialize(
        object.reference,
        specifiedType: const FullType(String),
      );
    }
    if (object.timeoutAt != null) {
      yield r'timeoutAt';
      yield serializers.serialize(
        object.timeoutAt,
        specifiedType: const FullType(DateTime),
      );
    }
    yield r'type';
    yield serializers.serialize(
      object.type,
      specifiedType: const FullType(PaymentNextActionType),
    );
    if (object.validUntil != null) {
      yield r'validUntil';
      yield serializers.serialize(
        object.validUntil,
        specifiedType: const FullType(DateTime),
      );
    }
  }

  @override
  Object serialize(
    Serializers serializers,
    PaymentNextAction object, {
    FullType specifiedType = FullType.unspecified,
  }) {
    return _serializeProperties(serializers, object, specifiedType: specifiedType).toList();
  }

  void _deserializeProperties(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
    required List<Object?> serializedList,
    required PaymentNextActionBuilder result,
    required List<Object?> unhandled,
  }) {
    for (var i = 0; i < serializedList.length; i += 2) {
      final key = serializedList[i] as String;
      final value = serializedList[i + 1];
      switch (key) {
        case r'amount':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(Money),
          ) as Money;
          result.amount.replace(valueDes);
          break;
        case r'entity':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.entity = valueDes;
          break;
        case r'nextCheckAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.nextCheckAt = valueDes;
          break;
        case r'reference':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(String),
          ) as String;
          result.reference = valueDes;
          break;
        case r'timeoutAt':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.timeoutAt = valueDes;
          break;
        case r'type':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(PaymentNextActionType),
          ) as PaymentNextActionType;
          result.type = valueDes;
          break;
        case r'validUntil':
          final valueDes = serializers.deserialize(
            value,
            specifiedType: const FullType(DateTime),
          ) as DateTime;
          result.validUntil = valueDes;
          break;
        default:
          unhandled.add(key);
          unhandled.add(value);
          break;
      }
    }
  }

  @override
  PaymentNextAction deserialize(
    Serializers serializers,
    Object serialized, {
    FullType specifiedType = FullType.unspecified,
  }) {
    final result = PaymentNextActionBuilder();
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

