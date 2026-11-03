// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'request_otp_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RequestOtpRequestChannelEnum _$requestOtpRequestChannelEnum_SMS =
    const RequestOtpRequestChannelEnum._('SMS');
const RequestOtpRequestChannelEnum _$requestOtpRequestChannelEnum_WHATSAPP =
    const RequestOtpRequestChannelEnum._('WHATSAPP');
const RequestOtpRequestChannelEnum _$requestOtpRequestChannelEnum_EMAIL =
    const RequestOtpRequestChannelEnum._('EMAIL');

RequestOtpRequestChannelEnum _$requestOtpRequestChannelEnumValueOf(
    String name) {
  switch (name) {
    case 'SMS':
      return _$requestOtpRequestChannelEnum_SMS;
    case 'WHATSAPP':
      return _$requestOtpRequestChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$requestOtpRequestChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RequestOtpRequestChannelEnum>
    _$requestOtpRequestChannelEnumValues =
    BuiltSet<RequestOtpRequestChannelEnum>(const <RequestOtpRequestChannelEnum>[
  _$requestOtpRequestChannelEnum_SMS,
  _$requestOtpRequestChannelEnum_WHATSAPP,
  _$requestOtpRequestChannelEnum_EMAIL,
]);

const RequestOtpRequestIdentifierTypeEnum
    _$requestOtpRequestIdentifierTypeEnum_MSISDN =
    const RequestOtpRequestIdentifierTypeEnum._('MSISDN');
const RequestOtpRequestIdentifierTypeEnum
    _$requestOtpRequestIdentifierTypeEnum_EMAIL =
    const RequestOtpRequestIdentifierTypeEnum._('EMAIL');

RequestOtpRequestIdentifierTypeEnum
    _$requestOtpRequestIdentifierTypeEnumValueOf(String name) {
  switch (name) {
    case 'MSISDN':
      return _$requestOtpRequestIdentifierTypeEnum_MSISDN;
    case 'EMAIL':
      return _$requestOtpRequestIdentifierTypeEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RequestOtpRequestIdentifierTypeEnum>
    _$requestOtpRequestIdentifierTypeEnumValues = BuiltSet<
        RequestOtpRequestIdentifierTypeEnum>(const <RequestOtpRequestIdentifierTypeEnum>[
  _$requestOtpRequestIdentifierTypeEnum_MSISDN,
  _$requestOtpRequestIdentifierTypeEnum_EMAIL,
]);

Serializer<RequestOtpRequestChannelEnum>
    _$requestOtpRequestChannelEnumSerializer =
    _$RequestOtpRequestChannelEnumSerializer();
Serializer<RequestOtpRequestIdentifierTypeEnum>
    _$requestOtpRequestIdentifierTypeEnumSerializer =
    _$RequestOtpRequestIdentifierTypeEnumSerializer();

class _$RequestOtpRequestChannelEnumSerializer
    implements PrimitiveSerializer<RequestOtpRequestChannelEnum> {
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
  final Iterable<Type> types = const <Type>[RequestOtpRequestChannelEnum];
  @override
  final String wireName = 'RequestOtpRequestChannelEnum';

  @override
  Object serialize(Serializers serializers, RequestOtpRequestChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RequestOtpRequestChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RequestOtpRequestChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RequestOtpRequestIdentifierTypeEnumSerializer
    implements PrimitiveSerializer<RequestOtpRequestIdentifierTypeEnum> {
  static const Map<String, Object> _toWire = const <String, Object>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };
  static const Map<Object, String> _fromWire = const <Object, String>{
    'MSISDN': 'MSISDN',
    'EMAIL': 'EMAIL',
  };

  @override
  final Iterable<Type> types = const <Type>[
    RequestOtpRequestIdentifierTypeEnum
  ];
  @override
  final String wireName = 'RequestOtpRequestIdentifierTypeEnum';

  @override
  Object serialize(
          Serializers serializers, RequestOtpRequestIdentifierTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RequestOtpRequestIdentifierTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RequestOtpRequestIdentifierTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RequestOtpRequest extends RequestOtpRequest {
  @override
  final RequestOtpRequestChannelEnum? channel;
  @override
  final String identifier;
  @override
  final RequestOtpRequestIdentifierTypeEnum identifierType;

  factory _$RequestOtpRequest(
          [void Function(RequestOtpRequestBuilder)? updates]) =>
      (RequestOtpRequestBuilder()..update(updates))._build();

  _$RequestOtpRequest._(
      {this.channel, required this.identifier, required this.identifierType})
      : super._();
  @override
  RequestOtpRequest rebuild(void Function(RequestOtpRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestOtpRequestBuilder toBuilder() =>
      RequestOtpRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestOtpRequest &&
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
    return (newBuiltValueToStringHelper(r'RequestOtpRequest')
          ..add('channel', channel)
          ..add('identifier', identifier)
          ..add('identifierType', identifierType))
        .toString();
  }
}

class RequestOtpRequestBuilder
    implements Builder<RequestOtpRequest, RequestOtpRequestBuilder> {
  _$RequestOtpRequest? _$v;

  RequestOtpRequestChannelEnum? _channel;
  RequestOtpRequestChannelEnum? get channel => _$this._channel;
  set channel(RequestOtpRequestChannelEnum? channel) =>
      _$this._channel = channel;

  String? _identifier;
  String? get identifier => _$this._identifier;
  set identifier(String? identifier) => _$this._identifier = identifier;

  RequestOtpRequestIdentifierTypeEnum? _identifierType;
  RequestOtpRequestIdentifierTypeEnum? get identifierType =>
      _$this._identifierType;
  set identifierType(RequestOtpRequestIdentifierTypeEnum? identifierType) =>
      _$this._identifierType = identifierType;

  RequestOtpRequestBuilder() {
    RequestOtpRequest._defaults(this);
  }

  RequestOtpRequestBuilder get _$this {
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
  void replace(RequestOtpRequest other) {
    _$v = other as _$RequestOtpRequest;
  }

  @override
  void update(void Function(RequestOtpRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestOtpRequest build() => _build();

  _$RequestOtpRequest _build() {
    final _$result = _$v ??
        _$RequestOtpRequest._(
          channel: channel,
          identifier: BuiltValueNullFieldError.checkNotNull(
              identifier, r'RequestOtpRequest', 'identifier'),
          identifierType: BuiltValueNullFieldError.checkNotNull(
              identifierType, r'RequestOtpRequest', 'identifierType'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
