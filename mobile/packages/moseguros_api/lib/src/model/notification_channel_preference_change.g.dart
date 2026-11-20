// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_channel_preference_change.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationChannelPreferenceChange
    extends NotificationChannelPreferenceChange {
  @override
  final NotificationChannel channel;
  @override
  final bool enabled;

  factory _$NotificationChannelPreferenceChange(
          [void Function(NotificationChannelPreferenceChangeBuilder)?
              updates]) =>
      (NotificationChannelPreferenceChangeBuilder()..update(updates))._build();

  _$NotificationChannelPreferenceChange._(
      {required this.channel, required this.enabled})
      : super._();
  @override
  NotificationChannelPreferenceChange rebuild(
          void Function(NotificationChannelPreferenceChangeBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationChannelPreferenceChangeBuilder toBuilder() =>
      NotificationChannelPreferenceChangeBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationChannelPreferenceChange &&
        channel == other.channel &&
        enabled == other.enabled;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, enabled.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationChannelPreferenceChange')
          ..add('channel', channel)
          ..add('enabled', enabled))
        .toString();
  }
}

class NotificationChannelPreferenceChangeBuilder
    implements
        Builder<NotificationChannelPreferenceChange,
            NotificationChannelPreferenceChangeBuilder> {
  _$NotificationChannelPreferenceChange? _$v;

  NotificationChannel? _channel;
  NotificationChannel? get channel => _$this._channel;
  set channel(NotificationChannel? channel) => _$this._channel = channel;

  bool? _enabled;
  bool? get enabled => _$this._enabled;
  set enabled(bool? enabled) => _$this._enabled = enabled;

  NotificationChannelPreferenceChangeBuilder() {
    NotificationChannelPreferenceChange._defaults(this);
  }

  NotificationChannelPreferenceChangeBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _channel = $v.channel;
      _enabled = $v.enabled;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationChannelPreferenceChange other) {
    _$v = other as _$NotificationChannelPreferenceChange;
  }

  @override
  void update(
      void Function(NotificationChannelPreferenceChangeBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationChannelPreferenceChange build() => _build();

  _$NotificationChannelPreferenceChange _build() {
    final _$result = _$v ??
        _$NotificationChannelPreferenceChange._(
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'NotificationChannelPreferenceChange', 'channel'),
          enabled: BuiltValueNullFieldError.checkNotNull(
              enabled, r'NotificationChannelPreferenceChange', 'enabled'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
