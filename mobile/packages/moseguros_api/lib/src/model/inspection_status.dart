//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'inspection_status.g.dart';

class InspectionStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'PENDING')
  static const InspectionStatus PENDING = _$PENDING;
  @BuiltValueEnumConst(wireName: r'SUBMITTED')
  static const InspectionStatus SUBMITTED = _$SUBMITTED;
  @BuiltValueEnumConst(wireName: r'APPROVED')
  static const InspectionStatus APPROVED = _$APPROVED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const InspectionStatus REJECTED = _$REJECTED;

  static Serializer<InspectionStatus> get serializer => _$inspectionStatusSerializer;

  const InspectionStatus._(String name): super(name);

  static BuiltSet<InspectionStatus> get values => _$values;
  static InspectionStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class InspectionStatusMixin = Object with _$InspectionStatusMixin;

