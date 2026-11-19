//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_event_type.g.dart';

class PaymentEventType extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CREATED')
  static const PaymentEventType CREATED = _$CREATED;
  @BuiltValueEnumConst(wireName: r'DISPATCHED')
  static const PaymentEventType DISPATCHED = _$DISPATCHED;
  @BuiltValueEnumConst(wireName: r'PROVIDER_RESPONSE')
  static const PaymentEventType PROVIDER_RESPONSE = _$PROVIDER_RESPONSE;
  @BuiltValueEnumConst(wireName: r'STATUS_QUERY')
  static const PaymentEventType STATUS_QUERY = _$STATUS_QUERY;
  @BuiltValueEnumConst(wireName: r'STATUS_CHANGED')
  static const PaymentEventType STATUS_CHANGED = _$STATUS_CHANGED;
  @BuiltValueEnumConst(wireName: r'REFERENCE_PAID')
  static const PaymentEventType REFERENCE_PAID = _$REFERENCE_PAID;
  @BuiltValueEnumConst(wireName: r'MANUAL_RESOLUTION')
  static const PaymentEventType MANUAL_RESOLUTION = _$MANUAL_RESOLUTION;

  static Serializer<PaymentEventType> get serializer => _$paymentEventTypeSerializer;

  const PaymentEventType._(String name): super(name);

  static BuiltSet<PaymentEventType> get values => _$values;
  static PaymentEventType valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentEventTypeMixin = Object with _$PaymentEventTypeMixin;

