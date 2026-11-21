// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'device_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$DeviceResponse extends DeviceResponse {
  @override
  final bool active;
  @override
  final String id;
  @override
  final DevicePlatform platform;

  factory _$DeviceResponse([void Function(DeviceResponseBuilder)? updates]) =>
      (DeviceResponseBuilder()..update(updates))._build();

  _$DeviceResponse._(
      {required this.active, required this.id, required this.platform})
      : super._();
  @override
  DeviceResponse rebuild(void Function(DeviceResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  DeviceResponseBuilder toBuilder() => DeviceResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is DeviceResponse &&
        active == other.active &&
        id == other.id &&
        platform == other.platform;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, platform.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'DeviceResponse')
          ..add('active', active)
          ..add('id', id)
          ..add('platform', platform))
        .toString();
  }
}

class DeviceResponseBuilder
    implements Builder<DeviceResponse, DeviceResponseBuilder> {
  _$DeviceResponse? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  DevicePlatform? _platform;
  DevicePlatform? get platform => _$this._platform;
  set platform(DevicePlatform? platform) => _$this._platform = platform;

  DeviceResponseBuilder() {
    DeviceResponse._defaults(this);
  }

  DeviceResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _id = $v.id;
      _platform = $v.platform;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(DeviceResponse other) {
    _$v = other as _$DeviceResponse;
  }

  @override
  void update(void Function(DeviceResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  DeviceResponse build() => _build();

  _$DeviceResponse _build() {
    final _$result = _$v ??
        _$DeviceResponse._(
          active: BuiltValueNullFieldError.checkNotNull(
              active, r'DeviceResponse', 'active'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'DeviceResponse', 'id'),
          platform: BuiltValueNullFieldError.checkNotNull(
              platform, r'DeviceResponse', 'platform'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
