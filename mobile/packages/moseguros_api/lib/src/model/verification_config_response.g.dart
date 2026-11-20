// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'verification_config_response.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VerificationConfigResponse extends VerificationConfigResponse {
  @override
  final CaptchaProvider captchaProvider;
  @override
  final String? captchaSiteKey;

  factory _$VerificationConfigResponse(
          [void Function(VerificationConfigResponseBuilder)? updates]) =>
      (VerificationConfigResponseBuilder()..update(updates))._build();

  _$VerificationConfigResponse._(
      {required this.captchaProvider, this.captchaSiteKey})
      : super._();
  @override
  VerificationConfigResponse rebuild(
          void Function(VerificationConfigResponseBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VerificationConfigResponseBuilder toBuilder() =>
      VerificationConfigResponseBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VerificationConfigResponse &&
        captchaProvider == other.captchaProvider &&
        captchaSiteKey == other.captchaSiteKey;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, captchaProvider.hashCode);
    _$hash = $jc(_$hash, captchaSiteKey.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VerificationConfigResponse')
          ..add('captchaProvider', captchaProvider)
          ..add('captchaSiteKey', captchaSiteKey))
        .toString();
  }
}

class VerificationConfigResponseBuilder
    implements
        Builder<VerificationConfigResponse, VerificationConfigResponseBuilder> {
  _$VerificationConfigResponse? _$v;

  CaptchaProvider? _captchaProvider;
  CaptchaProvider? get captchaProvider => _$this._captchaProvider;
  set captchaProvider(CaptchaProvider? captchaProvider) =>
      _$this._captchaProvider = captchaProvider;

  String? _captchaSiteKey;
  String? get captchaSiteKey => _$this._captchaSiteKey;
  set captchaSiteKey(String? captchaSiteKey) =>
      _$this._captchaSiteKey = captchaSiteKey;

  VerificationConfigResponseBuilder() {
    VerificationConfigResponse._defaults(this);
  }

  VerificationConfigResponseBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _captchaProvider = $v.captchaProvider;
      _captchaSiteKey = $v.captchaSiteKey;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VerificationConfigResponse other) {
    _$v = other as _$VerificationConfigResponse;
  }

  @override
  void update(void Function(VerificationConfigResponseBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VerificationConfigResponse build() => _build();

  _$VerificationConfigResponse _build() {
    final _$result = _$v ??
        _$VerificationConfigResponse._(
          captchaProvider: BuiltValueNullFieldError.checkNotNull(
              captchaProvider,
              r'VerificationConfigResponse',
              'captchaProvider'),
          captchaSiteKey: captchaSiteKey,
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
