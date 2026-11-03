// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_otp_settings_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

const UpdateOtpSettingsRequestDefaultChannelEnum
    _$updateOtpSettingsRequestDefaultChannelEnum_SMS =
    const UpdateOtpSettingsRequestDefaultChannelEnum._('SMS');
const UpdateOtpSettingsRequestDefaultChannelEnum
    _$updateOtpSettingsRequestDefaultChannelEnum_WHATSAPP =
    const UpdateOtpSettingsRequestDefaultChannelEnum._('WHATSAPP');
const UpdateOtpSettingsRequestDefaultChannelEnum
    _$updateOtpSettingsRequestDefaultChannelEnum_EMAIL =
    const UpdateOtpSettingsRequestDefaultChannelEnum._('EMAIL');

UpdateOtpSettingsRequestDefaultChannelEnum
    _$updateOtpSettingsRequestDefaultChannelEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$updateOtpSettingsRequestDefaultChannelEnum_SMS;
    case 'WHATSAPP':
      return _$updateOtpSettingsRequestDefaultChannelEnum_WHATSAPP;
    case 'EMAIL':
      return _$updateOtpSettingsRequestDefaultChannelEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateOtpSettingsRequestDefaultChannelEnum>
    _$updateOtpSettingsRequestDefaultChannelEnumValues = BuiltSet<
        UpdateOtpSettingsRequestDefaultChannelEnum>(const <UpdateOtpSettingsRequestDefaultChannelEnum>[
  _$updateOtpSettingsRequestDefaultChannelEnum_SMS,
  _$updateOtpSettingsRequestDefaultChannelEnum_WHATSAPP,
  _$updateOtpSettingsRequestDefaultChannelEnum_EMAIL,
]);

const UpdateOtpSettingsRequestEnabledChannelsEnum
    _$updateOtpSettingsRequestEnabledChannelsEnum_SMS =
    const UpdateOtpSettingsRequestEnabledChannelsEnum._('SMS');
const UpdateOtpSettingsRequestEnabledChannelsEnum
    _$updateOtpSettingsRequestEnabledChannelsEnum_WHATSAPP =
    const UpdateOtpSettingsRequestEnabledChannelsEnum._('WHATSAPP');
const UpdateOtpSettingsRequestEnabledChannelsEnum
    _$updateOtpSettingsRequestEnabledChannelsEnum_EMAIL =
    const UpdateOtpSettingsRequestEnabledChannelsEnum._('EMAIL');

UpdateOtpSettingsRequestEnabledChannelsEnum
    _$updateOtpSettingsRequestEnabledChannelsEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$updateOtpSettingsRequestEnabledChannelsEnum_SMS;
    case 'WHATSAPP':
      return _$updateOtpSettingsRequestEnabledChannelsEnum_WHATSAPP;
    case 'EMAIL':
      return _$updateOtpSettingsRequestEnabledChannelsEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateOtpSettingsRequestEnabledChannelsEnum>
    _$updateOtpSettingsRequestEnabledChannelsEnumValues = BuiltSet<
        UpdateOtpSettingsRequestEnabledChannelsEnum>(const <UpdateOtpSettingsRequestEnabledChannelsEnum>[
  _$updateOtpSettingsRequestEnabledChannelsEnum_SMS,
  _$updateOtpSettingsRequestEnabledChannelsEnum_WHATSAPP,
  _$updateOtpSettingsRequestEnabledChannelsEnum_EMAIL,
]);

const UpdateOtpSettingsRequestFallbackOrderEnum
    _$updateOtpSettingsRequestFallbackOrderEnum_SMS =
    const UpdateOtpSettingsRequestFallbackOrderEnum._('SMS');
const UpdateOtpSettingsRequestFallbackOrderEnum
    _$updateOtpSettingsRequestFallbackOrderEnum_WHATSAPP =
    const UpdateOtpSettingsRequestFallbackOrderEnum._('WHATSAPP');
const UpdateOtpSettingsRequestFallbackOrderEnum
    _$updateOtpSettingsRequestFallbackOrderEnum_EMAIL =
    const UpdateOtpSettingsRequestFallbackOrderEnum._('EMAIL');

UpdateOtpSettingsRequestFallbackOrderEnum
    _$updateOtpSettingsRequestFallbackOrderEnumValueOf(String name) {
  switch (name) {
    case 'SMS':
      return _$updateOtpSettingsRequestFallbackOrderEnum_SMS;
    case 'WHATSAPP':
      return _$updateOtpSettingsRequestFallbackOrderEnum_WHATSAPP;
    case 'EMAIL':
      return _$updateOtpSettingsRequestFallbackOrderEnum_EMAIL;
    default:
      throw ArgumentError(name);
  }
}

