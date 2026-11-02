// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'suspend_organization_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$SuspendOrganizationRequest extends SuspendOrganizationRequest {
  @override
  final String reason;

  factory _$SuspendOrganizationRequest(
          [void Function(SuspendOrganizationRequestBuilder)? updates]) =>
      (SuspendOrganizationRequestBuilder()..update(updates))._build();

  _$SuspendOrganizationRequest._({required this.reason}) : super._();
  @override
  SuspendOrganizationRequest rebuild(
          void Function(SuspendOrganizationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  SuspendOrganizationRequestBuilder toBuilder() =>
      SuspendOrganizationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is SuspendOrganizationRequest && reason == other.reason;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, reason.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'SuspendOrganizationRequest')
          ..add('reason', reason))
        .toString();
  }
}

class SuspendOrganizationRequestBuilder
    implements
        Builder<SuspendOrganizationRequest, SuspendOrganizationRequestBuilder> {
  _$SuspendOrganizationRequest? _$v;

  String? _reason;
  String? get reason => _$this._reason;
  set reason(String? reason) => _$this._reason = reason;

  SuspendOrganizationRequestBuilder() {
    SuspendOrganizationRequest._defaults(this);
  }

  SuspendOrganizationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _reason = $v.reason;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(SuspendOrganizationRequest other) {
    _$v = other as _$SuspendOrganizationRequest;
  }

  @override
  void update(void Function(SuspendOrganizationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  SuspendOrganizationRequest build() => _build();

  _$SuspendOrganizationRequest _build() {
    final _$result = _$v ??
        _$SuspendOrganizationRequest._(
          reason: BuiltValueNullFieldError.checkNotNull(
              reason, r'SuspendOrganizationRequest', 'reason'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
