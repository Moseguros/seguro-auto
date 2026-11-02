// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'verify_contact_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$VerifyContactRequest extends VerifyContactRequest {
  @override
  final String challengeId;
  @override
  final String code;

  factory _$VerifyContactRequest(
          [void Function(VerifyContactRequestBuilder)? updates]) =>
      (VerifyContactRequestBuilder()..update(updates))._build();

  _$VerifyContactRequest._({required this.challengeId, required this.code})
      : super._();
  @override
  VerifyContactRequest rebuild(
          void Function(VerifyContactRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  VerifyContactRequestBuilder toBuilder() =>
      VerifyContactRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is VerifyContactRequest &&
        challengeId == other.challengeId &&
        code == other.code;
  }

  @override
  int get hashCode {
    var _$hash = 0;
    _$hash = $jc(_$hash, challengeId.hashCode);
    _$hash = $jc(_$hash, code.hashCode);
    _$hash = $jf(_$hash);
    return _$hash;
  }

  @override
  String toString() {
    return (newBuiltValueToStringHelper(r'VerifyContactRequest')
          ..add('challengeId', challengeId)
          ..add('code', code))
        .toString();
  }
}

class VerifyContactRequestBuilder
    implements Builder<VerifyContactRequest, VerifyContactRequestBuilder> {
  _$VerifyContactRequest? _$v;

  String? _challengeId;
  String? get challengeId => _$this._challengeId;
  set challengeId(String? challengeId) => _$this._challengeId = challengeId;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  VerifyContactRequestBuilder() {
    VerifyContactRequest._defaults(this);
  }

  VerifyContactRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _challengeId = $v.challengeId;
      _code = $v.code;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(VerifyContactRequest other) {
    _$v = other as _$VerifyContactRequest;
  }

  @override
  void update(void Function(VerifyContactRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  VerifyContactRequest build() => _build();

  _$VerifyContactRequest _build() {
    final _$result = _$v ??
        _$VerifyContactRequest._(
          challengeId: BuiltValueNullFieldError.checkNotNull(
              challengeId, r'VerifyContactRequest', 'challengeId'),
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'VerifyContactRequest', 'code'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
