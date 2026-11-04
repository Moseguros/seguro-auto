// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_insurer_inspection_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseInsurerInspectionSummaryResponse
    extends PageResponseInsurerInspectionSummaryResponse {
  @override
  final BuiltList<InsurerInspectionSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseInsurerInspectionSummaryResponse(
          [void Function(PageResponseInsurerInspectionSummaryResponseBuilder)?
              updates]) =>
      (PageResponseInsurerInspectionSummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseInsurerInspectionSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseInsurerInspectionSummaryResponse rebuild(
          void Function(PageResponseInsurerInspectionSummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseInsurerInspectionSummaryResponseBuilder toBuilder() =>
      PageResponseInsurerInspectionSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseInsurerInspectionSummaryResponse &&
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
            r'PageResponseInsurerInspectionSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseInsurerInspectionSummaryResponseBuilder
    implements
        Builder<PageResponseInsurerInspectionSummaryResponse,
            PageResponseInsurerInspectionSummaryResponseBuilder> {
  _$PageResponseInsurerInspectionSummaryResponse? _$v;

  ListBuilder<InsurerInspectionSummaryResponse>? _content;
  ListBuilder<InsurerInspectionSummaryResponse> get content =>
      _$this._content ??= ListBuilder<InsurerInspectionSummaryResponse>();
  set content(ListBuilder<InsurerInspectionSummaryResponse>? content) =>
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

  PageResponseInsurerInspectionSummaryResponseBuilder() {
    PageResponseInsurerInspectionSummaryResponse._defaults(this);
  }

  PageResponseInsurerInspectionSummaryResponseBuilder get _$this {
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
  void replace(PageResponseInsurerInspectionSummaryResponse other) {
    _$v = other as _$PageResponseInsurerInspectionSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseInsurerInspectionSummaryResponseBuilder)?
          updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseInsurerInspectionSummaryResponse build() => _build();

  _$PageResponseInsurerInspectionSummaryResponse _build() {
    _$PageResponseInsurerInspectionSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseInsurerInspectionSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseInsurerInspectionSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseInsurerInspectionSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(
                totalElements,
                r'PageResponseInsurerInspectionSummaryResponse',
                'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseInsurerInspectionSummaryResponse',
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
