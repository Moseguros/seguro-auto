// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'accept_invitation_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$AcceptInvitationRequest extends AcceptInvitationRequest {
  @override
  final String password;

  factory _$AcceptInvitationRequest(
          [void Function(AcceptInvitationRequestBuilder)? updates]) =>
      (AcceptInvitationRequestBuilder()..update(updates))._build();

  _$AcceptInvitationRequest._({required this.password}) : super._();
  @override
  AcceptInvitationRequest rebuild(
          void Function(AcceptInvitationRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  AcceptInvitationRequestBuilder toBuilder() =>
      AcceptInvitationRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is AcceptInvitationRequest && password == other.password;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, password.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'AcceptInvitationRequest')
          ..add('password', password))
        .toString();
  }
}

class AcceptInvitationRequestBuilder
    implements
        Builder<AcceptInvitationRequest, AcceptInvitationRequestBuilder> {
  _$AcceptInvitationRequest? _$v;

  String? _password;
  String? get password => _$this._password;
  set password(String? password) => _$this._password = password;

  AcceptInvitationRequestBuilder() {
    AcceptInvitationRequest._defaults(this);
  }

  AcceptInvitationRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _password = $v.password;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(AcceptInvitationRequest other) {
    _$v = other as _$AcceptInvitationRequest;
  }

  @override
  void update(void Function(AcceptInvitationRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  AcceptInvitationRequest build() => _build();

  _$AcceptInvitationRequest _build() {
    final _$result = _$v ??
        _$AcceptInvitationRequest._(
          password: BuiltValueNullFieldError.checkNotNull(
              password, r'AcceptInvitationRequest', 'password'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
