// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'confirm_signature_request.dart';

// **************************************************************************
// BuiltValueGenerator
// **************************************************************************

class _$ConfirmSignatureRequest extends ConfirmSignatureRequest {
  @override
  final String challengeId;
  @override
  final String code;

  factory _$ConfirmSignatureRequest(
          [void Function(ConfirmSignatureRequestBuilder)? updates]) =>
      (ConfirmSignatureRequestBuilder()..update(updates))._build();

  _$ConfirmSignatureRequest._({required this.challengeId, required this.code})
      : super._();
  @override
  ConfirmSignatureRequest rebuild(
          void Function(ConfirmSignatureRequestBuilder) updates) =>
      (toBuilder()..update(updates)).build();

  @override
  ConfirmSignatureRequestBuilder toBuilder() =>
      ConfirmSignatureRequestBuilder()..replace(this);

  @override
  bool operator ==(Object other) {
    if (identical(other, this)) return true;
    return other is ConfirmSignatureRequest &&
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
    return (newBuiltValueToStringHelper(r'ConfirmSignatureRequest')
          ..add('challengeId', challengeId)
          ..add('code', code))
        .toString();
  }
}

class ConfirmSignatureRequestBuilder
    implements
        Builder<ConfirmSignatureRequest, ConfirmSignatureRequestBuilder> {
  _$ConfirmSignatureRequest? _$v;

  String? _challengeId;
  String? get challengeId => _$this._challengeId;
  set challengeId(String? challengeId) => _$this._challengeId = challengeId;

  String? _code;
  String? get code => _$this._code;
  set code(String? code) => _$this._code = code;

  ConfirmSignatureRequestBuilder() {
    ConfirmSignatureRequest._defaults(this);
  }

  ConfirmSignatureRequestBuilder get _$this {
    final $v = _$v;
    if ($v != null) {
      _challengeId = $v.challengeId;
      _code = $v.code;
      _$v = null;
    }
    return this;
  }

  @override
  void replace(ConfirmSignatureRequest other) {
    _$v = other as _$ConfirmSignatureRequest;
  }

  @override
  void update(void Function(ConfirmSignatureRequestBuilder)? updates) {
    if (updates != null) updates(this);
  }

  @override
  ConfirmSignatureRequest build() => _build();

  _$ConfirmSignatureRequest _build() {
    final _$result = _$v ??
        _$ConfirmSignatureRequest._(
          challengeId: BuiltValueNullFieldError.checkNotNull(
              challengeId, r'ConfirmSignatureRequest', 'challengeId'),
          code: BuiltValueNullFieldError.checkNotNull(
              code, r'ConfirmSignatureRequest', 'code'),
        );
    replace(_$result);
    return _$result;
  }
}

// ignore_for_file: deprecated_member_use_from_same_package,type=lint
