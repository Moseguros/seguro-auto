// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_staff_user_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseStaffUserResponse extends PageResponseStaffUserResponse {
  @override
  final BuiltList<StaffUserResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseStaffUserResponse(
          [void Function(PageResponseStaffUserResponseBuilder)? updates]) =>
      (PageResponseStaffUserResponseBuilder()..update(updates))._build();

  _$PageResponseStaffUserResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseStaffUserResponse rebuild(
          void Function(PageResponseStaffUserResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseStaffUserResponseBuilder toBuilder() =>
      PageResponseStaffUserResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseStaffUserResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseStaffUserResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseStaffUserResponseBuilder
    implements
        Builder<PageResponseStaffUserResponse,
            PageResponseStaffUserResponseBuilder> {
  _$PageResponseStaffUserResponse? _$v;

  ListBuilder<StaffUserResponse>? _content;
  ListBuilder<StaffUserResponse> get content =>
      _$this._content ??= ListBuilder<StaffUserResponse>();
  set content(ListBuilder<StaffUserResponse>? content) =>
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

  PageResponseStaffUserResponseBuilder() {
    PageResponseStaffUserResponse._defaults(this);
  }

  PageResponseStaffUserResponseBuilder get _$this {
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
  void replace(PageResponseStaffUserResponse other) {
    _$v = other as _$PageResponseStaffUserResponse;
  }

  @override
  void update(void Function(PageResponseStaffUserResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseStaffUserResponse build() => _build();

  _$PageResponseStaffUserResponse _build() {
    _$PageResponseStaffUserResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseStaffUserResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseStaffUserResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseStaffUserResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseStaffUserResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseStaffUserResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
