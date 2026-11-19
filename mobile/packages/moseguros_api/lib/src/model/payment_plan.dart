//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_plan.g.dart';

class PaymentPlan extends EnumClass {

  @BuiltValueEnumConst(wireName: r'FULL')
  static const PaymentPlan FULL = _$FULL;
  @BuiltValueEnumConst(wireName: r'SEMIANNUAL')
  static const PaymentPlan SEMIANNUAL = _$SEMIANNUAL;
  @BuiltValueEnumConst(wireName: r'QUARTERLY')
  static const PaymentPlan QUARTERLY = _$QUARTERLY;
  @BuiltValueEnumConst(wireName: r'MONTHLY')
  static const PaymentPlan MONTHLY = _$MONTHLY;

  static Serializer<PaymentPlan> get serializer => _$paymentPlanSerializer;

  const PaymentPlan._(String name): super(name);

  static BuiltSet<PaymentPlan> get values => _$values;
  static PaymentPlan valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentPlanMixin = Object with _$PaymentPlanMixin;

