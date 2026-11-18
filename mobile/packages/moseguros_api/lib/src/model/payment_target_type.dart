//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_target_type.g.dart';

class PaymentTargetType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROPOSAL')
  static const PaymentTargetType PROPOSAL = _$PROPOSAL;
  @BuiltValueEnumConst(wireName: r'INSTALMENT')
  static const PaymentTargetType INSTALMENT = _$INSTALMENT;
  @BuiltValueEnumConst(wireName: r'RENEWAL')
  static const PaymentTargetType RENEWAL = _$RENEWAL;
  @BuiltValueEnumConst(wireName: r'ENDORSEMENT')
  static const PaymentTargetType ENDORSEMENT = _$ENDORSEMENT;
  @BuiltValueEnumConst(wireName: r'FLEET_INCLUSION')
  static const PaymentTargetType FLEET_INCLUSION = _$FLEET_INCLUSION;

  static Serializer<PaymentTargetType> get serializer => _$paymentTargetTypeSerializer;

  const PaymentTargetType._(String name): super(name);

  static BuiltSet<PaymentTargetType> get values => _$values;
  static PaymentTargetType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentTargetTypeMixin = Object with _$PaymentTargetTypeMixin;

