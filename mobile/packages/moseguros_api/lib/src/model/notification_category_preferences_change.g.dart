// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_category_preferences_change.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationCategoryPreferencesChange
    extends NotificationCategoryPreferencesChange {
  @override
  final NotificationCategory category;
  @override
  final BuiltList<NotificationChannelPreferenceChange> channels;

  factory _$NotificationCategoryPreferencesChange(
          [void Function(NotificationCategoryPreferencesChangeBuilder)?
              updates]) =>
      (NotificationCategoryPreferencesChangeBuilder()..update(updates))
          ._build();

  _$NotificationCategoryPreferencesChange._(
      {required this.category, required this.channels})
      : super._();
  @override
  NotificationCategoryPreferencesChange rebuild(
          void Function(NotificationCategoryPreferencesChangeBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationCategoryPreferencesChangeBuilder toBuilder() =>
      NotificationCategoryPreferencesChangeBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationCategoryPreferencesChange &&
        category == other.category &&
        channels == other.channels;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, category.hashCode);
    _$hash = $jc(_$hash, channels.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(
            r'NotificationCategoryPreferencesChange')
          ..add('category', category)
          ..add('channels', channels))
        .toString();
  }
}

class NotificationCategoryPreferencesChangeBuilder
    implements
        Builder<NotificationCategoryPreferencesChange,
            NotificationCategoryPreferencesChangeBuilder> {
  _$NotificationCategoryPreferencesChange? _$v;

  NotificationCategory? _category;
  NotificationCategory? get category => _$this._category;
  set category(NotificationCategory? category) => _$this._category = category;

  ListBuilder<NotificationChannelPreferenceChange>? _channels;
  ListBuilder<NotificationChannelPreferenceChange> get channels =>
      _$this._channels ??= ListBuilder<NotificationChannelPreferenceChange>();
  set channels(ListBuilder<NotificationChannelPreferenceChange>? channels) =>
      _$this._channels = channels;

  NotificationCategoryPreferencesChangeBuilder() {
    NotificationCategoryPreferencesChange._defaults(this);
  }

  NotificationCategoryPreferencesChangeBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _category = $v.category;
      _channels = $v.channels.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationCategoryPreferencesChange other) {
    _$v = other as _$NotificationCategoryPreferencesChange;
  }

  @override
  void update(
      void Function(NotificationCategoryPreferencesChangeBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationCategoryPreferencesChange build() => _build();

  _$NotificationCategoryPreferencesChange _build() {
    _$NotificationCategoryPreferencesChange _$result;
    try {
      _$result = _$v ??
          _$NotificationCategoryPreferencesChange._(
            category: BuiltValueNullFieldError.checkNotNull(
                category, r'NotificationCategoryPreferencesChange', 'category'),
            channels: channels.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'channels';
        channels.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'NotificationCategoryPreferencesChange',
            _$failedField,
            e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
