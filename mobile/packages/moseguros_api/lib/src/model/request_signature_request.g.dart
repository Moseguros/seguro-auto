// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'request_signature_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const RequestSignatureRequestChannelEnum
    _$requestSignatureRequestChannelEnum_SMS =
    const RequestSignatureRequestChannelEnum._('SMS');
const RequestSignatureRequestChannelEnum
    _$requestSignatureRequestChannelEnum_WHATSAPP =
    const RequestSignatureRequestChannelEnum._('WHATSAPP');
const RequestSignatureRequestChannelEnum
    _$requestSignatureRequestChannelEnum_EMAIL =
    const RequestSignatureRequestChannelEnum._('EMAIL');

RequestSignatureRequestChannelEnum _$requestSignatureRequestChannelEnumValueOf(
    String name) {
  switch (name) {
    case 'SMS':
      return _$requestSignatureRequestChannelEnum_SMS;
    case 'WHATSAPP':
      return _$requestSignatureRequestChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$requestSignatureRequestChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<RequestSignatureRequestChannelEnum>
    _$requestSignatureRequestChannelEnumValues = BuiltSet<
        RequestSignatureRequestChannelEnum>(const <RequestSignatureRequestChannelEnum>[
  _$requestSignatureRequestChannelEnum_SMS,
  _$requestSignatureRequestChannelEnum_WHATSAPP,
  _$requestSignatureRequestChannelEnum_EMAIL,
]);

Serializer<RequestSignatureRequestChannelEnum>
    _$requestSignatureRequestChannelEnumSerializer =
    _$RequestSignatureRequestChannelEnumSerializer();

class _$RequestSignatureRequestChannelEnumSerializer
    implements PrimitiveSerializer<RequestSignatureRequestChannelEnum> {
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
  final Iterable<Type> types = const <Type>[RequestSignatureRequestChannelEnum];
  @override
  final String wireName = 'RequestSignatureRequestChannelEnum';

  @override
  Object serialize(
          Serializers serializers, RequestSignatureRequestChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  RequestSignatureRequestChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      RequestSignatureRequestChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$RequestSignatureRequest extends RequestSignatureRequest {
  @override
  final RequestSignatureRequestChannelEnum? channel;
  @override
  final bool documentsAcknowledged;
  @override
  final int version;

  factory _$RequestSignatureRequest(
          [void Function(RequestSignatureRequestBuilder)? updates]) =>
      (RequestSignatureRequestBuilder()..update(updates))._build();

  _$RequestSignatureRequest._(
      {this.channel,
      required this.documentsAcknowledged,
      required this.version})
      : super._();
  @override
  RequestSignatureRequest rebuild(
          void Function(RequestSignatureRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RequestSignatureRequestBuilder toBuilder() =>
      RequestSignatureRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RequestSignatureRequest &&
        channel == other.channel &&
        documentsAcknowledged == other.documentsAcknowledged &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, documentsAcknowledged.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RequestSignatureRequest')
          ..add('channel', channel)
          ..add('documentsAcknowledged', documentsAcknowledged)
          ..add('version', version))
        .toString();
  }
}

class RequestSignatureRequestBuilder
    implements
        Builder<RequestSignatureRequest, RequestSignatureRequestBuilder> {
  _$RequestSignatureRequest? _$v;

  RequestSignatureRequestChannelEnum? _channel;
  RequestSignatureRequestChannelEnum? get channel => _$this._channel;
  set channel(RequestSignatureRequestChannelEnum? channel) =>
      _$this._channel = channel;

  bool? _documentsAcknowledged;
  bool? get documentsAcknowledged => _$this._documentsAcknowledged;
  set documentsAcknowledged(bool? documentsAcknowledged) =>
      _$this._documentsAcknowledged = documentsAcknowledged;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RequestSignatureRequestBuilder() {
    RequestSignatureRequest._defaults(this);
  }

  RequestSignatureRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _documentsAcknowledged = $v.documentsAcknowledged;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RequestSignatureRequest other) {
    _$v = other as _$RequestSignatureRequest;
  }

  @override
  void update(void Function(RequestSignatureRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RequestSignatureRequest build() => _build();

  _$RequestSignatureRequest _build() {
    final _$result = _$v ??
        _$RequestSignatureRequest._(
          channel: channel,
          documentsAcknowledged: BuiltValueNullFieldError.checkNotNull(
              documentsAcknowledged,
              r'RequestSignatureRequest',
              'documentsAcknowledged'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'RequestSignatureRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
