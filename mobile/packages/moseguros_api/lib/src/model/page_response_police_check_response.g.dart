// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_police_check_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponsePoliceCheckResponse
    extends PageResponsePoliceCheckResponse {
  @override
  final BuiltList<PoliceCheckResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponsePoliceCheckResponse(
          [void Function(PageResponsePoliceCheckResponseBuilder)? updates]) =>
      (PageResponsePoliceCheckResponseBuilder()..update(updates))._build();

  _$PageResponsePoliceCheckResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponsePoliceCheckResponse rebuild(
          void Function(PageResponsePoliceCheckResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponsePoliceCheckResponseBuilder toBuilder() =>
      PageResponsePoliceCheckResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponsePoliceCheckResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponsePoliceCheckResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponsePoliceCheckResponseBuilder
    implements
        Builder<PageResponsePoliceCheckResponse,
            PageResponsePoliceCheckResponseBuilder> {
  _$PageResponsePoliceCheckResponse? _$v;

  ListBuilder<PoliceCheckResponse>? _content;
  ListBuilder<PoliceCheckResponse> get content =>
      _$this._content ??= ListBuilder<PoliceCheckResponse>();
  set content(ListBuilder<PoliceCheckResponse>? content) =>
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

  PageResponsePoliceCheckResponseBuilder() {
    PageResponsePoliceCheckResponse._defaults(this);
  }

  PageResponsePoliceCheckResponseBuilder get _$this {
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
  void replace(PageResponsePoliceCheckResponse other) {
    _$v = other as _$PageResponsePoliceCheckResponse;
  }

  @override
  void update(void Function(PageResponsePoliceCheckResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponsePoliceCheckResponse build() => _build();

  _$PageResponsePoliceCheckResponse _build() {
    _$PageResponsePoliceCheckResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponsePoliceCheckResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponsePoliceCheckResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponsePoliceCheckResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponsePoliceCheckResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponsePoliceCheckResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
