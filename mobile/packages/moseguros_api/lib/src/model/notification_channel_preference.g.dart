// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_channel_preference.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationChannelPreference extends NotificationChannelPreference {
  @override
  final bool available;
  @override
  final NotificationChannel channel;
  @override
  final bool enabled;

  factory _$NotificationChannelPreference(
          [void Function(NotificationChannelPreferenceBuilder)? updates]) =>
      (NotificationChannelPreferenceBuilder()..update(updates))._build();

  _$NotificationChannelPreference._(
      {required this.available, required this.channel, required this.enabled})
      : super._();
  @override
  NotificationChannelPreference rebuild(
          void Function(NotificationChannelPreferenceBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationChannelPreferenceBuilder toBuilder() =>
      NotificationChannelPreferenceBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationChannelPreference &&
        available == other.available &&
        channel == other.channel &&
        enabled == other.enabled;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, available.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, enabled.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationChannelPreference')
          ..add('available', available)
          ..add('channel', channel)
          ..add('enabled', enabled))
        .toString();
  }
}

class NotificationChannelPreferenceBuilder
    implements
        Builder<NotificationChannelPreference,
            NotificationChannelPreferenceBuilder> {
  _$NotificationChannelPreference? _$v;

  bool? _available;
  bool? get available => _$this._available;
  set available(bool? available) => _$this._available = available;

  NotificationChannel? _channel;
  NotificationChannel? get channel => _$this._channel;
  set channel(NotificationChannel? channel) => _$this._channel = channel;

  bool? _enabled;
  bool? get enabled => _$this._enabled;
  set enabled(bool? enabled) => _$this._enabled = enabled;

  NotificationChannelPreferenceBuilder() {
    NotificationChannelPreference._defaults(this);
  }

  NotificationChannelPreferenceBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _available = $v.available;
      _channel = $v.channel;
      _enabled = $v.enabled;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationChannelPreference other) {
    _$v = other as _$NotificationChannelPreference;
  }

  @override
  void update(void Function(NotificationChannelPreferenceBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationChannelPreference build() => _build();

  _$NotificationChannelPreference _build() {
    final _$result = _$v ??
        _$NotificationChannelPreference._(
          available: BuiltValueNullFieldError.checkNotNull(
              available, r'NotificationChannelPreference', 'available'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'NotificationChannelPreference', 'channel'),
          enabled: BuiltValueNullFieldError.checkNotNull(
              enabled, r'NotificationChannelPreference', 'enabled'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
