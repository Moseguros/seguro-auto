// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_payment_file_import.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponsePaymentFileImport extends PageResponsePaymentFileImport {
  @override
  final BuiltList<PaymentFileImport> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponsePaymentFileImport(
          [void Function(PageResponsePaymentFileImportBuilder)? updates]) =>
      (PageResponsePaymentFileImportBuilder()..update(updates))._build();

  _$PageResponsePaymentFileImport._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponsePaymentFileImport rebuild(
          void Function(PageResponsePaymentFileImportBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponsePaymentFileImportBuilder toBuilder() =>
      PageResponsePaymentFileImportBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponsePaymentFileImport &&
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
    return (newBuiltValueToStringHelper(r'PageResponsePaymentFileImport')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponsePaymentFileImportBuilder
    implements
        Builder<PageResponsePaymentFileImport,
            PageResponsePaymentFileImportBuilder> {
  _$PageResponsePaymentFileImport? _$v;

  ListBuilder<PaymentFileImport>? _content;
  ListBuilder<PaymentFileImport> get content =>
      _$this._content ??= ListBuilder<PaymentFileImport>();
  set content(ListBuilder<PaymentFileImport>? content) =>
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

  PageResponsePaymentFileImportBuilder() {
    PageResponsePaymentFileImport._defaults(this);
  }

  PageResponsePaymentFileImportBuilder get _$this {
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
  void replace(PageResponsePaymentFileImport other) {
    _$v = other as _$PageResponsePaymentFileImport;
  }

  @override
  void update(void Function(PageResponsePaymentFileImportBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponsePaymentFileImport build() => _build();

  _$PageResponsePaymentFileImport _build() {
    _$PageResponsePaymentFileImport _$result;
    try {
      _$result = _$v ??
          _$PageResponsePaymentFileImport._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponsePaymentFileImport', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponsePaymentFileImport', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponsePaymentFileImport', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponsePaymentFileImport', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
