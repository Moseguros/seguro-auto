// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_channel.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalChannel _$APP = const ProposalChannel._('APP');
const ProposalChannel _$WEB = const ProposalChannel._('WEB');
const ProposalChannel _$BROKER = const ProposalChannel._('BROKER');

ProposalChannel _$valueOf(String name) {
  switch (name) {
    case 'APP':
      return _$APP;
    case 'WEB':
      return _$WEB;
    case 'BROKER':
      return _$BROKER;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalChannel> _$values =
    BuiltSet<ProposalChannel>(const <ProposalChannel>[
  _$APP,
  _$WEB,
  _$BROKER,
]);

class _$ProposalChannelMeta {
  const _$ProposalChannelMeta();
  ProposalChannel get APP => _$APP;
  ProposalChannel get WEB => _$WEB;
  ProposalChannel get BROKER => _$BROKER;
  ProposalChannel valueOf(String name) => _$valueOf(name);
  BuiltSet<ProposalChannel> get values => _$values;
}

abstract class _$ProposalChannelMixin {
  // ignore: non_constant_identifier_names
  _$ProposalChannelMeta get ProposalChannel => const _$ProposalChannelMeta();
}

Serializer<ProposalChannel> _$proposalChannelSerializer =
    _$ProposalChannelSerializer();

class _$ProposalChannelSerializer
    implements PrimitiveSerializer<ProposalChannel> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'APP': 'APP',
    'WEB': 'WEB',
    'BROKER': 'BROKER',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'APP': 'APP',
    'WEB': 'WEB',
    'BROKER': 'BROKER',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalChannel];
  @override
  final String wireName = 'ProposalChannel';

  @override
  Object serialize(Serializers serializers, ProposalChannel object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalChannel deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalChannel.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
