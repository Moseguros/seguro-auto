// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_insurer_policy_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseInsurerPolicySummaryResponse
    extends PageResponseInsurerPolicySummaryResponse {
  @override
  final BuiltList<InsurerPolicySummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseInsurerPolicySummaryResponse(
          [void Function(PageResponseInsurerPolicySummaryResponseBuilder)?
              updates]) =>
      (PageResponseInsurerPolicySummaryResponseBuilder()..update(updates))
          ._build();

  _$PageResponseInsurerPolicySummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseInsurerPolicySummaryResponse rebuild(
          void Function(PageResponseInsurerPolicySummaryResponseBuilder)
              updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseInsurerPolicySummaryResponseBuilder toBuilder() =>
      PageResponseInsurerPolicySummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseInsurerPolicySummaryResponse &&
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
            r'PageResponseInsurerPolicySummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseInsurerPolicySummaryResponseBuilder
    implements
        Builder<PageResponseInsurerPolicySummaryResponse,
            PageResponseInsurerPolicySummaryResponseBuilder> {
  _$PageResponseInsurerPolicySummaryResponse? _$v;

  ListBuilder<InsurerPolicySummaryResponse>? _content;
  ListBuilder<InsurerPolicySummaryResponse> get content =>
      _$this._content ??= ListBuilder<InsurerPolicySummaryResponse>();
  set content(ListBuilder<InsurerPolicySummaryResponse>? content) =>
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

  PageResponseInsurerPolicySummaryResponseBuilder() {
    PageResponseInsurerPolicySummaryResponse._defaults(this);
  }

  PageResponseInsurerPolicySummaryResponseBuilder get _$this {
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
  void replace(PageResponseInsurerPolicySummaryResponse other) {
    _$v = other as _$PageResponseInsurerPolicySummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseInsurerPolicySummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseInsurerPolicySummaryResponse build() => _build();

  _$PageResponseInsurerPolicySummaryResponse _build() {
    _$PageResponseInsurerPolicySummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseInsurerPolicySummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseInsurerPolicySummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseInsurerPolicySummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseInsurerPolicySummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseInsurerPolicySummaryResponse',
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
