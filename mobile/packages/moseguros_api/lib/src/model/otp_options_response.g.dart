// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_options_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const InnerEnum _$innerEnum_SMS = const InnerEnum._('SMS');
const InnerEnum _$innerEnum_WHATSAPP = const InnerEnum._('WHATSAPP');
const InnerEnum _$innerEnum_EMAIL = const InnerEnum._('EMAIL');

InnerEnum _$innerEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$innerEnum_SMS;
    case 'WHATSAPP':
      return _$innerEnum_WHATSAPP;
    case 'EMAIL':
      return _$innerEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<InnerEnum> _$innerEnumValues =
    BuiltSet<InnerEnum>(const <InnerEnum>[
  _$innerEnum_SMS,
  _$innerEnum_WHATSAPP,
  _$innerEnum_EMAIL,
]);

const OtpOptionsResponseDefaultChannelsEnum
    _$otpOptionsResponseDefaultChannelsEnum_SMS =
    const OtpOptionsResponseDefaultChannelsEnum._('SMS');
const OtpOptionsResponseDefaultChannelsEnum
    _$otpOptionsResponseDefaultChannelsEnum_WHATSAPP =
    const OtpOptionsResponseDefaultChannelsEnum._('WHATSAPP');
const OtpOptionsResponseDefaultChannelsEnum
    _$otpOptionsResponseDefaultChannelsEnum_EMAIL =
    const OtpOptionsResponseDefaultChannelsEnum._('EMAIL');

OtpOptionsResponseDefaultChannelsEnum
    _$otpOptionsResponseDefaultChannelsEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpOptionsResponseDefaultChannelsEnum_SMS;
    case 'WHATSAPP':
      return _$otpOptionsResponseDefaultChannelsEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpOptionsResponseDefaultChannelsEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpOptionsResponseDefaultChannelsEnum>
    _$otpOptionsResponseDefaultChannelsEnumValues = BuiltSet<
        OtpOptionsResponseDefaultChannelsEnum>(const <OtpOptionsResponseDefaultChannelsEnum>[
  _$otpOptionsResponseDefaultChannelsEnum_SMS,
  _$otpOptionsResponseDefaultChannelsEnum_WHATSAPP,
  _$otpOptionsResponseDefaultChannelsEnum_EMAIL,
]);

const OtpOptionsResponseIdentifierTypesEnum
    _$otpOptionsResponseIdentifierTypesEnum_MSISDN =
    const OtpOptionsResponseIdentifierTypesEnum._('MSISDN');
const OtpOptionsResponseIdentifierTypesEnum
    _$otpOptionsResponseIdentifierTypesEnum_EMAIL =
    const OtpOptionsResponseIdentifierTypesEnum._('EMAIL');

