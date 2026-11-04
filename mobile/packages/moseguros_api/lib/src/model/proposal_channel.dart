//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_channel.g.dart';

class ProposalChannel extends EnumClass {

  @BuiltValueEnumConst(wireName: r'APP')
  static const ProposalChannel APP = _$APP;
  @BuiltValueEnumConst(wireName: r'WEB')
  static const ProposalChannel WEB = _$WEB;
  @BuiltValueEnumConst(wireName: r'BROKER')
  static const ProposalChannel BROKER = _$BROKER;

  static Serializer<ProposalChannel> get serializer => _$proposalChannelSerializer;

  const ProposalChannel._(String name): super(name);

  static BuiltSet<ProposalChannel> get values => _$values;
  static ProposalChannel valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class ProposalChannelMixin = Object with _$ProposalChannelMixin;

