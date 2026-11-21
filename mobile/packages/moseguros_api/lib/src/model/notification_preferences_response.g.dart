// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_preferences_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationPreferencesResponse
    extends NotificationPreferencesResponse {
  @override
  final BuiltList<NotificationCategoryPreferences> categories;

  factory _$NotificationPreferencesResponse(
          [void Function(NotificationPreferencesResponseBuilder)? updates]) =>
      (NotificationPreferencesResponseBuilder()..update(updates))._build();

  _$NotificationPreferencesResponse._({required this.categories}) : super._();
  @override
  NotificationPreferencesResponse rebuild(
          void Function(NotificationPreferencesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationPreferencesResponseBuilder toBuilder() =>
      NotificationPreferencesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationPreferencesResponse &&
        categories == other.categories;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, categories.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationPreferencesResponse')
          ..add('categories', categories))
        .toString();
  }
}

class NotificationPreferencesResponseBuilder
    implements
        Builder<NotificationPreferencesResponse,
            NotificationPreferencesResponseBuilder> {
  _$NotificationPreferencesResponse? _$v;

  ListBuilder<NotificationCategoryPreferences>? _categories;
  ListBuilder<NotificationCategoryPreferences> get categories =>
      _$this._categories ??= ListBuilder<NotificationCategoryPreferences>();
  set categories(ListBuilder<NotificationCategoryPreferences>? categories) =>
      _$this._categories = categories;

  NotificationPreferencesResponseBuilder() {
    NotificationPreferencesResponse._defaults(this);
  }

  NotificationPreferencesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _categories = $v.categories.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationPreferencesResponse other) {
    _$v = other as _$NotificationPreferencesResponse;
  }

  @override
  void update(void Function(NotificationPreferencesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationPreferencesResponse build() => _build();

  _$NotificationPreferencesResponse _build() {
    _$NotificationPreferencesResponse _$result;
    try {
      _$result = _$v ??
          _$NotificationPreferencesResponse._(
            categories: categories.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'categories';
        categories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'NotificationPreferencesResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
