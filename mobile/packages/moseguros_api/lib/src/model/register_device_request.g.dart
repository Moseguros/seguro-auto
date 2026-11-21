// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'register_device_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RegisterDeviceRequest extends RegisterDeviceRequest {
  @override
  final String? appVersion;
  @override
  final DevicePlatform platform;
  @override
  final String token;

  factory _$RegisterDeviceRequest(
          [void Function(RegisterDeviceRequestBuilder)? updates]) =>
      (RegisterDeviceRequestBuilder()..update(updates))._build();

  _$RegisterDeviceRequest._(
      {this.appVersion, required this.platform, required this.token})
      : super._();
  @override
  RegisterDeviceRequest rebuild(
          void Function(RegisterDeviceRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RegisterDeviceRequestBuilder toBuilder() =>
      RegisterDeviceRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RegisterDeviceRequest &&
        appVersion == other.appVersion &&
        platform == other.platform &&
        token == other.token;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, appVersion.hashCode);
    _$hash = $jc(_$hash, platform.hashCode);
    _$hash = $jc(_$hash, token.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RegisterDeviceRequest')
          ..add('appVersion', appVersion)
          ..add('platform', platform)
          ..add('token', token))
        .toString();
  }
}

class RegisterDeviceRequestBuilder
    implements Builder<RegisterDeviceRequest, RegisterDeviceRequestBuilder> {
  _$RegisterDeviceRequest? _$v;

  String? _appVersion;
  String? get appVersion => _$this._appVersion;
  set appVersion(String? appVersion) => _$this._appVersion = appVersion;

  DevicePlatform? _platform;
  DevicePlatform? get platform => _$this._platform;
  set platform(DevicePlatform? platform) => _$this._platform = platform;

  String? _token;
  String? get token => _$this._token;
  set token(String? token) => _$this._token = token;

  RegisterDeviceRequestBuilder() {
    RegisterDeviceRequest._defaults(this);
  }

  RegisterDeviceRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _appVersion = $v.appVersion;
      _platform = $v.platform;
      _token = $v.token;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RegisterDeviceRequest other) {
    _$v = other as _$RegisterDeviceRequest;
  }

  @override
  void update(void Function(RegisterDeviceRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RegisterDeviceRequest build() => _build();

  _$RegisterDeviceRequest _build() {
    final _$result = _$v ??
        _$RegisterDeviceRequest._(
          appVersion: appVersion,
          platform: BuiltValueNullFieldError.checkNotNull(
              platform, r'RegisterDeviceRequest', 'platform'),
          token: BuiltValueNullFieldError.checkNotNull(
              token, r'RegisterDeviceRequest', 'token'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
