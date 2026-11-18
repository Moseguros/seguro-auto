// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reference_document_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ReferenceDocumentResponse extends ReferenceDocumentResponse {
  @override
  final String fileId;

  factory _$ReferenceDocumentResponse(
          [void Function(ReferenceDocumentResponseBuilder)? updates]) =>
      (ReferenceDocumentResponseBuilder()..update(updates))._build();

  _$ReferenceDocumentResponse._({required this.fileId}) : super._();
  @override
  ReferenceDocumentResponse rebuild(
          void Function(ReferenceDocumentResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ReferenceDocumentResponseBuilder toBuilder() =>
      ReferenceDocumentResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ReferenceDocumentResponse && fileId == other.fileId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ReferenceDocumentResponse')
          ..add('fileId', fileId))
        .toString();
  }
}

class ReferenceDocumentResponseBuilder
    implements
        Builder<ReferenceDocumentResponse, ReferenceDocumentResponseBuilder> {
  _$ReferenceDocumentResponse? _$v;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  ReferenceDocumentResponseBuilder() {
    ReferenceDocumentResponse._defaults(this);
  }

  ReferenceDocumentResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fileId = $v.fileId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ReferenceDocumentResponse other) {
    _$v = other as _$ReferenceDocumentResponse;
  }

  @override
  void update(void Function(ReferenceDocumentResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ReferenceDocumentResponse build() => _build();

  _$ReferenceDocumentResponse _build() {
    final _$result = _$v ??
        _$ReferenceDocumentResponse._(
          fileId: BuiltValueNullFieldError.checkNotNull(
              fileId, r'ReferenceDocumentResponse', 'fileId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
