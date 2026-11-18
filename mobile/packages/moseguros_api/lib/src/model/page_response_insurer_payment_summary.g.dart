// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_insurer_payment_summary.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseInsurerPaymentSummary
    extends PageResponseInsurerPaymentSummary {
  @override
  final BuiltList<InsurerPaymentSummary> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseInsurerPaymentSummary(
          [void Function(PageResponseInsurerPaymentSummaryBuilder)? updates]) =>
      (PageResponseInsurerPaymentSummaryBuilder()..update(updates))._build();

  _$PageResponseInsurerPaymentSummary._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseInsurerPaymentSummary rebuild(
          void Function(PageResponseInsurerPaymentSummaryBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseInsurerPaymentSummaryBuilder toBuilder() =>
      PageResponseInsurerPaymentSummaryBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseInsurerPaymentSummary &&
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
    return (newBuiltValueToStringHelper(r'PageResponseInsurerPaymentSummary')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseInsurerPaymentSummaryBuilder
    implements
        Builder<PageResponseInsurerPaymentSummary,
            PageResponseInsurerPaymentSummaryBuilder> {
  _$PageResponseInsurerPaymentSummary? _$v;

  ListBuilder<InsurerPaymentSummary>? _content;
  ListBuilder<InsurerPaymentSummary> get content =>
      _$this._content ??= ListBuilder<InsurerPaymentSummary>();
  set content(ListBuilder<InsurerPaymentSummary>? content) =>
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

  PageResponseInsurerPaymentSummaryBuilder() {
    PageResponseInsurerPaymentSummary._defaults(this);
  }

  PageResponseInsurerPaymentSummaryBuilder get _$this {
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
  void replace(PageResponseInsurerPaymentSummary other) {
    _$v = other as _$PageResponseInsurerPaymentSummary;
  }

  @override
  void update(
      void Function(PageResponseInsurerPaymentSummaryBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseInsurerPaymentSummary build() => _build();

  _$PageResponseInsurerPaymentSummary _build() {
    _$PageResponseInsurerPaymentSummary _$result;
    try {
      _$result = _$v ??
          _$PageResponseInsurerPaymentSummary._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseInsurerPaymentSummary', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseInsurerPaymentSummary', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseInsurerPaymentSummary', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseInsurerPaymentSummary', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
