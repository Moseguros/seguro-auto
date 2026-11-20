// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_template_summary.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationTemplateSummary extends NotificationTemplateSummary {
  @override
  final bool active;
  @override
  final NotificationChannel channel;
  @override
  final String code;
  @override
  final bool customized;
  @override
  final String id;
  @override
  final String locale;
  @override
  final String? subject;
  @override
  final DateTime updatedAt;
  @override
  final String updatedBy;

  factory _$NotificationTemplateSummary(
          [void Function(NotificationTemplateSummaryBuilder)? updates]) =>
      (NotificationTemplateSummaryBuilder()..update(updates))._build();

  _$NotificationTemplateSummary._(
      {required this.active,
      required this.channel,
      required this.code,
      required this.customized,
      required this.id,
      required this.locale,
      this.subject,
      required this.updatedAt,
      required this.updatedBy})
      : super._();
  @override
  NotificationTemplateSummary rebuild(
          void Function(NotificationTemplateSummaryBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationTemplateSummaryBuilder toBuilder() =>
      NotificationTemplateSummaryBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationTemplateSummary &&
        active == other.active &&
        channel == other.channel &&
        code == other.code &&
        customized == other.customized &&
        id == other.id &&
        locale == other.locale &&
        subject == other.subject &&
        updatedAt == other.updatedAt &&
        updatedBy == other.updatedBy;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, customized.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, locale.hashCode);
    _$hash = $jc(_$hash, subject.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, updatedBy.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationTemplateSummary')
          ..add('active', active)
          ..add('channel', channel)
          ..add('code', code)
          ..add('customized', customized)
          ..add('id', id)
          ..add('locale', locale)
          ..add('subject', subject)
          ..add('updatedAt', updatedAt)
          ..add('updatedBy', updatedBy))
        .toString();
  }
}

class NotificationTemplateSummaryBuilder
    implements
        Builder<NotificationTemplateSummary,
            NotificationTemplateSummaryBuilder> {
  _$NotificationTemplateSummary? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  NotificationChannel? _channel;
  NotificationChannel? get channel => _$this._channel;
  set channel(NotificationChannel? channel) => _$this._channel = channel;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  bool? _customized;
  bool? get customized => _$this._customized;
  set customized(bool? customized) => _$this._customized = customized;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _locale;
  String? get locale => _$this._locale;
  set locale(String? locale) => _$this._locale = locale;

  String? _subject;
  String? get subject => _$this._subject;
  set subject(String? subject) => _$this._subject = subject;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  String? _updatedBy;
  String? get updatedBy => _$this._updatedBy;
  set updatedBy(String? updatedBy) => _$this._updatedBy = updatedBy;

  NotificationTemplateSummaryBuilder() {
    NotificationTemplateSummary._defaults(this);
  }

  NotificationTemplateSummaryBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _channel = $v.channel;
      _code = $v.code;
      _customized = $v.customized;
      _id = $v.id;
      _locale = $v.locale;
      _subject = $v.subject;
      _updatedAt = $v.updatedAt;
      _updatedBy = $v.updatedBy;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationTemplateSummary other) {
    _$v = other as _$NotificationTemplateSummary;
  }

  @override
  void update(void Function(NotificationTemplateSummaryBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationTemplateSummary build() => _build();

  _$NotificationTemplateSummary _build() {
    final _$result = _$v ??
        _$NotificationTemplateSummary._(
          active: BuiltValueNullFieldError.checkNotNull(
              active, r'NotificationTemplateSummary', 'active'),
          channel: BuiltValueNullFieldError.checkNotNull(
              channel, r'NotificationTemplateSummary', 'channel'),
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'NotificationTemplateSummary', 'code'),
          customized: BuiltValueNullFieldError.checkNotNull(
              customized, r'NotificationTemplateSummary', 'customized'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'NotificationTemplateSummary', 'id'),
          locale: BuiltValueNullFieldError.checkNotNull(
              locale, r'NotificationTemplateSummary', 'locale'),
          subject: subject,
          updatedAt: BuiltValueNullFieldError.checkNotNull(
              updatedAt, r'NotificationTemplateSummary', 'updatedAt'),
          updatedBy: BuiltValueNullFieldError.checkNotNull(
              updatedBy, r'NotificationTemplateSummary', 'updatedBy'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
