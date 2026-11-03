// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'platform_settings_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PlatformSettingsResponse extends PlatformSettingsResponse {
  @override
  final BuiltList<PlatformSettingResponse> settings;

  factory _$PlatformSettingsResponse(
          [void Function(PlatformSettingsResponseBuilder)? updates]) =>
      (PlatformSettingsResponseBuilder()..update(updates))._build();

  _$PlatformSettingsResponse._({required this.settings}) : super._();
  @override
  PlatformSettingsResponse rebuild(
          void Function(PlatformSettingsResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PlatformSettingsResponseBuilder toBuilder() =>
      PlatformSettingsResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PlatformSettingsResponse && settings == other.settings;
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
    return (newBuiltValueToStringHelper(r'PlatformSettingsResponse')
          ..add('settings', settings))
        .toString();
  }
}

class PlatformSettingsResponseBuilder
    implements
        Builder<PlatformSettingsResponse, PlatformSettingsResponseBuilder> {
  _$PlatformSettingsResponse? _$v;

  ListBuilder<PlatformSettingResponse>? _settings;
  ListBuilder<PlatformSettingResponse> get settings =>
      _$this._settings ??= ListBuilder<PlatformSettingResponse>();
  set settings(ListBuilder<PlatformSettingResponse>? settings) =>
      _$this._settings = settings;

  PlatformSettingsResponseBuilder() {
    PlatformSettingsResponse._defaults(this);
  }

  PlatformSettingsResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _settings = $v.settings.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PlatformSettingsResponse other) {
    _$v = other as _$PlatformSettingsResponse;
  }

  @override
  void update(void Function(PlatformSettingsResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PlatformSettingsResponse build() => _build();

  _$PlatformSettingsResponse _build() {
    _$PlatformSettingsResponse _$result;
    try {
      _$result = _$v ??
          _$PlatformSettingsResponse._(
            settings: settings.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'settings';
        settings.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PlatformSettingsResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
