// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_status.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalStatus _$DRAFT = const ProposalStatus._('DRAFT');
const ProposalStatus _$UNDER_REVIEW = const ProposalStatus._('UNDER_REVIEW');
const ProposalStatus _$INFO_REQUESTED =
    const ProposalStatus._('INFO_REQUESTED');
const ProposalStatus _$ACCEPTED = const ProposalStatus._('ACCEPTED');
const ProposalStatus _$REJECTED = const ProposalStatus._('REJECTED');
const ProposalStatus _$EXPIRED = const ProposalStatus._('EXPIRED');
const ProposalStatus _$PAID = const ProposalStatus._('PAID');
const ProposalStatus _$CONVERTED = const ProposalStatus._('CONVERTED');

ProposalStatus _$valueOf(String name) {
  switch (name) {
    case 'DRAFT':
      return _$DRAFT;
    case 'UNDER_REVIEW':
      return _$UNDER_REVIEW;
    case 'INFO_REQUESTED':
      return _$INFO_REQUESTED;
    case 'ACCEPTED':
      return _$ACCEPTED;
    case 'REJECTED':
      return _$REJECTED;
    case 'EXPIRED':
      return _$EXPIRED;
    case 'PAID':
      return _$PAID;
    case 'CONVERTED':
      return _$CONVERTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalStatus> _$values =
    BuiltSet<ProposalStatus>(const <ProposalStatus>[
  _$DRAFT,
  _$UNDER_REVIEW,
  _$INFO_REQUESTED,
  _$ACCEPTED,
  _$REJECTED,
  _$EXPIRED,
  _$PAID,
  _$CONVERTED,
]);

class _$ProposalStatusMeta {
  const _$ProposalStatusMeta();
  ProposalStatus get DRAFT => _$DRAFT;
  ProposalStatus get UNDER_REVIEW => _$UNDER_REVIEW;
  ProposalStatus get INFO_REQUESTED => _$INFO_REQUESTED;
  ProposalStatus get ACCEPTED => _$ACCEPTED;
  ProposalStatus get REJECTED => _$REJECTED;
  ProposalStatus get EXPIRED => _$EXPIRED;
  ProposalStatus get PAID => _$PAID;
  ProposalStatus get CONVERTED => _$CONVERTED;
  ProposalStatus valueOf(String name) => _$valueOf(name);
  BuiltSet<ProposalStatus> get values => _$values;
}

abstract class _$ProposalStatusMixin {
  // ignore: non_constant_identifier_names
  _$ProposalStatusMeta get ProposalStatus => const _$ProposalStatusMeta();
}

Serializer<ProposalStatus> _$proposalStatusSerializer =
    _$ProposalStatusSerializer();

class _$ProposalStatusSerializer
    implements PrimitiveSerializer<ProposalStatus> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'DRAFT': 'DRAFT',
    'UNDER_REVIEW': 'UNDER_REVIEW',
    'INFO_REQUESTED': 'INFO_REQUESTED',
    'ACCEPTED': 'ACCEPTED',
    'REJECTED': 'REJECTED',
    'EXPIRED': 'EXPIRED',
    'PAID': 'PAID',
    'CONVERTED': 'CONVERTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'DRAFT': 'DRAFT',
    'UNDER_REVIEW': 'UNDER_REVIEW',
    'INFO_REQUESTED': 'INFO_REQUESTED',
    'ACCEPTED': 'ACCEPTED',
    'REJECTED': 'REJECTED',
    'EXPIRED': 'EXPIRED',
    'PAID': 'PAID',
    'CONVERTED': 'CONVERTED',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalStatus];
  @override
  final String wireName = 'ProposalStatus';

  @override
  Object serialize(Serializers serializers, ProposalStatus object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalStatus deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalStatus.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
