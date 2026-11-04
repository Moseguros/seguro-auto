// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'submit_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SubmitProposalRequest extends SubmitProposalRequest {
  @override
  final int version;

  factory _$SubmitProposalRequest(
          [void Function(SubmitProposalRequestBuilder)? updates]) =>
      (SubmitProposalRequestBuilder()..update(updates))._build();

  _$SubmitProposalRequest._({required this.version}) : super._();
  @override
  SubmitProposalRequest rebuild(
          void Function(SubmitProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SubmitProposalRequestBuilder toBuilder() =>
      SubmitProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SubmitProposalRequest && version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SubmitProposalRequest')
          ..add('version', version))
        .toString();
  }
}

class SubmitProposalRequestBuilder
    implements Builder<SubmitProposalRequest, SubmitProposalRequestBuilder> {
  _$SubmitProposalRequest? _$v;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  SubmitProposalRequestBuilder() {
    SubmitProposalRequest._defaults(this);
  }

  SubmitProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SubmitProposalRequest other) {
    _$v = other as _$SubmitProposalRequest;
  }

  @override
  void update(void Function(SubmitProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SubmitProposalRequest build() => _build();

  _$SubmitProposalRequest _build() {
    final _$result = _$v ??
        _$SubmitProposalRequest._(
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'SubmitProposalRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
