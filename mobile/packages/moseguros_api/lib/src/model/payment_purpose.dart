//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_purpose.g.dart';

class PaymentPurpose extends EnumClass {

  @BuiltValueEnumConst(wireName: r'NEW_POLICY')
  static const PaymentPurpose NEW_POLICY = _$NEW_POLICY;
  @BuiltValueEnumConst(wireName: r'INSTALMENT')
  static const PaymentPurpose INSTALMENT = _$INSTALMENT;
  @BuiltValueEnumConst(wireName: r'RENEWAL')
  static const PaymentPurpose RENEWAL = _$RENEWAL;
  @BuiltValueEnumConst(wireName: r'ENDORSEMENT')
  static const PaymentPurpose ENDORSEMENT = _$ENDORSEMENT;
  @BuiltValueEnumConst(wireName: r'FLEET_INCLUSION')
  static const PaymentPurpose FLEET_INCLUSION = _$FLEET_INCLUSION;

  static Serializer<PaymentPurpose> get serializer => _$paymentPurposeSerializer;

  const PaymentPurpose._(String name): super(name);

  static BuiltSet<PaymentPurpose> get values => _$values;
  static PaymentPurpose valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentPurposeMixin = Object with _$PaymentPurposeMixin;

