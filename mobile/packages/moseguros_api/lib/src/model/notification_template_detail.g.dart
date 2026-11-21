// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_template_detail.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationTemplateDetail extends NotificationTemplateDetail {
  @override
  final bool active;
  @override
  final String body;
  @override
  final NotificationChannel channel;
  @override
  final String code;
  @override
  final bool customized;
  @override
  final String? defaultBody;
  @override
  final String? defaultSubject;
  @override
  final String id;
  @override
  final String locale;
  @override
  final BuiltMap<String, String> sampleParams;
  @override
  final String? subject;
  @override
  final DateTime updatedAt;
  @override
  final String updatedBy;
  @override
  final BuiltList<String> variables;
  @override
  final int version;
  @override
  final BuiltList<String>? whatsappParams;
  @override
  final String? whatsappTemplateName;

  factory _$NotificationTemplateDetail(
          [void Function(NotificationTemplateDetailBuilder)? updates]) =>
      (NotificationTemplateDetailBuilder()..update(updates))._build();

  _$NotificationTemplateDetail._(
      {required this.active,
      required this.body,
      required this.channel,
      required this.code,
      required this.customized,
      this.defaultBody,
      this.defaultSubject,
      required this.id,
      required this.locale,
      required this.sampleParams,
      this.subject,
      required this.updatedAt,
      required this.updatedBy,
      required this.variables,
      required this.version,
      this.whatsappParams,
      this.whatsappTemplateName})
      : super._();
  @override
  NotificationTemplateDetail rebuild(
          void Function(NotificationTemplateDetailBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationTemplateDetailBuilder toBuilder() =>
      NotificationTemplateDetailBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationTemplateDetail &&
        active == other.active &&
        body == other.body &&
        channel == other.channel &&
        code == other.code &&
        customized == other.customized &&
        defaultBody == other.defaultBody &&
        defaultSubject == other.defaultSubject &&
        id == other.id &&
        locale == other.locale &&
        sampleParams == other.sampleParams &&
        subject == other.subject &&
        updatedAt == other.updatedAt &&
        updatedBy == other.updatedBy &&
        variables == other.variables &&
        version == other.version &&
        whatsappParams == other.whatsappParams &&
        whatsappTemplateName == other.whatsappTemplateName;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, body.hashCode);
    _$hash = $jc(_$hash, channel.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jc(_$hash, customized.hashCode);
    _$hash = $jc(_$hash, defaultBody.hashCode);
    _$hash = $jc(_$hash, defaultSubject.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jc(_$hash, locale.hashCode);
    _$hash = $jc(_$hash, sampleParams.hashCode);
    _$hash = $jc(_$hash, subject.hashCode);
    _$hash = $jc(_$hash, updatedAt.hashCode);
    _$hash = $jc(_$hash, updatedBy.hashCode);
    _$hash = $jc(_$hash, variables.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jc(_$hash, whatsappParams.hashCode);
    _$hash = $jc(_$hash, whatsappTemplateName.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationTemplateDetail')
          ..add('active', active)
          ..add('body', body)
          ..add('channel', channel)
          ..add('code', code)
          ..add('customized', customized)
          ..add('defaultBody', defaultBody)
          ..add('defaultSubject', defaultSubject)
          ..add('id', id)
          ..add('locale', locale)
          ..add('sampleParams', sampleParams)
          ..add('subject', subject)
          ..add('updatedAt', updatedAt)
          ..add('updatedBy', updatedBy)
          ..add('variables', variables)
          ..add('version', version)
          ..add('whatsappParams', whatsappParams)
          ..add('whatsappTemplateName', whatsappTemplateName))
        .toString();
  }
}

class NotificationTemplateDetailBuilder
    implements
        Builder<NotificationTemplateDetail, NotificationTemplateDetailBuilder> {
  _$NotificationTemplateDetail? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  String? _body;
  String? get body => _$this._body;
  set body(String? body) => _$this._body = body;

  NotificationChannel? _channel;
  NotificationChannel? get channel => _$this._channel;
  set channel(NotificationChannel? channel) => _$this._channel = channel;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  bool? _customized;
  bool? get customized => _$this._customized;
  set customized(bool? customized) => _$this._customized = customized;

  String? _defaultBody;
  String? get defaultBody => _$this._defaultBody;
  set defaultBody(String? defaultBody) => _$this._defaultBody = defaultBody;

  String? _defaultSubject;
  String? get defaultSubject => _$this._defaultSubject;
  set defaultSubject(String? defaultSubject) =>
      _$this._defaultSubject = defaultSubject;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  String? _locale;
  String? get locale => _$this._locale;
  set locale(String? locale) => _$this._locale = locale;

  MapBuilder<String, String>? _sampleParams;
  MapBuilder<String, String> get sampleParams =>
      _$this._sampleParams ??= MapBuilder<String, String>();
  set sampleParams(MapBuilder<String, String>? sampleParams) =>
      _$this._sampleParams = sampleParams;

  String? _subject;
  String? get subject => _$this._subject;
  set subject(String? subject) => _$this._subject = subject;

  DateTime? _updatedAt;
  DateTime? get updatedAt => _$this._updatedAt;
  set updatedAt(DateTime? updatedAt) => _$this._updatedAt = updatedAt;

  String? _updatedBy;
  String? get updatedBy => _$this._updatedBy;
  set updatedBy(String? updatedBy) => _$this._updatedBy = updatedBy;

  ListBuilder<String>? _variables;
  ListBuilder<String> get variables =>
      _$this._variables ??= ListBuilder<String>();
  set variables(ListBuilder<String>? variables) =>
      _$this._variables = variables;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  ListBuilder<String>? _whatsappParams;
  ListBuilder<String> get whatsappParams =>
      _$this._whatsappParams ??= ListBuilder<String>();
  set whatsappParams(ListBuilder<String>? whatsappParams) =>
      _$this._whatsappParams = whatsappParams;

  String? _whatsappTemplateName;
  String? get whatsappTemplateName => _$this._whatsappTemplateName;
  set whatsappTemplateName(String? whatsappTemplateName) =>
      _$this._whatsappTemplateName = whatsappTemplateName;

  NotificationTemplateDetailBuilder() {
    NotificationTemplateDetail._defaults(this);
  }

  NotificationTemplateDetailBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _body = $v.body;
      _channel = $v.channel;
      _code = $v.code;
      _customized = $v.customized;
      _defaultBody = $v.defaultBody;
      _defaultSubject = $v.defaultSubject;
      _id = $v.id;
      _locale = $v.locale;
      _sampleParams = $v.sampleParams.toBuilder();
      _subject = $v.subject;
      _updatedAt = $v.updatedAt;
      _updatedBy = $v.updatedBy;
      _variables = $v.variables.toBuilder();
      _version = $v.version;
      _whatsappParams = $v.whatsappParams?.toBuilder();
      _whatsappTemplateName = $v.whatsappTemplateName;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationTemplateDetail other) {
    _$v = other as _$NotificationTemplateDetail;
  }

  @override
  void update(void Function(NotificationTemplateDetailBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationTemplateDetail build() => _build();

  _$NotificationTemplateDetail _build() {
    _$NotificationTemplateDetail _$result;
    try {
      _$result = _$v ??
          _$NotificationTemplateDetail._(
            active: BuiltValueNullFieldError.checkNotNull(
                active, r'NotificationTemplateDetail', 'active'),
            body: BuiltValueNullFieldError.checkNotNull(
                body, r'NotificationTemplateDetail', 'body'),
            channel: BuiltValueNullFieldError.checkNotNull(
                channel, r'NotificationTemplateDetail', 'channel'),
            code: BuiltValueNullFieldError.checkNotNull(
                code, r'NotificationTemplateDetail', 'code'),
            customized: BuiltValueNullFieldError.checkNotNull(
                customized, r'NotificationTemplateDetail', 'customized'),
            defaultBody: defaultBody,
            defaultSubject: defaultSubject,
            id: BuiltValueNullFieldError.checkNotNull(
                id, r'NotificationTemplateDetail', 'id'),
            locale: BuiltValueNullFieldError.checkNotNull(
                locale, r'NotificationTemplateDetail', 'locale'),
            sampleParams: sampleParams.build(),
            subject: subject,
            updatedAt: BuiltValueNullFieldError.checkNotNull(
                updatedAt, r'NotificationTemplateDetail', 'updatedAt'),
            updatedBy: BuiltValueNullFieldError.checkNotNull(
                updatedBy, r'NotificationTemplateDetail', 'updatedBy'),
            variables: variables.build(),
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'NotificationTemplateDetail', 'version'),
            whatsappParams: _whatsappParams?.build(),
            whatsappTemplateName: whatsappTemplateName,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'sampleParams';
        sampleParams.build();

        _$failedField = 'variables';
        variables.build();

        _$failedField = 'whatsappParams';
        _whatsappParams?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'NotificationTemplateDetail', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
