// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_settings_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const OtpSettingsResponseDefaultChannelEnum
    _$otpSettingsResponseDefaultChannelEnum_SMS =
    const OtpSettingsResponseDefaultChannelEnum._('SMS');
const OtpSettingsResponseDefaultChannelEnum
    _$otpSettingsResponseDefaultChannelEnum_WHATSAPP =
    const OtpSettingsResponseDefaultChannelEnum._('WHATSAPP');
const OtpSettingsResponseDefaultChannelEnum
    _$otpSettingsResponseDefaultChannelEnum_EMAIL =
    const OtpSettingsResponseDefaultChannelEnum._('EMAIL');

OtpSettingsResponseDefaultChannelEnum
    _$otpSettingsResponseDefaultChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpSettingsResponseDefaultChannelEnum_SMS;
    case 'WHATSAPP':
      return _$otpSettingsResponseDefaultChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpSettingsResponseDefaultChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpSettingsResponseDefaultChannelEnum>
    _$otpSettingsResponseDefaultChannelEnumValues = BuiltSet<
        OtpSettingsResponseDefaultChannelEnum>(const <OtpSettingsResponseDefaultChannelEnum>[
  _$otpSettingsResponseDefaultChannelEnum_SMS,
  _$otpSettingsResponseDefaultChannelEnum_WHATSAPP,
  _$otpSettingsResponseDefaultChannelEnum_EMAIL,
]);

const OtpSettingsResponseEnabledChannelsEnum
    _$otpSettingsResponseEnabledChannelsEnum_SMS =
    const OtpSettingsResponseEnabledChannelsEnum._('SMS');
const OtpSettingsResponseEnabledChannelsEnum
    _$otpSettingsResponseEnabledChannelsEnum_WHATSAPP =
    const OtpSettingsResponseEnabledChannelsEnum._('WHATSAPP');
const OtpSettingsResponseEnabledChannelsEnum
    _$otpSettingsResponseEnabledChannelsEnum_EMAIL =
    const OtpSettingsResponseEnabledChannelsEnum._('EMAIL');

OtpSettingsResponseEnabledChannelsEnum
    _$otpSettingsResponseEnabledChannelsEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpSettingsResponseEnabledChannelsEnum_SMS;
    case 'WHATSAPP':
      return _$otpSettingsResponseEnabledChannelsEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpSettingsResponseEnabledChannelsEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpSettingsResponseEnabledChannelsEnum>
    _$otpSettingsResponseEnabledChannelsEnumValues = BuiltSet<
        OtpSettingsResponseEnabledChannelsEnum>(const <OtpSettingsResponseEnabledChannelsEnum>[
  _$otpSettingsResponseEnabledChannelsEnum_SMS,
  _$otpSettingsResponseEnabledChannelsEnum_WHATSAPP,
  _$otpSettingsResponseEnabledChannelsEnum_EMAIL,
]);

const OtpSettingsResponseFallbackOrderEnum
    _$otpSettingsResponseFallbackOrderEnum_SMS =
    const OtpSettingsResponseFallbackOrderEnum._('SMS');
const OtpSettingsResponseFallbackOrderEnum
    _$otpSettingsResponseFallbackOrderEnum_WHATSAPP =
    const OtpSettingsResponseFallbackOrderEnum._('WHATSAPP');
const OtpSettingsResponseFallbackOrderEnum
    _$otpSettingsResponseFallbackOrderEnum_EMAIL =
    const OtpSettingsResponseFallbackOrderEnum._('EMAIL');

