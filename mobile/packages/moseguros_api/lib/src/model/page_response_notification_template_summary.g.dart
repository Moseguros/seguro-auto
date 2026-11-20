// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_notification_template_summary.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseNotificationTemplateSummary
    extends PageResponseNotificationTemplateSummary {
  @override
  final BuiltList<NotificationTemplateSummary> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseNotificationTemplateSummary(
          [void Function(PageResponseNotificationTemplateSummaryBuilder)?
              updates]) =>
      (PageResponseNotificationTemplateSummaryBuilder()..update(updates))
          ._build();

  _$PageResponseNotificationTemplateSummary._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseNotificationTemplateSummary rebuild(
          void Function(PageResponseNotificationTemplateSummaryBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseNotificationTemplateSummaryBuilder toBuilder() =>
      PageResponseNotificationTemplateSummaryBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseNotificationTemplateSummary &&
        content == other.content &&
        page == other.page &&
        size == other.size &&
        totalElements == other.totalElements;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, content.hashCode);
    _$hash = $jc(_$hash, page.hashCode);
    _$hash = $jc(_$hash, size.hashCode);
    _$hash = $jc(_$hash, totalElements.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(
            r'PageResponseNotificationTemplateSummary')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseNotificationTemplateSummaryBuilder
    implements
        Builder<PageResponseNotificationTemplateSummary,
            PageResponseNotificationTemplateSummaryBuilder> {
  _$PageResponseNotificationTemplateSummary? _$v;

  ListBuilder<NotificationTemplateSummary>? _content;
  ListBuilder<NotificationTemplateSummary> get content =>
      _$this._content ??= ListBuilder<NotificationTemplateSummary>();
  set content(ListBuilder<NotificationTemplateSummary>? content) =>
      _$this._content = content;

  int? _page;
  int? get page => _$this._page;
  set page(int? page) => _$this._page = page;

  int? _size;
  int? get size => _$this._size;
  set size(int? size) => _$this._size = size;

  int? _totalElements;
  int? get totalElements => _$this._totalElements;
  set totalElements(int? totalElements) =>
      _$this._totalElements = totalElements;

  PageResponseNotificationTemplateSummaryBuilder() {
    PageResponseNotificationTemplateSummary._defaults(this);
  }

  PageResponseNotificationTemplateSummaryBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _content = $v.content.toBuilder();
      _page = $v.page;
      _size = $v.size;
      _totalElements = $v.totalElements;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PageResponseNotificationTemplateSummary other) {
    _$v = other as _$PageResponseNotificationTemplateSummary;
  }

  @override
  void update(
      void Function(PageResponseNotificationTemplateSummaryBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseNotificationTemplateSummary build() => _build();

  _$PageResponseNotificationTemplateSummary _build() {
    _$PageResponseNotificationTemplateSummary _$result;
    try {
      _$result = _$v ??
          _$PageResponseNotificationTemplateSummary._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseNotificationTemplateSummary', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseNotificationTemplateSummary', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseNotificationTemplateSummary', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseNotificationTemplateSummary',
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
