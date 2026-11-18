//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'failure_code.g.dart';

class FailureCode extends EnumClass {

  @BuiltValueEnumConst(wireName: r'CUSTOMER_CANCELLED')
  static const FailureCode CUSTOMER_CANCELLED = _$CUSTOMER_CANCELLED;
  @BuiltValueEnumConst(wireName: r'INSUFFICIENT_BALANCE')
  static const FailureCode INSUFFICIENT_BALANCE = _$INSUFFICIENT_BALANCE;
  @BuiltValueEnumConst(wireName: r'INVALID_MSISDN')
  static const FailureCode INVALID_MSISDN = _$INVALID_MSISDN;
  @BuiltValueEnumConst(wireName: r'PROVIDER_REJECTED')
  static const FailureCode PROVIDER_REJECTED = _$PROVIDER_REJECTED;
  @BuiltValueEnumConst(wireName: r'PROVIDER_CONFIG_ERROR')
  static const FailureCode PROVIDER_CONFIG_ERROR = _$PROVIDER_CONFIG_ERROR;
  @BuiltValueEnumConst(wireName: r'PROVIDER_UNAVAILABLE')
  static const FailureCode PROVIDER_UNAVAILABLE = _$PROVIDER_UNAVAILABLE;
  @BuiltValueEnumConst(wireName: r'TIMEOUT_EXPIRED')
  static const FailureCode TIMEOUT_EXPIRED = _$TIMEOUT_EXPIRED;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const FailureCode EXPIRED = _$EXPIRED;
  @BuiltValueEnumConst(wireName: r'SUPERSEDED')
  static const FailureCode SUPERSEDED = _$SUPERSEDED;
  @BuiltValueEnumConst(wireName: r'MANUAL')
  static const FailureCode MANUAL = _$MANUAL;

  static Serializer<FailureCode> get serializer => _$failureCodeSerializer;

  const FailureCode._(String name): super(name);

  static BuiltSet<FailureCode> get values => _$values;
  static FailureCode valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class FailureCodeMixin = Object with _$FailureCodeMixin;

