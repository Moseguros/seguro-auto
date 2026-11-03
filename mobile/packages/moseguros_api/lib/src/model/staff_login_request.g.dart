// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'staff_login_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$StaffLoginRequest extends StaffLoginRequest {
  @override
  final String email;
  @override
  final String password;
  @override
  final String totpCode;

  factory _$StaffLoginRequest(
          [void Function(StaffLoginRequestBuilder)? updates]) =>
      (StaffLoginRequestBuilder()..update(updates))._build();

  _$StaffLoginRequest._(
      {required this.email, required this.password, required this.totpCode})
      : super._();
  @override
  StaffLoginRequest rebuild(void Function(StaffLoginRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  StaffLoginRequestBuilder toBuilder() =>
      StaffLoginRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is StaffLoginRequest &&
        email == other.email &&
        password == other.password &&
        totpCode == other.totpCode;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, email.hashCode);
    _$hash = $jc(_$hash, password.hashCode);
    _$hash = $jc(_$hash, totpCode.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'StaffLoginRequest')
          ..add('email', email)
          ..add('password', password)
          ..add('totpCode', totpCode))
        .toString();
  }
}

class StaffLoginRequestBuilder
    implements Builder<StaffLoginRequest, StaffLoginRequestBuilder> {
  _$StaffLoginRequest? _$v;

  String? _email;
  String? get email => _$this._email;
  set email(String? email) => _$this._email = email;

  String? _password;
  String? get password => _$this._password;
  set password(String? password) => _$this._password = password;

  String? _totpCode;
  String? get totpCode => _$this._totpCode;
  set totpCode(String? totpCode) => _$this._totpCode = totpCode;

  StaffLoginRequestBuilder() {
    StaffLoginRequest._defaults(this);
  }

  StaffLoginRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _email = $v.email;
      _password = $v.password;
      _totpCode = $v.totpCode;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(StaffLoginRequest other) {
    _$v = other as _$StaffLoginRequest;
  }

  @override
  void update(void Function(StaffLoginRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  StaffLoginRequest build() => _build();

  _$StaffLoginRequest _build() {
    final _$result = _$v ??
        _$StaffLoginRequest._(
          email: BuiltValueNullFieldError.checkNotNull(
              email, r'StaffLoginRequest', 'email'),
          password: BuiltValueNullFieldError.checkNotNull(
              password, r'StaffLoginRequest', 'password'),
          totpCode: BuiltValueNullFieldError.checkNotNull(
              totpCode, r'StaffLoginRequest', 'totpCode'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
