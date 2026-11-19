//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'payment_resolution.g.dart';

class PaymentResolution extends EnumClass {

  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueEnumConst(wireName: r'LINKED')
  static const PaymentResolution LINKED = _$LINKED;
  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueEnumConst(wireName: r'REFUND')
  static const PaymentResolution REFUND = _$REFUND;
  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueEnumConst(wireName: r'IGNORED')
  static const PaymentResolution IGNORED = _$IGNORED;
  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueEnumConst(wireName: r'MARK_SUCCEEDED')
  static const PaymentResolution MARK_SUCCEEDED = _$MARK_SUCCEEDED;
  /// Resoluções permitidas para o tipo; vazio depois de resolvida
  @BuiltValueEnumConst(wireName: r'MARK_FAILED')
  static const PaymentResolution MARK_FAILED = _$MARK_FAILED;

  static Serializer<PaymentResolution> get serializer => _$paymentResolutionSerializer;

  const PaymentResolution._(String name): super(name);

  static BuiltSet<PaymentResolution> get values => _$values;
  static PaymentResolution valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class PaymentResolutionMixin = Object with _$PaymentResolutionMixin;

