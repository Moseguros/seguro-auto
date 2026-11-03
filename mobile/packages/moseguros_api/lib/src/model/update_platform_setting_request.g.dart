// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_platform_setting_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdatePlatformSettingRequest extends UpdatePlatformSettingRequest {
  @override
  final String value;
  @override
  final int version;

  factory _$UpdatePlatformSettingRequest(
          [void Function(UpdatePlatformSettingRequestBuilder)? updates]) =>
      (UpdatePlatformSettingRequestBuilder()..update(updates))._build();

  _$UpdatePlatformSettingRequest._({required this.value, required this.version})
      : super._();
  @override
  UpdatePlatformSettingRequest rebuild(
          void Function(UpdatePlatformSettingRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdatePlatformSettingRequestBuilder toBuilder() =>
      UpdatePlatformSettingRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdatePlatformSettingRequest &&
        value == other.value &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, value.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdatePlatformSettingRequest')
          ..add('value', value)
          ..add('version', version))
        .toString();
  }
}

class UpdatePlatformSettingRequestBuilder
    implements
        Builder<UpdatePlatformSettingRequest,
            UpdatePlatformSettingRequestBuilder> {
  _$UpdatePlatformSettingRequest? _$v;

  String? _value;
  String? get value => _$this._value;
  set value(String? value) => _$this._value = value;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdatePlatformSettingRequestBuilder() {
    UpdatePlatformSettingRequest._defaults(this);
  }

  UpdatePlatformSettingRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _value = $v.value;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdatePlatformSettingRequest other) {
    _$v = other as _$UpdatePlatformSettingRequest;
  }

  @override
  void update(void Function(UpdatePlatformSettingRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdatePlatformSettingRequest build() => _build();

  _$UpdatePlatformSettingRequest _build() {
    final _$result = _$v ??
        _$UpdatePlatformSettingRequest._(
          value: BuiltValueNullFieldError.checkNotNull(
              value, r'UpdatePlatformSettingRequest', 'value'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdatePlatformSettingRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
