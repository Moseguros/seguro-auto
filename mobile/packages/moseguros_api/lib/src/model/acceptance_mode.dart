//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'acceptance_mode.g.dart';

class AcceptanceMode extends EnumClass {

  /// Só em ACCEPTED (e nos estados seguintes)
  @BuiltValueEnumConst(wireName: r'AUTOMATIC')
  static const AcceptanceMode AUTOMATIC = _$AUTOMATIC;
  /// Só em ACCEPTED (e nos estados seguintes)
  @BuiltValueEnumConst(wireName: r'MANUAL')
  static const AcceptanceMode MANUAL = _$MANUAL;

  static Serializer<AcceptanceMode> get serializer => _$acceptanceModeSerializer;

  const AcceptanceMode._(String name): super(name);

  static BuiltSet<AcceptanceMode> get values => _$values;
  static AcceptanceMode valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class AcceptanceModeMixin = Object with _$AcceptanceModeMixin;

