// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_vehicle_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseVehicleResponse extends PageResponseVehicleResponse {
  @override
  final BuiltList<VehicleResponse> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseVehicleResponse(
          [void Function(PageResponseVehicleResponseBuilder)? updates]) =>
      (PageResponseVehicleResponseBuilder()..update(updates))._build();

  _$PageResponseVehicleResponse._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseVehicleResponse rebuild(
          void Function(PageResponseVehicleResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseVehicleResponseBuilder toBuilder() =>
      PageResponseVehicleResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseVehicleResponse &&
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
    return (newBuiltValueToStringHelper(r'PageResponseVehicleResponse')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseVehicleResponseBuilder
    implements
        Builder<PageResponseVehicleResponse,
            PageResponseVehicleResponseBuilder> {
  _$PageResponseVehicleResponse? _$v;

  ListBuilder<VehicleResponse>? _content;
  ListBuilder<VehicleResponse> get content =>
      _$this._content ??= ListBuilder<VehicleResponse>();
  set content(ListBuilder<VehicleResponse>? content) =>
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

  PageResponseVehicleResponseBuilder() {
    PageResponseVehicleResponse._defaults(this);
  }

  PageResponseVehicleResponseBuilder get _$this {
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
  void replace(PageResponseVehicleResponse other) {
    _$v = other as _$PageResponseVehicleResponse;
  }

  @override
  void update(void Function(PageResponseVehicleResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseVehicleResponse build() => _build();

  _$PageResponseVehicleResponse _build() {
    _$PageResponseVehicleResponse _$result;
    try {
      _$result = _$v ??
          _$PageResponseVehicleResponse._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseVehicleResponse', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseVehicleResponse', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(
                totalElements, r'PageResponseVehicleResponse', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseVehicleResponse', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
