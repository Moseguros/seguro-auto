// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'catalog_insurer_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$CatalogInsurerResponse extends CatalogInsurerResponse {
  @override
  final String displayName;
  @override
  final String id;

  factory _$CatalogInsurerResponse(
          [void Function(CatalogInsurerResponseBuilder)? updates]) =>
      (CatalogInsurerResponseBuilder()..update(updates))._build();

  _$CatalogInsurerResponse._({required this.displayName, required this.id})
      : super._();
  @override
  CatalogInsurerResponse rebuild(
          void Function(CatalogInsurerResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  CatalogInsurerResponseBuilder toBuilder() =>
      CatalogInsurerResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is CatalogInsurerResponse &&
        displayName == other.displayName &&
        id == other.id;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, displayName.hashCode);
    _$hash = $jc(_$hash, id.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'CatalogInsurerResponse')
          ..add('displayName', displayName)
          ..add('id', id))
        .toString();
  }
}

class CatalogInsurerResponseBuilder
    implements Builder<CatalogInsurerResponse, CatalogInsurerResponseBuilder> {
  _$CatalogInsurerResponse? _$v;

  String? _displayName;
  String? get displayName => _$this._displayName;
  set displayName(String? displayName) => _$this._displayName = displayName;

  String? _id;
  String? get id => _$this._id;
  set id(String? id) => _$this._id = id;

  CatalogInsurerResponseBuilder() {
    CatalogInsurerResponse._defaults(this);
  }

  CatalogInsurerResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _displayName = $v.displayName;
      _id = $v.id;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(CatalogInsurerResponse other) {
    _$v = other as _$CatalogInsurerResponse;
  }

  @override
  void update(void Function(CatalogInsurerResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  CatalogInsurerResponse build() => _build();

  _$CatalogInsurerResponse _build() {
    final _$result = _$v ??
        _$CatalogInsurerResponse._(
          displayName: BuiltValueNullFieldError.checkNotNull(
              displayName, r'CatalogInsurerResponse', 'displayName'),
          id: BuiltValueNullFieldError.checkNotNull(
              id, r'CatalogInsurerResponse', 'id'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