final BuiltSet<UpdateOtpSettingsRequestFallbackOrderEnum>
    _$updateOtpSettingsRequestFallbackOrderEnumValues = BuiltSet<
        UpdateOtpSettingsRequestFallbackOrderEnum>(const <UpdateOtpSettingsRequestFallbackOrderEnum>[
  _$updateOtpSettingsRequestFallbackOrderEnum_SMS,
  _$updateOtpSettingsRequestFallbackOrderEnum_WHATSAPP,
  _$updateOtpSettingsRequestFallbackOrderEnum_EMAIL,
]);

Serializer<UpdateOtpSettingsRequestDefaultChannelEnum>
    _$updateOtpSettingsRequestDefaultChannelEnumSerializer =
    _$UpdateOtpSettingsRequestDefaultChannelEnumSerializer();
Serializer<UpdateOtpSettingsRequestEnabledChannelsEnum>
    _$updateOtpSettingsRequestEnabledChannelsEnumSerializer =
    _$UpdateOtpSettingsRequestEnabledChannelsEnumSerializer();
Serializer<UpdateOtpSettingsRequestFallbackOrderEnum>
    _$updateOtpSettingsRequestFallbackOrderEnumSerializer =
    _$UpdateOtpSettingsRequestFallbackOrderEnumSerializer();

