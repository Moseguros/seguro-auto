// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_notification_template_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateNotificationTemplateRequest
    extends UpdateNotificationTemplateRequest {
  @override
  final bool? active;
  @override
  final String body;
  @override
  final String? subject;
  @override
  final int version;
  @override
  final BuiltList<String>? whatsappParams;
  @override
  final String? whatsappTemplateName;

  factory _$UpdateNotificationTemplateRequest(
          [void Function(UpdateNotificationTemplateRequestBuilder)? updates]) =>
      (UpdateNotificationTemplateRequestBuilder()..update(updates))._build();

  _$UpdateNotificationTemplateRequest._(
      {this.active,
      required this.body,
      this.subject,
      required this.version,
      this.whatsappParams,
      this.whatsappTemplateName})
      : super._();
  @override
  UpdateNotificationTemplateRequest rebuild(
          void Function(UpdateNotificationTemplateRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateNotificationTemplateRequestBuilder toBuilder() =>
      UpdateNotificationTemplateRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateNotificationTemplateRequest &&
        active == other.active &&
        body == other.body &&
        subject == other.subject &&
        version == other.version &&
        whatsappParams == other.whatsappParams &&
        whatsappTemplateName == other.whatsappTemplateName;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, active.hashCode);
    _$hash = $jc(_$hash, body.hashCode);
    _$hash = $jc(_$hash, subject.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jc(_$hash, whatsappParams.hashCode);
    _$hash = $jc(_$hash, whatsappTemplateName.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateNotificationTemplateRequest')
          ..add('active', active)
          ..add('body', body)
          ..add('subject', subject)
          ..add('version', version)
          ..add('whatsappParams', whatsappParams)
          ..add('whatsappTemplateName', whatsappTemplateName))
        .toString();
  }
}

class UpdateNotificationTemplateRequestBuilder
    implements
        Builder<UpdateNotificationTemplateRequest,
            UpdateNotificationTemplateRequestBuilder> {
  _$UpdateNotificationTemplateRequest? _$v;

  bool? _active;
  bool? get active => _$this._active;
  set active(bool? active) => _$this._active = active;

  String? _body;
  String? get body => _$this._body;
  set body(String? body) => _$this._body = body;

  String? _subject;
  String? get subject => _$this._subject;
  set subject(String? subject) => _$this._subject = subject;

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

  UpdateNotificationTemplateRequestBuilder() {
    UpdateNotificationTemplateRequest._defaults(this);
  }

  UpdateNotificationTemplateRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _active = $v.active;
      _body = $v.body;
      _subject = $v.subject;
      _version = $v.version;
      _whatsappParams = $v.whatsappParams?.toBuilder();
      _whatsappTemplateName = $v.whatsappTemplateName;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateNotificationTemplateRequest other) {
    _$v = other as _$UpdateNotificationTemplateRequest;
  }

  @override
  void update(
      void Function(UpdateNotificationTemplateRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateNotificationTemplateRequest build() => _build();

  _$UpdateNotificationTemplateRequest _build() {
    _$UpdateNotificationTemplateRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateNotificationTemplateRequest._(
            active: active,
            body: BuiltValueNullFieldError.checkNotNull(
                body, r'UpdateNotificationTemplateRequest', 'body'),
            subject: subject,
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateNotificationTemplateRequest', 'version'),
            whatsappParams: _whatsappParams?.build(),
            whatsappTemplateName: whatsappTemplateName,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'whatsappParams';
        _whatsappParams?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateNotificationTemplateRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
