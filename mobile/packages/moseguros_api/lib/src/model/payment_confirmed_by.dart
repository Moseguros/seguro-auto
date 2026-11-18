//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_confirmed_by.g.dart';

class PaymentConfirmedBy extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PROVIDER')
  static const PaymentConfirmedBy PROVIDER = _$PROVIDER;
  @BuiltValueEnumConst(wireName: r'STATUS_QUERY')
  static const PaymentConfirmedBy STATUS_QUERY = _$STATUS_QUERY;
  @BuiltValueEnumConst(wireName: r'REFERENCE_FILE')
  static const PaymentConfirmedBy REFERENCE_FILE = _$REFERENCE_FILE;
  @BuiltValueEnumConst(wireName: r'MANUAL')
  static const PaymentConfirmedBy MANUAL = _$MANUAL;

  static Serializer<PaymentConfirmedBy> get serializer => _$paymentConfirmedBySerializer;

  const PaymentConfirmedBy._(String name): super(name);

  static BuiltSet<PaymentConfirmedBy> get values => _$values;
  static PaymentConfirmedBy valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentConfirmedByMixin = Object with _$PaymentConfirmedByMixin;

