// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_template_preview.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$NotificationTemplatePreview extends NotificationTemplatePreview {
  @override
  final String? body;
  @override
  final bool? fits;
  @override
  final int? length;
  @override
  final int? limit;
  @override
  final String? smsText;
  @override
  final String? subject;

  factory _$NotificationTemplatePreview(
          [void Function(NotificationTemplatePreviewBuilder)? updates]) =>
      (NotificationTemplatePreviewBuilder()..update(updates))._build();

  _$NotificationTemplatePreview._(
      {this.body,
      this.fits,
      this.length,
      this.limit,
      this.smsText,
      this.subject})
      : super._();
  @override
  NotificationTemplatePreview rebuild(
          void Function(NotificationTemplatePreviewBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  NotificationTemplatePreviewBuilder toBuilder() =>
      NotificationTemplatePreviewBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is NotificationTemplatePreview &&
        body == other.body &&
        fits == other.fits &&
        length == other.length &&
        limit == other.limit &&
        smsText == other.smsText &&
        subject == other.subject;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, body.hashCode);
    _$hash = $jc(_$hash, fits.hashCode);
    _$hash = $jc(_$hash, length.hashCode);
    _$hash = $jc(_$hash, limit.hashCode);
    _$hash = $jc(_$hash, smsText.hashCode);
    _$hash = $jc(_$hash, subject.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'NotificationTemplatePreview')
          ..add('body', body)
          ..add('fits', fits)
          ..add('length', length)
          ..add('limit', limit)
          ..add('smsText', smsText)
          ..add('subject', subject))
        .toString();
  }
}

class NotificationTemplatePreviewBuilder
    implements
        Builder<NotificationTemplatePreview,
            NotificationTemplatePreviewBuilder> {
  _$NotificationTemplatePreview? _$v;

  String? _body;
  String? get body => _$this._body;
  set body(String? body) => _$this._body = body;

  bool? _fits;
  bool? get fits => _$this._fits;
  set fits(bool? fits) => _$this._fits = fits;

  int? _length;
  int? get length => _$this._length;
  set length(int? length) => _$this._length = length;

  int? _limit;
  int? get limit => _$this._limit;
  set limit(int? limit) => _$this._limit = limit;

  String? _smsText;
  String? get smsText => _$this._smsText;
  set smsText(String? smsText) => _$this._smsText = smsText;

  String? _subject;
  String? get subject => _$this._subject;
  set subject(String? subject) => _$this._subject = subject;

  NotificationTemplatePreviewBuilder() {
    NotificationTemplatePreview._defaults(this);
  }

  NotificationTemplatePreviewBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _body = $v.body;
      _fits = $v.fits;
      _length = $v.length;
      _limit = $v.limit;
      _smsText = $v.smsText;
      _subject = $v.subject;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(NotificationTemplatePreview other) {
    _$v = other as _$NotificationTemplatePreview;
  }

  @override
  void update(void Function(NotificationTemplatePreviewBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  NotificationTemplatePreview build() => _build();

  _$NotificationTemplatePreview _build() {
    final _$result = _$v ??
        _$NotificationTemplatePreview._(
          body: body,
          fits: fits,
          length: length,
          limit: limit,
          smsText: smsText,
          subject: subject,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
