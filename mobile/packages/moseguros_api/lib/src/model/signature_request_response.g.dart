// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'signature_request_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const SignatureRequestResponseChannelEnum
    _$signatureRequestResponseChannelEnum_SMS =
    const SignatureRequestResponseChannelEnum._('SMS');
const SignatureRequestResponseChannelEnum
    _$signatureRequestResponseChannelEnum_WHATSAPP =
    const SignatureRequestResponseChannelEnum._('WHATSAPP');
const SignatureRequestResponseChannelEnum
    _$signatureRequestResponseChannelEnum_EMAIL =
    const SignatureRequestResponseChannelEnum._('EMAIL');

SignatureRequestResponseChannelEnum
    _$signatureRequestResponseChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$signatureRequestResponseChannelEnum_SMS;
    case 'WHATSAPP':
      return _$signatureRequestResponseChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$signatureRequestResponseChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<SignatureRequestResponseChannelEnum>
    _$signatureRequestResponseChannelEnumValues = BuiltSet<
        SignatureRequestResponseChannelEnum>(const <SignatureRequestResponseChannelEnum>[
  _$signatureRequestResponseChannelEnum_SMS,
  _$signatureRequestResponseChannelEnum_WHATSAPP,
  _$signatureRequestResponseChannelEnum_EMAIL,
]);

Serializer<SignatureRequestResponseChannelEnum>
    _$signatureRequestResponseChannelEnumSerializer =
    _$SignatureRequestResponseChannelEnumSerializer();

class _$SignatureRequestResponseChannelEnumSerializer
    implements PrimitiveSerializer<SignatureRequestResponseChannelEnum> {
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
    SignatureRequestResponseChannelEnum
  ];
  @override
  final String wireName = 'SignatureRequestResponseChannelEnum';

  @override
  Object serialize(
          Serializers serializers, SignatureRequestResponseChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  SignatureRequestResponseChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      SignatureRequestResponseChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$SignatureRequestResponse extends SignatureRequestResponse {
  @override
  final String challengeId;
  @override
  final SignatureRequestResponseChannelEnum channel;
  @override
  final String destinationMasked;
  @override
  final String documentFileId;
  @override
  final String documentHash;
  @override
  final DateTime expiresAt;
  @override
  final bool fallbackUsed;
  @override
  final int resendAfterSeconds;

  factory _$SignatureRequestResponse(
          [void Function(SignatureRequestResponseBuilder)? updates]) =>
      (SignatureRequestResponseBuilder()..update(updates))._build();

  _$SignatureRequestResponse._(
      {required this.challengeId,
      required this.channel,
      required this.destinationMasked,
      required this.documentFileId,
      required this.documentHash,
      required this.expiresAt,
      required this.fallbackUsed,
      required this.resendAfterSeconds})
      : super._();
  @override
  SignatureRequestResponse rebuild(
          void Function(SignatureRequestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SignatureRequestResponseBuilder toBuilder() =>
      SignatureRequestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SignatureRequestResponse &&
        challengeId == other.challengeId &&
        channel == other.channel &&
        destinationMasked == other.destinationMasked &&
        documentFileId == other.documentFileId &&
        documentHash == other.documentHash &&
        expiresAt == other.expiresAt &&
        fallbackUsed == other.fallbackUsed &&
        resendAfterSeconds == other.resendAfterSeconds;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, challengeId.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, destinationMasked.hashCode);
    _$hash = $jc(_$hash, documentFileId.hashCode);
    _$hash = $jc(_$hash, documentHash.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, fallbackUsed.hashCode);
    _$hash = $jc(_$hash, resendAfterSeconds.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SignatureRequestResponse')
          ..add('challengeId', challengeId)
          ..add('channel', channel)
          ..add('destinationMasked', destinationMasked)
          ..add('documentFileId', documentFileId)
          ..add('documentHash', documentHash)
          ..add('expiresAt', expiresAt)
          ..add('fallbackUsed', fallbackUsed)
          ..add('resendAfterSeconds', resendAfterSeconds))
        .toString();
  }
}

class SignatureRequestResponseBuilder
    implements
        Builder<SignatureRequestResponse, SignatureRequestResponseBuilder> {
  _$SignatureRequestResponse? _$v;

  String? _challengeId;
  String? get challengeId => _$this._challengeId;
  set challengeId(String? challengeId) => _$this._challengeId = challengeId;

  SignatureRequestResponseChannelEnum? _channel;
  SignatureRequestResponseChannelEnum? get channel => _$this._channel;
  set channel(SignatureRequestResponseChannelEnum? channel) =>
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

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  bool? _fallbackUsed;
  bool? get fallbackUsed => _$this._fallbackUsed;
  set fallbackUsed(bool? fallbackUsed) => _$this._fallbackUsed = fallbackUsed;

  int? _resendAfterSeconds;
  int? get resendAfterSeconds => _$this._resendAfterSeconds;
  set resendAfterSeconds(int? resendAfterSeconds) =>
      _$this._resendAfterSeconds = resendAfterSeconds;

  SignatureRequestResponseBuilder() {
    SignatureRequestResponse._defaults(this);
  }

  SignatureRequestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _challengeId = $v.challengeId;
      _channel = $v.channel;
      _destinationMasked = $v.destinationMasked;
      _documentFileId = $v.documentFileId;
      _documentHash = $v.documentHash;
      _expiresAt = $v.expiresAt;
      _fallbackUsed = $v.fallbackUsed;
      _resendAfterSeconds = $v.resendAfterSeconds;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SignatureRequestResponse other) {
    _$v = other as _$SignatureRequestResponse;
  }

  @override
  void update(void Function(SignatureRequestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SignatureRequestResponse build() => _build();

  _$SignatureRequestResponse _build() {
    final _$result = _$v ??
        _$SignatureRequestResponse._(
          challengeId: BuiltValueNullFieldError.checkNotNull(
              challengeId, r'SignatureRequestResponse', 'challengeId'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'SignatureRequestResponse', 'channel'),
          destinationMasked: BuiltValueNullFieldError.checkNotNull(
              destinationMasked,
              r'SignatureRequestResponse',
              'destinationMasked'),
          documentFileId: BuiltValueNullFieldError.checkNotNull(
              documentFileId, r'SignatureRequestResponse', 'documentFileId'),
          documentHash: BuiltValueNullFieldError.checkNotNull(
              documentHash, r'SignatureRequestResponse', 'documentHash'),
          expiresAt: BuiltValueNullFieldError.checkNotNull(
              expiresAt, r'SignatureRequestResponse', 'expiresAt'),
          fallbackUsed: BuiltValueNullFieldError.checkNotNull(
              fallbackUsed, r'SignatureRequestResponse', 'fallbackUsed'),
          resendAfterSeconds: BuiltValueNullFieldError.checkNotNull(
              resendAfterSeconds,
              r'SignatureRequestResponse',
              'resendAfterSeconds'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
