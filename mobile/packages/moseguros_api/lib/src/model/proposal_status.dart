//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_status.g.dart';

class ProposalStatus extends EnumClass {

  @BuiltValueEnumConst(wireName: r'DRAFT')
  static const ProposalStatus DRAFT = _$DRAFT;
  @BuiltValueEnumConst(wireName: r'UNDER_REVIEW')
  static const ProposalStatus UNDER_REVIEW = _$UNDER_REVIEW;
  @BuiltValueEnumConst(wireName: r'INFO_REQUESTED')
  static const ProposalStatus INFO_REQUESTED = _$INFO_REQUESTED;
  @BuiltValueEnumConst(wireName: r'ACCEPTED')
  static const ProposalStatus ACCEPTED = _$ACCEPTED;
  @BuiltValueEnumConst(wireName: r'REJECTED')
  static const ProposalStatus REJECTED = _$REJECTED;
  @BuiltValueEnumConst(wireName: r'EXPIRED')
  static const ProposalStatus EXPIRED = _$EXPIRED;
  @BuiltValueEnumConst(wireName: r'PAID')
  static const ProposalStatus PAID = _$PAID;
  @BuiltValueEnumConst(wireName: r'CONVERTED')
  static const ProposalStatus CONVERTED = _$CONVERTED;

  static Serializer<ProposalStatus> get serializer => _$proposalStatusSerializer;

  const ProposalStatus._(String name): super(name);

  static BuiltSet<ProposalStatus> get values => _$values;
  static ProposalStatus valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class ProposalStatusMixin = Object with _$ProposalStatusMixin;

