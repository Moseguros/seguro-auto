// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'otp_verify_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$OtpVerifyResponse extends OtpVerifyResponse {
  @override
  final String accessToken;
  @override
  final int expiresIn;
  @override
  final bool isNewUser;
  @override
  final String refreshToken;

  factory _$OtpVerifyResponse(
          [void Function(OtpVerifyResponseBuilder)? updates]) =>
      (OtpVerifyResponseBuilder()..update(updates))._build();

  _$OtpVerifyResponse._(
      {required this.accessToken,
      required this.expiresIn,
      required this.isNewUser,
      required this.refreshToken})
      : super._();
  @override
  OtpVerifyResponse rebuild(void Function(OtpVerifyResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  OtpVerifyResponseBuilder toBuilder() =>
      OtpVerifyResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is OtpVerifyResponse &&
        accessToken == other.accessToken &&
        expiresIn == other.expiresIn &&
        isNewUser == other.isNewUser &&
        refreshToken == other.refreshToken;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, accessToken.hashCode);
    _$hash = $jc(_$hash, expiresIn.hashCode);
    _$hash = $jc(_$hash, isNewUser.hashCode);
    _$hash = $jc(_$hash, refreshToken.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'OtpVerifyResponse')
          ..add('accessToken', accessToken)
          ..add('expiresIn', expiresIn)
          ..add('isNewUser', isNewUser)
          ..add('refreshToken', refreshToken))
        .toString();
  }
}

class OtpVerifyResponseBuilder
    implements Builder<OtpVerifyResponse, OtpVerifyResponseBuilder> {
  _$OtpVerifyResponse? _$v;

  String? _accessToken;
  String? get accessToken => _$this._accessToken;
  set accessToken(String? accessToken) => _$this._accessToken = accessToken;

  int? _expiresIn;
  int? get expiresIn => _$this._expiresIn;
  set expiresIn(int? expiresIn) => _$this._expiresIn = expiresIn;

  bool? _isNewUser;
  bool? get isNewUser => _$this._isNewUser;
  set isNewUser(bool? isNewUser) => _$this._isNewUser = isNewUser;

  String? _refreshToken;
  String? get refreshToken => _$this._refreshToken;
  set refreshToken(String? refreshToken) => _$this._refreshToken = refreshToken;

  OtpVerifyResponseBuilder() {
    OtpVerifyResponse._defaults(this);
  }

  OtpVerifyResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _accessToken = $v.accessToken;
      _expiresIn = $v.expiresIn;
      _isNewUser = $v.isNewUser;
      _refreshToken = $v.refreshToken;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(OtpVerifyResponse other) {
    _$v = other as _$OtpVerifyResponse;
  }

  @override
  void update(void Function(OtpVerifyResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  OtpVerifyResponse build() => _build();

  _$OtpVerifyResponse _build() {
    final _$result = _$v ??
        _$OtpVerifyResponse._(
          accessToken: BuiltValueNullFieldError.checkNotNull(
              accessToken, r'OtpVerifyResponse', 'accessToken'),
          expiresIn: BuiltValueNullFieldError.checkNotNull(
              expiresIn, r'OtpVerifyResponse', 'expiresIn'),
          isNewUser: BuiltValueNullFieldError.checkNotNull(
              isNewUser, r'OtpVerifyResponse', 'isNewUser'),
          refreshToken: BuiltValueNullFieldError.checkNotNull(
              refreshToken, r'OtpVerifyResponse', 'refreshToken'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
