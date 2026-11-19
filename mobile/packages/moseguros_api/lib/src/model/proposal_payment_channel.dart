//
// AUTO-GENERATED FILE, DO NOT MODIFY!
//

// ignore_for_file: unused_element
import 'package:built_collection/built_collection.dart';
import 'package:built_value/built_value.dart';
import 'package:built_value/serializer.dart';

part 'proposal_payment_channel.g.dart';

class ProposalPaymentChannel extends EnumClass {

  @BuiltValueEnumConst(wireName: r'WEB')
  static const ProposalPaymentChannel WEB = _$WEB;
  @BuiltValueEnumConst(wireName: r'APP')
  static const ProposalPaymentChannel APP = _$APP;
  @BuiltValueEnumConst(wireName: r'BROKER_LINK')
  static const ProposalPaymentChannel BROKER_LINK = _$BROKER_LINK;

  static Serializer<ProposalPaymentChannel> get serializer => _$proposalPaymentChannelSerializer;

  const ProposalPaymentChannel._(String name): super(name);

  static BuiltSet<ProposalPaymentChannel> get values => _$values;
  static ProposalPaymentChannel valueOf(String name) => _$valueOf(name);
}

/// Optionally, enum_class can generate a mixin to go with your enum for use
/// with Angular. It exposes your enum constants as getters. So, if you mix it
/// in to your Dart component class, the values become available to the
/// corresponding Angular template.
///
/// Trigger mixin generation by writing a line like this one next to your enum.
abstract class ProposalPaymentChannelMixin = Object with _$ProposalPaymentChannelMixin;

