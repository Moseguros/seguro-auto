// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'start_contact_verification_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const StartContactVerificationRequestChannelEnum
    _$startContactVerificationRequestChannelEnum_SMS =
    const StartContactVerificationRequestChannelEnum._('SMS');
const StartContactVerificationRequestChannelEnum
    _$startContactVerificationRequestChannelEnum_WHATSAPP =
    const StartContactVerificationRequestChannelEnum._('WHATSAPP');
const StartContactVerificationRequestChannelEnum
    _$startContactVerificationRequestChannelEnum_EMAIL =
    const StartContactVerificationRequestChannelEnum._('EMAIL');

StartContactVerificationRequestChannelEnum
    _$startContactVerificationRequestChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$startContactVerificationRequestChannelEnum_SMS;
    case 'WHATSAPP':
      return _$startContactVerificationRequestChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$startContactVerificationRequestChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<StartContactVerificationRequestChannelEnum>
    _$startContactVerificationRequestChannelEnumValues = BuiltSet<
        StartContactVerificationRequestChannelEnum>(const <StartContactVerificationRequestChannelEnum>[
  _$startContactVerificationRequestChannelEnum_SMS,
  _$startContactVerificationRequestChannelEnum_WHATSAPP,
  _$startContactVerificationRequestChannelEnum_EMAIL,
]);

const StartContactVerificationRequestTypeEnum
    _$startContactVerificationRequestTypeEnum_MSISDN =
    const StartContactVerificationRequestTypeEnum._('MSISDN');
const StartContactVerificationRequestTypeEnum
    _$startContactVerificationRequestTypeEnum_EMAIL =
    const StartContactVerificationRequestTypeEnum._('EMAIL');

StartContactVerificationRequestTypeEnum
    _$startContactVerificationRequestTypeEnumValueOf(String name) {
  switch (name) {
    case 'MSISDN':
      return _$startContactVerificationRequestTypeEnum_MSISDN;
    case 'EMAIL':
      return _$startContactVerificationRequestTypeEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<StartContactVerificationRequestTypeEnum>
    _$startContactVerificationRequestTypeEnumValues = BuiltSet<
        StartContactVerificationRequestTypeEnum>(const <StartContactVerificationRequestTypeEnum>[
  _$startContactVerificationRequestTypeEnum_MSISDN,
  _$startContactVerificationRequestTypeEnum_EMAIL,
]);

Serializer<StartContactVerificationRequestChannelEnum>
    _$startContactVerificationRequestChannelEnumSerializer =
    _$StartContactVerificationRequestChannelEnumSerializer();
Serializer<StartContactVerificationRequestTypeEnum>
    _$startContactVerificationRequestTypeEnumSerializer =
    _$StartContactVerificationRequestTypeEnumSerializer();

class _$StartContactVerificationRequestChannelEnumSerializer
    implements PrimitiveSerializer<StartContactVerificationRequestChannelEnum> {
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
    StartContactVerificationRequestChannelEnum
  ];
  @override
  final String wireName = 'StartContactVerificationRequestChannelEnum';

  @override
  Object serialize(Serializers serializers,
          StartContactVerificationRequestChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  StartContactVerificationRequestChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      StartContactVerificationRequestChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$StartContactVerificationRequestTypeEnumSerializer
    implements PrimitiveSerializer<StartContactVerificationRequestTypeEnum> {
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
    StartContactVerificationRequestTypeEnum
  ];
  @override
  final String wireName = 'StartContactVerificationRequestTypeEnum';

  @override
  Object serialize(Serializers serializers,
          StartContactVerificationRequestTypeEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  StartContactVerificationRequestTypeEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      StartContactVerificationRequestTypeEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$StartContactVerificationRequest
    extends StartContactVerificationRequest {
  @override
  final StartContactVerificationRequestChannelEnum? channel;
  @override
  final StartContactVerificationRequestTypeEnum type;
  @override
  final String value;

  factory _$StartContactVerificationRequest(
          [void Function(StartContactVerificationRequestBuilder)? updates]) =>
      (StartContactVerificationRequestBuilder()..update(updates))._build();

  _$StartContactVerificationRequest._(
      {this.channel, required this.type, required this.value})
      : super._();
  @override
  StartContactVerificationRequest rebuild(
          void Function(StartContactVerificationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  StartContactVerificationRequestBuilder toBuilder() =>
      StartContactVerificationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is StartContactVerificationRequest &&
        channel == other.channel &&
        type == other.type &&
        value == other.value;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'StartContactVerificationRequest')
          ..add('channel', channel)
          ..add('type', type)
          ..add('value', value))
        .toString();
  }
}

class StartContactVerificationRequestBuilder
    implements
        Builder<StartContactVerificationRequest,
            StartContactVerificationRequestBuilder> {
  _$StartContactVerificationRequest? _$v;

  StartContactVerificationRequestChannelEnum? _channel;
  StartContactVerificationRequestChannelEnum? get channel => _$this._channel;
  set channel(StartContactVerificationRequestChannelEnum? channel) =>
      _$this._channel = channel;

  StartContactVerificationRequestTypeEnum? _type;
  StartContactVerificationRequestTypeEnum? get type => _$this._type;
  set type(StartContactVerificationRequestTypeEnum? type) =>
      _$this._type = type;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  StartContactVerificationRequestBuilder() {
    StartContactVerificationRequest._defaults(this);
  }

  StartContactVerificationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _type = $v.type;
      _value = $v.value;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(StartContactVerificationRequest other) {
    _$v = other as _$StartContactVerificationRequest;
  }

  @override
  void update(void Function(StartContactVerificationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  StartContactVerificationRequest build() => _build();

  _$StartContactVerificationRequest _build() {
    final _$result = _$v ??
        _$StartContactVerificationRequest._(
          channel: channel,
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'StartContactVerificationRequest', 'type'),
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'StartContactVerificationRequest', 'value'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