OtpSettingsResponseFallbackOrderEnum
    _$otpSettingsResponseFallbackOrderEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$otpSettingsResponseFallbackOrderEnum_SMS;
    case 'WHATSAPP':
      return _$otpSettingsResponseFallbackOrderEnum_WHATSAPP;
    case 'EMAIL':
      return _$otpSettingsResponseFallbackOrderEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<OtpSettingsResponseFallbackOrderEnum>
    _$otpSettingsResponseFallbackOrderEnumValues = BuiltSet<
        OtpSettingsResponseFallbackOrderEnum>(const <OtpSettingsResponseFallbackOrderEnum>[
  _$otpSettingsResponseFallbackOrderEnum_SMS,
  _$otpSettingsResponseFallbackOrderEnum_WHATSAPP,
  _$otpSettingsResponseFallbackOrderEnum_EMAIL,
]);

Serializer<OtpSettingsResponseDefaultChannelEnum>
    _$otpSettingsResponseDefaultChannelEnumSerializer =
    _$OtpSettingsResponseDefaultChannelEnumSerializer();
Serializer<OtpSettingsResponseEnabledChannelsEnum>
    _$otpSettingsResponseEnabledChannelsEnumSerializer =
    _$OtpSettingsResponseEnabledChannelsEnumSerializer();
Serializer<OtpSettingsResponseFallbackOrderEnum>
    _$otpSettingsResponseFallbackOrderEnumSerializer =
    _$OtpSettingsResponseFallbackOrderEnumSerializer();

