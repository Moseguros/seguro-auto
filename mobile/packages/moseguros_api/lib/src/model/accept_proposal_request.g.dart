// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'accept_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AcceptProposalRequest extends AcceptProposalRequest {
  @override
  final String? note;
  @override
  final int version;

  factory _$AcceptProposalRequest(
          [void Function(AcceptProposalRequestBuilder)? updates]) =>
      (AcceptProposalRequestBuilder()..update(updates))._build();

  _$AcceptProposalRequest._({this.note, required this.version}) : super._();
  @override
  AcceptProposalRequest rebuild(
          void Function(AcceptProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AcceptProposalRequestBuilder toBuilder() =>
      AcceptProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AcceptProposalRequest &&
        note == other.note &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, note.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AcceptProposalRequest')
          ..add('note', note)
          ..add('version', version))
        .toString();
  }
}

class AcceptProposalRequestBuilder
    implements Builder<AcceptProposalRequest, AcceptProposalRequestBuilder> {
  _$AcceptProposalRequest? _$v;

  String? _note;
  String? get note => _$this._note;
  set note(String? note) => _$this._note = note;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  AcceptProposalRequestBuilder() {
    AcceptProposalRequest._defaults(this);
  }

  AcceptProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _note = $v.note;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AcceptProposalRequest other) {
    _$v = other as _$AcceptProposalRequest;
  }

  @override
  void update(void Function(AcceptProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AcceptProposalRequest build() => _build();

  _$AcceptProposalRequest _build() {
    final _$result = _$v ??
        _$AcceptProposalRequest._(
          note: note,
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'AcceptProposalRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
