//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'sla_status.g.dart';

class SlaStatus extends EnumClass {

  /// Só em UNDER_REVIEW
  @BuiltValueEnumConst(wireName: r'ON_TIME')
  static const SlaStatus ON_TIME = _$ON_TIME;
  /// Só em UNDER_REVIEW
  @BuiltValueEnumConst(wireName: r'ALERT')
  static const SlaStatus ALERT = _$ALERT;
  /// Só em UNDER_REVIEW
  @BuiltValueEnumConst(wireName: r'OVERDUE')
  static const SlaStatus OVERDUE = _$OVERDUE;

  static Serializer<SlaStatus> get serializer => _$slaStatusSerializer;

  const SlaStatus._(String name): super(name);

  static BuiltSet<SlaStatus> get values => _$values;
  static SlaStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class SlaStatusMixin = Object with _$SlaStatusMixin;

