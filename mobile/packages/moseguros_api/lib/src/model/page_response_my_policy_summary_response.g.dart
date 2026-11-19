// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_my_policy_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseMyPolicySummaryResponse
    extends PageResponseMyPolicySummaryResponse {
  @override
  final BuiltList<MyPolicySummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseMyPolicySummaryResponse(
          [void Function(PageResponseMyPolicySummaryResponseBuilder)?
              updates]) =>
      (PageResponseMyPolicySummaryResponseBuilder()..update(updates))._build();

  _$PageResponseMyPolicySummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseMyPolicySummaryResponse rebuild(
          void Function(PageResponseMyPolicySummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseMyPolicySummaryResponseBuilder toBuilder() =>
      PageResponseMyPolicySummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseMyPolicySummaryResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseMyPolicySummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseMyPolicySummaryResponseBuilder
    implements
        Builder<PageResponseMyPolicySummaryResponse,
            PageResponseMyPolicySummaryResponseBuilder> {
  _$PageResponseMyPolicySummaryResponse? _$v;

  ListBuilder<MyPolicySummaryResponse>? _content;
  ListBuilder<MyPolicySummaryResponse> get content =>
      _$this._content ??= ListBuilder<MyPolicySummaryResponse>();
  set content(ListBuilder<MyPolicySummaryResponse>? content) =>
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

  PageResponseMyPolicySummaryResponseBuilder() {
    PageResponseMyPolicySummaryResponse._defaults(this);
  }

  PageResponseMyPolicySummaryResponseBuilder get _$this {
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
  void replace(PageResponseMyPolicySummaryResponse other) {
    _$v = other as _$PageResponseMyPolicySummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseMyPolicySummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseMyPolicySummaryResponse build() => _build();

  _$PageResponseMyPolicySummaryResponse _build() {
    _$PageResponseMyPolicySummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseMyPolicySummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseMyPolicySummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseMyPolicySummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseMyPolicySummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(r'PageResponseMyPolicySummaryResponse',
            _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
