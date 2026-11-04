// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'update_broker_proposal_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$UpdateBrokerProposalRequest extends UpdateBrokerProposalRequest {
  @override
  final BrokerPolicyholderRequest? policyholder;
  @override
  final Date? startDate;
  @override
  final int version;

  factory _$UpdateBrokerProposalRequest(
          [void Function(UpdateBrokerProposalRequestBuilder)? updates]) =>
      (UpdateBrokerProposalRequestBuilder()..update(updates))._build();

  _$UpdateBrokerProposalRequest._(
      {this.policyholder, this.startDate, required this.version})
      : super._();
  @override
  UpdateBrokerProposalRequest rebuild(
          void Function(UpdateBrokerProposalRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  UpdateBrokerProposalRequestBuilder toBuilder() =>
      UpdateBrokerProposalRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is UpdateBrokerProposalRequest &&
        policyholder == other.policyholder &&
        startDate == other.startDate &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, policyholder.hashCode);
    _$hash = $jc(_$hash, startDate.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'UpdateBrokerProposalRequest')
          ..add('policyholder', policyholder)
          ..add('startDate', startDate)
          ..add('version', version))
        .toString();
  }
}

class UpdateBrokerProposalRequestBuilder
    implements
        Builder<UpdateBrokerProposalRequest,
            UpdateBrokerProposalRequestBuilder> {
  _$UpdateBrokerProposalRequest? _$v;

  BrokerPolicyholderRequestBuilder? _policyholder;
  BrokerPolicyholderRequestBuilder get policyholder =>
      _$this._policyholder ??= BrokerPolicyholderRequestBuilder();
  set policyholder(BrokerPolicyholderRequestBuilder? policyholder) =>
      _$this._policyholder = policyholder;

  Date? _startDate;
  Date? get startDate => _$this._startDate;
  set startDate(Date? startDate) => _$this._startDate = startDate;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  UpdateBrokerProposalRequestBuilder() {
    UpdateBrokerProposalRequest._defaults(this);
  }

  UpdateBrokerProposalRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _policyholder = $v.policyholder?.toBuilder();
      _startDate = $v.startDate;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(UpdateBrokerProposalRequest other) {
    _$v = other as _$UpdateBrokerProposalRequest;
  }

  @override
  void update(void Function(UpdateBrokerProposalRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  UpdateBrokerProposalRequest build() => _build();

  _$UpdateBrokerProposalRequest _build() {
    _$UpdateBrokerProposalRequest _$result;
    try {
      _$result = _$v ??
          _$UpdateBrokerProposalRequest._(
            policyholder: _policyholder?.build(),
            startDate: startDate,
            version: BuiltValueNullFieldError.checkNotNull(
                version, r'UpdateBrokerProposalRequest', 'version'),
          );
    } catch (_) {
      late String _$failedField;
      try {
        _$failedField = 'policyholder';
        _policyholder?.build();
      } catch (e) {
        throw BuiltValueNestedFieldError(
            r'UpdateBrokerProposalRequest', _$failedField, e.toString());
      }
      rethrow;
    }
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
