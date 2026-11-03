// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'registration_doc_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RegistrationDocResponse extends RegistrationDocResponse {
  @override
  final String contentType;
  @override
  final String fileId;
  @override
  final int sizeBytes;
  @override
  final DateTime uploadedAt;

  factory _$RegistrationDocResponse(
          [void Function(RegistrationDocResponseBuilder)? updates]) =>
      (RegistrationDocResponseBuilder()..update(updates))._build();

  _$RegistrationDocResponse._(
      {required this.contentType,
      required this.fileId,
      required this.sizeBytes,
      required this.uploadedAt})
      : super._();
  @override
  RegistrationDocResponse rebuild(
          void Function(RegistrationDocResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RegistrationDocResponseBuilder toBuilder() =>
      RegistrationDocResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RegistrationDocResponse &&
        contentType == other.contentType &&
        fileId == other.fileId &&
        sizeBytes == other.sizeBytes &&
        uploadedAt == other.uploadedAt;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, contentType.hashCode);
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, sizeBytes.hashCode);
    _$hash = $jc(_$hash, uploadedAt.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RegistrationDocResponse')
          ..add('contentType', contentType)
          ..add('fileId', fileId)
          ..add('sizeBytes', sizeBytes)
          ..add('uploadedAt', uploadedAt))
        .toString();
  }
}

class RegistrationDocResponseBuilder
    implements
        Builder<RegistrationDocResponse, RegistrationDocResponseBuilder> {
  _$RegistrationDocResponse? _$v;

  String? _contentType;
  String? get contentType => _$this._contentType;
  set contentType(String? contentType) => _$this._contentType = contentType;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  int? _sizeBytes;
  int? get sizeBytes => _$this._sizeBytes;
  set sizeBytes(int? sizeBytes) => _$this._sizeBytes = sizeBytes;

  DateTime? _uploadedAt;
  DateTime? get uploadedAt => _$this._uploadedAt;
  set uploadedAt(DateTime? uploadedAt) => _$this._uploadedAt = uploadedAt;

  RegistrationDocResponseBuilder() {
    RegistrationDocResponse._defaults(this);
  }

  RegistrationDocResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _contentType = $v.contentType;
      _fileId = $v.fileId;
      _sizeBytes = $v.sizeBytes;
      _uploadedAt = $v.uploadedAt;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RegistrationDocResponse other) {
    _$v = other as _$RegistrationDocResponse;
  }

  @override
  void update(void Function(RegistrationDocResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RegistrationDocResponse build() => _build();

  _$RegistrationDocResponse _build() {
    final _$result = _$v ??
        _$RegistrationDocResponse._(
          contentType: BuiltValueNullFieldError.checkNotNull(
              contentType, r'RegistrationDocResponse', 'contentType'),
          fileId: BuiltValueNullFieldError.checkNotNull(
              fileId, r'RegistrationDocResponse', 'fileId'),
          sizeBytes: BuiltValueNullFieldError.checkNotNull(
              sizeBytes, r'RegistrationDocResponse', 'sizeBytes'),
          uploadedAt: BuiltValueNullFieldError.checkNotNull(
              uploadedAt, r'RegistrationDocResponse', 'uploadedAt'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
