//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_next_action_type.g.dart';

class PaymentNextActionType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'AWAIT_CUSTOMER_CONFIRMATION')
  static const PaymentNextActionType AWAIT_CUSTOMER_CONFIRMATION = _$AWAIT_CUSTOMER_CONFIRMATION;
  @BuiltValueEnumConst(wireName: r'CHECKING_STATUS')
  static const PaymentNextActionType CHECKING_STATUS = _$CHECKING_STATUS;
  @BuiltValueEnumConst(wireName: r'PAY_BY_REFERENCE')
  static const PaymentNextActionType PAY_BY_REFERENCE = _$PAY_BY_REFERENCE;
  @BuiltValueEnumConst(wireName: r'NONE')
  static const PaymentNextActionType NONE = _$NONE;

  static Serializer<PaymentNextActionType> get serializer => _$paymentNextActionTypeSerializer;

  const PaymentNextActionType._(String name): super(name);

  static BuiltSet<PaymentNextActionType> get values => _$values;
  static PaymentNextActionType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentNextActionTypeMixin = Object with _$PaymentNextActionTypeMixin;

