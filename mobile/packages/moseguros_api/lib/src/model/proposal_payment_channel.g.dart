// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_payment_channel.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalPaymentChannel _$WEB = const ProposalPaymentChannel._('WEB');
const ProposalPaymentChannel _$APP = const ProposalPaymentChannel._('APP');
const ProposalPaymentChannel _$BROKER_LINK =
    const ProposalPaymentChannel._('BROKER_LINK');

ProposalPaymentChannel _$valueOf(String name) {
  switch (name) {
    case 'WEB':
      return _$WEB;
    case 'APP':
      return _$APP;
    case 'BROKER_LINK':
      return _$BROKER_LINK;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalPaymentChannel> _$values =
    BuiltSet<ProposalPaymentChannel>(const <ProposalPaymentChannel>[
  _$WEB,
  _$APP,
  _$BROKER_LINK,
]);

class _$ProposalPaymentChannelMeta {
  const _$ProposalPaymentChannelMeta();
  ProposalPaymentChannel get WEB => _$WEB;
  ProposalPaymentChannel get APP => _$APP;
  ProposalPaymentChannel get BROKER_LINK => _$BROKER_LINK;
  ProposalPaymentChannel valueOf(String name) => _$valueOf(name);
  BuiltSet<ProposalPaymentChannel> get values => _$values;
}

abstract class _$ProposalPaymentChannelMixin {
  // ignore: non_constant_identifier_names
  _$ProposalPaymentChannelMeta get ProposalPaymentChannel =>
      const _$ProposalPaymentChannelMeta();
}

Serializer<ProposalPaymentChannel> _$proposalPaymentChannelSerializer =
    _$ProposalPaymentChannelSerializer();

class _$ProposalPaymentChannelSerializer
    implements PrimitiveSerializer<ProposalPaymentChannel> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'WEB': 'WEB',
    'APP': 'APP',
    'BROKER_LINK': 'BROKER_LINK',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'WEB': 'WEB',
    'APP': 'APP',
    'BROKER_LINK': 'BROKER_LINK',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalPaymentChannel];
  @override
  final String wireName = 'ProposalPaymentChannel';

  @override
  Object serialize(Serializers serializers, ProposalPaymentChannel object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalPaymentChannel deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalPaymentChannel.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
