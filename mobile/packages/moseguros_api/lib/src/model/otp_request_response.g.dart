// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_request_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OtpRequestResponseChannelEnum _$otpRequestResponseChannelEnum_SMS =
    const OtpRequestResponseChannelEnum._('SMS');
const OtpRequestResponseChannelEnum _$otpRequestResponseChannelEnum_WHATSAPP =
    const OtpRequestResponseChannelEnum._('WHATSAPP');
const OtpRequestResponseChannelEnum _$otpRequestResponseChannelEnum_EMAIL =
    const OtpRequestResponseChannelEnum._('EMAIL');

OtpRequestResponseChannelEnum _$otpRequestResponseChannelEnumValueOf(
    String name) {
  switch (name) {
    case 'SMS':
      return _$otpRequestResponseChannelEnum_SMS;
    case 'WHATSAPP':
      return _$otpRequestResponseChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpRequestResponseChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpRequestResponseChannelEnum>
    _$otpRequestResponseChannelEnumValues = BuiltSet<
        OtpRequestResponseChannelEnum>(const <OtpRequestResponseChannelEnum>[
  _$otpRequestResponseChannelEnum_SMS,
  _$otpRequestResponseChannelEnum_WHATSAPP,
  _$otpRequestResponseChannelEnum_EMAIL,
]);

Serializer<OtpRequestResponseChannelEnum>
    _$otpRequestResponseChannelEnumSerializer =
    _$OtpRequestResponseChannelEnumSerializer();

class _$OtpRequestResponseChannelEnumSerializer
    implements PrimitiveSerializer<OtpRequestResponseChannelEnum> {
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
  final Iterable<Type> types = const <Type>[OtpRequestResponseChannelEnum];
  @override
  final String wireName = 'OtpRequestResponseChannelEnum';

  @override
  Object serialize(
          Serializers serializers, OtpRequestResponseChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpRequestResponseChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpRequestResponseChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpRequestResponse extends OtpRequestResponse {
  @override
  final String challengeId;
  @override
  final OtpRequestResponseChannelEnum channel;
  @override
  final DateTime expiresAt;
  @override
  final bool fallbackUsed;
  @override
  final int resendAfterSeconds;

  factory _$OtpRequestResponse(
          [void Function(OtpRequestResponseBuilder)? updates]) =>
      (OtpRequestResponseBuilder()..update(updates))._build();

  _$OtpRequestResponse._(
      {required this.challengeId,
      required this.channel,
      required this.expiresAt,
      required this.fallbackUsed,
      required this.resendAfterSeconds})
      : super._();
  @override
  OtpRequestResponse rebuild(
          void Function(OtpRequestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpRequestResponseBuilder toBuilder() =>
      OtpRequestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpRequestResponse &&
        challengeId == other.challengeId &&
        channel == other.channel &&
        expiresAt == other.expiresAt &&
        fallbackUsed == other.fallbackUsed &&
        resendAfterSeconds == other.resendAfterSeconds;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, challengeId.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, fallbackUsed.hashCode);
    _$hash = $jc(_$hash, resendAfterSeconds.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpRequestResponse')
          ..add('challengeId', challengeId)
          ..add('channel', channel)
          ..add('expiresAt', expiresAt)
          ..add('fallbackUsed', fallbackUsed)
          ..add('resendAfterSeconds', resendAfterSeconds))
        .toString();
  }
}

class OtpRequestResponseBuilder
    implements Builder<OtpRequestResponse, OtpRequestResponseBuilder> {
  _$OtpRequestResponse? _$v;

  String? _challengeId;
  String? get challengeId => _$this._challengeId;
  set challengeId(String? challengeId) => _$this._challengeId = challengeId;

  OtpRequestResponseChannelEnum? _channel;
  OtpRequestResponseChannelEnum? get channel => _$this._channel;
  set channel(OtpRequestResponseChannelEnum? channel) =>
      _$this._channel = channel;

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

  OtpRequestResponseBuilder() {
    OtpRequestResponse._defaults(this);
  }

  OtpRequestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _challengeId = $v.challengeId;
      _channel = $v.channel;
      _expiresAt = $v.expiresAt;
      _fallbackUsed = $v.fallbackUsed;
      _resendAfterSeconds = $v.resendAfterSeconds;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpRequestResponse other) {
    _$v = other as _$OtpRequestResponse;
  }

  @override
  void update(void Function(OtpRequestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpRequestResponse build() => _build();

  _$OtpRequestResponse _build() {
    final _$result = _$v ??
        _$OtpRequestResponse._(
          challengeId: BuiltValueNullFieldError.checkNotNull(
              challengeId, r'OtpRequestResponse', 'challengeId'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'OtpRequestResponse', 'channel'),
          expiresAt: BuiltValueNullFieldError.checkNotNull(
              expiresAt, r'OtpRequestResponse', 'expiresAt'),
          fallbackUsed: BuiltValueNullFieldError.checkNotNull(
              fallbackUsed, r'OtpRequestResponse', 'fallbackUsed'),
          resendAfterSeconds: BuiltValueNullFieldError.checkNotNull(
              resendAfterSeconds, r'OtpRequestResponse', 'resendAfterSeconds'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
