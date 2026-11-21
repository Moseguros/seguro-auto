// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_notification_preferences_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateNotificationPreferencesRequest
    extends UpdateNotificationPreferencesRequest {
  @override
  final BuiltList<NotificationCategoryPreferencesChange> categories;

  factory _$UpdateNotificationPreferencesRequest(
          [void Function(UpdateNotificationPreferencesRequestBuilder)?
              updates]) =>
      (UpdateNotificationPreferencesRequestBuilder()..update(updates))._build();

  _$UpdateNotificationPreferencesRequest._({required this.categories})
      : super._();
  @override
  UpdateNotificationPreferencesRequest rebuild(
          void Function(UpdateNotificationPreferencesRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateNotificationPreferencesRequestBuilder toBuilder() =>
      UpdateNotificationPreferencesRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateNotificationPreferencesRequest &&
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
    return (newBuiltValueToStringHelper(r'UpdateNotificationPreferencesRequest')
          ..add('categories', categories))
        .toString();
  }
}

class UpdateNotificationPreferencesRequestBuilder
    implements
        Builder<UpdateNotificationPreferencesRequest,
            UpdateNotificationPreferencesRequestBuilder> {
  _$UpdateNotificationPreferencesRequest? _$v;

  ListBuilder<NotificationCategoryPreferencesChange>? _categories;
  ListBuilder<NotificationCategoryPreferencesChange> get categories =>
      _$this._categories ??=
          ListBuilder<NotificationCategoryPreferencesChange>();
  set categories(
          ListBuilder<NotificationCategoryPreferencesChange>? categories) =>
      _$this._categories = categories;

  UpdateNotificationPreferencesRequestBuilder() {
    UpdateNotificationPreferencesRequest._defaults(this);
  }

  UpdateNotificationPreferencesRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _categories = $v.categories.toBuilder();
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateNotificationPreferencesRequest other) {
    _$v = other as _$UpdateNotificationPreferencesRequest;
  }

  @override
  void update(
      void Function(UpdateNotificationPreferencesRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateNotificationPreferencesRequest build() => _build();

  _$UpdateNotificationPreferencesRequest _build() {
    _$UpdateNotificationPreferencesRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateNotificationPreferencesRequest._(
            categories: categories.build(),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'categories';
        categories.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateNotificationPreferencesRequest',
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
