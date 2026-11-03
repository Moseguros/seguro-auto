// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'activate_invitation_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ActivateInvitationRequest extends ActivateInvitationRequest {
  @override
  final String totpCode;

  factory _$ActivateInvitationRequest(
          [void Function(ActivateInvitationRequestBuilder)? updates]) =>
      (ActivateInvitationRequestBuilder()..update(updates))._build();

  _$ActivateInvitationRequest._({required this.totpCode}) : super._();
  @override
  ActivateInvitationRequest rebuild(
          void Function(ActivateInvitationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ActivateInvitationRequestBuilder toBuilder() =>
      ActivateInvitationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ActivateInvitationRequest && totpCode == other.totpCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, totpCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'ActivateInvitationRequest')
          ..add('totpCode', totpCode))
        .toString();
  }
}

class ActivateInvitationRequestBuilder
    implements
        Builder<ActivateInvitationRequest, ActivateInvitationRequestBuilder> {
  _$ActivateInvitationRequest? _$v;

  String? _totpCode;
  String? get totpCode => _$this._totpCode;
  set totpCode(String? totpCode) => _$this._totpCode = totpCode;

  ActivateInvitationRequestBuilder() {
    ActivateInvitationRequest._defaults(this);
  }

  ActivateInvitationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _totpCode = $v.totpCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ActivateInvitationRequest other) {
    _$v = other as _$ActivateInvitationRequest;
  }

  @override
  void update(void Function(ActivateInvitationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ActivateInvitationRequest build() => _build();

  _$ActivateInvitationRequest _build() {
    final _$result = _$v ??
        _$ActivateInvitationRequest._(
          totpCode: BuiltValueNullFieldError.checkNotNull(
              totpCode, r'ActivateInvitationRequest', 'totpCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
