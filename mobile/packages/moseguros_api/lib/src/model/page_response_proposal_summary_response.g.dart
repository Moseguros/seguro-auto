// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseProposalSummaryResponse
    extends PageResponseProposalSummaryResponse {
  @override
  final BuiltList<ProposalSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseProposalSummaryResponse(
          [void Function(PageResponseProposalSummaryResponseBuilder)?
              updates]) =>
      (PageResponseProposalSummaryResponseBuilder()..update(updates))._build();

  _$PageResponseProposalSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseProposalSummaryResponse rebuild(
          void Function(PageResponseProposalSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseProposalSummaryResponseBuilder toBuilder() =>
      PageResponseProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseProposalSummaryResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseProposalSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseProposalSummaryResponseBuilder
    implements
        Builder<PageResponseProposalSummaryResponse,
            PageResponseProposalSummaryResponseBuilder> {
  _$PageResponseProposalSummaryResponse? _$v;

  ListBuilder<ProposalSummaryResponse>? _content;
  ListBuilder<ProposalSummaryResponse> get content =>
      _$this._content ??= ListBuilder<ProposalSummaryResponse>();
  set content(ListBuilder<ProposalSummaryResponse>? content) =>
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

  PageResponseProposalSummaryResponseBuilder() {
    PageResponseProposalSummaryResponse._defaults(this);
  }

  PageResponseProposalSummaryResponseBuilder get _$this {
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
  void replace(PageResponseProposalSummaryResponse other) {
    _$v = other as _$PageResponseProposalSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseProposalSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseProposalSummaryResponse build() => _build();

  _$PageResponseProposalSummaryResponse _build() {
    _$PageResponseProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseProposalSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseProposalSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseProposalSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseProposalSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(r'PageResponseProposalSummaryResponse',
            _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
