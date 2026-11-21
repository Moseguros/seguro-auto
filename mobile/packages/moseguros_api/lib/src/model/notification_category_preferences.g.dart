// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_category_preferences.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationCategoryPreferences
    extends NotificationCategoryPreferences {
  @override
  final NotificationCategory category;
  @override
  final BuiltList<NotificationChannelPreference> channels;
  @override
  final bool mandatory;

  factory _$NotificationCategoryPreferences(
          [void Function(NotificationCategoryPreferencesBuilder)? updates]) =>
      (NotificationCategoryPreferencesBuilder()..update(updates))._build();

  _$NotificationCategoryPreferences._(
      {required this.category, required this.channels, required this.mandatory})
      : super._();
  @override
  NotificationCategoryPreferences rebuild(
          void Function(NotificationCategoryPreferencesBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationCategoryPreferencesBuilder toBuilder() =>
      NotificationCategoryPreferencesBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationCategoryPreferences &&
        category == other.category &&
        channels == other.channels &&
        mandatory == other.mandatory;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, channels.hashCode);
    _$hash = $jc(_$hash, mandatory.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationCategoryPreferences')
          ..add('category', category)
          ..add('channels', channels)
          ..add('mandatory', mandatory))
        .toString();
  }
}

class NotificationCategoryPreferencesBuilder
    implements
        Builder<NotificationCategoryPreferences,
            NotificationCategoryPreferencesBuilder> {
  _$NotificationCategoryPreferences? _$v;

  NotificationCategory? _category;
  NotificationCategory? get category => _$this._category;
  set category(NotificationCategory? category) => _$this._category = category;

  ListBuilder<NotificationChannelPreference>? _channels;
  ListBuilder<NotificationChannelPreference> get channels =>
      _$this._channels ??= ListBuilder<NotificationChannelPreference>();
  set channels(ListBuilder<NotificationChannelPreference>? channels) =>
      _$this._channels = channels;

  bool? _mandatory;
  bool? get mandatory => _$this._mandatory;
  set mandatory(bool? mandatory) => _$this._mandatory = mandatory;

  NotificationCategoryPreferencesBuilder() {
    NotificationCategoryPreferences._defaults(this);
  }

  NotificationCategoryPreferencesBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _channels = $v.channels.toBuilder();
      _mandatory = $v.mandatory;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationCategoryPreferences other) {
    _$v = other as _$NotificationCategoryPreferences;
  }

  @override
  void update(void Function(NotificationCategoryPreferencesBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationCategoryPreferences build() => _build();

  _$NotificationCategoryPreferences _build() {
    _$NotificationCategoryPreferences _$result;
    try {
      _$result = _$v ??
          _$NotificationCategoryPreferences._(
            category: BuiltValueNullFieldError.checkNotNull(
                category, r'NotificationCategoryPreferences', 'category'),
            channels: channels.build(),
            mandatory: BuiltValueNullFieldError.checkNotNull(
                mandatory, r'NotificationCategoryPreferences', 'mandatory'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'channels';
        channels.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'NotificationCategoryPreferences', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
