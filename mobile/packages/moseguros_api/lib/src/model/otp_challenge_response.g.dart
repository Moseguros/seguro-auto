// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_challenge_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OtpChallengeResponseChannelEnum _$otpChallengeResponseChannelEnum_SMS =
    const OtpChallengeResponseChannelEnum._('SMS');
const OtpChallengeResponseChannelEnum
    _$otpChallengeResponseChannelEnum_WHATSAPP =
    const OtpChallengeResponseChannelEnum._('WHATSAPP');
const OtpChallengeResponseChannelEnum _$otpChallengeResponseChannelEnum_EMAIL =
    const OtpChallengeResponseChannelEnum._('EMAIL');

OtpChallengeResponseChannelEnum _$otpChallengeResponseChannelEnumValueOf(
    String name) {
  switch (name) {
    case 'SMS':
      return _$otpChallengeResponseChannelEnum_SMS;
    case 'WHATSAPP':
      return _$otpChallengeResponseChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpChallengeResponseChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpChallengeResponseChannelEnum>
    _$otpChallengeResponseChannelEnumValues = BuiltSet<
        OtpChallengeResponseChannelEnum>(const <OtpChallengeResponseChannelEnum>[
  _$otpChallengeResponseChannelEnum_SMS,
  _$otpChallengeResponseChannelEnum_WHATSAPP,
  _$otpChallengeResponseChannelEnum_EMAIL,
]);

const OtpChallengeResponseDeliveryStatusEnum
    _$otpChallengeResponseDeliveryStatusEnum_PENDING =
    const OtpChallengeResponseDeliveryStatusEnum._('PENDING');
const OtpChallengeResponseDeliveryStatusEnum
    _$otpChallengeResponseDeliveryStatusEnum_SENT =
    const OtpChallengeResponseDeliveryStatusEnum._('SENT');
const OtpChallengeResponseDeliveryStatusEnum
    _$otpChallengeResponseDeliveryStatusEnum_DELIVERED =
    const OtpChallengeResponseDeliveryStatusEnum._('DELIVERED');
const OtpChallengeResponseDeliveryStatusEnum
    _$otpChallengeResponseDeliveryStatusEnum_FAILED =
    const OtpChallengeResponseDeliveryStatusEnum._('FAILED');

OtpChallengeResponseDeliveryStatusEnum
    _$otpChallengeResponseDeliveryStatusEnumValueOf(String name) {
  switch (name) {
    case 'PENDING':
      return _$otpChallengeResponseDeliveryStatusEnum_PENDING;
    case 'SENT':
      return _$otpChallengeResponseDeliveryStatusEnum_SENT;
    case 'DELIVERED':
      return _$otpChallengeResponseDeliveryStatusEnum_DELIVERED;
    case 'FAILED':
      return _$otpChallengeResponseDeliveryStatusEnum_FAILED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpChallengeResponseDeliveryStatusEnum>
    _$otpChallengeResponseDeliveryStatusEnumValues = BuiltSet<
        OtpChallengeResponseDeliveryStatusEnum>(const <OtpChallengeResponseDeliveryStatusEnum>[
  _$otpChallengeResponseDeliveryStatusEnum_PENDING,
  _$otpChallengeResponseDeliveryStatusEnum_SENT,
  _$otpChallengeResponseDeliveryStatusEnum_DELIVERED,
  _$otpChallengeResponseDeliveryStatusEnum_FAILED,
]);

Serializer<OtpChallengeResponseChannelEnum>
    _$otpChallengeResponseChannelEnumSerializer =
    _$OtpChallengeResponseChannelEnumSerializer();
Serializer<OtpChallengeResponseDeliveryStatusEnum>
    _$otpChallengeResponseDeliveryStatusEnumSerializer =
    _$OtpChallengeResponseDeliveryStatusEnumSerializer();

class _$OtpChallengeResponseChannelEnumSerializer
    implements PrimitiveSerializer<OtpChallengeResponseChannelEnum> {
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
  final Iterable<Type> types = const <Type>[OtpChallengeResponseChannelEnum];
  @override
  final String wireName = 'OtpChallengeResponseChannelEnum';

  @override
  Object serialize(
          Serializers serializers, OtpChallengeResponseChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpChallengeResponseChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpChallengeResponseChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpChallengeResponseDeliveryStatusEnumSerializer
    implements PrimitiveSerializer<OtpChallengeResponseDeliveryStatusEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'PENDING': 'PENDING',
    'SENT': 'SENT',
    'DELIVERED': 'DELIVERED',
    'FAILED': 'FAILED',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'PENDING': 'PENDING',
    'SENT': 'SENT',
    'DELIVERED': 'DELIVERED',
    'FAILED': 'FAILED',
  };

  @override
  final Iterable<Type> types = const <Type>[
    OtpChallengeResponseDeliveryStatusEnum
  ];
  @override
  final String wireName = 'OtpChallengeResponseDeliveryStatusEnum';

  @override
  Object serialize(Serializers serializers,
          OtpChallengeResponseDeliveryStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpChallengeResponseDeliveryStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpChallengeResponseDeliveryStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpChallengeResponse extends OtpChallengeResponse {
  @override
  final OtpChallengeResponseChannelEnum channel;
  @override
  final OtpChallengeResponseDeliveryStatusEnum deliveryStatus;
  @override
  final DateTime expiresAt;
  @override
  final bool fallbackUsed;

  factory _$OtpChallengeResponse(
          [void Function(OtpChallengeResponseBuilder)? updates]) =>
      (OtpChallengeResponseBuilder()..update(updates))._build();

  _$OtpChallengeResponse._(
      {required this.channel,
      required this.deliveryStatus,
      required this.expiresAt,
      required this.fallbackUsed})
      : super._();
  @override
  OtpChallengeResponse rebuild(
          void Function(OtpChallengeResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpChallengeResponseBuilder toBuilder() =>
      OtpChallengeResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpChallengeResponse &&
        channel == other.channel &&
        deliveryStatus == other.deliveryStatus &&
        expiresAt == other.expiresAt &&
        fallbackUsed == other.fallbackUsed;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, deliveryStatus.hashCode);
    _$hash = $jc(_$hash, expiresAt.hashCode);
    _$hash = $jc(_$hash, fallbackUsed.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpChallengeResponse')
          ..add('channel', channel)
          ..add('deliveryStatus', deliveryStatus)
          ..add('expiresAt', expiresAt)
          ..add('fallbackUsed', fallbackUsed))
        .toString();
  }
}

class OtpChallengeResponseBuilder
    implements Builder<OtpChallengeResponse, OtpChallengeResponseBuilder> {
  _$OtpChallengeResponse? _$v;

  OtpChallengeResponseChannelEnum? _channel;
  OtpChallengeResponseChannelEnum? get channel => _$this._channel;
  set channel(OtpChallengeResponseChannelEnum? channel) =>
      _$this._channel = channel;

  OtpChallengeResponseDeliveryStatusEnum? _deliveryStatus;
  OtpChallengeResponseDeliveryStatusEnum? get deliveryStatus =>
      _$this._deliveryStatus;
  set deliveryStatus(OtpChallengeResponseDeliveryStatusEnum? deliveryStatus) =>
      _$this._deliveryStatus = deliveryStatus;

  DateTime? _expiresAt;
  DateTime? get expiresAt => _$this._expiresAt;
  set expiresAt(DateTime? expiresAt) => _$this._expiresAt = expiresAt;

  bool? _fallbackUsed;
  bool? get fallbackUsed => _$this._fallbackUsed;
  set fallbackUsed(bool? fallbackUsed) => _$this._fallbackUsed = fallbackUsed;

  OtpChallengeResponseBuilder() {
    OtpChallengeResponse._defaults(this);
  }

  OtpChallengeResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _deliveryStatus = $v.deliveryStatus;
      _expiresAt = $v.expiresAt;
      _fallbackUsed = $v.fallbackUsed;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpChallengeResponse other) {
    _$v = other as _$OtpChallengeResponse;
  }

  @override
  void update(void Function(OtpChallengeResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpChallengeResponse build() => _build();

  _$OtpChallengeResponse _build() {
    final _$result = _$v ??
        _$OtpChallengeResponse._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'OtpChallengeResponse', 'channel'),
          deliveryStatus: BuiltValueNullFieldError.checkNotNull(
              deliveryStatus, r'OtpChallengeResponse', 'deliveryStatus'),
          expiresAt: BuiltValueNullFieldError.checkNotNull(
              expiresAt, r'OtpChallengeResponse', 'expiresAt'),
          fallbackUsed: BuiltValueNullFieldError.checkNotNull(
              fallbackUsed, r'OtpChallengeResponse', 'fallbackUsed'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
