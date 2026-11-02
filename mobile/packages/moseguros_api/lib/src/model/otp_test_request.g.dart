// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_test_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OtpTestRequestChannelEnum _$otpTestRequestChannelEnum_SMS =
    const OtpTestRequestChannelEnum._('SMS');
const OtpTestRequestChannelEnum _$otpTestRequestChannelEnum_WHATSAPP =
    const OtpTestRequestChannelEnum._('WHATSAPP');
const OtpTestRequestChannelEnum _$otpTestRequestChannelEnum_EMAIL =
    const OtpTestRequestChannelEnum._('EMAIL');

OtpTestRequestChannelEnum _$otpTestRequestChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpTestRequestChannelEnum_SMS;
    case 'WHATSAPP':
      return _$otpTestRequestChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpTestRequestChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpTestRequestChannelEnum> _$otpTestRequestChannelEnumValues =
    BuiltSet<OtpTestRequestChannelEnum>(const <OtpTestRequestChannelEnum>[
  _$otpTestRequestChannelEnum_SMS,
  _$otpTestRequestChannelEnum_WHATSAPP,
  _$otpTestRequestChannelEnum_EMAIL,
]);

const OtpTestRequestIdentifierTypeEnum
    _$otpTestRequestIdentifierTypeEnum_MSISDN =
    const OtpTestRequestIdentifierTypeEnum._('MSISDN');
const OtpTestRequestIdentifierTypeEnum
    _$otpTestRequestIdentifierTypeEnum_EMAIL =
    const OtpTestRequestIdentifierTypeEnum._('EMAIL');

OtpTestRequestIdentifierTypeEnum _$otpTestRequestIdentifierTypeEnumValueOf(
    String name) {
  switch (name) {
    case 'MSISDN':
      return _$otpTestRequestIdentifierTypeEnum_MSISDN;
    case 'EMAIL':
      return _$otpTestRequestIdentifierTypeEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpTestRequestIdentifierTypeEnum>
    _$otpTestRequestIdentifierTypeEnumValues = BuiltSet<
        OtpTestRequestIdentifierTypeEnum>(const <OtpTestRequestIdentifierTypeEnum>[
  _$otpTestRequestIdentifierTypeEnum_MSISDN,
  _$otpTestRequestIdentifierTypeEnum_EMAIL,
]);

Serializer<OtpTestRequestChannelEnum> _$otpTestRequestChannelEnumSerializer =
    _$OtpTestRequestChannelEnumSerializer();
Serializer<OtpTestRequestIdentifierTypeEnum>
    _$otpTestRequestIdentifierTypeEnumSerializer =
    _$OtpTestRequestIdentifierTypeEnumSerializer();

class _$OtpTestRequestChannelEnumSerializer
    implements PrimitiveSerializer<OtpTestRequestChannelEnum> {
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
  final Iterable<Type> types = const <Type>[OtpTestRequestChannelEnum];
  @override
  final String wireName = 'OtpTestRequestChannelEnum';

  @override
  Object serialize(Serializers serializers, OtpTestRequestChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpTestRequestChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpTestRequestChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpTestRequestIdentifierTypeEnumSerializer
    implements PrimitiveSerializer<OtpTestRequestIdentifierTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };

  @override
  final Iterable<Type> types = const <Type>[OtpTestRequestIdentifierTypeEnum];
  @override
  final String wireName = 'OtpTestRequestIdentifierTypeEnum';

  @override
  Object serialize(
          Serializers serializers, OtpTestRequestIdentifierTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpTestRequestIdentifierTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpTestRequestIdentifierTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpTestRequest extends OtpTestRequest {
  @override
  final OtpTestRequestChannelEnum channel;
  @override
  final String identifier;
  @override
  final OtpTestRequestIdentifierTypeEnum identifierType;

  factory _$OtpTestRequest([void Function(OtpTestRequestBuilder)? updates]) =>
      (OtpTestRequestBuilder()..update(updates))._build();

  _$OtpTestRequest._(
      {required this.channel,
      required this.identifier,
      required this.identifierType})
      : super._();
  @override
  OtpTestRequest rebuild(void Function(OtpTestRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpTestRequestBuilder toBuilder() => OtpTestRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpTestRequest &&
        channel == other.channel &&
        identifier == other.identifier &&
        identifierType == other.identifierType;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, identifier.hashCode);
    _$hash = $jc(_$hash, identifierType.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpTestRequest')
          ..add('channel', channel)
          ..add('identifier', identifier)
          ..add('identifierType', identifierType))
        .toString();
  }
}

class OtpTestRequestBuilder
    implements Builder<OtpTestRequest, OtpTestRequestBuilder> {
  _$OtpTestRequest? _$v;

  OtpTestRequestChannelEnum? _channel;
  OtpTestRequestChannelEnum? get channel => _$this._channel;
  set channel(OtpTestRequestChannelEnum? channel) => _$this._channel = channel;

  String? _identifier;
  String? get identifier => _$this._identifier;
  set identifier(String? identifier) => _$this._identifier = identifier;

  OtpTestRequestIdentifierTypeEnum? _identifierType;
  OtpTestRequestIdentifierTypeEnum? get identifierType =>
      _$this._identifierType;
  set identifierType(OtpTestRequestIdentifierTypeEnum? identifierType) =>
      _$this._identifierType = identifierType;

  OtpTestRequestBuilder() {
    OtpTestRequest._defaults(this);
  }

  OtpTestRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _identifier = $v.identifier;
      _identifierType = $v.identifierType;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpTestRequest other) {
    _$v = other as _$OtpTestRequest;
  }

  @override
  void update(void Function(OtpTestRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpTestRequest build() => _build();

  _$OtpTestRequest _build() {
    final _$result = _$v ??
        _$OtpTestRequest._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'OtpTestRequest', 'channel'),
          identifier: BuiltValueNullFieldError.checkNotNull(
              identifier, r'OtpTestRequest', 'identifier'),
          identifierType: BuiltValueNullFieldError.checkNotNull(
              identifierType, r'OtpTestRequest', 'identifierType'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
