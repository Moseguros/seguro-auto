// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'insurer_branding_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$InsurerBrandingResponse extends InsurerBrandingResponse {
  @override
  final String? logoFileId;

  factory _$InsurerBrandingResponse(
          [void Function(InsurerBrandingResponseBuilder)? updates]) =>
      (InsurerBrandingResponseBuilder()..update(updates))._build();

  _$InsurerBrandingResponse._({this.logoFileId}) : super._();
  @override
  InsurerBrandingResponse rebuild(
          void Function(InsurerBrandingResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  InsurerBrandingResponseBuilder toBuilder() =>
      InsurerBrandingResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is InsurerBrandingResponse && logoFileId == other.logoFileId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, logoFileId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'InsurerBrandingResponse')
          ..add('logoFileId', logoFileId))
        .toString();
  }
}

class InsurerBrandingResponseBuilder
    implements
        Builder<InsurerBrandingResponse, InsurerBrandingResponseBuilder> {
  _$InsurerBrandingResponse? _$v;

  String? _logoFileId;
  String? get logoFileId => _$this._logoFileId;
  set logoFileId(String? logoFileId) => _$this._logoFileId = logoFileId;

  InsurerBrandingResponseBuilder() {
    InsurerBrandingResponse._defaults(this);
  }

  InsurerBrandingResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _logoFileId = $v.logoFileId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(InsurerBrandingResponse other) {
    _$v = other as _$InsurerBrandingResponse;
  }

  @override
  void update(void Function(InsurerBrandingResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  InsurerBrandingResponse build() => _build();

  _$InsurerBrandingResponse _build() {
    final _$result = _$v ??
        _$InsurerBrandingResponse._(
          logoFileId: logoFileId,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
