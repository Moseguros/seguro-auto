// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_my_notification_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseMyNotificationResponse
    extends PageResponseMyNotificationResponse {
  @override
  final BuiltList<MyNotificationResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseMyNotificationResponse(
          [void Function(PageResponseMyNotificationResponseBuilder)?
              updates]) =>
      (PageResponseMyNotificationResponseBuilder()..update(updates))._build();

  _$PageResponseMyNotificationResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseMyNotificationResponse rebuild(
          void Function(PageResponseMyNotificationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseMyNotificationResponseBuilder toBuilder() =>
      PageResponseMyNotificationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseMyNotificationResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseMyNotificationResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseMyNotificationResponseBuilder
    implements
        Builder<PageResponseMyNotificationResponse,
            PageResponseMyNotificationResponseBuilder> {
  _$PageResponseMyNotificationResponse? _$v;

  ListBuilder<MyNotificationResponse>? _content;
  ListBuilder<MyNotificationResponse> get content =>
      _$this._content ??= ListBuilder<MyNotificationResponse>();
  set content(ListBuilder<MyNotificationResponse>? content) =>
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

  PageResponseMyNotificationResponseBuilder() {
    PageResponseMyNotificationResponse._defaults(this);
  }

  PageResponseMyNotificationResponseBuilder get _$this {
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
  void replace(PageResponseMyNotificationResponse other) {
    _$v = other as _$PageResponseMyNotificationResponse;
  }

  @override
  void update(
      void Function(PageResponseMyNotificationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseMyNotificationResponse build() => _build();

  _$PageResponseMyNotificationResponse _build() {
    _$PageResponseMyNotificationResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseMyNotificationResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseMyNotificationResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseMyNotificationResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseMyNotificationResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseMyNotificationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
