// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_reference_value_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseReferenceValueResponse
    extends PageResponseReferenceValueResponse {
  @override
  final BuiltList<ReferenceValueResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseReferenceValueResponse(
          [void Function(PageResponseReferenceValueResponseBuilder)?
              updates]) =>
      (PageResponseReferenceValueResponseBuilder()..update(updates))._build();

  _$PageResponseReferenceValueResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseReferenceValueResponse rebuild(
          void Function(PageResponseReferenceValueResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseReferenceValueResponseBuilder toBuilder() =>
      PageResponseReferenceValueResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseReferenceValueResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseReferenceValueResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseReferenceValueResponseBuilder
    implements
        Builder<PageResponseReferenceValueResponse,
            PageResponseReferenceValueResponseBuilder> {
  _$PageResponseReferenceValueResponse? _$v;

  ListBuilder<ReferenceValueResponse>? _content;
  ListBuilder<ReferenceValueResponse> get content =>
      _$this._content ??= ListBuilder<ReferenceValueResponse>();
  set content(ListBuilder<ReferenceValueResponse>? content) =>
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

  PageResponseReferenceValueResponseBuilder() {
    PageResponseReferenceValueResponse._defaults(this);
  }

  PageResponseReferenceValueResponseBuilder get _$this {
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
  void replace(PageResponseReferenceValueResponse other) {
    _$v = other as _$PageResponseReferenceValueResponse;
  }

  @override
  void update(
      void Function(PageResponseReferenceValueResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseReferenceValueResponse build() => _build();

  _$PageResponseReferenceValueResponse _build() {
    _$PageResponseReferenceValueResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseReferenceValueResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseReferenceValueResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseReferenceValueResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseReferenceValueResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseReferenceValueResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
