//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_exception_type.g.dart';

class PaymentExceptionType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'AMOUNT_MISMATCH')
  static const PaymentExceptionType AMOUNT_MISMATCH = _$AMOUNT_MISMATCH;
  @BuiltValueEnumConst(wireName: r'EXPIRED_REFERENCE')
  static const PaymentExceptionType EXPIRED_REFERENCE = _$EXPIRED_REFERENCE;
  @BuiltValueEnumConst(wireName: r'ALREADY_PAID')
  static const PaymentExceptionType ALREADY_PAID = _$ALREADY_PAID;
  @BuiltValueEnumConst(wireName: r'UNKNOWN_REFERENCE')
  static const PaymentExceptionType UNKNOWN_REFERENCE = _$UNKNOWN_REFERENCE;
  @BuiltValueEnumConst(wireName: r'CANCELLED_REFERENCE')
  static const PaymentExceptionType CANCELLED_REFERENCE = _$CANCELLED_REFERENCE;
  @BuiltValueEnumConst(wireName: r'TARGET_NOT_PAYABLE')
  static const PaymentExceptionType TARGET_NOT_PAYABLE = _$TARGET_NOT_PAYABLE;
  @BuiltValueEnumConst(wireName: r'UNRESOLVED_STATUS')
  static const PaymentExceptionType UNRESOLVED_STATUS = _$UNRESOLVED_STATUS;

  static Serializer<PaymentExceptionType> get serializer => _$paymentExceptionTypeSerializer;

  const PaymentExceptionType._(String name): super(name);

  static BuiltSet<PaymentExceptionType> get values => _$values;
  static PaymentExceptionType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentExceptionTypeMixin = Object with _$PaymentExceptionTypeMixin;

