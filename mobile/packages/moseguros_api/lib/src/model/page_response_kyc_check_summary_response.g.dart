// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_kyc_check_summary_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseKycCheckSummaryResponse
    extends PageResponseKycCheckSummaryResponse {
  @override
  final BuiltList<KycCheckSummaryResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseKycCheckSummaryResponse(
          [void Function(PageResponseKycCheckSummaryResponseBuilder)?
              updates]) =>
      (PageResponseKycCheckSummaryResponseBuilder()..update(updates))._build();

  _$PageResponseKycCheckSummaryResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseKycCheckSummaryResponse rebuild(
          void Function(PageResponseKycCheckSummaryResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseKycCheckSummaryResponseBuilder toBuilder() =>
      PageResponseKycCheckSummaryResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseKycCheckSummaryResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseKycCheckSummaryResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseKycCheckSummaryResponseBuilder
    implements
        Builder<PageResponseKycCheckSummaryResponse,
            PageResponseKycCheckSummaryResponseBuilder> {
  _$PageResponseKycCheckSummaryResponse? _$v;

  ListBuilder<KycCheckSummaryResponse>? _content;
  ListBuilder<KycCheckSummaryResponse> get content =>
      _$this._content ??= ListBuilder<KycCheckSummaryResponse>();
  set content(ListBuilder<KycCheckSummaryResponse>? content) =>
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

  PageResponseKycCheckSummaryResponseBuilder() {
    PageResponseKycCheckSummaryResponse._defaults(this);
  }

  PageResponseKycCheckSummaryResponseBuilder get _$this {
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
  void replace(PageResponseKycCheckSummaryResponse other) {
    _$v = other as _$PageResponseKycCheckSummaryResponse;
  }

  @override
  void update(
      void Function(PageResponseKycCheckSummaryResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseKycCheckSummaryResponse build() => _build();

  _$PageResponseKycCheckSummaryResponse _build() {
    _$PageResponseKycCheckSummaryResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseKycCheckSummaryResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseKycCheckSummaryResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseKycCheckSummaryResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseKycCheckSummaryResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(r'PageResponseKycCheckSummaryResponse',
            _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
