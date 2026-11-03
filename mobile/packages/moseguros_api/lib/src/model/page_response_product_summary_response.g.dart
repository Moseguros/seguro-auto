// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_product_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseProductSummaryResponse
    extends PageResponseProductSummaryResponse {
  @override
  final BuiltList<ProductSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseProductSummaryResponse(
          [void Function(PageResponseProductSummaryResponseBuilder)?
              updates]) =>
      (PageResponseProductSummaryResponseBuilder()..update(updates))._build();

  _$PageResponseProductSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseProductSummaryResponse rebuild(
          void Function(PageResponseProductSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseProductSummaryResponseBuilder toBuilder() =>
      PageResponseProductSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseProductSummaryResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseProductSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseProductSummaryResponseBuilder
    implements
        Builder<PageResponseProductSummaryResponse,
            PageResponseProductSummaryResponseBuilder> {
  _$PageResponseProductSummaryResponse? _$v;

  ListBuilder<ProductSummaryResponse>? _content;
  ListBuilder<ProductSummaryResponse> get content =>
      _$this._content ??= ListBuilder<ProductSummaryResponse>();
  set content(ListBuilder<ProductSummaryResponse>? content) =>
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

  PageResponseProductSummaryResponseBuilder() {
    PageResponseProductSummaryResponse._defaults(this);
  }

  PageResponseProductSummaryResponseBuilder get _$this {
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
  void replace(PageResponseProductSummaryResponse other) {
    _$v = other as _$PageResponseProductSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseProductSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseProductSummaryResponse build() => _build();

  _$PageResponseProductSummaryResponse _build() {
    _$PageResponseProductSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseProductSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseProductSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseProductSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseProductSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseProductSummaryResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
