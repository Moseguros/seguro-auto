// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_invitation_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseInvitationResponse extends PageResponseInvitationResponse {
  @override
  final BuiltList<InvitationResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseInvitationResponse(
          [void Function(PageResponseInvitationResponseBuilder)? updates]) =>
      (PageResponseInvitationResponseBuilder()..update(updates))._build();

  _$PageResponseInvitationResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseInvitationResponse rebuild(
          void Function(PageResponseInvitationResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseInvitationResponseBuilder toBuilder() =>
      PageResponseInvitationResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseInvitationResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseInvitationResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseInvitationResponseBuilder
    implements
        Builder<PageResponseInvitationResponse,
            PageResponseInvitationResponseBuilder> {
  _$PageResponseInvitationResponse? _$v;

  ListBuilder<InvitationResponse>? _content;
  ListBuilder<InvitationResponse> get content =>
      _$this._content ??= ListBuilder<InvitationResponse>();
  set content(ListBuilder<InvitationResponse>? content) =>
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

  PageResponseInvitationResponseBuilder() {
    PageResponseInvitationResponse._defaults(this);
  }

  PageResponseInvitationResponseBuilder get _$this {
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
  void replace(PageResponseInvitationResponse other) {
    _$v = other as _$PageResponseInvitationResponse;
  }

  @override
  void update(void Function(PageResponseInvitationResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseInvitationResponse build() => _build();

  _$PageResponseInvitationResponse _build() {
    _$PageResponseInvitationResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseInvitationResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseInvitationResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseInvitationResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(totalElements,
                r'PageResponseInvitationResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseInvitationResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
