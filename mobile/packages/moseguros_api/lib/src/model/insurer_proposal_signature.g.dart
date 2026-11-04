// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_proposal_signature.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InsurerProposalSignatureChannelEnum
    _$insurerProposalSignatureChannelEnum_SMS =
    const InsurerProposalSignatureChannelEnum._('SMS');
const InsurerProposalSignatureChannelEnum
    _$insurerProposalSignatureChannelEnum_WHATSAPP =
    const InsurerProposalSignatureChannelEnum._('WHATSAPP');
const InsurerProposalSignatureChannelEnum
    _$insurerProposalSignatureChannelEnum_EMAIL =
    const InsurerProposalSignatureChannelEnum._('EMAIL');

InsurerProposalSignatureChannelEnum
    _$insurerProposalSignatureChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$insurerProposalSignatureChannelEnum_SMS;
    case 'WHATSAPP':
      return _$insurerProposalSignatureChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$insurerProposalSignatureChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerProposalSignatureChannelEnum>
    _$insurerProposalSignatureChannelEnumValues = BuiltSet<
        InsurerProposalSignatureChannelEnum>(const <InsurerProposalSignatureChannelEnum>[
  _$insurerProposalSignatureChannelEnum_SMS,
  _$insurerProposalSignatureChannelEnum_WHATSAPP,
  _$insurerProposalSignatureChannelEnum_EMAIL,
]);

const InsurerProposalSignatureInvalidatedReasonEnum
    _$insurerProposalSignatureInvalidatedReasonEnum_CONTENT_CHANGED =
    const InsurerProposalSignatureInvalidatedReasonEnum._('CONTENT_CHANGED');
const InsurerProposalSignatureInvalidatedReasonEnum
    _$insurerProposalSignatureInvalidatedReasonEnum_INFO_REQUESTED =
    const InsurerProposalSignatureInvalidatedReasonEnum._('INFO_REQUESTED');

