// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateProposalRequest extends UpdateProposalRequest {
  @override
  final Date startDate;
  @override
  final int version;

  factory _$UpdateProposalRequest(
          [void Function(UpdateProposalRequestBuilder)? updates]) =>
      (UpdateProposalRequestBuilder()..update(updates))._build();

  _$UpdateProposalRequest._({required this.startDate, required this.version})
      : super._();
  @override
  UpdateProposalRequest rebuild(
          void Function(UpdateProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateProposalRequestBuilder toBuilder() =>
      UpdateProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateProposalRequest &&
        startDate == other.startDate &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateProposalRequest')
          ..add('startDate', startDate)
          ..add('version', version))
        .toString();
  }
}

class UpdateProposalRequestBuilder
    implements Builder<UpdateProposalRequest, UpdateProposalRequestBuilder> {
  _$UpdateProposalRequest? _$v;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateProposalRequestBuilder() {
    UpdateProposalRequest._defaults(this);
  }

  UpdateProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _startDate = $v.startDate;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateProposalRequest other) {
    _$v = other as _$UpdateProposalRequest;
  }

  @override
  void update(void Function(UpdateProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateProposalRequest build() => _build();

  _$UpdateProposalRequest _build() {
    final _$result = _$v ??
        _$UpdateProposalRequest._(
          startDate: BuiltValueNullFieldError.checkNotNull(
              startDate, r'UpdateProposalRequest', 'startDate'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'UpdateProposalRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