OtpOptionsResponseIdentifierTypesEnum
    _$otpOptionsResponseIdentifierTypesEnumValueOf(String name) {
  switch (name) {
    case 'MSISDN':
      return _$otpOptionsResponseIdentifierTypesEnum_MSISDN;
    case 'EMAIL':
      return _$otpOptionsResponseIdentifierTypesEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpOptionsResponseIdentifierTypesEnum>
    _$otpOptionsResponseIdentifierTypesEnumValues = BuiltSet<
        OtpOptionsResponseIdentifierTypesEnum>(const <OtpOptionsResponseIdentifierTypesEnum>[
  _$otpOptionsResponseIdentifierTypesEnum_MSISDN,
  _$otpOptionsResponseIdentifierTypesEnum_EMAIL,
]);

Serializer<InnerEnum> _$innerEnumSerializer = _$InnerEnumSerializer();
Serializer<OtpOptionsResponseDefaultChannelsEnum>
    _$otpOptionsResponseDefaultChannelsEnumSerializer =
    _$OtpOptionsResponseDefaultChannelsEnumSerializer();
Serializer<OtpOptionsResponseIdentifierTypesEnum>
    _$otpOptionsResponseIdentifierTypesEnumSerializer =
    _$OtpOptionsResponseIdentifierTypesEnumSerializer();

class _$InnerEnumSerializer implements PrimitiveSerializer<InnerEnum> {
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
  final Iterable<Type> types = const <Type>[InnerEnum];
  @override
  final String wireName = 'InnerEnum';

  @override
  Object serialize(Serializers serializers, InnerEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  InnerEnum deserialize(Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      InnerEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpOptionsResponseDefaultChannelsEnumSerializer
    implements PrimitiveSerializer<OtpOptionsResponseDefaultChannelsEnum> {
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
    OtpOptionsResponseDefaultChannelsEnum
  ];
  @override
  final String wireName = 'OtpOptionsResponseDefaultChannelsEnum';

  @override
  Object serialize(
          Serializers serializers, OtpOptionsResponseDefaultChannelsEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpOptionsResponseDefaultChannelsEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpOptionsResponseDefaultChannelsEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpOptionsResponseIdentifierTypesEnumSerializer
    implements PrimitiveSerializer<OtpOptionsResponseIdentifierTypesEnum> {
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
    OtpOptionsResponseIdentifierTypesEnum
  ];
  @override
  final String wireName = 'OtpOptionsResponseIdentifierTypesEnum';

  @override
  Object serialize(
          Serializers serializers, OtpOptionsResponseIdentifierTypesEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpOptionsResponseIdentifierTypesEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpOptionsResponseIdentifierTypesEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpOptionsResponse extends OtpOptionsResponse {
  @override
  final BuiltMap<String, BuiltList<InnerEnum>> channels;
  @override
  final int codeLength;
  @override
  final bool customerCanChoose;
  @override
  final BuiltMap<String, OtpOptionsResponseDefaultChannelsEnum> defaultChannels;
  @override
  final BuiltList<OtpOptionsResponseIdentifierTypesEnum> identifierTypes;
  @override
  final int resendAfterSeconds;

  factory _$OtpOptionsResponse(
          [void Function(OtpOptionsResponseBuilder)? updates]) =>
      (OtpOptionsResponseBuilder()..update(updates))._build();

  _$OtpOptionsResponse._(
      {required this.channels,
      required this.codeLength,
      required this.customerCanChoose,
      required this.defaultChannels,
      required this.identifierTypes,
      required this.resendAfterSeconds})
      : super._();
  @override
  OtpOptionsResponse rebuild(
          void Function(OtpOptionsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpOptionsResponseBuilder toBuilder() =>
      OtpOptionsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpOptionsResponse &&
        channels == other.channels &&
        codeLength == other.codeLength &&
        customerCanChoose == other.customerCanChoose &&
        defaultChannels == other.defaultChannels &&
        identifierTypes == other.identifierTypes &&
        resendAfterSeconds == other.resendAfterSeconds;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channels.hashCode);
    _$hash = $jc(_$hash, codeLength.hashCode);
    _$hash = $jc(_$hash, customerCanChoose.hashCode);
    _$hash = $jc(_$hash, defaultChannels.hashCode);
    _$hash = $jc(_$hash, identifierTypes.hashCode);
    _$hash = $jc(_$hash, resendAfterSeconds.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpOptionsResponse')
          ..add('channels', channels)
          ..add('codeLength', codeLength)
          ..add('customerCanChoose', customerCanChoose)
          ..add('defaultChannels', defaultChannels)
          ..add('identifierTypes', identifierTypes)
          ..add('resendAfterSeconds', resendAfterSeconds))
        .toString();
  }
}

class OtpOptionsResponseBuilder
    implements Builder<OtpOptionsResponse, OtpOptionsResponseBuilder> {
  _$OtpOptionsResponse? _$v;

  MapBuilder<String, BuiltList<InnerEnum>>? _channels;
  MapBuilder<String, BuiltList<InnerEnum>> get channels =>
      _$this._channels ??= MapBuilder<String, BuiltList<InnerEnum>>();
  set channels(MapBuilder<String, BuiltList<InnerEnum>>? channels) =>
      _$this._channels = channels;

  int? _codeLength;
  int? get codeLength => _$this._codeLength;
  set codeLength(int? codeLength) => _$this._codeLength = codeLength;

  bool? _customerCanChoose;
  bool? get customerCanChoose => _$this._customerCanChoose;
  set customerCanChoose(bool? customerCanChoose) =>
      _$this._customerCanChoose = customerCanChoose;

  MapBuilder<String, OtpOptionsResponseDefaultChannelsEnum>? _defaultChannels;
  MapBuilder<String, OtpOptionsResponseDefaultChannelsEnum>
      get defaultChannels => _$this._defaultChannels ??=
          MapBuilder<String, OtpOptionsResponseDefaultChannelsEnum>();
  set defaultChannels(
          MapBuilder<String, OtpOptionsResponseDefaultChannelsEnum>?
              defaultChannels) =>
      _$this._defaultChannels = defaultChannels;

  ListBuilder<OtpOptionsResponseIdentifierTypesEnum>? _identifierTypes;
  ListBuilder<OtpOptionsResponseIdentifierTypesEnum> get identifierTypes =>
      _$this._identifierTypes ??=
          ListBuilder<OtpOptionsResponseIdentifierTypesEnum>();
  set identifierTypes(
          ListBuilder<OtpOptionsResponseIdentifierTypesEnum>?
              identifierTypes) =>
      _$this._identifierTypes = identifierTypes;

  int? _resendAfterSeconds;
  int? get resendAfterSeconds => _$this._resendAfterSeconds;
  set resendAfterSeconds(int? resendAfterSeconds) =>
      _$this._resendAfterSeconds = resendAfterSeconds;

  OtpOptionsResponseBuilder() {
    OtpOptionsResponse._defaults(this);
  }

  OtpOptionsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channels = $v.channels.toBuilder();
      _codeLength = $v.codeLength;
      _customerCanChoose = $v.customerCanChoose;
      _defaultChannels = $v.defaultChannels.toBuilder();
      _identifierTypes = $v.identifierTypes.toBuilder();
      _resendAfterSeconds = $v.resendAfterSeconds;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpOptionsResponse other) {
    _$v = other as _$OtpOptionsResponse;
  }

  @override
  void update(void Function(OtpOptionsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpOptionsResponse build() => _build();

  _$OtpOptionsResponse _build() {
    _$OtpOptionsResponse _$result;
    try {
      _$result = _$v ??
          _$OtpOptionsResponse._(
            channels: channels.build(),
            codeLength: BuiltValueNullFieldError.checkNotNull(
                codeLength, r'OtpOptionsResponse', 'codeLength'),
            customerCanChoose: BuiltValueNullFieldError.checkNotNull(
                customerCanChoose, r'OtpOptionsResponse', 'customerCanChoose'),
            defaultChannels: defaultChannels.build(),
            identifierTypes: identifierTypes.build(),
            resendAfterSeconds: BuiltValueNullFieldError.checkNotNull(
                resendAfterSeconds,
                r'OtpOptionsResponse',
                'resendAfterSeconds'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'channels';
        channels.build();

        _$failedField = 'defaultChannels';
        defaultChannels.build();
        _$failedField = 'identifierTypes';
        identifierTypes.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'OtpOptionsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