InsurerProposalSignatureInvalidatedReasonEnum
    _$insurerProposalSignatureInvalidatedReasonEnumValueOf(String name) {
  switch (name) {
    case 'CONTENT_CHANGED':
      return _$insurerProposalSignatureInvalidatedReasonEnum_CONTENT_CHANGED;
    case 'INFO_REQUESTED':
      return _$insurerProposalSignatureInvalidatedReasonEnum_INFO_REQUESTED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InsurerProposalSignatureInvalidatedReasonEnum>
    _$insurerProposalSignatureInvalidatedReasonEnumValues = BuiltSet<
        InsurerProposalSignatureInvalidatedReasonEnum>(const <InsurerProposalSignatureInvalidatedReasonEnum>[
  _$insurerProposalSignatureInvalidatedReasonEnum_CONTENT_CHANGED,
  _$insurerProposalSignatureInvalidatedReasonEnum_INFO_REQUESTED,
]);

Serializer<InsurerProposalSignatureChannelEnum>
    _$insurerProposalSignatureChannelEnumSerializer =
    _$InsurerProposalSignatureChannelEnumSerializer();
Serializer<InsurerProposalSignatureInvalidatedReasonEnum>
    _$insurerProposalSignatureInvalidatedReasonEnumSerializer =
    _$InsurerProposalSignatureInvalidatedReasonEnumSerializer();

class _$InsurerProposalSignatureChannelEnumSerializer
    implements PrimitiveSerializer<InsurerProposalSignatureChannelEnum> {
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
  final Iterable<Type> types = const <Type>[
    InsurerProposalSignatureChannelEnum
  ];
  @override
  final String wireName = 'InsurerProposalSignatureChannelEnum';

  @override
  Object serialize(
          Serializers serializers, InsurerProposalSignatureChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerProposalSignatureChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerProposalSignatureChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerProposalSignatureInvalidatedReasonEnumSerializer
    implements
        PrimitiveSerializer<InsurerProposalSignatureInvalidatedReasonEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'CONTENT_CHANGED': 'CONTENT_CHANGED',
    'INFO_REQUESTED': 'INFO_REQUESTED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'CONTENT_CHANGED': 'CONTENT_CHANGED',
    'INFO_REQUESTED': 'INFO_REQUESTED',
  };

  @override
  final Iterable<Type> types = const <Type>[
    InsurerProposalSignatureInvalidatedReasonEnum
  ];
  @override
  final String wireName = 'InsurerProposalSignatureInvalidatedReasonEnum';

  @override
  Object serialize(Serializers serializers,
          InsurerProposalSignatureInvalidatedReasonEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InsurerProposalSignatureInvalidatedReasonEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InsurerProposalSignatureInvalidatedReasonEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$InsurerProposalSignature extends InsurerProposalSignature {
  @override
  final bool assisted;
  @override
  final InsurerProposalSignatureChannelEnum channel;
  @override
  final String destinationMasked;
  @override
  final String documentFileId;
  @override
  final String documentHash;
  @override
  final DateTime? invalidatedAt;
  @override
  final InsurerProposalSignatureInvalidatedReasonEnum? invalidatedReason;
  @override
  final DateTime signedAt;

  factory _$InsurerProposalSignature(
          [void Function(InsurerProposalSignatureBuilder)? updates]) =>
      (InsurerProposalSignatureBuilder()..update(updates))._build();

  _$InsurerProposalSignature._(
      {required this.assisted,
      required this.channel,
      required this.destinationMasked,
      required this.documentFileId,
      required this.documentHash,
      this.invalidatedAt,
      this.invalidatedReason,
      required this.signedAt})
      : super._();
  @override
  InsurerProposalSignature rebuild(
          void Function(InsurerProposalSignatureBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerProposalSignatureBuilder toBuilder() =>
      InsurerProposalSignatureBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerProposalSignature &&
        assisted == other.assisted &&
        channel == other.channel &&
        destinationMasked == other.destinationMasked &&
        documentFileId == other.documentFileId &&
        documentHash == other.documentHash &&
        invalidatedAt == other.invalidatedAt &&
        invalidatedReason == other.invalidatedReason &&
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
    _$hash = $jc(_$hash, invalidatedAt.hashCode);
    _$hash = $jc(_$hash, invalidatedReason.hashCode);
    _$hash = $jc(_$hash, signedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerProposalSignature')
          ..add('assisted', assisted)
          ..add('channel', channel)
          ..add('destinationMasked', destinationMasked)
          ..add('documentFileId', documentFileId)
          ..add('documentHash', documentHash)
          ..add('invalidatedAt', invalidatedAt)
          ..add('invalidatedReason', invalidatedReason)
          ..add('signedAt', signedAt))
        .toString();
  }
}

class InsurerProposalSignatureBuilder
    implements
        Builder<InsurerProposalSignature, InsurerProposalSignatureBuilder> {
  _$InsurerProposalSignature? _$v;

  bool? _assisted;
  bool? get assisted => _$this._assisted;
  set assisted(bool? assisted) => _$this._assisted = assisted;

  InsurerProposalSignatureChannelEnum? _channel;
  InsurerProposalSignatureChannelEnum? get channel => _$this._channel;
  set channel(InsurerProposalSignatureChannelEnum? channel) =>
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

  DateTime? _invalidatedAt;
  DateTime? get invalidatedAt => _$this._invalidatedAt;
  set invalidatedAt(DateTime? invalidatedAt) =>
      _$this._invalidatedAt = invalidatedAt;

  InsurerProposalSignatureInvalidatedReasonEnum? _invalidatedReason;
  InsurerProposalSignatureInvalidatedReasonEnum? get invalidatedReason =>
      _$this._invalidatedReason;
  set invalidatedReason(
          InsurerProposalSignatureInvalidatedReasonEnum? invalidatedReason) =>
      _$this._invalidatedReason = invalidatedReason;

  DateTime? _signedAt;
  DateTime? get signedAt => _$this._signedAt;
  set signedAt(DateTime? signedAt) => _$this._signedAt = signedAt;

  InsurerProposalSignatureBuilder() {
    InsurerProposalSignature._defaults(this);
  }

  InsurerProposalSignatureBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _assisted = $v.assisted;
      _channel = $v.channel;
      _destinationMasked = $v.destinationMasked;
      _documentFileId = $v.documentFileId;
      _documentHash = $v.documentHash;
      _invalidatedAt = $v.invalidatedAt;
      _invalidatedReason = $v.invalidatedReason;
      _signedAt = $v.signedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerProposalSignature other) {
    _$v = other as _$InsurerProposalSignature;
  }

  @override
  void update(void Function(InsurerProposalSignatureBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerProposalSignature build() => _build();

  _$InsurerProposalSignature _build() {
    final _$result = _$v ??
        _$InsurerProposalSignature._(
          assisted: BuiltValueNullFieldError.checkNotNull(
              assisted, r'InsurerProposalSignature', 'assisted'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'InsurerProposalSignature', 'channel'),
          destinationMasked: BuiltValueNullFieldError.checkNotNull(
              destinationMasked,
              r'InsurerProposalSignature',
              'destinationMasked'),
          documentFileId: BuiltValueNullFieldError.checkNotNull(
              documentFileId, r'InsurerProposalSignature', 'documentFileId'),
          documentHash: BuiltValueNullFieldError.checkNotNull(
              documentHash, r'InsurerProposalSignature', 'documentHash'),
          invalidatedAt: invalidatedAt,
          invalidatedReason: invalidatedReason,
          signedAt: BuiltValueNullFieldError.checkNotNull(
              signedAt, r'InsurerProposalSignature', 'signedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
