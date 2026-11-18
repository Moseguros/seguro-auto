// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'page_response_intent_view.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PageResponseIntentView extends PageResponseIntentView {
  @override
  final BuiltList<IntentView> content;
  @override
  final int page;
  @override
  final int size;
  @override
  final int totalElements;

  factory _$PageResponseIntentView(
          [void Function(PageResponseIntentViewBuilder)? updates]) =>
      (PageResponseIntentViewBuilder()..update(updates))._build();

  _$PageResponseIntentView._(
      {required this.content,
      required this.page,
      required this.size,
      required this.totalElements})
      : super._();
  @override
  PageResponseIntentView rebuild(
          void Function(PageResponseIntentViewBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PageResponseIntentViewBuilder toBuilder() =>
      PageResponseIntentViewBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PageResponseIntentView &&
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
    return (newBuiltValueToStringHelper(r'PageResponseIntentView')
          ..add('content', content)
          ..add('page', page)
          ..add('size', size)
          ..add('totalElements', totalElements))
        .toString();
  }
}

class PageResponseIntentViewBuilder
    implements Builder<PageResponseIntentView, PageResponseIntentViewBuilder> {
  _$PageResponseIntentView? _$v;

  ListBuilder<IntentView>? _content;
  ListBuilder<IntentView> get content =>
      _$this._content ??= ListBuilder<IntentView>();
  set content(ListBuilder<IntentView>? content) => _$this._content = content;

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

  PageResponseIntentViewBuilder() {
    PageResponseIntentView._defaults(this);
  }

  PageResponseIntentViewBuilder get _$this {
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
  void replace(PageResponseIntentView other) {
    _$v = other as _$PageResponseIntentView;
  }

  @override
  void update(void Function(PageResponseIntentViewBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PageResponseIntentView build() => _build();

  _$PageResponseIntentView _build() {
    _$PageResponseIntentView _$result;
    try {
      _$result = _$v ??
          _$PageResponseIntentView._(
            content: content.build(),
            page: BuiltValueNullFieldError.checkNotNull(
                page, r'PageResponseIntentView', 'page'),
            size: BuiltValueNullFieldError.checkNotNull(
                size, r'PageResponseIntentView', 'size'),
            totalElements: BuiltValueNullFieldError.checkNotNull(
                totalElements, r'PageResponseIntentView', 'totalElements'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'content';
        content.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'PageResponseIntentView', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
