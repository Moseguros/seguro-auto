// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_test_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OtpTestResponseChannelEnum _$otpTestResponseChannelEnum_SMS =
    const OtpTestResponseChannelEnum._('SMS');
const OtpTestResponseChannelEnum _$otpTestResponseChannelEnum_WHATSAPP =
    const OtpTestResponseChannelEnum._('WHATSAPP');
const OtpTestResponseChannelEnum _$otpTestResponseChannelEnum_EMAIL =
    const OtpTestResponseChannelEnum._('EMAIL');

OtpTestResponseChannelEnum _$otpTestResponseChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpTestResponseChannelEnum_SMS;
    case 'WHATSAPP':
      return _$otpTestResponseChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpTestResponseChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpTestResponseChannelEnum> _$otpTestResponseChannelEnumValues =
    BuiltSet<OtpTestResponseChannelEnum>(const <OtpTestResponseChannelEnum>[
  _$otpTestResponseChannelEnum_SMS,
  _$otpTestResponseChannelEnum_WHATSAPP,
  _$otpTestResponseChannelEnum_EMAIL,
]);

const OtpTestResponseDeliveryStatusEnum
    _$otpTestResponseDeliveryStatusEnum_PENDING =
    const OtpTestResponseDeliveryStatusEnum._('PENDING');
const OtpTestResponseDeliveryStatusEnum
    _$otpTestResponseDeliveryStatusEnum_SENT =
    const OtpTestResponseDeliveryStatusEnum._('SENT');
const OtpTestResponseDeliveryStatusEnum
    _$otpTestResponseDeliveryStatusEnum_DELIVERED =
    const OtpTestResponseDeliveryStatusEnum._('DELIVERED');
const OtpTestResponseDeliveryStatusEnum
    _$otpTestResponseDeliveryStatusEnum_FAILED =
    const OtpTestResponseDeliveryStatusEnum._('FAILED');

OtpTestResponseDeliveryStatusEnum _$otpTestResponseDeliveryStatusEnumValueOf(
    String name) {
  switch (name) {
    case 'PENDING':
      return _$otpTestResponseDeliveryStatusEnum_PENDING;
    case 'SENT':
      return _$otpTestResponseDeliveryStatusEnum_SENT;
    case 'DELIVERED':
      return _$otpTestResponseDeliveryStatusEnum_DELIVERED;
    case 'FAILED':
      return _$otpTestResponseDeliveryStatusEnum_FAILED;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpTestResponseDeliveryStatusEnum>
    _$otpTestResponseDeliveryStatusEnumValues = BuiltSet<
        OtpTestResponseDeliveryStatusEnum>(const <OtpTestResponseDeliveryStatusEnum>[
  _$otpTestResponseDeliveryStatusEnum_PENDING,
  _$otpTestResponseDeliveryStatusEnum_SENT,
  _$otpTestResponseDeliveryStatusEnum_DELIVERED,
  _$otpTestResponseDeliveryStatusEnum_FAILED,
]);

Serializer<OtpTestResponseChannelEnum> _$otpTestResponseChannelEnumSerializer =
    _$OtpTestResponseChannelEnumSerializer();
Serializer<OtpTestResponseDeliveryStatusEnum>
    _$otpTestResponseDeliveryStatusEnumSerializer =
    _$OtpTestResponseDeliveryStatusEnumSerializer();

class _$OtpTestResponseChannelEnumSerializer
    implements PrimitiveSerializer<OtpTestResponseChannelEnum> {
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
  final Iterable<Type> types = const <Type>[OtpTestResponseChannelEnum];
  @override
  final String wireName = 'OtpTestResponseChannelEnum';

  @override
  Object serialize(Serializers serializers, OtpTestResponseChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpTestResponseChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpTestResponseChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpTestResponseDeliveryStatusEnumSerializer
    implements PrimitiveSerializer<OtpTestResponseDeliveryStatusEnum> {
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
  final Iterable<Type> types = const <Type>[OtpTestResponseDeliveryStatusEnum];
  @override
  final String wireName = 'OtpTestResponseDeliveryStatusEnum';

  @override
  Object serialize(
          Serializers serializers, OtpTestResponseDeliveryStatusEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpTestResponseDeliveryStatusEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpTestResponseDeliveryStatusEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpTestResponse extends OtpTestResponse {
  @override
  final OtpTestResponseChannelEnum channel;
  @override
  final OtpTestResponseDeliveryStatusEnum deliveryStatus;
  @override
  final String messageId;

  factory _$OtpTestResponse([void Function(OtpTestResponseBuilder)? updates]) =>
      (OtpTestResponseBuilder()..update(updates))._build();

  _$OtpTestResponse._(
      {required this.channel,
      required this.deliveryStatus,
      required this.messageId})
      : super._();
  @override
  OtpTestResponse rebuild(void Function(OtpTestResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpTestResponseBuilder toBuilder() => OtpTestResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpTestResponse &&
        channel == other.channel &&
        deliveryStatus == other.deliveryStatus &&
        messageId == other.messageId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, deliveryStatus.hashCode);
    _$hash = $jc(_$hash, messageId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpTestResponse')
          ..add('channel', channel)
          ..add('deliveryStatus', deliveryStatus)
          ..add('messageId', messageId))
        .toString();
  }
}

class OtpTestResponseBuilder
    implements Builder<OtpTestResponse, OtpTestResponseBuilder> {
  _$OtpTestResponse? _$v;

  OtpTestResponseChannelEnum? _channel;
  OtpTestResponseChannelEnum? get channel => _$this._channel;
  set channel(OtpTestResponseChannelEnum? channel) => _$this._channel = channel;

  OtpTestResponseDeliveryStatusEnum? _deliveryStatus;
  OtpTestResponseDeliveryStatusEnum? get deliveryStatus =>
      _$this._deliveryStatus;
  set deliveryStatus(OtpTestResponseDeliveryStatusEnum? deliveryStatus) =>
      _$this._deliveryStatus = deliveryStatus;

  String? _messageId;
  String? get messageId => _$this._messageId;
  set messageId(String? messageId) => _$this._messageId = messageId;

  OtpTestResponseBuilder() {
    OtpTestResponse._defaults(this);
  }

  OtpTestResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _deliveryStatus = $v.deliveryStatus;
      _messageId = $v.messageId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpTestResponse other) {
    _$v = other as _$OtpTestResponse;
  }

  @override
  void update(void Function(OtpTestResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpTestResponse build() => _build();

  _$OtpTestResponse _build() {
    final _$result = _$v ??
        _$OtpTestResponse._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'OtpTestResponse', 'channel'),
          deliveryStatus: BuiltValueNullFieldError.checkNotNull(
              deliveryStatus, r'OtpTestResponse', 'deliveryStatus'),
          messageId: BuiltValueNullFieldError.checkNotNull(
              messageId, r'OtpTestResponse', 'messageId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
