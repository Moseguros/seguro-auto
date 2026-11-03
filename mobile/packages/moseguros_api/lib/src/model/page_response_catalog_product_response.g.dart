// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_catalog_product_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseCatalogProductResponse
    extends PageResponseCatalogProductResponse {
  @override
  final BuiltList<CatalogProductResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseCatalogProductResponse(
          [void Function(PageResponseCatalogProductResponseBuilder)?
              updates]) =>
      (PageResponseCatalogProductResponseBuilder()..update(updates))._build();

  _$PageResponseCatalogProductResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseCatalogProductResponse rebuild(
          void Function(PageResponseCatalogProductResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseCatalogProductResponseBuilder toBuilder() =>
      PageResponseCatalogProductResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseCatalogProductResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseCatalogProductResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseCatalogProductResponseBuilder
    implements
        Builder<PageResponseCatalogProductResponse,
            PageResponseCatalogProductResponseBuilder> {
  _$PageResponseCatalogProductResponse? _$v;

  ListBuilder<CatalogProductResponse>? _content;
  ListBuilder<CatalogProductResponse> get content =>
      _$this._content ??= ListBuilder<CatalogProductResponse>();
  set content(ListBuilder<CatalogProductResponse>? content) =>
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

  PageResponseCatalogProductResponseBuilder() {
    PageResponseCatalogProductResponse._defaults(this);
  }

  PageResponseCatalogProductResponseBuilder get _$this {
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
  void replace(PageResponseCatalogProductResponse other) {
    _$v = other as _$PageResponseCatalogProductResponse;
  }

  @override
  void update(
      void Function(PageResponseCatalogProductResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseCatalogProductResponse build() => _build();

  _$PageResponseCatalogProductResponse _build() {
    _$PageResponseCatalogProductResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseCatalogProductResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseCatalogProductResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseCatalogProductResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseCatalogProductResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseCatalogProductResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
