// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_quote_request_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseQuoteRequestSummaryResponse
    extends PageResponseQuoteRequestSummaryResponse {
  @override
  final BuiltList<QuoteRequestSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseQuoteRequestSummaryResponse(
          [void Function(PageResponseQuoteRequestSummaryResponseBuilder)?
              updates]) =>
      (PageResponseQuoteRequestSummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseQuoteRequestSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseQuoteRequestSummaryResponse rebuild(
          void Function(PageResponseQuoteRequestSummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseQuoteRequestSummaryResponseBuilder toBuilder() =>
      PageResponseQuoteRequestSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseQuoteRequestSummaryResponse &&
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
            r'PageResponseQuoteRequestSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseQuoteRequestSummaryResponseBuilder
    implements
        Builder<PageResponseQuoteRequestSummaryResponse,
            PageResponseQuoteRequestSummaryResponseBuilder> {
  _$PageResponseQuoteRequestSummaryResponse? _$v;

  ListBuilder<QuoteRequestSummaryResponse>? _content;
  ListBuilder<QuoteRequestSummaryResponse> get content =>
      _$this._content ??= ListBuilder<QuoteRequestSummaryResponse>();
  set content(ListBuilder<QuoteRequestSummaryResponse>? content) =>
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

  PageResponseQuoteRequestSummaryResponseBuilder() {
    PageResponseQuoteRequestSummaryResponse._defaults(this);
  }

  PageResponseQuoteRequestSummaryResponseBuilder get _$this {
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
  void replace(PageResponseQuoteRequestSummaryResponse other) {
    _$v = other as _$PageResponseQuoteRequestSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseQuoteRequestSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseQuoteRequestSummaryResponse build() => _build();

  _$PageResponseQuoteRequestSummaryResponse _build() {
    _$PageResponseQuoteRequestSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseQuoteRequestSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseQuoteRequestSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseQuoteRequestSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseQuoteRequestSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseQuoteRequestSummaryResponse',
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
