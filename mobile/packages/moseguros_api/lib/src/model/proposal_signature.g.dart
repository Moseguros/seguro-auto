// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'proposal_signature.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const ProposalSignatureChannelEnum _$proposalSignatureChannelEnum_SMS =
    const ProposalSignatureChannelEnum._('SMS');
const ProposalSignatureChannelEnum _$proposalSignatureChannelEnum_WHATSAPP =
    const ProposalSignatureChannelEnum._('WHATSAPP');
const ProposalSignatureChannelEnum _$proposalSignatureChannelEnum_EMAIL =
    const ProposalSignatureChannelEnum._('EMAIL');

ProposalSignatureChannelEnum _$proposalSignatureChannelEnumValueOf(
    String name) {
  switch (name) {
    case 'SMS':
      return _$proposalSignatureChannelEnum_SMS;
    case 'WHATSAPP':
      return _$proposalSignatureChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$proposalSignatureChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<ProposalSignatureChannelEnum>
    _$proposalSignatureChannelEnumValues =
    BuiltSet<ProposalSignatureChannelEnum>(const <ProposalSignatureChannelEnum>[
  _$proposalSignatureChannelEnum_SMS,
  _$proposalSignatureChannelEnum_WHATSAPP,
  _$proposalSignatureChannelEnum_EMAIL,
]);

Serializer<ProposalSignatureChannelEnum>
    _$proposalSignatureChannelEnumSerializer =
    _$ProposalSignatureChannelEnumSerializer();

class _$ProposalSignatureChannelEnumSerializer
    implements PrimitiveSerializer<ProposalSignatureChannelEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'SMS': 'SMS',
    'WHATSAPP': 'WHATSAPP',
    'EMAIL': 'EMAIL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'SMS': 'SMS',
    'WHATSAPP': 'WHATSAPP',
    'EMAIL': 'EMAIL',
  };

  @override
  final Iterable<Type> types = const <Type>[ProposalSignatureChannelEnum];
  @override
  final String wireName = 'ProposalSignatureChannelEnum';

  @override
  Object serialize(Serializers serializers, ProposalSignatureChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  ProposalSignatureChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      ProposalSignatureChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$ProposalSignature extends ProposalSignature {
  @override
  final bool assisted;
  @override
  final ProposalSignatureChannelEnum channel;
  @override
  final String destinationMasked;
  @override
  final String documentFileId;
  @override
  final String documentHash;
  @override
  final DateTime signedAt;

  factory _$ProposalSignature(
          [void Function(ProposalSignatureBuilder)? updates]) =>
      (ProposalSignatureBuilder()..update(updates))._build();

  _$ProposalSignature._(
      {required this.assisted,
      required this.channel,
      required this.destinationMasked,
      required this.documentFileId,
      required this.documentHash,
      required this.signedAt})
      : super._();
  @override
  ProposalSignature rebuild(void Function(ProposalSignatureBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ProposalSignatureBuilder toBuilder() =>
      ProposalSignatureBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ProposalSignature &&
        assisted == other.assisted &&
        channel == other.channel &&
        destinationMasked == other.destinationMasked &&
        documentFileId == other.documentFileId &&
        documentHash == other.documentHash &&
        signedAt == other.signedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, assisted.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, destinationMasked.hashCode);
    _$hash = $jc(_$hash, documentFileId.hashCode);
    _$hash = $jc(_$hash, documentHash.hashCode);
    _$hash = $jc(_$hash, signedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ProposalSignature')
          ..add('assisted', assisted)
          ..add('channel', channel)
          ..add('destinationMasked', destinationMasked)
          ..add('documentFileId', documentFileId)
          ..add('documentHash', documentHash)
          ..add('signedAt', signedAt))
        .toString();
  }
}

class ProposalSignatureBuilder
    implements Builder<ProposalSignature, ProposalSignatureBuilder> {
  _$ProposalSignature? _$v;

  bool? _assisted;
  bool? get assisted => _$this._assisted;
  set assisted(bool? assisted) => _$this._assisted = assisted;

  ProposalSignatureChannelEnum? _channel;
  ProposalSignatureChannelEnum? get channel => _$this._channel;
  set channel(ProposalSignatureChannelEnum? channel) =>
      _$this._channel = channel;

  String? _destinationMasked;
  String? get destinationMasked => _$this._destinationMasked;
  set destinationMasked(String? destinationMasked) =>
      _$this._destinationMasked = destinationMasked;

  String? _documentFileId;
  String? get documentFileId => _$this._documentFileId;
  set documentFileId(String? documentFileId) =>
      _$this._documentFileId = documentFileId;

  String? _documentHash;
  String? get documentHash => _$this._documentHash;
  set documentHash(String? documentHash) => _$this._documentHash = documentHash;

  DateTime? _signedAt;
  DateTime? get signedAt => _$this._signedAt;
  set signedAt(DateTime? signedAt) => _$this._signedAt = signedAt;

  ProposalSignatureBuilder() {
    ProposalSignature._defaults(this);
  }

  ProposalSignatureBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _assisted = $v.assisted;
      _channel = $v.channel;
      _destinationMasked = $v.destinationMasked;
      _documentFileId = $v.documentFileId;
      _documentHash = $v.documentHash;
      _signedAt = $v.signedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ProposalSignature other) {
    _$v = other as _$ProposalSignature;
  }

  @override
  void update(void Function(ProposalSignatureBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ProposalSignature build() => _build();

  _$ProposalSignature _build() {
    final _$result = _$v ??
        _$ProposalSignature._(
          assisted: BuiltValueNullFieldError.checkNotNull(
              assisted, r'ProposalSignature', 'assisted'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'ProposalSignature', 'channel'),
          destinationMasked: BuiltValueNullFieldError.checkNotNull(
              destinationMasked, r'ProposalSignature', 'destinationMasked'),
          documentFileId: BuiltValueNullFieldError.checkNotNull(
              documentFileId, r'ProposalSignature', 'documentFileId'),
          documentHash: BuiltValueNullFieldError.checkNotNull(
              documentHash, r'ProposalSignature', 'documentHash'),
          signedAt: BuiltValueNullFieldError.checkNotNull(
              signedAt, r'ProposalSignature', 'signedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
