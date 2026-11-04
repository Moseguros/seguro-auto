// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'reject_kyc_check_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$RejectKycCheckRequest extends RejectKycCheckRequest {
  @override
  final String? reason;
  @override
  final KycRejectionReason reasonCode;
  @override
  final int version;

  factory _$RejectKycCheckRequest(
          [void Function(RejectKycCheckRequestBuilder)? updates]) =>
      (RejectKycCheckRequestBuilder()..update(updates))._build();

  _$RejectKycCheckRequest._(
      {this.reason, required this.reasonCode, required this.version})
      : super._();
  @override
  RejectKycCheckRequest rebuild(
          void Function(RejectKycCheckRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  RejectKycCheckRequestBuilder toBuilder() =>
      RejectKycCheckRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is RejectKycCheckRequest &&
        reason == other.reason &&
        reasonCode == other.reasonCode &&
        version == other.version;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jc(_$hash, reasonCode.hashCode);
    _$hash = $jc(_$hash, version.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'RejectKycCheckRequest')
          ..add('reason', reason)
          ..add('reasonCode', reasonCode)
          ..add('version', version))
        .toString();
  }
}

class RejectKycCheckRequestBuilder
    implements Builder<RejectKycCheckRequest, RejectKycCheckRequestBuilder> {
  _$RejectKycCheckRequest? _$v;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  KycRejectionReason? _reasonCode;
  KycRejectionReason? get reasonCode => _$this._reasonCode;
  set reasonCode(KycRejectionReason? reasonCode) =>
      _$this._reasonCode = reasonCode;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  RejectKycCheckRequestBuilder() {
    RejectKycCheckRequest._defaults(this);
  }

  RejectKycCheckRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _reason = $v.reason;
      _reasonCode = $v.reasonCode;
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(RejectKycCheckRequest other) {
    _$v = other as _$RejectKycCheckRequest;
  }

  @override
  void update(void Function(RejectKycCheckRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  RejectKycCheckRequest build() => _build();

  _$RejectKycCheckRequest _build() {
    final _$result = _$v ??
        _$RejectKycCheckRequest._(
          reason: reason,
          reasonCode: BuiltValueNullFieldError.checkNotNull(
              reasonCode, r'RejectKycCheckRequest', 'reasonCode'),
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'RejectKycCheckRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
