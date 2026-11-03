//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'unavailable_reason.g.dart';

class UnavailableReason extends EnumClass {

  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'TARIFF_MISSING')
  static const UnavailableReason TARIFF_MISSING = _$TARIFF_MISSING;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'TARIFF_INVALID')
  static const UnavailableReason TARIFF_INVALID = _$TARIFF_INVALID;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'COVERAGE_NOT_OFFERED')
  static const UnavailableReason COVERAGE_NOT_OFFERED = _$COVERAGE_NOT_OFFERED;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'COVERAGE_REQUIREMENTS_NOT_MET')
  static const UnavailableReason COVERAGE_REQUIREMENTS_NOT_MET = _$COVERAGE_REQUIREMENTS_NOT_MET;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'CAPITAL_NOT_OFFERED')
  static const UnavailableReason CAPITAL_NOT_OFFERED = _$CAPITAL_NOT_OFFERED;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'VEHICLE_VALUE_REQUIRED')
  static const UnavailableReason VEHICLE_VALUE_REQUIRED = _$VEHICLE_VALUE_REQUIRED;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'DURATION_NOT_RATED')
  static const UnavailableReason DURATION_NOT_RATED = _$DURATION_NOT_RATED;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'NO_MATCHING_ROW')
  static const UnavailableReason NO_MATCHING_ROW = _$NO_MATCHING_ROW;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'INSURER_TIMEOUT')
  static const UnavailableReason INSURER_TIMEOUT = _$INSURER_TIMEOUT;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'INSURER_UNAVAILABLE')
  static const UnavailableReason INSURER_UNAVAILABLE = _$INSURER_UNAVAILABLE;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'INSURER_DECLINED')
  static const UnavailableReason INSURER_DECLINED = _$INSURER_DECLINED;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'EXTERNAL_INVALID_RESPONSE')
  static const UnavailableReason EXTERNAL_INVALID_RESPONSE = _$EXTERNAL_INVALID_RESPONSE;
  /// Motivo de uma cotação NOT_AVAILABLE (traduzido em quoteUnavailable.<REASON>)
  @BuiltValueEnumConst(wireName: r'EVALUATION_ERROR')
  static const UnavailableReason EVALUATION_ERROR = _$EVALUATION_ERROR;

  static Serializer<UnavailableReason> get serializer => _$unavailableReasonSerializer;

  const UnavailableReason._(String name): super(name);

  static BuiltSet<UnavailableReason> get values => _$values;
  static UnavailableReason valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class UnavailableReasonMixin = Object with _$UnavailableReasonMixin;

