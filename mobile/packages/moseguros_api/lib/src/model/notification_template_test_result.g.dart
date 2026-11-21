// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_template_test_result.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationTemplateTestResult extends NotificationTemplateTestResult {
  @override
  final String? reason;
  @override
  final String status;

  factory _$NotificationTemplateTestResult(
          [void Function(NotificationTemplateTestResultBuilder)? updates]) =>
      (NotificationTemplateTestResultBuilder()..update(updates))._build();

  _$NotificationTemplateTestResult._({this.reason, required this.status})
      : super._();
  @override
  NotificationTemplateTestResult rebuild(
          void Function(NotificationTemplateTestResultBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationTemplateTestResultBuilder toBuilder() =>
      NotificationTemplateTestResultBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationTemplateTestResult &&
        reason == other.reason &&
        status == other.status;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationTemplateTestResult')
          ..add('reason', reason)
          ..add('status', status))
        .toString();
  }
}

class NotificationTemplateTestResultBuilder
    implements
        Builder<NotificationTemplateTestResult,
            NotificationTemplateTestResultBuilder> {
  _$NotificationTemplateTestResult? _$v;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  String? _status;
  String? get status => _$this._status;
  set status(String? status) => _$this._status = status;

  NotificationTemplateTestResultBuilder() {
    NotificationTemplateTestResult._defaults(this);
  }

  NotificationTemplateTestResultBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _reason = $v.reason;
      _status = $v.status;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationTemplateTestResult other) {
    _$v = other as _$NotificationTemplateTestResult;
  }

  @override
  void update(void Function(NotificationTemplateTestResultBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationTemplateTestResult build() => _build();

  _$NotificationTemplateTestResult _build() {
    final _$result = _$v ??
        _$NotificationTemplateTestResult._(
          reason: reason,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'NotificationTemplateTestResult', 'status'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
