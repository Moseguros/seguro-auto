//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'cover_status.g.dart';

class CoverStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'COVERED')
  static const CoverStatus COVERED = _$COVERED;
  @BuiltValueEnumConst(wireName: r'NOT_STARTED')
  static const CoverStatus NOT_STARTED = _$NOT_STARTED;
  @BuiltValueEnumConst(wireName: r'ENDED')
  static const CoverStatus ENDED = _$ENDED;
  @BuiltValueEnumConst(wireName: r'SUSPENDED')
  static const CoverStatus SUSPENDED = _$SUSPENDED;
  @BuiltValueEnumConst(wireName: r'CANCELLED')
  static const CoverStatus CANCELLED = _$CANCELLED;
  @BuiltValueEnumConst(wireName: r'LAPSED')
  static const CoverStatus LAPSED = _$LAPSED;

  static Serializer<CoverStatus> get serializer => _$coverStatusSerializer;

  const CoverStatus._(String name): super(name);

  static BuiltSet<CoverStatus> get values => _$values;
  static CoverStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class CoverStatusMixin = Object with _$CoverStatusMixin;

