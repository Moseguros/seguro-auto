// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'public_platform_settings_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PublicPlatformSettingsResponse extends PublicPlatformSettingsResponse {
  @override
  final BuiltList<PublicPlatformSettingResponse> settings;

  factory _$PublicPlatformSettingsResponse(
          [void Function(PublicPlatformSettingsResponseBuilder)? updates]) =>
      (PublicPlatformSettingsResponseBuilder()..update(updates))._build();

  _$PublicPlatformSettingsResponse._({required this.settings}) : super._();
  @override
  PublicPlatformSettingsResponse rebuild(
          void Function(PublicPlatformSettingsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PublicPlatformSettingsResponseBuilder toBuilder() =>
      PublicPlatformSettingsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PublicPlatformSettingsResponse &&
        settings == other.settings;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, settings.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PublicPlatformSettingsResponse')
          ..add('settings', settings))
        .toString();
  }
}

class PublicPlatformSettingsResponseBuilder
    implements
        Builder<PublicPlatformSettingsResponse,
            PublicPlatformSettingsResponseBuilder> {
  _$PublicPlatformSettingsResponse? _$v;

  ListBuilder<PublicPlatformSettingResponse>? _settings;
  ListBuilder<PublicPlatformSettingResponse> get settings =>
      _$this._settings ??= ListBuilder<PublicPlatformSettingResponse>();
  set settings(ListBuilder<PublicPlatformSettingResponse>? settings) =>
      _$this._settings = settings;

  PublicPlatformSettingsResponseBuilder() {
    PublicPlatformSettingsResponse._defaults(this);
  }

  PublicPlatformSettingsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _settings = $v.settings.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PublicPlatformSettingsResponse other) {
    _$v = other as _$PublicPlatformSettingsResponse;
  }

  @override
  void update(void Function(PublicPlatformSettingsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PublicPlatformSettingsResponse build() => _build();

  _$PublicPlatformSettingsResponse _build() {
    _$PublicPlatformSettingsResponse _$result;
    try {
      _$result = _$v ??
          _$PublicPlatformSettingsResponse._(
            settings: settings.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'settings';
        settings.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PublicPlatformSettingsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