class _$UpdateOtpSettingsRequestDefaultChannelEnumSerializer
    implements PrimitiveSerializer<UpdateOtpSettingsRequestDefaultChannelEnum> {
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
    UpdateOtpSettingsRequestDefaultChannelEnum
  ];
  @override
  final String wireName = 'UpdateOtpSettingsRequestDefaultChannelEnum';

  @override
  Object serialize(Serializers serializers,
          UpdateOtpSettingsRequestDefaultChannelEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateOtpSettingsRequestDefaultChannelEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateOtpSettingsRequestDefaultChannelEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateOtpSettingsRequestEnabledChannelsEnumSerializer
    implements
        PrimitiveSerializer<UpdateOtpSettingsRequestEnabledChannelsEnum> {
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
    UpdateOtpSettingsRequestEnabledChannelsEnum
  ];
  @override
  final String wireName = 'UpdateOtpSettingsRequestEnabledChannelsEnum';

  @override
  Object serialize(Serializers serializers,
          UpdateOtpSettingsRequestEnabledChannelsEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateOtpSettingsRequestEnabledChannelsEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateOtpSettingsRequestEnabledChannelsEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateOtpSettingsRequestFallbackOrderEnumSerializer
    implements PrimitiveSerializer<UpdateOtpSettingsRequestFallbackOrderEnum> {
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
    UpdateOtpSettingsRequestFallbackOrderEnum
  ];
  @override
  final String wireName = 'UpdateOtpSettingsRequestFallbackOrderEnum';

  @override
  Object serialize(Serializers serializers,
          UpdateOtpSettingsRequestFallbackOrderEnum object,
          {FullType specifiedType = FullType.unspecified}) =>
      _toWire[object.name] ?? object.name;

  @override
  UpdateOtpSettingsRequestFallbackOrderEnum deserialize(
          Serializers serializers, Object serialized,
          {FullType specifiedType = FullType.unspecified}) =>
      UpdateOtpSettingsRequestFallbackOrderEnum.valueOf(
          _fromWire[serialized] ?? (serialized is String ? serialized : ''));
}

class _$UpdateOtpSettingsRequest extends UpdateOtpSettingsRequest {
  @override
  final int codeLength;
  @override
  final bool customerCanChoose;
  @override
  final UpdateOtpSettingsRequestDefaultChannelEnum defaultChannel;
  @override
  final BuiltList<UpdateOtpSettingsRequestEnabledChannelsEnum> enabledChannels;
  @override
  final BuiltList<UpdateOtpSettingsRequestFallbackOrderEnum> fallbackOrder;
  @override
  final int maxAttempts;
  @override
  final int maxRequestsPerHour;
  @override
  final int resendAfterSeconds;
  @override
  final int validitySeconds;
  @override
  final int version;

  factory _$UpdateOtpSettingsRequest(
          [void Function(UpdateOtpSettingsRequestBuilder)? updates]) =>
      (UpdateOtpSettingsRequestBuilder()..update(updates))._build();

  _$UpdateOtpSettingsRequest._(
      {required this.codeLength,
      required this.customerCanChoose,
      required this.defaultChannel,
      required this.enabledChannels,
      required this.fallbackOrder,
      required this.maxAttempts,
      required this.maxRequestsPerHour,
      required this.resendAfterSeconds,
      required this.validitySeconds,
      required this.version})
      : super._();
  @override
  UpdateOtpSettingsRequest rebuild(
          void Function(UpdateOtpSettingsRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateOtpSettingsRequestBuilder toBuilder() =>
      UpdateOtpSettingsRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateOtpSettingsRequest &&
        codeLength == other.codeLength &&
        customerCanChoose == other.customerCanChoose &&
        defaultChannel == other.defaultChannel &&
        enabledChannels == other.enabledChannels &&
        fallbackOrder == other.fallbackOrder &&
        maxAttempts == other.maxAttempts &&
        maxRequestsPerHour == other.maxRequestsPerHour &&
        resendAfterSeconds == other.resendAfterSeconds &&
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
    _$hash = $jc(_$hash, validitySeconds.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateOtpSettingsRequest')
          ..add('codeLength', codeLength)
          ..add('customerCanChoose', customerCanChoose)
          ..add('defaultChannel', defaultChannel)
          ..add('enabledChannels', enabledChannels)
          ..add('fallbackOrder', fallbackOrder)
          ..add('maxAttempts', maxAttempts)
          ..add('maxRequestsPerHour', maxRequestsPerHour)
          ..add('resendAfterSeconds', resendAfterSeconds)
          ..add('validitySeconds', validitySeconds)
          ..add('version', version))
        .toString();
  }
}

class UpdateOtpSettingsRequestBuilder
    implements
        Builder<UpdateOtpSettingsRequest, UpdateOtpSettingsRequestBuilder> {
  _$UpdateOtpSettingsRequest? _$v;

  int? _codeLength;
  int? get codeLength => _$this._codeLength;
  set codeLength(int? codeLength) => _$this._codeLength = codeLength;

  bool? _customerCanChoose;
  bool? get customerCanChoose => _$this._customerCanChoose;
  set customerCanChoose(bool? customerCanChoose) =>
      _$this._customerCanChoose = customerCanChoose;

  UpdateOtpSettingsRequestDefaultChannelEnum? _defaultChannel;
  UpdateOtpSettingsRequestDefaultChannelEnum? get defaultChannel =>
      _$this._defaultChannel;
  set defaultChannel(
          UpdateOtpSettingsRequestDefaultChannelEnum? defaultChannel) =>
      _$this._defaultChannel = defaultChannel;

  ListBuilder<UpdateOtpSettingsRequestEnabledChannelsEnum>? _enabledChannels;
  ListBuilder<UpdateOtpSettingsRequestEnabledChannelsEnum>
      get enabledChannels => _$this._enabledChannels ??=
          ListBuilder<UpdateOtpSettingsRequestEnabledChannelsEnum>();
  set enabledChannels(
          ListBuilder<UpdateOtpSettingsRequestEnabledChannelsEnum>?
              enabledChannels) =>
      _$this._enabledChannels = enabledChannels;

  ListBuilder<UpdateOtpSettingsRequestFallbackOrderEnum>? _fallbackOrder;
  ListBuilder<UpdateOtpSettingsRequestFallbackOrderEnum> get fallbackOrder =>
      _$this._fallbackOrder ??=
          ListBuilder<UpdateOtpSettingsRequestFallbackOrderEnum>();
  set fallbackOrder(
          ListBuilder<UpdateOtpSettingsRequestFallbackOrderEnum>?
              fallbackOrder) =>
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

  int? _validitySeconds;
  int? get validitySeconds => _$this._validitySeconds;
  set validitySeconds(int? validitySeconds) =>
      _$this._validitySeconds = validitySeconds;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateOtpSettingsRequestBuilder() {
    UpdateOtpSettingsRequest._defaults(this);
  }

  UpdateOtpSettingsRequestBuilder get _$this {
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
      _validitySeconds = $v.validitySeconds;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateOtpSettingsRequest other) {
    _$v = other as _$UpdateOtpSettingsRequest;
  }

  @override
  void update(void Function(UpdateOtpSettingsRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateOtpSettingsRequest build() => _build();

  _$UpdateOtpSettingsRequest _build() {
    _$UpdateOtpSettingsRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateOtpSettingsRequest._(
            codeLength: BuiltValueNullFieldError.checkNotNull(
                codeLength, r'UpdateOtpSettingsRequest', 'codeLength'),
            customerCanChoose: BuiltValueNullFieldError.checkNotNull(
                customerCanChoose,
                r'UpdateOtpSettingsRequest',
                'customerCanChoose'),
            defaultChannel: BuiltValueNullFieldError.checkNotNull(
                defaultChannel, r'UpdateOtpSettingsRequest', 'defaultChannel'),
            enabledChannels: enabledChannels.build(),
            fallbackOrder: fallbackOrder.build(),
            maxAttempts: BuiltValueNullFieldError.checkNotNull(
                maxAttempts, r'UpdateOtpSettingsRequest', 'maxAttempts'),
            maxRequestsPerHour: BuiltValueNullFieldError.checkNotNull(
                maxRequestsPerHour,
                r'UpdateOtpSettingsRequest',
                'maxRequestsPerHour'),
            resendAfterSeconds: BuiltValueNullFieldError.checkNotNull(
                resendAfterSeconds,
                r'UpdateOtpSettingsRequest',
                'resendAfterSeconds'),
            validitySeconds: BuiltValueNullFieldError.checkNotNull(
                validitySeconds,
                r'UpdateOtpSettingsRequest',
                'validitySeconds'),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateOtpSettingsRequest', 'version'),
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
            r'UpdateOtpSettingsRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
