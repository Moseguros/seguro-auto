//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'instalment_status.g.dart';

class InstalmentStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DUE')
  static const InstalmentStatus DUE = _$DUE;
  @BuiltValueEnumConst(wireName: r'PAID')
  static const InstalmentStatus PAID = _$PAID;
  @BuiltValueEnumConst(wireName: r'OVERDUE')
  static const InstalmentStatus OVERDUE = _$OVERDUE;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const InstalmentStatus CANCELLED = _$CANCELLED;

  static Serializer<InstalmentStatus> get serializer => _$instalmentStatusSerializer;

  const InstalmentStatus._(String name): super(name);

  static BuiltSet<InstalmentStatus> get values => _$values;
  static InstalmentStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class InstalmentStatusMixin = Object with _$InstalmentStatusMixin;

