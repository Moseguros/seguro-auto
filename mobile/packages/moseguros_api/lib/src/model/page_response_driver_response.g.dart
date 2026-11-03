// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_driver_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseDriverResponse extends PageResponseDriverResponse {
  @override
  final BuiltList<DriverResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseDriverResponse(
          [void Function(PageResponseDriverResponseBuilder)? updates]) =>
      (PageResponseDriverResponseBuilder()..update(updates))._build();

  _$PageResponseDriverResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseDriverResponse rebuild(
          void Function(PageResponseDriverResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseDriverResponseBuilder toBuilder() =>
      PageResponseDriverResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseDriverResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseDriverResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseDriverResponseBuilder
    implements
        Builder<PageResponseDriverResponse, PageResponseDriverResponseBuilder> {
  _$PageResponseDriverResponse? _$v;

  ListBuilder<DriverResponse>? _content;
  ListBuilder<DriverResponse> get content =>
      _$this._content ??= ListBuilder<DriverResponse>();
  set content(ListBuilder<DriverResponse>? content) =>
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

  PageResponseDriverResponseBuilder() {
    PageResponseDriverResponse._defaults(this);
  }

  PageResponseDriverResponseBuilder get _$this {
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
  void replace(PageResponseDriverResponse other) {
    _$v = other as _$PageResponseDriverResponse;
  }

  @override
  void update(void Function(PageResponseDriverResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseDriverResponse build() => _build();

  _$PageResponseDriverResponse _build() {
    _$PageResponseDriverResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseDriverResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseDriverResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseDriverResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(
                totalElements, r'PageResponseDriverResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseDriverResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
