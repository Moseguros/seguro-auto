// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'preview_notification_template_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PreviewNotificationTemplateRequest
    extends PreviewNotificationTemplateRequest {
  @override
  final String body;
  @override
  final BuiltMap<String, String>? params;
  @override
  final String? subject;

  factory _$PreviewNotificationTemplateRequest(
          [void Function(PreviewNotificationTemplateRequestBuilder)?
              updates]) =>
      (PreviewNotificationTemplateRequestBuilder()..update(updates))._build();

  _$PreviewNotificationTemplateRequest._(
      {required this.body, this.params, this.subject})
      : super._();
  @override
  PreviewNotificationTemplateRequest rebuild(
          void Function(PreviewNotificationTemplateRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PreviewNotificationTemplateRequestBuilder toBuilder() =>
      PreviewNotificationTemplateRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PreviewNotificationTemplateRequest &&
        body == other.body &&
        params == other.params &&
        subject == other.subject;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, body.hashCode);
    _$hash = $jc(_$hash, params.hashCode);
    _$hash = $jc(_$hash, subject.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PreviewNotificationTemplateRequest')
          ..add('body', body)
          ..add('params', params)
          ..add('subject', subject))
        .toString();
  }
}

class PreviewNotificationTemplateRequestBuilder
    implements
        Builder<PreviewNotificationTemplateRequest,
            PreviewNotificationTemplateRequestBuilder> {
  _$PreviewNotificationTemplateRequest? _$v;

  String? _body;
  String? get body => _$this._body;
  set body(String? body) => _$this._body = body;

  MapBuilder<String, String>? _params;
  MapBuilder<String, String> get params =>
      _$this._params ??= MapBuilder<String, String>();
  set params(MapBuilder<String, String>? params) => _$this._params = params;

  String? _subject;
  String? get subject => _$this._subject;
  set subject(String? subject) => _$this._subject = subject;

  PreviewNotificationTemplateRequestBuilder() {
    PreviewNotificationTemplateRequest._defaults(this);
  }

  PreviewNotificationTemplateRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _body = $v.body;
      _params = $v.params?.toBuilder();
      _subject = $v.subject;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PreviewNotificationTemplateRequest other) {
    _$v = other as _$PreviewNotificationTemplateRequest;
  }

  @override
  void update(
      void Function(PreviewNotificationTemplateRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PreviewNotificationTemplateRequest build() => _build();

  _$PreviewNotificationTemplateRequest _build() {
    _$PreviewNotificationTemplateRequest _$result;
    try {
      _$result = _$v ??
          _$PreviewNotificationTemplateRequest._(
            body: BuiltValueNullFieldError.checkNotNull(
                body, r'PreviewNotificationTemplateRequest', 'body'),
            params: _params?.build(),
            subject: subject,
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'params';
        _params?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PreviewNotificationTemplateRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
