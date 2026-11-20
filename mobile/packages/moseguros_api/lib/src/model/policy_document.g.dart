// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'policy_document.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$PolicyDocument extends PolicyDocument {
  @override
  final String? fileId;
  @override
  final String? fileName;
  @override
  final PolicyDocumentStatus status;
  @override
  final PolicyDocumentType type;

  factory _$PolicyDocument([void Function(PolicyDocumentBuilder)? updates]) =>
      (PolicyDocumentBuilder()..update(updates))._build();

  _$PolicyDocument._(
      {this.fileId, this.fileName, required this.status, required this.type})
      : super._();
  @override
  PolicyDocument rebuild(void Function(PolicyDocumentBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  PolicyDocumentBuilder toBuilder() => PolicyDocumentBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is PolicyDocument &&
        fileId == other.fileId &&
        fileName == other.fileName &&
        status == other.status &&
        type == other.type;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, fileId.hashCode);
    _$hash = $jc(_$hash, fileName.hashCode);
    _$hash = $jc(_$hash, status.hashCode);
    _$hash = $jc(_$hash, type.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'PolicyDocument')
          ..add('fileId', fileId)
          ..add('fileName', fileName)
          ..add('status', status)
          ..add('type', type))
        .toString();
  }
}

class PolicyDocumentBuilder
    implements Builder<PolicyDocument, PolicyDocumentBuilder> {
  _$PolicyDocument? _$v;

  String? _fileId;
  String? get fileId => _$this._fileId;
  set fileId(String? fileId) => _$this._fileId = fileId;

  String? _fileName;
  String? get fileName => _$this._fileName;
  set fileName(String? fileName) => _$this._fileName = fileName;

  PolicyDocumentStatus? _status;
  PolicyDocumentStatus? get status => _$this._status;
  set status(PolicyDocumentStatus? status) => _$this._status = status;

  PolicyDocumentType? _type;
  PolicyDocumentType? get type => _$this._type;
  set type(PolicyDocumentType? type) => _$this._type = type;

  PolicyDocumentBuilder() {
    PolicyDocument._defaults(this);
  }

  PolicyDocumentBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _fileId = $v.fileId;
      _fileName = $v.fileName;
      _status = $v.status;
      _type = $v.type;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(PolicyDocument other) {
    _$v = other as _$PolicyDocument;
  }

  @override
  void update(void Function(PolicyDocumentBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  PolicyDocument build() => _build();

  _$PolicyDocument _build() {
    final _$result = _$v ??
        _$PolicyDocument._(
          fileId: fileId,
          fileName: fileName,
          status: BuiltValueNullFieldError.checkNotNull(
              status, r'PolicyDocument', 'status'),
          type: BuiltValueNullFieldError.checkNotNull(
              type, r'PolicyDocument', 'type'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
