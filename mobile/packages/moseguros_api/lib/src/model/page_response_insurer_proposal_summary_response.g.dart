// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_insurer_proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseInsurerProposalSummaryResponse
    extends PageResponseInsurerProposalSummaryResponse {
  @override
  final BuiltList<InsurerProposalSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseInsurerProposalSummaryResponse(
          [void Function(PageResponseInsurerProposalSummaryResponseBuilder)?
              updates]) =>
      (PageResponseInsurerProposalSummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseInsurerProposalSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseInsurerProposalSummaryResponse rebuild(
          void Function(PageResponseInsurerProposalSummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseInsurerProposalSummaryResponseBuilder toBuilder() =>
      PageResponseInsurerProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseInsurerProposalSummaryResponse &&
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
            r'PageResponseInsurerProposalSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseInsurerProposalSummaryResponseBuilder
    implements
        Builder<PageResponseInsurerProposalSummaryResponse,
            PageResponseInsurerProposalSummaryResponseBuilder> {
  _$PageResponseInsurerProposalSummaryResponse? _$v;

  ListBuilder<InsurerProposalSummaryResponse>? _content;
  ListBuilder<InsurerProposalSummaryResponse> get content =>
      _$this._content ??= ListBuilder<InsurerProposalSummaryResponse>();
  set content(ListBuilder<InsurerProposalSummaryResponse>? content) =>
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

  PageResponseInsurerProposalSummaryResponseBuilder() {
    PageResponseInsurerProposalSummaryResponse._defaults(this);
  }

  PageResponseInsurerProposalSummaryResponseBuilder get _$this {
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
  void replace(PageResponseInsurerProposalSummaryResponse other) {
    _$v = other as _$PageResponseInsurerProposalSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseInsurerProposalSummaryResponseBuilder)?
          updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseInsurerProposalSummaryResponse build() => _build();

  _$PageResponseInsurerProposalSummaryResponse _build() {
    _$PageResponseInsurerProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseInsurerProposalSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseInsurerProposalSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseInsurerProposalSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseInsurerProposalSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseInsurerProposalSummaryResponse',
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
