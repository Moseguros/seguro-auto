// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_organization_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseOrganizationSummaryResponse
    extends PageResponseOrganizationSummaryResponse {
  @override
  final BuiltList<OrganizationSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseOrganizationSummaryResponse(
          [void Function(PageResponseOrganizationSummaryResponseBuilder)?
              updates]) =>
      (PageResponseOrganizationSummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseOrganizationSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseOrganizationSummaryResponse rebuild(
          void Function(PageResponseOrganizationSummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseOrganizationSummaryResponseBuilder toBuilder() =>
      PageResponseOrganizationSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseOrganizationSummaryResponse &&
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
            r'PageResponseOrganizationSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseOrganizationSummaryResponseBuilder
    implements
        Builder<PageResponseOrganizationSummaryResponse,
            PageResponseOrganizationSummaryResponseBuilder> {
  _$PageResponseOrganizationSummaryResponse? _$v;

  ListBuilder<OrganizationSummaryResponse>? _content;
  ListBuilder<OrganizationSummaryResponse> get content =>
      _$this._content ??= ListBuilder<OrganizationSummaryResponse>();
  set content(ListBuilder<OrganizationSummaryResponse>? content) =>
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

  PageResponseOrganizationSummaryResponseBuilder() {
    PageResponseOrganizationSummaryResponse._defaults(this);
  }

  PageResponseOrganizationSummaryResponseBuilder get _$this {
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
  void replace(PageResponseOrganizationSummaryResponse other) {
    _$v = other as _$PageResponseOrganizationSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseOrganizationSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseOrganizationSummaryResponse build() => _build();

  _$PageResponseOrganizationSummaryResponse _build() {
    _$PageResponseOrganizationSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseOrganizationSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseOrganizationSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseOrganizationSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseOrganizationSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseOrganizationSummaryResponse',
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
