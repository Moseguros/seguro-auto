// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'kyc_files_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$KycFilesResponse extends KycFilesResponse {
  @override
  final String? docBackFileId;
  @override
  final String docFrontFileId;
  @override
  final String selfieFileId;

  factory _$KycFilesResponse(
          [void Function(KycFilesResponseBuilder)? updates]) =>
      (KycFilesResponseBuilder()..update(updates))._build();

  _$KycFilesResponse._(
      {this.docBackFileId,
      required this.docFrontFileId,
      required this.selfieFileId})
      : super._();
  @override
  KycFilesResponse rebuild(void Function(KycFilesResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  KycFilesResponseBuilder toBuilder() =>
      KycFilesResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is KycFilesResponse &&
        docBackFileId == other.docBackFileId &&
        docFrontFileId == other.docFrontFileId &&
        selfieFileId == other.selfieFileId;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, docBackFileId.hashCode);
    _$hash = $jc(_$hash, docFrontFileId.hashCode);
    _$hash = $jc(_$hash, selfieFileId.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'KycFilesResponse')
          ..add('docBackFileId', docBackFileId)
          ..add('docFrontFileId', docFrontFileId)
          ..add('selfieFileId', selfieFileId))
        .toString();
  }
}

class KycFilesResponseBuilder
    implements Builder<KycFilesResponse, KycFilesResponseBuilder> {
  _$KycFilesResponse? _$v;

  String? _docBackFileId;
  String? get docBackFileId => _$this._docBackFileId;
  set docBackFileId(String? docBackFileId) =>
      _$this._docBackFileId = docBackFileId;

  String? _docFrontFileId;
  String? get docFrontFileId => _$this._docFrontFileId;
  set docFrontFileId(String? docFrontFileId) =>
      _$this._docFrontFileId = docFrontFileId;

  String? _selfieFileId;
  String? get selfieFileId => _$this._selfieFileId;
  set selfieFileId(String? selfieFileId) => _$this._selfieFileId = selfieFileId;

  KycFilesResponseBuilder() {
    KycFilesResponse._defaults(this);
  }

  KycFilesResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _docBackFileId = $v.docBackFileId;
      _docFrontFileId = $v.docFrontFileId;
      _selfieFileId = $v.selfieFileId;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(KycFilesResponse other) {
    _$v = other as _$KycFilesResponse;
  }

  @override
  void update(void Function(KycFilesResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  KycFilesResponse build() => _build();

  _$KycFilesResponse _build() {
    final _$result = _$v ??
        _$KycFilesResponse._(
          docBackFileId: docBackFileId,
          docFrontFileId: BuiltValueNullFieldError.checkNotNull(
              docFrontFileId, r'KycFilesResponse', 'docFrontFileId'),
          selfieFileId: BuiltValueNullFieldError.checkNotNull(
              selfieFileId, r'KycFilesResponse', 'selfieFileId'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
