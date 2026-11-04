// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_broker_proposal_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseBrokerProposalSummaryResponse
    extends PageResponseBrokerProposalSummaryResponse {
  @override
  final BuiltList<BrokerProposalSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseBrokerProposalSummaryResponse(
          [void Function(PageResponseBrokerProposalSummaryResponseBuilder)?
              updates]) =>
      (PageResponseBrokerProposalSummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseBrokerProposalSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseBrokerProposalSummaryResponse rebuild(
          void Function(PageResponseBrokerProposalSummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseBrokerProposalSummaryResponseBuilder toBuilder() =>
      PageResponseBrokerProposalSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseBrokerProposalSummaryResponse &&
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
            r'PageResponseBrokerProposalSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseBrokerProposalSummaryResponseBuilder
    implements
        Builder<PageResponseBrokerProposalSummaryResponse,
            PageResponseBrokerProposalSummaryResponseBuilder> {
  _$PageResponseBrokerProposalSummaryResponse? _$v;

  ListBuilder<BrokerProposalSummaryResponse>? _content;
  ListBuilder<BrokerProposalSummaryResponse> get content =>
      _$this._content ??= ListBuilder<BrokerProposalSummaryResponse>();
  set content(ListBuilder<BrokerProposalSummaryResponse>? content) =>
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

  PageResponseBrokerProposalSummaryResponseBuilder() {
    PageResponseBrokerProposalSummaryResponse._defaults(this);
  }

  PageResponseBrokerProposalSummaryResponseBuilder get _$this {
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
  void replace(PageResponseBrokerProposalSummaryResponse other) {
    _$v = other as _$PageResponseBrokerProposalSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseBrokerProposalSummaryResponseBuilder)?
          updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseBrokerProposalSummaryResponse build() => _build();

  _$PageResponseBrokerProposalSummaryResponse _build() {
    _$PageResponseBrokerProposalSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseBrokerProposalSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseBrokerProposalSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseBrokerProposalSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseBrokerProposalSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseBrokerProposalSummaryResponse',
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