class _$OtpSettingsResponseDefaultChannelEnumSerializer
    implements PrimitiveSerializer<OtpSettingsResponseDefaultChannelEnum> {
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
    OtpSettingsResponseDefaultChannelEnum
  ];
  @override
  final String wireName = 'OtpSettingsResponseDefaultChannelEnum';

  @override
  Object serialize(
          Serializers serializers, OtpSettingsResponseDefaultChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpSettingsResponseDefaultChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpSettingsResponseDefaultChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpSettingsResponseEnabledChannelsEnumSerializer
    implements PrimitiveSerializer<OtpSettingsResponseEnabledChannelsEnum> {
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
    OtpSettingsResponseEnabledChannelsEnum
  ];
  @override
  final String wireName = 'OtpSettingsResponseEnabledChannelsEnum';

  @override
  Object serialize(Serializers serializers,
          OtpSettingsResponseEnabledChannelsEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpSettingsResponseEnabledChannelsEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpSettingsResponseEnabledChannelsEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpSettingsResponseFallbackOrderEnumSerializer
    implements PrimitiveSerializer<OtpSettingsResponseFallbackOrderEnum> {
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
    OtpSettingsResponseFallbackOrderEnum
  ];
  @override
  final String wireName = 'OtpSettingsResponseFallbackOrderEnum';

  @override
  Object serialize(
          Serializers serializers, OtpSettingsResponseFallbackOrderEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  OtpSettingsResponseFallbackOrderEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      OtpSettingsResponseFallbackOrderEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$OtpSettingsResponse extends OtpSettingsResponse {
  @override
  final int codeLength;
  @override
  final bool customerCanChoose;
  @override
  final OtpSettingsResponseDefaultChannelEnum defaultChannel;
  @override
  final BuiltList<OtpSettingsResponseEnabledChannelsEnum> enabledChannels;
  @override
  final BuiltList<OtpSettingsResponseFallbackOrderEnum> fallbackOrder;
  @override
  final int maxAttempts;
  @override
  final int maxRequestsPerHour;
  @override
  final int resendAfterSeconds;
  @override
  final DateTime updatedAt;
  @override
  final String updatedBy;
  @override
  final int validitySeconds;
  @override
  final int version;

  factory _$OtpSettingsResponse(
          [void Function(OtpSettingsResponseBuilder)? updates]) =>
      (OtpSettingsResponseBuilder()..update(updates))._build();

  _$OtpSettingsResponse._(
      {required this.codeLength,
      required this.customerCanChoose,
      required this.defaultChannel,
      required this.enabledChannels,
      required this.fallbackOrder,
      required this.maxAttempts,
      required this.maxRequestsPerHour,
      required this.resendAfterSeconds,
      required this.updatedAt,
      required this.updatedBy,
      required this.validitySeconds,
      required this.version})
      : super._();
  @override
  OtpSettingsResponse rebuild(
          void Function(OtpSettingsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpSettingsResponseBuilder toBuilder() =>
      OtpSettingsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpSettingsResponse &&
        codeLength == other.codeLength &&
        customerCanChoose == other.customerCanChoose &&
        defaultChannel == other.defaultChannel &&
        enabledChannels == other.enabledChannels &&
        fallbackOrder == other.fallbackOrder &&
        maxAttempts == other.maxAttempts &&
        maxRequestsPerHour == other.maxRequestsPerHour &&
        resendAfterSeconds == other.resendAfterSeconds &&
        updatedAt == other.updatedAt &&
        updatedBy == other.updatedBy &&
        validitySeconds == other.validitySeconds &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, codeLength.hashCode);
    _$hash = $jc(_$hash, customerCanChoose.hashCode);
    _$hash = $jc(_$hash, defaultChannel.hashCode);
    _$hash = $jc(_$hash, enabledChannels.hashCode);
    _$hash = $jc(_$hash, fallbackOrder.hashCode);
    _$hash = $jc(_$hash, maxAttempts.hashCode);
    _$hash = $jc(_$hash, maxRequestsPerHour.hashCode);
    _$hash = $jc(_$hash, resendAfterSeconds.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, updatedBy.hashCode);
    _$hash = $jc(_$hash, validitySeconds.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpSettingsResponse')
          ..add('codeLength', codeLength)
          ..add('customerCanChoose', customerCanChoose)
          ..add('defaultChannel', defaultChannel)
          ..add('enabledChannels', enabledChannels)
          ..add('fallbackOrder', fallbackOrder)
          ..add('maxAttempts', maxAttempts)
          ..add('maxRequestsPerHour', maxRequestsPerHour)
          ..add('resendAfterSeconds', resendAfterSeconds)
          ..add('updatedAt', updatedAt)
          ..add('updatedBy', updatedBy)
          ..add('validitySeconds', validitySeconds)
          ..add('version', version))
        .toString();
  }
}

class OtpSettingsResponseBuilder
    implements Builder<OtpSettingsResponse, OtpSettingsResponseBuilder> {
  _$OtpSettingsResponse? _$v;

  int? _codeLength;
  int? get codeLength => _$this._codeLength;
  set codeLength(int? codeLength) => _$this._codeLength = codeLength;

  bool? _customerCanChoose;
  bool? get customerCanChoose => _$this._customerCanChoose;
  set customerCanChoose(bool? customerCanChoose) =>
      _$this._customerCanChoose = customerCanChoose;

  OtpSettingsResponseDefaultChannelEnum? _defaultChannel;
  OtpSettingsResponseDefaultChannelEnum? get defaultChannel =>
      _$this._defaultChannel;
  set defaultChannel(OtpSettingsResponseDefaultChannelEnum? defaultChannel) =>
      _$this._defaultChannel = defaultChannel;

  ListBuilder<OtpSettingsResponseEnabledChannelsEnum>? _enabledChannels;
  ListBuilder<OtpSettingsResponseEnabledChannelsEnum> get enabledChannels =>
      _$this._enabledChannels ??=
          ListBuilder<OtpSettingsResponseEnabledChannelsEnum>();
  set enabledChannels(
          ListBuilder<OtpSettingsResponseEnabledChannelsEnum>?
              enabledChannels) =>
      _$this._enabledChannels = enabledChannels;

  ListBuilder<OtpSettingsResponseFallbackOrderEnum>? _fallbackOrder;
  ListBuilder<OtpSettingsResponseFallbackOrderEnum> get fallbackOrder =>
      _$this._fallbackOrder ??=
          ListBuilder<OtpSettingsResponseFallbackOrderEnum>();
  set fallbackOrder(
          ListBuilder<OtpSettingsResponseFallbackOrderEnum>? fallbackOrder) =>
      _$this._fallbackOrder = fallbackOrder;

  int? _maxAttempts;
  int? get maxAttempts => _$this._maxAttempts;
  set maxAttempts(int? maxAttempts) => _$this._maxAttempts = maxAttempts;

  int? _maxRequestsPerHour;
  int? get maxRequestsPerHour => _$this._maxRequestsPerHour;
  set maxRequestsPerHour(int? maxRequestsPerHour) =>
      _$this._maxRequestsPerHour = maxRequestsPerHour;

  int? _resendAfterSeconds;
  int? get resendAfterSeconds => _$this._resendAfterSeconds;
  set resendAfterSeconds(int? resendAfterSeconds) =>
      _$this._resendAfterSeconds = resendAfterSeconds;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  String? _updatedBy;
  String? get updatedBy => _$this._updatedBy;
  set updatedBy(String? updatedBy) => _$this._updatedBy = updatedBy;

  int? _validitySeconds;
  int? get validitySeconds => _$this._validitySeconds;
  set validitySeconds(int? validitySeconds) =>
      _$this._validitySeconds = validitySeconds;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  OtpSettingsResponseBuilder() {
    OtpSettingsResponse._defaults(this);
  }

  OtpSettingsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _codeLength = $v.codeLength;
      _customerCanChoose = $v.customerCanChoose;
      _defaultChannel = $v.defaultChannel;
      _enabledChannels = $v.enabledChannels.toBuilder();
      _fallbackOrder = $v.fallbackOrder.toBuilder();
      _maxAttempts = $v.maxAttempts;
      _maxRequestsPerHour = $v.maxRequestsPerHour;
      _resendAfterSeconds = $v.resendAfterSeconds;
      _updatedAt = $v.updatedAt;
      _updatedBy = $v.updatedBy;
      _validitySeconds = $v.validitySeconds;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpSettingsResponse other) {
    _$v = other as _$OtpSettingsResponse;
  }

  @override
  void update(void Function(OtpSettingsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpSettingsResponse build() => _build();

  _$OtpSettingsResponse _build() {
    _$OtpSettingsResponse _$result;
    try {
      _$result = _$v ??
          _$OtpSettingsResponse._(
            codeLength: BuiltValueNullFieldError.checkNotNull(
                codeLength, r'OtpSettingsResponse', 'codeLength'),
            customerCanChoose: BuiltValueNullFieldError.checkNotNull(
                customerCanChoose, r'OtpSettingsResponse', 'customerCanChoose'),
            defaultChannel: BuiltValueNullFieldError.checkNotNull(
                defaultChannel, r'OtpSettingsResponse', 'defaultChannel'),
            enabledChannels: enabledChannels.build(),
            fallbackOrder: fallbackOrder.build(),
            maxAttempts: BuiltValueNullFieldError.checkNotNull(
                maxAttempts, r'OtpSettingsResponse', 'maxAttempts'),
            maxRequestsPerHour: BuiltValueNullFieldError.checkNotNull(
                maxRequestsPerHour,
                r'OtpSettingsResponse',
                'maxRequestsPerHour'),
            resendAfterSeconds: BuiltValueNullFieldError.checkNotNull(
                resendAfterSeconds,
                r'OtpSettingsResponse',
                'resendAfterSeconds'),
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'OtpSettingsResponse', 'updatedAt'),
            updatedBy: BuiltValueNullFieldError.checkNotNull(
                updatedBy, r'OtpSettingsResponse', 'updatedBy'),
            validitySeconds: BuiltValueNullFieldError.checkNotNull(
                validitySeconds, r'OtpSettingsResponse', 'validitySeconds'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'OtpSettingsResponse', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'enabledChannels';
        enabledChannels.build();
        _$failedField = 'fallbackOrder';
        fallbackOrder.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'OtpSettingsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
