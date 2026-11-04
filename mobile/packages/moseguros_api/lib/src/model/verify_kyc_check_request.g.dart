// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'verify_kyc_check_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VerifyKycCheckRequest extends VerifyKycCheckRequest {
  @override
  final int version;

  factory _$VerifyKycCheckRequest(
          [void Function(VerifyKycCheckRequestBuilder)? updates]) =>
      (VerifyKycCheckRequestBuilder()..update(updates))._build();

  _$VerifyKycCheckRequest._({required this.version}) : super._();
  @override
  VerifyKycCheckRequest rebuild(
          void Function(VerifyKycCheckRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VerifyKycCheckRequestBuilder toBuilder() =>
      VerifyKycCheckRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VerifyKycCheckRequest && version == other.version;
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
    return (newBuiltValueToStringHelper(r'VerifyKycCheckRequest')
          ..add('version', version))
        .toString();
  }
}

class VerifyKycCheckRequestBuilder
    implements Builder<VerifyKycCheckRequest, VerifyKycCheckRequestBuilder> {
  _$VerifyKycCheckRequest? _$v;

  int? _version;
  int? get version => _$this._version;
  set version(int? version) => _$this._version = version;

  VerifyKycCheckRequestBuilder() {
    VerifyKycCheckRequest._defaults(this);
  }

  VerifyKycCheckRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _version = $v.version;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VerifyKycCheckRequest other) {
    _$v = other as _$VerifyKycCheckRequest;
  }

  @override
  void update(void Function(VerifyKycCheckRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VerifyKycCheckRequest build() => _build();

  _$VerifyKycCheckRequest _build() {
    final _$result = _$v ??
        _$VerifyKycCheckRequest._(
          version: BuiltValueNullFieldError.checkNotNull(
              version, r'VerifyKycCheckRequest', 'version'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
